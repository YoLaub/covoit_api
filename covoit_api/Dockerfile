# ============================================================
# Étape 1 : Build du WAR avec Maven
# ============================================================
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================================
# Étape 2 : Image légère pour l'exécution
# ============================================================
FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /app/target/*.war app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.war"]