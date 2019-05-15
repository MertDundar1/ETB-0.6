package com.ibm.icu.text;

import com.ibm.icu.util.ULocale;

public abstract interface RbnfLenientScannerProvider
{
  public abstract RbnfLenientScanner get(ULocale paramULocale, String paramString);
}
