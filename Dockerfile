FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build
COPY ./ /build
RUN ./mvnw -V -B -ff -P docker package -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/cli/target/openapi-diff.jar /app/openapi-diff.jar
COPY --from=build /build/core/src/test/resources/petstore_v2_* /tmp
RUN java -XX:ArchiveClassesAtExit=/app/appcds.jsa \
    -jar /app/openapi-diff.jar \
    /tmp/petstore_v2_1.yaml \
    /tmp/petstore_v2_2.yaml \
    && rm -f /tmp/petstore_v2_*
ENTRYPOINT ["java", "-Xshare:on", "-XX:SharedArchiveFile=/app/appcds.jsa", "-jar", "/app/openapi-diff.jar"]
CMD ["--help"]
