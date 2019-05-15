package com.ibm.icu.text;

import com.ibm.icu.impl.CurrencyData.CurrencyDisplayInfo;
import com.ibm.icu.impl.CurrencyData.CurrencyFormatInfo;
import com.ibm.icu.impl.CurrencyData.CurrencySpacingInfo;
import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Type;
import com.ibm.icu.util.UResourceBundle;
import java.io.ObjectInputStream;
import java.text.ChoiceFormat;
import java.util.Locale;
import java.util.MissingResourceException;

public class DecimalFormatSymbols implements Cloneable, java.io.Serializable
{
  public static final int CURRENCY_SPC_CURRENCY_MATCH = 0;
  public static final int CURRENCY_SPC_SURROUNDING_MATCH = 1;
  public static final int CURRENCY_SPC_INSERT = 2;
  private String[] currencySpcBeforeSym;
  private String[] currencySpcAfterSym;
  private char zeroDigit;
  private char[] digits;
  private char groupingSeparator;
  private char decimalSeparator;
  private char perMill;
  private char percent;
  private char digit;
  private char sigDigit;
  private char patternSeparator;
  private String infinity;
  private String NaN;
  private char minusSign;
  private String currencySymbol;
  private String intlCurrencySymbol;
  private char monetarySeparator;
  private char monetaryGroupingSeparator;
  private char exponential;
  private String exponentSeparator;
  private char padEscape;
  private char plusSign;
  private Locale requestedLocale;
  private ULocale ulocale;
  private static final long serialVersionUID = 5772796243397350300L;
  private static final int currentSerialVersion = 6;
  
  public DecimalFormatSymbols()
  {
    initialize(ULocale.getDefault(com.ibm.icu.util.ULocale.Category.FORMAT));
  }
  




  public DecimalFormatSymbols(Locale locale)
  {
    initialize(ULocale.forLocale(locale));
  }
  




  public DecimalFormatSymbols(ULocale locale)
  {
    initialize(locale);
  }
  










  public static DecimalFormatSymbols getInstance()
  {
    return new DecimalFormatSymbols();
  }
  












  public static DecimalFormatSymbols getInstance(Locale locale)
  {
    return new DecimalFormatSymbols(locale);
  }
  












  public static DecimalFormatSymbols getInstance(ULocale locale)
  {
    return new DecimalFormatSymbols(locale);
  }
  













  public static Locale[] getAvailableLocales()
  {
    return ICUResourceBundle.getAvailableLocales();
  }
  














  public static ULocale[] getAvailableULocales()
  {
    return ICUResourceBundle.getAvailableULocales();
  }
  





  public char getZeroDigit()
  {
    if (digits != null) {
      return digits[0];
    }
    return zeroDigit;
  }
  




  public char[] getDigits()
  {
    if (digits != null) {
      return (char[])digits.clone();
    }
    char[] digitArray = new char[10];
    for (int i = 0; i < 10; i++) {
      digitArray[i] = ((char)(zeroDigit + i));
    }
    return digitArray;
  }
  





  char[] getDigitsLocal()
  {
    if (digits != null) {
      return digits;
    }
    char[] digitArray = new char[10];
    for (int i = 0; i < 10; i++) {
      digitArray[i] = ((char)(zeroDigit + i));
    }
    return digitArray;
  }
  





  public void setZeroDigit(char zeroDigit)
  {
    if (digits != null) {
      digits[0] = zeroDigit;
      if (Character.digit(zeroDigit, 10) == 0) {
        for (int i = 1; i < 10; i++) {
          digits[i] = ((char)(zeroDigit + i));
        }
      }
    } else {
      this.zeroDigit = zeroDigit;
    }
  }
  




  public char getSignificantDigit()
  {
    return sigDigit;
  }
  




  public void setSignificantDigit(char sigDigit)
  {
    this.sigDigit = sigDigit;
  }
  




  public char getGroupingSeparator()
  {
    return groupingSeparator;
  }
  




  public void setGroupingSeparator(char groupingSeparator)
  {
    this.groupingSeparator = groupingSeparator;
  }
  




  public char getDecimalSeparator()
  {
    return decimalSeparator;
  }
  




