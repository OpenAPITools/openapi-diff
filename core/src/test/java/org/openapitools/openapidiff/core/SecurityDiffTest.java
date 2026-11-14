package org.openapitools.openapidiff.core;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.*;

public class SecurityDiffTest {
  private final String OPENAPI_DOC1 = "security_diff_1.yaml";
  private final String OPENAPI_DOC2 = "security_diff_2.yaml";
  private final String OPENAPI_DOC3 = "security_diff_3.yaml";
  private final String OPENAPI_DOC4 = "security_diff_4.yaml";
  private final String OPENAPI_DOC5 = "security_diff_5.yaml";

  @Test
  public void testDiffDifferent() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);

    assertThat(changedOpenApi.getChangedOperations()).hasSize(3);

    ChangedOperation changedOperation1 =
        changedOpenApi.getChangedOperations().stream()
            .filter(x -> x.getPathUrl().equals("/pet/{petId}"))
            .findFirst()
            .get();
    assertThat(changedOperation1).isNotNull();
    assertThat(changedOperation1.isCompatible()).isFalse();
    ChangedSecurityRequirements changedSecurityRequirements1 =
        changedOperation1.getSecurityRequirements();
    assertThat(changedSecurityRequirements1).isNotNull();
    assertThat(changedSecurityRequirements1.isCompatible()).isFalse();
    assertThat(changedSecurityRequirements1.getIncreased()).hasSize(1);
    assertThat(changedSecurityRequirements1.getChanged()).hasSize(1);
    ChangedSecurityRequirement changedSecurityRequirement1 =
        changedSecurityRequirements1.getChanged().get(0);
    assertThat(changedSecurityRequirement1.getChanged()).hasSize(1);
    ChangedSecuritySchemeScopes changedScopes1 =
        changedSecurityRequirement1.getChanged().get(0).getChangedScopes();
    assertThat(changedScopes1)
        .isNotNull()
        .satisfies(
            stringListDiff ->
                assertThat(stringListDiff.getIncreased())
                    .hasSize(1)
                    .first()
                    .asString()
                    .isEqualTo("read:pets"));

    ChangedOperation changedOperation2 =
        changedOpenApi.getChangedOperations().stream()
            .filter(x -> x.getPathUrl().equals("/pet3"))
            .findFirst()
            .get();
    assertThat(changedOperation2).isNotNull();
    assertThat(changedOperation2.isCompatible()).isFalse();
    ChangedSecurityRequirements changedSecurityRequirements2 =
        changedOperation2.getSecurityRequirements();
    assertThat(changedSecurityRequirements2).isNotNull();
    assertThat(changedSecurityRequirements2.isCompatible()).isFalse();
    assertThat(changedSecurityRequirements2.getChanged()).hasSize(1);
    ChangedSecurityRequirement changedSecurityRequirement2 =
        changedSecurityRequirements2.getChanged().get(0);
    assertThat(changedSecurityRequirement2.getChanged()).hasSize(1);
    ChangedOAuthFlow changedImplicitOAuthFlow2 =
        changedSecurityRequirement2.getChanged().get(0).getOAuthFlows().getImplicitOAuthFlow();
    assertThat(changedImplicitOAuthFlow2).isNotNull();
    assertThat(changedImplicitOAuthFlow2.isAuthorizationUrl()).isTrue();

    ChangedOperation changedOperation3 =
        changedOpenApi.getChangedOperations().stream()
            .filter(x -> x.getPathUrl().equals("/pet/findByStatus2"))
            .findFirst()
            .get();
    assertThat(changedOperation3).isNotNull();
    assertThat(changedOperation3.isCompatible()).isTrue();
    ChangedSecurityRequirements changedSecurityRequirements3 =
        changedOperation3.getSecurityRequirements();
    assertThat(changedSecurityRequirements3).isNotNull();
    assertThat(changedSecurityRequirements3.getIncreased()).hasSize(1);
    SecurityRequirement securityRequirement3 = changedSecurityRequirements3.getIncreased().get(0);
    assertThat(securityRequirement3)
        .hasSize(1)
        .hasEntrySatisfying("petstore_auth", values -> assertThat(values).hasSize(2));
  }

  @Test
  public void testWithUnknownSecurityScheme() {
    assertThrows(
        IllegalArgumentException.class,
        () -> OpenApiCompare.fromLocations(OPENAPI_DOC3, OPENAPI_DOC3));
    assertThrows(
        IllegalArgumentException.class,
        () -> OpenApiCompare.fromLocations(OPENAPI_DOC4, OPENAPI_DOC4));
  }

  @Test
  public void testMissingSecurityDefinition() {
    assertThrows(
        IllegalArgumentException.class,
        () -> OpenApiCompare.fromLocations(OPENAPI_DOC5, OPENAPI_DOC5));
  }

  /** Case 1: the component key is renamed but the apiKey parameter name is unchanged. */
  @Test
  public void testApiKeyKeyRenamedNameSame() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "issue-802-api-key-key-renamed-1.yaml", "issue-802-api-key-key-renamed-2.yaml");

    assertThat(changedOpenApi.isUnchanged()).isTrue();
    assertThat(changedOpenApi.getChangedOperations()).isEmpty();
  }

  /**
   * Case 2 (#802): same component key, the apiKey parameter name changed. The name change is
   * reported on the scheme inside the matched requirement.
   */
  @Test
  public void testApiKeyNameChanged() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "issue-802-api-key-name-changed-1.yaml", "issue-802-api-key-name-changed-2.yaml");

    assertThat(changedOpenApi.getChangedOperations()).hasSize(1);
    assertThat(changedOpenApi.isIncompatible()).isTrue();

    ChangedSecurityRequirements changedSecurityRequirements =
        changedOpenApi.getChangedOperations().get(0).getSecurityRequirements();
    assertThat(changedSecurityRequirements.getIncreased()).isNull();
    assertThat(changedSecurityRequirements.getMissing()).isNull();
    assertThat(changedSecurityRequirements.getChanged()).hasSize(1);

    ChangedSecurityRequirement changedSecurityRequirement =
        changedSecurityRequirements.getChanged().get(0);
    assertThat(changedSecurityRequirement.getIncreased()).isNull();
    assertThat(changedSecurityRequirement.getMissing()).isNull();
    assertThat(changedSecurityRequirement.getChanged()).hasSize(1);

    ChangedSecurityScheme changedSecurityScheme = changedSecurityRequirement.getChanged().get(0);
    assertThat(changedSecurityScheme.isChangedName()).isTrue();
    assertThat(changedSecurityScheme.getOldSecurityScheme().getName()).isEqualTo("Authorization");
    assertThat(changedSecurityScheme.getNewSecurityScheme().getName()).isEqualTo("pat");
  }

  /**
   * Case 3: both the component key and the parameter name changed. The scheme is reported as
   * removed and added, not as a rename.
   */
  @Test
  public void testApiKeyKeyAndNameChanged() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "issue-802-api-key-key-and-name-changed-1.yaml",
            "issue-802-api-key-key-and-name-changed-2.yaml");

    assertThat(changedOpenApi.getChangedOperations()).hasSize(1);
    assertThat(changedOpenApi.isIncompatible()).isTrue();

    ChangedSecurityRequirements changedSecurityRequirements =
        changedOpenApi.getChangedOperations().get(0).getSecurityRequirements();
    assertThat(changedSecurityRequirements.getChanged()).hasSize(1);

    ChangedSecurityRequirement changedSecurityRequirement =
        changedSecurityRequirements.getChanged().get(0);
    assertThat(changedSecurityRequirement.getMissing())
        .hasSize(1)
        .containsKey("PAT")
        .containsValue(emptyList());
    assertThat(changedSecurityRequirement.getIncreased())
        .hasSize(1)
        .containsKey("PAT2")
        .containsValue(emptyList());
    // no rename is guessed
    assertThat(changedSecurityRequirement.getChanged()).isEmpty();
  }

  /** Case 4: a new alternative security requirement is added, which is compatible. */
  @Test
  public void testAlternativeSecurityRequirementAdded() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "issue-802-security-requirement-added-1.yaml",
            "issue-802-security-requirement-added-2.yaml");

    assertThat(changedOpenApi.getChangedOperations()).hasSize(1);
    assertThat(changedOpenApi.isCompatible()).isTrue();
    assertThat(changedOpenApi.isIncompatible()).isFalse();

    ChangedSecurityRequirements changedSecurityRequirements =
        changedOpenApi.getChangedOperations().get(0).getSecurityRequirements();
    assertThat(changedSecurityRequirements.getIncreased())
        .hasSize(1)
        .first()
        .satisfies(requirement -> assertThat(requirement).containsKey("petstore_auth"));
    assertThat(changedSecurityRequirements.getMissing()).isNull();
  }

  /**
   * Several apiKey schemes in one requirement are paired by component key first, so a name swap
   * between two schemes is reported on both schemes instead of pairing each scheme with the other
   * one by name.
   */
  @Test
  public void testSeveralApiKeysMatchedByComponentKeyFirst() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "issue-802-api-key-two-schemes-names-swapped-1.yaml",
            "issue-802-api-key-two-schemes-names-swapped-2.yaml");

    assertThat(changedOpenApi.getChangedOperations()).hasSize(1);
    assertThat(changedOpenApi.isIncompatible()).isTrue();

    ChangedSecurityRequirement changedSecurityRequirement =
        changedOpenApi.getChangedOperations().get(0).getSecurityRequirements().getChanged().get(0);
    assertThat(changedSecurityRequirement.getChanged()).hasSize(2);
    assertThat(changedSecurityRequirement.getChanged())
        .extracting(
            scheme -> scheme.getOldSecurityScheme().getName(),
            scheme -> scheme.getNewSecurityScheme().getName())
        .containsExactlyInAnyOrder(
            tuple("Authorization", "X-Key"), tuple("X-Key", "Authorization"));
  }

  @Test
  public void testApiKeySchemeNameChangeWithinMatchedRequirement() {
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations(
            "bc_security_scheme_base.yaml", "bc_security_scheme_api_key_name_changed.yaml");

    assertThat(changedOpenApi.getChangedOperations()).hasSize(1);
    ChangedSecurityRequirements changedSecurityRequirements =
        changedOpenApi.getChangedOperations().get(0).getSecurityRequirements();
    assertThat(changedSecurityRequirements.getChanged()).hasSize(1);

    ChangedSecurityRequirement changedSecurityRequirement =
        changedSecurityRequirements.getChanged().get(0);
    assertThat(changedSecurityRequirement.getChanged()).hasSize(1);

    ChangedSecurityScheme changedSecurityScheme = changedSecurityRequirement.getChanged().get(0);
    assertThat(changedSecurityScheme.isChangedName()).isTrue();
    assertThat(changedSecurityScheme.getOldSecurityScheme().getName()).isEqualTo("api_key");
    assertThat(changedSecurityScheme.getNewSecurityScheme().getName()).isEqualTo("api_key_renamed");
  }
}
