package com.ibm.icu.text;

import java.math.BigInteger;


































































final class DigitList
{
  public static final int MAX_LONG_DIGITS = 19;
  public static final int DBL_DIG = 17;
  public int decimalAt = 0;
  public int count = 0;
  public byte[] digits = new byte[19];
  private static byte[] LONG_MIN_REP;
  
  private final void ensureCapacity(int digitCapacity, int digitsToCopy) { if (digitCapacity > digits.length) {
      byte[] newDigits = new byte[digitCapacity * 2];
      System.arraycopy(digits, 0, newDigits, 0, digitsToCopy);
      digits = newDigits;
    }
  }
  



  boolean isZero()
  {
    for (int i = 0; i < count; i++) if (digits[i] != 48) return false;
    return true;
  }
  















  public void append(int digit)
  {
    ensureCapacity(count + 1, count);
    digits[(count++)] = ((byte)digit);
  }
  
  public byte getDigitValue(int i) {
    return (byte)(digits[i] - 48);
  }
  




  public final double getDouble()
  {
    if (count == 0) return 0.0D;
    StringBuilder temp = new StringBuilder(count);
    temp.append('.');
    for (int i = 0; i < count; i++) temp.append((char)digits[i]);
    temp.append('E');
    temp.append(Integer.toString(decimalAt));
    return Double.valueOf(temp.toString()).doubleValue();
  }
  







  public final long getLong()
  {
    if (count == 0) { return 0L;
    }
    


    if (isLongMIN_VALUE()) { return Long.MIN_VALUE;
    }
    StringBuilder temp = new StringBuilder(count);
    for (int i = 0; i < decimalAt; i++)
    {
      temp.append(i < count ? (char)digits[i] : '0');
    }
    return Long.parseLong(temp.toString());
  }
  







  public BigInteger getBigInteger(boolean isPositive)
  {
    if (isZero()) { return BigInteger.valueOf(0L);
    }
    













    int len = decimalAt > count ? decimalAt : count;
    if (!isPositive) {
      len++;
    }
    char[] text = new char[len];
    int n = 0;
    if (!isPositive) {
      text[0] = '-';
      for (int i = 0; i < count; i++) {
        text[(i + 1)] = ((char)digits[i]);
      }
      n = count + 1;
    } else {
      for (int i = 0; i < count; i++) {
        text[i] = ((char)digits[i]);
      }
      n = count;
    }
    for (int i = n; i < text.length; i++) {
      text[i] = '0';
    }
    return new BigInteger(new String(text));
  }
  
  private String getStringRep(boolean isPositive)
  {
    if (isZero()) return "0";
    StringBuilder stringRep = new StringBuilder(count + 1);
    if (!isPositive) {
      stringRep.append('-');
    }
    int d = decimalAt;
    if (d < 0) {
      stringRep.append('.');
      while (d < 0) {
        stringRep.append('0');
        d++;
      }
      d = -1;
    }
    for (int i = 0; i < count; i++) {
      if (d == i) {
        stringRep.append('.');
      }
      stringRep.append((char)digits[i]);
    }
    while (d-- > count) {
      stringRep.append('0');
    }
    return stringRep.toString();
  }
  








  public java.math.BigDecimal getBigDecimal(boolean isPositive)
  {
    if (isZero()) {
      return java.math.BigDecimal.valueOf(0L);
    }
    



    long scale = count - decimalAt;
    if (scale > 0L) {
      int numDigits = count;
      if (scale > 2147483647L)
      {
        long numShift = scale - 2147483647L;
        if (numShift < count) {
          numDigits = (int)(numDigits - numShift);
        }
        else {
          return new java.math.BigDecimal(0);
        }
      }
      StringBuilder significantDigits = new StringBuilder(numDigits + 1);
      if (!isPositive) {
        significantDigits.append('-');
      }
      for (int i = 0; i < numDigits; i++) {
        significantDigits.append((char)digits[i]);
      }
      BigInteger unscaledVal = new BigInteger(significantDigits.toString());
      return new java.math.BigDecimal(unscaledVal, (int)scale);
    }
    


    return new java.math.BigDecimal(getStringRep(isPositive));
  }
  