  public void setDecimalSeparator(char decimalSeparator)
  {
    this.decimalSeparator = decimalSeparator;
  }
  




  public char getPerMill()
  {
    return perMill;
  }
  




  public void setPerMill(char perMill)
  {
    this.perMill = perMill;
  }
  




  public char getPercent()
  {
    return percent;
  }
  




  public void setPercent(char percent)
  {
    this.percent = percent;
  }
  




  public char getDigit()
  {
    return digit;
  }
  




  public void setDigit(char digit)
  {
    this.digit = digit;
  }
  





  public char getPatternSeparator()
  {
    return patternSeparator;
  }
  





  public void setPatternSeparator(char patternSeparator)
  {
    this.patternSeparator = patternSeparator;
  }
  







  public String getInfinity()
  {
    return infinity;
  }
  





  public void setInfinity(String infinity)
  {
    this.infinity = infinity;
  }
  






  public String getNaN()
  {
    return NaN;
  }
  





  public void setNaN(String NaN)
  {
    this.NaN = NaN;
  }
  






  public char getMinusSign()
  {
    return minusSign;
  }
  






  public void setMinusSign(char minusSign)
  {
    this.minusSign = minusSign;
  }
  




  public String getCurrencySymbol()
  {
    return currencySymbol;
  }
  




  public void setCurrencySymbol(String currency)
  {
    currencySymbol = currency;
  }
  




  public String getInternationalCurrencySymbol()
  {
    return intlCurrencySymbol;
  }
  




  public void setInternationalCurrencySymbol(String currency)
  {
    intlCurrencySymbol = currency;
  }
  





  public Currency getCurrency()
  {
    return currency;
  }
  
















  public void setCurrency(Currency currency)
  {
    if (currency == null) {
      throw new NullPointerException();
    }
    this.currency = currency;
    intlCurrencySymbol = currency.getCurrencyCode();
    currencySymbol = currency.getSymbol(requestedLocale);
  }
  




  public char getMonetaryDecimalSeparator()
  {
    return monetarySeparator;
  }
  




  public char getMonetaryGroupingSeparator()
  {
    return monetaryGroupingSeparator;
  }
  



  String getCurrencyPattern()
  {
    return currencyPattern;
  }
  




  public void setMonetaryDecimalSeparator(char sep)
  {
    monetarySeparator = sep;
  }
  




  public void setMonetaryGroupingSeparator(char sep)
  {
    monetaryGroupingSeparator = sep;
  }
  







  public String getExponentSeparator()
  {
    return exponentSeparator;
  }
  







  public void setExponentSeparator(String exp)
  {
    exponentSeparator = exp;
  }
  








  public char getPlusSign()
  {
    return plusSign;
  }
  








  public void setPlusSign(char plus)
  {
    plusSign = plus;
  }
  











  public char getPadEscape()
  {
    return padEscape;
  }
  










  public void setPadEscape(char c)
  {
    padEscape = c;
  }
  








































  public String getPatternForCurrencySpacing(int itemType, boolean beforeCurrency)
  {
    if ((itemType < 0) || (itemType > 2))
    {
      throw new IllegalArgumentException("unknown currency spacing: " + itemType);
    }
    if (beforeCurrency) {
      return currencySpcBeforeSym[itemType];
    }
    return currencySpcAfterSym[itemType];
  }
  
















  public void setPatternForCurrencySpacing(int itemType, boolean beforeCurrency, String pattern)
  {
    if ((itemType < 0) || (itemType > 2))
    {
      throw new IllegalArgumentException("unknown currency spacing: " + itemType);
    }
    if (beforeCurrency) {
      currencySpcBeforeSym[itemType] = pattern;
    } else {
      currencySpcAfterSym[itemType] = pattern;
    }
  }
  




  public Locale getLocale()
  {
    return requestedLocale;
  }
  




  public ULocale getULocale()
  {
    return ulocale;
  }
  


  public Object clone()
  {
    try
    {
      return (DecimalFormatSymbols)super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException();
    }
  }
  




