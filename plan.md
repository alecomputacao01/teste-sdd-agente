# Plano de Implementação (SDD) — cliente

> Fase **plan** do fluxo `constitution → specify → plan → tasks → implement`. Mapeia cada RF de
> `spec.md` para os componentes concretos da estrutura hexagonal fixa (`CLAUDE.md`, seção
> "Arquitetura fixa"). Não redefine regra de negócio — só onde ela mora e como as camadas se
> conectam. Fonte de verdade sobre dependência entre camadas continua sendo
> `src/test/java/br/com/algar/poc/cliente/arch/HexagonalArchitectureTest.java`.

## 0. Decisões técnicas assumidas nesta fase

Estas decisões foram confirmadas com o usuário durante `/plan` (não são regra de negócio de
`spec.md`, são realização técnica dela):

1. **Dados de Billing e de crédito chegam já informados**, sem porta de integração externa real
   (consistente com o non-goal "sem integração real com externos" de `spec.md` §2): endpoints
   dedicados recebem esses dados como entrada direta (dias de atraso da fatura mais antiga; score
   interno "Verde/Amarelo/Vermelho"; flag de contestação aberta nos últimos 6 meses). O mesmo vale
   para a validação de endereço nos Correios (RF-010): o cadastro recebe um flag
   `enderecoValidadoNosCorreios` já resolvido por quem chama, sem porta de saída dedicada.
2. **Transições de status ganham endpoint REST** (Suspender a Pedido, Reativar, Cancelar) para o
   CRM/Portal acionarem. A suspensão por inadimplência (RF-002) **não** é uma dessas ações — é
   consequência automática de atualizar a informação de atraso de fatura.
3. **`obterAcoesDisponiveis()` (RF-004) tem endpoint dedicado** (`GET /clientes/{id}/estado`),
   separado do perfil-360.
4. **Contrato é referência somente-leitura** nesta PoC: um Value Object mínimo (id + descrição/status)
   só para compor o perfil-360. Criação/gestão real de contratos é de outro serviço, fora de escopo.
5. **Perfil por consumidor sem autenticação real** (consistente com o non-goal de auth de `spec.md`
   §2): o cabeçalho `X-Perfil-Consumidor: PORTAL|CRM` é o "dado de contexto recebido" que seleciona
   a máscara aplicada em `/perfil-360` — não é um mecanismo de autenticação. O Sistema de
   Auditoria/Fraude usa uma rota HTTP própria (`/auditoria`), não esse cabeçalho.
6. **RF-012 não introduz campos fictícios.** Como nenhum RF cria/edita senha de roteador, token de
   autenticação ou dados parciais de pagamento, o domínio simplesmente não os modela — a invariante
   "não expostos na entidade pública" é satisfeita pela ausência desses campos em `Cliente` e em
   todos os DTOs de resposta, e verificada por um teste que inspeciona a superfície pública.

## 1. Modelo de Domínio (`domain/model/`)

| Classe | Tipo | Descrição |
|---|---|---|
| `Cliente` | Aggregate Root | Raiz do agregado; dono do `StatusCliente`, `Documento`, lista de `Endereco`, lista de `ContaFaturamento`, lista de `Contrato` (referência), `DadosContato`, `ScoreCredito` + flag de contestação. Todas as transições de status e a decisão de elegibilidade são métodos seus. |
| `ClienteId` | Value Object | Identidade do agregado (UUID), mesmo padrão de `ProductId`. |
| `Documento` | Value Object | CPF/CNPJ único e imutável; valida formato básico (11 ou 14 dígitos). |
| `StatusCliente` | Enum | `ATIVO`, `SUSPENSO_INADIMPLENCIA`, `SUSPENSO_A_PEDIDO`, `CANCELADO` (RF-001). |
| `AcaoDisponivel` | Enum | `SUSPENDER_A_PEDIDO`, `REATIVAR`, `ALTERAR`, `CANCELAR` — o subconjunto disponível depende do `StatusCliente` atual (RF-004). |
| `Endereco` | Value Object | Imutável; possui flag `principal`; qualquer alteração gera nova instância (RF-009). |
| `DadosContato` | Value Object | Imutável (telefone, e-mail) (RF-009). |
| `ContaFaturamento` | Entity (filha, dentro do agregado `Cliente`) | Identidade própria (`ContaFaturamentoId`), mas persistida e gerenciada só através de `Cliente` (RF-008). |
| `Contrato` | Value Object | Referência somente-leitura (id + descrição/status) só para compor o perfil-360 (decisão §0.4). |
| `ScoreCredito` | Enum | `VERDE`, `AMARELO`, `VERMELHO` — já "limpo", sem detalhe do sistema externo de origem (RF-005). |
| `ResultadoElegibilidade` | Value Object | `elegivel: boolean` + `motivos: List<String>` (RF-007). |

