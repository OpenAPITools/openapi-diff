# OpenAPI-diff

Compare two OpenAPI specifications(3.x) and render the difference to html file or markdown file.

# Requirements
`jdk1.8+`

# Feature
* Supports OpenAPi spec v3.0.
* Depth comparison of parameters, responses, endpoint, http method(GET,POST,PUT,DELETE...)
* Supports swagger api Authorization
* Render difference of property with Expression Language
* html & markdown render

# Maven

```xml
<dependency>
        <groupId>com.qdesrame</groupId>
        <artifactId>openapi-diff</artifactId>
	<version>1.0.0</version>
</dependency>
```

# Usage
OpenDiff can read swagger api spec from json file or http.
```java
final String OPENAPI_V3_DOC1 = "petstore_v3_1.json";
final String OPENAPI_V3_DOC2 = "http://petstore.swagger.io/v2/swagger.json";

SwaggerDiff diff = OpenApiDiff.compare(SWAGGER_V3_DOC1, SWAGGER_V3_DOC2);
```

# Render difference
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
```markdown
### What's New
---
* `GET` /pet/{petId} Find pet by ID

### What's Deprecated
---
* `POST` /pet/{petId} Updates a pet in the store with form data

### What's Changed
---
* `PUT` /pet Update an existing pet  
    Parameter

        Add body.newField //a field demo
        Add body.category.newCatField
        Delete body.category.name
* `POST` /pet Add a new pet to the store  
    Parameter

        Add tags //add new query param demo
        Add body.newField //a field demo
        Add body.category.newCatField
        Delete body.category.name
* `DELETE` /pet/{petId} Deletes a pet  
    Parameter

        Add newHeaderParam
* `POST` /pet/{petId}/uploadImage uploads an image for pet  
    Parameter

        petId change into not required Notes ID of pet to update change into ID of pet to update, default false
* `POST` /user Create user  
    Parameter

        Add body.newUserFeild //a new user feild demo
        Delete body.phone
* `GET` /user/login Logs user into the system  
    Parameter

        Delete password //The password for login in clear text
* `GET` /user/{username} Get user by user name  
    Return Type

        Add newUserFeild //a new user feild demo
        Delete phone
* `PUT` /user/{username} Updated user  
    Parameter

        Add body.newUserFeild //a new user feild demo
        Delete body.phone

```

# License
open-diff is released under the Apache License 2.0.





