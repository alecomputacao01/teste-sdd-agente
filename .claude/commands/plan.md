---
description: Fase "plan" do SDD — mapeia spec.md para a estrutura hexagonal, gera plan.md
---

Antes de qualquer outra coisa, rode `scripts/sdd/check-spec-ready.sh spec.md`.

- Se falhar: pare. Não gere `plan.md`. Mostre ao usuário exatamente quais `NEEDS CLARIFICATION`
  restam em `spec.md` e peça para resolvê-los (editando o arquivo ou rodando `/specify` de novo).
- Se passar: continue.

Leia `spec.md` (RF/RNF/critérios de aceite) e `CLAUDE.md` (regras de camada da seção "Arquitetura
fixa: Hexagonal + DDD"). Gere (ou sobrescreva) `plan.md` na raiz do repositório, mapeando cada RF de
`spec.md` para os componentes concretos da estrutura hexagonal fixa:

- Que Aggregate(s)/Value Object(s) em `domain/model/` esse RF precisa (novos ou existentes)?
- Que porta em `domain/ports/in/` (caso de uso) e/ou `domain/ports/out/` (dependência externa) esse
  RF precisa?
- Que implementação em `application/usecase/` orquestra isso?
- Que adapter em `adapters/in/web/` e/ou `adapters/out/persistence/` esse RF precisa (endpoint,
  entidade JPA, migration Flyway)?

Se, ao mapear, surgir uma decisão técnica que `spec.md` não permite resolver sozinho (ex.: formato
exato de um endpoint, nome de uma tabela), marque `NEEDS CLARIFICATION: <pergunta>` em `plan.md` no
lugar da decisão — mesma regra de `/specify`, não adivinhe.

Ao terminar, rode `scripts/sdd/check-spec-ready.sh plan.md`. Se passar, informe ao usuário que
`plan.md` está pronto e que a próxima fase é `/tasks`, mas não a inicie sem o usuário pedir.
