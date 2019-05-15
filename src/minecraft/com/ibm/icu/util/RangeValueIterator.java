package com.ibm.icu.util;

public abstract interface RangeValueIterator
{
  public abstract boolean next(Element paramElement);
  
  public abstract void reset();
  
  public static class Element
  {
    public int start;
    public int limit;
    public int value;
    
    public Element() {}
  }
}
