package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.MissingResourceException;













public final class DateNumberFormat
  extends NumberFormat
{
  private static final long serialVersionUID = -6315692826916346953L;
  private char[] digits;
  private char zeroDigit;
  private char minusSign;
  private boolean positiveOnly = false;
  
  private transient char[] decimalBuf = new char[20];
  
  private static SimpleCache<ULocale, char[]> CACHE = new SimpleCache();
  private int maxIntDigits;
  private int minIntDigits;
  private static final long PARSE_THRESHOLD = 922337203685477579L;
  
  public DateNumberFormat(ULocale loc, String digitString, String nsName) {
    initialize(loc, digitString, nsName);
  }
  
  public DateNumberFormat(ULocale loc, char zeroDigit, String nsName) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < 10; i++) {
      buf.append((char)(zeroDigit + i));
    }
    initialize(loc, buf.toString(), nsName);
  }
  
  private void initialize(ULocale loc, String digitString, String nsName) {
    char[] elems = (char[])CACHE.get(loc);
    if (elems == null)
    {

      ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", loc);
      String minusString;
      try { minusString = rb.getStringWithFallback("NumberElements/" + nsName + "/symbols/minusSign");
      } catch (MissingResourceException ex) { String minusString;
        if (!nsName.equals("latn")) {
          try {
            minusString = rb.getStringWithFallback("NumberElements/latn/symbols/minusSign");
          } catch (MissingResourceException ex1) {
            minusString = "-";
          }
        } else {
          minusString = "-";
        }
      }
      elems = new char[11];
      for (int i = 0; i < 10; i++) {
        elems[i] = digitString.charAt(i);
      }
      elems[10] = minusString.charAt(0);
      CACHE.put(loc, elems);
    }
    
    digits = new char[10];
    System.arraycopy(elems, 0, digits, 0, 10);
    zeroDigit = digits[0];
    
    minusSign = elems[10];
  }
  
  public void setMaximumIntegerDigits(int newValue) {
    maxIntDigits = newValue;
  }
  
  public int getMaximumIntegerDigits() {
    return maxIntDigits;
  }
  
  public void setMinimumIntegerDigits(int newValue) {
    minIntDigits = newValue;
  }
  
  public int getMinimumIntegerDigits() {
    return minIntDigits;
  }
  
  public void setParsePositiveOnly(boolean isPositiveOnly)
  {
    positiveOnly = isPositiveOnly;
  }
  
  public char getZeroDigit() {
    return zeroDigit;
  }
  
  public void setZeroDigit(char zero) {
    zeroDigit = zero;
    if (digits == null) {
      digits = new char[10];
    }
    digits[0] = zero;
    for (int i = 1; i < 10; i++) {
      digits[i] = ((char)(zero + i));
    }
  }
  
  public char[] getDigits() {
    return digits;
  }
  
  public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos)
  {
    throw new UnsupportedOperationException("StringBuffer format(double, StringBuffer, FieldPostion) is not implemented");
  }
  

  public StringBuffer format(long numberL, StringBuffer toAppendTo, FieldPosition pos)
  {
    if (numberL < 0L)
    {
      toAppendTo.append(minusSign);
      numberL = -numberL;
    }
    



    int number = (int)numberL;
    
    int limit = decimalBuf.length < maxIntDigits ? decimalBuf.length : maxIntDigits;
    int index = limit - 1;
    for (;;) {
      decimalBuf[index] = digits[(number % 10)];
      number /= 10;
      if ((index == 0) || (number == 0)) {
        break;
      }
      index--;
    }
    for (int padding = minIntDigits - (limit - index); 
        padding > 0; padding--) {
      decimalBuf[(--index)] = digits[0];
    }
    int length = limit - index;
    toAppendTo.append(decimalBuf, index, length);
    pos.setBeginIndex(0);
    if (pos.getField() == 0) {
      pos.setEndIndex(length);
    } else {
      pos.setEndIndex(0);
    }
    return toAppendTo;
  }
  
  public StringBuffer format(BigInteger number, StringBuffer toAppendTo, FieldPosition pos)
  {
    throw new UnsupportedOperationException("StringBuffer format(BigInteger, StringBuffer, FieldPostion) is not implemented");
  }
  
  public StringBuffer format(java.math.BigDecimal number, StringBuffer toAppendTo, FieldPosition pos)
  {
    throw new UnsupportedOperationException("StringBuffer format(BigDecimal, StringBuffer, FieldPostion) is not implemented");
  }
  
  public StringBuffer format(com.ibm.icu.math.BigDecimal number, StringBuffer toAppendTo, FieldPosition pos)
  {
    throw new UnsupportedOperationException("StringBuffer format(BigDecimal, StringBuffer, FieldPostion) is not implemented");
  }
  




  public Number parse(String text, ParsePosition parsePosition)
  {
    long num = 0L;
    boolean sawNumber = false;
    boolean negative = false;
    int base = parsePosition.getIndex();
    for (int offset = 0; 
        base + offset < text.length(); offset++) {
      char ch = text.charAt(base + offset);
      if ((offset == 0) && (ch == minusSign)) {
        if (positiveOnly) {
          break;
        }
        negative = true;
      } else {
        int digit = ch - digits[0];
        if ((digit < 0) || (9 < digit)) {
          digit = UCharacter.digit(ch);
        }
        if ((digit < 0) || (9 < digit)) {
          for (digit = 0; digit < 10; digit++) {
            if (ch == digits[digit]) {
              break;
            }
          }
        }
        if ((0 > digit) || (digit > 9) || (num >= 922337203685477579L)) break;
        sawNumber = true;
        num = num * 10L + digit;
      }
    }
    


    Number result = null;
    if (sawNumber) {
      num = negative ? num * -1L : num;
      result = Long.valueOf(num);
      parsePosition.setIndex(base + offset);
    }
    return result;
  }
  
  public boolean equals(Object obj) {
    if ((obj == null) || (!super.equals(obj)) || (!(obj instanceof DateNumberFormat))) {
      return false;
    }
    DateNumberFormat other = (DateNumberFormat)obj;
    return (maxIntDigits == maxIntDigits) && (minIntDigits == minIntDigits) && (minusSign == minusSign) && (positiveOnly == positiveOnly) && (Arrays.equals(digits, digits));
  }
  



  public int hashCode()
  {
    return super.hashCode();
  }
  
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    if (digits == null) {
      setZeroDigit(zeroDigit);
    }
    
    decimalBuf = new char[20];
  }
}
