package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.compare.ParameterDiffResult;
import io.swagger.oas.models.parameters.Parameter;
import io.swagger.oas.models.responses.ApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChangedOperation implements Changed {

    private String summary;

    private List<Parameter> addParameters = new ArrayList<Parameter>();
    private List<Parameter> missingParameters = new ArrayList<Parameter>();

    private List<ParameterDiffResult> changedParameter = new ArrayList<>();

    private List<ElSchema> addProps = new ArrayList<ElSchema>();
    private List<ElSchema> missingProps = new ArrayList<ElSchema>();
    private Map<String, ApiResponse> missingResponses;
    private Map<String, ApiResponse> addResponses;
    private Map<String, ChangedResponse> changedResponses;
    private boolean deprecated;

    public List<Parameter> getAddParameters() {
        return addParameters;
    }

    public void setAddParameters(List<Parameter> addParameters) {
        this.addParameters = addParameters;
    }

    public List<Parameter> getMissingParameters() {
        return missingParameters;
    }

    public void setMissingParameters(List<Parameter> missingParameters) {
        this.missingParameters = missingParameters;
    }

    public List<ParameterDiffResult> getChangedParameter() {
        return changedParameter;
    }

    public void setChangedParameter(List<ParameterDiffResult> changedParameter) {
        this.changedParameter = changedParameter;
    }

    public List<ElSchema> getAddProps() {
        return addProps;
    }

    public void setAddProps(List<ElSchema> addProps) {
        this.addProps = addProps;
    }

    public List<ElSchema> getMissingProps() {
        return missingProps;
    }

    public void setMissingProps(List<ElSchema> missingProps) {
        this.missingProps = missingProps;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public boolean isDiff() {
        return isDiffProp() || isDiffParam() || isDiffResponse();
    }

    public boolean isDiffProp() {
        return !addProps.isEmpty()
                || !missingProps.isEmpty();
    }

    public boolean isDiffParam() {
        return !addParameters.isEmpty() || !missingParameters.isEmpty()
                || !changedParameter.isEmpty();
    }

    public boolean isDiffResponse() {
        return !addResponses.isEmpty() || !missingResponses.isEmpty() || !changedResponses.isEmpty();
    }

    public void setAddResponses(Map<String, ApiResponse> addResponses) {
        this.addResponses = addResponses;
    }

    public void setMissingResponses(Map<String, ApiResponse> missingResponses) {
        this.missingResponses = missingResponses;
    }

    public Map<String, ChangedResponse> getChangedResponses() {
        return changedResponses;
    }

    public ChangedOperation setChangedResponses(Map<String, ChangedResponse> changedResponses) {
        this.changedResponses = changedResponses;
        return this;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
}
