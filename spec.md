# Especificação (SDD) — cliente

> Gerado no modo **assistido por agente** (Platform Engineering 2.0) do template `poc-java-arquitetura-ddd`.
> Segue o fluxo `constitution → specify → plan → tasks → implement` (mesmo estilo *spec-kit* usado no
> projeto `poc-backstage-archunit-sdd` que gerou este repositório). A "constitution" já vem resolvida —
> ver `CLAUDE.md`. Este arquivo cobre **specify**: o que este serviço faz, não como.
>
> **Antes de pedir `/plan`:** resolva todo marcador `NEEDS CLARIFICATION` abaixo (edite este arquivo
> diretamente, ou peça ao agente para refinar via `/specify`) e rode
> `scripts/sdd/check-spec-ready.sh spec.md` — ele falha enquanto sobrar algum.

## 1. Contexto e Objetivo
REQ-01: Encapsulamento de Estado e Ciclo de Vida do Assinante (RDM / Máquina de Estados)Descrição: A entidade Cliente (Raiz do Agregado) deve gerenciar de forma autônoma suas transições de status (Ativo, Suspenso por Inadimplência, Suspenso a Pedido, Cancelado). Nenhuma aplicação consumidora (como o CRM) pode alterar o status do cliente diretamente por propriedades (getters/setters).Regras de Negócio (Invariantes):O cliente só pode passar para o estado Suspenso por Inadimplência se houver pelo menos uma fatura com mais de 15 dias de atraso informada pelo contexto de Billing.Um cliente no estado Cancelado não pode reativar serviços ou sofrer novas cobranças; ele deve ser tratado como histórico ou fluxo de reaquisição.Exposição para Consumidores: O modelo deve expor um método de consulta que retorne o estado atual e as ações permitidas para aquele momento (ex: cliente.ObterAcoesDisponiveis()). O CRM e o Portal consumirão essa API para renderizar dinamicamente quais botões (Reativar, Suspender, Alterar) estarão visíveis ou bloqueados para o atendente ou usuário. REQ-02: Isolamento de Lógica Externa via Domain Services (Elegibilidade e Crédito)Descrição: A tomada de decisão sobre a concessão de novas linhas ou upgrades de planos deve ser validada no domínio do cliente, mas sem acoplamento com sistemas externos de crédito (ex: Serasa, SPC) ou sistemas de rede.Regras de Negócio (Invariantes):Para que um cliente seja considerado elegível a uma nova oferta de alto valor, ele deve possuir um score interno de adimplência "Verde" e não ter contestações de fatura abertas nos últimos 6 meses.Mapeamento DDD: A infraestrutura ou a camada de aplicação buscará o score de crédito externo e o injetará em um Domain Service (ex: ServicoValidacaoElegibilidade). Este serviço passará as informações limpas para o método cliente.VerificarElegibilidade(scoreCredito).Exposição para Consumidores: O CRM (Vendas) e o Portal (Upgrade de Plano) farão uma chamada ao endpoint /clientes/{id}/elegibilidade. O domínio processa internamente e responde um JSON boleano estruturado com os motivos em caso de recusa. REQ-03: Modularidade de Identidade e Contas de Faturamento (Value Objects e Agregados)Descrição: O sistema deve suportar a visão unificada do cliente (CPF/CNPJ único), mas permitir que ele possua múltiplos endereços de instalação e múltiplas contas de faturamento (ex: cobrança da banda larga em um endereço e do celular em outro).Regras de Negócio (Invariantes):Endereco e DadosContato devem ser tratados como Value Objects (Objetos de Valor). Eles não possuem identidade própria fora do cliente e são imutáveis (qualquer alteração gera uma nova instância validada).A entidade Cliente garante que pelo menos um endereço de instalação principal exista e seja válido na base de dados dos Correios antes de permitir a ativação do cadastro.Exposição para Consumidores: O Portal de Autoatendimento e o CRM consumirão o endpoint /clientes/{id}/perfil-360. O modelo rico consolidará a árvore de sub-entidades (Endereços, Contas de Cobrança vinculadas e Contratos) entregando uma resposta limpa e de alta performance (otimizada via CQRS/Read Models se necessário). REQ-05: Abstração e Proteção de Dados Sensíveis (Camada Anticorrupção / ACL e LGPD)Descrição: O modelo de domínio rico deve proteger dados sensíveis e garantir conformidade com regras de privacidade (LGPD), assegurando que sistemas terceiros ou menos confiáveis não acessem dados restritos.Regras de Negócio (Invariantes):Dados como senhas de roteador do cliente, tokens de autenticação ou dados parciais de pagamento não ficam expostos na entidade principal pública.Exposição para Consumidores: O microsserviço implementará fachadas de visualização baseadas no perfil do consumidor através de DTOs (Data Transfer Objects).O Portal do Cliente acessa dados de perfil via token do próprio usuário. O CRM acessa dados com máscaras de segurança em telefones/documentos para o atendente.O Sistema de Auditoria/Fraude acessa uma rota restrita com criptografia ponta a ponta.

