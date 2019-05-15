package com.ibm.icu.impl;












public class CalendarCache
{
  public CalendarCache()
  {
    makeArrays(arraySize);
  }
  
  private void makeArrays(int newSize) {
    keys = new long[newSize];
    values = new long[newSize];
    
    for (int i = 0; i < newSize; i++) {
      values[i] = EMPTY;
    }
    arraySize = newSize;
    threshold = ((int)(arraySize * 0.75D));
    size = 0;
  }
  


  public synchronized long get(long key)
  {
    return values[findIndex(key)];
  }
  



  public synchronized void put(long key, long value)
  {
    if (size >= threshold) {
      rehash();
    }
    int index = findIndex(key);
    
    keys[index] = key;
    values[index] = value;
    size += 1;
  }
  
  private final int findIndex(long key) {
    int index = hash(key);
    int delta = 0;
    
    while ((values[index] != EMPTY) && (keys[index] != key))
    {
      if (delta == 0) {
        delta = hash2(key);
      }
      index = (index + delta) % arraySize;
    }
    return index;
  }
  
  private void rehash()
  {
    int oldSize = arraySize;
    long[] oldKeys = keys;
    long[] oldValues = values;
    
    if (pIndex < primes.length - 1) {
      arraySize = primes[(++pIndex)];
    } else {
      arraySize = (arraySize * 2 + 1);
    }
    size = 0;
    
    makeArrays(arraySize);
    for (int i = 0; i < oldSize; i++) {
      if (oldValues[i] != EMPTY) {
        put(oldKeys[i], oldValues[i]);
      }
    }
  }
  






  private final int hash(long key)
  {
    int h = (int)((key * 15821L + 1L) % arraySize);
    if (h < 0) {
      h += arraySize;
    }
    return h;
  }
  
  private final int hash2(long key) {
    return arraySize - 2 - (int)(key % (arraySize - 2));
  }
  
  private static final int[] primes = { 61, 127, 509, 1021, 2039, 4093, 8191, 16381, 32749, 65521, 131071, 262139 };
  



  private int pIndex = 0;
  private int size = 0;
  private int arraySize = primes[pIndex];
  private int threshold = arraySize * 3 / 4;
  
  private long[] keys = new long[arraySize];
  private long[] values = new long[arraySize];
  



  public static long EMPTY = Long.MIN_VALUE;
}
