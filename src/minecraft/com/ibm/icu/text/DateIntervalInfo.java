package com.ibm.icu.text;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.Freezable;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Set;





























































































































































public class DateIntervalInfo
  implements Cloneable, Freezable<DateIntervalInfo>, Serializable
{
  static final int currentSerialVersion = 1;
  
  public static final class PatternInfo
    implements Cloneable, Serializable
  {
    static final int currentSerialVersion = 1;
    private static final long serialVersionUID = 1L;
    private final String fIntervalPatternFirstPart;
    private final String fIntervalPatternSecondPart;
    private final boolean fFirstDateInPtnIsLaterDate;
    
    public PatternInfo(String firstPart, String secondPart, boolean firstDateInPtnIsLaterDate)
    {
      fIntervalPatternFirstPart = firstPart;
      fIntervalPatternSecondPart = secondPart;
      fFirstDateInPtnIsLaterDate = firstDateInPtnIsLaterDate;
    }
    



    public String getFirstPart()
    {
      return fIntervalPatternFirstPart;
    }
    



    public String getSecondPart()
    {
      return fIntervalPatternSecondPart;
    }
    



    public boolean firstDateInPtnIsLaterDate()
    {
      return fFirstDateInPtnIsLaterDate;
    }
    



    public boolean equals(Object a)
    {
      if ((a instanceof PatternInfo)) {
        PatternInfo patternInfo = (PatternInfo)a;
        return (Utility.objectEquals(fIntervalPatternFirstPart, fIntervalPatternFirstPart)) && (Utility.objectEquals(fIntervalPatternSecondPart, fIntervalPatternSecondPart)) && (fFirstDateInPtnIsLaterDate == fFirstDateInPtnIsLaterDate);
      }
      

      return false;
    }
    



    public int hashCode()
    {
      int hash = fIntervalPatternFirstPart != null ? fIntervalPatternFirstPart.hashCode() : 0;
      if (fIntervalPatternSecondPart != null) {
        hash ^= fIntervalPatternSecondPart.hashCode();
      }
      if (fFirstDateInPtnIsLaterDate) {
        hash ^= 0xFFFFFFFF;
      }
      return hash;
    }
  }
  


  static final String[] CALENDAR_FIELD_TO_PATTERN_LETTER = { "G", "y", "M", "w", "W", "d", "D", "E", "F", "a", "h", "H", "m" };
  



  private static final long serialVersionUID = 1L;
  



  private static final int MINIMUM_SUPPORTED_CALENDAR_FIELD = 12;
  



  private static String FALLBACK_STRING = "fallback";
  private static String LATEST_FIRST_PREFIX = "latestFirst:";
  private static String EARLIEST_FIRST_PREFIX = "earliestFirst:";
  

  private static final ICUCache<String, DateIntervalInfo> DIICACHE = new SimpleCache();
  

  private String fFallbackIntervalPattern;
  
  private boolean fFirstDateInPtnIsLaterDate = false;
  

  private Map<String, Map<String, PatternInfo>> fIntervalPatterns = null;
  
  private transient boolean frozen = false;
  




  private transient boolean fIntervalPatternsReadOnly = false;
  












  /**
   * @deprecated
   */
  public DateIntervalInfo()
  {
    fIntervalPatterns = new HashMap();
    fFallbackIntervalPattern = "{0} – {1}";
  }
  








  public DateIntervalInfo(ULocale locale)
  {
    initializeData(locale);
  }
  





  private void initializeData(ULocale locale)
  {
    String key = locale.toString();
    DateIntervalInfo dii = (DateIntervalInfo)DIICACHE.get(key);
    if (dii == null)
    {
      setup(locale);
      
      fIntervalPatternsReadOnly = true;
      
      DIICACHE.put(key, ((DateIntervalInfo)clone()).freeze());
    } else {
      initializeFromReadOnlyPatterns(dii);
    }
  }
  





  private void initializeFromReadOnlyPatterns(DateIntervalInfo dii)
  {
    fFallbackIntervalPattern = fFallbackIntervalPattern;
    fFirstDateInPtnIsLaterDate = fFirstDateInPtnIsLaterDate;
    fIntervalPatterns = fIntervalPatterns;
    fIntervalPatternsReadOnly = true;
  }
  




  private void setup(ULocale locale)
  {
    int DEFAULT_HASH_SIZE = 19;
    fIntervalPatterns = new HashMap(DEFAULT_HASH_SIZE);
    

    fFallbackIntervalPattern = "{0} – {1}";
    HashSet<String> skeletonSet = new HashSet();
    
    try
    {
      ULocale currentLocale = locale;
      
      String calendarTypeToUse = locale.getKeywordValue("calendar");
      if (calendarTypeToUse == null) {
        String[] preferredCalendarTypes = Calendar.getKeywordValuesForLocale("calendar", locale, true);
        calendarTypeToUse = preferredCalendarTypes[0];
      }
      if (calendarTypeToUse == null) {
        calendarTypeToUse = "gregorian";
      }
      do {
        String name = currentLocale.getName();
        if (name.length() == 0) {
          break;
        }
        
        ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", currentLocale);
        








        ICUResourceBundle itvDtPtnResource = rb.getWithFallback("calendar/" + calendarTypeToUse + "/intervalFormats");
        
        String fallback = itvDtPtnResource.getStringWithFallback(FALLBACK_STRING);
        setFallbackIntervalPattern(fallback);
        int size = itvDtPtnResource.getSize();
        for (int index = 0; index < size; index++) {
          String skeleton = itvDtPtnResource.get(index).getKey();
          if (!skeletonSet.contains(skeleton))
          {

            skeletonSet.add(skeleton);
            if (skeleton.compareTo(FALLBACK_STRING) != 0)
            {

              ICUResourceBundle intervalPatterns = (ICUResourceBundle)itvDtPtnResource.get(skeleton);
              int ptnNum = intervalPatterns.getSize();
              for (int ptnIndex = 0; ptnIndex < ptnNum; ptnIndex++) {
                String key = intervalPatterns.get(ptnIndex).getKey();
                String pattern = intervalPatterns.get(ptnIndex).getString();
                
                int calendarField = -1;
                if (key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[1]) == 0) {
                  calendarField = 1;
                } else if (key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[2]) == 0) {
                  calendarField = 2;
                } else if (key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[5]) == 0) {
                  calendarField = 5;
                } else if (key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[9]) == 0) {
                  calendarField = 9;
                } else if (key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[10]) == 0) {
                  calendarField = 10;
                } else if (key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[12]) == 0) {
                  calendarField = 12;
                }
                
                if (calendarField != -1)
                  setIntervalPatternInternally(skeleton, key, pattern);
              }
            }
          }
        }
        try { UResourceBundle parentNameBundle = rb.get("%%Parent");
          currentLocale = new ULocale(parentNameBundle.getString());
        } catch (MissingResourceException e) {
          currentLocale = currentLocale.getFallback();
        }
        if (currentLocale == null) break; } while (!currentLocale.getBaseName().equals("root"));
    }
    catch (MissingResourceException e) {}
  }
  






  private static int splitPatternInto2Part(String intervalPattern)
  {
    boolean inQuote = false;
    char prevCh = '\000';
    int count = 0;
    




    int[] patternRepeated = new int[58];
    
    int PATTERN_CHAR_BASE = 65;
    





    boolean foundRepetition = false;
    for (int i = 0; i < intervalPattern.length(); i++) {
      char ch = intervalPattern.charAt(i);
      
      if ((ch != prevCh) && (count > 0))
      {
        int repeated = patternRepeated[(prevCh - PATTERN_CHAR_BASE)];
        if (repeated == 0) {
          patternRepeated[(prevCh - PATTERN_CHAR_BASE)] = 1;
        } else {
          foundRepetition = true;
          break;
        }
        count = 0;
      }
      if (ch == '\'')
      {

        if ((i + 1 < intervalPattern.length()) && (intervalPattern.charAt(i + 1) == '\''))
        {
          i++;
        } else {
          inQuote = !inQuote;
        }
      }
      else if ((!inQuote) && (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))))
      {

        prevCh = ch;
        count++;
      }
    }
    



    if ((count > 0) && (!foundRepetition) && 
      (patternRepeated[(prevCh - PATTERN_CHAR_BASE)] == 0)) {
      count = 0;
    }
    
    return i - count;
  }
  





































  public void setIntervalPattern(String skeleton, int lrgDiffCalUnit, String intervalPattern)
  {
    if (frozen) {
      throw new UnsupportedOperationException("no modification is allowed after DII is frozen");
    }
    if (lrgDiffCalUnit > 12) {
      throw new IllegalArgumentException("calendar field is larger than MINIMUM_SUPPORTED_CALENDAR_FIELD");
    }
    if (fIntervalPatternsReadOnly) {
      fIntervalPatterns = cloneIntervalPatterns(fIntervalPatterns);
      fIntervalPatternsReadOnly = false;
    }
    PatternInfo ptnInfo = setIntervalPatternInternally(skeleton, CALENDAR_FIELD_TO_PATTERN_LETTER[lrgDiffCalUnit], intervalPattern);
    

    if (lrgDiffCalUnit == 11) {
      setIntervalPattern(skeleton, CALENDAR_FIELD_TO_PATTERN_LETTER[9], ptnInfo);
      

      setIntervalPattern(skeleton, CALENDAR_FIELD_TO_PATTERN_LETTER[10], ptnInfo);

    }
    else if ((lrgDiffCalUnit == 5) || (lrgDiffCalUnit == 7))
    {
      setIntervalPattern(skeleton, CALENDAR_FIELD_TO_PATTERN_LETTER[5], ptnInfo);
    }
  }
  

















  private PatternInfo setIntervalPatternInternally(String skeleton, String lrgDiffCalUnit, String intervalPattern)
  {
    Map<String, PatternInfo> patternsOfOneSkeleton = (Map)fIntervalPatterns.get(skeleton);
    boolean emptyHash = false;
    if (patternsOfOneSkeleton == null) {
      patternsOfOneSkeleton = new HashMap();
      emptyHash = true;
    }
    boolean order = fFirstDateInPtnIsLaterDate;
    
    if (intervalPattern.startsWith(LATEST_FIRST_PREFIX)) {
      order = true;
      int prefixLength = LATEST_FIRST_PREFIX.length();
      intervalPattern = intervalPattern.substring(prefixLength, intervalPattern.length());
    } else if (intervalPattern.startsWith(EARLIEST_FIRST_PREFIX)) {
      order = false;
      int earliestFirstLength = EARLIEST_FIRST_PREFIX.length();
      intervalPattern = intervalPattern.substring(earliestFirstLength, intervalPattern.length());
    }
    PatternInfo itvPtnInfo = genPatternInfo(intervalPattern, order);
    
    patternsOfOneSkeleton.put(lrgDiffCalUnit, itvPtnInfo);
    if (emptyHash == true) {
      fIntervalPatterns.put(skeleton, patternsOfOneSkeleton);
    }
    
    return itvPtnInfo;
  }
  








  private void setIntervalPattern(String skeleton, String lrgDiffCalUnit, PatternInfo ptnInfo)
  {
    Map<String, PatternInfo> patternsOfOneSkeleton = (Map)fIntervalPatterns.get(skeleton);
    patternsOfOneSkeleton.put(lrgDiffCalUnit, ptnInfo);
  }
  








  static PatternInfo genPatternInfo(String intervalPattern, boolean laterDateFirst)
  {
    int splitPoint = splitPatternInto2Part(intervalPattern);
    
    String firstPart = intervalPattern.substring(0, splitPoint);
    String secondPart = null;
    if (splitPoint < intervalPattern.length()) {
      secondPart = intervalPattern.substring(splitPoint, intervalPattern.length());
    }
    
    return new PatternInfo(firstPart, secondPart, laterDateFirst);
  }
  











  public PatternInfo getIntervalPattern(String skeleton, int field)
  {
    if (field > 12) {
      throw new IllegalArgumentException("no support for field less than MINUTE");
    }
    Map<String, PatternInfo> patternsOfOneSkeleton = (Map)fIntervalPatterns.get(skeleton);
    if (patternsOfOneSkeleton != null) {
      PatternInfo intervalPattern = (PatternInfo)patternsOfOneSkeleton.get(CALENDAR_FIELD_TO_PATTERN_LETTER[field]);
      
      if (intervalPattern != null) {
        return intervalPattern;
      }
    }
    return null;
  }
  







  public String getFallbackIntervalPattern()
  {
    return fFallbackIntervalPattern;
  }
  

















  public void setFallbackIntervalPattern(String fallbackPattern)
  {
    if (frozen) {
      throw new UnsupportedOperationException("no modification is allowed after DII is frozen");
    }
    int firstPatternIndex = fallbackPattern.indexOf("{0}");
    int secondPatternIndex = fallbackPattern.indexOf("{1}");
    if ((firstPatternIndex == -1) || (secondPatternIndex == -1)) {
      throw new IllegalArgumentException("no pattern {0} or pattern {1} in fallbackPattern");
    }
    if (firstPatternIndex > secondPatternIndex) {
      fFirstDateInPtnIsLaterDate = true;
    }
    fFallbackIntervalPattern = fallbackPattern;
  }
  









  public boolean getDefaultOrder()
  {
    return fFirstDateInPtnIsLaterDate;
  }
  






  public Object clone()
  {
    if (frozen) {
      return this;
    }
    return cloneUnfrozenDII();
  }
  




  private Object cloneUnfrozenDII()
  {
    try
    {
      DateIntervalInfo other = (DateIntervalInfo)super.clone();
      fFallbackIntervalPattern = fFallbackIntervalPattern;
      fFirstDateInPtnIsLaterDate = fFirstDateInPtnIsLaterDate;
      if (fIntervalPatternsReadOnly) {
        fIntervalPatterns = fIntervalPatterns;
        fIntervalPatternsReadOnly = true;
      } else {
        fIntervalPatterns = cloneIntervalPatterns(fIntervalPatterns);
        fIntervalPatternsReadOnly = false;
      }
      frozen = false;
      return other;
    }
    catch (CloneNotSupportedException e) {
      throw new IllegalStateException("clone is not supported");
    }
  }
  

  private static Map<String, Map<String, PatternInfo>> cloneIntervalPatterns(Map<String, Map<String, PatternInfo>> patterns)
  {
    Map<String, Map<String, PatternInfo>> result = new HashMap();
    for (Map.Entry<String, Map<String, PatternInfo>> skeletonEntry : patterns.entrySet()) {
      String skeleton = (String)skeletonEntry.getKey();
      Map<String, PatternInfo> patternsOfOneSkeleton = (Map)skeletonEntry.getValue();
      Map<String, PatternInfo> oneSetPtn = new HashMap();
      for (Map.Entry<String, PatternInfo> calEntry : patternsOfOneSkeleton.entrySet()) {
        String calField = (String)calEntry.getKey();
        PatternInfo value = (PatternInfo)calEntry.getValue();
        oneSetPtn.put(calField, value);
      }
      result.put(skeleton, oneSetPtn);
    }
    return result;
  }
  





  public boolean isFrozen()
  {
    return frozen;
  }
  



  public DateIntervalInfo freeze()
  {
    frozen = true;
    fIntervalPatternsReadOnly = true;
    return this;
  }
  



  public DateIntervalInfo cloneAsThawed()
  {
    DateIntervalInfo result = (DateIntervalInfo)cloneUnfrozenDII();
    return result;
  }
  







  static void parseSkeleton(String skeleton, int[] skeletonFieldWidth)
  {
    int PATTERN_CHAR_BASE = 65;
    for (int i = 0; i < skeleton.length(); i++) {
      skeletonFieldWidth[(skeleton.charAt(i) - PATTERN_CHAR_BASE)] += 1;
    }
  }
  














  private static boolean stringNumeric(int fieldWidth, int anotherFieldWidth, char patternLetter)
  {
    if ((patternLetter == 'M') && (
      ((fieldWidth <= 2) && (anotherFieldWidth > 2)) || ((fieldWidth > 2) && (anotherFieldWidth <= 2))))
    {
      return true;
    }
    
    return false;
  }
  














  DateIntervalFormat.BestMatchInfo getBestSkeleton(String inputSkeleton)
  {
    String bestSkeleton = inputSkeleton;
    int[] inputSkeletonFieldWidth = new int[58];
    int[] skeletonFieldWidth = new int[58];
    
    int DIFFERENT_FIELD = 4096;
    int STRING_NUMERIC_DIFFERENCE = 256;
    int BASE = 65;
    



    boolean replaceZWithV = false;
    if (inputSkeleton.indexOf('z') != -1) {
      inputSkeleton = inputSkeleton.replace('z', 'v');
      replaceZWithV = true;
    }
    
    parseSkeleton(inputSkeleton, inputSkeletonFieldWidth);
    int bestDistance = Integer.MAX_VALUE;
    



    int bestFieldDifference = 0;
    for (String skeleton : fIntervalPatterns.keySet())
    {
      for (int i = 0; i < skeletonFieldWidth.length; i++) {
        skeletonFieldWidth[i] = 0;
      }
      parseSkeleton(skeleton, skeletonFieldWidth);
      
      int distance = 0;
      int fieldDifference = 1;
      for (int i = 0; i < inputSkeletonFieldWidth.length; i++) {
        int inputFieldWidth = inputSkeletonFieldWidth[i];
        int fieldWidth = skeletonFieldWidth[i];
        if (inputFieldWidth != fieldWidth)
        {

          if (inputFieldWidth == 0) {
            fieldDifference = -1;
            distance += 4096;
          } else if (fieldWidth == 0) {
            fieldDifference = -1;
            distance += 4096;
          } else if (stringNumeric(inputFieldWidth, fieldWidth, (char)(i + 65)))
          {
            distance += 256;
          } else {
            distance += Math.abs(inputFieldWidth - fieldWidth);
          } }
      }
      if (distance < bestDistance) {
        bestSkeleton = skeleton;
        bestDistance = distance;
        bestFieldDifference = fieldDifference;
      }
      if (distance == 0) {
        bestFieldDifference = 0;
        break;
      }
    }
    if ((replaceZWithV) && (bestFieldDifference != -1)) {
      bestFieldDifference = 2;
    }
    return new DateIntervalFormat.BestMatchInfo(bestSkeleton, bestFieldDifference);
  }
  



  public boolean equals(Object a)
  {
    if ((a instanceof DateIntervalInfo)) {
      DateIntervalInfo dtInfo = (DateIntervalInfo)a;
      return fIntervalPatterns.equals(fIntervalPatterns);
    }
    return false;
  }
  



  public int hashCode()
  {
    return fIntervalPatterns.hashCode();
  }
  
  /**
   * @deprecated
   */
  public Map<String, Set<String>> getPatterns()
  {
    LinkedHashMap<String, Set<String>> result = new LinkedHashMap();
    for (Map.Entry<String, Map<String, PatternInfo>> entry : fIntervalPatterns.entrySet()) {
      result.put(entry.getKey(), new LinkedHashSet(((Map)entry.getValue()).keySet()));
    }
    return result;
  }
}