## 2. Fora de escopo (non-goals)

**NEEDS CLARIFICATION:** o que este serviço explicitamente NÃO faz? (evita que o agente expanda
escopo silenciosamente durante `/plan`/`/tasks`.)

## 3. Persona / Usuário

**NEEDS CLARIFICATION:** quem usa/consome este serviço (outro serviço, um time, um usuário final)?

## 4. Requisitos Funcionais

| ID | Requisito |
|---|---|
| RF-001 | **NEEDS CLARIFICATION:** liste os requisitos funcionais reais deste serviço, um por linha, numerados RF-001, RF-002... — o `/specify` os deriva do Contexto acima e das respostas às perguntas feitas ao usuário. |

## 5. Restrições arquiteturais fixas (já decididas pela plataforma — não precisam clarificação)

Estas vieram do formulário do Backstage e **não são negociáveis** por este serviço — são o mesmo
papel que a "constitution" tem no fluxo spec-kit:

| Parâmetro | Valor |
|---|---|
| Framework | spring-boot-4 |
| Banco de dados | postgresql |
| Abordagem arquitetural | hexagonal |
| Pacote base | `br.com.algar.poc.cliente` |
| Modelagem de domínio | DDD (fixo) |
| Metodologia de teste | TDD (fixo) |

As regras de dependência entre camadas estão em
`src/test/java/br/com/algar/poc/cliente/arch/HexagonalArchitectureTest.java`
— esse teste é o oráculo real, não este documento. Detalhes de convenção (onde cada coisa mora,
o que substituir) estão em `CLAUDE.md`.

## 6. Requisitos Não-Funcionais

**NEEDS CLARIFICATION:** algum requisito de performance, segurança, disponibilidade específico deste
serviço, além do que a plataforma já garante (ArchUnit, pipeline, branch protection)? Se nenhum, marque
explicitamente "nenhum além do padrão da plataforma" para resolver este marcador.

## 7. Critérios de Aceite (Definition of Done)

- [ ] **NEEDS CLARIFICATION:** um critério verificável por RF da seção 4 (ex.: "RF-001: dado X, quando Y, então Z").
- [x] Suíte de testes (TDD + ArchUnit) roda localmente com `mvn test` e passa — já garantido pela
      plataforma, não depende do domínio real.

## 8. Rastreamento

Depois que este arquivo não tiver mais `NEEDS CLARIFICATION`: `/plan` gera `plan.md` (mapeando cada RF
para Aggregate/Use Case/Porta/Adapter dentro da estrutura hexagonal), e `/tasks` gera `tasks.md` (uma
tarefa por RF/critério de aceite, ciclo TDD explícito). Ver `CLAUDE.md` para as regras que o agente
segue em cada fase.
