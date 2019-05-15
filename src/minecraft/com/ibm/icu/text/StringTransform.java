package com.ibm.icu.text;

public abstract interface StringTransform
  extends Transform<String, String>
{
  public abstract String transform(String paramString);
}
