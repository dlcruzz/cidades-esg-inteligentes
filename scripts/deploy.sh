#!/usr/bin/env bash
# ==========================================================
# deploy.sh - sobe um ambiente (staging ou production) com
# Docker Compose e valida com smoke test.
#
# Uso:
#   bash scripts/deploy.sh staging                 # constrói a imagem localmente
#   bash scripts/deploy.sh production <imagem>     # usa imagem do registry (pipeline)
#
# Cada ambiente usa um nome de projeto Compose diferente, então
# tem sua própria rede, volume de banco e porta:
#   staging    -> http://localhost:8081
#   production -> http://localhost:8082
# ==========================================================
set -euo pipefail

AMBIENTE="${1:-}"
IMAGEM="${2:-}"

case "$AMBIENTE" in
  staging)
    PORTA=8081
    DB_PORTA=5433
    ARQUIVOS=(-f docker-compose.yml)
    ;;
  production)
    PORTA=8082
    DB_PORTA=5434   # ignorada: em produção o banco não é exposto
    ARQUIVOS=(-f docker-compose.yml -f docker-compose.production.yml)
    ;;
  *)
    echo "Uso: bash scripts/deploy.sh <staging|production> [imagem]" >&2
    exit 1
    ;;
esac

PROJETO="cidades-esg-${AMBIENTE}"

export SPRING_PROFILES_ACTIVE="$AMBIENTE"
export APP_PORT="$PORTA"
export DB_EXPOSED_PORT="$DB_PORTA"
export POSTGRES_DB="${POSTGRES_DB:-cidades_esg}"
export POSTGRES_USER="${POSTGRES_USER:-esg_user}"
export POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-esg_${AMBIENTE}_pass}"

echo "=============================================="
echo " Deploy do ambiente: ${AMBIENTE}"
echo " Projeto Compose:    ${PROJETO}"
echo " Porta da API:       ${PORTA}"
echo "=============================================="

if [ -n "$IMAGEM" ]; then
  export APP_IMAGE="$IMAGEM"
  echo ">> Baixando imagem ${APP_IMAGE}"
  docker pull "$APP_IMAGE"
  docker compose -p "$PROJETO" "${ARQUIVOS[@]}" up -d --no-build
else
  echo ">> Construindo imagem localmente"
  docker compose -p "$PROJETO" "${ARQUIVOS[@]}" up -d --build
fi

echo ">> Containers em execução:"
docker compose -p "$PROJETO" "${ARQUIVOS[@]}" ps

bash "$(dirname "$0")/smoke-test.sh" "http://localhost:${PORTA}" "$AMBIENTE"
