#!/usr/bin/env bash
# ==========================================================
# smoke-test.sh - valida se a API subiu e responde corretamente.
# Uso: bash scripts/smoke-test.sh <url-base> [ambiente-esperado]
# Ex.: bash scripts/smoke-test.sh http://localhost:8081 staging
# ==========================================================
set -euo pipefail

URL="${1:-http://localhost:8080}"
AMBIENTE_ESPERADO="${2:-}"

echo ">> Aguardando ${URL}/actuator/health ficar UP..."
for i in $(seq 1 40); do
  if curl -fs "${URL}/actuator/health" | grep -q '"status":"UP"'; then
    echo "   OK - aplicação saudável (tentativa ${i})"
    break
  fi
  if [ "$i" -eq 40 ]; then
    echo "   ERRO - aplicação não ficou saudável a tempo" >&2
    exit 1
  fi
  sleep 3
done

echo ""
echo ">> GET /actuator/health"
curl -fs "${URL}/actuator/health"; echo

echo ""
echo ">> GET /  (status e ambiente ativo)"
RAIZ=$(curl -fs "${URL}/")
echo "$RAIZ"
if [ -n "$AMBIENTE_ESPERADO" ] && ! echo "$RAIZ" | grep -q "\"ambiente\":\"${AMBIENTE_ESPERADO}\""; then
  echo "   ERRO - ambiente ativo diferente de '${AMBIENTE_ESPERADO}'" >&2
  exit 1
fi

echo ""
echo ">> POST /api/cidades  (cria uma cidade de teste)"
curl -fs -X POST "${URL}/api/cidades" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Curitiba","uf":"PR","populacao":1963726,"indicadorAmbiental":85,"indicadorSocial":78,"indicadorGovernanca":90}'
echo

echo ""
echo ">> GET /api/cidades?uf=PR"
curl -fs "${URL}/api/cidades?uf=PR"; echo

echo ""
echo ">> GET /api/cidades/999999  (deve retornar 404)"
CODIGO=$(curl -s -o /dev/null -w "%{http_code}" "${URL}/api/cidades/999999")
echo "   HTTP ${CODIGO}"
[ "$CODIGO" = "404" ] || { echo "   ERRO - esperado 404" >&2; exit 1; }

echo ""
echo "SMOKE TEST APROVADO em ${URL}"
