package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adarsh.sharma on 06/01/18.
 */
@Getter
@Setter
public class ChangedSecurityRequirement implements Changed {
    private SecurityRequirement oldSecurityRequirement;
    private SecurityRequirement newSecurityRequirement;

    private SecurityRequirement missing;
    private SecurityRequirement increased;
    private List<ChangedSecurityScheme> changed;

    public ChangedSecurityRequirement(SecurityRequirement oldSecurityRequirement, SecurityRequirement newSecurityRequirement) {
        this.oldSecurityRequirement = oldSecurityRequirement;
        this.newSecurityRequirement = newSecurityRequirement;
    }

    @Override
    public DiffResult isChanged() {
        if (missing == null && increased == null && CollectionUtils.isEmpty(changed)) {
            return DiffResult.NO_CHANGES;
        }
        if (increased == null && (changed == null || changed.stream().allMatch(Changed::isCompatible))) {
            return DiffResult.COMPATIBLE;
        }
        return DiffResult.INCOMPATIBLE;
    }

    public void addMissing(String key, List<String> scopes) {
        if (missing == null) {
            missing = new SecurityRequirement();
        }
        missing.put(key, scopes);
    }

    public void addIncreased(String key, List<String> scopes) {
        if (increased == null) {
            increased = new SecurityRequirement();
        }
        increased.put(key, scopes);
    }

    public void addChanged(ChangedSecurityScheme changedSecurityScheme) {
        if (changed == null) {
            changed = new ArrayList<>();
        }
        changed.add(changedSecurityScheme);
    }
}