  public com.ibm.icu.math.BigDecimal getBigDecimalICU(boolean isPositive)
  {
    if (isZero()) {
      return com.ibm.icu.math.BigDecimal.valueOf(0L);
    }
    



    long scale = count - decimalAt;
    if (scale > 0L) {
      int numDigits = count;
      if (scale > 2147483647L)
      {
        long numShift = scale - 2147483647L;
        if (numShift < count) {
          numDigits = (int)(numDigits - numShift);
        }
        else {
          return new com.ibm.icu.math.BigDecimal(0);
        }
      }
      StringBuilder significantDigits = new StringBuilder(numDigits + 1);
      if (!isPositive) {
        significantDigits.append('-');
      }
      for (int i = 0; i < numDigits; i++) {
        significantDigits.append((char)digits[i]);
      }
      BigInteger unscaledVal = new BigInteger(significantDigits.toString());
      return new com.ibm.icu.math.BigDecimal(unscaledVal, (int)scale);
    }
    return new com.ibm.icu.math.BigDecimal(getStringRep(isPositive));
  }
  






  boolean isIntegral()
  {
    while ((count > 0) && (digits[(count - 1)] == 48)) count -= 1;
    return (count == 0) || (decimalAt >= count);
  }
  






































































  final void set(double source, int maximumDigits, boolean fixedPoint)
  {
    if (source == 0.0D) { source = 0.0D;
    }
    
    String rep = Double.toString(source);
    
    set(rep, 19);
    
    if (fixedPoint)
    {




      if (-decimalAt > maximumDigits) {
        count = 0;
        return; }
      if (-decimalAt == maximumDigits) {
        if (shouldRoundUp(0)) {
          count = 1;
          decimalAt += 1;
          digits[0] = 49;
        } else {
          count = 0;
        }
        return;
      }
    }
    


    while ((count > 1) && (digits[(count - 1)] == 48)) {
      count -= 1;
    }
    

    round(maximumDigits == 0 ? -1 : fixedPoint ? maximumDigits + decimalAt : maximumDigits);
  }
  




  private void set(String rep, int maxCount)
  {
    decimalAt = -1;
    count = 0;
    int exponent = 0;
    

    int leadingZerosAfterDecimal = 0;
    boolean nonZeroDigitSeen = false;
    
    int i = 0;
    if (rep.charAt(i) == '-') {
      i++;
    }
    for (; i < rep.length(); i++) {
      char c = rep.charAt(i);
      if (c == '.') {
        decimalAt = count;
      } else { if ((c == 'e') || (c == 'E')) {
          i++;
          
          if (rep.charAt(i) == '+') {
            i++;
          }
          exponent = Integer.valueOf(rep.substring(i)).intValue();
          break; }
        if (count < maxCount) {
          if (!nonZeroDigitSeen) {
            nonZeroDigitSeen = c != '0';
            if ((!nonZeroDigitSeen) && (decimalAt != -1)) {
              leadingZerosAfterDecimal++;
            }
          }
          
          if (nonZeroDigitSeen) {
            ensureCapacity(count + 1, count);
            digits[(count++)] = ((byte)c);
          }
        }
      } }
    if (decimalAt == -1) {
      decimalAt = count;
    }
    decimalAt += exponent - leadingZerosAfterDecimal;
  }
  
















  private boolean shouldRoundUp(int maximumDigits)
  {
    if (maximumDigits < count) {
      if (digits[maximumDigits] > 53)
        return true;
      if (digits[maximumDigits] == 53) {
        for (int i = maximumDigits + 1; i < count; i++) {
          if (digits[i] != 48) {
            return true;
          }
        }
        return (maximumDigits > 0) && (digits[(maximumDigits - 1)] % 2 != 0);
      }
    }
    return false;
  }
  








