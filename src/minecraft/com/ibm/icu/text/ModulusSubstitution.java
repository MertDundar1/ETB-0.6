package com.ibm.icu.text;

import java.text.ParsePosition;


































































































































































































































































































































































































































































































































































































































































































































































































































































class ModulusSubstitution
  extends NFSubstitution
{
  double divisor;
  NFRule ruleToUse;
  
  ModulusSubstitution(int pos, double divisor, NFRule rulePredecessor, NFRuleSet ruleSet, RuleBasedNumberFormat formatter, String description)
  {
    super(pos, ruleSet, formatter, description);
    



    this.divisor = divisor;
    
    if (divisor == 0.0D) {
      throw new IllegalStateException("Substitution with bad divisor (" + divisor + ") " + description.substring(0, pos) + " | " + description.substring(pos));
    }
    






    if (description.equals(">>>")) {
      ruleToUse = rulePredecessor;
    } else {
      ruleToUse = null;
    }
  }
  





  public void setDivisor(int radix, int exponent)
  {
    divisor = Math.pow(radix, exponent);
    
    if (divisor == 0.0D) {
      throw new IllegalStateException("Substitution with bad divisor");
    }
  }
  









  public boolean equals(Object that)
  {
    if (super.equals(that)) {
      ModulusSubstitution that2 = (ModulusSubstitution)that;
      
      return divisor == divisor;
    }
    return false;
  }
  
  public int hashCode()
  {
    if (!$assertionsDisabled) throw new AssertionError("hashCode not designed");
    return 42;
  }
  














  public void doSubstitution(long number, StringBuffer toInsertInto, int position)
  {
    if (ruleToUse == null) {
      super.doSubstitution(number, toInsertInto, position);

    }
    else
    {
      long numberToFormat = transformNumber(number);
      ruleToUse.doFormat(numberToFormat, toInsertInto, position + pos);
    }
  }
  










  public void doSubstitution(double number, StringBuffer toInsertInto, int position)
  {
    if (ruleToUse == null) {
      super.doSubstitution(number, toInsertInto, position);

    }
    else
    {
      double numberToFormat = transformNumber(number);
      
      ruleToUse.doFormat(numberToFormat, toInsertInto, position + pos);
    }
  }
  





  public long transformNumber(long number)
  {
    return Math.floor(number % divisor);
  }
  





  public double transformNumber(double number)
  {
    return Math.floor(number % divisor);
  }
  















  public Number doParse(String text, ParsePosition parsePosition, double baseValue, double upperBound, boolean lenientParse)
  {
    if (ruleToUse == null) {
      return super.doParse(text, parsePosition, baseValue, upperBound, lenientParse);
    }
    



    Number tempResult = ruleToUse.doParse(text, parsePosition, false, upperBound);
    
    if (parsePosition.getIndex() != 0) {
      double result = tempResult.doubleValue();
      
      result = composeRuleValue(result, baseValue);
      if (result == result) {
        return Long.valueOf(result);
      }
      return new Double(result);
    }
    
    return tempResult;
  }
  














  public double composeRuleValue(double newRuleValue, double oldRuleValue)
  {
    return oldRuleValue - oldRuleValue % divisor + newRuleValue;
  }
  




  public double calcUpperBound(double oldUpperBound)
  {
    return divisor;
  }
  







  public boolean isModulusSubstitution()
  {
    return true;
  }
  



  char tokenChar()
  {
    return '>';
  }
}