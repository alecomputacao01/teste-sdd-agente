---
description: Fase "specify" do SDD — transforma o problema de negócio em spec.md completo
---

Leia `spec.md` e `CLAUDE.md` neste repositório antes de continuar.

Objetivo desta fase: preencher `spec.md` (seções 1-4, 6-7) com os requisitos reais deste serviço, a
partir do que o usuário descrever agora ou já tiver colocado na seção 1 (Contexto e Objetivo).

Regras:

1. Não invente requisito, non-goal, persona ou critério de aceite que o usuário não tenha dito ou
   confirmado. Para qualquer informação que falte ou seja ambígua, escreva
   `NEEDS CLARIFICATION: <pergunta específica>` exatamente no ponto do documento onde a informação
   deveria estar — nunca escolha um default silenciosamente.
2. Pergunte ao usuário o que faltar antes de tentar adivinhar. Se ele responder parcialmente, marque
   `NEEDS CLARIFICATION` só no que continuar em aberto.
3. Não toque na seção 5 (Restrições arquiteturais fixas) — já vem resolvida pela plataforma.
4. Numere os requisitos funcionais como RF-001, RF-002... e os critérios de aceite de forma
   verificável (dado/quando/então), um por RF.
5. Ao terminar, rode `scripts/sdd/check-spec-ready.sh spec.md`. Se ele falhar, liste ao usuário
   exatamente quais `NEEDS CLARIFICATION` restam e pare — não prossiga para `/plan` sozinho.
6. Se ele passar, informe ao usuário que `spec.md` está pronto e que a próxima fase é `/plan`, mas
   não a inicie sem o usuário pedir explicitamente.
