package com.ibm.icu.text;

import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;




























public abstract class MeasureFormat
  extends UFormat
{
  static final long serialVersionUID = -7182021401701778240L;
  
  /**
   * @deprecated
   */
  protected MeasureFormat() {}
  
  public static MeasureFormat getCurrencyFormat(ULocale locale)
  {
    return new CurrencyFormat(locale);
  }
  






  public static MeasureFormat getCurrencyFormat()
  {
    return getCurrencyFormat(ULocale.getDefault(ULocale.Category.FORMAT));
  }
}
