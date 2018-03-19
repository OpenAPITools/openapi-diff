package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedSecurityRequirement;
import com.qdesrame.openapi.diff.model.ChangedSecurityRequirements;
import com.qdesrame.openapi.diff.utils.RefPointer;
import com.qdesrame.openapi.diff.utils.RefType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by adarsh.sharma on 07/01/18.
 */
public class SecurityRequirementsDiff {
    private OpenApiDiff openApiDiff;
    private Components leftComponents;
    private Components rightComponents;
    private static RefPointer<SecurityScheme> refPointer = new RefPointer<>(RefType.SECURITY_SCHEMES);

    public SecurityRequirementsDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.leftComponents = openApiDiff.getOldSpecOpenApi() != null ? openApiDiff.getOldSpecOpenApi().getComponents() : null;
        this.rightComponents = openApiDiff.getNewSpecOpenApi() != null ? openApiDiff.getNewSpecOpenApi().getComponents() : null;
    }

    public Optional<SecurityRequirement> contains(List<SecurityRequirement> securityRequirements,
                                                  SecurityRequirement left) {
        return securityRequirements.stream()
                .filter(rightSecurities -> same(left, rightSecurities))
                .findFirst();
    }


    public boolean same(SecurityRequirement left, SecurityRequirement right) {
//        List<SecurityScheme.Type> leftTypes = left.keySet().stream()
//                .map(x -> leftComponents.getSecuritySchemes().get(x).getType())
//                .collect(Collectors.toList());
//        List<SecurityScheme.Type> rightTypes = right.keySet().stream()
//                .map(x -> rightComponents.getSecuritySchemes().get(x).getType())
//                .collect(Collectors.toList());
//
        List<Pair<SecurityScheme.Type, SecurityScheme.In>> leftTypes = getListOfSecuritySchemes(leftComponents, left);
        List<Pair<SecurityScheme.Type, SecurityScheme.In>> rightTypes = getListOfSecuritySchemes(rightComponents, right);

        return CollectionUtils.isEqualCollection(leftTypes, rightTypes);

    }

    private List<Pair<SecurityScheme.Type, SecurityScheme.In>> getListOfSecuritySchemes(Components components, SecurityRequirement securityRequirement) {
        return securityRequirement.keySet().stream()
                .map(x -> {
                    SecurityScheme result = components.getSecuritySchemes().get(x);
                    if (result == null) {
                        throw new IllegalArgumentException("Impossible to find security scheme: " + x);
                    }
                    return result;
                })
                .map(this::getPair)
                .distinct()
                .collect(Collectors.toList());
    }

    private Pair<SecurityScheme.Type, SecurityScheme.In> getPair(SecurityScheme securityScheme) {
        return new ImmutablePair<>(securityScheme.getType(), securityScheme.getIn());
    }

    protected Optional<ChangedSecurityRequirements> diff(List<SecurityRequirement> left, List<SecurityRequirement> right) {
        left = left == null ? new ArrayList<>() : left;
        right = right == null ? new ArrayList<>() : getCopy(right);

        ChangedSecurityRequirements changedSecurityRequirements = new ChangedSecurityRequirements(left, right);


        for (SecurityRequirement leftSecurity : left) {
            Optional<SecurityRequirement> rightSecOpt = contains(right, leftSecurity);
            if (!rightSecOpt.isPresent()) {
                changedSecurityRequirements.addMissing(leftSecurity);
            } else {
                SecurityRequirement rightSec = rightSecOpt.get();
                right.remove(rightSec);
                Optional<ChangedSecurityRequirement> diff = openApiDiff.getSecurityRequirementDiff().diff(leftSecurity, rightSec);
                diff.ifPresent(changedSecurityRequirements::addChanged);
            }
        }
        right.forEach(changedSecurityRequirements::addIncreased);

        return changedSecurityRequirements.isDiff() ? Optional.of(changedSecurityRequirements) : Optional.empty();
    }

    private List<SecurityRequirement> getCopy(List<SecurityRequirement> right) {
        return right.stream()
                .map(SecurityRequirementDiff::getCopy)
                .collect(Collectors.toList());
    }

//    public static void main(String[] args) {
//        List<SecurityScheme.Type> types1 = new ArrayList<>(Arrays.asList(SecurityScheme.Type.APIKEY, SecurityScheme.Type.APIKEY));
//        List<SecurityScheme.Type> types2 = new ArrayList<>(Arrays.asList(SecurityScheme.Type.APIKEY, SecurityScheme.Type.APIKEY));
//        List<SecurityScheme.Type> types3 = new ArrayList<>(Arrays.asList(SecurityScheme.Type.OAUTH2));
//        List<SecurityScheme.Type> types4 = new ArrayList<>(Arrays.asList(SecurityScheme.Type.OAUTH2, SecurityScheme.Type.APIKEY));
//        List<SecurityScheme.Type> types5 = new ArrayList<>(Arrays.asList(SecurityScheme.Type.APIKEY, SecurityScheme.Type.OAUTH2));
//
//        List<Pair<SecurityScheme.Type, SecurityScheme.In>> one = new ArrayList<>();
//        one.add(new ImmutablePair<>(SecurityScheme.Type.OAUTH2, null));
//        one.add(new ImmutablePair<>(SecurityScheme.Type.APIKEY, SecurityScheme.In.HEADER));
//
//        List<Pair<SecurityScheme.Type, SecurityScheme.In>> two = new ArrayList<>();
//        two.add(new ImmutablePair<>(SecurityScheme.Type.APIKEY, SecurityScheme.In.HEADER));
//        two.add(new ImmutablePair<>(SecurityScheme.Type.OAUTH2, null));
//
//        System.out.println(CollectionUtils.isEqualCollection(types1, types2));
//        System.out.println(CollectionUtils.isEqualCollection(types1, types3));
//        System.out.println(CollectionUtils.isEqualCollection(types4, types5));
//        System.out.println(CollectionUtils.isEqualCollection(one, two));
//    }

//    private List<List<SecurityDiffInfo>> getSecurityInfo(Components components, List<SecurityRequirement> securityRequirements) {
//        List<List<SecurityDiffInfo>> securityInfoList = new ArrayList<>();
//
//        for (LinkedHashMap<String, List<String>> securityRequirement : securityRequirements) {
//            securityInfoList.add(securityRequirement.entrySet().stream()
//                    .map(e -> new SecurityDiffInfo(e.getKey(), refPointer.resolveRef(components, new SecurityScheme(), e.getKey()), e.getValue()))
//                    .collect(Collectors.toList()));
//        }
//
//        return securityInfoList;
//    }

}
