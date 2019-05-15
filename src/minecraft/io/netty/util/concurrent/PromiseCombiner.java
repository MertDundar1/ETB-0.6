package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;















public final class PromiseCombiner
{
  private int expectedCount;
  private int doneCount;
  private boolean doneAdding;
  private Promise<Void> aggregatePromise;
  private Throwable cause;
  private final GenericFutureListener<Future<?>> listener = new GenericFutureListener()
  {
    public void operationComplete(Future<?> future) throws Exception {
      PromiseCombiner.access$004(PromiseCombiner.this);
      if ((!future.isSuccess()) && (cause == null)) {
        cause = future.cause();
      }
      if ((doneCount == expectedCount) && (doneAdding))
        PromiseCombiner.this.tryPromise();
    }
  };
  
  public PromiseCombiner() {}
  
  public void add(Promise promise) {
    checkAddAllowed();
    expectedCount += 1;
    promise.addListener(listener);
  }
  
  public void addAll(Promise... promises)
  {
    checkAddAllowed();
    expectedCount += promises.length;
    for (Promise promise : promises) {
      promise.addListener(listener);
    }
  }
  
  public void finish(Promise<Void> aggregatePromise) {
    if (doneAdding) {
      throw new IllegalStateException("Already finished");
    }
    doneAdding = true;
    this.aggregatePromise = ((Promise)ObjectUtil.checkNotNull(aggregatePromise, "aggregatePromise"));
    if (doneCount == expectedCount) {
      tryPromise();
    }
  }
  
  private boolean tryPromise() {
    return cause == null ? aggregatePromise.trySuccess(null) : aggregatePromise.tryFailure(cause);
  }
  
  private void checkAddAllowed() {
    if (doneAdding) {
      throw new IllegalStateException("Adding promises is not allowed after finished adding");
    }
  }
}
