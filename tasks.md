# Tarefas (SDD) — cliente

> Fase **tasks** do fluxo `constitution → specify → plan → tasks → implement`. Quebra `plan.md` em
> tarefas TDD rastreáveis (convenção `T0xx → RF-00X` de `poc-backstage-archunit-sdd`). Ordem de
> implementação: domínio (`domain/model`, `domain/ports`) → aplicação (`application/usecase`) →
> adapters de entrada (`adapters/in/web`) → adapters de saída (`adapters/out/persistence`) →
> fiação (`config/BeanConfig`) → verificação final. Após **cada** tarefa: `mvn test` (unitários +
> application + ArchUnit) tem que passar antes de avançar para a próxima. Tarefas de persistência
> (`*IT`) também exigem `mvn test -Dtest='*IT'` verde (Testcontainers/Docker) antes de serem dadas
> como concluídas.
>
> Se, ao implementar, uma tarefa revelar um requisito não coberto por `spec.md`/`plan.md`: **parar e
> perguntar ao usuário** — não expandir escopo silenciosamente.

## Fase 0 — Remoção do domínio de exemplo

### T001 → (housekeeping, `CLAUDE.md` "Domínio de exemplo: substituir, não conviver")
Remover por completo `Product`, `Sku`, `ProductId`, `RegisterProductUseCase`, `ProductRepository`,
`RegisterProductService`, `ProductController` (+ DTOs `ProductRequest`/`ProductResponse`),
`ProductRepositoryAdapter`, `ProductMapper`, `ProductJpaEntity`, `ProductJpaRepository`,
`V1__create_product.sql`, e os testes `ProductTest`, `SkuTest`, `RegisterProductServiceTest`,
`ProductControllerTest`, `ProductRepositoryAdapterIT`. Esvaziar `BeanConfig` (será refeito na Fase 5).
Não é TDD (é remoção), mas ao final rode `mvn test` — só a suíte ArchUnit deve restar, e deve passar
trivialmente (pacotes ainda vazios).

## Fase 1 — Domínio (`domain/model/`, sem dependência de Spring/JPA)

> **Nota de execução (descoberta em T002):** `HexagonalArchitectureTest.portas_de_entrada_sao_interfaces`
> e `.portas_de_saida_sao_interfaces` não têm `allowEmptyShould(true)` — falham se `domain/ports/in`
> ou `domain/ports/out` estiverem vazios (ao contrário das regras de `application`/`adapters.in`, que
> têm essa permissão explícita). Por isso `ClienteRepository` (out) e um `CadastrarClienteUseCase`
> mínimo (in, só `cadastrar(ClienteId id)`) foram criados já em T002, junto do primeiro esqueleto de
> `Cliente` — mesmo padrão do esqueleto original (`RegisterProductUseCase`/`ProductRepository` já
> existiam desde o primeiro commit). `CadastrarClienteUseCase` ganha os parâmetros reais
> (documento, endereços, dados de contato) em T007/T008/T011, quando esses Value Objects existirem.
> Isso não muda nenhum RF nem RF novo — é só ordem de criação de arquivo, corrigida para não
> enfraquecer a regra do ArchUnit.

### T002 → RF-001
- **RED** (`domain/model/ClienteTest.java` novo): teste que cria um `Cliente` e afirma que não existe
  nenhum setter público de status — só métodos de transição nomeados (`suspenderAPedido`,
  `reativar`, `cancelar`) e uma consulta de status; teste de transição básica (ex.: `Cliente` recém
  criado está `ATIVO`).
- **GREEN**: criar `StatusCliente` (enum) e `Cliente` (aggregate root, construtor/fábrica privados,
  campo `status` privado, sem setter público).
- Rodar `mvn test`.

### T003 → RF-002
- **RED**: teste em `ClienteTest` — `avaliarAtrasoFatura(16)` transiciona `Cliente` `ATIVO` para
  `SUSPENSO_INADIMPLENCIA`; `avaliarAtrasoFatura(15)` ou menos não transiciona.
- **GREEN**: implementar `Cliente.avaliarAtrasoFatura(int diasAtraso)`.
- Rodar `mvn test`.

