package org.openapitools.openapidiff.core;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.io.File;
import java.util.List;
import org.openapitools.openapidiff.core.compare.OpenApiDiff;
import org.openapitools.openapidiff.core.compare.OpenApiDiffOptions;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class OpenApiCompare {
  private static final OpenAPIParser PARSER = new OpenAPIParser();
  private static final ParseOptions OPTIONS = new ParseOptions();

  static {
    OPTIONS.setResolve(true);
  }

  private OpenApiCompare() {}

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldContent old API document location: JSON or HTTP
   * @param newContent new API document location: JSON or HTTP
   * @return Comparison result
   */
  public static ChangedOpenApi fromContents(String oldContent, String newContent) {
    return fromContents(oldContent, newContent, null);
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldContent old API document location: JSON or HTTP
   * @param newContent new API document location: JSON or HTTP
   * @param auths authorization values
   * @return Comparison result
   */
  public static ChangedOpenApi fromContents(
      String oldContent, String newContent, List<AuthorizationValue> auths) {
    return fromContents(oldContent, newContent, auths, OpenApiDiffOptions.builder().build());
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldContent old API document location: JSON or HTTP
   * @param newContent new API document location: JSON or HTTP
   * @param auths authorization values
   * @param options comparison options
   * @return Comparison result
   */
  public static ChangedOpenApi fromContents(
      String oldContent,
      String newContent,
      List<AuthorizationValue> auths,
      OpenApiDiffOptions options) {
    return fromSpecifications(
        readContent(oldContent, auths), readContent(newContent, auths), options);
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldFile old API document file
   * @param newFile new API document file
   * @return Comparison result
   */
  public static ChangedOpenApi fromFiles(File oldFile, File newFile) {
    return fromFiles(oldFile, newFile, null);
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldFile old API document file
   * @param newFile new API document file
   * @param auths authorization values
   * @return Comparison result
   */
  public static ChangedOpenApi fromFiles(
      File oldFile, File newFile, List<AuthorizationValue> auths) {
    return fromFiles(oldFile, newFile, auths, OpenApiDiffOptions.builder().build());
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldFile old API document file
   * @param newFile new API document file
   * @param auths authorization values
   * @param options comparison options
   * @return Comparison result
   */
  public static ChangedOpenApi fromFiles(
      File oldFile, File newFile, List<AuthorizationValue> auths, OpenApiDiffOptions options) {
    return fromLocations(oldFile.getAbsolutePath(), newFile.getAbsolutePath(), auths, options);
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldLocation old API document location (local or HTTP)
   * @param newLocation new API document location (local or HTTP)
   * @return Comparison result
   */
  public static ChangedOpenApi fromLocations(String oldLocation, String newLocation) {
    return fromLocations(oldLocation, newLocation, null);
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldLocation old API document location (local or HTTP)
   * @param newLocation new API document location (local or HTTP)
   * @param auths authorization values
   * @return Comparison result
   */
  public static ChangedOpenApi fromLocations(
      String oldLocation, String newLocation, List<AuthorizationValue> auths) {
    return fromLocations(oldLocation, newLocation, auths, OpenApiDiffOptions.builder().build());
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldLocation old API document location (local or HTTP)
   * @param newLocation new API document location (local or HTTP)
   * @param auths authorization values
   * @param options comparison options
   * @return Comparison result
   */
  public static ChangedOpenApi fromLocations(
      String oldLocation,
      String newLocation,
      List<AuthorizationValue> auths,
      OpenApiDiffOptions options) {
    return fromSpecifications(
        readLocation(oldLocation, auths), readLocation(newLocation, auths), options);
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldSpec old API document specification
   * @param newSpec new API document specification
   * @return Comparison result
   */
  public static ChangedOpenApi fromSpecifications(OpenAPI oldSpec, OpenAPI newSpec) {
    return fromSpecifications(oldSpec, newSpec, OpenApiDiffOptions.builder().build());
  }

  /**
   * Compare two OpenAPI documents.
   *
   * @param oldSpec old API document specification
   * @param newSpec new API document specification
   * @param options comparison options
   * @return Comparison result
   */
  public static ChangedOpenApi fromSpecifications(
      OpenAPI oldSpec, OpenAPI newSpec, OpenApiDiffOptions options) {
    return OpenApiDiff.compare(notNull(oldSpec, "old"), notNull(newSpec, "new"), options);
  }

  private static OpenAPI notNull(OpenAPI spec, String type) {
    if (spec == null) {
      throw new RuntimeException(String.format("Cannot read %s OpenAPI spec", type));
    }
    return spec;
  }

  private static OpenAPI readContent(String content, List<AuthorizationValue> auths) {
    return PARSER.readContents(content, auths, OPTIONS).getOpenAPI();
  }

  private static OpenAPI readLocation(String location, List<AuthorizationValue> auths) {
    return PARSER.readLocation(location, auths, OPTIONS).getOpenAPI();
  }
}
