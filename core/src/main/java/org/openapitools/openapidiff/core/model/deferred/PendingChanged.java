package org.openapitools.openapidiff.core.model.deferred;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PendingChanged<T> implements DeferredChanged<T> {
  private static final Logger log = LoggerFactory.getLogger(PendingChanged.class);

  private final List<Consumer<T>> ifPresentConsumers = new ArrayList<>();
  private final List<Consumer<Optional<T>>> whenSetConsumers = new ArrayList<>();

  @Nullable private T value;
  private Optional<T> valueOptional = Optional.empty();
  private boolean valueIsPresent;
  private boolean valueSet;

  private static final AtomicInteger deferredCounter = new AtomicInteger();
  private static final AtomicInteger resolvedCounter = new AtomicInteger();

  @Override
  public void ifPresent(Consumer<T> consumer) {
    if (valueSet) {
      if (valueIsPresent) {
        consumer.accept(value);
      }
    } else {
      ifPresentConsumers.add(consumer);
    }
  }

  public void setValue(Optional<T> value) {
    if (!valueSet) {
      this.valueSet = true;
      this.valueIsPresent = value.isPresent();
      this.value = value.orElse(null);
      this.valueOptional = value;

      log.debug("set {}", DeferredLogger.logValue(this.value));

      if (this.valueIsPresent) {
        ifPresentConsumers.forEach(c -> c.accept(this.value));
      }

      whenSetConsumers.forEach(c -> c.accept(this.valueOptional));

    } else {
      throw new IllegalStateException(
          "PendingChanged may not be set more than once. Value was already set.");
    }
  }

  public boolean isPresent() {
    return valueSet && valueIsPresent;
  }

  public T get() {
    return valueOptional.get();
  }

  public boolean isValueSet() {
    return valueSet;
  }

  public void whenSet(Consumer<Optional<T>> consumer) {
    if (valueSet) {
      consumer.accept(valueOptional);
    } else {
      whenSetConsumers.add(consumer);
    }
  }

  public <Q> DeferredChanged<Q> map(Function<Optional<T>, Q> function) {
    return mapOptional(v -> Optional.ofNullable(function.apply(v)));
  }

  public <Q> DeferredChanged<Q> mapOptional(Function<Optional<T>, Optional<Q>> function) {
    if (valueSet) {
      Optional<Q> result = function.apply(this.valueOptional);
      log.debug(
          "map resolved {} {} -> {}",
          function,
          DeferredLogger.logValue(this.value),
          DeferredLogger.logValue(result));
      return new RealizedChanged<>(result);
    } else {
      final PendingChanged<Q> mappedChanged = new PendingChanged<>();
      log.debug("map deferred {} ? -> ?", function);
      deferredCounter.incrementAndGet();
      whenSet(
          value -> {
            Optional<Q> result = function.apply(this.valueOptional);
            log.debug(
                "map resolved {} {} -> {}",
                function,
                DeferredLogger.logValue(this.value),
                DeferredLogger.logValue(result));
            resolvedCounter.incrementAndGet();
            mappedChanged.setValue(result);
          });
      return mappedChanged;
    }
  }

  public <Q> DeferredChanged<Q> flatMap(Function<Optional<T>, DeferredChanged<Q>> function) {
    if (valueSet) {
      DeferredChanged<Q> nextDeferred = function.apply(this.valueOptional);
      log.debug("flat map deferred {} {} -> ?", function, DeferredLogger.logValue(this.value));
      deferredCounter.incrementAndGet();
      nextDeferred.whenSet(
          nextValue -> {
            log.debug(
                "flat map resolved {} {} -> {}",
                function,
                DeferredLogger.logValue(this.value),
                DeferredLogger.logValue(nextValue));
            resolvedCounter.incrementAndGet();
          });
      return nextDeferred;
    } else {
      final PendingChanged<Q> mappedChanged = new PendingChanged<>();
      log.debug("flat map deferred {} ? -> ?", function);
      deferredCounter.incrementAndGet();
      whenSet(
          value -> {
            DeferredChanged<Q> nextDeferred = function.apply(value);
            nextDeferred.whenSet(
                nextValue -> {
                  log.debug(
                      "flat map deferred {} {} -> {}",
                      function,
                      DeferredLogger.logValue(this.value),
                      DeferredLogger.logValue(nextValue));
                  resolvedCounter.incrementAndGet();
                  mappedChanged.setValue(nextValue);
                });
            log.debug("flat map resolved {} {} -> ?", function, DeferredLogger.logValue(value));
          });
      return mappedChanged;
    }
  }

  @Override
  public String toString() {
    return "PendingChanged{"
        + "value="
        + DeferredLogger.logValue(value)
        + ", valueSet="
        + valueSet
        + ", ifPresentConsumers.size="
        + ifPresentConsumers.size()
        + ", whenSetConsumers.size="
        + whenSetConsumers.size()
        + '}';
  }

  public static void logResolved() {
    int deferred = deferredCounter.get();
    int resolved = resolvedCounter.get();
    log.debug(
        "Outstanding: {}  Deferred: {}  Resolved {}", deferred - resolved, deferred, resolved);
  }
}
