package io.netty.util.collection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;































public class IntObjectHashMap<V>
  implements IntObjectMap<V>, Iterable<IntObjectMap.Entry<V>>
{
  private static final int DEFAULT_CAPACITY = 11;
  private static final float DEFAULT_LOAD_FACTOR = 0.5F;
  private static final Object NULL_VALUE = new Object();
  
  private int maxSize;
  
  private final float loadFactor;
  
  private int[] keys;
  
  private V[] values;
  private int size;
  
  public IntObjectHashMap()
  {
    this(11, 0.5F);
  }
  
  public IntObjectHashMap(int initialCapacity) {
    this(initialCapacity, 0.5F);
  }
  
  public IntObjectHashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 1) {
      throw new IllegalArgumentException("initialCapacity must be >= 1");
    }
    if ((loadFactor <= 0.0F) || (loadFactor > 1.0F))
    {

      throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
    }
    
    this.loadFactor = loadFactor;
    

    int capacity = adjustCapacity(initialCapacity);
    

    keys = new int[capacity];
    
    V[] temp = (Object[])new Object[capacity];
    values = temp;
    

    maxSize = calcMaxSize(capacity);
  }
  
  private static <T> T toExternal(T value) {
    return value == NULL_VALUE ? null : value;
  }
  
  private static <T> T toInternal(T value)
  {
    return value == null ? NULL_VALUE : value;
  }
  
  public V get(int key)
  {
    int index = indexOf(key);
    return index == -1 ? null : toExternal(values[index]);
  }
  
  public V put(int key, V value)
  {
    int startIndex = hashIndex(key);
    int index = startIndex;
    do
    {
      if (values[index] == null)
      {
        keys[index] = key;
        values[index] = toInternal(value);
        growSize();
        return null; }
      if (keys[index] == key)
      {
        V previousValue = values[index];
        values[index] = toInternal(value);
        return toExternal(previousValue);
      }
      
    }
    while ((index = probeNext(index)) != startIndex);
    
    throw new IllegalStateException("Unable to insert");
  }
  

  private int probeNext(int index)
  {
    return index == values.length - 1 ? 0 : index + 1;
  }
  
  public void putAll(IntObjectMap<V> sourceMap)
  {
    if ((sourceMap instanceof IntObjectHashMap))
    {
      IntObjectHashMap<V> source = (IntObjectHashMap)sourceMap;
      for (int i = 0; i < values.length; i++) {
        V sourceValue = values[i];
        if (sourceValue != null) {
          put(keys[i], sourceValue);
        }
      }
      return;
    }
    

    for (IntObjectMap.Entry<V> entry : sourceMap.entries()) {
      put(entry.key(), entry.value());
    }
  }
  
  public V remove(int key)
  {
    int index = indexOf(key);
    if (index == -1) {
      return null;
    }
    
    V prev = values[index];
    removeAt(index);
    return toExternal(prev);
  }
  
  public int size()
  {
    return size;
  }
  
  public boolean isEmpty()
  {
    return size == 0;
  }
  
  public void clear()
  {
    Arrays.fill(keys, 0);
    Arrays.fill(values, null);
    size = 0;
  }
  
  public boolean containsKey(int key)
  {
    return indexOf(key) >= 0;
  }
  
  public boolean containsValue(V value)
  {
    V v = toInternal(value);
    for (int i = 0; i < values.length; i++)
    {
      if ((values[i] != null) && (values[i].equals(v))) {
        return true;
      }
    }
    return false;
  }
  
  public Iterable<IntObjectMap.Entry<V>> entries()
  {
    return this;
  }
  
  public Iterator<IntObjectMap.Entry<V>> iterator()
  {
    return new IteratorImpl(null);
  }
  
  public int[] keys()
  {
    int[] outKeys = new int[size()];
    int targetIx = 0;
    for (int i = 0; i < values.length; i++) {
      if (values[i] != null) {
        outKeys[(targetIx++)] = keys[i];
      }
    }
    return outKeys;
  }
  

  public V[] values(Class<V> clazz)
  {
    V[] outValues = (Object[])Array.newInstance(clazz, size());
    int targetIx = 0;
    for (int i = 0; i < values.length; i++) {
      if (values[i] != null) {
        outValues[(targetIx++)] = values[i];
      }
    }
    return outValues;
  }
  



  public int hashCode()
  {
    int hash = size;
    for (int i = 0; i < keys.length; i++)
    {






      hash ^= keys[i];
    }
    return hash;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!(obj instanceof IntObjectMap)) {
      return false;
    }
    
    IntObjectMap other = (IntObjectMap)obj;
    if (size != other.size()) {
      return false;
    }
    for (int i = 0; i < values.length; i++) {
      V value = values[i];
      if (value != null) {
        int key = keys[i];
        Object otherValue = other.get(key);
        if (value == NULL_VALUE) {
          if (otherValue != null) {
            return false;
          }
        } else if (!value.equals(otherValue)) {
          return false;
        }
      }
    }
    return true;
  }
  





  private int indexOf(int key)
  {
    int startIndex = hashIndex(key);
    int index = startIndex;
    do
    {
      if (values[index] == null)
      {
        return -1; }
      if (key == keys[index]) {
        return index;
      }
      
    }
    while ((index = probeNext(index)) != startIndex);
    return -1;
  }
  




  private int hashIndex(int key)
  {
    return key % keys.length;
  }
  


  private void growSize()
  {
    size += 1;
    
    if (size > maxSize)
    {

      rehash(adjustCapacity((int)Math.min(keys.length * 2.0D, 2.147483639E9D)));
    } else if (size == keys.length)
    {

      rehash(keys.length);
    }
  }
  


  private static int adjustCapacity(int capacity)
  {
    return capacity | 0x1;
  }
  





  private void removeAt(int index)
  {
    size -= 1;
    

    keys[index] = 0;
    values[index] = null;
    





    int nextFree = index;
    for (int i = probeNext(index); values[i] != null; i = probeNext(i)) {
      int bucket = hashIndex(keys[i]);
      if (((i < bucket) && ((bucket <= nextFree) || (nextFree <= i))) || ((bucket <= nextFree) && (nextFree <= i)))
      {

        keys[nextFree] = keys[i];
        values[nextFree] = values[i];
        
        keys[i] = 0;
        values[i] = null;
        nextFree = i;
      }
    }
  }
  



  private int calcMaxSize(int capacity)
  {
    int upperBound = capacity - 1;
    return Math.min(upperBound, (int)(capacity * loadFactor));
  }
  




  private void rehash(int newCapacity)
  {
    int[] oldKeys = keys;
    V[] oldVals = values;
    
    keys = new int[newCapacity];
    
    V[] temp = (Object[])new Object[newCapacity];
    values = temp;
    
    maxSize = calcMaxSize(newCapacity);
    

    for (int i = 0; i < oldVals.length; i++) {
      V oldVal = oldVals[i];
      if (oldVal != null)
      {

        int oldKey = oldKeys[i];
        int startIndex = hashIndex(oldKey);
        int index = startIndex;
        for (;;)
        {
          if (values[index] == null) {
            keys[index] = oldKey;
            values[index] = toInternal(oldVal);
            break;
          }
          

          index = probeNext(index);
        }
      }
    }
  }
  

  private final class IteratorImpl
    implements Iterator<IntObjectMap.Entry<V>>, IntObjectMap.Entry<V>
  {
    private int prevIndex = -1;
    private int nextIndex = -1;
    private int entryIndex = -1;
    
    private IteratorImpl() {}
    
    private void scanNext() { while (++nextIndex != values.length) { if (values[nextIndex] != null) {
          break;
        }
      }
    }
    
    public boolean hasNext()
    {
      if (nextIndex == -1) {
        scanNext();
      }
      return nextIndex < keys.length;
    }
    
    public IntObjectMap.Entry<V> next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      
      prevIndex = nextIndex;
      scanNext();
      

      entryIndex = prevIndex;
      return this;
    }
    
    public void remove()
    {
      if (prevIndex < 0) {
        throw new IllegalStateException("next must be called before each remove.");
      }
      IntObjectHashMap.this.removeAt(prevIndex);
      prevIndex = -1;
    }
    



    public int key()
    {
      return keys[entryIndex];
    }
    
    public V value()
    {
      return IntObjectHashMap.toExternal(values[entryIndex]);
    }
    
    public void setValue(V value)
    {
      values[entryIndex] = IntObjectHashMap.toInternal(value);
    }
  }
  
  public String toString()
  {
    if (size == 0) {
      return "{}";
    }
    StringBuilder sb = new StringBuilder(4 * size);
    for (int i = 0; i < values.length; i++) {
      V value = values[i];
      if (value != null) {
        sb.append(sb.length() == 0 ? "{" : ", ");
        sb.append(keys[i]).append('=').append(value == this ? "(this Map)" : value);
      }
    }
    return '}';
  }
}
