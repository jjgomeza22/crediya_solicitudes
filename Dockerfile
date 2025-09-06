FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY main.gradle .
COPY gradle.properties .
COPY build.gradle .

COPY applications applications
COPY domain domain
COPY infrastructure infrastructure

ARG ENV
COPY deployment/enviroments/application-${ENV}.yaml /app/applications/app-service/src/main/resources/application.yaml

RUN chmod +x gradlew
RUN ./gradlew clean build -x validateStructure -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/applications/app-service/build/libs/crediYaSolicitudes.jar .
CMD ["java", "-jar", "crediYaSolicitudes.jar"]