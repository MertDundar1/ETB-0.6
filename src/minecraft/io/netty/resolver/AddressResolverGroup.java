package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;




















public abstract class AddressResolverGroup<T extends SocketAddress>
  implements Closeable
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(AddressResolverGroup.class);
  



  private final Map<EventExecutor, AddressResolver<T>> resolvers = new IdentityHashMap();
  



  protected AddressResolverGroup() {}
  



  public AddressResolver<T> getResolver(final EventExecutor executor)
  {
    if (executor == null) {
      throw new NullPointerException("executor");
    }
    
    if (executor.isShuttingDown()) {
      throw new IllegalStateException("executor not accepting a task");
    }
    

    synchronized (resolvers) {
      AddressResolver<T> r = (AddressResolver)resolvers.get(executor);
      if (r == null)
      {
        try {
          newResolver = newResolver(executor);
        } catch (Exception e) { AddressResolver<T> newResolver;
          throw new IllegalStateException("failed to create a new resolver", e);
        }
        final AddressResolver<T> newResolver;
        resolvers.put(executor, newResolver);
        executor.terminationFuture().addListener(new FutureListener()
        {
          public void operationComplete(Future<Object> future) throws Exception {
            synchronized (resolvers) {
              resolvers.remove(executor);
            }
            newResolver.close();
          }
          
        });
        r = newResolver;
      }
    }
    AddressResolver<T> r;
    return r;
  }
  




  protected abstract AddressResolver<T> newResolver(EventExecutor paramEventExecutor)
    throws Exception;
  



  public void close()
  {
    synchronized (resolvers) {
      AddressResolver<T>[] rArray = (AddressResolver[])resolvers.values().toArray(new AddressResolver[resolvers.size()]);
      resolvers.clear();
    }
    AddressResolver<T>[] rArray;
    for (AddressResolver<T> r : rArray) {
      try {
        r.close();
      } catch (Throwable t) {
        logger.warn("Failed to close a resolver:", t);
      }
    }
  }
}
