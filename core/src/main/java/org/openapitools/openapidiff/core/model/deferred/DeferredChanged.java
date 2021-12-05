package org.openapitools.openapidiff.core.model.deferred;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedResponse;

public interface DeferredChanged<T> {

  void ifPresent(Consumer<T> consumer);

  void whenSet(Consumer<Optional<T>> consumer);

  <Q> DeferredChanged<Q> map(Function<Optional<T>, Q> consumer);

  <Q> DeferredChanged<Q> mapOptional(Function<Optional<T>, Optional<Q>> consumer);

  <Q> DeferredChanged<Q> flatMap(Function<Optional<T>, DeferredChanged<Q>> consumer);

  static <T> DeferredChanged<T> empty() {
    return RealizedChanged.empty();
  }

  static <T> DeferredChanged<T> ofNullable(@Nullable T value) {
    return new RealizedChanged<>(value);
  }

  static <T extends Changed> DeferredChanged<ChangedResponse> of(Optional<T> changed) {
    return new RealizedChanged(changed);
  }

  boolean isPresent();

  boolean isValueSet();

  T get();
}
