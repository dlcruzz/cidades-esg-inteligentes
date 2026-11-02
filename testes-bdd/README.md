# 🧪 Testes BDD e de API — Cidades ESG Inteligentes

Suíte de testes automatizados da API **Cidades ESG Inteligentes** (Java Spring Boot, projeto das atividades anteriores do Grupo 14), escrita com **BDD**: cenários em **Gherkin (português)** executados com **Cucumber**, chamadas HTTP com **REST Assured** e **testes de contrato com JSON Schema**.

Esta pasta (`testes-bdd/`) fica dentro do repositório da própria API: <https://github.com/dlcruzz/cidades-esg-inteligentes>. O workflow está em `.github/workflows/testes-bdd.yml`, na raiz do repositório.

**Grupo 14**
- Amanda do Nascimento Diacís — RM565236
- Ariane Hemerich — RM561659
- Ariel de Souza Lima — RM561994
- Danilo Lima Cruz — RM564828
- Paulo Augusto Siqueira — RM563083

---

## 🌱 Como os testes se ligam aos pilares ESG

| Pilar | O que os testes garantem |
|-------|--------------------------|
| **Ambiental / Social / Governança** (`@esg`) | O score ESG é a média exata dos três indicadores; os limites 0 e 100 são aceitos e valores fora da faixa são recusados com mensagem clara. |
| **Governança e compliance** (`@governanca`) | Rastreabilidade: toda atualização renova `atualizadoEm`; uma atualização inválida **não** altera o dado original; a API informa em qual ambiente roda (`GET /`) e sua saúde (`/actuator/health`). Erros não expõem detalhes internos. |
| **Eficiência** (`@eficiencia`) | Rotas críticas respondem em menos de 2 segundos. |

---

## 📌 O que é testado

Todas as rotas da API têm teste de **status code**, de **corpo JSON** e de **contrato (JSON Schema)**:

| Rota | Cenários (arquivo `.feature`) | Contrato (`src/test/resources/schemas`) |
|------|-------------------------------|------------------------------------------|
| `GET /` | 01 — status da aplicação | `status.schema.json` |
| `GET /actuator/health` | 01 — saúde da aplicação | `health.schema.json` |
| `POST /api/cidades` | 02 — cadastro (válido, score, limites, obrigatórios, JSON malformado), 05 — ciclo de vida | `cidade.schema.json`, `erro-validacao.schema.json` |
| `GET /api/cidades` (`?uf=`, `?nome=`) | 03 — listar, filtrar por UF, filtrar por nome, sem resultados | `lista-cidades.schema.json` |
| `GET /api/cidades/{id}` | 03 — encontrado, inexistente (404), id inválido (400) | `cidade.schema.json`, `erro-nao-encontrado.schema.json` |
| `PUT /api/cidades/{id}` | 04 — atualizar, rastreabilidade, inválido (400), inexistente (404) | `cidade.schema.json`, `erro-validacao.schema.json`, `erro-nao-encontrado.schema.json` |
| `DELETE /api/cidades/{id}` | 04 — remover (204), remover duas vezes (404), inexistente (404) | `erro-nao-encontrado.schema.json` |

São **5 funcionalidades, 23 cenários escritos e 36 execuções** (os "Esquemas do Cenário" rodam uma vez por linha de exemplos), com caminhos felizes (`@positivo`) e de falha (`@negativo`).

Cada cenário cria os próprios dados (com nome único) e **apaga tudo ao terminar**, então a suíte pode rodar em qualquer ambiente sem deixar lixo e sem depender da ordem.

---

## 📁 Estrutura

```
cidades-esg-testes-bdd/
├── scripts/
│   ├── executar-docker.sh             # sobe API + banco + testes com Docker
│   └── executar-local.sh              # roda os testes contra uma API já no ar
├── src/test/
│   ├── java/com/esg/cidades/testes/
│   │   ├── ExecutorBddTest.java       # ponto de entrada da suíte
│   │   ├── passos/                    # implementação dos passos do Gherkin
│   │   └── suporte/                   # cliente HTTP, contexto do cenário, ganchos
│   └── resources/
│       ├── features/                  # cenários em Gherkin (português)
│       ├── schemas/                   # contratos JSON Schema
│       └── junit-platform.properties  # configuração do Cucumber e dos relatórios
├── Dockerfile                         # imagem que executa os testes
├── docker-compose.yml                 # API + PostgreSQL + testes
└── pom.xml
```

---

## ▶️ Como executar

### Opção 1 — Docker (recomendada, um comando só)