### T004 → RF-003
- **RED**: teste em `ClienteTest` — um `Cliente` `CANCELADO` que recebe `reativar()` (ou qualquer
  outra transição) lança exceção de domínio; nenhuma cobrança/mudança é aplicada.
- **GREEN**: implementar a guarda em `cancelar()`/`reativar()`/`suspenderAPedido()`/
  `avaliarAtrasoFatura()` para rejeitar a partir de `CANCELADO`.
- Rodar `mvn test`.

### T005 → RF-004
- **RED**: teste em `ClienteTest` — `obterAcoesDisponiveis()` retorna o conjunto correto de
  `AcaoDisponivel` para cada `StatusCliente` (ex.: `CANCELADO` nunca contém `REATIVAR`).
- **GREEN**: criar `AcaoDisponivel` (enum) e implementar `Cliente.obterAcoesDisponiveis()`.
- Rodar `mvn test`.

### T006 → RF-009
- **RED**: `EnderecoTest` e `DadosContatoTest` novos — criar um VO, tentar "alterar" e confirmar que
  o resultado é uma nova instância (o objeto original não muda); igualdade por valor.
- **GREEN**: criar `Endereco` (com flag `principal`) e `DadosContato`, imutáveis.
- Rodar `mvn test`.

### T007 → RF-008
- **RED**: `DocumentoTest` (formato CPF/CNPJ válido/inválido) e teste em `ClienteTest` — um `Cliente`
  aceita múltiplos `Endereco` e múltiplas `ContaFaturamento` vinculados à mesma identidade
  (`Documento` único).
- **GREEN**: criar `Documento`, `ContaFaturamentoId`, `ContaFaturamento`; adicionar listas de
  endereços/contas de faturamento a `Cliente`.
- Rodar `mvn test`.

### T008 → RF-010 (implementada junto com T007 — mesma fábrica `Cliente.registrar(...)`, evitar duas reescritas de assinatura)
- **RED**: teste em `ClienteTest` — fábrica de criação rejeita quando não há `Endereco` principal, ou
  quando o principal tem `enderecoValidadoNosCorreios=false`; aceita e ativa (`status=ATIVO`) quando
  válido.
- **GREEN**: implementar a fábrica de `Cliente` com essa validação (ver `plan.md` §0.1 — o flag já
  chega resolvido, sem porta de saída dedicada).
- Rodar `mvn test`.

### T009 → RF-005, RF-006
- **RED**: `ResultadoElegibilidadeTest`/teste em `ClienteTest` — `verificarElegibilidade(VERDE, false)`
  → elegível; `verificarElegibilidade(AMARELO, false)` ou `verificarElegibilidade(VERDE, true)` → não
  elegível, com motivo(s) preenchido(s).
- **GREEN**: criar `ScoreCredito` (enum) e `ResultadoElegibilidade`; implementar
  `Cliente.verificarElegibilidade(ScoreCredito, boolean)`.
- Rodar `mvn test`.

### T010 → RF-012
- **RED**: teste em `ClienteTest` (ou teste de reflexão simples) que enumera os métodos/campos
  públicos de `Cliente` e afirma que nenhum se refere a senha de roteador, token de autenticação ou
  dado parcial de pagamento.
- **GREEN**: nenhuma mudança de produção esperada (a ausência já satisfaz — ver `plan.md` §0.6); se o
  teste falhar é porque algo desses foi adicionado por engano em tarefa anterior.
- Rodar `mvn test`.

## Fase 2 — Aplicação (`application/usecase/`, orquestra domínio via portas; sem Spring)

### T011 → RF-008, RF-010
- **RED** (`application/usecase/CadastrarClienteServiceTest.java`): com `ClienteRepository` mockado
  (Mockito), testar cadastro bem-sucedido (endereço principal válido) e rejeição (sem endereço
  principal válido).
- **GREEN**: criar porta `domain/ports/out/ClienteRepository` (`save`, `findById`, mínimo necessário)
  e porta `domain/ports/in/CadastrarClienteUseCase`; implementar `CadastrarClienteService`.
- Rodar `mvn test`.

