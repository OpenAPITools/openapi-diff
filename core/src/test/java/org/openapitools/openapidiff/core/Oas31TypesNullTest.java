package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.model.DiffResult.COMPATIBLE;
import static org.openapitools.openapidiff.core.model.DiffResult.INCOMPATIBLE;
import static org.openapitools.openapidiff.core.model.DiffResult.NO_CHANGES;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openapitools.openapidiff.core.model.DiffResult;

/**
 * Area 1 of Phase 2 of the OpenAPI 3.1 plan (issue #950): the engine reads the type keyword from
 * the types set and routes "null" through the null rule.
 *
 * <p>Fixtures follow the paired layout of the Phase 0 harness (#961): every rule directory {@code
 * core/src/test/resources/oas31/<rule>/} holds the same minimal document as old-3.0.yaml/
 * new-3.0.yaml and old-3.1.yaml/new-3.1.yaml. The type-change and nullable pairs are the same files
 * the harness carries; the Phase 2 area-1 fix un-disables their same-version comparisons there.
 *
 * <p>Acceptance: a 3.1 document gives the same verdict as the same change written in 3.0, and the
 * same spec written in 3.0 and in 3.1 diffs to no changes. Cross-version comparisons (3.0 old
 * against 3.1 new) stay with Phase 3 (#951).
 */
class Oas31TypesNullTest {

  /** Verdict the engine gives for the rule pair when both documents are 3.0. */
  private static final Map<String, DiffResult> VERDICT_30 =
      Map.of(
          "null-removal", COMPATIBLE,
          "nullable", COMPATIBLE,
          "type-change", INCOMPATIBLE,
          "type-multi", NO_CHANGES);

  private static final List<String> RULES =
      List.of("null-removal", "nullable", "type-change", "type-multi");

  /**
   * Rules whose 3.0 and 3.1 spellings encode the same document. The multi-type keyword has no 3.0
   * spelling, so for type-multi the two versions are different specs and the written-versions
   * equivalence does not apply.
   */
  private static final List<String> WRITTEN_EQUIVALENCE_RULES =
      List.of("null-removal", "nullable", "type-change");

  static Stream<String> rules() {
    return RULES.stream();
  }

  static Stream<String> writtenEquivalenceRules() {
    return WRITTEN_EQUIVALENCE_RULES.stream();
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

  /** The 3.0->3.0 comparison detects the change with the documented verdict. */
  @ParameterizedTest(name = "{0}: 3.0->3.0 keeps its documented verdict")
  @MethodSource("rules")
  void changeDetectedIn30WithDocumentedVerdict(String rule) {
    assertThat(verdict30to30(rule)).isEqualTo(VERDICT_30.get(rule));
  }

  /** The 3.1->3.1 comparison of the same change agrees with the 3.0 verdict. */
  @ParameterizedTest(name = "{0}: 3.1->3.1 agrees with the 3.0 verdict")
  @MethodSource("rules")
  void sameVerdictIn31AsIn30(String rule) {
    assertThat(verdict31to31(rule)).isEqualTo(verdict30to30(rule));
  }

  /** The same document written in 3.0 and in 3.1 diffs to no changes. */
  @ParameterizedTest(name = "{0}: the 3.0 and 3.1 spellings agree")
  @MethodSource("writtenEquivalenceRules")
  @DisplayName("written versions agree")
  void writtenVersionsAgree(String rule) {
    assertThat(verdict("oas31/" + rule + "/old-3.0.yaml", "oas31/" + rule + "/old-3.1.yaml"))
        .isEqualTo(NO_CHANGES);
    assertThat(verdict("oas31/" + rule + "/new-3.0.yaml", "oas31/" + rule + "/new-3.1.yaml"))
        .isEqualTo(NO_CHANGES);
  }
}
