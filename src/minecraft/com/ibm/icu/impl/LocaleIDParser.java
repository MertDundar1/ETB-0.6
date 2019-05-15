package com.ibm.icu.impl;

import com.ibm.icu.impl.locale.AsciiUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;



























public final class LocaleIDParser
{
  private char[] id;
  private int index;
  private StringBuilder buffer;
  private boolean canonicalize;
  private boolean hadCountry;
  Map<String, String> keywords;
  String baseName;
  private static final char KEYWORD_SEPARATOR = '@';
  private static final char HYPHEN = '-';
  private static final char KEYWORD_ASSIGN = '=';
  private static final char COMMA = ',';
  private static final char ITEM_SEPARATOR = ';';
  private static final char DOT = '.';
  private static final char UNDERSCORE = '_';
  private static final char DONE = '￿';
  
  public LocaleIDParser(String localeID)
  {
    this(localeID, false);
  }
  
  public LocaleIDParser(String localeID, boolean canonicalize) {
    id = localeID.toCharArray();
    index = 0;
    buffer = new StringBuilder(id.length + 5);
    this.canonicalize = canonicalize;
  }
  
  private void reset() {
    index = 0;
    buffer = new StringBuilder(id.length + 5);
  }
  




  private void append(char c)
  {
    buffer.append(c);
  }
  
  private void addSeparator() {
    append('_');
  }
  


  private String getString(int start)
  {
    return buffer.substring(start);
  }
  


  private void set(int pos, String s)
  {
    buffer.delete(pos, buffer.length());
    buffer.insert(pos, s);
  }
  


  private void append(String s)
  {
    buffer.append(s);
  }
  











  private char next()
  {
    if (index == id.length) {
      index += 1;
      return 65535;
    }
    
    return id[(index++)];
  }
  


  private void skipUntilTerminatorOrIDSeparator()
  {
    while (!isTerminatorOrIDSeparator(next())) {}
    index -= 1;
  }
  


  private boolean atTerminator()
  {
    return (index >= id.length) || (isTerminator(id[index]));
  }
  




  private boolean isTerminator(char c)
  {
    return (c == '@') || (c == 65535) || (c == '.');
  }
  


  private boolean isTerminatorOrIDSeparator(char c)
  {
    return (c == '_') || (c == '-') || (isTerminator(c));
  }
  



  private boolean haveExperimentalLanguagePrefix()
  {
    if (id.length > 2) {
      char c = id[1];
      if ((c == '-') || (c == '_')) {
        c = id[0];
        return (c == 'x') || (c == 'X') || (c == 'i') || (c == 'I');
      }
    }
    return false;
  }
  



  private boolean haveKeywordAssign()
  {
    for (int i = index; i < id.length; i++) {
      if (id[i] == '=') {
        return true;
      }
    }
    return false;
  }
  




  private int parseLanguage()
  {
    int startLength = buffer.length();
    
    if (haveExperimentalLanguagePrefix()) {
      append(AsciiUtil.toLower(id[0]));
      append('-');
      index = 2;
    }
    
    char c;
    while (!isTerminatorOrIDSeparator(c = next())) {
      append(AsciiUtil.toLower(c));
    }
    index -= 1;
    
    if (buffer.length() - startLength == 3) {
      String lang = LocaleIDs.threeToTwoLetterLanguage(getString(0));
      if (lang != null) {
        set(0, lang);
      }
    }
    
    return 0;
  }
  



  private void skipLanguage()
  {
    if (haveExperimentalLanguagePrefix()) {
      index = 2;
    }
    skipUntilTerminatorOrIDSeparator();
  }
  








  private int parseScript()
  {
    if (!atTerminator()) {
      int oldIndex = index;
      index += 1;
      
      int oldBlen = buffer.length();
      
      boolean firstPass = true;
      char c; while ((!isTerminatorOrIDSeparator(c = next())) && (AsciiUtil.isAlpha(c))) {
        if (firstPass) {
          addSeparator();
          append(AsciiUtil.toUpper(c));
          firstPass = false;
        } else {
          append(AsciiUtil.toLower(c));
        }
      }
      index -= 1;
      

      if (index - oldIndex != 5) {
        index = oldIndex;
        buffer.delete(oldBlen, buffer.length());
      } else {
        oldBlen++;
      }
      
      return oldBlen;
    }
    return buffer.length();
  }
  






