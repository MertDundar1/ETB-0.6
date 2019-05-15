package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Map;




























































































































public class PluralFormat
  extends UFormat
{
  private static final long serialVersionUID = 1L;
  private ULocale ulocale = null;
  




  private PluralRules pluralRules = null;
  




  private String pattern = null;
  





  private transient MessagePattern msgPattern;
  





  private Map<String, String> parsedValues = null;
  





  private NumberFormat numberFormat = null;
  



  private transient double offset = 0.0D;
  






  public PluralFormat()
  {
    init(null, PluralRules.PluralType.CARDINAL, ULocale.getDefault(ULocale.Category.FORMAT));
  }
  






  public PluralFormat(ULocale ulocale)
  {
    init(null, PluralRules.PluralType.CARDINAL, ulocale);
  }
  







  public PluralFormat(PluralRules rules)
  {
    init(rules, PluralRules.PluralType.CARDINAL, ULocale.getDefault(ULocale.Category.FORMAT));
  }
  








  public PluralFormat(ULocale ulocale, PluralRules rules)
  {
    init(rules, PluralRules.PluralType.CARDINAL, ulocale);
  }
  








  public PluralFormat(ULocale ulocale, PluralRules.PluralType type)
  {
    init(null, type, ulocale);
  }
  








  public PluralFormat(String pattern)
  {
    init(null, PluralRules.PluralType.CARDINAL, ULocale.getDefault(ULocale.Category.FORMAT));
    applyPattern(pattern);
  }
  











  public PluralFormat(ULocale ulocale, String pattern)
  {
    init(null, PluralRules.PluralType.CARDINAL, ulocale);
    applyPattern(pattern);
  }
  










  public PluralFormat(PluralRules rules, String pattern)
  {
    init(rules, PluralRules.PluralType.CARDINAL, ULocale.getDefault(ULocale.Category.FORMAT));
    applyPattern(pattern);
  }
  











  public PluralFormat(ULocale ulocale, PluralRules rules, String pattern)
  {
    init(rules, PluralRules.PluralType.CARDINAL, ulocale);
    applyPattern(pattern);
  }
  











  public PluralFormat(ULocale ulocale, PluralRules.PluralType type, String pattern)
  {
    init(null, type, ulocale);
    applyPattern(pattern);
  }
  












  private void init(PluralRules rules, PluralRules.PluralType type, ULocale locale)
  {
    ulocale = locale;
    pluralRules = (rules == null ? PluralRules.forLocale(ulocale, type) : rules);
    
    resetPattern();
    numberFormat = NumberFormat.getInstance(ulocale);
  }
  
  private void resetPattern() {
    pattern = null;
    if (msgPattern != null) {
      msgPattern.clear();
    }
    offset = 0.0D;
  }
  









  public void applyPattern(String pattern)
  {
    this.pattern = pattern;
    if (msgPattern == null) {
      msgPattern = new MessagePattern();
    }
    try {
      msgPattern.parsePluralStyle(pattern);
      offset = msgPattern.getPluralOffset(0);
    } catch (RuntimeException e) {
      resetPattern();
      throw e;
    }
  }
  





  public String toPattern()
  {
    return pattern;
  }
  










  static int findSubMessage(MessagePattern pattern, int partIndex, PluralSelector selector, double number)
  {
    int count = pattern.countParts();
    
    MessagePattern.Part part = pattern.getPart(partIndex);
    double offset; if (part.getType().hasNumericValue()) {
      double offset = pattern.getNumericValue(part);
      partIndex++;
    } else {
      offset = 0.0D;
    }
    



    String keyword = null;
    


    boolean haveKeywordMatch = false;
    









    int msgStart = 0;
    
    do
    {
      part = pattern.getPart(partIndex++);
      MessagePattern.Part.Type type = part.getType();
      if (type == MessagePattern.Part.Type.ARG_LIMIT) {
        break;
      }
      assert (type == MessagePattern.Part.Type.ARG_SELECTOR);
      
      if (pattern.getPartType(partIndex).hasNumericValue())
      {
        part = pattern.getPart(partIndex++);
        if (number == pattern.getNumericValue(part))
        {
          return partIndex;
        }
      } else if (!haveKeywordMatch)
      {

        if (pattern.partSubstringMatches(part, "other")) {
          if (msgStart == 0) {
            msgStart = partIndex;
            if ((keyword != null) && (keyword.equals("other")))
            {


              haveKeywordMatch = true;
            }
          }
        } else {
          if (keyword == null) {
            keyword = selector.select(number - offset);
            if ((msgStart != 0) && (keyword.equals("other")))
            {

              haveKeywordMatch = true;
            }
          }
          
          if ((!haveKeywordMatch) && (pattern.partSubstringMatches(part, keyword)))
          {
            msgStart = partIndex;
            
            haveKeywordMatch = true;
          }
        }
      }
      partIndex = pattern.getLimitPartIndex(partIndex);
      partIndex++; } while (partIndex < count);
    return msgStart;
  }
  










  private final class PluralSelectorAdapter
    implements PluralFormat.PluralSelector
  {
    private PluralSelectorAdapter() {}
    









    public String select(double number) { return pluralRules.select(number); }
  }
  
  private transient PluralSelectorAdapter pluralRulesWrapper = new PluralSelectorAdapter(null);
  










  public final String format(double number)
  {
    if ((msgPattern == null) || (msgPattern.countParts() == 0)) {
      return numberFormat.format(number);
    }
    

    int partIndex = findSubMessage(msgPattern, 0, pluralRulesWrapper, number);
    

    number -= offset;
    StringBuilder result = null;
    int prevIndex = msgPattern.getPart(partIndex).getLimit();
    for (;;) {
      MessagePattern.Part part = msgPattern.getPart(++partIndex);
      MessagePattern.Part.Type type = part.getType();
      int index = part.getIndex();
      if (type == MessagePattern.Part.Type.MSG_LIMIT) {
        if (result == null) {
          return pattern.substring(prevIndex, index);
        }
        return result.append(pattern, prevIndex, index).toString();
      }
      if ((type == MessagePattern.Part.Type.REPLACE_NUMBER) || ((type == MessagePattern.Part.Type.SKIP_SYNTAX) && (msgPattern.jdkAposMode())))
      {

        if (result == null) {
          result = new StringBuilder();
        }
        result.append(pattern, prevIndex, index);
        if (type == MessagePattern.Part.Type.REPLACE_NUMBER) {
          result.append(numberFormat.format(number));
        }
        prevIndex = part.getLimit();
      } else if (type == MessagePattern.Part.Type.ARG_START) {
        if (result == null) {
          result = new StringBuilder();
        }
        result.append(pattern, prevIndex, index);
        prevIndex = index;
        partIndex = msgPattern.getLimitPartIndex(partIndex);
        index = msgPattern.getPart(partIndex).getLimit();
        MessagePattern.appendReducedApostrophes(pattern, prevIndex, index, result);
        prevIndex = index;
      }
    }
  }
  

















  public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos)
  {
    if ((number instanceof Number)) {
      toAppendTo.append(format(((Number)number).doubleValue()));
      return toAppendTo;
    }
    throw new IllegalArgumentException("'" + number + "' is not a Number");
  }
  









  public Number parse(String text, ParsePosition parsePosition)
  {
    throw new UnsupportedOperationException();
  }
  









  public Object parseObject(String source, ParsePosition pos)
  {
    throw new UnsupportedOperationException();
  }
  












  /**
   * @deprecated
   */
  public void setLocale(ULocale ulocale)
  {
    if (ulocale == null) {
      ulocale = ULocale.getDefault(ULocale.Category.FORMAT);
    }
    init(null, PluralRules.PluralType.CARDINAL, ulocale);
  }
  






  public void setNumberFormat(NumberFormat format)
  {
    numberFormat = format;
  }
  




  public boolean equals(Object rhs)
  {
    if (this == rhs) {
      return true;
    }
    if ((rhs == null) || (getClass() != rhs.getClass())) {
      return false;
    }
    PluralFormat pf = (PluralFormat)rhs;
    return (Utility.objectEquals(ulocale, ulocale)) && (Utility.objectEquals(pluralRules, pluralRules)) && (Utility.objectEquals(msgPattern, msgPattern)) && (Utility.objectEquals(numberFormat, numberFormat));
  }
  









  public boolean equals(PluralFormat rhs)
  {
    return equals(rhs);
  }
  




  public int hashCode()
  {
    return pluralRules.hashCode() ^ parsedValues.hashCode();
  }
  




  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("locale=" + ulocale);
    buf.append(", rules='" + pluralRules + "'");
    buf.append(", pattern='" + pattern + "'");
    buf.append(", format='" + numberFormat + "'");
    return buf.toString();
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    pluralRulesWrapper = new PluralSelectorAdapter(null);
    

    parsedValues = null;
    if (pattern != null) {
      applyPattern(pattern);
    }
  }
  
  static abstract interface PluralSelector
  {
    public abstract String select(double paramDouble);
  }
}
