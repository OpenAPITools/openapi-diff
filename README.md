# OpenAPI-diff 

Compare two OpenAPI specifications (3.x) and render the difference to HTML plaintext, or Markdown files.

[![Test](https://github.com/OpenAPITools/openapi-diff/workflows/Test/badge.svg)](https://github.com/OpenAPITools/openapi-diff/actions?query=branch%3Amaster+workflow%3ATest+)

[![Join the Slack chat room](https://img.shields.io/badge/Slack-Join%20the%20chat%20room-orange)](https://join.slack.com/t/openapi-generator/shared_invite/enQtNzAyNDMyOTU0OTE1LTY5ZDBiNDI5NzI5ZjQ1Y2E5OWVjMjZkYzY1ZGM2MWQ4YWFjMzcyNDY5MGI4NjQxNDBiMTlmZTc5NjY2ZTQ5MGM)

# Requirements

* Java 8

# Feature

* Supports OpenAPI spec v3.0.
* Depth comparison of parameters, responses, endpoint, http method (GET,POST,PUT,DELETE...)
* Supports swagger api Authorization
* Render difference of property with Expression Language
* HTML & Markdown render

# Maven

Available on [Maven Central](https://search.maven.org/artifact/com.github.joschi.openapi-diff/core)

```xml
<dependency>
  <groupId>com.github.joschi.openapi-diff</groupId>
  <artifactId>core</artifactId>
  <version>${openapi-diff-version}</version>
</dependency>
```

# Docker

Available on [Docker Hub](https://hub.docker.com/r/joschi/openapi-diff/) as `joschi/openapi-diff`.

```bash
# docker run joschi/openapi-diff:latest
usage: openapi-diff <old> <new>
    --debug                     Print debugging information
    --error                     Print error information
    --fail-on-changed           Fail if API changed but is backward
                                compatible
    --fail-on-incompatible      Fail only if API changes broke backward
                                compatibility
 -h,--help                      print this message
    --header <property=value>   use given header for authorisation
    --html <file>               export diff as html in given file
    --info                      Print additional information
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
docker run -t \
  -v $(pwd)/core/src/test/resources:/specs:ro \
  joschi/openapi-diff /specs/path_1.yaml /specs/path_2.yaml
```

The remote name `joschi/openapi-diff` can be replaced with `local-openapi-diff` or the name you gave to your local image.

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
 -l,--log <level>               use given level for log (TRACE, DEBUG,
                                INFO, WARN, ERROR, OFF). Default: ERROR
    --markdown <file>           export diff as markdown in given file
    --off                       No information printed
    --query <property=value>    use query param for authorisation
    --state                     Only output diff state: no_changes,
                                incompatible, compatible
    --fail-on-incompatible      Fail only if API changes broke backward compatibility
    --fail-on-changed           Fail if API changed but is backward compatible
    --trace                     be extra verbose
    --version                   print the version information and exit
    --warn                      Print warning information
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
    FileWriter fw = new FileWriter(
            "testNewApi.html");
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
    FileWriter fw = new FileWriter(
            "testDiff.md");
    fw.write(render);
    fw.close();
    
} catch (IOException e) {
    e.printStackTrace();
}
```

### Extensions

This project uses Java Service Provider Inteface (SPI) so additional extensions can be added. 

To build your own extension, you simply need to create a `src/main/resources/META-INF/services/com.qdesrame.openapi.diff.core.compare.ExtensionDiff` file with the full classname of your implementation.  Your class must also implement the `com.qdesrame.openapi.diff.core.compare.ExtensionDiff` interface.  Then, including your library with the `openapi-diff` module will cause it to be triggered automatically.

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

# License

openapi-diff is released under the Apache License 2.0.

# Thanks

* Adarsh Sharma / [adarshsharma](https://github.com/adarshsharma)
* Quentin Desramé / [quen2404](https://github.com/quen2404)
* [Sayi](https://github.com/Sayi) for his project [swagger-diff](https://github.com/Sayi/swagger-diff) 
  which was a source of inspiration for this tool
