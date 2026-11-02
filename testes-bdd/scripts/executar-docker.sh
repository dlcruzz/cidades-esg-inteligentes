#!/usr/bin/env bash
# ==========================================================
# Sobe API + banco + testes com Docker Compose e devolve
# 0 se todos os testes passaram ou diferente de 0 se algum falhou.
# Uso: bash scripts/executar-docker.sh
# ==========================================================
set -uo pipefail
cd "$(dirname "$0")/.."
mkdir -p target

CODIGO=0
docker compose up --build --abort-on-container-exit --exit-code-from testes || CODIGO=$?
docker compose down -v

echo ""
echo "Relatório HTML : target/relatorios/relatorio-cucumber.html"
echo "Relatório JSON : target/relatorios/cucumber.json"
echo "Relatório JUnit: target/surefire-reports/"
if [ "$CODIGO" -eq 0 ]; then
  echo "RESULTADO: TODOS OS TESTES PASSARAM"
else
  echo "RESULTADO: HOUVE FALHAS (código $CODIGO)"
fi
exit "$CODIGO"
