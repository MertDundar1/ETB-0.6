package com.ibm.icu.text;

import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.Utility;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;






























final class NFRuleSet
{
  private String name;
  private NFRule[] rules;
  private NFRule negativeNumberRule = null;
  





  private NFRule[] fractionRules = new NFRule[3];
  








  private boolean isFractionRuleSet = false;
  



  private boolean isParseable = true;
  



  private int recursionCount = 0;
  






  private static final int RECURSION_LIMIT = 50;
  







  public NFRuleSet(String[] descriptions, int index)
    throws IllegalArgumentException
  {
    String description = descriptions[index];
    
    if (description.length() == 0) {
      throw new IllegalArgumentException("Empty rule set description");
    }
    




    if (description.charAt(0) == '%') {
      int pos = description.indexOf(':');
      if (pos == -1) {
        throw new IllegalArgumentException("Rule set name doesn't end in colon");
      }
      name = description.substring(0, pos);
      while ((pos < description.length()) && (PatternProps.isWhiteSpace(description.charAt(++pos)))) {}
      

      description = description.substring(pos);
      descriptions[index] = description;

    }
    else
    {

      name = "%default";
    }
    
    if (description.length() == 0) {
      throw new IllegalArgumentException("Empty rule set description");
    }
    
    if (name.endsWith("@noparse")) {
      name = name.substring(0, name.length() - 8);
      isParseable = false;
    }
  }
  

















  public void parseRules(String description, RuleBasedNumberFormat owner)
  {
    List<String> ruleDescriptions = new ArrayList();
    
    int oldP = 0;
    int p = description.indexOf(';');
    while (oldP != -1) {
      if (p != -1) {
        ruleDescriptions.add(description.substring(oldP, p));
        oldP = p + 1;
      } else {
        if (oldP < description.length()) {
          ruleDescriptions.add(description.substring(oldP));
        }
        oldP = p;
      }
      p = description.indexOf(';', p + 1);
    }
    



    List<NFRule> tempRules = new ArrayList();
    


    NFRule predecessor = null;
    for (int i = 0; i < ruleDescriptions.size(); i++)
    {


      Object temp = NFRule.makeRules((String)ruleDescriptions.get(i), this, predecessor, owner);
      

      if ((temp instanceof NFRule)) {
        tempRules.add((NFRule)temp);
        predecessor = (NFRule)temp;
      }
      else if ((temp instanceof NFRule[])) {
        NFRule[] rulesToAdd = (NFRule[])temp;
        
        for (int j = 0; j < rulesToAdd.length; j++) {
          tempRules.add(rulesToAdd[j]);
          predecessor = rulesToAdd[j];
        }
      }
    }
    
    ruleDescriptions = null;
    




    long defaultBaseValue = 0L;
    



    int i = 0;
    while (i < tempRules.size()) {
      NFRule rule = (NFRule)tempRules.get(i);
      
      switch ((int)rule.getBaseValue())
      {




      case 0: 
        rule.setBaseValue(defaultBaseValue);
        if (!isFractionRuleSet) {
          defaultBaseValue += 1L;
        }
        i++;
        break;
      


      case -1: 
        negativeNumberRule = rule;
        tempRules.remove(i);
        break;
      


      case -2: 
        fractionRules[0] = rule;
        tempRules.remove(i);
        break;
      


      case -3: 
        fractionRules[1] = rule;
        tempRules.remove(i);
        break;
      


      case -4: 
        fractionRules[2] = rule;
        tempRules.remove(i);
        break;
      



      default: 
        if (rule.getBaseValue() < defaultBaseValue) {
          throw new IllegalArgumentException("Rules are not in order, base: " + rule.getBaseValue() + " < " + defaultBaseValue);
        }
        
        defaultBaseValue = rule.getBaseValue();
        if (!isFractionRuleSet) {
          defaultBaseValue += 1L;
        }
        i++;
      }
      
    }
    


    rules = new NFRule[tempRules.size()];
    tempRules.toArray(rules);
  }
  







  public void makeIntoFractionRuleSet()
  {
    isFractionRuleSet = true;
  }
  









