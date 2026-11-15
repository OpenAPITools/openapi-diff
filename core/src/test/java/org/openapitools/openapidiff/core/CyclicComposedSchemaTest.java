package org.openapitools.openapidiff.core;

import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiBackwardIncompatible;
import static org.openapitools.openapidiff.core.TestUtils.assertSpecUnchanged;

import org.junit.jupiter.api.Test;

public class CyclicComposedSchemaTest {
  private final String CYCLIC_OLD = "cyclic_composed_1.yaml";
  private final String CYCLIC_NEW = "cyclic_composed_2.yaml";
  private final String DEEP_OLD = "cyclic_composed_deep_1.yaml";
  private final String DEEP_NEW = "cyclic_composed_deep_2.yaml";

  @Test
  public void testCyclicComposedSchemaAgainstItself() {
    assertOpenApiAreEquals(CYCLIC_OLD, CYCLIC_OLD);
  }

  @Test
  public void testChangeInsideCyclicComposedSchema() {
    assertOpenApiBackwardIncompatible(CYCLIC_OLD, CYCLIC_NEW);
  }

  @Test
  public void testDeepAcyclicComposedChainUnchanged() {
    assertSpecUnchanged(DEEP_OLD, DEEP_OLD);
  }

  @Test
  public void testChangeAtBottomOfDeepAcyclicComposedChain() {
    assertOpenApiBackwardIncompatible(DEEP_OLD, DEEP_NEW);
  }
}
