package com.qdesrame.openapi.diff.model;

/**
 * Created by adarsh.sharma on 24/12/17.
 */
public interface RequestResponseChanged {
    boolean isDiff();
    boolean isDiffBackwardCompatible(boolean isRequest);
}
