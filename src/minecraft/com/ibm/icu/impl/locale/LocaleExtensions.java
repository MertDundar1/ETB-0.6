package com.ibm.icu.impl.locale;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;





public class LocaleExtensions
{
  private SortedMap<Character, Extension> _map;
  private String _id;
  private static final SortedMap<Character, Extension> EMPTY_MAP;
  public static final LocaleExtensions EMPTY_EXTENSIONS;
  public static final LocaleExtensions CALENDAR_JAPANESE;
  public static final LocaleExtensions NUMBER_THAI;
  
  static
  {
    EMPTY_MAP = Collections.unmodifiableSortedMap(new TreeMap());
    






    EMPTY_EXTENSIONS = new LocaleExtensions();
    EMPTY_EXTENSIONS_id = "";
    EMPTY_EXTENSIONS_map = EMPTY_MAP;
    
    CALENDAR_JAPANESE = new LocaleExtensions();
    CALENDAR_JAPANESE_id = "u-ca-japanese";
    CALENDAR_JAPANESE_map = new TreeMap();
    CALENDAR_JAPANESE_map.put(Character.valueOf('u'), UnicodeLocaleExtension.CA_JAPANESE);
    
    NUMBER_THAI = new LocaleExtensions();
    NUMBER_THAI_id = "u-nu-thai";
    NUMBER_THAI_map = new TreeMap();
    NUMBER_THAI_map.put(Character.valueOf('u'), UnicodeLocaleExtension.NU_THAI);
  }
  






  LocaleExtensions(Map<InternalLocaleBuilder.CaseInsensitiveChar, String> extensions, Set<InternalLocaleBuilder.CaseInsensitiveString> uattributes, Map<InternalLocaleBuilder.CaseInsensitiveString, String> ukeywords)
  {
    boolean hasExtension = (extensions != null) && (extensions.size() > 0);
    boolean hasUAttributes = (uattributes != null) && (uattributes.size() > 0);
    boolean hasUKeywords = (ukeywords != null) && (ukeywords.size() > 0);
    
    if ((!hasExtension) && (!hasUAttributes) && (!hasUKeywords)) {
      _map = EMPTY_MAP;
      _id = "";
      return;
    }
    

    _map = new TreeMap();
    if (hasExtension) {
      for (Map.Entry<InternalLocaleBuilder.CaseInsensitiveChar, String> ext : extensions.entrySet()) {
        char key = AsciiUtil.toLower(((InternalLocaleBuilder.CaseInsensitiveChar)ext.getKey()).value());
        String value = (String)ext.getValue();
        
        if (LanguageTag.isPrivateusePrefixChar(key))
        {
          value = InternalLocaleBuilder.removePrivateuseVariant(value);
          if (value == null) {}

        }
        else
        {
          Extension e = new Extension(key, AsciiUtil.toLowerString(value));
          _map.put(Character.valueOf(key), e);
        }
      }
    }
    if ((hasUAttributes) || (hasUKeywords)) {
      TreeSet<String> uaset = null;
      TreeMap<String, String> ukmap = null;
      
      if (hasUAttributes) {
        uaset = new TreeSet();
        for (InternalLocaleBuilder.CaseInsensitiveString cis : uattributes) {
          uaset.add(AsciiUtil.toLowerString(cis.value()));
        }
      }
      
      if (hasUKeywords) {
        ukmap = new TreeMap();
        for (Map.Entry<InternalLocaleBuilder.CaseInsensitiveString, String> kwd : ukeywords.entrySet()) {
          String key = AsciiUtil.toLowerString(((InternalLocaleBuilder.CaseInsensitiveString)kwd.getKey()).value());
          String type = AsciiUtil.toLowerString((String)kwd.getValue());
          ukmap.put(key, type);
        }
      }
      
      UnicodeLocaleExtension ule = new UnicodeLocaleExtension(uaset, ukmap);
      _map.put(Character.valueOf('u'), ule);
    }
    
    if (_map.size() == 0)
    {
      _map = EMPTY_MAP;
      _id = "";
    } else {
      _id = toID(_map);
    }
  }
  
  public Set<Character> getKeys() {
    return Collections.unmodifiableSet(_map.keySet());
  }
  
  public Extension getExtension(Character key) {
    return (Extension)_map.get(Character.valueOf(AsciiUtil.toLower(key.charValue())));
  }
  
  public String getExtensionValue(Character key) {
    Extension ext = (Extension)_map.get(Character.valueOf(AsciiUtil.toLower(key.charValue())));
    if (ext == null) {
      return null;
    }
    return ext.getValue();
  }
  
  public Set<String> getUnicodeLocaleAttributes() {
    Extension ext = (Extension)_map.get(Character.valueOf('u'));
    if (ext == null) {
      return Collections.emptySet();
    }
    assert ((ext instanceof UnicodeLocaleExtension));
    return ((UnicodeLocaleExtension)ext).getUnicodeLocaleAttributes();
  }
  
  public Set<String> getUnicodeLocaleKeys() {
    Extension ext = (Extension)_map.get(Character.valueOf('u'));
    if (ext == null) {
      return Collections.emptySet();
    }
    assert ((ext instanceof UnicodeLocaleExtension));
    return ((UnicodeLocaleExtension)ext).getUnicodeLocaleKeys();
  }
  
  public String getUnicodeLocaleType(String unicodeLocaleKey) {
    Extension ext = (Extension)_map.get(Character.valueOf('u'));
    if (ext == null) {
      return null;
    }
    assert ((ext instanceof UnicodeLocaleExtension));
    return ((UnicodeLocaleExtension)ext).getUnicodeLocaleType(AsciiUtil.toLowerString(unicodeLocaleKey));
  }
  
  public boolean isEmpty() {
    return _map.isEmpty();
  }
  
  public static boolean isValidKey(char c) {
    return (LanguageTag.isExtensionSingletonChar(c)) || (LanguageTag.isPrivateusePrefixChar(c));
  }
  
  public static boolean isValidUnicodeLocaleKey(String ukey) {
    return UnicodeLocaleExtension.isKey(ukey);
  }
  
  private static String toID(SortedMap<Character, Extension> map) {
    StringBuilder buf = new StringBuilder();
    Extension privuse = null;
    for (Map.Entry<Character, Extension> entry : map.entrySet()) {
      char singleton = ((Character)entry.getKey()).charValue();
      Extension extension = (Extension)entry.getValue();
      if (LanguageTag.isPrivateusePrefixChar(singleton)) {
        privuse = extension;
      } else {
        if (buf.length() > 0) {
          buf.append("-");
        }
        buf.append(extension);
      }
    }
    if (privuse != null) {
      if (buf.length() > 0) {
        buf.append("-");
      }
      buf.append(privuse);
    }
    return buf.toString();
  }
  
  public String toString()
  {
    return _id;
  }
  
  public String getID() {
    return _id;
  }
  
  public int hashCode() {
    return _id.hashCode();
  }
  
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof LocaleExtensions)) {
      return false;
    }
    return _id.equals(_id);
  }
  
  private LocaleExtensions() {}
}
