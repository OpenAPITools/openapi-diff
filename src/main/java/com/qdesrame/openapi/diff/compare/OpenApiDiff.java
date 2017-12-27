package com.qdesrame.openapi.diff.compare;

import com.qdesrame.openapi.diff.model.*;
import com.qdesrame.openapi.diff.utils.EndpointUtils;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenApiDiff {

    public static final String SWAGGER_VERSION_V2 = "2.0";

    private static Logger logger = LoggerFactory.getLogger(OpenApiDiff.class);

    private ChangedOpenApi changedOpenApi;
    private SchemaDiff schemaDiff;
    private ContentDiff contentDiff;
    private ParametersDiff parametersDiff;
    private ParameterDiff parameterDiff;
    private RequestBodyDiff requestBodyDiff;

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
        this.schemaDiff = new SchemaDiff(this);
        this.contentDiff = new ContentDiff(this);
        this.parametersDiff = new ParametersDiff(this);
        this.parameterDiff = new ParameterDiff(this);
        this.requestBodyDiff = new RequestBodyDiff(this);
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
        Map<String, PathItem> oldPaths = oldSpecOpenApi.getPaths();
        Map<String, PathItem> newPaths = newSpecOpenApi.getPaths();
        MapKeyDiff<String, PathItem> pathDiff = MapKeyDiff.diff(oldPaths, newPaths);
        this.newEndpoints = EndpointUtils.convert2EndpointList(pathDiff.getIncreased());
        this.missingEndpoints = EndpointUtils.convert2EndpointList(pathDiff.getMissing());

        this.changedOperations = new ArrayList<>();

        List<String> sharedKey = pathDiff.getSharedKey();
        for (String pathUrl : sharedKey) {
            PathItem oldPath = oldPaths.get(pathUrl);
            PathItem newPath = newPaths.get(pathUrl);

            Map<PathItem.HttpMethod, Operation> oldOperationMap = oldPath.readOperationsMap();
            Map<PathItem.HttpMethod, Operation> newOperationMap = newPath.readOperationsMap();
            MapKeyDiff<PathItem.HttpMethod, Operation> operationDiff = MapKeyDiff.diff(oldOperationMap,
                    newOperationMap);
            Map<PathItem.HttpMethod, Operation> increasedOperation = operationDiff.getIncreased();
            Map<PathItem.HttpMethod, Operation> missingOperation = operationDiff.getMissing();

            this.newEndpoints.addAll(EndpointUtils.convert2Endpoints(pathUrl, increasedOperation));
            this.missingEndpoints.addAll(EndpointUtils.convert2Endpoints(pathUrl, missingOperation));

            List<PathItem.HttpMethod> sharedMethods = operationDiff.getSharedKey();

            ChangedOperation changedOperation;
            for (PathItem.HttpMethod method : sharedMethods) {
                Operation oldOperation = oldOperationMap.get(method);
                Operation newOperation = newOperationMap.get(method);
                changedOperation = new ChangedOperation(pathUrl, method, oldOperation, newOperation);
                changedOperation.setSummary(newOperation.getSummary());
                changedOperation.setDeprecated(!Boolean.TRUE.equals(oldOperation.getDeprecated()) && Boolean.TRUE.equals(newOperation.getDeprecated()));

                if (oldOperation.getRequestBody() != null || newOperation.getRequestBody() != null) {
                    changedOperation.setChangedRequestBody(requestBodyDiff.diff(oldOperation.getRequestBody(), newOperation.getRequestBody()));
                }

                ChangedParameters changedParameters = parametersDiff.diff(oldOperation.getParameters(), newOperation.getParameters());
                changedOperation.setChangedParameters(changedParameters);

                MapKeyDiff<String, ApiResponse> responseDiff = MapKeyDiff.diff(oldOperation.getResponses(),
                        newOperation.getResponses());
                ChangedApiResponse changedApiResponse = new ChangedApiResponse(oldOperation.getResponses(), newOperation.getResponses());
                changedOperation.setChangedApiResponse(changedApiResponse);
                changedApiResponse.setAddResponses(responseDiff.getIncreased());
                changedApiResponse.setMissingResponses(responseDiff.getMissing());
                List<String> sharedResponseCodes = responseDiff.getSharedKey();
                Map<String, ChangedResponse> resps = new HashMap<>();
                for (String responseCode : sharedResponseCodes) {
                    ApiResponse oldResponse = RefPointer.Replace.response(oldSpecOpenApi.getComponents(), oldOperation.getResponses().get(responseCode));
                    ApiResponse newResponse = RefPointer.Replace.response(newSpecOpenApi.getComponents(), newOperation.getResponses().get(responseCode));
                    ChangedContent changedContent = contentDiff.diff(oldResponse.getContent(), newResponse.getContent());
                    ChangedResponse changedResponse = new ChangedResponse(newResponse.getDescription(), oldResponse.getContent(), newResponse.getContent());
                    changedResponse.setDescription(newResponse.getDescription());
                    changedResponse.setChangedContent(changedContent);
                    if (changedResponse.isDiff()) {
                        resps.put(responseCode, changedResponse);
                    }
                }
                changedApiResponse.setChangedResponses(resps);

                if (changedOperation.isDiff()) {
                    changedOperations.add(changedOperation);
                }
            }

        }

        return getChangedOpenApi();
    }

    private ChangedOpenApi getChangedOpenApi() {
        changedOpenApi.setMissingEndpoints(missingEndpoints);
        changedOpenApi.setNewEndpoints(newEndpoints);
        changedOpenApi.setNewSpecOpenApi(newSpecOpenApi);
        changedOpenApi.setOldSpecOpenApi(oldSpecOpenApi);
        changedOpenApi.setChangedOperations(changedOperations);
        return changedOpenApi;
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