**Pré-requisito:** Docker Desktop com Docker Compose v2. No Windows, use o **Git Bash**.

```bash
bash scripts/executar-docker.sh
```

O script constrói a API a partir do repositório no GitHub, sobe o PostgreSQL, espera a API ficar saudável, roda os testes e derruba tudo. O código de saída é `0` quando todos passam.

Equivalente manual:

```bash
docker compose up --build --abort-on-container-exit --exit-code-from testes
docker compose down -v
```

Se preferir usar uma cópia local da API em vez do GitHub:

```bash
API_CONTEXT=../cidades-esg-inteligentes docker compose up --build --abort-on-container-exit --exit-code-from testes
```

### Opção 2 — Local, contra uma API que já está no ar

**Pré-requisitos:** Java 17 e Maven 3.9+.

1. Suba a API (na pasta do projeto da API):
   ```bash
   cp .env.example .env
   docker compose --env-file .env up -d --build
   ```
   Ela fica em `http://localhost:8080`.
2. Rode os testes (na pasta deste projeto):
   ```bash
   bash scripts/executar-local.sh
   ```
   ou direto com o Maven:
   ```bash
   mvn test -Dapi.url=http://localhost:8080
   ```

A URL da API também pode vir da variável de ambiente `API_URL`. Sem nada informado, usa `http://localhost:8080`.

### Rodar só parte dos cenários (tags)

```bash
mvn test -Dcucumber.filter.tags="@negativo"        # só os cenários de falha
mvn test -Dcucumber.filter.tags="@contrato"        # só os que validam JSON Schema
mvn test -Dcucumber.filter.tags="@cadastro"        # só o cadastro
mvn test -Dcucumber.filter.tags="@governanca"      # só governança e rastreabilidade
```

### Opção 2b — Local sem Docker (API com banco em memória)

Útil quando o Docker não está disponível. Em um terminal, na pasta da API:

```bash
mvn spring-boot:run -Dspring-boot.run.useTestClasspath=true -Dspring-boot.run.profiles=test \
  -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL --spring.datasource.driver-class-name=org.h2.Driver --spring.datasource.username=sa --spring.datasource.password= --spring.jpa.hibernate.ddl-auto=create-drop --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
```

Quando aparecer `Started CidadesEsgApplication`, rode os testes em outro terminal, na pasta deste projeto:

```bash
mvn -B test -Dapi.url=http://localhost:8080
```

### Opção 3 — Pipeline de CI (GitHub Actions)

O arquivo `.github/workflows/testes-bdd.yml` (na raiz do repositório) roda a Opção 1 a cada `push` na `main` ou na branch `testes-bdd`, em `pull_request` para a `main`, e também manualmente em **Actions → Testes BDD e de contrato da API → Run workflow**. A API é construída do próprio repositório (`API_CONTEXT=..`). Ao final publica os relatórios como artefato (`relatorios-testes-bdd`) e um resumo na página da execução.

---

## 📊 Relatórios

Depois da execução, em `target/`:

| Arquivo | Conteúdo |
|---------|----------|
| `relatorios/relatorio-cucumber.html` | Relatório visual: cada cenário, cada passo e, em anexo, **todas as requisições e respostas da API** |
| `relatorios/cucumber.json` | Resultado em JSON (para integrações) |
| `relatorios/cucumber.xml` | Resultado em formato JUnit |
| `surefire-reports/` | Resumo do Maven Surefire |

---

## 🛠️ Tecnologias

- **Java 17** e **Maven**
- **Cucumber 7** (Gherkin em português) com **JUnit Platform Suite**
- **REST Assured 5** (chamadas HTTP) e **json-schema-validator** (contratos, JSON Schema draft-04)
- **PicoContainer** (compartilha o estado entre os passos de um mesmo cenário)
- **Docker / Docker Compose** e **GitHub Actions**

---

## ❓ Problemas comuns

| Sintoma | Causa provável |
|---------|----------------|
| `ERRO: a API não respondeu UP` | A API não está no ar na URL informada. Confira `http://localhost:8080/actuator/health`. |
| `Connection refused` nos testes | Mesma causa, ou `API_URL` apontando para o endereço errado. |
| Porta 8080 ocupada ao subir a API | Pare o outro serviço ou altere `APP_PORT` no `.env` da API e passe `-Dapi.url` com a nova porta. |
| `docker compose` não encontra o contexto da API | Sem internet para acessar o GitHub. Use `API_CONTEXT=../cidades-esg-inteligentes`. |
