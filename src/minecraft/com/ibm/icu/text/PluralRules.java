package com.ibm.icu.text;

import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.PluralRulesLoader;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.Output;
import com.ibm.icu.util.ULocale;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
































































































































public class PluralRules
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final RuleList rules;
  private final Set<String> keywords;
  private int repeatLimit;
  private transient int hashCode;
  private transient Map<String, List<Double>> _keySamplesMap;
  private transient Map<String, Boolean> _keyLimitedMap;
  public static final String KEYWORD_ZERO = "zero";
  public static final String KEYWORD_ONE = "one";
  public static final String KEYWORD_TWO = "two";
  public static final String KEYWORD_FEW = "few";
  public static final String KEYWORD_MANY = "many";
  public static final String KEYWORD_OTHER = "other";
  public static final double NO_UNIQUE_VALUE = -0.00123456777D;
  
  public static enum PluralType
  {
    CARDINAL, 
    




    ORDINAL;
    

    private PluralType() {}
  }
  
  private static final Constraint NO_CONSTRAINT = new Constraint() {
    private static final long serialVersionUID = 9163464945387899416L;
    
    public boolean isFulfilled(double n) {
      return true;
    }
    
    public boolean isLimited() {
      return false;
    }
    
    public String toString() {
      return "n is any";
    }
    
    public int updateRepeatLimit(int limit) {
      return limit;
    }
  };
  



  private static final Rule DEFAULT_RULE = new Rule() {
    private static final long serialVersionUID = -5677499073940822149L;
    
    public String getKeyword() {
      return "other";
    }
    
    public boolean appliesTo(double n) {
      return true;
    }
    
    public boolean isLimited() {
      return false;
    }
    
    public String toString() {
      return "(other)";
    }
    
    public int updateRepeatLimit(int limit) {
      return limit;
    }
  };
  





  public static final PluralRules DEFAULT = new PluralRules(new RuleChain(DEFAULT_RULE));
  








  public static PluralRules parseDescription(String description)
    throws ParseException
  {
    description = description.trim();
    if (description.length() == 0) {
      return DEFAULT;
    }
    
    return new PluralRules(parseRuleChain(description));
  }
  





  public static PluralRules createRules(String description)
  {
    try
    {
      return parseDescription(description);
    } catch (ParseException e) {}
    return null;
  }
  








  private static abstract interface Constraint
    extends Serializable
  {
    public abstract boolean isFulfilled(double paramDouble);
    








    public abstract boolean isLimited();
    








    public abstract int updateRepeatLimit(int paramInt);
  }
  








  private static abstract interface Rule
    extends Serializable
  {
    public abstract String getKeyword();
    








    public abstract boolean appliesTo(double paramDouble);
    








    public abstract boolean isLimited();
    








    public abstract int updateRepeatLimit(int paramInt);
  }
  








  private static Constraint parseConstraint(String description)
    throws ParseException
  {
    description = description.trim().toLowerCase(Locale.ENGLISH);
    
    Constraint result = null;
    String[] or_together = Utility.splitString(description, "or");
    for (int i = 0; i < or_together.length; i++) {
      Constraint andConstraint = null;
      String[] and_together = Utility.splitString(or_together[i], "and");
      for (int j = 0; j < and_together.length; j++) {
        Constraint newConstraint = NO_CONSTRAINT;
        
        String condition = and_together[j].trim();
        String[] tokens = Utility.splitWhitespace(condition);
        
        int mod = 0;
        boolean inRange = true;
        boolean integersOnly = true;
        long lowBound = Long.MAX_VALUE;
        long highBound = Long.MIN_VALUE;
        long[] vals = null;
        
        boolean isRange = false;
        
        int x = 0;
        String t = tokens[(x++)];
        if (!"n".equals(t)) {
          throw unexpected(t, condition);
        }
        if (x < tokens.length) {
          t = tokens[(x++)];
          if ("mod".equals(t)) {
            mod = Integer.parseInt(tokens[(x++)]);
            t = nextToken(tokens, x++, condition);
          }
          if ("is".equals(t)) {
            t = nextToken(tokens, x++, condition);
            if ("not".equals(t)) {
              inRange = false;
              t = nextToken(tokens, x++, condition);
            }
          } else {
            isRange = true;
            if ("not".equals(t)) {
              inRange = false;
              t = nextToken(tokens, x++, condition);
            }
            if ("in".equals(t)) {
              t = nextToken(tokens, x++, condition);
            } else if ("within".equals(t)) {
              integersOnly = false;
              t = nextToken(tokens, x++, condition);
            } else {
              throw unexpected(t, condition);
            }
          }
          
          if (isRange) {
            String[] range_list = Utility.splitString(t, ",");
            vals = new long[range_list.length * 2];
            int k1 = 0; for (int k2 = 0; k1 < range_list.length; k2 += 2) {
              String range = range_list[k1];
              String[] pair = Utility.splitString(range, "..");
              
              if (pair.length == 2) {
                long low = Long.parseLong(pair[0]);
                long high = Long.parseLong(pair[1]);
                if (low > high)
                  throw unexpected(range, condition);
              } else { long low;
                if (pair.length == 1) { long high;
                  low = high = Long.parseLong(pair[0]);
                } else {
                  throw unexpected(range, condition); } }
              long high;
              long low; vals[k2] = low;
              vals[(k2 + 1)] = high;
              lowBound = Math.min(lowBound, low);
              highBound = Math.max(highBound, high);k1++;
            }
            


            if (vals.length == 2) {
              vals = null;
            }
          } else {
            lowBound = highBound = Long.parseLong(t);
          }
          
          if (x != tokens.length) {
            throw unexpected(tokens[x], condition);
          }
          
          newConstraint = new RangeConstraint(mod, inRange, integersOnly, lowBound, highBound, vals);
        }
        

        if (andConstraint == null) {
          andConstraint = newConstraint;
        } else {
          andConstraint = new AndConstraint(andConstraint, newConstraint);
        }
      }
      

      if (result == null) {
        result = andConstraint;
      } else {
        result = new OrConstraint(result, andConstraint);
      }
    }
    
    return result;
  }
  
  private static ParseException unexpected(String token, String context)
  {
    return new ParseException("unexpected token '" + token + "' in '" + context + "'", -1);
  }
  



  private static String nextToken(String[] tokens, int x, String context)
    throws ParseException
  {
    if (x < tokens.length) {
      return tokens[x];
    }
    throw new ParseException("missing token at end of '" + context + "'", -1);
  }
  



  private static Rule parseRule(String description)
    throws ParseException
  {
    int x = description.indexOf(':');
    if (x == -1) {
      throw new ParseException("missing ':' in rule description '" + description + "'", 0);
    }
    

    String keyword = description.substring(0, x).trim();
    if (!isValidKeyword(keyword)) {
      throw new ParseException("keyword '" + keyword + " is not valid", 0);
    }
    

    description = description.substring(x + 1).trim();
    if (description.length() == 0) {
      throw new ParseException("missing constraint in '" + description + "'", x + 1);
    }
    
    Constraint constraint = parseConstraint(description);
    Rule rule = new ConstrainedRule(keyword, constraint);
    return rule;
  }
  





  private static RuleChain parseRuleChain(String description)
    throws ParseException
  {
    RuleChain rc = null;
    String[] rules = Utility.split(description, ';');
    for (int i = 0; i < rules.length; i++) {
      Rule r = parseRule(rules[i].trim());
      if (rc == null) {
        rc = new RuleChain(r);
      } else {
        rc = rc.addRule(r);
      }
    }
    return rc;
  }
  
  private static abstract interface RuleList
    extends Serializable
  {
    public abstract String select(double paramDouble);
    
    public abstract Set<String> getKeywords();
    
    public abstract int getRepeatLimit();
    
    public abstract boolean isLimited(String paramString);
  }
  
  private static class RangeConstraint
    implements PluralRules.Constraint, Serializable
  {
    RangeConstraint(int mod, boolean inRange, boolean integersOnly, long lowerBound, long upperBound, long[] range_list)
    {
      this.mod = mod;
      this.inRange = inRange;
      this.integersOnly = integersOnly;
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
      this.range_list = range_list;
    }
    
    public boolean isFulfilled(double n) {
      if ((integersOnly) && (n - n != 0.0D)) {
        return !inRange;
      }
      if (mod != 0) {
        n %= mod;
      }
      boolean test = (n >= lowerBound) && (n <= upperBound);
      if ((test) && (range_list != null)) {
        test = false;
        for (int i = 0; (!test) && (i < range_list.length); i += 2) {
          test = (n >= range_list[i]) && (n <= range_list[(i + 1)]);
        }
      }
      return inRange == test;
    }
    
    public boolean isLimited() {
      return (integersOnly) && (inRange) && (mod == 0);
    }
    
    public int updateRepeatLimit(int limit) {
      int mylimit = mod == 0 ? (int)upperBound : mod;
      return Math.max(mylimit, limit);
    }
    


    private static final long serialVersionUID = 1L;
    

    private int mod;
    

    private boolean inRange;
    

    private boolean integersOnly;
    

    private long lowerBound;
    
    private long upperBound;
    
    private long[] range_list;
    
    public String toString()
    {
      Object lb = new Object()
      {
        StringBuilder sb = new StringBuilder("[");
        
        1ListBuilder add(String s) { return add(s, null); }
        
        1ListBuilder add(String s, Object o) {
          if (sb.length() > 1) {
            sb.append(", ");
          }
          sb.append(s);
          if (o != null) {
            sb.append(": ").append(o.toString());
          }
          return this;
        }
        
        public String toString() { String s = ']';
          sb = null;
          return s;
        }
      };
      
      if (mod > 1) {
        lb.add("mod", Integer.valueOf(mod));
      }
      if (inRange) {
        lb.add("in");
      } else {
        lb.add("except");
      }
      if (integersOnly) {
        lb.add("ints");
      }
      if (lowerBound == upperBound) {
        lb.add(String.valueOf(lowerBound));
      } else {
        lb.add(String.valueOf(lowerBound) + "-" + String.valueOf(upperBound));
      }
      if (range_list != null) {
        lb.add(Arrays.toString(range_list));
      }
      return lb.toString();
    }
  }
  
  private static abstract class BinaryConstraint implements PluralRules.Constraint, Serializable
  {
    private static final long serialVersionUID = 1L;
    protected final PluralRules.Constraint a;
    protected final PluralRules.Constraint b;
    private final String conjunction;
    
    protected BinaryConstraint(PluralRules.Constraint a, PluralRules.Constraint b, String c)
    {
      this.a = a;
      this.b = b;
      conjunction = c;
    }
    
    public int updateRepeatLimit(int limit) {
      return a.updateRepeatLimit(b.updateRepeatLimit(limit));
    }
    
    public String toString() {
      return a.toString() + conjunction + b.toString();
    }
  }
  
  private static class AndConstraint extends PluralRules.BinaryConstraint
  {
    private static final long serialVersionUID = 7766999779862263523L;
    
    AndConstraint(PluralRules.Constraint a, PluralRules.Constraint b) {
      super(b, " && ");
    }
    
    public boolean isFulfilled(double n) {
      return (a.isFulfilled(n)) && (b.isFulfilled(n));
    }
    

    public boolean isLimited()
    {
      return (a.isLimited()) || (b.isLimited());
    }
  }
  
  private static class OrConstraint extends PluralRules.BinaryConstraint
  {
    private static final long serialVersionUID = 1405488568664762222L;
    
    OrConstraint(PluralRules.Constraint a, PluralRules.Constraint b) {
      super(b, " || ");
    }
    
    public boolean isFulfilled(double n) {
      return (a.isFulfilled(n)) || (b.isFulfilled(n));
    }
    
    public boolean isLimited() {
      return (a.isLimited()) && (b.isLimited());
    }
  }
  

  private static class ConstrainedRule
    implements PluralRules.Rule, Serializable
  {
    private static final long serialVersionUID = 1L;
    private final String keyword;
    private final PluralRules.Constraint constraint;
    
    public ConstrainedRule(String keyword, PluralRules.Constraint constraint)
    {
      this.keyword = keyword;
      this.constraint = constraint;
    }
    
    public PluralRules.Rule and(PluralRules.Constraint c)
    {
      return new ConstrainedRule(keyword, new PluralRules.AndConstraint(constraint, c));
    }
    
    public PluralRules.Rule or(PluralRules.Constraint c)
    {
      return new ConstrainedRule(keyword, new PluralRules.OrConstraint(constraint, c));
    }
    
    public String getKeyword() {
      return keyword;
    }
    
    public boolean appliesTo(double n) {
      return constraint.isFulfilled(n);
    }
    
    public int updateRepeatLimit(int limit) {
      return constraint.updateRepeatLimit(limit);
    }
    
    public boolean isLimited() {
      return constraint.isLimited();
    }
    
    public String toString() {
      return keyword + ": " + constraint;
    }
  }
  

  private static class RuleChain
    implements PluralRules.RuleList, Serializable
  {
    private static final long serialVersionUID = 1L;
    
    private final PluralRules.Rule rule;
    private final RuleChain next;
    
    public RuleChain(PluralRules.Rule rule)
    {
      this(rule, null);
    }
    
    private RuleChain(PluralRules.Rule rule, RuleChain next) {
      this.rule = rule;
      this.next = next;
    }
    
    public RuleChain addRule(PluralRules.Rule nextRule) {
      return new RuleChain(nextRule, this);
    }
    
    private PluralRules.Rule selectRule(double n) {
      PluralRules.Rule r = null;
      if (next != null) {
        r = next.selectRule(n);
      }
      if ((r == null) && (rule.appliesTo(n))) {
        r = rule;
      }
      return r;
    }
    
    public String select(double n) {
      PluralRules.Rule r = selectRule(n);
      if (r == null) {
        return "other";
      }
      return r.getKeyword();
    }
    
    public Set<String> getKeywords() {
      Set<String> result = new HashSet();
      result.add("other");
      RuleChain rc = this;
      while (rc != null) {
        result.add(rule.getKeyword());
        rc = next;
      }
      return result;
    }
    

    public boolean isLimited(String keyword)
    {
      RuleChain rc = this;
      boolean result = false;
      while (rc != null) {
        if (keyword.equals(rule.getKeyword())) {
          if (!rule.isLimited()) {
            return false;
          }
          result = true;
        }
        rc = next;
      }
      return result;
    }
    
    public int getRepeatLimit() {
      int result = 0;
      RuleChain rc = this;
      while (rc != null) {
        result = rule.updateRepeatLimit(result);
        rc = next;
      }
      return result;
    }
    
    public String toString() {
      String s = rule.toString();
      if (next != null) {
        s = next.toString() + "; " + s;
      }
      return s;
    }
  }
  





















  public static PluralRules forLocale(ULocale locale)
  {
    return PluralRulesLoader.loader.forLocale(locale, PluralType.CARDINAL);
  }
  


















  public static PluralRules forLocale(ULocale locale, PluralType type)
  {
    return PluralRulesLoader.loader.forLocale(locale, type);
  }
  





  private static boolean isValidKeyword(String token)
  {
    return PatternProps.isIdentifier(token);
  }
  


  private PluralRules(RuleList rules)
  {
    this.rules = rules;
    keywords = Collections.unmodifiableSet(rules.getKeywords());
  }
  







  public String select(double number)
  {
    return rules.select(number);
  }
  






  public Set<String> getKeywords()
  {
    return keywords;
  }
  







  public double getUniqueKeywordValue(String keyword)
  {
    Collection<Double> values = getAllKeywordValues(keyword);
    if ((values != null) && (values.size() == 1)) {
      return ((Double)values.iterator().next()).doubleValue();
    }
    return -0.00123456777D;
  }
  









  public Collection<Double> getAllKeywordValues(String keyword)
  {
    if (!keywords.contains(keyword)) {
      return Collections.emptyList();
    }
    Collection<Double> result = (Collection)getKeySamplesMap().get(keyword);
    






    if ((result.size() > 2) && (!((Boolean)getKeyLimitedMap().get(keyword)).booleanValue())) {
      return null;
    }
    return result;
  }
  










  public Collection<Double> getSamples(String keyword)
  {
    if (!keywords.contains(keyword)) {
      return null;
    }
    return (Collection)getKeySamplesMap().get(keyword);
  }
  
  private Map<String, Boolean> getKeyLimitedMap() {
    initKeyMaps();
    return _keyLimitedMap;
  }
  
  private Map<String, List<Double>> getKeySamplesMap() {
    initKeyMaps();
    return _keySamplesMap;
  }
  
  private synchronized void initKeyMaps()
  {
    if (_keySamplesMap == null)
    {



      int MAX_SAMPLES = 3;
      
      Map<String, Boolean> temp = new HashMap();
      for (String k : keywords) {
        temp.put(k, Boolean.valueOf(rules.isLimited(k)));
      }
      _keyLimitedMap = temp;
      
      Map<String, List<Double>> sampleMap = new HashMap();
      int keywordsRemaining = keywords.size();
      
      int limit = Math.max(5, getRepeatLimit() * 3) * 2;
      for (int i = 0; (keywordsRemaining > 0) && (i < limit); i++) {
        double val = i / 2.0D;
        String keyword = select(val);
        boolean keyIsLimited = ((Boolean)_keyLimitedMap.get(keyword)).booleanValue();
        
        List<Double> list = (List)sampleMap.get(keyword);
        if (list == null) {
          list = new ArrayList(3);
          sampleMap.put(keyword, list);
        } else { if ((!keyIsLimited) && (list.size() == 3))
            continue;
        }
        list.add(Double.valueOf(val));
        
        if ((!keyIsLimited) && (list.size() == 3)) {
          keywordsRemaining--;
        }
      }
      
      if (keywordsRemaining > 0) {
        for (String k : keywords) {
          if (!sampleMap.containsKey(k)) {
            sampleMap.put(k, Collections.emptyList());
            keywordsRemaining--; if (keywordsRemaining == 0) {
              break;
            }
          }
        }
      }
      

      for (Map.Entry<String, List<Double>> entry : sampleMap.entrySet()) {
        sampleMap.put(entry.getKey(), Collections.unmodifiableList((List)entry.getValue()));
      }
      _keySamplesMap = sampleMap;
    }
  }
  





  public static ULocale[] getAvailableULocales()
  {
    return PluralRulesLoader.loader.getAvailableULocales();
  }
  
















  public static ULocale getFunctionalEquivalent(ULocale locale, boolean[] isAvailable)
  {
    return PluralRulesLoader.loader.getFunctionalEquivalent(locale, isAvailable);
  }
  



  public String toString()
  {
    return "keywords: " + keywords + " limit: " + getRepeatLimit() + " rules: " + rules.toString();
  }
  





  public int hashCode()
  {
    if (hashCode == 0)
    {
      int newHashCode = keywords.hashCode();
      for (int i = 0; i < 12; i++) {
        newHashCode = newHashCode * 31 + select(i).hashCode();
      }
      if (newHashCode == 0) {
        newHashCode = 1;
      }
      hashCode = newHashCode;
    }
    return hashCode;
  }
  



  public boolean equals(Object rhs)
  {
    return ((rhs instanceof PluralRules)) && (equals((PluralRules)rhs));
  }
  





  public boolean equals(PluralRules rhs)
  {
    if (rhs == null) {
      return false;
    }
    if (rhs == this) {
      return true;
    }
    
    if (hashCode() != rhs.hashCode()) {
      return false;
    }
    
    if (!rhs.getKeywords().equals(keywords)) {
      return false;
    }
    
    int limit = Math.max(getRepeatLimit(), rhs.getRepeatLimit());
    for (int i = 0; i < limit * 2; i++) {
      if (!select(i).equals(rhs.select(i))) {
        return false;
      }
    }
    return true;
  }
  
  private int getRepeatLimit() {
    if (repeatLimit == 0) {
      repeatLimit = (rules.getRepeatLimit() + 1);
    }
    return repeatLimit;
  }
  











  public static enum KeywordStatus
  {
    INVALID, 
    





    SUPPRESSED, 
    





    UNIQUE, 
    





    BOUNDED, 
    





    UNBOUNDED;
    








    private KeywordStatus() {}
  }
  







  public KeywordStatus getKeywordStatus(String keyword, int offset, Set<Double> explicits, Output<Double> uniqueValue)
  {
    if (uniqueValue != null) {
      value = null;
    }
    
    if (!rules.getKeywords().contains(keyword)) {
      return KeywordStatus.INVALID;
    }
    Collection<Double> values = getAllKeywordValues(keyword);
    if (values == null) {
      return KeywordStatus.UNBOUNDED;
    }
    int originalSize = values.size();
    
    if (explicits == null) {
      explicits = Collections.emptySet();
    }
    


    if (originalSize > explicits.size()) {
      if (originalSize == 1) {
        if (uniqueValue != null) {
          value = values.iterator().next();
        }
        return KeywordStatus.UNIQUE;
      }
      return KeywordStatus.BOUNDED;
    }
    


    HashSet<Double> subtractedSet = new HashSet(values);
    for (Double explicit : explicits) {
      subtractedSet.remove(Double.valueOf(explicit.doubleValue() - offset));
    }
    if (subtractedSet.size() == 0) {
      return KeywordStatus.SUPPRESSED;
    }
    
    if ((uniqueValue != null) && (subtractedSet.size() == 1)) {
      value = subtractedSet.iterator().next();
    }
    
    return originalSize == 1 ? KeywordStatus.UNIQUE : KeywordStatus.BOUNDED;
  }
}
