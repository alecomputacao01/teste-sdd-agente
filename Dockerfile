# Multi-stage: compila com Maven (JDK 21, mesma versão de <java.version> no pom.xml) e roda com
# JRE 21 apenas — imagem final não carrega o Maven/JDK completo.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
# Sem "-u 1000": a imagem base já tem um usuário nessa UID — deixa o useradd escolher uma livre.
RUN useradd -m app
USER app
WORKDIR /app
COPY --from=build --chown=app:app /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
