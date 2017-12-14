package com.qdesrame.openapi.diff.compare;

public interface Comparable<T> {

    boolean compare(T left, T right);
}
