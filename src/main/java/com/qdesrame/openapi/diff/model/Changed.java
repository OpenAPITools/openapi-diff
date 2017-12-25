package com.qdesrame.openapi.diff.model;

public interface Changed {
	boolean isDiff();
    boolean isDiffBackwardCompatible();
}
