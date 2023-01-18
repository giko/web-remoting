FROM quay.io/quarkus/ubi-quarkus-graalvmce-builder-image:22.3-java17 AS builder

WORKDIR /app
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml

RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

COPY src src
RUN ./mvnw package -Pnative

FROM quay.io/quarkus/quarkus-micro-image:2.0
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/target/*-runner /app/application
RUN chmod +x /app/application
ENTRYPOINT ["/app/application"]
