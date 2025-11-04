# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Porta
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8083/actuator/health || exit 1

# Executar aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]