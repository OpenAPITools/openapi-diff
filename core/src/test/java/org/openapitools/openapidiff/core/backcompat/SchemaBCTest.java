package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;
import static org.openapitools.openapidiff.core.model.BackwardIncompatibleProp.*;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.BackwardIncompatibleProp;

public class SchemaBCTest {
  private final String BASE = "bc_schema_base.yaml";

  @Test
  public void unchanged() {
    assertSpecUnchanged(BASE, BASE);
  }

  @Test
  public void changedButCompatible() {
    assertSpecChangedButCompatible(BASE, "bc_schema_changed_but_compatible.yaml");
  }

  @Test
  public void discriminatorChanged() {
    BackwardIncompatibleProp prop = SCHEMA_DISCRIMINATOR_CHANGED;
    assertSpecIncompatible(BASE, "bc_schema_discriminator_changed.yaml", prop);
  }

  @Test
  public void requestFormatDecreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_request_schema_format_decreased.yaml");
  }

  @Test
  public void requestFormatIncreased() {
    // TODO: Document why desired or remove support (test added to avoid unintentional regression)
    assertOpenApiBackwardIncompatible(BASE, "bc_request_schema_format_increased.yaml");
  }

  @Test
  public void requestPropsPutIncreased() {
    // See https://github.com/OpenAPITools/openapi-diff/issues/537
    assertSpecChangedButCompatible(BASE, "bc_request_schema_props_put_increased.yaml");
  }

  @Test
  public void responseFormatDecreased() {
    // TODO: Document why desired or remove support (test added to avoid unintentional regression)
    assertOpenApiBackwardIncompatible(BASE, "bc_response_schema_format_decreased.yaml");
  }

  @Test
  public void responseFormatIncreased() {
    assertOpenApiBackwardIncompatible(BASE, "bc_response_schema_format_increased.yaml");
  }

  @Test
  public void responsePropsRequiredDecreased() {
    BackwardIncompatibleProp prop = RESPONSE_REQUIRED_DECREASED;
    assertSpecIncompatible(BASE, "bc_response_schema_props_required_decreased.yaml", prop);
  }

  @Test
  public void typeChanged() {
    BackwardIncompatibleProp prop = SCHEMA_TYPE_CHANGED;
    assertSpecIncompatible(BASE, "bc_schema_type_changed.yaml", prop);
  }

  @Test
  public void patternChanged() {
    BackwardIncompatibleProp prop = SCHEMA_PATTERN_CHANGED;
    assertSpecIncompatible(BASE, "bc_schema_pattern_changed.yaml", prop);
  }

  @Test
  public void minPropertiesIncreased() {
    BackwardIncompatibleProp prop = SCHEMA_MIN_PROPERTIES_CHANGED;
    assertSpecIncompatible(BASE, "bc_schema_min_properties_increased.yaml", prop);
  }

  @Test
  public void minPropertiesAdded() {
    BackwardIncompatibleProp prop = SCHEMA_MIN_PROPERTIES_CHANGED;
    assertSpecIncompatible(BASE, "bc_schema_min_properties_added.yaml", prop);
  }

  @Test
  public void maxPropertiesDecreased() {
    BackwardIncompatibleProp prop = SCHEMA_MAX_PROPERTIES_CHANGED;
    assertSpecIncompatible(BASE, "bc_schema_max_properties_decreased.yaml", prop);
  }

  @Test
  public void maxPropertiesAdded() {
    BackwardIncompatibleProp prop = SCHEMA_MAX_PROPERTIES_CHANGED;
    assertSpecIncompatible(BASE, "bc_schema_max_properties_added.yaml", prop);
  }
}
