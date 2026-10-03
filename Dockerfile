# ==========================================================
# Dockerfile - Cidades ESG Inteligentes
# Estratégia: build multi-stage para manter a imagem final
# enxuta (sem Maven/JDK completo) e mais segura (usuário
# não-root, apenas JRE em runtime).
# ==========================================================

# ---------- STAGE 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache de camadas
# do Docker: dependências só são baixadas de novo se o pom mudar.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o restante do código-fonte e gera o artefato .jar
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- STAGE 2: runtime ----------
FROM eclipse-temurin:17-jre-jammy AS runtime

# wget é usado apenas pelo HEALTHCHECK abaixo
RUN apt-get update && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

# Usuário não-root por segurança
RUN groupadd -r esg && useradd -r -g esg esgapp

WORKDIR /app

# Copia apenas o .jar gerado no estágio de build (imagem final não tem Maven/JDK)
COPY --from=build /app/target/cidades-esg-inteligentes.jar app.jar

RUN chown -R esgapp:esg /app
USER esgapp

EXPOSE 8080

# Healthcheck usado pelo docker-compose / orquestrador
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# MaxRAMPercentage: a JVM respeita o limite de memória do container
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
