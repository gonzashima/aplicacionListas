# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar pom del padre primero (para cache de dependencias)
COPY pom.xml .
COPY aplicacion-web/pom.xml aplicacion-web/pom.xml

# Descargar dependencias (cacheado si los pom no cambian)
RUN mvn -f aplicacion-web/pom.xml dependency:go-offline -B

# Copiar fuentes y compilar
COPY aplicacion-web/src aplicacion-web/src
RUN mvn -f aplicacion-web/pom.xml package -DskipTests -B

# ---- Run stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/aplicacion-web/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

