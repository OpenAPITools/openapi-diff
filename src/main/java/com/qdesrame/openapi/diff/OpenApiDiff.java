package com.qdesrame.openapi.diff;

import com.qdesrame.openapi.diff.compare.*;
import com.qdesrame.openapi.diff.model.*;
import com.qdesrame.openapi.diff.utils.RefPointer;
import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.Operation;
import io.swagger.oas.models.PathItem;
import io.swagger.oas.models.media.MediaType;
import io.swagger.oas.models.parameters.RequestBody;
import io.swagger.oas.models.responses.ApiResponse;
import io.swagger.parser.models.AuthorizationValue;
import io.swagger.parser.models.ParseOptions;
import io.swagger.parser.v3.OpenAPIV3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class OpenApiDiff {

    public static final String SWAGGER_VERSION_V2 = "2.0";

    private static Logger logger = LoggerFactory.getLogger(OpenApiDiff.class);

    private OpenAPI oldSpecOpenApi;
    private OpenAPI newSpecOpenApi;

    private List<Endpoint> newEndpoints;
    private List<Endpoint> missingEndpoints;
    private List<Endpoint> deprecatedEndpoints;
    private List<ChangedEndpoint> changedEndpoints;

    /**
     * compare two openapi doc
     *
     * @param oldSpec old api-doc location:Json or Http
     * @param newSpec new api-doc location:Json or Http
     */
    public static OpenApiDiff compare(String oldSpec, String newSpec) {
        return compare(oldSpec, newSpec, null);
    }

    public static OpenApiDiff compare(String oldSpec, String newSpec,
                                      List<AuthorizationValue> auths) {
        return new OpenApiDiff(oldSpec, newSpec, auths).compare();
    }

    /**
     * @param oldSpec
     * @param newSpec
     * @param auths
     */
    private OpenApiDiff(String oldSpec, String newSpec, List<AuthorizationValue> auths) {
        OpenAPIV3Parser openApiParser = new OpenAPIV3Parser();
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        oldSpecOpenApi = openApiParser.read(oldSpec, auths, options);
        newSpecOpenApi = openApiParser.read(newSpec, auths, options);
        if (null == oldSpecOpenApi || null == newSpecOpenApi) {
            throw new RuntimeException(
                    "cannot read api-doc from spec.");
        }
    }

    private OpenApiDiff compare() {
        Map<String, PathItem> oldPaths = oldSpecOpenApi.getPaths();
        Map<String, PathItem> newPaths = newSpecOpenApi.getPaths();
        MapKeyDiff<String, PathItem> pathDiff = MapKeyDiff.diff(oldPaths, newPaths);
        this.newEndpoints = convert2EndpointList(pathDiff.getIncreased());
        this.missingEndpoints = convert2EndpointList(pathDiff.getMissing());

        this.changedEndpoints = new ArrayList<>();
        this.deprecatedEndpoints = new ArrayList<>();

        List<String> sharedKey = pathDiff.getSharedKey();
        ChangedEndpoint changedEndpoint;
        for (String pathUrl : sharedKey) {
            changedEndpoint = new ChangedEndpoint();
            changedEndpoint.setPathUrl(pathUrl);
            PathItem oldPath = oldPaths.get(pathUrl);
            PathItem newPath = newPaths.get(pathUrl);

            Map<PathItem.HttpMethod, Operation> oldOperationMap = oldPath.readOperationsMap();
            Map<PathItem.HttpMethod, Operation> newOperationMap = newPath.readOperationsMap();
            MapKeyDiff<PathItem.HttpMethod, Operation> operationDiff = MapKeyDiff.diff(oldOperationMap,
                    newOperationMap);
            Map<PathItem.HttpMethod, Operation> increasedOperation = operationDiff.getIncreased();
            Map<PathItem.HttpMethod, Operation> missingOperation = operationDiff.getMissing();
            changedEndpoint.setNewOperations(increasedOperation);
            changedEndpoint.setMissingOperations(missingOperation);

            List<PathItem.HttpMethod> sharedMethods = operationDiff.getSharedKey();
            Map<PathItem.HttpMethod, ChangedOperation> operas = new HashMap<>();
            Map<PathItem.HttpMethod, Operation> deprecOperas = new HashMap<>();
            ChangedOperation changedOperation;
            for (PathItem.HttpMethod method : sharedMethods) {
                changedOperation = new ChangedOperation();
                Operation oldOperation = oldOperationMap.get(method);
                Operation newOperation = newOperationMap.get(method);
                changedOperation.setSummary(newOperation.getSummary());
                changedOperation.setDeprecated(!Boolean.TRUE.equals(oldOperation.getDeprecated()) && Boolean.TRUE.equals(newOperation.getDeprecated()));

                if (oldOperation.getRequestBody() != null && newOperation.getRequestBody() != null) {
                    RequestBody oldRequestBody = RefPointer.Replace.requestBody(oldSpecOpenApi.getComponents(), oldOperation.getRequestBody());
                    RequestBody newRequestBody = RefPointer.Replace.requestBody(newSpecOpenApi.getComponents(), newOperation.getRequestBody());
                    MapKeyDiff<String, MediaType> mediaTypeDiff = MapKeyDiff.diff(oldRequestBody.getContent(), newRequestBody.getContent());
                    Map<String, MediaType> increasedMediaType = mediaTypeDiff.getIncreased();
                    Map<String, MediaType> missingMediaType = mediaTypeDiff.getMissing();
                    changedOperation.setMissingRequestMediaTypes(missingMediaType);
                    changedOperation.setAddRequestMediaTypes(increasedMediaType);
                    List<String> sharedMediaTypes = mediaTypeDiff.getSharedKey();
                    Map<String, ChangedMediaType> medias = new HashMap<String, ChangedMediaType>();
                    ChangedMediaType changedMediaType;
                    for (String mediaTypeKey : sharedMediaTypes) {
                        changedMediaType = new ChangedMediaType();
                        MediaType oldMediaType = oldRequestBody.getContent().get(mediaTypeKey);
                        MediaType newMediaType = newRequestBody.getContent().get(mediaTypeKey);
                        SchemaDiffResult schemaDiff = SchemaDiff.fromComponents(
                                oldSpecOpenApi.getComponents(), newSpecOpenApi.getComponents())
                                .diff(oldMediaType.getSchema(), newMediaType.getSchema());

                        changedMediaType.setSchema(schemaDiff);
                        if (changedMediaType.isDiff()) {
                            medias.put(mediaTypeKey, changedMediaType);
                        }
                    }
                    changedOperation.setChangedRequestMediaTypes(medias);
                } else if (oldOperation.getRequestBody() == null && newOperation.getRequestBody() == null) {

                } else {
                    throw new RuntimeException("TODO"); // TODO
                }

                ParametersDiffResult parameterDiff = ParametersDiff
                        .fromComponents(oldSpecOpenApi.getComponents(),
                                newSpecOpenApi.getComponents())
                        .diff(oldOperation.getParameters(), newOperation.getParameters());
                changedOperation.setAddParameters(parameterDiff.getIncreased());
                changedOperation.setMissingParameters(parameterDiff.getMissing());
                changedOperation.setChangedParameter(parameterDiff.getChanged());

                MapKeyDiff<String, ApiResponse> responseDiff = MapKeyDiff.diff(oldOperation.getResponses(),
                        newOperation.getResponses());
                changedOperation.setAddResponses(responseDiff.getIncreased());
                changedOperation.setMissingResponses(responseDiff.getMissing());
                List<String> sharedResponseCodes = responseDiff.getSharedKey();
                Map<String, ChangedResponse> resps = new HashMap<String, ChangedResponse>();
                ChangedResponse changedResponse;
                for (String responseCode : sharedResponseCodes) {
                    changedResponse = new ChangedResponse();
                    ApiResponse oldResponse = oldOperation.getResponses().get(responseCode);
                    ApiResponse newResponse = newOperation.getResponses().get(responseCode);
                    changedResponse.setDescription(newResponse.getDescription());

                    MapKeyDiff<String, MediaType> mediaTypeDiff = MapKeyDiff.diff(oldResponse.getContent(), newResponse.getContent());
                    Map<String, MediaType> increasedMediaType = mediaTypeDiff.getIncreased();
                    Map<String, MediaType> missingMediaType = mediaTypeDiff.getMissing();
                    changedResponse.setMissingMediaTypes(missingMediaType);
                    changedResponse.setAddMediaTypes(increasedMediaType);
                    List<String> sharedMediaTypes = mediaTypeDiff.getSharedKey();
                    Map<String, ChangedMediaType> medias = new HashMap<String, ChangedMediaType>();
                    ChangedMediaType changedMediaType;
                    for (String mediaTypeKey : sharedMediaTypes) {
                        changedMediaType = new ChangedMediaType();
                        MediaType oldMediaType = oldResponse.getContent().get(mediaTypeKey);
                        MediaType newMediaType = newResponse.getContent().get(mediaTypeKey);
                        SchemaDiffResult schemaDiff = SchemaDiff.fromComponents(
                                oldSpecOpenApi.getComponents(), newSpecOpenApi.getComponents())
                                .diff(oldMediaType.getSchema(), newMediaType.getSchema());

                        changedMediaType.setSchema(schemaDiff);
                        if (changedMediaType.isDiff()) {
                            medias.put(mediaTypeKey, changedMediaType);
                        }
                    }
                    changedResponse.setChangedMediaTypes(medias);
                    if (changedResponse.isDiff()) {
                        resps.put(responseCode, changedResponse);
                    }
                }
                changedOperation.setChangedResponses(resps);

                if (changedOperation.isDiff()) {
                    operas.put(method, changedOperation);
                } else if (changedOperation.isDeprecated()) {
                    deprecOperas.put(method, newOperation);
                }
            }
            changedEndpoint.setChangedOperations(operas);
            changedEndpoint.setDeprecatedOperations(deprecOperas);

            this.newEndpoints.addAll(convert2EndpointList(changedEndpoint.getPathUrl(),
                    changedEndpoint.getNewOperations()));
            this.missingEndpoints.addAll(convert2EndpointList(changedEndpoint.getPathUrl(),
                    changedEndpoint.getMissingOperations()));
            if (changedEndpoint.isDeprecated()) {
                this.deprecatedEndpoints.addAll(convert2EndpointList(changedEndpoint.getPathUrl(), changedEndpoint.getDeprecatedOperations()));
            }

            if (changedEndpoint.isDiff()) {
                changedEndpoints.add(changedEndpoint);
            }
        }

        return this;
    }

    private List<Endpoint> convert2EndpointList(Map<String, PathItem> map) {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        if (null == map) return endpoints;
        for (Entry<String, PathItem> entry : map.entrySet()) {
            String url = entry.getKey();
            PathItem path = entry.getValue();

            Map<PathItem.HttpMethod, Operation> operationMap = path.readOperationsMap();
            for (Entry<PathItem.HttpMethod, Operation> entryOper : operationMap.entrySet()) {
                PathItem.HttpMethod httpMethod = entryOper.getKey();
                Operation operation = entryOper.getValue();

                Endpoint endpoint = new Endpoint();
                endpoint.setPathUrl(url);
                endpoint.setMethod(httpMethod);
                endpoint.setSummary(operation.getSummary());
                endpoint.setPath(path);
                endpoint.setOperation(operation);
                endpoints.add(endpoint);
            }
        }
        return endpoints;
    }

    private Collection<? extends Endpoint> convert2EndpointList(String pathUrl,
                                                                Map<PathItem.HttpMethod, Operation> map) {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        if (null == map) return endpoints;
        for (Entry<PathItem.HttpMethod, Operation> entry : map.entrySet()) {
            PathItem.HttpMethod httpMethod = entry.getKey();
            Operation operation = entry.getValue();
            Endpoint endpoint = new Endpoint();
            endpoint.setPathUrl(pathUrl);
            endpoint.setMethod(httpMethod);
            endpoint.setSummary(operation.getSummary());
            endpoint.setOperation(operation);
            endpoints.add(endpoint);
        }
        return endpoints;
    }

    public List<Endpoint> getNewEndpoints() {
        return newEndpoints;
    }

    public List<Endpoint> getMissingEndpoints() {
        return missingEndpoints;
    }

    public List<Endpoint> getDeprecatedEndpoints() {
        return deprecatedEndpoints;
    }

    public List<ChangedEndpoint> getChangedEndpoints() {
        return changedEndpoints;
    }

}
