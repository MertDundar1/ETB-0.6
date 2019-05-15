package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;



































































@GwtCompatible(emulated=true)
public abstract class CacheLoader<K, V>
{
  protected CacheLoader() {}
  
  public abstract V load(K paramK)
    throws Exception;
  
  @GwtIncompatible("Futures")
  public ListenableFuture<V> reload(K key, V oldValue)
    throws Exception
  {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(oldValue);
    return Futures.immediateFuture(load(key));
  }
  






















  public Map<K, V> loadAll(Iterable<? extends K> keys)
    throws Exception
  {
    throw new UnsupportedLoadingOperationException();
  }
  







  @Beta
  public static <K, V> CacheLoader<K, V> from(Function<K, V> function)
  {
    return new FunctionToCacheLoader(function);
  }
  
  private static final class FunctionToCacheLoader<K, V> extends CacheLoader<K, V> implements Serializable {
    private final Function<K, V> computingFunction;
    private static final long serialVersionUID = 0L;
    
    public FunctionToCacheLoader(Function<K, V> computingFunction) {
      this.computingFunction = ((Function)Preconditions.checkNotNull(computingFunction));
    }
    
    public V load(K key)
    {
      return computingFunction.apply(Preconditions.checkNotNull(key));
    }
  }
  










  @Beta
  public static <V> CacheLoader<Object, V> from(Supplier<V> supplier)
  {
    return new SupplierToCacheLoader(supplier);
  }
  









  @Beta
  @GwtIncompatible("Executor + Futures")
  public static <K, V> CacheLoader<K, V> asyncReloading(CacheLoader<K, V> loader, final Executor executor)
  {
    Preconditions.checkNotNull(loader);
    Preconditions.checkNotNull(executor);
    new CacheLoader()
    {
      public V load(K key) throws Exception {
        return val$loader.load(key);
      }
      
      public ListenableFuture<V> reload(final K key, final V oldValue) throws Exception
      {
        ListenableFutureTask<V> task = ListenableFutureTask.create(new Callable()
        {
          public V call() throws Exception {
            return val$loader.reload(key, oldValue).get();
          }
        });
        executor.execute(task);
        return task;
      }
      
      public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception
      {
        return val$loader.loadAll(keys);
      }
    };
  }
  
  private static final class SupplierToCacheLoader<V> extends CacheLoader<Object, V> implements Serializable {
    private final Supplier<V> computingSupplier;
    private static final long serialVersionUID = 0L;
    
    public SupplierToCacheLoader(Supplier<V> computingSupplier) {
      this.computingSupplier = ((Supplier)Preconditions.checkNotNull(computingSupplier));
    }
    
    public V load(Object key)
    {
      Preconditions.checkNotNull(key);
      return computingSupplier.get();
    }
  }
  
  static final class UnsupportedLoadingOperationException
    extends UnsupportedOperationException
  {
    UnsupportedLoadingOperationException() {}
  }
  
  public static final class InvalidCacheLoadException
    extends RuntimeException
  {
    public InvalidCacheLoadException(String message)
    {
      super();
    }
  }
}
