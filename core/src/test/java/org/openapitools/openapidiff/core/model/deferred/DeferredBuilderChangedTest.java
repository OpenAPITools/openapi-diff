package org.openapitools.openapidiff.core.model.deferred;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.model.schema.ChangedReadOnly;
import org.openapitools.openapidiff.core.model.schema.ChangedWriteOnly;

public class DeferredBuilderChangedTest {

  private PendingChanged<ChangedReadOnly> changed;
  private Optional<ChangedReadOnly> whenSet;
  private ChangedWriteOnly mappedValue;
  private Optional<?> chainedValue;

  @BeforeEach
  public void beforeEach() {
    whenSet = Optional.empty();
    mappedValue = null;
    chainedValue = Optional.empty();
    changed = new PendingChanged<>();
  }

  @Test
  public void testPendingChangedValueSetBeforeListeners() {
    PendingChanged<String> changed = new PendingChanged<>();
    changed.setValue(Optional.of("Hello"));
    ChangedAssertion assertion = new ChangedAssertion(changed);
    assertion.assertSet(true);
  }

  @Test
  public void testPendingChangedValueSetAfterListeners() {
    PendingChanged<String> changed = new PendingChanged<>();
    ChangedAssertion assertion = new ChangedAssertion(changed);
    changed.setValue(Optional.of("Hello"));
    assertion.assertSet(true);
  }

  @Test
  public void testPendingChangedValueEMpty() {
    PendingChanged<String> changed = new PendingChanged<>();
    ChangedAssertion assertion = new ChangedAssertion(changed);
    changed.setValue(Optional.empty());
    assertion.assertSet(false);
  }

  @Test
  public void testRealizedChange() {
    RealizedChanged<String> changed = new RealizedChanged<>("hello");
    ChangedAssertion assertion = new ChangedAssertion(changed);
    assertion.assertSet(true);
  }

  @Test
  public void testRealizedChangeEmpty() {
    RealizedChanged<String> changed = new RealizedChanged<>(Optional.empty());
    ChangedAssertion assertion = new ChangedAssertion(changed);
    assertion.assertSet(false);
  }

  private static class ChangedAssertion {
    AtomicBoolean map = new AtomicBoolean(false);
    AtomicBoolean flatMap = new AtomicBoolean(false);
    AtomicBoolean mapOptional = new AtomicBoolean(false);
    AtomicBoolean whenSet = new AtomicBoolean(false);
    AtomicBoolean ifPresent = new AtomicBoolean(false);

    public ChangedAssertion(DeferredChanged<?> changed) {
      changed.mapOptional(
          (value) -> {
            mapOptional.set(true);
            return Optional.empty();
          });
      changed.map(
          (value) -> {
            map.set(true);
            return Optional.empty();
          });
      changed.flatMap(
          (value) -> {
            flatMap.set(true);
            return DeferredChanged.empty();
          });
      changed.whenSet((value) -> whenSet.set(true));
      changed.ifPresent((value) -> ifPresent.set(true));
    }

    public void assertSet(boolean expectedIfPresent) {
      Assertions.assertTrue(mapOptional.get(), "mapOptional");
      Assertions.assertTrue(map.get(), "map");
      Assertions.assertTrue(flatMap.get(), "flatMap");
      Assertions.assertTrue(whenSet.get(), "whenSet");
      Assertions.assertEquals(expectedIfPresent, ifPresent.get(), "ifPresent");
    }

    public void assertNotSet() {
      Assertions.assertFalse(mapOptional.get(), "mapOptional");
      Assertions.assertFalse(map.get(), "map");
      Assertions.assertFalse(flatMap.get(), "flatMap");
      Assertions.assertFalse(whenSet.get(), "whenSet");
      Assertions.assertFalse(ifPresent.get(), "ifPresent");
    }
  }

  @Test
  public void testFlatMap() {
    PendingChanged<ChangedWriteOnly> flatMapPending = new PendingChanged<>();

    changed.whenSet((value) -> this.whenSet = value);

    DeferredChanged<ChangedWriteOnly> chainedChanged =
        changed.flatMap(
            (value) -> {
              System.out.println("Flatmap called");
              return flatMapPending;
            });
    chainedChanged.whenSet(value -> chainedValue = value);

    DeferredChanged<ChangedWriteOnly> mappedDeferred =
        changed.map(value -> new ChangedWriteOnly(false, true, null));
    mappedDeferred.ifPresent(v -> mappedValue = v);

    Assertions.assertFalse(whenSet.isPresent());
    Assertions.assertFalse(chainedValue.isPresent());
    Assertions.assertNull(mappedValue);

    changed.setValue(Optional.of(new ChangedReadOnly(false, true, null)));
    Assertions.assertTrue(whenSet.isPresent());
    Assertions.assertNotNull(mappedValue);
    Assertions.assertFalse(chainedValue.isPresent());

    flatMapPending.setValue(Optional.of(new ChangedWriteOnly(false, true, null)));
    Assertions.assertTrue(whenSet.isPresent());
    Assertions.assertTrue(chainedValue.isPresent());
  }

  @Test
  public void testDeferredBuilderEmpty() {
    DeferredBuilder<String> builder = new DeferredBuilder<>();
    ChangedAssertion builderAssertion = new ChangedAssertion(builder.build());
    builderAssertion.assertSet(false);
  }

  @Test
  public void testDeferredBuilderAllRealized() {
    DeferredBuilder<String> builder = new DeferredBuilder<>();
    builder.add(new RealizedChanged<>("hello"));
    builder.add(new RealizedChanged<>("bye"));
    ChangedAssertion builderAssertion = new ChangedAssertion(builder.build());
    builderAssertion.assertSet(true);
  }

  @Test
  public void testDeferredBuilderPending() {
    PendingChanged<String> changed = new PendingChanged<>();

    DeferredBuilder<String> builder = new DeferredBuilder<>();
    builder.add(new RealizedChanged<>("hello"));
    builder.add(changed);
    ChangedAssertion builderAssertion = new ChangedAssertion(builder.build());
    builderAssertion.assertNotSet();

    changed.setValue(Optional.of("hello"));
    builderAssertion.assertSet(true);
  }
}