Regra de negócio chave, todas dentro de `Cliente` (sem setters públicos de status):
- `avaliarAtrasoFatura(diasAtraso)`: transiciona para `SUSPENSO_INADIMPLENCIA` apenas se `diasAtraso > 15` (RF-002); idempotente se já suspenso pelo mesmo motivo.
- `suspenderAPedido()`, `reativar()`, `cancelar()`: cada um valida a partir de qual `StatusCliente` a transição é permitida; `reativar()`/qualquer transição a partir de `CANCELADO` lança exceção de domínio (RF-003).
- `obterAcoesDisponiveis()`: `Set<AcaoDisponivel>` derivado do `StatusCliente` atual (RF-004; mapeamento exato de estado→ações é refinado pelos testes de `/tasks`, ciclo TDD).
- `verificarElegibilidade(ScoreCredito, boolean possuiContestacaoAbertaUltimos6Meses)`: `ResultadoElegibilidade` elegível apenas se score `VERDE` **e** sem contestação (RF-005/RF-006).
- Fábrica de criação exige ao menos um `Endereco` com `principal=true` e `enderecoValidadoNosCorreios=true`; caso contrário a criação é rejeitada por inteiro (RF-010).

## 2. Portas (`domain/ports/`)

| Porta | Direção | Descrição |
|---|---|---|
| `CadastrarClienteUseCase` | in | Cria o `Cliente` (RF-008, RF-009, RF-010). |
| `AlterarStatusClienteUseCase` | in | Aciona `suspenderAPedido`/`reativar`/`cancelar` (RF-001, RF-003). |
| `AtualizarFaturaClienteUseCase` | in | Recebe dias de atraso de fatura, aciona `avaliarAtrasoFatura` (RF-002). |
| `AtualizarCreditoClienteUseCase` | in | Recebe score interno + flag de contestação, armazena no `Cliente` (RF-005). |
| `ConsultarEstadoClienteUseCase` | in | Retorna status + ações disponíveis (RF-004). |
| `VerificarElegibilidadeUseCase` | in | Retorna `ResultadoElegibilidade` (RF-005, RF-006, RF-007). |
| `ConsultarPerfil360UseCase` | in | Retorna endereços, contas de faturamento e contratos consolidados, aplicando máscara conforme perfil do consumidor (RF-008, RF-009, RF-011, RF-013). |
| `ConsultarDadosAuditoriaUseCase` | in | Retorna dados completos (sem máscara) pela rota restrita (RF-012, RF-013). |
| `ClienteRepository` | out | Persistência do agregado `Cliente` (único repositório — `ContaFaturamento`/`Contrato`/`Endereco` não têm repositório próprio, pois só existem dentro do agregado). |

## 3. Aplicação (`application/usecase/`)

Uma classe por porta de entrada, mesmo padrão de `RegisterProductService` (sem anotação Spring,
fiação explícita via `config/BeanConfig`): `CadastrarClienteService`, `AlterarStatusClienteService`,
`AtualizarFaturaClienteService`, `AtualizarCreditoClienteService`, `ConsultarEstadoClienteService`,
`VerificarElegibilidadeService`, `ConsultarPerfil360Service`, `ConsultarDadosAuditoriaService`. Todas
dependem só de `ClienteRepository` — nenhuma outra porta de saída é necessária (decisão §0.1).

## 4. Adapters de entrada (`adapters/in/web/`)

`ClienteController` (`/api/v1/clientes`):

