package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.PathItem;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by Quentin Desramé on 04/04/17.
 */
@Getter
@Setter
public class DiffContext {

    private String url;
    private Map<String, String> parameters;
    private PathItem.HttpMethod method;
    private boolean response;
    private boolean request;
}
