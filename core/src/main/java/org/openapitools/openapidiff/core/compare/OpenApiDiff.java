package org.openapitools.openapidiff.core.compare;

import static org.openapitools.openapidiff.core.compare.PathsDiff.valOrEmpty;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.openapitools.openapidiff.core.model.ChangedExtensions;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.ChangedPath;
import org.openapitools.openapidiff.core.model.ChangedPaths;
import org.openapitools.openapidiff.core.model.Endpoint;
import org.openapitools.openapidiff.core.model.deferred.DeferredChanged;
import org.openapitools.openapidiff.core.model.deferred.DeferredSchemaCache;
import org.openapitools.openapidiff.core.utils.EndpointUtils;

public class OpenApiDiff {
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
  private ExtensionsDiff extensionsDiff;
  private MetadataDiff metadataDiff;
  private final OpenAPI oldSpecOpenApi;
  private final OpenAPI newSpecOpenApi;
  private List<Endpoint> newEndpoints;
  private List<Endpoint> missingEndpoints;
  private List<ChangedOperation> changedOperations;
  private ChangedExtensions changedExtensions;
  private DeferredSchemaCache deferredSchemaCache;

  /*
   * @param oldSpecOpenApi
   * @param newSpecOpenApi
   */
  private OpenApiDiff(OpenAPI oldSpecOpenApi, OpenAPI newSpecOpenApi) {
    this.oldSpecOpenApi = oldSpecOpenApi;
    this.newSpecOpenApi = newSpecOpenApi;
    if (null == oldSpecOpenApi || null == newSpecOpenApi) {
      throw new RuntimeException("one of the old or new object is null");
    }
    initializeFields();
  }

  public static ChangedOpenApi compare(OpenAPI oldSpec, OpenAPI newSpec) {
    return new OpenApiDiff(oldSpec, newSpec).compare();
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
    this.extensionsDiff = new ExtensionsDiff(this);
    this.metadataDiff = new MetadataDiff(this);
    this.deferredSchemaCache = new DeferredSchemaCache(this);
  }

  private ChangedOpenApi compare() {
    preProcess(oldSpecOpenApi);
    preProcess(newSpecOpenApi);

    // 1st pass scans paths to collect all schemas
    DeferredChanged<ChangedPaths> paths =
        this.pathsDiff.diff(
            valOrEmpty(oldSpecOpenApi.getPaths()), valOrEmpty(newSpecOpenApi.getPaths()));

    // 2nd pass processes deferred schemas
    deferredSchemaCache.process();

    this.newEndpoints = new ArrayList<>();
    this.missingEndpoints = new ArrayList<>();
    this.changedOperations = new ArrayList<>();

    paths.ifPresent(
        changedPaths -> {
          this.newEndpoints = EndpointUtils.convert2EndpointList(changedPaths.getIncreased());
          this.missingEndpoints = EndpointUtils.convert2EndpointList(changedPaths.getMissing());
          changedPaths
              .getChanged()
              .keySet()
              .forEach(
                  path -> {
                    ChangedPath changedPath = changedPaths.getChanged().get(path);
                    this.newEndpoints.addAll(
                        EndpointUtils.convert2Endpoints(path, changedPath.getIncreased()));
                    this.missingEndpoints.addAll(
                        EndpointUtils.convert2Endpoints(path, changedPath.getMissing()));
                    changedOperations.addAll(changedPath.getChanged());
                  });
        });
    getExtensionsDiff()
        .diff(oldSpecOpenApi.getExtensions(), newSpecOpenApi.getExtensions())
        .ifPresent(this::setChangedExtension);

    return getChangedOpenApi();
  }

  private void setChangedExtension(ChangedExtensions changedExtension) {
    this.changedExtensions = changedExtension;
  }

