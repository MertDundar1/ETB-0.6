package com.ibm.icu.text;

import java.text.ParsePosition;

public abstract interface SymbolTable
{
  public static final char SYMBOL_REF = '$';
  
  public abstract char[] lookup(String paramString);
  
  public abstract UnicodeMatcher lookupMatcher(int paramInt);
  
  public abstract String parseReference(String paramString, ParsePosition paramParsePosition, int paramInt);
}