  public boolean equals(Object obj)
  {
    if (!(obj instanceof DecimalFormatSymbols)) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    DecimalFormatSymbols other = (DecimalFormatSymbols)obj;
    for (int i = 0; i <= 2; i++) {
      if (!currencySpcBeforeSym[i].equals(currencySpcBeforeSym[i])) {
        return false;
      }
      if (!currencySpcAfterSym[i].equals(currencySpcAfterSym[i])) {
        return false;
      }
    }
    
    if (digits == null) {
      for (int i = 0; i < 10; i++) {
        if (digits[i] != zeroDigit + i) {
          return false;
        }
      }
    } else if (!java.util.Arrays.equals(digits, digits)) {
      return false;
    }
    
    return (groupingSeparator == groupingSeparator) && (decimalSeparator == decimalSeparator) && (percent == percent) && (perMill == perMill) && (digit == digit) && (minusSign == minusSign) && (patternSeparator == patternSeparator) && (infinity.equals(infinity)) && (NaN.equals(NaN)) && (currencySymbol.equals(currencySymbol)) && (intlCurrencySymbol.equals(intlCurrencySymbol)) && (padEscape == padEscape) && (plusSign == plusSign) && (exponentSeparator.equals(exponentSeparator)) && (monetarySeparator == monetarySeparator) && (monetaryGroupingSeparator == monetaryGroupingSeparator);
  }
  



















  public int hashCode()
  {
    int result = digits[0];
    result = result * 37 + groupingSeparator;
    result = result * 37 + decimalSeparator;
    return result;
  }
  




  private void initialize(ULocale locale)
  {
    requestedLocale = locale.toLocale();
    ulocale = locale;
    


    NumberingSystem ns = NumberingSystem.getInstance(locale);
    digits = new char[10];
    String nsName; String nsName; if ((ns != null) && (ns.getRadix() == 10) && (!ns.isAlgorithmic()) && (NumberingSystem.isValidDigitString(ns.getDescription())))
    {
      String digitString = ns.getDescription();
      digits[0] = digitString.charAt(0);
      digits[1] = digitString.charAt(1);
      digits[2] = digitString.charAt(2);
      digits[3] = digitString.charAt(3);
      digits[4] = digitString.charAt(4);
      digits[5] = digitString.charAt(5);
      digits[6] = digitString.charAt(6);
      digits[7] = digitString.charAt(7);
      digits[8] = digitString.charAt(8);
      digits[9] = digitString.charAt(9);
      nsName = ns.getName();
    } else {
      digits[0] = '0';
      digits[1] = '1';
      digits[2] = '2';
      digits[3] = '3';
      digits[4] = '4';
      digits[5] = '5';
      digits[6] = '6';
      digits[7] = '7';
      digits[8] = '8';
      digits[9] = '9';
      nsName = "latn";
    }
    

    String[][] data = (String[][])cachedLocaleData.get(locale);
    
    if (data == null) {
      data = new String[1][];
      ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
      
      boolean isLatn = nsName.equals("latn");
      String baseKey = "NumberElements/" + nsName + "/symbols/";
      String latnKey = "NumberElements/latn/symbols/";
      String[] symbolKeys = { "decimal", "group", "list", "percentSign", "minusSign", "plusSign", "exponential", "perMille", "infinity", "nan", "currencyDecimal", "currencyGroup" };
      String[] fallbackElements = { ".", ",", ";", "%", "-", "+", "E", "‰", "∞", "NaN", null, null };
      String[] symbolsArray = new String[symbolKeys.length];
      for (int i = 0; i < symbolKeys.length; i++) {
        try {
          symbolsArray[i] = rb.getStringWithFallback(baseKey + symbolKeys[i]);
        } catch (MissingResourceException ex) {
          if (!isLatn) {
            try {
              symbolsArray[i] = rb.getStringWithFallback(latnKey + symbolKeys[i]);
            } catch (MissingResourceException ex1) {
              symbolsArray[i] = fallbackElements[i];
            }
          } else {
            symbolsArray[i] = fallbackElements[i];
          }
        }
      }
      
      data[0] = symbolsArray;
      
      cachedLocaleData.put(locale, data);
    }
    String[] numberElements = data[0];
    
    ICUResourceBundle r = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
    


    ULocale uloc = r.getULocale();
    setLocale(uloc, uloc);
    

    decimalSeparator = numberElements[0].charAt(0);
    groupingSeparator = numberElements[1].charAt(0);
    patternSeparator = numberElements[2].charAt(0);
    percent = numberElements[3].charAt(0);
    minusSign = numberElements[4].charAt(0);
    plusSign = numberElements[5].charAt(0);
    exponentSeparator = numberElements[6];
    perMill = numberElements[7].charAt(0);
    infinity = numberElements[8];
    NaN = numberElements[9];
    
    if (numberElements[10] != null) {
      monetarySeparator = numberElements[10].charAt(0);
    } else {
      monetarySeparator = decimalSeparator;
    }
    
    if (numberElements[11] != null) {
      monetaryGroupingSeparator = numberElements[11].charAt(0);
    } else {
      monetaryGroupingSeparator = groupingSeparator;
    }
    
    digit = '#';
    padEscape = '*';
    sigDigit = '@';
    

    CurrencyData.CurrencyDisplayInfo info = com.ibm.icu.impl.CurrencyData.provider.getInstance(locale, true);
    



    String currname = null;
    currency = Currency.getInstance(locale);
    if (currency != null) {
      intlCurrencySymbol = currency.getCurrencyCode();
      boolean[] isChoiceFormat = new boolean[1];
      currname = currency.getName(locale, 0, isChoiceFormat);
      

      currencySymbol = (isChoiceFormat[0] != 0 ? new ChoiceFormat(currname).format(2.0D) : currname);
      

      CurrencyData.CurrencyFormatInfo fmtInfo = info.getFormatInfo(intlCurrencySymbol);
      if (fmtInfo != null) {
        currencyPattern = currencyPattern;
        monetarySeparator = monetarySeparator;
        monetaryGroupingSeparator = monetaryGroupingSeparator;
      }
    } else {
      intlCurrencySymbol = "XXX";
      currencySymbol = "¤";
    }
    


    currencySpcBeforeSym = new String[3];
    currencySpcAfterSym = new String[3];
    initSpacingInfo(info.getSpacingInfo());
  }
  
