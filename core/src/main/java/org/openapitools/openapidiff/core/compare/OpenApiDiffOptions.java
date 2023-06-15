package org.openapitools.openapidiff.core.compare;

public class OpenApiDiffOptions {
  // Whether to fail backward compatibility check when enum values are added to responses
  private final boolean allowResponseEnumAdditions;

  private OpenApiDiffOptions(boolean allowResponseEnumAdditions) {
    this.allowResponseEnumAdditions = allowResponseEnumAdditions;
  }

  public boolean isAllowResponseEnumAdditions() {
    return allowResponseEnumAdditions;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private OpenApiDiffOptions built = new OpenApiDiffOptions(false);

    public Builder allowResponseEnumAdditions(boolean allowResponseEnumAdditions) {
      built = new OpenApiDiffOptions(allowResponseEnumAdditions);
      return this;
    }

    public OpenApiDiffOptions build() {
      return built;
    }
  }
}
