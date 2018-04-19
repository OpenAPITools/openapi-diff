package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.PathItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Quentin Desramé on 04/04/17.
 */
public class DiffContext {

    private String url;
    private Map<String, String> parameters;
    private PathItem.HttpMethod method;
    private boolean response;
    private boolean request;

    public DiffContext() {
        parameters = new HashMap<>();
        response = false;
        request = true;
    }

    public DiffContext copyWithMethod(PathItem.HttpMethod method) {
        return copy().setMethod(method);
    }

    public DiffContext copyAsRequest() {
        return copy().setRequest();
    }

    public DiffContext copyAsResponse() {
        return copy().setResponse();
    }

    private DiffContext setRequest() {
        this.request = true;
        this.response = false;
        return this;
    }

    private DiffContext setResponse() {
        this.response = true;
        this.request = false;
        return this;
    }

    public boolean isResponse() {
        return this.response;
    }

    public boolean isRequest() {
        return this.request;
    }

    public String getUrl() {
        return url;
    }

    public DiffContext setUrl(String url) {
        this.url = url;
        return this;
    }

    public PathItem.HttpMethod getMethod() {
        return method;
    }

    private DiffContext setMethod(PathItem.HttpMethod method) {
        this.method = method;
        return this;
    }

    private DiffContext copy() {
        DiffContext context = new DiffContext();
        context.url = this.url;
        context.parameters = this.parameters;
        context.method = this.method;
        context.response = this.response;
        context.request = this.request;
        return context;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public DiffContext setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }
}
