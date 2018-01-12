package com.qdesrame.openapi.test;

import com.qdesrame.openapi.diff.compare.OpenApiDiff;
import com.qdesrame.openapi.diff.model.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by adarsh.sharma on 06/01/18.
 */
public class SecurityDiffTest {
    private final String OPENAPI_DOC1 = "security_diff_1.yaml";
    private final String OPENAPI_DOC2 = "security_diff_2.yaml";


    @Test
    public void testDiffDifferent() {
        ChangedOpenApi changedOpenApi = OpenApiDiff.compare(OPENAPI_DOC1, OPENAPI_DOC2);

        assertTrue(changedOpenApi.getChangedOperations().size() == 3);

        ChangedOperation changedOperation1 = changedOpenApi.getChangedOperations().stream().filter(x -> x.getPathUrl().equals("/pet/{petId}")).findFirst().get();
        assertNotNull(changedOperation1);
        assertFalse(changedOperation1.isDiffBackwardCompatible());
        ChangedSecurityRequirements changedSecurityRequirements1 = changedOperation1.getChangedSecurityRequirements();
        assertNotNull(changedSecurityRequirements1);
        assertFalse(changedSecurityRequirements1.isDiffBackwardCompatible());
        assertTrue(changedSecurityRequirements1.getIncreased().size() == 1);
        assertTrue(changedSecurityRequirements1.getChanged().size() == 1);
        ChangedSecurityRequirement changedSecurityRequirement1 = changedSecurityRequirements1.getChanged().get(0);
        assertTrue(changedSecurityRequirement1.getChanged().size() == 1);
        ListDiff<String> changedScopes1 = changedSecurityRequirement1.getChanged().get(0).getChangedScopes();
        assertNotNull(changedScopes1);
        assertTrue(changedScopes1.getIncreased().size() == 1);
        assertTrue(changedScopes1.getIncreased().get(0).equals("read:pets"));

        ChangedOperation changedOperation2 = changedOpenApi.getChangedOperations().stream().filter(x -> x.getPathUrl().equals("/pet3")).findFirst().get();
        assertNotNull(changedOperation2);
        assertFalse(changedOperation2.isDiffBackwardCompatible());
        ChangedSecurityRequirements changedSecurityRequirements2 = changedOperation2.getChangedSecurityRequirements();
        assertNotNull(changedSecurityRequirements2);
        assertFalse(changedSecurityRequirements2.isDiffBackwardCompatible());
        assertTrue(changedSecurityRequirements2.getChanged().size() == 1);
        ChangedSecurityRequirement changedSecurityRequirement2 = changedSecurityRequirements2.getChanged().get(0);
        assertTrue(changedSecurityRequirement2.getChanged().size() == 1);
        ChangedOAuthFlow changedImplicitOAuthFlow2 = changedSecurityRequirement2.getChanged().get(0).getChangedOAuthFlows().getChangedImplicitOAuthFlow();
        assertNotNull(changedImplicitOAuthFlow2);
        assertTrue(changedImplicitOAuthFlow2.isChangedAuthorizationUrl());

        ChangedOperation changedOperation3 = changedOpenApi.getChangedOperations().stream().filter(x -> x.getPathUrl().equals("/pet/findByStatus2")).findFirst().get();
        assertNotNull(changedOperation3);
        assertTrue(changedOperation3.isDiffBackwardCompatible());
        ChangedSecurityRequirements changedSecurityRequirements3 = changedOperation3.getChangedSecurityRequirements();
        assertNotNull(changedSecurityRequirements3);
        assertTrue(changedSecurityRequirements3.getIncreased().size() == 1);
        SecurityRequirement securityRequirement3 = changedSecurityRequirements3.getIncreased().get(0);
        assertTrue(securityRequirement3.size() == 1);
        assertTrue(securityRequirement3.get("petstore_auth").size() == 2);
    }
}