  public boolean equals(Object that)
  {
    if (!(that instanceof NFRuleSet)) {
      return false;
    }
    
    NFRuleSet that2 = (NFRuleSet)that;
    
    if ((!name.equals(name)) || (!Utility.objectEquals(negativeNumberRule, negativeNumberRule)) || (!Utility.objectEquals(fractionRules[0], fractionRules[0])) || (!Utility.objectEquals(fractionRules[1], fractionRules[1])) || (!Utility.objectEquals(fractionRules[2], fractionRules[2])) || (rules.length != rules.length) || (isFractionRuleSet != isFractionRuleSet))
    {






      return false;
    }
    

    for (int i = 0; i < rules.length; i++) {
      if (!rules[i].equals(rules[i])) {
        return false;
      }
    }
    

    return true;
  }
  
  public int hashCode()
  {
    if (!$assertionsDisabled) throw new AssertionError("hashCode not designed");
    return 42;
  }
  






  public String toString()
  {
    StringBuilder result = new StringBuilder();
    

    result.append(name + ":\n");
    

    for (int i = 0; i < rules.length; i++) {
      result.append("    " + rules[i].toString() + "\n");
    }
    

    if (negativeNumberRule != null) {
      result.append("    " + negativeNumberRule.toString() + "\n");
    }
    if (fractionRules[0] != null) {
      result.append("    " + fractionRules[0].toString() + "\n");
    }
    if (fractionRules[1] != null) {
      result.append("    " + fractionRules[1].toString() + "\n");
    }
    if (fractionRules[2] != null) {
      result.append("    " + fractionRules[2].toString() + "\n");
    }
    
    return result.toString();
  }
  







  public boolean isFractionSet()
  {
    return isFractionRuleSet;
  }
  



  public String getName()
  {
    return name;
  }
  



  public boolean isPublic()
  {
    return !name.startsWith("%%");
  }
  



  public boolean isParseable()
  {
    return isParseable;
  }
  











  public void format(long number, StringBuffer toInsertInto, int pos)
  {
    NFRule applicableRule = findNormalRule(number);
    
    if (++recursionCount >= 50) {
      recursionCount = 0;
      throw new IllegalStateException("Recursion limit exceeded when applying ruleSet " + name);
    }
    applicableRule.doFormat(number, toInsertInto, pos);
    recursionCount -= 1;
  }
  







  public void format(double number, StringBuffer toInsertInto, int pos)
  {
    NFRule applicableRule = findRule(number);
    
    if (++recursionCount >= 50) {
      recursionCount = 0;
      throw new IllegalStateException("Recursion limit exceeded when applying ruleSet " + name);
    }
    applicableRule.doFormat(number, toInsertInto, pos);
    recursionCount -= 1;
  }
  





  private NFRule findRule(double number)
  {
    if (isFractionRuleSet) {
      return findFractionRuleSetRule(number);
    }
    



    if (number < 0.0D) {
      if (negativeNumberRule != null) {
        return negativeNumberRule;
      }
      number = -number;
    }
    


    if (number != Math.floor(number))
    {

      if ((number < 1.0D) && (fractionRules[1] != null)) {
        return fractionRules[1];
      }
      

      if (fractionRules[0] != null) {
        return fractionRules[0];
      }
    }
    

    if (fractionRules[2] != null) {
      return fractionRules[2];
    }
    


    return findNormalRule(Math.round(number));
  }
  



















  private NFRule findNormalRule(long number)
  {
    if (isFractionRuleSet) {
      return findFractionRuleSetRule(number);
    }
    


    if (number < 0L) {
      if (negativeNumberRule != null) {
        return negativeNumberRule;
      }
      number = -number;
    }
    













    int lo = 0;
    int hi = rules.length;
    if (hi > 0) {
      while (lo < hi) {
        int mid = lo + hi >>> 1;
        if (rules[mid].getBaseValue() == number) {
          return rules[mid];
        }
        if (rules[mid].getBaseValue() > number) {
          hi = mid;
        }
        else {
          lo = mid + 1;
        }
      }
      if (hi == 0) {
        throw new IllegalStateException("The rule set " + name + " cannot format the value " + number);
      }
      NFRule result = rules[(hi - 1)];
      





      if (result.shouldRollBack(number)) {
        if (hi == 1) {
          throw new IllegalStateException("The rule set " + name + " cannot roll back from the rule '" + result + "'");
        }
        
        result = rules[(hi - 2)];
      }
      return result;
    }
    
    return fractionRules[2];
  }
  























