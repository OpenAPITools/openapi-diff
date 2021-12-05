package org.openapitools.openapidiff.core.model.deferred;

import io.swagger.v3.oas.models.media.Schema;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.openapitools.openapidiff.core.compare.CacheKey;
import org.openapitools.openapidiff.core.compare.OpenApiDiff;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.ChangedSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeferredSchemaCache {
  private static final Logger log = LoggerFactory.getLogger(DeferredSchemaCache.class);

  private final Map<CacheKey, SchemaDiffOperation> cache = new LinkedHashMap<>();
  private final Queue<CacheKey> processingQueue = new ArrayDeque<>();

  private final OpenApiDiff openApiDiff;

  public DeferredSchemaCache(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
  }

  public SchemaDiffOperation addSchema(
      RecursiveSchemaSet refSet, CacheKey key, Schema left, Schema right) {
    if (!cache.containsKey(key)) {
      log.debug("Added schema cache {}", key);
      SchemaDiffOperation operation =
          new SchemaDiffOperation(openApiDiff, refSet, key, left, right);
      cache.put(key, operation);
      processingQueue.add(key);
      return operation;
    } else {
      return cache.get(key);
    }
  }

  public DeferredChanged<ChangedSchema> getOrAddSchema(
      RecursiveSchemaSet refSet, CacheKey key, Schema left, Schema right) {
    // don't allow recursive references to schemas
    if (refSet.contains(key)) {
      log.debug("getOrAddSchema recursive call aborted {} ", key);
      return DeferredChanged.empty();
    }

    refSet.put(key);
    SchemaDiffOperation operation;
    if (cache.containsKey(key)) {
      operation = cache.get(key);
      log.debug("getOrAddSchema cached {} {}", key, operation.diffResult);
    } else {
      operation = addSchema(refSet, key, left, right);
      log.debug("getOrAddSchema added {} {}", key, operation.diffResult);
    }
    return operation.diffResult;
  }

  public void process() {
    processSchemaQueue();
    //        while(! deferredOperations.isEmpty()) {
    //            processSchemaQueue();
    //            DeferredOperation op = deferredOperations.poll();
    //            if(op != null) {
    //                log.debug("Processing deferred {}", op);
    //                op.process();
    //            }
    //        }
  }

  public void processSchemaQueue() {
    PendingChanged.logResolved();
    while (!processingQueue.isEmpty()) {
      CacheKey key = processingQueue.poll();
      if (key != null) {
        log.debug("Processing schema {}", key);
        SchemaDiffOperation operation = cache.get(key);
        DeferredChanged<ChangedSchema> realValue =
            operation
                .openApiDiff
                .getSchemaDiff()
                .computeDiffForReal(
                    operation.refSet, operation.left, operation.right, key.getContext());
        operation.processed = true;
        realValue.whenSet(
            (value) -> {
              log.debug("Schema processed {} {}", key, DeferredLogger.logValue(value));
              operation.diffResult.setValue(value);
            });
        log.debug("Processing schema started {}", key);
      }
      PendingChanged.logResolved();
    }
  }

  public Collection<SchemaDiffOperation> getOperations() {
    return cache.values();
  }

  public List<ChangedSchema> getChangedSchemas() {
    return cache.values().stream()
        .filter(op -> op.processed && op.diffResult.isPresent())
        .map(op -> op.diffResult.get())
        .collect(Collectors.toList());
  }

  private static class DeferredOperation<T extends Changed> {
    private final PendingChanged<T> pending = new PendingChanged<>();
    private final Supplier<Optional<T>> supplier;

    public DeferredOperation(Supplier<Optional<T>> supplier) {
      this.supplier = supplier;
    }

    public void process() {
      pending.setValue(supplier.get());
    }
  }
}
