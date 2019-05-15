package com.ibm.icu.text;

import com.ibm.icu.impl.CurrencyData;
import com.ibm.icu.impl.CurrencyData.CurrencyDisplayInfo;
import com.ibm.icu.impl.CurrencyData.CurrencyDisplayInfoProvider;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
























public class CurrencyPluralInfo
  implements Cloneable, Serializable
{
  private static final long serialVersionUID = 1L;
  
  public CurrencyPluralInfo()
  {
    initialize(ULocale.getDefault(ULocale.Category.FORMAT));
  }
  




  public CurrencyPluralInfo(Locale locale)
  {
    initialize(ULocale.forLocale(locale));
  }
  




  public CurrencyPluralInfo(ULocale locale)
  {
    initialize(locale);
  }
  





  public static CurrencyPluralInfo getInstance()
  {
    return new CurrencyPluralInfo();
  }
  






  public static CurrencyPluralInfo getInstance(Locale locale)
  {
    return new CurrencyPluralInfo(locale);
  }
  






  public static CurrencyPluralInfo getInstance(ULocale locale)
  {
    return new CurrencyPluralInfo(locale);
  }
  





  public PluralRules getPluralRules()
  {
    return pluralRules;
  }
  







  public String getCurrencyPluralPattern(String pluralCount)
  {
    String currencyPluralPattern = (String)pluralCountToCurrencyUnitPattern.get(pluralCount);
    if (currencyPluralPattern == null)
    {
      if (!pluralCount.equals("other")) {
        currencyPluralPattern = (String)pluralCountToCurrencyUnitPattern.get("other");
      }
      if (currencyPluralPattern == null)
      {




        currencyPluralPattern = defaultCurrencyPluralPattern;
      }
    }
    return currencyPluralPattern;
  }
  






  public ULocale getLocale()
  {
    return ulocale;
  }
  






  public void setPluralRules(String ruleDescription)
  {
    pluralRules = PluralRules.createRules(ruleDescription);
  }
  








  public void setCurrencyPluralPattern(String pluralCount, String pattern)
  {
    pluralCountToCurrencyUnitPattern.put(pluralCount, pattern);
  }
  






  public void setLocale(ULocale loc)
  {
    ulocale = loc;
    initialize(loc);
  }
  



  public Object clone()
  {
    try
    {
      CurrencyPluralInfo other = (CurrencyPluralInfo)super.clone();
      
      ulocale = ((ULocale)ulocale.clone());
      



      pluralCountToCurrencyUnitPattern = new HashMap();
      for (String pluralCount : pluralCountToCurrencyUnitPattern.keySet()) {
        String currencyPattern = (String)pluralCountToCurrencyUnitPattern.get(pluralCount);
        pluralCountToCurrencyUnitPattern.put(pluralCount, currencyPattern);
      }
      return other;
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException();
    }
  }
  




  public boolean equals(Object a)
  {
    if ((a instanceof CurrencyPluralInfo)) {
      CurrencyPluralInfo other = (CurrencyPluralInfo)a;
      return (pluralRules.equals(pluralRules)) && (pluralCountToCurrencyUnitPattern.equals(pluralCountToCurrencyUnitPattern));
    }
    
    return false;
  }
  


  /**
   * @deprecated
   */
  public int hashCode()
  {
    if (!$assertionsDisabled) throw new AssertionError("hashCode not designed");
    return 42;
  }
  



  String select(double number)
  {
    return pluralRules.select(number);
  }
  




  Iterator<String> pluralPatternIterator()
  {
    return pluralCountToCurrencyUnitPattern.keySet().iterator();
  }
  
  private void initialize(ULocale uloc) {
    ulocale = uloc;
    pluralRules = PluralRules.forLocale(uloc);
    setupCurrencyPluralPattern(uloc);
  }
  
  private void setupCurrencyPluralPattern(ULocale uloc) {
    pluralCountToCurrencyUnitPattern = new HashMap();
    
    String numberStylePattern = NumberFormat.getPattern(uloc, 0);
    
    int separatorIndex = numberStylePattern.indexOf(";");
    String negNumberPattern = null;
    if (separatorIndex != -1) {
      negNumberPattern = numberStylePattern.substring(separatorIndex + 1);
      numberStylePattern = numberStylePattern.substring(0, separatorIndex);
    }
    Map<String, String> map = CurrencyData.provider.getInstance(uloc, true).getUnitPatterns();
    for (Map.Entry<String, String> e : map.entrySet()) {
      String pluralCount = (String)e.getKey();
      String pattern = (String)e.getValue();
      


      String patternWithNumber = pattern.replace("{0}", numberStylePattern);
      String patternWithCurrencySign = patternWithNumber.replace("{1}", tripleCurrencyStr);
      if (separatorIndex != -1) {
        String negPattern = pattern;
        String negWithNumber = negPattern.replace("{0}", negNumberPattern);
        String negWithCurrSign = negWithNumber.replace("{1}", tripleCurrencyStr);
        StringBuilder posNegPatterns = new StringBuilder(patternWithCurrencySign);
        posNegPatterns.append(";");
        posNegPatterns.append(negWithCurrSign);
        patternWithCurrencySign = posNegPatterns.toString();
      }
      pluralCountToCurrencyUnitPattern.put(pluralCount, patternWithCurrencySign);
    }
  }
  




  private static final char[] tripleCurrencySign = { '¤', '¤', '¤' };
  
  private static final String tripleCurrencyStr = new String(tripleCurrencySign);
  

  private static final char[] defaultCurrencyPluralPatternChar = { '\000', '.', '#', '#', ' ', '¤', '¤', '¤' };
  
  private static final String defaultCurrencyPluralPattern = new String(defaultCurrencyPluralPatternChar);
  



  private Map<String, String> pluralCountToCurrencyUnitPattern = null;
  






  private PluralRules pluralRules = null;
  

  private ULocale ulocale = null;
}
