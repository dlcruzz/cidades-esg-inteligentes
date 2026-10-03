#!/usr/bin/env bash
# Derruba um ambiente. Uso: bash scripts/destroy.sh <staging|production> [--volumes]
set -euo pipefail
AMBIENTE="${1:?Uso: bash scripts/destroy.sh <staging|production> [--volumes]}"
EXTRA=()
[ "${2:-}" = "--volumes" ] && EXTRA=(-v)
docker compose -p "cidades-esg-${AMBIENTE}" -f docker-compose.yml -f docker-compose.production.yml down "${EXTRA[@]}"
