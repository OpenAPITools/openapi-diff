FROM openjdk:8-jdk-alpine AS build
WORKDIR /build
COPY ./ /build
RUN ./mvnw -V -B -ff -P cli package -q

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=build /build/target/openapi-diff-jar-with-dependencies.jar /app/openapi-diff.jar
ENTRYPOINT ["java", "-jar", "/app/openapi-diff.jar"]
CMD ["--help"]
