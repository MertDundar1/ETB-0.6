package com.ibm.icu.text;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;













































































































































































































































































































































































































































































































public class RuleBasedNumberFormat
  extends NumberFormat
{
  static final long serialVersionUID = -7664252765575395068L;
  public static final int SPELLOUT = 1;
  public static final int ORDINAL = 2;
  public static final int DURATION = 3;
  public static final int NUMBERING_SYSTEM = 4;
  private transient NFRuleSet[] ruleSets = null;
  



  private transient String[] ruleSetDescriptions = null;
  




  private transient NFRuleSet defaultRuleSet = null;
  





  private ULocale locale = null;
  





  private transient RbnfLenientScannerProvider scannerProvider = null;
  



  private transient boolean lookedForScanner;
  



  private transient DecimalFormatSymbols decimalFormatSymbols = null;
  





  private transient DecimalFormat decimalFormat = null;
  




  private boolean lenientParse = false;
  




  private transient String lenientParseRules;
  



  private transient String postProcessRules;
  



  private transient RBNFPostProcessor postProcessor;
  



  private Map<String, String[]> ruleSetDisplayNames;
  



  private String[] publicRuleSetNames;
  



  private static final boolean DEBUG = ICUDebug.enabled("rbnf");
  












  public RuleBasedNumberFormat(String description)
  {
    locale = ULocale.getDefault(ULocale.Category.FORMAT);
    init(description, (String[][])null);
  }
  





















  public RuleBasedNumberFormat(String description, String[][] localizations)
  {
    locale = ULocale.getDefault(ULocale.Category.FORMAT);
    init(description, localizations);
  }
  












  public RuleBasedNumberFormat(String description, Locale locale)
  {
    this(description, ULocale.forLocale(locale));
  }
  












  public RuleBasedNumberFormat(String description, ULocale locale)
  {
    this.locale = locale;
    init(description, (String[][])null);
  }
  
























  public RuleBasedNumberFormat(String description, String[][] localizations, ULocale locale)
  {
    this.locale = locale;
    init(description, localizations);
  }
  











  public RuleBasedNumberFormat(Locale locale, int format)
  {
    this(ULocale.forLocale(locale), format);
  }
  













  public RuleBasedNumberFormat(ULocale locale, int format)
  {
    this.locale = locale;
    
    ICUResourceBundle bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/rbnf", locale);
    




    ULocale uloc = bundle.getULocale();
    setLocale(uloc, uloc);
    
    String description = "";
    String[][] localizations = (String[][])null;
    
    try
    {
      description = bundle.getString(rulenames[(format - 1)]);
    }
    catch (MissingResourceException e) {
      try {
        ICUResourceBundle rules = bundle.getWithFallback("RBNFRules/" + rulenames[(format - 1)]);
        UResourceBundleIterator it = rules.getIterator();
        while (it.hasNext()) {
          description = description.concat(it.nextString());
        }
      }
      catch (MissingResourceException e1) {}
    }
    
    try
    {
      UResourceBundle locb = bundle.get(locnames[(format - 1)]);
      localizations = new String[locb.getSize()][];
      for (int i = 0; i < localizations.length; i++) {
        localizations[i] = locb.get(i).getStringArray();
      }
    }
    catch (MissingResourceException e) {}
    


    init(description, localizations);
  }
  

  private static final String[] rulenames = { "SpelloutRules", "OrdinalRules", "DurationRules", "NumberingSystemRules" };
  

  private static final String[] locnames = { "SpelloutLocalizations", "OrdinalLocalizations", "DurationLocalizations", "NumberingSystemLocalizations" };
  













  public RuleBasedNumberFormat(int format)
  {
    this(ULocale.getDefault(ULocale.Category.FORMAT), format);
  }
  








  public Object clone()
  {
    return super.clone();
  }
  







  public boolean equals(Object that)
  {
    if (!(that instanceof RuleBasedNumberFormat)) {
      return false;
    }
    

    RuleBasedNumberFormat that2 = (RuleBasedNumberFormat)that;
    

    if ((!locale.equals(locale)) || (lenientParse != lenientParse)) {
      return false;
    }
    

    if (ruleSets.length != ruleSets.length) {
      return false;
    }
    for (int i = 0; i < ruleSets.length; i++) {
      if (!ruleSets[i].equals(ruleSets[i])) {
        return false;
      }
    }
    
    return true;
  }
  



  /**
   * @deprecated
   */
  public int hashCode()
  {
    return super.hashCode();
  }
  










  public String toString()
  {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < ruleSets.length; i++) {
      result.append(ruleSets[i].toString());
    }
    return result.toString();
  }
  





  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeUTF(toString());
    out.writeObject(locale);
  }
  





  private void readObject(ObjectInputStream in)
    throws IOException
  {
    String description = in.readUTF();
    ULocale loc;
    try
    {
      loc = (ULocale)in.readObject();
    } catch (Exception e) {
      loc = ULocale.getDefault(ULocale.Category.FORMAT);
    }
    




    RuleBasedNumberFormat temp = new RuleBasedNumberFormat(description, loc);
    ruleSets = ruleSets;
    defaultRuleSet = defaultRuleSet;
    publicRuleSetNames = publicRuleSetNames;
    decimalFormatSymbols = decimalFormatSymbols;
    decimalFormat = decimalFormat;
    locale = locale;
  }
  









  public String[] getRuleSetNames()
  {
    return (String[])publicRuleSetNames.clone();
  }
  





  public ULocale[] getRuleSetDisplayNameLocales()
  {
    if (ruleSetDisplayNames != null) {
      Set<String> s = ruleSetDisplayNames.keySet();
      String[] locales = (String[])s.toArray(new String[s.size()]);
      Arrays.sort(locales, String.CASE_INSENSITIVE_ORDER);
      ULocale[] result = new ULocale[locales.length];
      for (int i = 0; i < locales.length; i++) {
        result[i] = new ULocale(locales[i]);
      }
      return result;
    }
    return null;
  }
  
  private String[] getNameListForLocale(ULocale loc) {
    if ((loc != null) && (ruleSetDisplayNames != null)) {
      String[] localeNames = { loc.getBaseName(), ULocale.getDefault(ULocale.Category.DISPLAY).getBaseName() };
      for (int i = 0; i < localeNames.length; i++) {
        String lname = localeNames[i];
        while (lname.length() > 0) {
          String[] names = (String[])ruleSetDisplayNames.get(lname);
          if (names != null) {
            return names;
          }
          lname = ULocale.getFallback(lname);
        }
      }
    }
    return null;
  }
  









  public String[] getRuleSetDisplayNames(ULocale loc)
  {
    String[] names = getNameListForLocale(loc);
    if (names != null) {
      return (String[])names.clone();
    }
    names = getRuleSetNames();
    for (int i = 0; i < names.length; i++) {
      names[i] = names[i].substring(1);
    }
    return names;
  }
  






  public String[] getRuleSetDisplayNames()
  {
    return getRuleSetDisplayNames(ULocale.getDefault(ULocale.Category.DISPLAY));
  }
  








  public String getRuleSetDisplayName(String ruleSetName, ULocale loc)
  {
    String[] rsnames = publicRuleSetNames;
    for (int ix = 0; ix < rsnames.length; ix++) {
      if (rsnames[ix].equals(ruleSetName)) {
        String[] names = getNameListForLocale(loc);
        if (names != null) {
          return names[ix];
        }
        return rsnames[ix].substring(1);
      }
    }
    throw new IllegalArgumentException("unrecognized rule set name: " + ruleSetName);
  }
  






  public String getRuleSetDisplayName(String ruleSetName)
  {
    return getRuleSetDisplayName(ruleSetName, ULocale.getDefault(ULocale.Category.DISPLAY));
  }
  






  public String format(double number, String ruleSet)
    throws IllegalArgumentException
  {
    if (ruleSet.startsWith("%%")) {
      throw new IllegalArgumentException("Can't use internal rule set");
    }
    return format(number, findRuleSet(ruleSet));
  }
  










  public String format(long number, String ruleSet)
    throws IllegalArgumentException
  {
    if (ruleSet.startsWith("%%")) {
      throw new IllegalArgumentException("Can't use internal rule set");
    }
    return format(number, findRuleSet(ruleSet));
  }
  













  public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition ignore)
  {
    toAppendTo.append(format(number, defaultRuleSet));
    return toAppendTo;
  }
  

















  public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition ignore)
  {
    toAppendTo.append(format(number, defaultRuleSet));
    return toAppendTo;
  }
  







  public StringBuffer format(BigInteger number, StringBuffer toAppendTo, FieldPosition pos)
  {
    return format(new com.ibm.icu.math.BigDecimal(number), toAppendTo, pos);
  }
  







  public StringBuffer format(java.math.BigDecimal number, StringBuffer toAppendTo, FieldPosition pos)
  {
    return format(new com.ibm.icu.math.BigDecimal(number), toAppendTo, pos);
  }
  








  public StringBuffer format(com.ibm.icu.math.BigDecimal number, StringBuffer toAppendTo, FieldPosition pos)
  {
    return format(number.doubleValue(), toAppendTo, pos);
  }
  




















  public Number parse(String text, ParsePosition parsePosition)
  {
    String workingText = text.substring(parsePosition.getIndex());
    ParsePosition workingPos = new ParsePosition(0);
    Number tempResult = null;
    


    Number result = Long.valueOf(0L);
    ParsePosition highWaterMark = new ParsePosition(workingPos.getIndex());
    




    for (int i = ruleSets.length - 1; i >= 0; i--)
    {
      if ((ruleSets[i].isPublic()) && (ruleSets[i].isParseable()))
      {




        tempResult = ruleSets[i].parse(workingText, workingPos, Double.MAX_VALUE);
        if (workingPos.getIndex() > highWaterMark.getIndex()) {
          result = tempResult;
          highWaterMark.setIndex(workingPos.getIndex());
        }
        






        if (highWaterMark.getIndex() == workingText.length()) {
          break;
        }
        


        workingPos.setIndex(0);
      }
    }
    

    parsePosition.setIndex(parsePosition.getIndex() + highWaterMark.getIndex());
    



    return result;
  }
  













  public void setLenientParseMode(boolean enabled)
  {
    lenientParse = enabled;
  }
  






  public boolean lenientParseEnabled()
  {
    return lenientParse;
  }
  








  public void setLenientScannerProvider(RbnfLenientScannerProvider scannerProvider)
  {
    this.scannerProvider = scannerProvider;
  }
  










  public RbnfLenientScannerProvider getLenientScannerProvider()
  {
    if ((scannerProvider == null) && (lenientParse) && (!lookedForScanner)) {
      try
      {
        lookedForScanner = true;
        Class<?> cls = Class.forName("com.ibm.icu.text.RbnfScannerProviderImpl");
        RbnfLenientScannerProvider provider = (RbnfLenientScannerProvider)cls.newInstance();
        setLenientScannerProvider(provider);
      }
      catch (Exception e) {}
    }
    



    return scannerProvider;
  }
  






  public void setDefaultRuleSet(String ruleSetName)
  {
    if (ruleSetName == null) {
      if (publicRuleSetNames.length > 0) {
        defaultRuleSet = findRuleSet(publicRuleSetNames[0]);
      } else {
        defaultRuleSet = null;
        int n = ruleSets.length;
        for (;;) { n--; if (n < 0) break;
          String currentName = ruleSets[n].getName();
          if ((currentName.equals("%spellout-numbering")) || (currentName.equals("%digits-ordinal")) || (currentName.equals("%duration")))
          {


            defaultRuleSet = ruleSets[n];
            return;
          }
        }
        
        n = ruleSets.length;
        do { n--; if (n < 0) break;
        } while (!ruleSets[n].isPublic());
        defaultRuleSet = ruleSets[n];
      }
    }
    else
    {
      if (ruleSetName.startsWith("%%")) {
        throw new IllegalArgumentException("cannot use private rule set: " + ruleSetName);
      }
      defaultRuleSet = findRuleSet(ruleSetName);
    }
  }
  




  public String getDefaultRuleSetName()
  {
    if ((defaultRuleSet != null) && (defaultRuleSet.isPublic())) {
      return defaultRuleSet.getName();
    }
    return "";
  }
  







  public void setDecimalFormatSymbols(DecimalFormatSymbols newSymbols)
  {
    if (newSymbols != null) {
      decimalFormatSymbols = ((DecimalFormatSymbols)newSymbols.clone());
      if (decimalFormat != null) {
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
      }
      

      for (int i = 0; i < ruleSets.length; i++) {
        ruleSets[i].parseRules(ruleSetDescriptions[i], this);
      }
    }
  }
  









  NFRuleSet getDefaultRuleSet()
  {
    return defaultRuleSet;
  }
  





  RbnfLenientScanner getLenientScanner()
  {
    if (lenientParse) {
      RbnfLenientScannerProvider provider = getLenientScannerProvider();
      if (provider != null) {
        return provider.get(locale, lenientParseRules);
      }
    }
    return null;
  }
  









  DecimalFormatSymbols getDecimalFormatSymbols()
  {
    if (decimalFormatSymbols == null) {
      decimalFormatSymbols = new DecimalFormatSymbols(locale);
    }
    return decimalFormatSymbols;
  }
  
  DecimalFormat getDecimalFormat() {
    if (decimalFormat == null) {
      decimalFormat = ((DecimalFormat)NumberFormat.getInstance(locale));
      
      if (decimalFormatSymbols != null) {
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
      }
    }
    return decimalFormat;
  }
  














  private String extractSpecial(StringBuilder description, String specialName)
  {
    String result = null;
    int lp = description.indexOf(specialName);
    if (lp != -1)
    {


      if ((lp == 0) || (description.charAt(lp - 1) == ';'))
      {


        int lpEnd = description.indexOf(";%", lp);
        
        if (lpEnd == -1) {
          lpEnd = description.length() - 1;
        }
        int lpStart = lp + specialName.length();
        while ((lpStart < lpEnd) && (PatternProps.isWhiteSpace(description.charAt(lpStart))))
        {
          lpStart++;
        }
        

        result = description.substring(lpStart, lpEnd);
        

        description.delete(lp, lpEnd + 1);
      }
    }
    return result;
  }
  







  private void init(String description, String[][] localizations)
  {
    initLocalizations(localizations);
    





    StringBuilder descBuf = stripWhitespace(description);
    





    lenientParseRules = extractSpecial(descBuf, "%%lenient-parse:");
    postProcessRules = extractSpecial(descBuf, "%%post-process:");
    



    int numRuleSets = 0;
    for (int p = descBuf.indexOf(";%"); p != -1; p = descBuf.indexOf(";%", p)) {
      numRuleSets++;
      p++;
    }
    numRuleSets++;
    

    ruleSets = new NFRuleSet[numRuleSets];
    







    ruleSetDescriptions = new String[numRuleSets];
    
    int curRuleSet = 0;
    int start = 0;
    for (int p = descBuf.indexOf(";%"); p != -1; p = descBuf.indexOf(";%", start)) {
      ruleSetDescriptions[curRuleSet] = descBuf.substring(start, p + 1);
      ruleSets[curRuleSet] = new NFRuleSet(ruleSetDescriptions, curRuleSet);
      curRuleSet++;
      start = p + 1;
    }
    ruleSetDescriptions[curRuleSet] = descBuf.substring(start);
    ruleSets[curRuleSet] = new NFRuleSet(ruleSetDescriptions, curRuleSet);
    













    boolean defaultNameFound = false;
    int n = ruleSets.length;
    defaultRuleSet = ruleSets[(ruleSets.length - 1)];
    for (;;) {
      n--; if (n < 0) break;
      String currentName = ruleSets[n].getName();
      if ((currentName.equals("%spellout-numbering")) || (currentName.equals("%digits-ordinal")) || (currentName.equals("%duration"))) {
        defaultRuleSet = ruleSets[n];
        defaultNameFound = true;
        break;
      }
    }
    
    if (!defaultNameFound) {
      for (int i = ruleSets.length - 1; i >= 0; i--) {
        if (!ruleSets[i].getName().startsWith("%%")) {
          defaultRuleSet = ruleSets[i];
          break;
        }
      }
    }
    


    for (int i = 0; i < ruleSets.length; i++) {
      ruleSets[i].parseRules(ruleSetDescriptions[i], this);
    }
    





    int publicRuleSetCount = 0;
    for (int i = 0; i < ruleSets.length; i++) {
      if (!ruleSets[i].getName().startsWith("%%")) {
        publicRuleSetCount++;
      }
    }
    

    String[] publicRuleSetTemp = new String[publicRuleSetCount];
    publicRuleSetCount = 0;
    for (int i = ruleSets.length - 1; i >= 0; i--) {
      if (!ruleSets[i].getName().startsWith("%%")) {
        publicRuleSetTemp[(publicRuleSetCount++)] = ruleSets[i].getName();
      }
    }
    
    if (publicRuleSetNames != null)
    {
      label591:
      for (int i = 0; i < publicRuleSetNames.length; i++) {
        String name = publicRuleSetNames[i];
        for (int j = 0; j < publicRuleSetTemp.length; j++) {
          if (name.equals(publicRuleSetTemp[j])) {
            break label591;
          }
        }
        throw new IllegalArgumentException("did not find public rule set: " + name);
      }
      
      defaultRuleSet = findRuleSet(publicRuleSetNames[0]);
    } else {
      publicRuleSetNames = publicRuleSetTemp;
    }
  }
  



  private void initLocalizations(String[][] localizations)
  {
    if (localizations != null) {
      publicRuleSetNames = ((String[])localizations[0].clone());
      
      Map<String, String[]> m = new HashMap();
      for (int i = 1; i < localizations.length; i++) {
        String[] data = localizations[i];
        String loc = data[0];
        String[] names = new String[data.length - 1];
        if (names.length != publicRuleSetNames.length) {
          throw new IllegalArgumentException("public name length: " + publicRuleSetNames.length + " != localized names[" + i + "] length: " + names.length);
        }
        
        System.arraycopy(data, 1, names, 0, names.length);
        m.put(loc, names);
      }
      
      if (!m.isEmpty()) {
        ruleSetDisplayNames = m;
      }
    }
  }
  








  private StringBuilder stripWhitespace(String description)
  {
    StringBuilder result = new StringBuilder();
    

    int start = 0;
    while ((start != -1) && (start < description.length()))
    {

      while ((start < description.length()) && (PatternProps.isWhiteSpace(description.charAt(start)))) {
        start++;
      }
      

      if ((start < description.length()) && (description.charAt(start) == ';')) {
        start++;


      }
      else
      {

        int p = description.indexOf(';', start);
        if (p == -1)
        {

          result.append(description.substring(start));
          start = -1;
        }
        else if (p < description.length()) {
          result.append(description.substring(start, p + 1));
          start = p + 1;


        }
        else
        {


          start = -1;
        }
      } }
    return result;
  }
  




































  private String format(double number, NFRuleSet ruleSet)
  {
    StringBuffer result = new StringBuffer();
    ruleSet.format(number, result, 0);
    postProcess(result, ruleSet);
    return result.toString();
  }
  

















  private String format(long number, NFRuleSet ruleSet)
  {
    StringBuffer result = new StringBuffer();
    ruleSet.format(number, result, 0);
    postProcess(result, ruleSet);
    return result.toString();
  }
  


  private void postProcess(StringBuffer result, NFRuleSet ruleSet)
  {
    if (postProcessRules != null) {
      if (postProcessor == null) {
        int ix = postProcessRules.indexOf(";");
        if (ix == -1) {
          ix = postProcessRules.length();
        }
        String ppClassName = postProcessRules.substring(0, ix).trim();
        try {
          Class<?> cls = Class.forName(ppClassName);
          postProcessor = ((RBNFPostProcessor)cls.newInstance());
          postProcessor.init(this, postProcessRules);
        }
        catch (Exception e)
        {
          if (DEBUG) { System.out.println("could not locate " + ppClassName + ", error " + e.getClass().getName() + ", " + e.getMessage());
          }
          postProcessor = null;
          postProcessRules = null;
          return;
        }
      }
      
      postProcessor.process(result, ruleSet);
    }
  }
  




  NFRuleSet findRuleSet(String name)
    throws IllegalArgumentException
  {
    for (int i = 0; i < ruleSets.length; i++) {
      if (ruleSets[i].getName().equals(name)) {
        return ruleSets[i];
      }
    }
    throw new IllegalArgumentException("No rule set named " + name);
  }
}