| Endpoint | Método | RF | Request/Response |
|---|---|---|---|
| `/clientes` | POST | RF-008, RF-009, RF-010 | `CadastrarClienteRequest` (documento, dadosContato, lista de endereços com flag principal + `enderecoValidadoNosCorreios`, lista opcional de contas de faturamento) → `ClienteResponse` (id, status). 400 se endereço principal ausente/inválido. |
| `/clientes/{id}/status` | PATCH | RF-001, RF-003 | `AlterarStatusClienteRequest { acao: SUSPENDER_A_PEDIDO \| REATIVAR \| CANCELAR }` → `ClienteResponse`. 409 se a transição não é permitida a partir do estado atual. |
| `/clientes/{id}/faturas` | PATCH | RF-002 | `AtualizarFaturaClienteRequest { diasAtrasoFaturaMaisAntiga: int }` → `ClienteResponse` (pode refletir auto-suspensão). |
| `/clientes/{id}/credito` | PATCH | RF-005, RF-006 | `AtualizarCreditoClienteRequest { score: VERDE\|AMARELO\|VERMELHO, possuiContestacaoAbertaUltimos6Meses: boolean }` → 204. |
| `/clientes/{id}/estado` | GET | RF-004 | `EstadoClienteResponse { status, acoesDisponiveis: [...] }`. |
| `/clientes/{id}/elegibilidade` | GET | RF-005, RF-006, RF-007 | `ElegibilidadeResponse { elegivel: boolean, motivos: [...] }`. |
| `/clientes/{id}/perfil-360` | GET (header `X-Perfil-Consumidor: PORTAL\|CRM`) | RF-008, RF-009, RF-011, RF-013 | `Perfil360Response` (endereços, contas de faturamento, contratos); telefone/documento mascarados quando `CRM`. |
| `/clientes/{id}/auditoria` | GET | RF-012, RF-013 | `AuditoriaResponse` sem máscara, rota restrita — criptografia ponta a ponta simulada/documentada nesta PoC (NFR de `spec.md` §6), sem implementação real de transporte. |

Nenhum DTO de resposta inclui campo de senha de roteador, token de autenticação ou dado parcial de
pagamento (RF-012) — esses conceitos não existem no modelo, ver §0.6.

## 5. Adapters de saída (`adapters/out/persistence/`)

| Classe | Descrição |
|---|---|
| `ClienteJpaEntity` | Tabela `cliente` (id, documento, status, dados de contato, score, flag contestação, dias de atraso). |
| `EnderecoJpaEntity` | Tabela `cliente_endereco`, FK para `cliente`, com flag `principal`. |
| `ContaFaturamentoJpaEntity` | Tabela `conta_faturamento`, FK para `cliente`. |
| `ContratoJpaEntity` | Tabela `cliente_contrato`, FK para `cliente` — armazenamento de referência simples (id externo + descrição/status), sem ciclo de vida próprio. |
| `ClienteMapper` | Domínio ↔ JPA, mesmo padrão de `ProductMapper` (classe package-private, métodos estáticos). |
| `ClienteJpaRepository` | `JpaRepository<ClienteJpaEntity, UUID>`. |
| `ClienteRepositoryAdapter` | Implementa `ClienteRepository`. |
| `db/migration/V1__create_cliente.sql` | Cria as 4 tabelas acima (substitui `V1__create_product.sql`, removido junto com o domínio de exemplo). |

## 6. Remoção do domínio de exemplo

Conforme `CLAUDE.md` ("Domínio de exemplo: substituir, não conviver"), esta fase remove por completo
(não deixa ao lado do domínio real): `Product`, `Sku`, `ProductId`, `RegisterProductUseCase`,
`ProductRepository`, `RegisterProductService`, `ProductController` (+ DTOs), `ProductRepositoryAdapter`,
`ProductMapper`, `ProductJpaEntity`, `ProductJpaRepository`, `V1__create_product.sql`, `BeanConfig`
(será reescrito para fiar os casos de uso de `Cliente`), e os testes `ProductTest`, `SkuTest`,
`RegisterProductServiceTest`, `ProductControllerTest`, `ProductRepositoryAdapterIT`. A suíte ArchUnit
não é afetada (valida pacotes, não nomes de classe).

## 7. Rastreamento

Cobertura: RF-001 (§1, §4 `/status`), RF-002 (§1, §4 `/faturas`), RF-003 (§1, §4 `/status`), RF-004
(§1, §4 `/estado`), RF-005 (§1–§4 `/credito`, `/elegibilidade`), RF-006 (§1 `verificarElegibilidade`),
RF-007 (§4 `/elegibilidade`), RF-008 (§1 `Cliente`/`ContaFaturamento`, §4 `POST /clientes`), RF-009
(§1 `Endereco`/`DadosContato`), RF-010 (§1 fábrica de `Cliente`, §4 `POST /clientes`), RF-011 (§4
`/perfil-360`), RF-012 (§0.6, §4), RF-013 (§4 `/perfil-360` + `/auditoria`). Todos os 13 RF de
`spec.md` §4 têm componente mapeado — nenhum ficou sem dono de camada.

Próxima fase: `/tasks` quebra este plano em tarefas TDD rastreáveis (uma por RF/critério de aceite),
mas não deve ser iniciada sem pedido explícito do usuário.
