package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.TestUtils.getOpenApiChangedOperations;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOperation;

public class ExamplesDiffTest {

  @Test
  public void issue666OAS30ExampleInMediaType() {
    List<ChangedOperation> changedOperations =
        getOpenApiChangedOperations(
            "issue-666-oas-3-0-examples-handling-1.yaml",
            "issue-666-oas-3-0-examples-handling-2.yaml");

    ChangedOperation requestMediaTypeExampleChanged =
        changedOperations.stream()
            .filter(o -> "/example/request/media-type".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);
    ChangedOperation responseMediaTypeExampleChanged =
        changedOperations.stream()
            .filter(o -> "/example/response/media-type".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);

    assertThat(requestMediaTypeExampleChanged).isNotNull();
    assertThat(responseMediaTypeExampleChanged).isNotNull();
  }

  @Test
  public void issue666OAS31ExamplesInMediaType() {
    List<ChangedOperation> changedOperations =
        getOpenApiChangedOperations(
            "issue-666-oas-3-1-examples-handling-1.yaml",
            "issue-666-oas-3-1-examples-handling-2.yaml");

    ChangedOperation requestMediaTypeExamplesChanged =
        changedOperations.stream()
            .filter(o -> "/examples/request/media-type".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);
    ChangedOperation responseMediaTypeExamplesChanged =
        changedOperations.stream()
            .filter(o -> "/examples/response/media-type".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);

    assertThat(requestMediaTypeExamplesChanged).isNotNull();
    assertThat(responseMediaTypeExamplesChanged).isNotNull();
  }

  @Test
  public void issue666OAS30ExampleInParameter() {
    List<ChangedOperation> changedOperations =
        getOpenApiChangedOperations(
            "issue-666-oas-3-0-examples-handling-1.yaml",
            "issue-666-oas-3-0-examples-handling-2.yaml");

    ChangedOperation paramExampleChanged =
        changedOperations.stream()
            .filter(o -> "/example/parameter/{id}".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);

    assertThat(paramExampleChanged).isNotNull();
  }

  @Test
  public void issue666OAS31ExamplesInParameter() {
    List<ChangedOperation> changedOperations =
        getOpenApiChangedOperations(
            "issue-666-oas-3-1-examples-handling-1.yaml",
            "issue-666-oas-3-1-examples-handling-2.yaml");

    ChangedOperation paramExamplesChanged =
        changedOperations.stream()
            .filter(o -> "/examples/parameter/{id}".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);

    assertThat(paramExamplesChanged).isNotNull();
  }

  @Test
  public void issue666OAS30ExampleInResponseHeader() {
    List<ChangedOperation> changedOperations =
        getOpenApiChangedOperations(
            "issue-666-oas-3-0-examples-handling-1.yaml",
            "issue-666-oas-3-0-examples-handling-2.yaml");

    ChangedOperation operation =
        changedOperations.stream()
            .filter(o -> "/example/response/header".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);

    assertThat(operation).isNotNull();
  }

  @Test
  public void issue666OAS31ExamplesInResponseHeader() {
    List<ChangedOperation> changedOperations =
        getOpenApiChangedOperations(
            "issue-666-oas-3-1-examples-handling-1.yaml",
            "issue-666-oas-3-1-examples-handling-2.yaml");

    ChangedOperation operation =
        changedOperations.stream()
            .filter(o -> "/examples/response/header".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);

    assertThat(operation).isNotNull();
  }

  @Test
  public void issue666OAS30ExampleInSchema() {
    List<ChangedOperation> changedOperations =
        getOpenApiChangedOperations(
            "issue-666-oas-3-0-examples-handling-1.yaml",
            "issue-666-oas-3-0-examples-handling-2.yaml");

    ChangedOperation operation =
        changedOperations.stream()
            .filter(o -> "/example/model/property".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);

    assertThat(operation).isNotNull();
  }

  @Test
  public void issue666OAS31ExamplesInSchema() {
    List<ChangedOperation> changedOperations =
        getOpenApiChangedOperations(
            "issue-666-oas-3-1-examples-handling-1.yaml",
            "issue-666-oas-3-1-examples-handling-2.yaml");

    ChangedOperation operation =
        changedOperations.stream()
            .filter(o -> "/examples/model/property".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);

    assertThat(operation).isNotNull();
  }

  @Test
  public void issue666OAS30ExampleInComposedSchema() {
    List<ChangedOperation> changedOperations =
        getOpenApiChangedOperations(
            "issue-666-oas-3-0-examples-handling-1.yaml",
            "issue-666-oas-3-0-examples-handling-2.yaml");

    ChangedOperation operation =
        changedOperations.stream()
            .filter(o -> "/example/model/property/composed".equals(o.getPathUrl()))
            .findFirst()
            .orElse(null);

    assertThat(operation).isNotNull();
  }
}
