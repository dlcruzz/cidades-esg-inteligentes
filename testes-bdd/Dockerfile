# ==========================================================
# Dockerfile - Testes BDD da API Cidades ESG Inteligentes
# Executa a suíte Cucumber + REST Assured contra uma API já
# em execução (URL informada em API_URL).
# ==========================================================
FROM maven:3.9-eclipse-temurin-17

WORKDIR /testes

# Copia só o pom.xml primeiro para aproveitar o cache de camadas:
# as dependências só são baixadas de novo se o pom mudar.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Código dos testes (cenários .feature, schemas e passos em Java)
COPY src ./src

# Endereço da API sob teste (no docker-compose é o serviço "api")
ENV API_URL=http://api:8080

# O código de saída do Maven vira o código de saída do contêiner:
# 0 = todos os testes passaram; diferente de 0 = houve falha.
CMD ["mvn", "-B", "test"]
