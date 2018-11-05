package com.qdesrame.openapi.diff.model;

import static java.lang.String.format;

public enum DiffResult {
  NO_CHANGES("no_changes", 0),
  METADATA("metadata", 1),
  COMPATIBLE("compatible", 2),
  UNKNOWN("unknown", 3),
  INCOMPATIBLE("incompatible", 4);

  private final String value;
  private final int weight;

  DiffResult(String value, int weight) {
    this.value = value;
    this.weight = weight;
  }

  public static DiffResult fromWeight(int weight) {
    for (DiffResult result : DiffResult.values()) {
      if (weight == result.getWeight()) {
        return result;
      }
    }
    throw new IllegalArgumentException(format("Invalid weight from DiffResult: %d", weight));
  }

  public int getWeight() {
    return this.weight;
  }

  public String getValue() {
    return value;
  }

  public boolean isUnchanged() {
    return this.weight == 0;
  }

  public boolean isDifferent() {
    return this.weight > 0;
  }

  public boolean isIncompatible() {
    return this.weight > 2;
  }

  public boolean isCompatible() {
    return this.weight <= 2;
  }

  public boolean isMetaChanged() {
    return this.weight == 1;
  }
}
