package com.ibm.icu.text;

import com.ibm.icu.impl.PatternProps;
import java.text.ParsePosition;












































final class NFRule
{
  public static final int NEGATIVE_NUMBER_RULE = -1;
  public static final int IMPROPER_FRACTION_RULE = -2;
  public static final int PROPER_FRACTION_RULE = -3;
  public static final int MASTER_RULE = -4;
  private long baseValue;
  private int radix = 10;
  




  private short exponent = 0;
  





  private String ruleText = null;
  




  private NFSubstitution sub1 = null;
  




  private NFSubstitution sub2 = null;
  



  private RuleBasedNumberFormat formatter = null;
  




















  public static Object makeRules(String description, NFRuleSet owner, NFRule predecessor, RuleBasedNumberFormat ownersOwner)
  {
    NFRule rule1 = new NFRule(ownersOwner);
    description = rule1.parseRuleDescriptor(description);
    


    int brack1 = description.indexOf("[");
    int brack2 = description.indexOf("]");
    




    if ((brack1 == -1) || (brack2 == -1) || (brack1 > brack2) || (rule1.getBaseValue() == -3L) || (rule1.getBaseValue() == -1L))
    {

      ruleText = description;
      rule1.extractSubstitutions(owner, predecessor, ownersOwner);
      return rule1;
    }
    

    NFRule rule2 = null;
    StringBuilder sbuf = new StringBuilder();
    



    if (((baseValue > 0L) && (baseValue % Math.pow(radix, exponent) == 0.0D)) || (baseValue == -2L) || (baseValue == -4L))
    {








      rule2 = new NFRule(ownersOwner);
      if (baseValue >= 0L) {
        baseValue = baseValue;
        if (!owner.isFractionSet()) {
          baseValue += 1L;

        }
        


      }
      else if (baseValue == -2L) {
        baseValue = -3L;




      }
      else if (baseValue == -4L) {
        baseValue = baseValue;
        baseValue = -2L;
      }
      


      radix = radix;
      exponent = exponent;
      


      sbuf.append(description.substring(0, brack1));
      if (brack2 + 1 < description.length()) {
        sbuf.append(description.substring(brack2 + 1));
      }
      ruleText = sbuf.toString();
      rule2.extractSubstitutions(owner, predecessor, ownersOwner);
    }
    



    sbuf.setLength(0);
    sbuf.append(description.substring(0, brack1));
    sbuf.append(description.substring(brack1 + 1, brack2));
    if (brack2 + 1 < description.length()) {
      sbuf.append(description.substring(brack2 + 1));
    }
    ruleText = sbuf.toString();
    rule1.extractSubstitutions(owner, predecessor, ownersOwner);
    





    if (rule2 == null) {
      return rule1;
    }
    return new NFRule[] { rule2, rule1 };
  }
  





  public NFRule(RuleBasedNumberFormat formatter)
  {
    this.formatter = formatter;
  }
  
















