package com.ibm.icu.text;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.CurrencyAmount;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import com.ibm.icu.util.UResourceBundle;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.Format.Field;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;








































































































































































































public abstract class NumberFormat
  extends UFormat
{
  public static final int NUMBERSTYLE = 0;
  public static final int CURRENCYSTYLE = 1;
  public static final int PERCENTSTYLE = 2;
  public static final int SCIENTIFICSTYLE = 3;
  public static final int INTEGERSTYLE = 4;
  public static final int ISOCURRENCYSTYLE = 5;
  public static final int PLURALCURRENCYSTYLE = 6;
  public static final int INTEGER_FIELD = 0;
  public static final int FRACTION_FIELD = 1;
  private static NumberFormatShim shim;
  
  public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos)
  {
    if ((number instanceof Long))
      return format(((Long)number).longValue(), toAppendTo, pos);
    if ((number instanceof BigInteger))
      return format((BigInteger)number, toAppendTo, pos);
    if ((number instanceof java.math.BigDecimal))
      return format((java.math.BigDecimal)number, toAppendTo, pos);
    if ((number instanceof com.ibm.icu.math.BigDecimal))
      return format((com.ibm.icu.math.BigDecimal)number, toAppendTo, pos);
    if ((number instanceof CurrencyAmount))
      return format((CurrencyAmount)number, toAppendTo, pos);
    if ((number instanceof Number)) {
      return format(((Number)number).doubleValue(), toAppendTo, pos);
    }
    throw new IllegalArgumentException("Cannot format given Object as a Number");
  }
  










  public final Object parseObject(String source, ParsePosition parsePosition)
  {
    return parse(source, parsePosition);
  }
  




  public final String format(double number)
  {
    return format(number, new StringBuffer(), new FieldPosition(0)).toString();
  }
  





  public final String format(long number)
  {
    StringBuffer buf = new StringBuffer(19);
    FieldPosition pos = new FieldPosition(0);
    format(number, buf, pos);
    return buf.toString();
  }
  



  public final String format(BigInteger number)
  {
    return format(number, new StringBuffer(), new FieldPosition(0)).toString();
  }
  




  public final String format(java.math.BigDecimal number)
  {
    return format(number, new StringBuffer(), new FieldPosition(0)).toString();
  }
  




  public final String format(com.ibm.icu.math.BigDecimal number)
  {
    return format(number, new StringBuffer(), new FieldPosition(0)).toString();
  }
  




  public final String format(CurrencyAmount currAmt)
  {
    return format(currAmt, new StringBuffer(), new FieldPosition(0)).toString();
  }
  







  public abstract StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  







  public abstract StringBuffer format(long paramLong, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  







  public abstract StringBuffer format(BigInteger paramBigInteger, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  






  public abstract StringBuffer format(java.math.BigDecimal paramBigDecimal, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  






  public abstract StringBuffer format(com.ibm.icu.math.BigDecimal paramBigDecimal, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  






  public StringBuffer format(CurrencyAmount currAmt, StringBuffer toAppendTo, FieldPosition pos)
  {
    Currency save = getCurrency();Currency curr = currAmt.getCurrency();
    boolean same = curr.equals(save);
    if (!same) setCurrency(curr);
    format(currAmt.getNumber(), toAppendTo, pos);
    if (!same) setCurrency(save);
    return toAppendTo;
  }
  











  public abstract Number parse(String paramString, ParsePosition paramParsePosition);
  











  public Number parse(String text)
    throws ParseException
  {
    ParsePosition parsePosition = new ParsePosition(0);
    Number result = parse(text, parsePosition);
    if (parsePosition.getIndex() == 0) {
      throw new ParseException("Unparseable number: \"" + text + '"', parsePosition.getErrorIndex());
    }
    
    return result;
  }
  


















  public CurrencyAmount parseCurrency(CharSequence text, ParsePosition pos)
  {
    Number n = parse(text.toString(), pos);
    return n == null ? null : new CurrencyAmount(n, getEffectiveCurrency());
  }
  










  public boolean isParseIntegerOnly()
  {
    return parseIntegerOnly;
  }
  





  public void setParseIntegerOnly(boolean value)
  {
    parseIntegerOnly = value;
  }
  
















  public void setParseStrict(boolean value)
  {
    parseStrict = value;
  }
  





  public boolean isParseStrict()
  {
    return parseStrict;
  }
  











  public static final NumberFormat getInstance()
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT), 0);
  }
  






  public static NumberFormat getInstance(Locale inLocale)
  {
    return getInstance(ULocale.forLocale(inLocale), 0);
  }
  






  public static NumberFormat getInstance(ULocale inLocale)
  {
    return getInstance(inLocale, 0);
  }
  





  public static final NumberFormat getInstance(int style)
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT), style);
  }
  





  public static NumberFormat getInstance(Locale inLocale, int style)
  {
    return getInstance(ULocale.forLocale(inLocale), style);
  }
  





  public static final NumberFormat getNumberInstance()
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT), 0);
  }
  



  public static NumberFormat getNumberInstance(Locale inLocale)
  {
    return getInstance(ULocale.forLocale(inLocale), 0);
  }
  



  public static NumberFormat getNumberInstance(ULocale inLocale)
  {
    return getInstance(inLocale, 0);
  }
  












  public static final NumberFormat getIntegerInstance()
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT), 4);
  }
  












  public static NumberFormat getIntegerInstance(Locale inLocale)
  {
    return getInstance(ULocale.forLocale(inLocale), 4);
  }
  











  public static NumberFormat getIntegerInstance(ULocale inLocale)
  {
    return getInstance(inLocale, 4);
  }
  





  public static final NumberFormat getCurrencyInstance()
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT), 1);
  }
  




  public static NumberFormat getCurrencyInstance(Locale inLocale)
  {
    return getInstance(ULocale.forLocale(inLocale), 1);
  }
  




  public static NumberFormat getCurrencyInstance(ULocale inLocale)
  {
    return getInstance(inLocale, 1);
  }
  





  public static final NumberFormat getPercentInstance()
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT), 2);
  }
  




  public static NumberFormat getPercentInstance(Locale inLocale)
  {
    return getInstance(ULocale.forLocale(inLocale), 2);
  }
  




  public static NumberFormat getPercentInstance(ULocale inLocale)
  {
    return getInstance(inLocale, 2);
  }
  





  public static final NumberFormat getScientificInstance()
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT), 3);
  }
  




  public static NumberFormat getScientificInstance(Locale inLocale)
  {
    return getInstance(ULocale.forLocale(inLocale), 3);
  }
  




  public static NumberFormat getScientificInstance(ULocale inLocale)
  {
    return getInstance(inLocale, 3);
  }
  








  public static abstract class NumberFormatFactory
  {
    public static final int FORMAT_NUMBER = 0;
    







    public static final int FORMAT_CURRENCY = 1;
    






    public static final int FORMAT_PERCENT = 2;
    






    public static final int FORMAT_SCIENTIFIC = 3;
    






    public static final int FORMAT_INTEGER = 4;
    







    public boolean visible()
    {
      return true;
    }
    









    public abstract Set<String> getSupportedLocaleNames();
    








    public NumberFormat createFormat(ULocale loc, int formatType)
    {
      return createFormat(loc.toLocale(), formatType);
    }
    












    public NumberFormat createFormat(Locale loc, int formatType)
    {
      return createFormat(ULocale.forLocale(loc), formatType);
    }
    



    protected NumberFormatFactory() {}
  }
  


  public static abstract class SimpleNumberFormatFactory
    extends NumberFormat.NumberFormatFactory
  {
    final Set<String> localeNames;
    

    final boolean visible;
    


    public SimpleNumberFormatFactory(Locale locale)
    {
      this(locale, true);
    }
    




    public SimpleNumberFormatFactory(Locale locale, boolean visible)
    {
      localeNames = Collections.singleton(ULocale.forLocale(locale).getBaseName());
      this.visible = visible;
    }
    



    public SimpleNumberFormatFactory(ULocale locale)
    {
      this(locale, true);
    }
    




    public SimpleNumberFormatFactory(ULocale locale, boolean visible)
    {
      localeNames = Collections.singleton(locale.getBaseName());
      this.visible = visible;
    }
    




    public final boolean visible()
    {
      return visible;
    }
    




    public final Set<String> getSupportedLocaleNames()
    {
      return localeNames;
    }
  }
  













  private static NumberFormatShim getShim()
  {
    if (shim == null) {
      try {
        Class<?> cls = Class.forName("com.ibm.icu.text.NumberFormatServiceShim");
        shim = (NumberFormatShim)cls.newInstance();
      }
      catch (MissingResourceException e)
      {
        throw e;
      }
      catch (Exception e)
      {
        throw new RuntimeException(e.getMessage());
      }
    }
    
    return shim;
  }
  




  public static Locale[] getAvailableLocales()
  {
    if (shim == null) {
      return ICUResourceBundle.getAvailableLocales();
    }
    return getShim().getAvailableLocales();
  }
  





  public static ULocale[] getAvailableULocales()
  {
    if (shim == null) {
      return ICUResourceBundle.getAvailableULocales();
    }
    return getShim().getAvailableULocales();
  }
  







  public static Object registerFactory(NumberFormatFactory factory)
  {
    if (factory == null) {
      throw new IllegalArgumentException("factory must not be null");
    }
    return getShim().registerFactory(factory);
  }
  






  public static boolean unregister(Object registryKey)
  {
    if (registryKey == null) {
      throw new IllegalArgumentException("registryKey must not be null");
    }
    
    if (shim == null) {
      return false;
    }
    
    return shim.unregister(registryKey);
  }
  






  public int hashCode()
  {
    return maximumIntegerDigits * 37 + maxFractionDigits;
  }
  










  public boolean equals(Object obj)
  {
    if (obj == null) return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;
    NumberFormat other = (NumberFormat)obj;
    return (maximumIntegerDigits == maximumIntegerDigits) && (minimumIntegerDigits == minimumIntegerDigits) && (maximumFractionDigits == maximumFractionDigits) && (minimumFractionDigits == minimumFractionDigits) && (groupingUsed == groupingUsed) && (parseIntegerOnly == parseIntegerOnly) && (parseStrict == parseStrict);
  }
  










  public Object clone()
  {
    NumberFormat other = (NumberFormat)super.clone();
    return other;
  }
  









  public boolean isGroupingUsed()
  {
    return groupingUsed;
  }
  






  public void setGroupingUsed(boolean newValue)
  {
    groupingUsed = newValue;
  }
  








  public int getMaximumIntegerDigits()
  {
    return maximumIntegerDigits;
  }
  











  public void setMaximumIntegerDigits(int newValue)
  {
    maximumIntegerDigits = Math.max(0, newValue);
    if (minimumIntegerDigits > maximumIntegerDigits) {
      minimumIntegerDigits = maximumIntegerDigits;
    }
  }
  








  public int getMinimumIntegerDigits()
  {
    return minimumIntegerDigits;
  }
  











  public void setMinimumIntegerDigits(int newValue)
  {
    minimumIntegerDigits = Math.max(0, newValue);
    if (minimumIntegerDigits > maximumIntegerDigits) {
      maximumIntegerDigits = minimumIntegerDigits;
    }
  }
  








  public int getMaximumFractionDigits()
  {
    return maximumFractionDigits;
  }
  











  public void setMaximumFractionDigits(int newValue)
  {
    maximumFractionDigits = Math.max(0, newValue);
    if (maximumFractionDigits < minimumFractionDigits) {
      minimumFractionDigits = maximumFractionDigits;
    }
  }
  








  public int getMinimumFractionDigits()
  {
    return minimumFractionDigits;
  }
  











  public void setMinimumFractionDigits(int newValue)
  {
    minimumFractionDigits = Math.max(0, newValue);
    if (maximumFractionDigits < minimumFractionDigits) {
      maximumFractionDigits = minimumFractionDigits;
    }
  }
  








  public void setCurrency(Currency theCurrency)
  {
    currency = theCurrency;
  }
  




  public Currency getCurrency()
  {
    return currency;
  }
  







  @Deprecated
  protected Currency getEffectiveCurrency()
  {
    Currency c = getCurrency();
    if (c == null) {
      ULocale uloc = getLocale(ULocale.VALID_LOCALE);
      if (uloc == null) {
        uloc = ULocale.getDefault(ULocale.Category.FORMAT);
      }
      c = Currency.getInstance(uloc);
    }
    return c;
  }
  







  public int getRoundingMode()
  {
    throw new UnsupportedOperationException("getRoundingMode must be implemented by the subclass implementation.");
  }
  









  public void setRoundingMode(int roundingMode)
  {
    throw new UnsupportedOperationException("setRoundingMode must be implemented by the subclass implementation.");
  }
  












  public static NumberFormat getInstance(ULocale desiredLocale, int choice)
  {
    if ((choice < 0) || (choice > 6)) {
      throw new IllegalArgumentException("choice should be from NUMBERSTYLE to PLURALCURRENCYSTYLE");
    }
    






    return getShim().createInstance(desiredLocale, choice);
  }
  






  static NumberFormat createInstance(ULocale desiredLocale, int choice)
  {
    String pattern = getPattern(desiredLocale, choice);
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(desiredLocale);
    





    if ((choice == 1) || (choice == 5)) {
      String temp = symbols.getCurrencyPattern();
      if (temp != null) {
        pattern = temp;
      }
    }
    


    if (choice == 5) {
      pattern = pattern.replace("¤", doubleCurrencyStr);
    }
    

    NumberingSystem ns = NumberingSystem.getInstance(desiredLocale);
    if (ns == null) {
      return null;
    }
    
    NumberFormat format;
    NumberFormat format;
    if ((ns != null) && (ns.isAlgorithmic()))
    {



      int desiredRulesType = 4;
      
      String nsDesc = ns.getDescription();
      int firstSlash = nsDesc.indexOf("/");
      int lastSlash = nsDesc.lastIndexOf("/");
      ULocale nsLoc;
      String nsRuleSetName; if (lastSlash > firstSlash) {
        String nsLocID = nsDesc.substring(0, firstSlash);
        String nsRuleSetGroup = nsDesc.substring(firstSlash + 1, lastSlash);
        String nsRuleSetName = nsDesc.substring(lastSlash + 1);
        
        ULocale nsLoc = new ULocale(nsLocID);
        if (nsRuleSetGroup.equals("SpelloutRules")) {
          desiredRulesType = 1;
        }
      } else {
        nsLoc = desiredLocale;
        nsRuleSetName = nsDesc;
      }
      
      RuleBasedNumberFormat r = new RuleBasedNumberFormat(nsLoc, desiredRulesType);
      r.setDefaultRuleSet(nsRuleSetName);
      format = r;
    } else {
      DecimalFormat f = new DecimalFormat(pattern, symbols, choice);
      






      if (choice == 4) {
        f.setMaximumFractionDigits(0);
        f.setDecimalSeparatorAlwaysShown(false);
        f.setParseIntegerOnly(true);
      }
      format = f;
    }
    


    ULocale valid = symbols.getLocale(ULocale.VALID_LOCALE);
    ULocale actual = symbols.getLocale(ULocale.ACTUAL_LOCALE);
    format.setLocale(valid, actual);
    
    return format;
  }
  






  @Deprecated
  protected static String getPattern(Locale forLocale, int choice)
  {
    return getPattern(ULocale.forLocale(forLocale), choice);
  }
  


















































  protected static String getPattern(ULocale forLocale, int choice)
  {
    int entry = (choice == 5) || (choice == 6) ? 1 : choice == 4 ? 0 : choice;
    


    ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", forLocale);
    
    String[] numberPatternKeys = { "decimalFormat", "currencyFormat", "percentFormat", "scientificFormat" };
    NumberingSystem ns = NumberingSystem.getInstance(forLocale);
    
    String result = null;
    try {
      result = rb.getStringWithFallback("NumberElements/" + ns.getName() + "/patterns/" + numberPatternKeys[entry]);
    } catch (MissingResourceException ex) {
      result = rb.getStringWithFallback("NumberElements/latn/patterns/" + numberPatternKeys[entry]);
    }
    
    return result;
  }
  











  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    

    if (serialVersionOnStream < 1)
    {
      maximumIntegerDigits = maxIntegerDigits;
      minimumIntegerDigits = minIntegerDigits;
      maximumFractionDigits = maxFractionDigits;
      minimumFractionDigits = minFractionDigits;
    }
    



    if ((minimumIntegerDigits > maximumIntegerDigits) || (minimumFractionDigits > maximumFractionDigits) || (minimumIntegerDigits < 0) || (minimumFractionDigits < 0))
    {

      throw new InvalidObjectException("Digit count range invalid");
    }
    serialVersionOnStream = 1;
  }
  







  private void writeObject(ObjectOutputStream stream)
    throws IOException
  {
    maxIntegerDigits = (maximumIntegerDigits > 127 ? Byte.MAX_VALUE : (byte)maximumIntegerDigits);
    
    minIntegerDigits = (minimumIntegerDigits > 127 ? Byte.MAX_VALUE : (byte)minimumIntegerDigits);
    
    maxFractionDigits = (maximumFractionDigits > 127 ? Byte.MAX_VALUE : (byte)maximumFractionDigits);
    
    minFractionDigits = (minimumFractionDigits > 127 ? Byte.MAX_VALUE : (byte)minimumFractionDigits);
    
    stream.defaultWriteObject();
  }
  






  private static final char[] doubleCurrencySign = { '¤', '¤' };
  private static final String doubleCurrencyStr = new String(doubleCurrencySign);
  












  private boolean groupingUsed = true;
  
















  private byte maxIntegerDigits = 40;
  
















  private byte minIntegerDigits = 1;
  
















  private byte maxFractionDigits = 3;
  
















  private byte minFractionDigits = 0;
  






  private boolean parseIntegerOnly = false;
  










  private int maximumIntegerDigits = 40;
  








  private int minimumIntegerDigits = 1;
  








  private int maximumFractionDigits = 3;
  








  private int minimumFractionDigits = 0;
  









  private Currency currency;
  









  static final int currentSerialVersion = 1;
  








  private int serialVersionOnStream = 1;
  
  private static final long serialVersionUID = -2308460125733713944L;
  
  private boolean parseStrict;
  

  public NumberFormat() {}
  

  static abstract class NumberFormatShim
  {
    NumberFormatShim() {}
    

    abstract Locale[] getAvailableLocales();
    

    abstract ULocale[] getAvailableULocales();
    

    abstract Object registerFactory(NumberFormat.NumberFormatFactory paramNumberFormatFactory);
    

    abstract boolean unregister(Object paramObject);
    

    abstract NumberFormat createInstance(ULocale paramULocale, int paramInt);
  }
  

  public static class Field
    extends Format.Field
  {
    static final long serialVersionUID = -4516273749929385842L;
    public static final Field SIGN = new Field("sign");
    



    public static final Field INTEGER = new Field("integer");
    



    public static final Field FRACTION = new Field("fraction");
    



    public static final Field EXPONENT = new Field("exponent");
    



    public static final Field EXPONENT_SIGN = new Field("exponent sign");
    



    public static final Field EXPONENT_SYMBOL = new Field("exponent symbol");
    



    public static final Field DECIMAL_SEPARATOR = new Field("decimal separator");
    


    public static final Field GROUPING_SEPARATOR = new Field("grouping separator");
    



    public static final Field PERCENT = new Field("percent");
    



    public static final Field PERMILLE = new Field("per mille");
    



    public static final Field CURRENCY = new Field("currency");
    




    protected Field(String fieldName)
    {
      super();
    }
    




    protected Object readResolve()
      throws InvalidObjectException
    {
      if (getName().equals(INTEGER.getName()))
        return INTEGER;
      if (getName().equals(FRACTION.getName()))
        return FRACTION;
      if (getName().equals(EXPONENT.getName()))
        return EXPONENT;
      if (getName().equals(EXPONENT_SIGN.getName()))
        return EXPONENT_SIGN;
      if (getName().equals(EXPONENT_SYMBOL.getName()))
        return EXPONENT_SYMBOL;
      if (getName().equals(CURRENCY.getName()))
        return CURRENCY;
      if (getName().equals(DECIMAL_SEPARATOR.getName()))
        return DECIMAL_SEPARATOR;
      if (getName().equals(GROUPING_SEPARATOR.getName()))
        return GROUPING_SEPARATOR;
      if (getName().equals(PERCENT.getName()))
        return PERCENT;
      if (getName().equals(PERMILLE.getName()))
        return PERMILLE;
      if (getName().equals(SIGN.getName())) {
        return SIGN;
      }
      throw new InvalidObjectException("An invalid object.");
    }
  }
}
