package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adarsh.sharma on 06/01/18.
 */
@Getter
@Setter
public class ChangedSecurityRequirements implements Changed {
    private List<SecurityRequirement> oldSecurityRequirements;
    private List<SecurityRequirement> newSecurityRequirements;

    private List<SecurityRequirement> missing;
    private List<SecurityRequirement> increased;
    private List<ChangedSecurityRequirement> changed;

    public ChangedSecurityRequirements(List<SecurityRequirement> oldSecurityRequirements,
                                       List<SecurityRequirement> newSecurityRequirements) {
        this.oldSecurityRequirements = oldSecurityRequirements;
        this.newSecurityRequirements = newSecurityRequirements;
    }

    @Override
    public boolean isDiff() {
        return (missing != null && !missing.isEmpty()) ||
                (increased != null && !increased.isEmpty()) ||
                (changed != null && changed.stream().anyMatch(x -> x.isDiff()));
    }

    @Override
    public boolean isDiffBackwardCompatible() {
        return (missing == null || missing.isEmpty()) &&
                (changed == null || changed.stream().allMatch(x -> x.isDiffBackwardCompatible()));
    }

    public void addMissing(SecurityRequirement securityRequirement) {
        if(this.missing == null){
            this.missing = new ArrayList<>();
        }
        this.missing.add(securityRequirement);
    }

    public void addIncreased(SecurityRequirement securityRequirement) {
        if(this.increased == null){
            this.increased = new ArrayList<>();
        }
        this.increased.add(securityRequirement);
    }

    public void addChanged(ChangedSecurityRequirement changedSecurityRequirement) {
        if(this.changed == null){
            this.changed = new ArrayList<>();
        }
        this.changed.add(changedSecurityRequirement);
    }
}
