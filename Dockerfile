FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build
COPY ./ /build
RUN ./mvnw -V -B -ff -P docker package -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/cli/target/openapi-diff-cli-*-all.jar /app/openapi-diff.jar
ENTRYPOINT ["java", "-jar", "/app/openapi-diff.jar"]
CMD ["--help"]
