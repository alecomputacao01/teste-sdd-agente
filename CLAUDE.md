# CLAUDE.md — cliente

Este repositório foi gerado no modo **assistido por agente** do template `poc-java-arquitetura-ddd`
(Backstage). Este arquivo é a "constitution" do fluxo SDD (`constitution → specify → plan → tasks →
implement`): regras não-negociáveis, já decididas pela plataforma antes de qualquer requisito de
negócio existir. Nenhuma tarefa de `/plan`/`/tasks` pode contorná-las.

## Arquitetura fixa: Hexagonal + DDD

Pacote base: `br.com.algar.poc.cliente`.

- `domain/model/` — Aggregates e Value Objects. Regra de negócio pura.
- `domain/ports/in/` — casos de uso expostos pelo domínio, **sempre interfaces**.
- `domain/ports/out/` — dependências que o domínio precisa (ex.: repositório), **sempre interfaces**.
- `application/usecase/` — implementação dos casos de uso (`ports/in`), orquestra o domínio via portas.
- `adapters/in/web/` — controllers REST. Nunca acessam `adapters/out/*` diretamente — sempre via
  `application`/`domain/ports/in`.
- `adapters/out/persistence/` — implementação de `domain/ports/out`, entidades JPA, migrations Flyway.

Regras de dependência (fonte de verdade real:
`src/test/java/br/com/algar/poc/cliente/arch/HexagonalArchitectureTest.java`
— se este texto e o teste divergirem no futuro, **o teste manda**):

1. `domain` não depende de `adapters`.
2. `domain` não depende de `application`.
3. `domain` não depende de Spring nem de JPA (`org.springframework..`, `jakarta.persistence..`).
4. `application` não depende de `adapters`.
5. `adapters.in` não depende de `adapters.out` (comunicação sempre via `application`/`domain.ports`).
6. Classes em `domain.ports.in` e `domain.ports.out` são interfaces.

**Regra dura:** uma falha do ArchUnit nunca é resolvida enfraquecendo, comentando ou apagando a
regra violada — só ajustando o código de produção para respeitá-la.

## TDD (fixo, não opcional)

Ciclo red-green: escreva o teste que falha antes do código de produção correspondente existir. Depois
de cada tarefa de `tasks.md`, rode `mvn test` (unitários + `application` + ArchUnit) e só siga para a
próxima tarefa se tudo passar. `mvn test -Dtest='*IT'` cobre os testes de integração (Testcontainers,
requer Docker) — rode antes de considerar uma tarefa de persistência concluída.

## Domínio de exemplo: substituir, não conviver

O skeleton nasce com um domínio de exemplo (`Product`, `Sku`, `RegisterProductService`,
`ProductController`, `ProductRepositoryAdapter`) e seus testes (`ProductTest`, `SkuTest`,
`RegisterProductServiceTest`, `ProductControllerTest`, `ProductRepositoryAdapterIT`) só para provar a
arquitetura funcionando de ponta a ponta. Ao implementar o domínio real definido em `spec.md`/`tasks.md`,
**substitua** essas classes e testes pelo domínio real — não deixe os dois lados a lado. A suíte
ArchUnit valida a estrutura de pacotes, não os nomes das classes, então essa substituição não quebra
nenhuma regra.

## O gate de validação SDD

Este repositório segue `spec.md` (specify) → `plan.md` (plan) → `tasks.md` (tasks) → implementação,
via os comandos em `.claude/commands/`. Regra dura em cada transição:

- **Nunca gerar `plan.md`** enquanto `spec.md` tiver algum `NEEDS CLARIFICATION` pendente. Rode
  `scripts/sdd/check-spec-ready.sh spec.md` primeiro — se falhar, pare e devolva a lista de pendências
  ao usuário; não adivinhe as respostas.
- **Nunca gerar `tasks.md`** enquanto `plan.md` tiver algum `NEEDS CLARIFICATION` pendente. Mesmo
  gate: `scripts/sdd/check-spec-ready.sh plan.md`.
- Quem decide avançar de fase é o usuário, não o agente sozinho — pare no gate e peça confirmação
  explícita em vez de encadear specify→plan→tasks→implement de uma vez.
- Cada tarefa em `tasks.md` deve referenciar o RF ou critério de aceite de `spec.md` que ela cobre
  (mesma convenção de rastreamento `T0xx → RF-00X` usada no projeto-mãe `poc-backstage-archunit-sdd`).

## Comandos disponíveis

Ver `.claude/commands/specify.md`, `.claude/commands/plan.md`, `.claude/commands/tasks.md` para o
procedimento detalhado de cada fase.