### T012 → RF-001, RF-003
- **RED** (`AlterarStatusClienteServiceTest`): repositório mockado — aciona `suspenderAPedido`,
  `reativar`, `cancelar` via serviço; caso de transição inválida (ex.: reativar `CANCELADO`) propaga
  a exceção de domínio.
- **GREEN**: criar `AlterarStatusClienteUseCase` e `AlterarStatusClienteService`.
- Rodar `mvn test`.

### T013 → RF-002
- **RED** (`AtualizarFaturaClienteServiceTest`): repositório mockado — atualizar dias de atraso
  aciona `avaliarAtrasoFatura` e persiste o `Cliente` atualizado.
- **GREEN**: criar `AtualizarFaturaClienteUseCase` e `AtualizarFaturaClienteService`.
- Rodar `mvn test`.

### T014 → RF-005, RF-006
- **RED** (`AtualizarCreditoClienteServiceTest` e `VerificarElegibilidadeServiceTest`): repositório
  mockado — armazenar score/contestação; depois consultar elegibilidade e obter
  `ResultadoElegibilidade` coerente com o que foi armazenado.
- **GREEN**: criar `AtualizarCreditoClienteUseCase`/`Service` e `VerificarElegibilidadeUseCase`/
  `Service`.
- Rodar `mvn test`.

### T015 → RF-004
- **RED** (`ConsultarEstadoClienteServiceTest`): repositório mockado — retorna status atual +
  `obterAcoesDisponiveis()` do `Cliente` encontrado; cliente inexistente é tratado (ex.: exceção
  específica).
- **GREEN**: criar `ConsultarEstadoClienteUseCase` e `ConsultarEstadoClienteService`.
- Rodar `mvn test`.

### T016 → RF-011, RF-013
- **RED** (`ConsultarPerfil360ServiceTest`): repositório mockado — retorna endereços, contas de
  faturamento e contratos consolidados; quando o perfil informado é `CRM`, telefone/documento vêm
  mascarados; quando é `PORTAL`, vêm completos.
- **GREEN**: criar `ConsultarPerfil360UseCase` e `ConsultarPerfil360Service`, recebendo um parâmetro
  de perfil (`PORTAL`/`CRM`) e aplicando a máscara na composição do retorno.
- Rodar `mvn test`.

### T017 → RF-012, RF-013
- **RED** (`ConsultarDadosAuditoriaServiceTest`): repositório mockado — retorna os dados do `Cliente`
  sem máscara, mesmo verificando (junto com T010) que nenhum campo sensível inexistente é retornado.
- **GREEN**: criar `ConsultarDadosAuditoriaUseCase` e `ConsultarDadosAuditoriaService`.
- Rodar `mvn test`.

## Fase 3 — Adapters de entrada (`adapters/in/web/`)

Todos os testes de controller seguem o padrão já usado no domínio de exemplo: `MockMvc` standalone +
Mockito no caso de uso, sem contexto Spring (ver nota em `ProductControllerTest`, removido em T001).

### T018 → RF-008, RF-010
- **RED** (`ClienteControllerTest#deveRetornar201...` / `#deveRetornar400...`): `POST /api/v1/clientes`
  com `CadastrarClienteRequest`.
- **GREEN**: criar `ClienteController` (endpoint `POST`), `CadastrarClienteRequest`, `ClienteResponse`.
- Rodar `mvn test`.

### T019 → RF-001, RF-003
- **RED**: `PATCH /api/v1/clientes/{id}/status` — 200 em transição válida, 409 em transição inválida.
- **GREEN**: endpoint + `AlterarStatusClienteRequest`.
- Rodar `mvn test`.

### T020 → RF-002
- **RED**: `PATCH /api/v1/clientes/{id}/faturas` — 200, refletindo eventual auto-suspensão.
- **GREEN**: endpoint + `AtualizarFaturaClienteRequest`.
- Rodar `mvn test`.

### T021 → RF-005, RF-006
- **RED**: `PATCH /api/v1/clientes/{id}/credito` — 204.
- **GREEN**: endpoint + `AtualizarCreditoClienteRequest`.
- Rodar `mvn test`.

