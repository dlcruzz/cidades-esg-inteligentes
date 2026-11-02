#!/usr/bin/env bash
# ==========================================================
# Roda os testes contra uma API que já está no ar (sem Docker
# para os testes). Pré-requisitos: Java 17 e Maven.
# Uso: bash scripts/executar-local.sh [url-da-api] [tags]
# Ex.: bash scripts/executar-local.sh http://localhost:8080
#      bash scripts/executar-local.sh http://localhost:8080 "@negativo"
# ==========================================================
set -uo pipefail
cd "$(dirname "$0")/.."

URL="${1:-${API_URL:-http://localhost:8080}}"
TAGS="${2:-}"

echo ">> Verificando a API em ${URL}/actuator/health ..."
if ! curl -fs "${URL}/actuator/health" | grep -q '"status":"UP"'; then
  echo "ERRO: a API não respondeu UP em ${URL}. Suba a API antes (veja o README)." >&2
  exit 2
fi
echo "   API saudável."

if [ -n "$TAGS" ]; then
  mvn -B test -Dapi.url="${URL}" -Dcucumber.filter.tags="${TAGS}"
else
  mvn -B test -Dapi.url="${URL}"
fi
