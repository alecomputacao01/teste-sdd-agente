---
description: Fase "tasks" do SDD — quebra plan.md em tarefas TDD rastreáveis, gera tasks.md
---

Antes de qualquer outra coisa, rode `scripts/sdd/check-spec-ready.sh plan.md`.

- Se falhar: pare. Não gere `tasks.md`. Mostre ao usuário exatamente quais `NEEDS CLARIFICATION`
  restam em `plan.md` e peça para resolvê-los (editando o arquivo ou rodando `/plan` de novo).
- Se passar: continue.

Leia `plan.md` e gere (ou sobrescreva) `tasks.md` na raiz do repositório: uma tarefa por
RF/critério de aceite de `spec.md`, numeradas `T001`, `T002`... Cada tarefa deve:

1. Citar o RF ou critério de aceite que cobre (ex.: `T001 → RF-001`) — mesma convenção do
   `tasks.md` do projeto-mãe `poc-backstage-archunit-sdd`.
2. Descrever o ciclo TDD explícito: qual teste escrever primeiro (e onde: `domain`, `application`,
   `adapters`, conforme o mapeamento de `plan.md`), depois qual código de produção faz o teste
   passar, depois confirmar `mvn test` verde (incluindo ArchUnit) antes de marcar a tarefa concluída.
3. Estar em ordem de implementação (domínio antes de aplicação antes de adapters, respeitando as
   dependências da arquitetura hexagonal).

Quando o usuário pedir para implementar, trabalhe as tarefas de `tasks.md` uma de cada vez, nesta
ordem, rodando `mvn test` após cada uma e só avançando se passar. Se uma tarefa revelar um requisito
não coberto por `spec.md`, pare e pergunte ao usuário — não expanda escopo silenciosamente.
