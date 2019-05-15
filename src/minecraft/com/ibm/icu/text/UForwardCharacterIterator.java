package com.ibm.icu.text;

public abstract interface UForwardCharacterIterator
{
  public static final int DONE = -1;
  
  public abstract int next();
  
  public abstract int nextCodePoint();
}
