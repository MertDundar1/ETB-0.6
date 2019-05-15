package com.ibm.icu.text;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.PatternTokenizer;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.Freezable;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import com.ibm.icu.util.UResourceBundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


























public class DateTimePatternGenerator
  implements Freezable<DateTimePatternGenerator>, Cloneable
{
  private static final boolean DEBUG = false;
  public static final int ERA = 0;
  public static final int YEAR = 1;
  public static final int QUARTER = 2;
  public static final int MONTH = 3;
  public static final int WEEK_OF_YEAR = 4;
  public static final int WEEK_OF_MONTH = 5;
  public static final int WEEKDAY = 6;
  public static final int DAY = 7;
  public static final int DAY_OF_YEAR = 8;
  public static final int DAY_OF_WEEK_IN_MONTH = 9;
  public static final int DAYPERIOD = 10;
  public static final int HOUR = 11;
  public static final int MINUTE = 12;
  public static final int SECOND = 13;
  public static final int FRACTIONAL_SECOND = 14;
  public static final int ZONE = 15;
  public static final int TYPE_LIMIT = 16;
  public static final int MATCH_NO_OPTIONS = 0;
  public static final int MATCH_HOUR_FIELD_LENGTH = 2048;
  /**
   * @deprecated
   */
  public static final int MATCH_MINUTE_FIELD_LENGTH = 4096;
  /**
   * @deprecated
   */
  public static final int MATCH_SECOND_FIELD_LENGTH = 8192;
  public static final int MATCH_ALL_FIELDS_LENGTH = 65535;
  private TreeMap<DateTimeMatcher, PatternWithSkeletonFlag> skeleton2pattern;
  private TreeMap<String, PatternWithSkeletonFlag> basePattern_pattern;
  private String decimal;
  private String dateTimeFormat;
  private String[] appendItemFormats;
  private String[] appendItemNames;
  private char defaultHourFormatChar;
  private boolean frozen;
  private transient DateTimeMatcher current;
  private transient FormatParser fp;
  private transient DistanceInfo _distanceInfo;
  private static final int FRACTIONAL_MASK = 16384;
  private static final int SECOND_AND_FRACTIONAL_MASK = 24576;
  
  public static DateTimePatternGenerator getEmptyInstance()
  {
    return new DateTimePatternGenerator();
  }
  











  public static DateTimePatternGenerator getInstance()
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT));
  }
  




  public static DateTimePatternGenerator getInstance(ULocale uLocale)
  {
    return getFrozenInstance(uLocale).cloneAsThawed();
  }
  






  /**
   * @deprecated
   */
  public static DateTimePatternGenerator getFrozenInstance(ULocale uLocale)
  {
    String localeKey = uLocale.toString();
    DateTimePatternGenerator result = (DateTimePatternGenerator)DTPNG_CACHE.get(localeKey);
    if (result != null) {
      return result;
    }
    result = new DateTimePatternGenerator();
    PatternInfo returnInfo = new PatternInfo();
    String shortTimePattern = null;
    
    for (int i = 0; i <= 3; i++) {
      SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance(i, uLocale);
      result.addPattern(df.toPattern(), false, returnInfo);
      df = (SimpleDateFormat)DateFormat.getTimeInstance(i, uLocale);
      result.addPattern(df.toPattern(), false, returnInfo);
      if (i == 3)
      {

        shortTimePattern = df.toPattern();
        


        FormatParser fp = new FormatParser();
        fp.set(shortTimePattern);
        List<Object> items = fp.getItems();
        for (int idx = 0; idx < items.size(); idx++) {
          Object item = items.get(idx);
          if ((item instanceof VariableField)) {
            VariableField fld = (VariableField)item;
            if (fld.getType() == 11) {
              defaultHourFormatChar = fld.toString().charAt(0);
              break;
            }
          }
        }
      }
    }
    
    ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", uLocale);
    
    String calendarTypeToUse = uLocale.getKeywordValue("calendar");
    if (calendarTypeToUse == null) {
      String[] preferredCalendarTypes = Calendar.getKeywordValuesForLocale("calendar", uLocale, true);
      calendarTypeToUse = preferredCalendarTypes[0];
    }
    if (calendarTypeToUse == null) {
      calendarTypeToUse = "gregorian";
    }
    






    try
    {
      ICUResourceBundle itemBundle = rb.getWithFallback("calendar/" + calendarTypeToUse + "/appendItems");
      for (int i = 0; i < itemBundle.getSize(); i++) {
        ICUResourceBundle formatBundle = (ICUResourceBundle)itemBundle.get(i);
        String formatName = itemBundle.get(i).getKey();
        String value = formatBundle.getString();
        result.setAppendItemFormat(getAppendFormatNumber(formatName), value);
      }
    }
    catch (MissingResourceException e) {}
    
    try
    {
      ICUResourceBundle itemBundle = rb.getWithFallback("fields");
      
      for (int i = 0; i < 16; i++) {
        if (isCLDRFieldName(i)) {
          ICUResourceBundle fieldBundle = itemBundle.getWithFallback(CLDR_FIELD_NAME[i]);
          ICUResourceBundle dnBundle = fieldBundle.getWithFallback("dn");
          String value = dnBundle.getString();
          
          result.setAppendItemName(i, value);
        }
      }
    }
    catch (MissingResourceException e) {}
    

    ICUResourceBundle availFormatsBundle = null;
    




    try
    {
      availFormatsBundle = rb.getWithFallback("calendar/" + calendarTypeToUse + "/availableFormats");
    }
    catch (MissingResourceException e) {}
    

    boolean override = true;
    while (availFormatsBundle != null) {
      for (int i = 0; i < availFormatsBundle.getSize(); i++) {
        String formatKey = availFormatsBundle.get(i).getKey();
        
        if (!result.isAvailableFormatSet(formatKey)) {
          result.setAvailableFormat(formatKey);
          

          String formatValue = availFormatsBundle.get(i).getString();
          result.addPatternWithSkeleton(formatValue, formatKey, override, returnInfo);
        }
      }
      
      ICUResourceBundle pbundle = (ICUResourceBundle)availFormatsBundle.getParent();
      if (pbundle == null) {
        break;
      }
      try {
        availFormatsBundle = pbundle.getWithFallback("calendar/" + calendarTypeToUse + "/availableFormats");
      } catch (MissingResourceException e) {
        availFormatsBundle = null;
      }
      if ((availFormatsBundle != null) && (pbundle.getULocale().getBaseName().equals("root"))) {
        override = false;
      }
    }
    


    if (shortTimePattern != null) {
      hackTimes(result, returnInfo, shortTimePattern);
    }
    
    result.setDateTimeFormat(Calendar.getDateTimePattern(Calendar.getInstance(uLocale), uLocale, 2));
    

    DecimalFormatSymbols dfs = new DecimalFormatSymbols(uLocale);
    result.setDecimal(String.valueOf(dfs.getDecimalSeparator()));
    

    result.freeze();
    DTPNG_CACHE.put(localeKey, result);
    return result;
  }
  
  /**
   * @deprecated
   */
  public char getDefaultHourFormatChar()
  {
    return defaultHourFormatChar;
  }
  
  /**
   * @deprecated
   */
  public void setDefaultHourFormatChar(char defaultHourFormatChar)
  {
    this.defaultHourFormatChar = defaultHourFormatChar;
  }
  
  private static void hackTimes(DateTimePatternGenerator result, PatternInfo returnInfo, String hackPattern) {
    fp.set(hackPattern);
    StringBuilder mmss = new StringBuilder();
    
    boolean gotMm = false;
    for (int i = 0; i < fp.items.size(); i++) {
      Object item = fp.items.get(i);
      if ((item instanceof String)) {
        if (gotMm) {
          mmss.append(fp.quoteLiteral(item.toString()));
        }
      } else {
        char ch = item.toString().charAt(0);
        if (ch == 'm') {
          gotMm = true;
          mmss.append(item);
        } else if (ch == 's') {
          if (gotMm)
          {

            mmss.append(item);
            result.addPattern(mmss.toString(), false, returnInfo);
          }
        } else { if ((gotMm) || (ch == 'z') || (ch == 'Z') || (ch == 'v') || (ch == 'V')) {
            break;
          }
        }
      }
    }
    
    BitSet variables = new BitSet();
    BitSet nuke = new BitSet();
    for (int i = 0; i < fp.items.size(); i++) {
      Object item = fp.items.get(i);
      if ((item instanceof VariableField)) {
        variables.set(i);
        char ch = item.toString().charAt(0);
        if ((ch == 's') || (ch == 'S')) {
          nuke.set(i);
          for (int j = i - 1; j >= 0; j++) {
            if (variables.get(j)) break;
            nuke.set(i);
          }
        }
      }
    }
    String hhmm = getFilteredPattern(fp, nuke);
    result.addPattern(hhmm, false, returnInfo);
  }
  
  private static String getFilteredPattern(FormatParser fp, BitSet nuke) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < items.size(); i++) {
      if (!nuke.get(i)) {
        Object item = items.get(i);
        if ((item instanceof String)) {
          result.append(fp.quoteLiteral(item.toString()));
        } else
          result.append(item.toString());
      }
    }
    return result.toString();
  }
  







  /**
   * @deprecated
   */
  public static int getAppendFormatNumber(String string)
  {
    for (int i = 0; i < CLDR_FIELD_APPEND.length; i++) {
      if (CLDR_FIELD_APPEND[i].equals(string)) return i;
    }
    return -1;
  }
  
  private static boolean isCLDRFieldName(int index)
  {
    if ((index < 0) && (index >= 16)) {
      return false;
    }
    if (CLDR_FIELD_NAME[index].charAt(0) == '*') {
      return false;
    }
    
    return true;
  }
  









  public String getBestPattern(String skeleton)
  {
    return getBestPattern(skeleton, null, 0);
  }
  











  public String getBestPattern(String skeleton, int options)
  {
    return getBestPattern(skeleton, null, options);
  }
  





  private String getBestPattern(String skeleton, DateTimeMatcher skipMatcher, int options)
  {
    skeleton = skeleton.replaceAll("j", String.valueOf(defaultHourFormatChar));
    String datePattern;
    String timePattern;
    synchronized (this) {
      current.set(skeleton, fp, false);
      PatternWithMatcher bestWithMatcher = getBestRaw(current, -1, _distanceInfo, skipMatcher);
      if ((_distanceInfo.missingFieldMask == 0) && (_distanceInfo.extraFieldMask == 0))
      {
        return adjustFieldTypes(bestWithMatcher, current, false, options);
      }
      int neededFields = current.getFieldMask();
      

      datePattern = getBestAppending(current, neededFields & 0x3FF, _distanceInfo, skipMatcher, options);
      timePattern = getBestAppending(current, neededFields & 0xFC00, _distanceInfo, skipMatcher, options);
    }
    
    if (datePattern == null) return timePattern == null ? "" : timePattern;
    if (timePattern == null) return datePattern;
    return MessageFormat.format(getDateTimeFormat(), new Object[] { timePattern, datePattern });
  }
  























































  public DateTimePatternGenerator addPattern(String pattern, boolean override, PatternInfo returnInfo)
  {
    return addPatternWithSkeleton(pattern, null, override, returnInfo);
  }
  










  /**
   * @deprecated
   */
  public DateTimePatternGenerator addPatternWithSkeleton(String pattern, String skeletonToUse, boolean override, PatternInfo returnInfo)
  {
    checkFrozen();
    DateTimeMatcher matcher;
    DateTimeMatcher matcher; if (skeletonToUse == null) {
      matcher = new DateTimeMatcher(null).set(pattern, fp, false);
    } else {
      matcher = new DateTimeMatcher(null).set(skeletonToUse, fp, false);
    }
    String basePattern = matcher.getBasePattern();
    






    PatternWithSkeletonFlag previousPatternWithSameBase = (PatternWithSkeletonFlag)basePattern_pattern.get(basePattern);
    if ((previousPatternWithSameBase != null) && ((!skeletonWasSpecified) || ((skeletonToUse != null) && (!override)))) {
      status = 1;
      conflictingPattern = pattern;
      if (!override) {
        return this;
      }
    }
    



    PatternWithSkeletonFlag previousValue = (PatternWithSkeletonFlag)skeleton2pattern.get(matcher);
    if (previousValue != null) {
      status = 2;
      conflictingPattern = pattern;
      if ((!override) || ((skeletonToUse != null) && (skeletonWasSpecified))) return this;
    }
    status = 0;
    conflictingPattern = "";
    PatternWithSkeletonFlag patWithSkelFlag = new PatternWithSkeletonFlag(pattern, skeletonToUse != null);
    


    skeleton2pattern.put(matcher, patWithSkelFlag);
    basePattern_pattern.put(basePattern, patWithSkelFlag);
    return this;
  }
  







  public String getSkeleton(String pattern)
  {
    synchronized (this) {
      current.set(pattern, fp, false);
      return current.toString();
    }
  }
  




  /**
   * @deprecated
   */
  public String getSkeletonAllowingDuplicates(String pattern)
  {
    synchronized (this) {
      current.set(pattern, fp, true);
      return current.toString();
    }
  }
  





  /**
   * @deprecated
   */
  public String getCanonicalSkeletonAllowingDuplicates(String pattern)
  {
    synchronized (this) {
      current.set(pattern, fp, true);
      return current.toCanonicalString();
    }
  }
  










  public String getBaseSkeleton(String pattern)
  {
    synchronized (this) {
      current.set(pattern, fp, false);
      return current.getBasePattern();
    }
  }
  














  public Map<String, String> getSkeletons(Map<String, String> result)
  {
    if (result == null) {
      result = new LinkedHashMap();
    }
    for (DateTimeMatcher item : skeleton2pattern.keySet()) {
      PatternWithSkeletonFlag patternWithSkelFlag = (PatternWithSkeletonFlag)skeleton2pattern.get(item);
      String pattern = pattern;
      if (!CANONICAL_SET.contains(pattern))
      {

        result.put(item.toString(), pattern); }
    }
    return result;
  }
  



  public Set<String> getBaseSkeletons(Set<String> result)
  {
    if (result == null) {
      result = new HashSet();
    }
    result.addAll(basePattern_pattern.keySet());
    return result;
  }
  











  public String replaceFieldTypes(String pattern, String skeleton)
  {
    return replaceFieldTypes(pattern, skeleton, 0);
  }
  














  public String replaceFieldTypes(String pattern, String skeleton, int options)
  {
    synchronized (this) {
      PatternWithMatcher patternNoMatcher = new PatternWithMatcher(pattern, null);
      return adjustFieldTypes(patternNoMatcher, current.set(skeleton, fp, false), false, options);
    }
  }
  

















  public void setDateTimeFormat(String dateTimeFormat)
  {
    checkFrozen();
    this.dateTimeFormat = dateTimeFormat;
  }
  





  public String getDateTimeFormat()
  {
    return dateTimeFormat;
  }
  










  public void setDecimal(String decimal)
  {
    checkFrozen();
    this.decimal = decimal;
  }
  




  public String getDecimal()
  {
    return decimal;
  }
  








  /**
   * @deprecated
   */
  public Collection<String> getRedundants(Collection<String> output)
  {
    synchronized (this) {
      if (output == null) {
        output = new LinkedHashSet();
      }
      for (DateTimeMatcher cur : skeleton2pattern.keySet()) {
        PatternWithSkeletonFlag patternWithSkelFlag = (PatternWithSkeletonFlag)skeleton2pattern.get(cur);
        String pattern = pattern;
        if (!CANONICAL_SET.contains(pattern))
        {

          String trial = getBestPattern(cur.toString(), cur, 0);
          if (trial.equals(pattern)) {
            output.add(pattern);
          }
        }
      }
      



















      return output;
    }
  }
  

















































































































































  public void setAppendItemFormat(int field, String value)
  {
    checkFrozen();
    appendItemFormats[field] = value;
  }
  







  public String getAppendItemFormat(int field)
  {
    return appendItemFormats[field];
  }
  










  public void setAppendItemName(int field, String value)
  {
    checkFrozen();
    appendItemNames[field] = value;
  }
  







  public String getAppendItemName(int field)
  {
    return appendItemNames[field];
  }
  




  /**
   * @deprecated
   */
  public static boolean isSingleField(String skeleton)
  {
    char first = skeleton.charAt(0);
    for (int i = 1; i < skeleton.length(); i++) {
      if (skeleton.charAt(i) != first) return false;
    }
    return true;
  }
  





  private void setAvailableFormat(String key)
  {
    checkFrozen();
    cldrAvailableFormatKeys.add(key);
  }
  










  private boolean isAvailableFormatSet(String key)
  {
    return cldrAvailableFormatKeys.contains(key);
  }
  



  public boolean isFrozen()
  {
    return frozen;
  }
  



  public DateTimePatternGenerator freeze()
  {
    frozen = true;
    return this;
  }
  



  public DateTimePatternGenerator cloneAsThawed()
  {
    DateTimePatternGenerator result = (DateTimePatternGenerator)clone();
    frozen = false;
    return result;
  }
  



  public Object clone()
  {
    try
    {
      DateTimePatternGenerator result = (DateTimePatternGenerator)super.clone();
      skeleton2pattern = ((TreeMap)skeleton2pattern.clone());
      basePattern_pattern = ((TreeMap)basePattern_pattern.clone());
      appendItemFormats = ((String[])appendItemFormats.clone());
      appendItemNames = ((String[])appendItemNames.clone());
      current = new DateTimeMatcher(null);
      fp = new FormatParser();
      _distanceInfo = new DistanceInfo(null);
      
      frozen = false;
      return result;
    }
    catch (CloneNotSupportedException e) {
      throw new IllegalArgumentException("Internal Error");
    }
  }
  
  public static final class PatternInfo
  {
    public static final int OK = 0;
    public static final int BASE_CONFLICT = 1;
    public static final int CONFLICT = 2;
    public int status;
    public String conflictingPattern;
    
    public PatternInfo() {}
  }
  
  /**
   * @deprecated
   */
  public static class VariableField {
    private final String string;
    private final int canonicalIndex;
    
    /**
     * @deprecated
     */
    public VariableField(String string) {
      this(string, false);
    }
    



    /**
     * @deprecated
     */
    public VariableField(String string, boolean strict)
    {
      canonicalIndex = DateTimePatternGenerator.getCanonicalIndex(string, strict);
      if (canonicalIndex < 0) {
        throw new IllegalArgumentException("Illegal datetime field:\t" + string);
      }
      
      this.string = string;
    }
    




    /**
     * @deprecated
     */
    public int getType()
    {
      return DateTimePatternGenerator.types[canonicalIndex][1];
    }
    
    /**
     * @deprecated
     */
    public static String getCanonicalCode(int type)
    {
      try {
        return DateTimePatternGenerator.CANONICAL_ITEMS[type];
      } catch (Exception e) {}
      return String.valueOf(type);
    }
    


    /**
     * @deprecated
     */
    public boolean isNumeric()
    {
      return DateTimePatternGenerator.types[canonicalIndex][2] > 0;
    }
    


    private int getCanonicalIndex()
    {
      return canonicalIndex;
    }
    

    /**
     * @deprecated
     */
    public String toString()
    {
      return string;
    }
  }
  






















  /**
   * @deprecated
   */
  public static class FormatParser
  {
    private transient PatternTokenizer tokenizer = new PatternTokenizer().setSyntaxCharacters(new UnicodeSet("[a-zA-Z]")).setExtraQuotingCharacters(new UnicodeSet("[[[:script=Latn:][:script=Cyrl:]]&[[:L:][:M:]]]")).setUsingQuote(true);
    



    private List<Object> items = new ArrayList();
    



    /**
     * @deprecated
     */
    public FormatParser() {}
    



    /**
     * @deprecated
     */
    public final FormatParser set(String string)
    {
      return set(string, false);
    }
    




    /**
     * @deprecated
     */
    public FormatParser set(String string, boolean strict)
    {
      items.clear();
      if (string.length() == 0) return this;
      tokenizer.setPattern(string);
      StringBuffer buffer = new StringBuffer();
      StringBuffer variable = new StringBuffer();
      for (;;) {
        buffer.setLength(0);
        int status = tokenizer.next(buffer);
        if (status == 0) break;
        if (status == 1) {
          if ((variable.length() != 0) && (buffer.charAt(0) != variable.charAt(0))) {
            addVariable(variable, false);
          }
          variable.append(buffer);
        } else {
          addVariable(variable, false);
          items.add(buffer.toString());
        }
      }
      addVariable(variable, false);
      return this;
    }
    
    private void addVariable(StringBuffer variable, boolean strict) {
      if (variable.length() != 0) {
        items.add(new DateTimePatternGenerator.VariableField(variable.toString(), strict));
        variable.setLength(0);
      }
    }
    














































    /**
     * @deprecated
     */
    public List<Object> getItems()
    {
      return items;
    }
    

    /**
     * @deprecated
     */
    public String toString()
    {
      return toString(0, items.size());
    }
    




    /**
     * @deprecated
     */
    public String toString(int start, int limit)
    {
      StringBuilder result = new StringBuilder();
      for (int i = start; i < limit; i++) {
        Object item = items.get(i);
        if ((item instanceof String)) {
          String itemString = (String)item;
          result.append(tokenizer.quoteLiteral(itemString));
        } else {
          result.append(items.get(i).toString());
        }
      }
      return result.toString();
    }
    


    /**
     * @deprecated
     */
    public boolean hasDateAndTimeFields()
    {
      int foundMask = 0;
      for (Object item : items) {
        if ((item instanceof DateTimePatternGenerator.VariableField)) {
          int type = ((DateTimePatternGenerator.VariableField)item).getType();
          foundMask |= 1 << type;
        }
      }
      boolean isDate = (foundMask & 0x3FF) != 0;
      boolean isTime = (foundMask & 0xFC00) != 0;
      return (isDate) && (isTime);
    }
    
































































































    /**
     * @deprecated
     */
    public Object quoteLiteral(String string)
    {
      return tokenizer.quoteLiteral(string);
    }
  }
  













  /**
   * @deprecated
   */
  public boolean skeletonsAreSimilar(String id, String skeleton)
  {
    if (id.equals(skeleton)) {
      return true;
    }
    
    TreeSet<String> parser1 = getSet(id);
    TreeSet<String> parser2 = getSet(skeleton);
    if (parser1.size() != parser2.size()) {
      return false;
    }
    Iterator<String> it2 = parser2.iterator();
    for (String item : parser1) {
      int index1 = getCanonicalIndex(item, false);
      String item2 = (String)it2.next();
      int index2 = getCanonicalIndex(item2, false);
      if (types[index1][1] != types[index2][1]) {
        return false;
      }
    }
    return true;
  }
  
  private TreeSet<String> getSet(String id) {
    List<Object> items = fp.set(id).getItems();
    TreeSet<String> result = new TreeSet();
    for (Object obj : items) {
      String item = obj.toString();
      if ((!item.startsWith("G")) && (!item.startsWith("a")))
      {

        result.add(item); }
    }
    return result;
  }
  
  private static class PatternWithMatcher
  {
    public String pattern;
    public DateTimePatternGenerator.DateTimeMatcher matcherWithSkeleton;
    
    public PatternWithMatcher(String pat, DateTimePatternGenerator.DateTimeMatcher matcher)
    {
      pattern = pat;
      matcherWithSkeleton = matcher;
    }
  }
  
  private static class PatternWithSkeletonFlag {
    public String pattern;
    public boolean skeletonWasSpecified;
    
    public PatternWithSkeletonFlag(String pat, boolean skelSpecified) { pattern = pat;
      skeletonWasSpecified = skelSpecified;
    }
    
    public String toString() { return pattern + "," + skeletonWasSpecified; }
  }
  

























  private static ICUCache<String, DateTimePatternGenerator> DTPNG_CACHE = new SimpleCache();
  
  private void checkFrozen() {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify frozen object");
    }
  }
  



  private String getBestAppending(DateTimeMatcher source, int missingFields, DistanceInfo distInfo, DateTimeMatcher skipMatcher, int options)
  {
    String resultPattern = null;
    if (missingFields != 0) {
      PatternWithMatcher resultPatternWithMatcher = getBestRaw(source, missingFields, distInfo, skipMatcher);
      resultPattern = adjustFieldTypes(resultPatternWithMatcher, source, false, options);
      
      while (missingFieldMask != 0)
      {


        if (((missingFieldMask & 0x6000) == 16384) && ((missingFields & 0x6000) == 24576))
        {
          pattern = resultPattern;
          resultPattern = adjustFieldTypes(resultPatternWithMatcher, source, true, options);
          missingFieldMask &= 0xBFFF;
        }
        else
        {
          int startingMask = missingFieldMask;
          PatternWithMatcher tempWithMatcher = getBestRaw(source, missingFieldMask, distInfo, skipMatcher);
          String temp = adjustFieldTypes(tempWithMatcher, source, false, options);
          int foundMask = startingMask & (missingFieldMask ^ 0xFFFFFFFF);
          int topField = getTopBitNumber(foundMask);
          resultPattern = MessageFormat.format(getAppendFormat(topField), new Object[] { resultPattern, temp, getAppendName(topField) });
        } }
    }
    return resultPattern;
  }
  
  private String getAppendName(int foundMask) {
    return "'" + appendItemNames[foundMask] + "'";
  }
  
  private String getAppendFormat(int foundMask) { return appendItemFormats[foundMask]; }
  












  private int getTopBitNumber(int foundMask)
  {
    int i = 0;
    while (foundMask != 0) {
      foundMask >>>= 1;
      i++;
    }
    return i - 1;
  }
  


  private void complete()
  {
    PatternInfo patternInfo = new PatternInfo();
    
    for (int i = 0; i < CANONICAL_ITEMS.length; i++)
    {
      addPattern(String.valueOf(CANONICAL_ITEMS[i]), false, patternInfo);
    }
  }
  








  private PatternWithMatcher getBestRaw(DateTimeMatcher source, int includeMask, DistanceInfo missingFields, DateTimeMatcher skipMatcher)
  {
    int bestDistance = Integer.MAX_VALUE;
    PatternWithMatcher bestPatternWithMatcher = new PatternWithMatcher("", null);
    DistanceInfo tempInfo = new DistanceInfo(null);
    for (DateTimeMatcher trial : skeleton2pattern.keySet()) {
      if (!trial.equals(skipMatcher))
      {

        int distance = source.getDistance(trial, includeMask, tempInfo);
        

        if (distance < bestDistance) {
          bestDistance = distance;
          PatternWithSkeletonFlag patternWithSkelFlag = (PatternWithSkeletonFlag)skeleton2pattern.get(trial);
          pattern = pattern;
          

          if (skeletonWasSpecified) {
            matcherWithSkeleton = trial;
          } else {
            matcherWithSkeleton = null;
          }
          missingFields.setTo(tempInfo);
          if (distance == 0)
            break;
        }
      }
    }
    return bestPatternWithMatcher;
  }
  



  private String adjustFieldTypes(PatternWithMatcher patternWithMatcher, DateTimeMatcher inputRequest, boolean fixFractionalSeconds, int options)
  {
    fp.set(pattern);
    StringBuilder newPattern = new StringBuilder();
    for (Object item : fp.getItems()) {
      if ((item instanceof String)) {
        newPattern.append(fp.quoteLiteral((String)item));
      } else {
        VariableField variableField = (VariableField)item;
        StringBuilder fieldBuilder = new StringBuilder(variableField.toString());
        




        int type = variableField.getType();
        
        if ((fixFractionalSeconds) && (type == 13)) {
          String newField = original[14];
          fieldBuilder.append(decimal);
          fieldBuilder.append(newField);
        } else if (type[type] != 0)
        {























          String reqField = original[type];
          int reqFieldLen = reqField.length();
          if ((reqField.charAt(0) == 'E') && (reqFieldLen < 3)) {
            reqFieldLen = 3;
          }
          int adjFieldLen = reqFieldLen;
          DateTimeMatcher matcherWithSkeleton = matcherWithSkeleton;
          if (((type == 11) && ((options & 0x800) == 0)) || ((type == 12) && ((options & 0x1000) == 0)) || ((type == 13) && ((options & 0x2000) == 0)))
          {

            adjFieldLen = fieldBuilder.length();
          } else if (matcherWithSkeleton != null) {
            String skelField = matcherWithSkeleton.origStringForField(type);
            int skelFieldLen = skelField.length();
            boolean patFieldIsNumeric = variableField.isNumeric();
            boolean skelFieldIsNumeric = matcherWithSkeleton.fieldIsNumeric(type);
            if ((skelFieldLen == reqFieldLen) || ((patFieldIsNumeric) && (!skelFieldIsNumeric)) || ((skelFieldIsNumeric) && (!patFieldIsNumeric)))
            {
              adjFieldLen = fieldBuilder.length();
            }
          }
          char c = (type != 11) && (type != 3) && (type != 6) && (type != 1) ? reqField.charAt(0) : fieldBuilder.charAt(0);
          fieldBuilder = new StringBuilder();
          for (int i = adjFieldLen; i > 0; i--) fieldBuilder.append(c);
        }
        newPattern.append(fieldBuilder);
      }
    }
    
    return newPattern.toString();
  }
  











  /**
   * @deprecated
   */
  public String getFields(String pattern)
  {
    fp.set(pattern);
    StringBuilder newPattern = new StringBuilder();
    for (Object item : fp.getItems()) {
      if ((item instanceof String)) {
        newPattern.append(fp.quoteLiteral((String)item));
      } else {
        newPattern.append("{" + getName(item.toString()) + "}");
      }
    }
    return newPattern.toString();
  }
  
  private static String showMask(int mask) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < 16; i++)
      if ((mask & 1 << i) != 0)
      {
        if (result.length() != 0)
          result.append(" | ");
        result.append(FIELD_NAME[i]);
        result.append(" ");
      }
    return result.toString();
  }
  
  private static final String[] CLDR_FIELD_APPEND = { "Era", "Year", "Quarter", "Month", "Week", "*", "Day-Of-Week", "Day", "*", "*", "*", "Hour", "Minute", "Second", "*", "Timezone" };
  




  private static final String[] CLDR_FIELD_NAME = { "era", "year", "*", "month", "week", "*", "weekday", "day", "*", "*", "dayperiod", "hour", "minute", "second", "*", "zone" };
  




  private static final String[] FIELD_NAME = { "Era", "Year", "Quarter", "Month", "Week_in_Year", "Week_in_Month", "Weekday", "Day", "Day_Of_Year", "Day_of_Week_in_Month", "Dayperiod", "Hour", "Minute", "Second", "Fractional_Second", "Zone" };
  





  private static final String[] CANONICAL_ITEMS = { "G", "y", "Q", "M", "w", "W", "E", "d", "D", "F", "H", "m", "s", "S", "v" };
  




  private static final Set<String> CANONICAL_SET = new HashSet(Arrays.asList(CANONICAL_ITEMS));
  private Set<String> cldrAvailableFormatKeys;
  private static final int DATE_MASK = 1023;
  private static final int TIME_MASK = 64512;
  private static final int DELTA = 16;
  private static final int NUMERIC = 256;
  private static final int NONE = 0;
  private static final int NARROW = -257;
  private static final int SHORT = -258;
  private static final int LONG = -259;
  private static final int EXTRA_FIELD = 65536;
  private static final int MISSING_FIELD = 4096;
  
  protected DateTimePatternGenerator()
  {
    skeleton2pattern = new TreeMap();
    basePattern_pattern = new TreeMap();
    decimal = "?";
    dateTimeFormat = "{1} {0}";
    appendItemFormats = new String[16];
    appendItemNames = new String[16];
    
    for (int i = 0; i < 16; i++) {
      appendItemFormats[i] = "{0} ├{2}: {1}┤";
      appendItemNames[i] = ("F" + i);
    }
    
    defaultHourFormatChar = 'H';
    

    frozen = false;
    
    current = new DateTimeMatcher(null);
    fp = new FormatParser();
    _distanceInfo = new DistanceInfo(null);
    






















































































    complete();
    

























































































































































































    cldrAvailableFormatKeys = new HashSet(20);
  }
  













  private static String getName(String s)
  {
    int i = getCanonicalIndex(s, true);
    String name = FIELD_NAME[types[i][1]];
    int subtype = types[i][2];
    boolean string = subtype < 0;
    if (string) subtype = -subtype;
    if (subtype < 0) name = name + ":S"; else
      name = name + ":N";
    return name;
  }
  




  private static int getCanonicalIndex(String s, boolean strict)
  {
    int len = s.length();
    if (len == 0) {
      return -1;
    }
    int ch = s.charAt(0);
    
    for (int i = 1; i < len; i++) {
      if (s.charAt(i) != ch) {
        return -1;
      }
    }
    int bestRow = -1;
    for (int i = 0; i < types.length; i++) {
      int[] row = types[i];
      if (row[0] == ch) {
        bestRow = i;
        if ((row[3] <= len) && 
          (row[(row.length - 1)] >= len))
          return i;
      } }
    return strict ? -1 : bestRow;
  }
  
  private static final int[][] types = { { 71, 0, 65278, 1, 3 }, { 71, 0, 65277, 4 }, { 121, 1, 256, 1, 20 }, { 89, 1, 272, 1, 20 }, { 117, 1, 288, 1, 20 }, { 85, 1, 65278, 1, 3 }, { 85, 1, 65277, 4 }, { 85, 1, 65279, 5 }, { 81, 2, 256, 1, 2 }, { 81, 2, 65278, 3 }, { 81, 2, 65277, 4 }, { 113, 2, 272, 1, 2 }, { 113, 2, 65294, 3 }, { 113, 2, 65293, 4 }, { 77, 3, 256, 1, 2 }, { 77, 3, 65278, 3 }, { 77, 3, 65277, 4 }, { 77, 3, 65279, 5 }, { 76, 3, 272, 1, 2 }, { 76, 3, 65262, 3 }, { 76, 3, 65261, 4 }, { 76, 3, 65263, 5 }, { 108, 3, 272, 1, 1 }, { 119, 4, 256, 1, 2 }, { 87, 5, 272, 1 }, { 69, 6, 65278, 1, 3 }, { 69, 6, 65277, 4 }, { 69, 6, 65279, 5 }, { 99, 6, 288, 1, 2 }, { 99, 6, 65246, 3 }, { 99, 6, 65245, 4 }, { 99, 6, 65247, 5 }, { 101, 6, 272, 1, 2 }, { 101, 6, 65262, 3 }, { 101, 6, 65261, 4 }, { 101, 6, 65263, 5 }, { 100, 7, 256, 1, 2 }, { 68, 8, 272, 1, 3 }, { 70, 9, 288, 1 }, { 103, 7, 304, 1, 20 }, { 97, 10, 65278, 1 }, { 72, 11, 416, 1, 2 }, { 107, 11, 432, 1, 2 }, { 104, 11, 256, 1, 2 }, { 75, 11, 272, 1, 2 }, { 109, 12, 256, 1, 2 }, { 115, 13, 256, 1, 2 }, { 83, 14, 272, 1, 1000 }, { 65, 13, 288, 1, 1000 }, { 118, 15, 65246, 1 }, { 118, 15, 65245, 4 }, { 122, 15, 65278, 1, 3 }, { 122, 15, 65277, 4 }, { 90, 15, 65262, 1, 3 }, { 90, 15, 65261, 4 }, { 86, 15, 65262, 1, 3 }, { 86, 15, 65261, 4 } };
  









































































  private static class DateTimeMatcher
    implements Comparable<DateTimeMatcher>
  {
    private int[] type = new int[16];
    private String[] original = new String[16];
    private String[] baseOriginal = new String[16];
    
    private DateTimeMatcher() {}
    
    public String origStringForField(int field)
    {
      return original[field];
    }
    
    public boolean fieldIsNumeric(int field) {
      return type[field] > 0;
    }
    
    public String toString() {
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < 16; i++) {
        if (original[i].length() != 0) result.append(original[i]);
      }
      return result.toString();
    }
    


    public String toCanonicalString()
    {
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < 16; i++) {
        if (original[i].length() != 0)
        {
          for (int j = 0; j < DateTimePatternGenerator.types.length; j++) {
            int[] row = DateTimePatternGenerator.types[j];
            if (row[1] == i) {
              char originalChar = original[i].charAt(0);
              char repeatChar = (originalChar == 'h') || (originalChar == 'K') ? 'h' : (char)row[0];
              result.append(Utility.repeat(String.valueOf(repeatChar), original[i].length()));
              break;
            }
          }
        }
      }
      return result.toString();
    }
    
    String getBasePattern() {
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < 16; i++) {
        if (baseOriginal[i].length() != 0) result.append(baseOriginal[i]);
      }
      return result.toString();
    }
    
    DateTimeMatcher set(String pattern, DateTimePatternGenerator.FormatParser fp, boolean allowDuplicateFields) {
      for (int i = 0; i < 16; i++) {
        type[i] = 0;
        original[i] = "";
        baseOriginal[i] = "";
      }
      fp.set(pattern);
      for (Object obj : fp.getItems())
        if ((obj instanceof DateTimePatternGenerator.VariableField))
        {

          DateTimePatternGenerator.VariableField item = (DateTimePatternGenerator.VariableField)obj;
          String field = item.toString();
          if (field.charAt(0) != 'a') {
            int canonicalIndex = DateTimePatternGenerator.VariableField.access$800(item);
            



            int[] row = DateTimePatternGenerator.types[canonicalIndex];
            int typeValue = row[1];
            if (original[typeValue].length() != 0) {
              if (!allowDuplicateFields)
              {

                throw new IllegalArgumentException("Conflicting fields:\t" + original[typeValue] + ", " + field + "\t in " + pattern);
              }
            } else {
              original[typeValue] = field;
              char repeatChar = (char)row[0];
              int repeatCount = row[3];
              
              if ("GEzvQ".indexOf(repeatChar) >= 0) repeatCount = 1;
              baseOriginal[typeValue] = Utility.repeat(String.valueOf(repeatChar), repeatCount);
              int subTypeValue = row[2];
              if (subTypeValue > 0) subTypeValue += field.length();
              type[typeValue] = subTypeValue;
            } } }
      return this;
    }
    


    int getFieldMask()
    {
      int result = 0;
      for (int i = 0; i < type.length; i++) {
        if (type[i] != 0) result |= 1 << i;
      }
      return result;
    }
    



    void extractFrom(DateTimeMatcher source, int fieldMask)
    {
      for (int i = 0; i < type.length; i++) {
        if ((fieldMask & 1 << i) != 0) {
          type[i] = type[i];
          original[i] = original[i];
        } else {
          type[i] = 0;
          original[i] = "";
        }
      }
    }
    
    int getDistance(DateTimeMatcher other, int includeMask, DateTimePatternGenerator.DistanceInfo distanceInfo) {
      int result = 0;
      distanceInfo.clear();
      for (int i = 0; i < type.length; i++) {
        int myType = (includeMask & 1 << i) == 0 ? 0 : type[i];
        int otherType = type[i];
        if (myType != otherType)
          if (myType == 0) {
            result += 65536;
            distanceInfo.addExtra(i);
          } else if (otherType == 0) {
            result += 4096;
            distanceInfo.addMissing(i);
          } else {
            result += Math.abs(myType - otherType);
          }
      }
      return result;
    }
    
    public int compareTo(DateTimeMatcher that) {
      for (int i = 0; i < original.length; i++) {
        int comp = original[i].compareTo(original[i]);
        if (comp != 0) return -comp;
      }
      return 0;
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof DateTimeMatcher)) {
        return false;
      }
      DateTimeMatcher that = (DateTimeMatcher)other;
      for (int i = 0; i < original.length; i++) {
        if (!original[i].equals(original[i])) return false;
      }
      return true;
    }
    
    public int hashCode() { int result = 0;
      for (int i = 0; i < original.length; i++) {
        result ^= original[i].hashCode();
      }
      return result;
    } }
  
  private static class DistanceInfo { int missingFieldMask;
    int extraFieldMask;
    
    private DistanceInfo() {}
    
    void clear() { missingFieldMask = (this.extraFieldMask = 0); }
    


    void setTo(DistanceInfo other)
    {
      missingFieldMask = missingFieldMask;
      extraFieldMask = extraFieldMask;
    }
    
    void addMissing(int field) { missingFieldMask |= 1 << field; }
    
    void addExtra(int field) {
      extraFieldMask |= 1 << field;
    }
    
    public String toString() { return "missingFieldMask: " + DateTimePatternGenerator.showMask(missingFieldMask) + ", extraFieldMask: " + DateTimePatternGenerator.showMask(extraFieldMask); }
  }
}
