# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos de configuración
COPY pom.xml .
COPY src ./src

# Compilar (esto va a recrear la carpeta target dentro del contenedor)
RUN mvn clean package -DskipTests -B

# ---- Run stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Aquí buscamos el jar que Maven acaba de crear en la etapa anterior
COPY --from=build /app/target/*.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app.jar"]
