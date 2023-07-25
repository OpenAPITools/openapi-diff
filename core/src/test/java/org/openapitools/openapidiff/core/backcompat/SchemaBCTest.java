package org.openapitools.openapidiff.core.backcompat;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecChangedButCompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

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
    assertOpenApiBackwardIncompatible(BASE, "bc_schema_discriminator_changed.yaml");
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
    assertOpenApiBackwardIncompatible(BASE, "bc_response_schema_props_required_decreased.yaml");
  }

  @Test
  public void typeChanged() {
    assertOpenApiBackwardIncompatible(BASE, "bc_schema_type_changed.yaml");
  }
}
