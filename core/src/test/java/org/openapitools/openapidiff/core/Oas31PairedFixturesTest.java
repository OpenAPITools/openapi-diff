package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.model.DiffResult.COMPATIBLE;
import static org.openapitools.openapidiff.core.model.DiffResult.INCOMPATIBLE;
import static org.openapitools.openapidiff.core.model.DiffResult.NO_CHANGES;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openapitools.openapidiff.core.model.DiffResult;

/**
 * Phase 0 test harness for the OpenAPI 3.1 plan, see issue #948.
 *
 * <p>Fixture convention: every rule directory {@code core/src/test/resources/oas31/<rule>/} holds
 * the same minimal document as old-3.0.yaml/new-3.0.yaml and old-3.1.yaml/new-3.1.yaml, so the same
 * change is expressed twice: once in 3.0 syntax and once in 3.1 syntax. The harness contract is
 * that the same change gets the same verdict in every version, and that the same document written
 * in 3.0 and in 3.1 diffs to no changes.
 *
 * <p>Comparisons that fail on the current engine are not deleted: they are kept as disabled tests,
 * each naming the phase of the plan that fixes them, so the build stays green while the gaps remain
 * visible. When a phase lands, remove its {@code @Disabled} annotations. New rule fixtures follow
 * the same pattern: add the directory, add the expected 3.0 verdict below, and disable the 3.1
 * comparisons if they do not hold yet.
 */
class Oas31PairedFixturesTest {

  /** Verdict the engine gives for the rule pair when both documents are 3.0. */
  private static final Map<String, DiffResult> VERDICT_30 =
      Map.of(
          "all-of", INCOMPATIBLE,
          "array-items", INCOMPATIBLE,
          "exclusive-bounds", INCOMPATIBLE,
          "nullable", COMPATIBLE,
          "one-of", INCOMPATIBLE,
          "type-change", INCOMPATIBLE);

  private static final List<String> RULES =
      List.of("all-of", "array-items", "exclusive-bounds", "nullable", "one-of", "type-change");

  static Stream<String> rules() {
    return RULES.stream();
  }

  private static DiffResult verdict(String oldSpec, String newSpec) {
    return OpenApiCompare.fromLocations(oldSpec, newSpec).isChanged();
  }

  private static DiffResult verdict30to30(String rule) {
    return verdict("oas31/" + rule + "/old-3.0.yaml", "oas31/" + rule + "/new-3.0.yaml");
  }

  private static DiffResult verdict31to31(String rule) {
    return verdict("oas31/" + rule + "/old-3.1.yaml", "oas31/" + rule + "/new-3.1.yaml");
  }

  private static DiffResult verdict30to31(String rule) {
    return verdict("oas31/" + rule + "/old-3.0.yaml", "oas31/" + rule + "/new-3.1.yaml");
  }

  /** The 3.0->3.0 comparison detects the change with the documented verdict. */
  @ParameterizedTest(name = "{0}: 3.0->3.0 keeps its documented verdict")
  @MethodSource("rules")
  void changeDetectedIn30WithDocumentedVerdict(String rule) {
    assertThat(verdict30to30(rule)).isEqualTo(VERDICT_30.get(rule));
  }

  /** The Swagger 2.0 pair encodes the type-change rule and matches the 3.0 verdict. */
  @Test
  void swagger2PairMatchesThe30Verdict() {
    assertThat(verdict("oas31/swagger2/old-2.0.yaml", "oas31/swagger2/new-2.0.yaml"))
        .isEqualTo(VERDICT_30.get("type-change"));
  }

  /** The 3.1->3.1 comparison of the same change agrees with the 3.0 verdict. */
  private static void assertSameVerdictIn30And31(String rule) {
    assertThat(verdict31to31(rule)).isEqualTo(verdict30to30(rule));
  }

  /** The 3.0->3.1 comparison of the same change agrees with the 3.0 verdict. */
  private static void assertCrossVersionMatches30(String rule) {
    assertThat(verdict30to31(rule)).isEqualTo(verdict30to30(rule));
  }

  /** The same document written in 3.0 and in 3.1 diffs to no changes. */
  private static void assertWrittenVersionsAgree(String rule) {
    assertThat(verdict("oas31/" + rule + "/old-3.0.yaml", "oas31/" + rule + "/old-3.1.yaml"))
        .isEqualTo(NO_CHANGES);
    assertThat(verdict("oas31/" + rule + "/new-3.0.yaml", "oas31/" + rule + "/new-3.1.yaml"))
        .isEqualTo(NO_CHANGES);
  }

  /**
   * Known 3.1 gaps of the current engine, kept as disabled tests so they are visible and green.
   * Every test names the phase of the plan that fixes it; remove the {@code @Disabled} annotation
   * when that phase lands.
   */
  @Nested
  @DisplayName("Known 3.1 gaps")
  class KnownGaps {

    @Nested
    @DisplayName("Same-version verdicts: Phase 2, engine reads 3.1 fields (#950)")
    class SameVersion31 {

      @Test
      @Disabled(
          "#950 (Phase 2): the engine compares getType(), which is null for 3.1 schemas; the"
              + " type keyword lives in the types set (see also #938)")
      void typeChange() {
        assertSameVerdictIn30And31("type-change");
      }

      @Test
      @Disabled(
          "#950 (Phase 2): 3.1 nullability is \"null\" in the types set and only goes through"
              + " the null rule, which is not implemented yet")
      void nullable() {
        assertSameVerdictIn30And31("nullable");
      }

      @Test
      @Disabled(
          "#950 (Phase 2): the 3.1 numeric exclusiveMinimum is not read; bounds need the"
              + " effective-bound rule (value plus inclusive/exclusive)")
      void exclusiveBounds() {
        assertSameVerdictIn30And31("exclusive-bounds");
      }

      @Test
      @Disabled(
          "#950 (Phase 2): items are compared only on ArraySchema, but 3.1 arrays deserialize"
              + " as JsonSchema")
      void arrayItems() {
        assertSameVerdictIn30And31("array-items");
      }

      @Test
      @Disabled(
          "#950 (Phase 2): allOf is flattened only on ComposedSchema, but 3.1 compositions"
              + " deserialize as JsonSchema (see also #938)")
      void allOf() {
        assertSameVerdictIn30And31("all-of");
      }

      @Test
      @Disabled(
          "#950 (Phase 2): oneOf members are compared only on ComposedSchema, but 3.1"
              + " compositions deserialize as JsonSchema")
      void oneOf() {
        assertSameVerdictIn30And31("one-of");
      }
    }

    @Nested
    @DisplayName("Cross-version verdicts: Phase 3, converter wired in (#951)")
    class CrossVersion {

      @ParameterizedTest(name = "{0}: 3.0->3.1 agrees with the 3.0 verdict")
      @MethodSource("org.openapitools.openapidiff.core.Oas31PairedFixturesTest#rules")
      @Disabled(
          "#951 (Phase 3): 3.0 documents are not converted to the 3.1 shape before comparing,"
              + " so mixed-version comparisons report a false type change")
      void crossVersionMatches30(String rule) {
        assertCrossVersionMatches30(rule);
      }

      @ParameterizedTest(name = "{0}: the 3.0 and 3.1 spellings agree")
      @MethodSource("org.openapitools.openapidiff.core.Oas31PairedFixturesTest#rules")
      @Disabled(
          "#951 (Phase 3): the same document written in 3.0 and in 3.1 currently reports"
              + " changes (version noise, see #858)")
      void writtenVersionsAgree(String rule) {
        assertWrittenVersionsAgree(rule);
      }
    }
  }
}
