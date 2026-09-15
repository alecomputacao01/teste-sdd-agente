#!/usr/bin/env bash
# Gate mecânico do fluxo SDD (specify -> plan -> tasks): falha se o arquivo passado ainda tiver
# algum marcador NEEDS CLARIFICATION pendente. Usado pelos comandos .claude/commands/{plan,tasks}.md
# antes de gerar o próximo artefato, e pode ser rodado manualmente por qualquer pessoa a qualquer
# momento para conferir sem depender do agente.
set -euo pipefail

file="${1:?uso: check-spec-ready.sh <arquivo.md>}"

if [[ ! -f "$file" ]]; then
  echo "ERRO: arquivo '$file' não encontrado." >&2
  exit 2
fi

matches=$(grep -n "NEEDS CLARIFICATION" "$file" || true)

if [[ -n "$matches" ]]; then
  echo "BLOQUEADO: $file ainda tem pontos em aberto:" >&2
  echo "$matches" >&2
  exit 1
fi

echo "OK: $file sem NEEDS CLARIFICATION pendente."
