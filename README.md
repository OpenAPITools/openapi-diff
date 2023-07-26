# OpenAPI-diff 

Compare two OpenAPI specifications (3.x) and render the difference to HTML plaintext, or Markdown files.

[![Build](https://github.com/OpenAPITools/openapi-diff/workflows/Main%20Build/badge.svg)](https://github.com/OpenAPITools/openapi-diff/actions?query=branch%3Amaster+workflow%3A"Main+Build")
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=OpenAPITools_openapi-diff&metric=alert_status)](https://sonarcloud.io/dashboard?id=OpenAPITools_openapi-diff)
[![Maven Central](https://img.shields.io/maven-central/v/org.openapitools.openapidiff/openapi-diff-core)](https://search.maven.org/artifact/org.openapitools.openapidiff/openapi-diff-core)

[![Contribute with Gitpod](https://img.shields.io/badge/Contribute%20with-Gitpod-908a85?logo=gitpod)](https://gitpod.io/#https://github.com/OpenAPITools/openapi-diff)
[![Join the Slack chat room](https://img.shields.io/badge/Slack-Join%20the%20chat%20room-orange)](https://join.slack.com/t/openapi-generator/shared_invite/enQtNzAyNDMyOTU0OTE1LTY5ZDBiNDI5NzI5ZjQ1Y2E5OWVjMjZkYzY1ZGM2MWQ4YWFjMzcyNDY5MGI4NjQxNDBiMTlmZTc5NjY2ZTQ5MGM)

[![Docker Cloud Automated build](https://img.shields.io/docker/cloud/automated/openapitools/openapi-diff)](https://hub.docker.com/r/openapitools/openapi-diff)
[![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/openapitools/openapi-diff)](https://hub.docker.com/r/openapitools/openapi-diff)
[![Docker Image Version](https://img.shields.io/docker/v/openapitools/openapi-diff?sort=semver)](https://hub.docker.com/r/openapitools/openapi-diff/tags)

# Requirements

* Java 8

# Feature

* Supports OpenAPI spec v3.0.
* Depth comparison of parameters, responses, endpoint, http method (GET,POST,PUT,DELETE...)
* Supports swagger api Authorization
* Render difference of property with Expression Language
* HTML, Markdown & JSON render

# Maven

Available on [Maven Central](https://search.maven.org/artifact/org.openapitools.openapidiff/openapi-diff-core)

```xml
<dependency>
  <groupId>org.openapitools.openapidiff</groupId>
  <artifactId>openapi-diff-core</artifactId>
  <version>${openapi-diff-version}</version>
</dependency>
```

# Docker

Available on [Docker Hub](https://hub.docker.com/r/openapitools/openapi-diff/) as `openapitools/openapi-diff`.

```bash
# docker run openapitools/openapi-diff:latest
usage: openapi-diff <old> <new>
    --debug                     Print debugging information
    --error                     Print error information
    --fail-on-changed           Fail if API changed but is backward
                                compatible
    --fail-on-incompatible      Fail only if API changes broke backward
                                compatibility
    --config-file               Config file to override default behavior. Supported file formats: .yaml
    --config-prop               Config property to override default behavior with key:value format (e.g. my.prop:true)
 -h,--help                      print this message
    --header <property=value>   use given header for authorisation
    --html <file>               export diff as html in given file
    --info                      Print additional information
    --json <file>               export diff as json in given file
 -l,--log <level>               use given level for log (TRACE, DEBUG,
                                INFO, WARN, ERROR, OFF). Default: ERROR
    --markdown <file>           export diff as markdown in given file
    --off                       No information printed
    --query <property=value>    use query param for authorisation
    --state                     Only output diff state: no_changes,
                                incompatible, compatible
    --text <file>               export diff as text in given file
    --trace                     be extra verbose
    --version                   print the version information and exit
    --warn                      Print warning information
```


## Build the image

This is only required if you want to try new changes in the Dockerfile of this project.

```bash
docker build -t local-openapi-diff .
```

You can replace the local image name `local-openapi-diff` by any name of your choice.

## Run an instance

In this example the `$(pwd)/core/src/test/resources` directory is mounted in the `/specs` directory of the container
in readonly mode (`ro`).

```bash
docker run --rm -t \
  -v $(pwd)/core/src/test/resources:/specs:ro \
  openapitools/openapi-diff:latest /specs/path_1.yaml /specs/path_2.yaml
```

The remote name `openapitools/openapi-diff` can be replaced with `local-openapi-diff` or the name you gave to your local image.

# Usage

openapi-diff can read OpenAPI specs from JSON files or HTTP URLs.

## Command Line

```bash
$ openapi-diff --help
usage: openapi-diff <old> <new>
    --debug                     Print debugging information
    --error                     Print error information
 -h,--help                      print this message
    --header <property=value>   use given header for authorisation
    --html <file>               export diff as html in given file
    --info                      Print additional information
    --json <file>               export diff as json in given file
 -l,--log <level>               use given level for log (TRACE, DEBUG,
                                INFO, WARN, ERROR, OFF). Default: ERROR
    --markdown <file>           export diff as markdown in given file
    --off                       No information printed
    --query <property=value>    use query param for authorisation
    --state                     Only output diff state: no_changes,
                                incompatible, compatible
    --fail-on-incompatible      Fail only if API changes broke backward compatibility
    --fail-on-changed           Fail if API changed but is backward compatible
    --config-file               Config file to override default behavior. Supported file formats: .yaml
    --config-prop               Config property to override default behavior with key:value format (e.g. my.prop:true)
    --trace                     be extra verbose
    --version                   print the version information and exit
    --warn                      Print warning information
```

## Maven Plugin

Add openapi-diff to your POM to show diffs when you test your Maven project. You may opt to throw an error if you have broken backwards compatibility or if your API has changed.  

```xml
<plugin>
  <groupId>org.openapitools.openapidiff</groupId>
  <artifactId>openapi-diff-maven</artifactId>
  <version>${openapi-diff-version}</version>
  <executions>
    <execution>
      <goals>
        <goal>diff</goal>
      </goals>
      <configuration>
        <!-- Reference specification (perhaps your prod schema) -->
        <oldSpec>https://petstore3.swagger.io/api/v3/openapi.json</oldSpec>
        <!-- Specification generated by your project in the compile phase -->
        <newSpec>${project.basedir}/target/openapi.yaml</newSpec>
        <!-- Fail only if API changes broke backward compatibility (default: false) -->
        <failOnIncompatible>true</failOnIncompatible>
        <!-- Fail if API changed (default: false) -->
        <failOnChanged>true</failOnChanged>
        <!-- Supply file path for console output to file if desired. -->
        <consoleOutputFileName>${project.basedir}/../maven/target/diff.txt</consoleOutputFileName>
        <!-- Supply file path for json output to file if desired. -->
        <jsonOutputFileName>${project.basedir}/../maven/target/diff.json</jsonOutputFileName>
        <!-- Supply file path for markdown output to file if desired. -->
        <markdownOutputFileName>${project.basedir}/../maven/target/diff.md</markdownOutputFileName>
        <!-- Supply config file(s), e.g. to disable incompatibility checks. Later files override earlier files -->
        <configFiles>
          <configFile>my/config-file.yaml</configFile>
        </configFiles>
        <!-- Supply config properties, e.g. to disable incompatibility checks. Overrides configFiles. -->
        <configProps>
          <incompatible.response.enum.increased>false</incompatible.response.enum.increased>
        </configProps>
      </configuration>
    </execution>
  </executions>
</plugin>
```

## Direct Invocation

```java
public class Main {
    public static final String OPENAPI_DOC1 = "petstore_v3_1.json";
    public static final String OPENAPI_DOC2 = "petstore_v2_2.yaml";
        
    public static void main(String[] args) {
        ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);

        //...
    }
}
```

### Render difference

---
#### HTML

```java
String html = new HtmlRender("Changelog",
        "http://deepoove.com/swagger-diff/stylesheets/demo.css")
                .render(diff);

try {
    FileWriter fw = new FileWriter("testNewApi.html");
    fw.write(html);
    fw.close();
} catch (IOException e) {
    e.printStackTrace();
}
```

#### Markdown

```java
String render = new MarkdownRender().render(diff);
try {
    FileWriter fw = new FileWriter("testDiff.md");
    fw.write(render);
    fw.close();
} catch (IOException e) {
    e.printStackTrace();
}
```


#### JSON

```java
String render = new JsonRender().render(diff);
try {
    FileWriter fw = new FileWriter("testDiff.json");
    fw.write(render);
    fw.close();
} catch (IOException e) {
    e.printStackTrace();
}
```

### Extensions

This project uses Java Service Provider Inteface (SPI) so additional extensions can be added. 

To build your own extension, you simply need to create a `src/main/resources/META-INF/services/org.openapitools.openapidiff.core.compare.ExtensionDiff` file with the full classname of your implementation.
Your class must also implement the `org.openapitools.openapidiff.core.compare.ExtensionDiff` interface.
Then, including your library with the `openapi-diff` module will cause it to be triggered automatically.

# Examples

### CLI Output

```text
==========================================================================
==                            API CHANGE LOG                            ==
==========================================================================
                             Swagger Petstore                             
--------------------------------------------------------------------------
--                              What's New                              --
--------------------------------------------------------------------------
- GET    /pet/{petId}

--------------------------------------------------------------------------
--                            What's Deleted                            --
--------------------------------------------------------------------------
- POST   /pet/{petId}

--------------------------------------------------------------------------
--                          What's Deprecated                           --
--------------------------------------------------------------------------
- GET    /user/logout

--------------------------------------------------------------------------
--                            What's Changed                            --
--------------------------------------------------------------------------
- PUT    /pet
  Request:
        - Deleted application/xml
        - Changed application/json
          Schema: Backward compatible
- POST   /pet
  Parameter:
    - Add tags in query
  Request:
        - Changed application/xml
          Schema: Backward compatible
        - Changed application/json
          Schema: Backward compatible
- GET    /pet/findByStatus
  Parameter:
    - Deprecated status in query
  Return Type:
    - Changed 200 OK
      Media types:
        - Changed application/xml
          Schema: Broken compatibility
        - Changed application/json
          Schema: Broken compatibility
- GET    /pet/findByTags
  Return Type:
    - Changed 200 OK
      Media types:
        - Changed application/xml
          Schema: Broken compatibility
        - Changed application/json
          Schema: Broken compatibility
- DELETE /pet/{petId}
  Parameter:
    - Add newHeaderParam in header
- POST   /pet/{petId}/uploadImage
  Parameter:
    - Changed petId in path
- POST   /user
  Request:
        - Changed application/json
          Schema: Backward compatible
- POST   /user/createWithArray
  Request:
        - Changed application/json
          Schema: Backward compatible
- POST   /user/createWithList
  Request:
        - Changed application/json
          Schema: Backward compatible
- GET    /user/login
  Parameter:
    - Delete password in query
- GET    /user/logout
- GET    /user/{username}
  Return Type:
    - Changed 200 OK
      Media types:
        - Changed application/xml
          Schema: Broken compatibility
        - Changed application/json
          Schema: Broken compatibility
- PUT    /user/{username}
  Request:
        - Changed application/json
          Schema: Backward compatible
--------------------------------------------------------------------------
--                                Result                                --
--------------------------------------------------------------------------
                 API changes broke backward compatibility                 
--------------------------------------------------------------------------
```

### Markdown

```markdown
### What's New
---
* `GET` /pet/{petId} Find pet by ID

### What's Deleted
---
* `POST` /pet/{petId} Updates a pet in the store with form data

### What's Deprecated
---
* `GET` /user/logout Logs out current logged in user session

### What's Changed
---
* `PUT` /pet Update an existing pet  
    Request

        Deleted request body : [application/xml]
        Changed response : [application/json]
* `POST` /pet Add a new pet to the store  
    Parameter

        Add tags //add new query param demo
    Request

        Changed response : [application/xml]
        Changed response : [application/json]
* `GET` /pet/findByStatus Finds Pets by status  
    Parameter

    Return Type

        Changed response : [200] //successful operation
* `GET` /pet/findByTags Finds Pets by tags  
    Return Type

        Changed response : [200] //successful operation
* `DELETE` /pet/{petId} Deletes a pet  
    Parameter

        Add newHeaderParam
* `POST` /pet/{petId}/uploadImage uploads an image for pet  
    Parameter

        petId Notes ID of pet to update change into ID of pet to update, default false
* `POST` /user Create user  
    Request

        Changed response : [application/json]
* `POST` /user/createWithArray Creates list of users with given input array  
    Request

        Changed response : [application/json]
* `POST` /user/createWithList Creates list of users with given input array  
    Request

        Changed response : [application/json]
* `GET` /user/login Logs user into the system  
    Parameter

        Delete password //The password for login in clear text
* `GET` /user/logout Logs out current logged in user session  
* `PUT` /user/{username} Updated user  
    Request

        Changed response : [application/json]
* `GET` /user/{username} Get user by user name  
    Return Type

        Changed response : [200] //successful operation
```

### JSON

```json
{
    "changedElements": [...],
    "changedExtensions": null,
    "changedOperations": [...],
    "compatible": false,
    "deprecatedEndpoints": [...],
    "different": true,
    "incompatible": true,
    "missingEndpoints": [...],
    "newEndpoints": [
        {
            "method": "GET",
            "operation": {
                "callbacks": null,
                "deprecated": null,
                "description": "Returns a single pet",
                "extensions": null,
                "externalDocs": null,
                "operationId": "getPetById",
                "parameters": [
                    {
                        "$ref": null,
                        "allowEmptyValue": null,
                        "allowReserved": null,
                        "content": null,
                        "deprecated": null,
                        "description": "ID of pet to return",
                        "example": null,
                        "examples": null,
                        "explode": false,
                        "extensions": null,
                        "in": "path",
                        "name": "petId",
                        "required": true,
                        "schema": {...},
                        "style": "SIMPLE"
                    }
                ],
                "requestBody": null,
                "responses": {...},
                "security": [
                    {
                        "api_key": []
                    }
                ],
                "servers": null,
                "summary": "Find pet by ID",
                "tags": [
                    "pet"
                ]
            },
            "path": null,
            "pathUrl": "/pet/{petId}",
            "summary": "Find pet by ID"
        }
    ],
    "newSpecOpenApi": {...},
    "oldSpecOpenApi": {...},
    "unchanged": false
}
```

# License

openapi-diff is released under the Apache License 2.0.

# Thanks

* Adarsh Sharma / [adarshsharma](https://github.com/adarshsharma)
* Quentin Desramé / [quen2404](https://github.com/quen2404)
* [Sayi](https://github.com/Sayi) for his project [swagger-diff](https://github.com/Sayi/swagger-diff) 
  which was a source of inspiration for this tool
