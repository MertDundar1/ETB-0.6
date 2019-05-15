package com.ibm.icu.util;

public abstract interface ValueIterator
{
  public abstract boolean next(Element paramElement);
  
  public abstract void reset();
  
  public abstract void setRange(int paramInt1, int paramInt2);
  
  public static final class Element
  {
    public int integer;
    public Object value;
    
    public Element() {}
  }
}
