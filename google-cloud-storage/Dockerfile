# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests

# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run with JVM arguments from env vars
ENTRYPOINT ["sh", "-c", "\
exec java \
-XX:+EnableDynamicAgentLoading \
-DPROJECT_ID=$PROJECT_ID \
-DGCS_BUCKET_NAME=$GCS_BUCKET_NAME \
-DIMPERSONATION_TARGET=$IMPERSONATION_TARGET \
-DOAUTH_ACCESS_TOKEN=$OAUTH_ACCESS_TOKEN \
-DGKMS_KEY_NAME=$GKMS_KEY_NAME \
-jar app.jar"]
