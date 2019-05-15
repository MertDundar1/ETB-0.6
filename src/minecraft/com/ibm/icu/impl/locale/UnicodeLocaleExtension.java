package com.ibm.icu.impl.locale;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;






public class UnicodeLocaleExtension
  extends Extension
{
  public static final char SINGLETON = 'u';
  private static final SortedSet<String> EMPTY_SORTED_SET = new TreeSet();
  private static final SortedMap<String, String> EMPTY_SORTED_MAP = new TreeMap();
  
  private SortedSet<String> _attributes = EMPTY_SORTED_SET;
  private SortedMap<String, String> _keywords = EMPTY_SORTED_MAP;
  




  public static final UnicodeLocaleExtension CA_JAPANESE = new UnicodeLocaleExtension();
  static { CA_JAPANESE_keywords = new TreeMap();
    CA_JAPANESE_keywords.put("ca", "japanese");
    CA_JAPANESE_value = "ca-japanese";
    
    NU_THAI = new UnicodeLocaleExtension();
    NU_THAI_keywords = new TreeMap();
    NU_THAI_keywords.put("nu", "thai");
    NU_THAI_value = "nu-thai";
  }
  
  public static final UnicodeLocaleExtension NU_THAI;
  private UnicodeLocaleExtension() { super('u'); }
  

  UnicodeLocaleExtension(SortedSet<String> attributes, SortedMap<String, String> keywords) {
    this();
    if ((attributes != null) && (attributes.size() > 0)) {
      _attributes = attributes;
    }
    if ((keywords != null) && (keywords.size() > 0)) {
      _keywords = keywords;
    }
    
    if ((_attributes.size() > 0) || (_keywords.size() > 0)) {
      StringBuilder sb = new StringBuilder();
      for (String attribute : _attributes) {
        sb.append("-").append(attribute);
      }
      for (Map.Entry<String, String> keyword : _keywords.entrySet()) {
        String key = (String)keyword.getKey();
        String value = (String)keyword.getValue();
        
        sb.append("-").append(key);
        if (value.length() > 0) {
          sb.append("-").append(value);
        }
      }
      _value = sb.substring(1);
    }
  }
  
  public Set<String> getUnicodeLocaleAttributes() {
    return Collections.unmodifiableSet(_attributes);
  }
  
  public Set<String> getUnicodeLocaleKeys() {
    return Collections.unmodifiableSet(_keywords.keySet());
  }
  
  public String getUnicodeLocaleType(String unicodeLocaleKey) {
    return (String)_keywords.get(unicodeLocaleKey);
  }
  
  public static boolean isSingletonChar(char c) {
    return 'u' == AsciiUtil.toLower(c);
  }
  
  public static boolean isAttribute(String s)
  {
    return (s.length() >= 3) && (s.length() <= 8) && (AsciiUtil.isAlphaNumericString(s));
  }
  
  public static boolean isKey(String s)
  {
    return (s.length() == 2) && (AsciiUtil.isAlphaNumericString(s));
  }
  
  public static boolean isTypeSubtag(String s)
  {
    return (s.length() >= 3) && (s.length() <= 8) && (AsciiUtil.isAlphaNumericString(s));
  }
}
