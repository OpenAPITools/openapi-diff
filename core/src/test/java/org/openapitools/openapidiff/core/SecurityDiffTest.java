package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.*;

/** Created by adarsh.sharma on 06/01/18. */
public class SecurityDiffTest {
  private final String OPENAPI_DOC1 = "security_diff_1.yaml";
  private final String OPENAPI_DOC2 = "security_diff_2.yaml";
  private final String OPENAPI_DOC3 = "security_diff_3.yaml";
  private final String OPENAPI_DOC4 = "security_diff_4.yaml";

  @Test
  public void testDiffDifferent() {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);

    assertThat(changedOpenApi.getChangedOperations().size() == 3);

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
}
