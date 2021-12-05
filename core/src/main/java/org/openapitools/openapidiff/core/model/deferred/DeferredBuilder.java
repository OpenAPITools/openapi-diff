package org.openapitools.openapidiff.core.model.deferred;

import static org.openapitools.openapidiff.core.utils.ChangedUtils.isChanged;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.openapitools.openapidiff.core.model.Changed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeferredBuilder<T> implements Consumer<DeferredChanged<T>> {
  private static final Logger log = LoggerFactory.getLogger(DeferredBuilder.class);

  private final List<DeferredChanged<? extends T>> deferredValues = new ArrayList<>();
  private final List<Consumer<Optional<List<Optional<? super T>>>>> whenSet = new ArrayList<>();

  public <V extends T> Optional<V> with(Optional<V> value) {
    return value;
  }

  public <V extends T> DeferredChanged<V> with(DeferredChanged<V> value) {
    deferredValues.add(value);
    return value;
  }

  public <V extends T> DeferredBuilder<T> add(DeferredChanged<V> value) {
    deferredValues.add(value);
    return this;
  }

  public <V extends T> DeferredBuilder<T> addAll(List<DeferredChanged<V>> values) {
    deferredValues.addAll(values);
    return this;
  }

  public <V extends T> DeferredBuilder<T> addAll(Stream<DeferredChanged<V>> values) {
    deferredValues.addAll(values.collect(Collectors.toList()));
    return this;
  }

  public DeferredBuilder<T> whenSet(Consumer<Optional<List<Optional<? super T>>>> consumer) {
    whenSet.add(consumer);
    return this;
  }

  @Override
  public void accept(DeferredChanged<T> value) {
    deferredValues.add(value);
  }

  public DeferredChanged<List<Optional<? super T>>> build() {
    if (deferredValues.isEmpty()) {
      return DeferredChanged.empty();
    }

    log.debug("Building collected deferred {}", DeferredLogger.logValue(deferredValues));

    final PendingChanged<List<Optional<? super T>>> changed = new PendingChanged<>();
    whenSet.forEach(changed::whenSet);

    Optional[] values = new Optional[deferredValues.size()];

    IntStream.range(0, deferredValues.size())
        .forEach(
            (i) -> {
              DeferredChanged<? extends T> deferredItem = deferredValues.get(i);
              deferredItem.whenSet(
                  (value) -> {
                    values[i] = value;
                    log.debug(
                        "Collected deferred item set this={}, item={}, values = {}",
                        this,
                        DeferredLogger.logValue(value),
                        DeferredLogger.logValue(values));
                    if (isFull(values)) {
                      log.debug(
                          "Collected deferred triggering complete this={}, values = {}",
                          this,
                          DeferredLogger.logValue(values));
                      changed.setValue(Optional.of(Arrays.asList(values)));
                    }
                  });
            });

    return changed;
  }

  public <V extends Changed> DeferredChanged<V> buildIsChanged(V changed) {
    return build().flatMap((values) -> (DeferredChanged<V>) DeferredChanged.of(isChanged(changed)));
  }

  private static boolean isFull(Object[] values) {
    for (Object value : values) {
      if (value == null) {
        return false;
      }
    }
    return true;
  }
}
