FROM maven:3.5-jdk-8-alpine as build
WORKDIR /app
COPY ./ /app
RUN mvn install -q && \
  mvn package -q && \
  ls /app/target/ && \
  MVN_VERSION=$(mvn -q \
    -Dexec.executable="echo" \
    -Dexec.args='${project.version}' \
    --non-recursive \
    org.codehaus.mojo:exec-maven-plugin:1.6.0:exec) && \
  mv /app/target/openapi-diff-${MVN_VERSION}-jar-with-dependencies.jar /app/openapi-diff.jar

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=0 /app/openapi-diff.jar /app
ENTRYPOINT ["java", "-jar", "/app/openapi-diff.jar"]
CMD ["--help"]
