package com.ibm.icu.text;

import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Type;
import java.text.Format;












































public abstract class UFormat
  extends Format
{
  private static final long serialVersionUID = -4964390515840164416L;
  private ULocale validLocale;
  private ULocale actualLocale;
  
  public UFormat() {}
  
  public final ULocale getLocale(ULocale.Type type)
  {
    return type == ULocale.ACTUAL_LOCALE ? actualLocale : validLocale;
  }
  

















  final void setLocale(ULocale valid, ULocale actual)
  {
    if ((valid == null ? 1 : 0) != (actual == null ? 1 : 0))
    {
      throw new IllegalArgumentException();
    }
    


    validLocale = valid;
    actualLocale = actual;
  }
}
