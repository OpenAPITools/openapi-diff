package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.ChangedParameter;
import io.swagger.oas.models.media.Schema;
import io.swagger.oas.models.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * compare two parameter
 *
 * @author QDesrame
 */
public class ParameterDiff {

    private List<Parameter> increased;
    private List<Parameter> missing;
    private List<ChangedParameter> changed;

    Map<String, Schema> oldDefinitions;
    Map<String, Schema> newDefinitions;

    private ParameterDiff() {
    }

    public static ParameterDiff buildWithDefinition(Map<String, Schema> left,
                                                    Map<String, Schema> right) {
        ParameterDiff diff = new ParameterDiff();
        diff.oldDefinitions = left;
        diff.newDefinitions = right;
        return diff;
    }

    public ParameterDiff diff(List<Parameter> left,
                              List<Parameter> right) {
        ParameterDiff instance = new ParameterDiff();
        if (null == left) left = new ArrayList<Parameter>();
        if (null == right) right = new ArrayList<Parameter>();

        instance.increased = new ArrayList<Parameter>(right);
        instance.missing = new ArrayList<Parameter>();
        instance.changed = new ArrayList<ChangedParameter>();
        for (Parameter leftPara : left) {
            String name = leftPara.getName();
            Optional<Parameter> rightParam = contains(right, leftPara);
            if (!rightParam.isPresent()) {
                instance.missing.add(leftPara);
            } else {
                Parameter rightPara = rightParam.get();

                ChangedParameter changedParameter = new ChangedParameter();
                changedParameter.setLeftParameter(leftPara);
                changedParameter.setRightParameter(rightPara);

                //is required
                boolean rightRequired = rightPara.getRequired();
                boolean leftRequired = leftPara.getRequired();
                changedParameter.setChangeRequired(leftRequired != rightRequired);

                //description
                String description = rightPara.getDescription();
                String oldPescription = leftPara.getDescription();
                if (StringUtils.isBlank(description)) description = "";
                if (StringUtils.isBlank(oldPescription)) oldPescription = "";
                changedParameter.setChangeDescription(!description.equals(oldPescription));

                if (changedParameter.isDiff()) {
                    instance.changed.add(changedParameter);
                }

            }

        }
        return instance;
    }

    private static int index(List<Parameter> right, String name) {
        int i = 0;
        for (; i < right.size(); i++) {
            Parameter para = right.get(i);
            if (name.equals(para.getName())) {
                return i;
            }
        }
        return -1;
    }

    public List<Parameter> getIncreased() {
        return increased;
    }

    public void setIncreased(List<Parameter> increased) {
        this.increased = increased;
    }

    public List<Parameter> getMissing() {
        return missing;
    }

    public void setMissing(List<Parameter> missing) {
        this.missing = missing;
    }

    public List<ChangedParameter> getChanged() {
        return changed;
    }

    public void setChanged(List<ChangedParameter> changed) {
        this.changed = changed;
    }

    public static boolean same(Parameter left, Parameter right) {
        return Objects.equals(left.getName(), right.getName()) && Objects.equals(left.getIn(), right.getIn());
    }

    public static Optional<Parameter> contains(List<Parameter> parameters, Parameter parameter) {
        return parameters.stream().filter(param -> same(param, parameter)).findFirst();
    }
}
