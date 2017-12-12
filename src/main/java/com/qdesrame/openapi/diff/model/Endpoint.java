package com.qdesrame.openapi.diff.model;


import io.swagger.oas.models.Operation;
import io.swagger.oas.models.PathItem;

public class Endpoint {

    private String pathUrl;
    private PathItem.HttpMethod method;
    private String summary;

    private PathItem path;
    private Operation operation;

    public String getPathUrl() {
        return pathUrl;
    }

    public void setPathUrl(String pathUrl) {
        this.pathUrl = pathUrl;
    }

    public PathItem.HttpMethod getMethod() {
        return method;
    }

    public void setMethod(PathItem.HttpMethod method) {
        this.method = method;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public PathItem getPath() {
        return path;
    }

    public void setPath(PathItem path) {
        this.path = path;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

	@Override
    public String toString() {
        return method + " " + pathUrl;
    }
}
