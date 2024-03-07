package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiChangedEndpoints;

import org.junit.jupiter.api.Test;

public class ParameterDiffTest {

  @Test
  public void testDiffDifferent() {
    assertOpenApiChangedEndpoints("parameters_diff_1.yaml", "parameters_diff_2.yaml");
  }

  @Test
  public void issue458MaximumDecreased() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_1.yaml", "issue-458-integer-limits_2.yaml");
  }

  @Test
  public void issue458MaximumIncreased() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_1.yaml", "issue-458-integer-limits_3.yaml");
  }

  @Test
  public void issue458MinimumDecreased() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_1.yaml", "issue-458-integer-limits_4.yaml");
  }

  @Test
  public void issue458MinimumIncreased() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_1.yaml", "issue-458-integer-limits_5.yaml");
  }

  @Test
  public void issue458IntegerFormatChanged() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_1.yaml", "issue-458-integer-limits_6.yaml");
  }

  @Test
  public void issue458ExclusiveMinimumChanged() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_1.yaml", "issue-458-integer-limits_7.yaml");
  }

  @Test
  public void issue458ExclusiveMaximumChanged() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_1.yaml", "issue-458-integer-limits_8.yaml");
  }

  @Test
  public void issue458ExclusiveMinimumRemoved() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_1.yaml", "issue-458-integer-limits_9.yaml");
  }

  @Test
  public void issue458ExclusiveMaximumRemoved() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_1.yaml", "issue-458-integer-limits_10.yaml");
  }

  @Test
  public void issue458ExclusiveMaximumTrueToFalse() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_11.yaml", "issue-458-integer-limits_12.yaml");
  }

  @Test
  public void issue458ExclusiveMinimumTrueToFalse() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_11.yaml", "issue-458-integer-limits_13.yaml");
  }

  @Test
  public void issue458ExclusiveMaximumTrueRemoved() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_11.yaml", "issue-458-integer-limits_12.yaml");
  }

  @Test
  public void issue458ExclusiveMinimumTrueRemoved() {
    assertOpenApiChangedEndpoints(
        "issue-458-integer-limits_11.yaml", "issue-458-integer-limits_13.yaml");
  }

  @Test
  public void issue488RenameParameterAddAndRemoveParameterReturnFalse() {
    assertOpenApiChangedEndpoints("issue-488-1.json", "issue-488-2.json");
  }
}