### T022 → RF-004
- **RED**: `GET /api/v1/clientes/{id}/estado` — 200 com `status` e `acoesDisponiveis`.
- **GREEN**: endpoint + `EstadoClienteResponse`.
- Rodar `mvn test`.

### T023 → RF-007
- **RED**: `GET /api/v1/clientes/{id}/elegibilidade` — 200 com `elegivel` e `motivos`.
- **GREEN**: endpoint + `ElegibilidadeResponse`.
- Rodar `mvn test`.

### T024 → RF-011, RF-013
- **RED**: `GET /api/v1/clientes/{id}/perfil-360` — com header `X-Perfil-Consumidor: PORTAL` retorna
  dados completos; com `CRM` retorna telefone/documento mascarados.
- **GREEN**: endpoint + `Perfil360Response`.
- Rodar `mvn test`.

### T025 → RF-012, RF-013
- **RED**: `GET /api/v1/clientes/{id}/auditoria` — 200 sem máscara; assert de que a resposta não
  contém campo de senha de roteador/token/dado parcial de pagamento (reforça RF-012 na borda HTTP).
- **GREEN**: endpoint + `AuditoriaResponse`.
- Rodar `mvn test`.

## Fase 4 — Adapters de saída (`adapters/out/persistence/`)

> **Notas de execução (descobertas em T026):**
> - `@SpringBootTest` sobe o contexto inteiro, incluindo `ClienteController` com as 8 portas — a IT
>   só sobe com `BeanConfig` já fiado. Por isso **T027 foi adiantada para antes de fechar a
>   verificação de T026** (ordem real: código de T026 → T027 → rodar a IT de T026).
> - `testcontainers-bom` foi de `1.21.3` para `1.21.4` em `pom.xml`: a versão anterior falhava com
>   `client version 1.32 is too old` contra o Docker Engine 29.x instalado neste ambiente — bug de
>   negociação de API do docker-java, não algo resolvível no código do domínio.
> - `conta_faturamento.cliente_id` é `NULL`able na migration: `@OneToMany` unidirecional do
>   Hibernate insere a entidade filha e só depois faz `UPDATE` para setar a FK; com `NOT NULL` o
>   insert falha antes do update rodar.

### T026 → RF-008, RF-009, RF-010, RF-011
- **RED** (`ClienteRepositoryAdapterIT`, Testcontainers PostgreSQL — mesmo padrão de
  `ProductRepositoryAdapterIT`, removido em T001): salvar um `Cliente` com múltiplos endereços,
  contas de faturamento e contratos; recuperar por id e confirmar que a árvore completa volta
  intacta.
- **GREEN**: criar `ClienteJpaEntity`, `EnderecoJpaEntity`, `ContaFaturamentoJpaEntity`,
  `ContratoJpaEntity`, `ClienteMapper`, `ClienteJpaRepository`, `ClienteRepositoryAdapter`, e a
  migration `db/migration/V1__create_cliente.sql` (4 tabelas — ver `plan.md` §5).
- Rodar `mvn test` e depois `mvn test -Dtest='*IT'` (requer Docker) — ambos verdes antes de concluir.

## Fase 5 — Fiação e verificação final

### T027 → (housekeeping, sem RF direto — necessário para a aplicação subir; executada antes do fechamento de T026, ver nota acima)
Reescrever `config/BeanConfig` ligando cada porta de entrada (`CadastrarClienteUseCase`,
`AlterarStatusClienteUseCase`, `AtualizarFaturaClienteUseCase`, `AtualizarCreditoClienteUseCase`,
`ConsultarEstadoClienteUseCase`, `VerificarElegibilidadeUseCase`, `ConsultarPerfil360UseCase`,
`ConsultarDadosAuditoriaUseCase`) à sua implementação em `application.usecase`, injetando
`ClienteRepository`. Rodar `mvn test`.

### T028 → spec.md §7 (Definition of Done)
Rodar a suíte completa: `mvn test` (unitários + application + ArchUnit) e
`mvn test -Dtest='*IT'` (persistência, Testcontainers/Docker) — ambos verdes. Conferir manualmente
que todo RF-001..RF-013 tem pelo menos uma tarefa concluída marcada acima antes de considerar a
implementação encerrada.
