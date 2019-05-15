package com.ibm.icu.text;

public abstract interface Transform<S, D>
{
  public abstract D transform(S paramS);
}
