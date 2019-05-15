package com.ibm.icu.text;

import com.ibm.icu.util.CurrencyAmount;
import com.ibm.icu.util.ULocale;
import java.text.FieldPosition;
import java.text.ParsePosition;























class CurrencyFormat
  extends MeasureFormat
{
  static final long serialVersionUID = -931679363692504634L;
  private NumberFormat fmt;
  
  public CurrencyFormat(ULocale locale)
  {
    fmt = NumberFormat.getCurrencyInstance(locale.toLocale());
  }
  


  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
  {
    try
    {
      CurrencyAmount currency = (CurrencyAmount)obj;
      fmt.setCurrency(currency.getCurrency());
      return fmt.format(currency.getNumber(), toAppendTo, pos);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException("Invalid type: " + obj.getClass().getName());
    }
  }
  



  public Object parseObject(String source, ParsePosition pos)
  {
    return fmt.parseCurrency(source, pos);
  }
}
