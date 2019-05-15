package com.ibm.icu.text;

abstract interface RBNFPostProcessor
{
  public abstract void init(RuleBasedNumberFormat paramRuleBasedNumberFormat, String paramString);
  
  public abstract void process(StringBuffer paramStringBuffer, NFRuleSet paramNFRuleSet);
}
