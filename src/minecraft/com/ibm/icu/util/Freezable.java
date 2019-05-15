package com.ibm.icu.util;

public abstract interface Freezable<T>
  extends Cloneable
{
  public abstract boolean isFrozen();
  
  public abstract T freeze();
  
  public abstract T cloneAsThawed();
}
