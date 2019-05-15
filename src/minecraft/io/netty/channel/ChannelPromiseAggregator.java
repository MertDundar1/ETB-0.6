package io.netty.channel;

import java.util.LinkedHashSet;
import java.util.Set;


























public final class ChannelPromiseAggregator
  implements ChannelFutureListener
{
  private final ChannelPromise aggregatePromise;
  private Set<ChannelPromise> pendingPromises;
  
  public ChannelPromiseAggregator(ChannelPromise aggregatePromise)
  {
    if (aggregatePromise == null) {
      throw new NullPointerException("aggregatePromise");
    }
    this.aggregatePromise = aggregatePromise;
  }
  


  public ChannelPromiseAggregator add(ChannelPromise... promises)
  {
    if (promises == null) {
      throw new NullPointerException("promises");
    }
    if (promises.length == 0) {
      return this;
    }
    synchronized (this) {
      if (pendingPromises == null) { int size;
        int size;
        if (promises.length > 1) {
          size = promises.length;
        } else {
          size = 2;
        }
        pendingPromises = new LinkedHashSet(size);
      }
      for (ChannelPromise p : promises)
        if (p != null)
        {

          pendingPromises.add(p);
          p.addListener(this);
        }
    }
    return this;
  }
  
  public synchronized void operationComplete(ChannelFuture future) throws Exception
  {
    if (pendingPromises == null) {
      aggregatePromise.setSuccess();
    } else {
      pendingPromises.remove(future);
      if (!future.isSuccess()) {
        aggregatePromise.setFailure(future.cause());
        for (ChannelPromise pendingFuture : pendingPromises) {
          pendingFuture.setFailure(future.cause());
        }
      }
      else if (pendingPromises.isEmpty()) {
        aggregatePromise.setSuccess();
      }
    }
  }
}
