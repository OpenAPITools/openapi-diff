package com.qdesrame.openapi.diff.core.compare;

public interface Comparable<T> {

  boolean compare(T left, T right);
}