  private void skipScript()
  {
    if (!atTerminator()) {
      int oldIndex = index;
      index += 1;
      
      char c;
      while ((!isTerminatorOrIDSeparator(c = next())) && (AsciiUtil.isAlpha(c))) {}
      index -= 1;
      
      if (index - oldIndex != 5) {
        index = oldIndex;
      }
    }
  }
  




  private int parseCountry()
  {
    if (!atTerminator()) {
      int oldIndex = index;
      index += 1;
      
      int oldBlen = buffer.length();
      
      boolean firstPass = true;
      char c; while (!isTerminatorOrIDSeparator(c = next())) {
        if (firstPass) {
          hadCountry = true;
          addSeparator();
          oldBlen++;
          firstPass = false;
        }
        append(AsciiUtil.toUpper(c));
      }
      index -= 1;
      
      int charsAppended = buffer.length() - oldBlen;
      
      if (charsAppended != 0)
      {

        if ((charsAppended < 2) || (charsAppended > 3))
        {

          index = oldIndex;
          oldBlen--;
          buffer.delete(oldBlen, buffer.length());
          hadCountry = false;
        }
        else if (charsAppended == 3) {
          String region = LocaleIDs.threeToTwoLetterRegion(getString(oldBlen));
          if (region != null) {
            set(oldBlen, region);
          }
        }
      }
      return oldBlen;
    }
    
    return buffer.length();
  }
  




  private void skipCountry()
  {
    if (!atTerminator()) {
      if ((id[index] == '_') || (id[index] == '-')) {
        index += 1;
      }
      



      int oldIndex = index;
      
      skipUntilTerminatorOrIDSeparator();
      int charsSkipped = index - oldIndex;
      if ((charsSkipped < 2) || (charsSkipped > 3)) {
        index = oldIndex;
      }
    }
  }
  























  private int parseVariant()
  {
    int oldBlen = buffer.length();
    
    boolean start = true;
    boolean needSeparator = true;
    boolean skipping = false;
    
    boolean firstPass = true;
    char c;
    while ((c = next()) != 65535) {
      if (c == '.') {
        start = false;
        skipping = true;
      } else if (c == '@') {
        if (haveKeywordAssign()) {
          break;
        }
        skipping = false;
        start = false;
        needSeparator = true;
      } else if (start) {
        start = false;
        if ((c != '_') && (c != '-')) {
          index -= 1;
        }
      } else if (!skipping) {
        if (needSeparator) {
          needSeparator = false;
          if ((firstPass) && (!hadCountry)) {
            addSeparator();
            oldBlen++;
          }
          addSeparator();
          if (firstPass) {
            oldBlen++;
            firstPass = false;
          }
        }
        c = AsciiUtil.toUpper(c);
        if ((c == '-') || (c == ',')) {
          c = '_';
        }
        append(c);
      }
    }
    index -= 1;
    
    return oldBlen;
  }
  





  public String getLanguage()
  {
    reset();
    return getString(parseLanguage());
  }
  


  public String getScript()
  {
    reset();
    skipLanguage();
    return getString(parseScript());
  }
  


  public String getCountry()
  {
    reset();
    skipLanguage();
    skipScript();
    return getString(parseCountry());
  }
  


  public String getVariant()
  {
    reset();
    skipLanguage();
    skipScript();
    skipCountry();
    return getString(parseVariant());
  }
  


  public String[] getLanguageScriptCountryVariant()
  {
    reset();
    return new String[] { getString(parseLanguage()), getString(parseScript()), getString(parseCountry()), getString(parseVariant()) };
  }
  




  public void setBaseName(String baseName)
  {
    this.baseName = baseName;
  }
  
  public void parseBaseName() {
    if (baseName != null) {
      set(0, baseName);
    } else {
      reset();
      parseLanguage();
      parseScript();
      parseCountry();
      parseVariant();
      

      int len = buffer.length();
      if ((len > 0) && (buffer.charAt(len - 1) == '_')) {
        buffer.deleteCharAt(len - 1);
      }
    }
  }
  



  public String getBaseName()
  {
    if (baseName != null) {
      return baseName;
    }
    parseBaseName();
    return getString(0);
  }
  



