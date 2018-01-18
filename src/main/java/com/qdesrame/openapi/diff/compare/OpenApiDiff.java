package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.SecurityRequirementDiff;
import com.qdesrame.openapi.diff.model.*;
import com.qdesrame.openapi.diff.utils.EndpointUtils;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OpenApiDiff {

    public static final String SWAGGER_VERSION_V2 = "2.0";

    private static Logger logger = LoggerFactory.getLogger(OpenApiDiff.class);

    private ChangedOpenApi changedOpenApi;
    private PathsDiff pathsDiff;
    private PathDiff pathDiff;
    private SchemaDiff schemaDiff;
    private ContentDiff contentDiff;
    private ParametersDiff parametersDiff;
    private ParameterDiff parameterDiff;
    private RequestBodyDiff requestBodyDiff;
    private ResponseDiff responseDiff;
    private HeadersDiff headersDiff;
    private HeaderDiff headerDiff;
    private ApiResponseDiff apiResponseDiff;
    private OperationDiff operationDiff;
    private SecurityRequirementsDiff securityRequirementsDiff;
    private SecurityRequirementDiff securityRequirementDiff;
    private SecuritySchemeDiff securitySchemeDiff;
    private OAuthFlowsDiff oAuthFlowsDiff;
    private OAuthFlowDiff oAuthFlowDiff;

    private OpenAPI oldSpecOpenApi;
    private OpenAPI newSpecOpenApi;
    private List<Endpoint> newEndpoints;
    private List<Endpoint> missingEndpoints;
    private List<ChangedOperation> changedOperations;

    /**
     * compare two openapi doc
     *
     * @param oldSpec old api-doc location:Json or Http
     * @param newSpec new api-doc location:Json or Http
     */
    public static ChangedOpenApi compare(String oldSpec, String newSpec) {
        return compare(oldSpec, newSpec, null);
    }

    /**
     * @param oldSpec
     * @param newSpec
     * @param auths
     */
    private OpenApiDiff(String oldSpec, String newSpec, List<AuthorizationValue> auths) {
        this();
        OpenAPIV3Parser openApiParser = new OpenAPIV3Parser();
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        oldSpecOpenApi = openApiParser.read(oldSpec, auths, options);
        if (oldSpecOpenApi == null) {
            throw new RuntimeException("Cannot read old OpenAPI spec");
        }
        newSpecOpenApi = openApiParser.read(newSpec, auths, options);
        if (null == newSpecOpenApi) {
            throw new RuntimeException("Cannot read new OpenAPI spec");
        }
        initializeFields();
    }

    private OpenApiDiff() {
        this.changedOpenApi = new ChangedOpenApi();
    }

    public static ChangedOpenApi compare(OpenAPI oldOpenAPI, OpenAPI newOpenAPI) {
        return new OpenApiDiff(oldOpenAPI, newOpenAPI).compare();
    }

    public static ChangedOpenApi compare(String oldSpec, String newSpec,
                                         List<AuthorizationValue> auths) {
        return new OpenApiDiff(oldSpec, newSpec, auths).compare();
    }

    private void initializeFields() {
        this.pathsDiff = new PathsDiff(this);
        this.pathDiff = new PathDiff(this);
        this.schemaDiff = new SchemaDiff(this);
        this.contentDiff = new ContentDiff(this);
        this.parametersDiff = new ParametersDiff(this);
        this.parameterDiff = new ParameterDiff(this);
        this.requestBodyDiff = new RequestBodyDiff(this);
        this.responseDiff = new ResponseDiff(this);
        this.headersDiff = new HeadersDiff(this);
        this.headerDiff = new HeaderDiff(this);
        this.apiResponseDiff = new ApiResponseDiff(this);
        this.operationDiff = new OperationDiff(this);
        this.securityRequirementsDiff = new SecurityRequirementsDiff(this);
        this.securityRequirementDiff = new SecurityRequirementDiff(this);
        this.securitySchemeDiff = new SecuritySchemeDiff(this);
        this.oAuthFlowsDiff = new OAuthFlowsDiff(this);
        this.oAuthFlowDiff = new OAuthFlowDiff(this);
    }

    /*
     * @param oldSpecOpenApi
     * @param newSpecOpenApi
     */
    private OpenApiDiff(OpenAPI oldSpecOpenApi, OpenAPI newSpecOpenApi) {
        this();
        this.oldSpecOpenApi = oldSpecOpenApi;
        this.newSpecOpenApi = newSpecOpenApi;
        if (null == oldSpecOpenApi || null == newSpecOpenApi) {
            throw new RuntimeException(
                    "one of the old or new object is null");
        }
        initializeFields();
    }

    private ChangedOpenApi compare() {
        preProcess(oldSpecOpenApi);
        preProcess(newSpecOpenApi);
        Optional<ChangedPaths> paths = this.pathsDiff.diff(oldSpecOpenApi.getPaths(), newSpecOpenApi.getPaths());
        this.newEndpoints = new ArrayList<>();
        this.missingEndpoints = new ArrayList<>();
        this.changedOperations = new ArrayList<>();
        paths.ifPresent(changedPaths -> {
            this.newEndpoints = EndpointUtils.convert2EndpointList(changedPaths.getIncreased());
            this.missingEndpoints = EndpointUtils.convert2EndpointList(changedPaths.getMissing());
            changedPaths.getChanged().keySet().forEach(path -> {
                ChangedPath changedPath = changedPaths.getChanged().get(path);
                this.newEndpoints.addAll(EndpointUtils.convert2Endpoints(path, changedPath.getIncreased()));
                this.missingEndpoints.addAll(EndpointUtils.convert2Endpoints(path, changedPath.getMissing()));
                changedOperations.addAll(changedPath.getChanged());
            });
        });
        return getChangedOpenApi();
    }

    private void preProcess(OpenAPI openApi) {
        List<SecurityRequirement> securityRequirements = openApi.getSecurity();

        if (securityRequirements != null) {
            List<SecurityRequirement> distinctSecurityRequirements = securityRequirements.stream().distinct().collect(Collectors.toList());
            Map<String, PathItem> paths = openApi.getPaths();
            if (paths != null) {
                paths.values().forEach(pathItem -> pathItem.readOperationsMap().values().stream()
                        .filter(operation -> operation.getSecurity() != null)
                        .forEach(operation -> operation.setSecurity(operation.getSecurity().stream().distinct().collect(Collectors.toList()))));
                paths.values().forEach(pathItem -> pathItem.readOperationsMap().values().stream()
                        .filter(operation -> operation.getSecurity() == null)
                        .forEach(operation -> operation.setSecurity(distinctSecurityRequirements)));
            }
            openApi.setSecurity(null);
        }
    }

    private ChangedOpenApi getChangedOpenApi() {
        changedOpenApi.setMissingEndpoints(missingEndpoints);
        changedOpenApi.setNewEndpoints(newEndpoints);
        changedOpenApi.setNewSpecOpenApi(newSpecOpenApi);
        changedOpenApi.setOldSpecOpenApi(oldSpecOpenApi);
        changedOpenApi.setChangedOperations(changedOperations);
        return changedOpenApi;
    }

    public PathsDiff getPathsDiff() {
        return pathsDiff;
    }

    public PathDiff getPathDiff() {
        return pathDiff;
    }

    public SchemaDiff getSchemaDiff() {
        return schemaDiff;
    }

    public ContentDiff getContentDiff() {
        return contentDiff;
    }

    public ParametersDiff getParametersDiff() {
        return parametersDiff;
    }

    public ParameterDiff getParameterDiff() {
        return parameterDiff;
    }

    public RequestBodyDiff getRequestBodyDiff() {
        return requestBodyDiff;
    }

    public ResponseDiff getResponseDiff() {
        return responseDiff;
    }

    public HeadersDiff getHeadersDiff() {
        return headersDiff;
    }

    public HeaderDiff getHeaderDiff() {
        return headerDiff;
    }

    public ApiResponseDiff getApiResponseDiff() {
        return apiResponseDiff;
    }

    public OperationDiff getOperationDiff() {
        return operationDiff;
    }

    public SecurityRequirementsDiff getSecurityRequirementsDiff() {
        return securityRequirementsDiff;
    }

    public SecurityRequirementDiff getSecurityRequirementDiff() {
        return securityRequirementDiff;
    }

    public SecuritySchemeDiff getSecuritySchemeDiff() {
        return securitySchemeDiff;
    }

    public OAuthFlowsDiff getoAuthFlowsDiff() {
        return oAuthFlowsDiff;
    }

    public OAuthFlowDiff getoAuthFlowDiff() {
        return oAuthFlowDiff;
    }

    public OpenAPI getOldSpecOpenApi() {
        return oldSpecOpenApi;
    }

    public OpenAPI getNewSpecOpenApi() {
        return newSpecOpenApi;
    }

    public List<Endpoint> getNewEndpoints() {
        return newEndpoints;
    }

    public List<Endpoint> getMissingEndpoints() {
        return missingEndpoints;
    }

    public List<ChangedOperation> getChangedOperations() {
        return changedOperations;
    }

}