  private NFRule findFractionRuleSetRule(double number)
  {
    long leastCommonMultiple = rules[0].getBaseValue();
    for (int i = 1; i < rules.length; i++) {
      leastCommonMultiple = lcm(leastCommonMultiple, rules[i].getBaseValue());
    }
    long numerator = Math.round(number * leastCommonMultiple);
    


    long difference = Long.MAX_VALUE;
    int winner = 0;
    for (int i = 0; i < rules.length; i++)
    {





      long tempDifference = numerator * rules[i].getBaseValue() % leastCommonMultiple;
      



      if (leastCommonMultiple - tempDifference < tempDifference) {
        tempDifference = leastCommonMultiple - tempDifference;
      }
      




      if (tempDifference < difference) {
        difference = tempDifference;
        winner = i;
        if (difference == 0L) {
          break;
        }
      }
    }
    






    if ((winner + 1 < rules.length) && (rules[(winner + 1)].getBaseValue() == rules[winner].getBaseValue()))
    {
      if ((Math.round(number * rules[winner].getBaseValue()) < 1L) || (Math.round(number * rules[winner].getBaseValue()) >= 2L))
      {
        winner++;
      }
    }
    

    return rules[winner];
  }
  




  private static long lcm(long x, long y)
  {
    long x1 = x;
    long y1 = y;
    
    int p2 = 0;
    while (((x1 & 1L) == 0L) && ((y1 & 1L) == 0L)) {
      p2++;
      x1 >>= 1;
      y1 >>= 1;
    }
    long t;
    long t;
    if ((x1 & 1L) == 1L) {
      t = -y1;
    } else {
      t = x1;
    }
    
    while (t != 0L) {
      while ((t & 1L) == 0L) {
        t >>= 1;
      }
      if (t > 0L) {
        x1 = t;
      } else {
        y1 = -t;
      }
      t = x1 - y1;
    }
    long gcd = x1 << p2;
    

    return x / gcd * y;
  }
  




























  public Number parse(String text, ParsePosition parsePosition, double upperBound)
  {
    ParsePosition highWaterMark = new ParsePosition(0);
    Number result = Long.valueOf(0L);
    Number tempResult = null;
    

    if (text.length() == 0) {
      return result;
    }
    

    if (negativeNumberRule != null) {
      tempResult = negativeNumberRule.doParse(text, parsePosition, false, upperBound);
      if (parsePosition.getIndex() > highWaterMark.getIndex()) {
        result = tempResult;
        highWaterMark.setIndex(parsePosition.getIndex());
      }
      



      parsePosition.setIndex(0);
    }
    

    for (int i = 0; i < 3; i++) {
      if (fractionRules[i] != null) {
        tempResult = fractionRules[i].doParse(text, parsePosition, false, upperBound);
        if (parsePosition.getIndex() > highWaterMark.getIndex()) {
          result = tempResult;
          highWaterMark.setIndex(parsePosition.getIndex());
        }
        



        parsePosition.setIndex(0);
      }
    }
    









    for (int i = rules.length - 1; (i >= 0) && (highWaterMark.getIndex() < text.length()); i--) {
      if ((isFractionRuleSet) || (rules[i].getBaseValue() < upperBound))
      {


        tempResult = rules[i].doParse(text, parsePosition, isFractionRuleSet, upperBound);
        if (parsePosition.getIndex() > highWaterMark.getIndex()) {
          result = tempResult;
          highWaterMark.setIndex(parsePosition.getIndex());
        }
        



        parsePosition.setIndex(0);
      }
    }
    


    parsePosition.setIndex(highWaterMark.getIndex());
    




    return result;
  }
}
