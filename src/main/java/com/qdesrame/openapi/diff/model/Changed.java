package com.qdesrame.openapi.diff.model;

public interface Changed {
    DiffResult isChanged();

    default boolean isCompatible() {
        return isChanged().isCompatible();
    }

    default boolean isIncompatible() {
        return isChanged().isIncompatible();
    }

    default boolean isUnchanged() {
        return isChanged().isUnchanged();
    }

    default boolean isDifferent() {
        return isChanged().isDifferent();
    }
}