  public final void round(int maximumDigits)
  {
    if ((maximumDigits >= 0) && (maximumDigits < count)) {
      if (shouldRoundUp(maximumDigits))
      {

        for (;;)
        {

          maximumDigits--;
          if (maximumDigits < 0)
          {


            digits[0] = 49;
            decimalAt += 1;
            maximumDigits = 0;
          }
          else
          {
            int tmp55_54 = maximumDigits; byte[] tmp55_51 = digits;tmp55_51[tmp55_54] = ((byte)(tmp55_51[tmp55_54] + 1));
            if (digits[maximumDigits] <= 57) break;
          }
        }
        maximumDigits++;
      }
      count = maximumDigits;
    }
    


    while ((count > 1) && (digits[(count - 1)] == 48)) {
      count -= 1;
    }
  }
  



  public final void set(long source)
  {
    set(source, 0);
  }
  















  public final void set(long source, int maximumDigits)
  {
    if (source <= 0L) {
      if (source == Long.MIN_VALUE) {
        decimalAt = (this.count = 19);
        System.arraycopy(LONG_MIN_REP, 0, digits, 0, count);
      } else {
        count = 0;
        decimalAt = 0;
      }
    } else {
      int left = 19;
      
      while (source > 0L) {
        digits[(--left)] = ((byte)(int)(48L + source % 10L));
        source /= 10L;
      }
      decimalAt = (19 - left);
      


      for (int right = 18; digits[right] == 48; right--) {}
      count = (right - left + 1);
      System.arraycopy(digits, left, digits, 0, count);
    }
    if (maximumDigits > 0) { round(maximumDigits);
    }
  }
  






  public final void set(BigInteger source, int maximumDigits)
  {
    String stringDigits = source.toString();
    
    count = (this.decimalAt = stringDigits.length());
    

    while ((count > 1) && (stringDigits.charAt(count - 1) == '0')) { count -= 1;
    }
    int offset = 0;
    if (stringDigits.charAt(0) == '-') {
      offset++;
      count -= 1;
      decimalAt -= 1;
    }
    
    ensureCapacity(count, 0);
    for (int i = 0; i < count; i++) {
      digits[i] = ((byte)stringDigits.charAt(i + offset));
    }
    
    if (maximumDigits > 0) { round(maximumDigits);
    }
  }
  




























































  private void setBigDecimalDigits(String stringDigits, int maximumDigits, boolean fixedPoint)
  {
    set(stringDigits, stringDigits.length());
    








    round(maximumDigits == 0 ? -1 : fixedPoint ? maximumDigits + decimalAt : maximumDigits);
  }
  










  public final void set(java.math.BigDecimal source, int maximumDigits, boolean fixedPoint)
  {
    setBigDecimalDigits(source.toString(), maximumDigits, fixedPoint);
  }
  










  public final void set(com.ibm.icu.math.BigDecimal source, int maximumDigits, boolean fixedPoint)
  {
    setBigDecimalDigits(source.toString(), maximumDigits, fixedPoint);
  }
  




  private boolean isLongMIN_VALUE()
  {
    if ((decimalAt != count) || (count != 19)) {
      return false;
    }
    for (int i = 0; i < count; i++)
    {
      if (digits[i] != LONG_MIN_REP[i]) { return false;
      }
    }
    return true;
  }
  



  static
  {
    String s = Long.toString(Long.MIN_VALUE);
    LONG_MIN_REP = new byte[19];
    for (int i = 0; i < 19; i++)
    {
      LONG_MIN_REP[i] = ((byte)s.charAt(i + 1));
    }
  }
  





































  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!(obj instanceof DigitList))
      return false;
    DigitList other = (DigitList)obj;
    if ((count != count) || (decimalAt != decimalAt))
    {
      return false; }
    for (int i = 0; i < count; i++)
      if (digits[i] != digits[i])
        return false;
    return true;
  }
  


  public int hashCode()
  {
    int hashcode = decimalAt;
    
    for (int i = 0; i < count; i++) {
      hashcode = hashcode * 37 + digits[i];
    }
    return hashcode;
  }
  
  public String toString()
  {
    if (isZero()) return "0";
    StringBuilder buf = new StringBuilder("0.");
    for (int i = 0; i < count; i++) buf.append((char)digits[i]);
    buf.append("x10^");
    buf.append(decimalAt);
    return buf.toString();
  }
  
  DigitList() {}
}