  private String parseRuleDescriptor(String description)
  {
    int p = description.indexOf(":");
    if (p == -1) {
      setBaseValue(0L);

    }
    else
    {
      String descriptor = description.substring(0, p);
      p++;
      while ((p < description.length()) && (PatternProps.isWhiteSpace(description.charAt(p))))
        p++;
      description = description.substring(p);
      



      if (descriptor.equals("-x")) {
        setBaseValue(-1L);
      }
      else if (descriptor.equals("x.x")) {
        setBaseValue(-2L);
      }
      else if (descriptor.equals("0.x")) {
        setBaseValue(-3L);
      }
      else if (descriptor.equals("x.0")) {
        setBaseValue(-4L);



      }
      else if ((descriptor.charAt(0) >= '0') && (descriptor.charAt(0) <= '9')) {
        StringBuilder tempValue = new StringBuilder();
        p = 0;
        char c = ' ';
        




        while (p < descriptor.length()) {
          c = descriptor.charAt(p);
          if ((c >= '0') && (c <= '9')) {
            tempValue.append(c);
          } else {
            if ((c == '/') || (c == '>')) {
              break;
            }
            if ((!PatternProps.isWhiteSpace(c)) && (c != ',') && (c != '.'))
            {

              throw new IllegalArgumentException("Illegal character in rule descriptor"); }
          }
          p++;
        }
        



        setBaseValue(Long.parseLong(tempValue.toString()));
        




        if (c == '/') {
          tempValue.setLength(0);
          p++;
          while (p < descriptor.length()) {
            c = descriptor.charAt(p);
            if ((c >= '0') && (c <= '9')) {
              tempValue.append(c);
            } else {
              if (c == '>') {
                break;
              }
              if ((!PatternProps.isWhiteSpace(c)) && (c != ',') && (c != '.'))
              {

                throw new IllegalArgumentException("Illegal character is rule descriptor"); }
            }
            p++;
          }
          


          radix = Integer.parseInt(tempValue.toString());
          if (radix == 0) {
            throw new IllegalArgumentException("Rule can't have radix of 0");
          }
          exponent = expectedExponent();
        }
        





        if (c == '>') {
          while (p < descriptor.length()) {
            c = descriptor.charAt(p);
            if ((c == '>') && (exponent > 0)) {
              exponent = ((short)(exponent - 1));
            } else {
              throw new IllegalArgumentException("Illegal character in rule descriptor");
            }
            p++;
          }
        }
      }
    }
    



    if ((description.length() > 0) && (description.charAt(0) == '\'')) {
      description = description.substring(1);
    }
    


    return description;
  }
  









  private void extractSubstitutions(NFRuleSet owner, NFRule predecessor, RuleBasedNumberFormat ownersOwner)
  {
    sub1 = extractSubstitution(owner, predecessor, ownersOwner);
    sub2 = extractSubstitution(owner, predecessor, ownersOwner);
  }
  













  private NFSubstitution extractSubstitution(NFRuleSet owner, NFRule predecessor, RuleBasedNumberFormat ownersOwner)
  {
    NFSubstitution result = null;
    




    int subStart = indexOfAny(new String[] { "<<", "<%", "<#", "<0", ">>", ">%", ">#", ">0", "=%", "=#", "=0" });
    




    if (subStart == -1) {
      return NFSubstitution.makeSubstitution(ruleText.length(), this, predecessor, owner, ownersOwner, "");
    }
    
    int subEnd;
    
    int subEnd;
    if (ruleText.substring(subStart).startsWith(">>>")) {
      subEnd = subStart + 2;

    }
    else
    {
      char c = ruleText.charAt(subStart);
      subEnd = ruleText.indexOf(c, subStart + 1);
      
      if ((c == '<') && (subEnd != -1) && (subEnd < ruleText.length() - 1) && (ruleText.charAt(subEnd + 1) == c))
      {



        subEnd++;
      }
    }
    



    if (subEnd == -1) {
      return NFSubstitution.makeSubstitution(ruleText.length(), this, predecessor, owner, ownersOwner, "");
    }
    




    result = NFSubstitution.makeSubstitution(subStart, this, predecessor, owner, ownersOwner, ruleText.substring(subStart, subEnd + 1));
    


    ruleText = (ruleText.substring(0, subStart) + ruleText.substring(subEnd + 1));
    return result;
  }
  







  public final void setBaseValue(long newBaseValue)
  {
    baseValue = newBaseValue;
    





    if (baseValue >= 1L) {
      radix = 10;
      exponent = expectedExponent();
      




      if (sub1 != null) {
        sub1.setDivisor(radix, exponent);
      }
      if (sub2 != null) {
        sub2.setDivisor(radix, exponent);
      }
      
    }
    else
    {
      radix = 10;
      exponent = 0;
    }
  }
  







  private short expectedExponent()
  {
    if ((radix == 0) || (baseValue < 1L)) {
      return 0;
    }
    



    short tempResult = (short)(int)(Math.log(baseValue) / Math.log(radix));
    if (Math.pow(radix, tempResult + 1) <= baseValue) {
      return (short)(tempResult + 1);
    }
    return tempResult;
  }
  










