package com.ibm.icu.impl;




public abstract interface ICUCache<K, V>
{
  public static final int SOFT = 0;
  


  public static final int WEAK = 1;
  


  public static final Object NULL = new Object();
  
  public abstract void clear();
  
  public abstract void put(K paramK, V paramV);
  
  public abstract V get(Object paramObject);
}
