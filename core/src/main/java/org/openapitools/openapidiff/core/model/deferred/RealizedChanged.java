package org.openapitools.openapidiff.core.model.deferred;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class RealizedChanged<T> implements DeferredChanged<T> {
  private final Optional<T> value;

  public RealizedChanged(T value) {
    this.value = Optional.ofNullable(value);
  }

  public RealizedChanged(Optional<T> value) {
    this.value = value;
  }

  @Override
  public void ifPresent(Consumer<T> consumer) {
    value.ifPresent(consumer);
  }

  @Override
  public void whenSet(Consumer<Optional<T>> consumer) {
    consumer.accept(value);
  }

  public static <T> RealizedChanged<T> empty() {
    return new RealizedChanged<>(Optional.empty());
  }

  @Override
  public boolean isPresent() {
    return value.isPresent();
  }

  @Override
  public boolean isValueSet() {
    return true;
  }

  @Override
  public T get() {
    return value.get();
  }

  public <Q> DeferredChanged<Q> map(Function<Optional<T>, Q> function) {
    return new RealizedChanged<>(function.apply(this.value));
  }

  @Override
  public <Q> DeferredChanged<Q> mapOptional(Function<Optional<T>, Optional<Q>> consumer) {
    return new RealizedChanged<>(consumer.apply(this.value));
  }

  public <Q> DeferredChanged<Q> flatMap(Function<Optional<T>, DeferredChanged<Q>> function) {
    return function.apply(this.value);
  }

  @Override
  public String toString() {
    return "RealizedChanged{" + "value=" + value + '}';
  }
}