  private int indexOfAny(String[] strings)
  {
    int result = -1;
    for (int i = 0; i < strings.length; i++) {
      int pos = ruleText.indexOf(strings[i]);
      if ((pos != -1) && ((result == -1) || (pos < result))) {
        result = pos;
      }
    }
    return result;
  }
  








  public boolean equals(Object that)
  {
    if ((that instanceof NFRule)) {
      NFRule that2 = (NFRule)that;
      
      return (baseValue == baseValue) && (radix == radix) && (exponent == exponent) && (ruleText.equals(ruleText)) && (sub1.equals(sub1)) && (sub2.equals(sub2));
    }
    




    return false;
  }
  
  public int hashCode() {
    if (!$assertionsDisabled) throw new AssertionError("hashCode not designed");
    return 42;
  }
  





  public String toString()
  {
    StringBuilder result = new StringBuilder();
    

    if (baseValue == -1L) {
      result.append("-x: ");
    }
    else if (baseValue == -2L) {
      result.append("x.x: ");
    }
    else if (baseValue == -3L) {
      result.append("0.x: ");
    }
    else if (baseValue == -4L) {
      result.append("x.0: ");



    }
    else
    {



      result.append(String.valueOf(baseValue));
      if (radix != 10) {
        result.append('/');
        result.append(String.valueOf(radix));
      }
      int numCarets = expectedExponent() - exponent;
      for (int i = 0; i < numCarets; i++)
        result.append('>');
      result.append(": ");
    }
    



    if ((ruleText.startsWith(" ")) && ((sub1 == null) || (sub1.getPos() != 0))) {
      result.append("'");
    }
    


    StringBuilder ruleTextCopy = new StringBuilder(ruleText);
    ruleTextCopy.insert(sub2.getPos(), sub2.toString());
    ruleTextCopy.insert(sub1.getPos(), sub1.toString());
    result.append(ruleTextCopy.toString());
    


    result.append(';');
    return result.toString();
  }
  







  public final long getBaseValue()
  {
    return baseValue;
  }
  




  public double getDivisor()
  {
    return Math.pow(radix, exponent);
  }
  

















  public void doFormat(long number, StringBuffer toInsertInto, int pos)
  {
    toInsertInto.insert(pos, ruleText);
    sub2.doSubstitution(number, toInsertInto, pos);
    sub1.doSubstitution(number, toInsertInto, pos);
  }
  














  public void doFormat(double number, StringBuffer toInsertInto, int pos)
  {
    toInsertInto.insert(pos, ruleText);
    sub2.doSubstitution(number, toInsertInto, pos);
    sub1.doSubstitution(number, toInsertInto, pos);
  }
  























  public boolean shouldRollBack(double number)
  {
    if ((sub1.isModulusSubstitution()) || (sub2.isModulusSubstitution())) {
      return (number % Math.pow(radix, exponent) == 0.0D) && (baseValue % Math.pow(radix, exponent) != 0.0D);
    }
    
    return false;
  }
  

























