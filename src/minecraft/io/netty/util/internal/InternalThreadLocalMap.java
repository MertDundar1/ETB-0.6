package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;






















public final class InternalThreadLocalMap
  extends UnpaddedInternalThreadLocalMap
{
  public static final Object UNSET = new Object();
  public long rp1;
  
  public static InternalThreadLocalMap getIfSet() { Thread thread = Thread.currentThread();
    InternalThreadLocalMap threadLocalMap;
    InternalThreadLocalMap threadLocalMap; if ((thread instanceof FastThreadLocalThread)) {
      threadLocalMap = ((FastThreadLocalThread)thread).threadLocalMap();
    } else {
      ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
      InternalThreadLocalMap threadLocalMap; if (slowThreadLocalMap == null) {
        threadLocalMap = null;
      } else {
        threadLocalMap = (InternalThreadLocalMap)slowThreadLocalMap.get();
      }
    }
    return threadLocalMap; }
  
  public long rp2;
  
  public static InternalThreadLocalMap get() { Thread thread = Thread.currentThread();
    if ((thread instanceof FastThreadLocalThread)) {
      return fastGet((FastThreadLocalThread)thread);
    }
    return slowGet();
  }
  
  public long rp3;
  public long rp4;
  private static InternalThreadLocalMap fastGet(FastThreadLocalThread thread) { InternalThreadLocalMap threadLocalMap = thread.threadLocalMap();
    if (threadLocalMap == null) {
      thread.setThreadLocalMap(threadLocalMap = new InternalThreadLocalMap());
    }
    return threadLocalMap;
  }
  
  private static InternalThreadLocalMap slowGet() {
    ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
    if (slowThreadLocalMap == null) {
      UnpaddedInternalThreadLocalMap.slowThreadLocalMap = slowThreadLocalMap = new ThreadLocal();
    }
    

    InternalThreadLocalMap ret = (InternalThreadLocalMap)slowThreadLocalMap.get();
    if (ret == null) {
      ret = new InternalThreadLocalMap();
      slowThreadLocalMap.set(ret);
    }
    return ret;
  }
  
  public static void remove() {
    Thread thread = Thread.currentThread();
    if ((thread instanceof FastThreadLocalThread)) {
      ((FastThreadLocalThread)thread).setThreadLocalMap(null);
    } else {
      ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
      if (slowThreadLocalMap != null) {
        slowThreadLocalMap.remove();
      }
    }
  }
  
  public static void destroy() {
    slowThreadLocalMap = null;
  }
  
  public static int nextVariableIndex() {
    int index = nextIndex.getAndIncrement();
    if (index < 0) {
      nextIndex.decrementAndGet();
      throw new IllegalStateException("too many thread-local indexed variables");
    }
    return index;
  }
  
  public static int lastVariableIndex() {
    return nextIndex.get() - 1;
  }
  
  public long rp5;
  public long rp6;
  public long rp7;
  public long rp8;
  public long rp9;
  private InternalThreadLocalMap() { super(newIndexedVariableTable()); }
  

  private static Object[] newIndexedVariableTable() {
    Object[] array = new Object[32];
    Arrays.fill(array, UNSET);
    return array;
  }
  
  public int size() {
    int count = 0;
    
    if (futureListenerStackDepth != 0) {
      count++;
    }
    if (localChannelReaderStackDepth != 0) {
      count++;
    }
    if (handlerSharableCache != null) {
      count++;
    }
    if (counterHashCode != null) {
      count++;
    }
    if (random != null) {
      count++;
    }
    if (typeParameterMatcherGetCache != null) {
      count++;
    }
    if (typeParameterMatcherFindCache != null) {
      count++;
    }
    if (stringBuilder != null) {
      count++;
    }
    if (charsetEncoderCache != null) {
      count++;
    }
    if (charsetDecoderCache != null) {
      count++;
    }
    
    for (Object o : indexedVariables) {
      if (o != UNSET) {
        count++;
      }
    }
    


    return count - 1;
  }
  
  public StringBuilder stringBuilder() {
    StringBuilder builder = stringBuilder;
    if (builder == null) {
      stringBuilder = (builder = new StringBuilder(512));
    } else {
      builder.setLength(0);
    }
    return builder;
  }
  
  public Map<Charset, CharsetEncoder> charsetEncoderCache() {
    Map<Charset, CharsetEncoder> cache = charsetEncoderCache;
    if (cache == null) {
      charsetEncoderCache = (cache = new IdentityHashMap());
    }
    return cache;
  }
  
  public Map<Charset, CharsetDecoder> charsetDecoderCache() {
    Map<Charset, CharsetDecoder> cache = charsetDecoderCache;
    if (cache == null) {
      charsetDecoderCache = (cache = new IdentityHashMap());
    }
    return cache;
  }
  
  public int futureListenerStackDepth() {
    return futureListenerStackDepth;
  }
  
  public void setFutureListenerStackDepth(int futureListenerStackDepth) {
    this.futureListenerStackDepth = futureListenerStackDepth;
  }
  
  public ThreadLocalRandom random() {
    ThreadLocalRandom r = random;
    if (r == null) {
      random = (r = new ThreadLocalRandom());
    }
    return r;
  }
  
  public Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache() {
    Map<Class<?>, TypeParameterMatcher> cache = typeParameterMatcherGetCache;
    if (cache == null) {
      typeParameterMatcherGetCache = (cache = new IdentityHashMap());
    }
    return cache;
  }
  
  public Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache() {
    Map<Class<?>, Map<String, TypeParameterMatcher>> cache = typeParameterMatcherFindCache;
    if (cache == null) {
      typeParameterMatcherFindCache = (cache = new IdentityHashMap());
    }
    return cache;
  }
  
  public IntegerHolder counterHashCode() {
    return counterHashCode;
  }
  
  public void setCounterHashCode(IntegerHolder counterHashCode) {
    this.counterHashCode = counterHashCode;
  }
  
  public Map<Class<?>, Boolean> handlerSharableCache() {
    Map<Class<?>, Boolean> cache = handlerSharableCache;
    if (cache == null)
    {
      handlerSharableCache = (cache = new WeakHashMap(4));
    }
    return cache;
  }
  
  public int localChannelReaderStackDepth() {
    return localChannelReaderStackDepth;
  }
  
  public void setLocalChannelReaderStackDepth(int localChannelReaderStackDepth) {
    this.localChannelReaderStackDepth = localChannelReaderStackDepth;
  }
  
  public Object indexedVariable(int index) {
    Object[] lookup = indexedVariables;
    return index < lookup.length ? lookup[index] : UNSET;
  }
  


  public boolean setIndexedVariable(int index, Object value)
  {
    Object[] lookup = indexedVariables;
    if (index < lookup.length) {
      Object oldValue = lookup[index];
      lookup[index] = value;
      return oldValue == UNSET;
    }
    expandIndexedVariableTableAndSet(index, value);
    return true;
  }
  
  private void expandIndexedVariableTableAndSet(int index, Object value)
  {
    Object[] oldArray = indexedVariables;
    int oldCapacity = oldArray.length;
    int newCapacity = index;
    newCapacity |= newCapacity >>> 1;
    newCapacity |= newCapacity >>> 2;
    newCapacity |= newCapacity >>> 4;
    newCapacity |= newCapacity >>> 8;
    newCapacity |= newCapacity >>> 16;
    newCapacity++;
    
    Object[] newArray = Arrays.copyOf(oldArray, newCapacity);
    Arrays.fill(newArray, oldCapacity, newArray.length, UNSET);
    newArray[index] = value;
    indexedVariables = newArray;
  }
  
  public Object removeIndexedVariable(int index) {
    Object[] lookup = indexedVariables;
    if (index < lookup.length) {
      Object v = lookup[index];
      lookup[index] = UNSET;
      return v;
    }
    return UNSET;
  }
  
  public boolean isIndexedVariableSet(int index)
  {
    Object[] lookup = indexedVariables;
    return (index < lookup.length) && (lookup[index] != UNSET);
  }
}
