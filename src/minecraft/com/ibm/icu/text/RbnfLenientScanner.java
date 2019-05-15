package com.ibm.icu.text;

public abstract interface RbnfLenientScanner
{
  public abstract boolean allIgnorable(String paramString);
  
  public abstract int prefixLength(String paramString1, String paramString2);
  
  public abstract int[] findText(String paramString1, String paramString2, int paramInt);
}
