package org.openapitools.openapidiff.core.compare;

public interface Comparable<T> {

  boolean compare(T left, T right);
}
