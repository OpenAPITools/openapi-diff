package com.qdesrame.openapi.diff.model;

public enum DiffResult {
    NO_CHANGES("no_changes"),
    COMPATIBLE("compatible"),
    INCOMPATIBLE("incompatible"),
    UNKNOWN("unknown");

    private final String value;

    DiffResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isUnchanged() {
        return this.equals(NO_CHANGES);
    }

    public boolean isDifferent() {
        return !this.equals(NO_CHANGES);
    }

    public boolean isIncompatible() {
        return !this.equals(NO_CHANGES) && !this.equals(COMPATIBLE);
    }

    public boolean isCompatible() {
        return this.equals(NO_CHANGES) || this.equals(COMPATIBLE);
    }
}
