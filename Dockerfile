FROM amazoncorretto:17-al2-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw package

FROM amazoncorretto:17
WORKDIR /app
EXPOSE 8082, 8085
COPY --from=builder /app/target/web-remoting-*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
