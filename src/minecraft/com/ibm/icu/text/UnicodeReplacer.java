package com.ibm.icu.text;

abstract interface UnicodeReplacer
{
  public abstract int replace(Replaceable paramReplaceable, int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  public abstract String toReplacerPattern(boolean paramBoolean);
  
  public abstract void addReplacementSetTo(UnicodeSet paramUnicodeSet);
}