  public String getName()
  {
    parseBaseName();
    parseKeywords();
    return getString(0);
  }
  





  private boolean setToKeywordStart()
  {
    for (int i = index; i < id.length; i++) {
      if (id[i] == '@') {
        if (canonicalize) {
          i++; for (int j = i; j < id.length; j++)
            if (id[j] == '=') {
              index = i;
              return true;
            }
          break;
        }
        i++; if (i >= id.length) break;
        index = i;
        return true;
      }
    }
    


    return false;
  }
  
  private static boolean isDoneOrKeywordAssign(char c) {
    return (c == 65535) || (c == '=');
  }
  
  private static boolean isDoneOrItemSeparator(char c) {
    return (c == 65535) || (c == ';');
  }
  
  private String getKeyword() {
    int start = index;
    while (!isDoneOrKeywordAssign(next())) {}
    
    index -= 1;
    return AsciiUtil.toLowerString(new String(id, start, index - start).trim());
  }
  
  private String getValue() {
    int start = index;
    while (!isDoneOrItemSeparator(next())) {}
    
    index -= 1;
    return new String(id, start, index - start).trim();
  }
  
  private Comparator<String> getKeyComparator() {
    Comparator<String> comp = new Comparator() {
      public int compare(String lhs, String rhs) {
        return lhs.compareTo(rhs);
      }
    };
    return comp;
  }
  


  public Map<String, String> getKeywordMap()
  {
    if (keywords == null) {
      TreeMap<String, String> m = null;
      if (setToKeywordStart()) {
        do
        {
          String key = getKeyword();
          if (key.length() == 0) {
            break;
          }
          char c = next();
          if (c != '=')
          {
            if (c == 65535) {
              break;
            }
          }
          else
          {
            String value = getValue();
            if (value.length() != 0)
            {


              if (m == null)
                m = new TreeMap(getKeyComparator()); else {
                if (m.containsKey(key)) {
                  continue;
                }
              }
              m.put(key, value);
            } } } while (next() == ';');
      }
      keywords = (m != null ? m : Collections.emptyMap());
    }
    
    return keywords;
  }
  



  private int parseKeywords()
  {
    int oldBlen = buffer.length();
    Map<String, String> m = getKeywordMap();
    if (!m.isEmpty()) {
      boolean first = true;
      for (Map.Entry<String, String> e : m.entrySet()) {
        append(first ? '@' : ';');
        first = false;
        append((String)e.getKey());
        append('=');
        append((String)e.getValue());
      }
      if (!first) {
        oldBlen++;
      }
    }
    return oldBlen;
  }
  


  public Iterator<String> getKeywords()
  {
    Map<String, String> m = getKeywordMap();
    return m.isEmpty() ? null : m.keySet().iterator();
  }
  



  public String getKeywordValue(String keywordName)
  {
    Map<String, String> m = getKeywordMap();
    return m.isEmpty() ? null : (String)m.get(AsciiUtil.toLowerString(keywordName.trim()));
  }
  


  public void defaultKeywordValue(String keywordName, String value)
  {
    setKeywordValue(keywordName, value, false);
  }
  




  public void setKeywordValue(String keywordName, String value)
  {
    setKeywordValue(keywordName, value, true);
  }
  






  private void setKeywordValue(String keywordName, String value, boolean reset)
  {
    if (keywordName == null) {
      if (reset)
      {
        keywords = Collections.emptyMap();
      }
    } else {
      keywordName = AsciiUtil.toLowerString(keywordName.trim());
      if (keywordName.length() == 0) {
        throw new IllegalArgumentException("keyword must not be empty");
      }
      if (value != null) {
        value = value.trim();
        if (value.length() == 0) {
          throw new IllegalArgumentException("value must not be empty");
        }
      }
      Map<String, String> m = getKeywordMap();
      if (m.isEmpty()) {
        if (value != null)
        {
          keywords = new TreeMap(getKeyComparator());
          keywords.put(keywordName, value.trim());
        }
      }
      else if ((reset) || (!m.containsKey(keywordName))) {
        if (value != null) {
          m.put(keywordName, value);
        } else {
          m.remove(keywordName);
          if (m.isEmpty())
          {
            keywords = Collections.emptyMap();
          }
        }
      }
    }
  }
}
