package com.ibm.icu.impl;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;



























public abstract class SoftCache<K, V, D>
  extends CacheBase<K, V, D>
{
  public SoftCache() {}
  
  public final V getInstance(K key, D data)
  {
    SettableSoftReference<V> valueRef = (SettableSoftReference)map.get(key);
    
    if (valueRef != null) {
      synchronized (valueRef) {
        value = ref.get();
        if (value != null) {
          return value;
        }
        

        value = createInstance(key, data);
        if (value != null) {
          ref = new SoftReference(value);
        }
        return value;
      }
    }
    

    V value = createInstance(key, data);
    if (value == null) {
      return null;
    }
    valueRef = (SettableSoftReference)map.putIfAbsent(key, new SettableSoftReference(value, null));
    if (valueRef == null)
    {
      return value;
    }
    


    return valueRef.setIfAbsent(value);
  }
  


  private static final class SettableSoftReference<V>
  {
    private SoftReference<V> ref;
    


    private SettableSoftReference(V value)
    {
      ref = new SoftReference(value);
    }
    





    private synchronized V setIfAbsent(V value)
    {
      V oldValue = ref.get();
      if (oldValue == null) {
        ref = new SoftReference(value);
        return value;
      }
      return oldValue;
    }
  }
  

  private ConcurrentHashMap<K, SettableSoftReference<V>> map = new ConcurrentHashMap();
}