  public Number doParse(String text, ParsePosition parsePosition, boolean isFractionRule, double upperBound)
  {
    ParsePosition pp = new ParsePosition(0);
    




    String workText = stripPrefix(text, ruleText.substring(0, sub1.getPos()), pp);
    int prefixLength = text.length() - workText.length();
    
    if ((pp.getIndex() == 0) && (sub1.getPos() != 0))
    {

      return Long.valueOf(0L);
    }
    





























    int highWaterMark = 0;
    double result = 0.0D;
    int start = 0;
    double tempBaseValue = Math.max(0L, baseValue);
    



    do
    {
      pp.setIndex(0);
      double partialResult = matchToDelimiter(workText, start, tempBaseValue, ruleText.substring(sub1.getPos(), sub2.getPos()), pp, sub1, upperBound).doubleValue();
      






      if ((pp.getIndex() != 0) || (sub1.isNullSubstitution())) {
        start = pp.getIndex();
        
        String workText2 = workText.substring(pp.getIndex());
        ParsePosition pp2 = new ParsePosition(0);
        




        partialResult = matchToDelimiter(workText2, 0, partialResult, ruleText.substring(sub2.getPos()), pp2, sub2, upperBound).doubleValue();
        





        if (((pp2.getIndex() != 0) || (sub2.isNullSubstitution())) && 
          (prefixLength + pp.getIndex() + pp2.getIndex() > highWaterMark)) {
          highWaterMark = prefixLength + pp.getIndex() + pp2.getIndex();
          result = partialResult;






        }
        





      }
      





    }
    while ((sub1.getPos() != sub2.getPos()) && (pp.getIndex() > 0) && (pp.getIndex() < workText.length()) && (pp.getIndex() != start));
    




    parsePosition.setIndex(highWaterMark);
    










    if ((isFractionRule) && (highWaterMark > 0) && (sub1.isNullSubstitution())) {
      result = 1.0D / result;
    }
    

    if (result == result) {
      return Long.valueOf(result);
    }
    return new Double(result);
  }
  

















  private String stripPrefix(String text, String prefix, ParsePosition pp)
  {
    if (prefix.length() == 0) {
      return text;
    }
    



    int pfl = prefixLength(text, prefix);
    if (pfl != 0)
    {

      pp.setIndex(pp.getIndex() + pfl);
      return text.substring(pfl);
    }
    

    return text;
  }
  
































  private Number matchToDelimiter(String text, int startPos, double baseVal, String delimiter, ParsePosition pp, NFSubstitution sub, double upperBound)
  {
    if (!allIgnorable(delimiter)) {
      ParsePosition tempPP = new ParsePosition(0);
      





      int[] temp = findText(text, delimiter, startPos);
      int dPos = temp[0];
      int dLen = temp[1];
      


      while (dPos >= 0) {
        String subText = text.substring(0, dPos);
        if (subText.length() > 0) {
          Number tempResult = sub.doParse(subText, tempPP, baseVal, upperBound, formatter.lenientParseEnabled());
          







          if (tempPP.getIndex() == dPos) {
            pp.setIndex(dPos + dLen);
            return tempResult;
          }
        }
        











        tempPP.setIndex(0);
        temp = findText(text, delimiter, dPos + dLen);
        dPos = temp[0];
        dLen = temp[1];
      }
      

      pp.setIndex(0);
      return Long.valueOf(0L);
    }
    




    ParsePosition tempPP = new ParsePosition(0);
    Number result = Long.valueOf(0L);
    


    Number tempResult = sub.doParse(text, tempPP, baseVal, upperBound, formatter.lenientParseEnabled());
    
    if ((tempPP.getIndex() != 0) || (sub.isNullSubstitution()))
    {



      pp.setIndex(tempPP.getIndex());
      if (tempResult != null) {
        result = tempResult;
      }
    }
    






    return result;
  }
  
















  private int prefixLength(String str, String prefix)
  {
    if (prefix.length() == 0) {
      return 0;
    }
    
    RbnfLenientScanner scanner = formatter.getLenientScanner();
    if (scanner != null) {
      return scanner.prefixLength(str, prefix);
    }
    

















































































































    if (str.startsWith(prefix)) {
      return prefix.length();
    }
    return 0;
  }
  



































  private int[] findText(String str, String key, int startingAt)
  {
    RbnfLenientScanner scanner = formatter.getLenientScanner();
    
    if (scanner == null) {
      return new int[] { str.indexOf(key, startingAt), key.length() };
    }
    


    return scanner.findText(str, key, startingAt);
  }
  


























































































  private boolean allIgnorable(String str)
  {
    if (str.length() == 0) {
      return true;
    }
    RbnfLenientScanner scanner = formatter.getLenientScanner();
    if (scanner != null) {
      return scanner.allIgnorable(str);
    }
    return false;
  }
}
