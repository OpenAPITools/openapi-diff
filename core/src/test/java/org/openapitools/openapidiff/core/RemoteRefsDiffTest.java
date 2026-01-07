package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiChangedEndpoints;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;

import org.junit.jupiter.api.Test;

public class RemoteRefsDiffTest {

  private final String OPENAPI_NESTED_REFS = "remoteRefs/specUsingNestedRefs.yaml";
  private final String OPENAPI_NO_REFS = "remoteRefs/specUsingNoRefs.yaml";
  private final String OPENAPI_SIMPLE_REFS = "remoteRefs/specUsingSimpleRefs.yaml";
  private final String OPENAPI_ELEMENT_REFS = "remoteRefs/specUsingElementRefs.yaml";
  private final String OPENAPI_MODIFIED_REFS = "remoteRefs/specUsingModifiedRefs.yaml";

  @Test
  public void testDiffSpecIsSameAsItself() {
    assertOpenApiAreEquals(OPENAPI_NESTED_REFS, OPENAPI_NESTED_REFS);
  }

  @Test
  public void testDiffRefsSameAsInline() {
    assertOpenApiAreEquals(OPENAPI_NESTED_REFS, OPENAPI_NO_REFS);
  }

  @Test
  public void testDiffNestedRefsSameAsSimpleRefs() {
    assertOpenApiAreEquals(OPENAPI_NESTED_REFS, OPENAPI_SIMPLE_REFS);
  }

  @Test
  public void testDiffNestedRefsSameAsElementRefs() {
    assertOpenApiAreEquals(OPENAPI_NESTED_REFS, OPENAPI_ELEMENT_REFS);
  }

  @Test
  public void testDiffShowsChangesWhenRefContentChanges() {
    assertOpenApiChangedEndpoints(OPENAPI_NESTED_REFS, OPENAPI_MODIFIED_REFS);
    assertSpecChangedButCompatible(OPENAPI_NESTED_REFS, OPENAPI_MODIFIED_REFS);
  }
}