  private void initSpacingInfo(CurrencyData.CurrencySpacingInfo spcInfo) {
    currencySpcBeforeSym[0] = beforeCurrencyMatch;
    currencySpcBeforeSym[1] = beforeContextMatch;
    currencySpcBeforeSym[2] = beforeInsert;
    currencySpcAfterSym[0] = afterCurrencyMatch;
    currencySpcAfterSym[1] = afterContextMatch;
    currencySpcAfterSym[2] = afterInsert;
  }
  











  private void readObject(ObjectInputStream stream)
    throws java.io.IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    

    if (serialVersionOnStream < 1)
    {

      monetarySeparator = decimalSeparator;
      exponential = 'E';
    }
    if (serialVersionOnStream < 2) {
      padEscape = '*';
      plusSign = '+';
      exponentSeparator = String.valueOf(exponential);
    }
    




    if (serialVersionOnStream < 3)
    {




      requestedLocale = Locale.getDefault();
    }
    if (serialVersionOnStream < 4)
    {
      ulocale = ULocale.forLocale(requestedLocale);
    }
    if (serialVersionOnStream < 5)
    {
      monetaryGroupingSeparator = groupingSeparator;
    }
    if (serialVersionOnStream < 6)
    {
      if (currencySpcBeforeSym == null) {
        currencySpcBeforeSym = new String[3];
      }
      if (currencySpcAfterSym == null) {
        currencySpcAfterSym = new String[3];
      }
      initSpacingInfo(CurrencyData.CurrencySpacingInfo.DEFAULT);
    }
    serialVersionOnStream = 6;
    

    currency = Currency.getInstance(intlCurrencySymbol);
  }
  















































































































































































































  private int serialVersionOnStream = 6;
  



  private static final ICUCache<ULocale, String[][]> cachedLocaleData = new SimpleCache();
  




  private String currencyPattern = null;
  




  private ULocale validLocale;
  




  private ULocale actualLocale;
  




  private transient Currency currency;
  





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
