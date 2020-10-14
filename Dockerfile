FROM openjdk:8-jdk-slim AS build
WORKDIR /build
COPY ./ /build
RUN ./mvnw -V -B -ff -P docker package -q

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /build/cli/target/openapi-diff-cli-*-all.jar /app/openapi-diff.jar
ENTRYPOINT ["java", "-jar", "/app/openapi-diff.jar"]
CMD ["--help"]