  private void preProcess(OpenAPI openApi) {
    List<SecurityRequirement> securityRequirements = openApi.getSecurity();
    if (securityRequirements != null) {
      List<SecurityRequirement> distinctSecurityRequirements =
          securityRequirements.stream().distinct().collect(Collectors.toList());
      Map<String, PathItem> paths = openApi.getPaths();
      if (paths != null) {
        paths
            .values()
            .forEach(
                pathItem ->
                    pathItem.readOperationsMap().values().stream()
                        .filter(operation -> operation.getSecurity() != null)
                        .forEach(
                            operation ->
                                operation.setSecurity(
                                    operation.getSecurity().stream()
                                        .distinct()
                                        .collect(Collectors.toList()))));
        paths
            .values()
            .forEach(
                pathItem ->
                    pathItem.readOperationsMap().values().stream()
                        .filter(operation -> operation.getSecurity() == null)
                        .forEach(operation -> operation.setSecurity(distinctSecurityRequirements)));
      }
      openApi.setSecurity(null);
    }
  }

  private ChangedOpenApi getChangedOpenApi() {
    return new ChangedOpenApi()
        .setMissingEndpoints(missingEndpoints)
        .setNewEndpoints(newEndpoints)
        .setNewSpecOpenApi(newSpecOpenApi)
        .setOldSpecOpenApi(oldSpecOpenApi)
        .setChangedOperations(changedOperations)
        .setChangedExtensions(changedExtensions)
        .setChangedSchemas(deferredSchemaCache.getChangedSchemas());
  }

  public DeferredSchemaCache getDeferredSchemaCache() {
    return deferredSchemaCache;
  }

  public PathsDiff getPathsDiff() {
    return this.pathsDiff;
  }

  public PathDiff getPathDiff() {
    return this.pathDiff;
  }

  public SchemaDiff getSchemaDiff() {
    return this.schemaDiff;
  }

  public ContentDiff getContentDiff() {
    return this.contentDiff;
  }

  public ParametersDiff getParametersDiff() {
    return this.parametersDiff;
  }

  public ParameterDiff getParameterDiff() {
    return this.parameterDiff;
  }

  public RequestBodyDiff getRequestBodyDiff() {
    return this.requestBodyDiff;
  }

  public ResponseDiff getResponseDiff() {
    return this.responseDiff;
  }

  public HeadersDiff getHeadersDiff() {
    return this.headersDiff;
  }

  public HeaderDiff getHeaderDiff() {
    return this.headerDiff;
  }

  public ApiResponseDiff getApiResponseDiff() {
    return this.apiResponseDiff;
  }

  public OperationDiff getOperationDiff() {
    return this.operationDiff;
  }

  public SecurityRequirementsDiff getSecurityRequirementsDiff() {
    return this.securityRequirementsDiff;
  }

  public SecurityRequirementDiff getSecurityRequirementDiff() {
    return this.securityRequirementDiff;
  }

  public SecuritySchemeDiff getSecuritySchemeDiff() {
    return this.securitySchemeDiff;
  }

  public OAuthFlowsDiff getOAuthFlowsDiff() {
    return this.oAuthFlowsDiff;
  }

  public OAuthFlowDiff getOAuthFlowDiff() {
    return this.oAuthFlowDiff;
  }

  public ExtensionsDiff getExtensionsDiff() {
    return this.extensionsDiff;
  }

  public MetadataDiff getMetadataDiff() {
    return this.metadataDiff;
  }

  public OpenAPI getOldSpecOpenApi() {
    return this.oldSpecOpenApi;
  }

  public OpenAPI getNewSpecOpenApi() {
    return this.newSpecOpenApi;
  }

  public List<Endpoint> getNewEndpoints() {
    return this.newEndpoints;
  }

  public List<Endpoint> getMissingEndpoints() {
    return this.missingEndpoints;
  }

  public List<ChangedOperation> getChangedOperations() {
    return this.changedOperations;
  }

  public ChangedExtensions getChangedExtensions() {
    return this.changedExtensions;
  }
}
