package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class AddPropPutDiffTest {
  private final String OPENAPI_DOC1 = "add-prop-put-1.yaml";
  private final String OPENAPI_DOC2 = "add-prop-put-2.yaml";

  @Test
  public void testDiffSame() {
    assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
  }

  @Test
  public void testFieldAdditionalInPutApiIsCompatible() {
    // See https://github.com/OpenAPITools/openapi-diff/pull/537
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    assertThat(changedOpenApi.isDifferent()).isTrue();
    assertThat(changedOpenApi.isCompatible()).isTrue();
  }
}
