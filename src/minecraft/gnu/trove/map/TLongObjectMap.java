package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.util.Collection;
import java.util.Map;

public abstract interface TLongObjectMap<V>
{
  public abstract long getNoEntryKey();
  
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract boolean containsKey(long paramLong);
  
  public abstract boolean containsValue(Object paramObject);
  
  public abstract V get(long paramLong);
  
  public abstract V put(long paramLong, V paramV);
  
  public abstract V putIfAbsent(long paramLong, V paramV);
  
  public abstract V remove(long paramLong);
  
  public abstract void putAll(Map<? extends Long, ? extends V> paramMap);
  
  public abstract void putAll(TLongObjectMap<V> paramTLongObjectMap);
  
  public abstract void clear();
  
  public abstract TLongSet keySet();
  
  public abstract long[] keys();
  
  public abstract long[] keys(long[] paramArrayOfLong);
  
  public abstract Collection<V> valueCollection();
  
  public abstract V[] values();
  
  public abstract <T> T[] values(T[] paramArrayOfT);
  
  public abstract TLongObjectIterator<V> iterator();
  
  public abstract boolean forEachKey(TLongProcedure paramTLongProcedure);
  
  public abstract boolean forEachValue(TObjectProcedure<V> paramTObjectProcedure);
  
  public abstract boolean forEachEntry(TLongObjectProcedure<V> paramTLongObjectProcedure);
  
  public abstract void transformValues(TObjectFunction<V, V> paramTObjectFunction);
  
  public abstract boolean retainEntries(TLongObjectProcedure<V> paramTLongObjectProcedure);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
}
