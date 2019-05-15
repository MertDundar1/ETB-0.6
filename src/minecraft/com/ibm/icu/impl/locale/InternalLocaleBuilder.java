package com.ibm.icu.impl.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;








public final class InternalLocaleBuilder
{
  private static final boolean JDKIMPL = false;
  private String _language = "";
  private String _script = "";
  private String _region = "";
  private String _variant = "";
  
  private static final CaseInsensitiveChar PRIVUSE_KEY = new CaseInsensitiveChar("x".charAt(0));
  
  private HashMap<CaseInsensitiveChar, String> _extensions;
  private HashSet<CaseInsensitiveString> _uattributes;
  private HashMap<CaseInsensitiveString, String> _ukeywords;
  
  public InternalLocaleBuilder() {}
  
  public InternalLocaleBuilder setLanguage(String language)
    throws LocaleSyntaxException
  {
    if ((language == null) || (language.length() == 0)) {
      _language = "";
    } else {
      if (!LanguageTag.isLanguage(language)) {
        throw new LocaleSyntaxException("Ill-formed language: " + language, 0);
      }
      _language = language;
    }
    return this;
  }
  
  public InternalLocaleBuilder setScript(String script) throws LocaleSyntaxException {
    if ((script == null) || (script.length() == 0)) {
      _script = "";
    } else {
      if (!LanguageTag.isScript(script)) {
        throw new LocaleSyntaxException("Ill-formed script: " + script, 0);
      }
      _script = script;
    }
    return this;
  }
  
  public InternalLocaleBuilder setRegion(String region) throws LocaleSyntaxException {
    if ((region == null) || (region.length() == 0)) {
      _region = "";
    } else {
      if (!LanguageTag.isRegion(region)) {
        throw new LocaleSyntaxException("Ill-formed region: " + region, 0);
      }
      _region = region;
    }
    return this;
  }
  
  public InternalLocaleBuilder setVariant(String variant) throws LocaleSyntaxException {
    if ((variant == null) || (variant.length() == 0)) {
      _variant = "";
    }
    else {
      String var = variant.replaceAll("-", "_");
      int errIdx = checkVariants(var, "_");
      if (errIdx != -1) {
        throw new LocaleSyntaxException("Ill-formed variant: " + variant, errIdx);
      }
      _variant = var;
    }
    return this;
  }
  
  public InternalLocaleBuilder addUnicodeLocaleAttribute(String attribute) throws LocaleSyntaxException {
    if ((attribute == null) || (!UnicodeLocaleExtension.isAttribute(attribute))) {
      throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + attribute);
    }
    
    if (_uattributes == null) {
      _uattributes = new HashSet(4);
    }
    _uattributes.add(new CaseInsensitiveString(attribute));
    return this;
  }
  
  public InternalLocaleBuilder removeUnicodeLocaleAttribute(String attribute) throws LocaleSyntaxException {
    if ((attribute == null) || (!UnicodeLocaleExtension.isAttribute(attribute))) {
      throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + attribute);
    }
    if (_uattributes != null) {
      _uattributes.remove(new CaseInsensitiveString(attribute));
    }
    return this;
  }
  
  public InternalLocaleBuilder setUnicodeLocaleKeyword(String key, String type) throws LocaleSyntaxException {
    if (!UnicodeLocaleExtension.isKey(key)) {
      throw new LocaleSyntaxException("Ill-formed Unicode locale keyword key: " + key);
    }
    
    CaseInsensitiveString cikey = new CaseInsensitiveString(key);
    if (type == null) {
      if (_ukeywords != null)
      {
        _ukeywords.remove(cikey);
      }
    } else {
      if (type.length() != 0)
      {
        String tp = type.replaceAll("_", "-");
        
        StringTokenIterator itr = new StringTokenIterator(tp, "-");
        while (!itr.isDone()) {
          String s = itr.current();
          if (!UnicodeLocaleExtension.isTypeSubtag(s)) {
            throw new LocaleSyntaxException("Ill-formed Unicode locale keyword type: " + type, itr.currentStart());
          }
          itr.next();
        }
      }
      if (_ukeywords == null) {
        _ukeywords = new HashMap(4);
      }
      _ukeywords.put(cikey, type);
    }
    return this;
  }
  
  public InternalLocaleBuilder setExtension(char singleton, String value) throws LocaleSyntaxException
  {
    boolean isBcpPrivateuse = LanguageTag.isPrivateusePrefixChar(singleton);
    if ((!isBcpPrivateuse) && (!LanguageTag.isExtensionSingletonChar(singleton))) {
      throw new LocaleSyntaxException("Ill-formed extension key: " + singleton);
    }
    
    boolean remove = (value == null) || (value.length() == 0);
    CaseInsensitiveChar key = new CaseInsensitiveChar(singleton);
    
    if (remove) {
      if (UnicodeLocaleExtension.isSingletonChar(key.value()))
      {
        if (_uattributes != null) {
          _uattributes.clear();
        }
        if (_ukeywords != null) {
          _ukeywords.clear();
        }
      }
      else if ((_extensions != null) && (_extensions.containsKey(key))) {
        _extensions.remove(key);
      }
    }
    else
    {
      String val = value.replaceAll("_", "-");
      StringTokenIterator itr = new StringTokenIterator(val, "-");
      while (!itr.isDone()) {
        String s = itr.current();
        boolean validSubtag;
        boolean validSubtag; if (isBcpPrivateuse) {
          validSubtag = LanguageTag.isPrivateuseSubtag(s);
        } else {
          validSubtag = LanguageTag.isExtensionSubtag(s);
        }
        if (!validSubtag) {
          throw new LocaleSyntaxException("Ill-formed extension value: " + s, itr.currentStart());
        }
        itr.next();
      }
      
      if (UnicodeLocaleExtension.isSingletonChar(key.value())) {
        setUnicodeLocaleExtension(val);
      } else {
        if (_extensions == null) {
          _extensions = new HashMap(4);
        }
        _extensions.put(key, val);
      }
    }
    return this;
  }
  

  public InternalLocaleBuilder setExtensions(String subtags)
    throws LocaleSyntaxException
  {
    if ((subtags == null) || (subtags.length() == 0)) {
      clearExtensions();
      return this;
    }
    subtags = subtags.replaceAll("_", "-");
    StringTokenIterator itr = new StringTokenIterator(subtags, "-");
    
    List<String> extensions = null;
    String privateuse = null;
    
    int parsed = 0;
    


    while (!itr.isDone()) {
      String s = itr.current();
      if (!LanguageTag.isExtensionSingleton(s)) break;
      int start = itr.currentStart();
      String singleton = s;
      StringBuilder sb = new StringBuilder(singleton);
      
      itr.next();
      while (!itr.isDone()) {
        s = itr.current();
        if (!LanguageTag.isExtensionSubtag(s)) break;
        sb.append("-").append(s);
        parsed = itr.currentEnd();
        


        itr.next();
      }
      
      if (parsed < start) {
        throw new LocaleSyntaxException("Incomplete extension '" + singleton + "'", start);
      }
      
      if (extensions == null) {
        extensions = new ArrayList(4);
      }
      extensions.add(sb.toString());
    }
    


    if (!itr.isDone()) {
      String s = itr.current();
      if (LanguageTag.isPrivateusePrefix(s)) {
        int start = itr.currentStart();
        StringBuilder sb = new StringBuilder(s);
        
        itr.next();
        while (!itr.isDone()) {
          s = itr.current();
          if (!LanguageTag.isPrivateuseSubtag(s)) {
            break;
          }
          sb.append("-").append(s);
          parsed = itr.currentEnd();
          
          itr.next();
        }
        if (parsed <= start) {
          throw new LocaleSyntaxException("Incomplete privateuse:" + subtags.substring(start), start);
        }
        privateuse = sb.toString();
      }
    }
    

    if (!itr.isDone()) {
      throw new LocaleSyntaxException("Ill-formed extension subtags:" + subtags.substring(itr.currentStart()), itr.currentStart());
    }
    
    return setExtensions(extensions, privateuse);
  }
  



  private InternalLocaleBuilder setExtensions(List<String> bcpExtensions, String privateuse)
  {
    clearExtensions();
    HashSet<CaseInsensitiveChar> processedExtensions;
    if ((bcpExtensions != null) && (bcpExtensions.size() > 0)) {
      processedExtensions = new HashSet(bcpExtensions.size());
      for (String bcpExt : bcpExtensions) {
        CaseInsensitiveChar key = new CaseInsensitiveChar(bcpExt.charAt(0));
        
        if (!processedExtensions.contains(key))
        {
          if (UnicodeLocaleExtension.isSingletonChar(key.value())) {
            setUnicodeLocaleExtension(bcpExt.substring(2));
          } else {
            if (_extensions == null) {
              _extensions = new HashMap(4);
            }
            _extensions.put(key, bcpExt.substring(2));
          }
        }
      }
    }
    if ((privateuse != null) && (privateuse.length() > 0))
    {
      if (_extensions == null) {
        _extensions = new HashMap(1);
      }
      _extensions.put(new CaseInsensitiveChar(privateuse.charAt(0)), privateuse.substring(2));
    }
    
    return this;
  }
  


  public InternalLocaleBuilder setLanguageTag(LanguageTag langtag)
  {
    clear();
    if (langtag.getExtlangs().size() > 0) {
      _language = ((String)langtag.getExtlangs().get(0));
    } else {
      String language = langtag.getLanguage();
      if (!language.equals(LanguageTag.UNDETERMINED)) {
        _language = language;
      }
    }
    _script = langtag.getScript();
    _region = langtag.getRegion();
    
    List<String> bcpVariants = langtag.getVariants();
    if (bcpVariants.size() > 0) {
      StringBuilder var = new StringBuilder((String)bcpVariants.get(0));
      for (int i = 1; i < bcpVariants.size(); i++) {
        var.append("_").append((String)bcpVariants.get(i));
      }
      _variant = var.toString();
    }
    
    setExtensions(langtag.getExtensions(), langtag.getPrivateuse());
    
    return this;
  }
  
  public InternalLocaleBuilder setLocale(BaseLocale base, LocaleExtensions extensions) throws LocaleSyntaxException {
    String language = base.getLanguage();
    String script = base.getScript();
    String region = base.getRegion();
    String variant = base.getVariant();
    





























    if ((language.length() > 0) && (!LanguageTag.isLanguage(language))) {
      throw new LocaleSyntaxException("Ill-formed language: " + language);
    }
    
    if ((script.length() > 0) && (!LanguageTag.isScript(script))) {
      throw new LocaleSyntaxException("Ill-formed script: " + script);
    }
    
    if ((region.length() > 0) && (!LanguageTag.isRegion(region))) {
      throw new LocaleSyntaxException("Ill-formed region: " + region);
    }
    
    if (variant.length() > 0) {
      int errIdx = checkVariants(variant, "_");
      if (errIdx != -1) {
        throw new LocaleSyntaxException("Ill-formed variant: " + variant, errIdx);
      }
    }
    


    _language = language;
    _script = script;
    _region = region;
    _variant = variant;
    clearExtensions();
    
    Set<Character> extKeys = extensions == null ? null : extensions.getKeys();
    if (extKeys != null)
    {
      for (Character key : extKeys) {
        Extension e = extensions.getExtension(key);
        UnicodeLocaleExtension ue; if ((e instanceof UnicodeLocaleExtension)) {
          ue = (UnicodeLocaleExtension)e;
          for (String uatr : ue.getUnicodeLocaleAttributes()) {
            if (_uattributes == null) {
              _uattributes = new HashSet(4);
            }
            _uattributes.add(new CaseInsensitiveString(uatr));
          }
          for (String ukey : ue.getUnicodeLocaleKeys()) {
            if (_ukeywords == null) {
              _ukeywords = new HashMap(4);
            }
            _ukeywords.put(new CaseInsensitiveString(ukey), ue.getUnicodeLocaleType(ukey));
          }
        } else {
          if (_extensions == null) {
            _extensions = new HashMap(4);
          }
          _extensions.put(new CaseInsensitiveChar(key.charValue()), e.getValue());
        }
      }
    }
    return this;
  }
  
  public InternalLocaleBuilder clear() {
    _language = "";
    _script = "";
    _region = "";
    _variant = "";
    clearExtensions();
    return this;
  }
  
  public InternalLocaleBuilder clearExtensions() {
    if (_extensions != null) {
      _extensions.clear();
    }
    if (_uattributes != null) {
      _uattributes.clear();
    }
    if (_ukeywords != null) {
      _ukeywords.clear();
    }
    return this;
  }
  
  public BaseLocale getBaseLocale() {
    String language = _language;
    String script = _script;
    String region = _region;
    String variant = _variant;
    


    if (_extensions != null) {
      String privuse = (String)_extensions.get(PRIVUSE_KEY);
      if (privuse != null) {
        StringTokenIterator itr = new StringTokenIterator(privuse, "-");
        boolean sawPrefix = false;
        int privVarStart = -1;
        while (!itr.isDone()) {
          if (sawPrefix) {
            privVarStart = itr.currentStart();
            break;
          }
          if (AsciiUtil.caseIgnoreMatch(itr.current(), "lvariant")) {
            sawPrefix = true;
          }
          itr.next();
        }
        if (privVarStart != -1) {
          StringBuilder sb = new StringBuilder(variant);
          if (sb.length() != 0) {
            sb.append("_");
          }
          sb.append(privuse.substring(privVarStart).replaceAll("-", "_"));
          variant = sb.toString();
        }
      }
    }
    
    return BaseLocale.getInstance(language, script, region, variant);
  }
  
  public LocaleExtensions getLocaleExtensions() {
    if (((_extensions == null) || (_extensions.size() == 0)) && ((_uattributes == null) || (_uattributes.size() == 0)) && ((_ukeywords == null) || (_ukeywords.size() == 0)))
    {

      return LocaleExtensions.EMPTY_EXTENSIONS;
    }
    
    return new LocaleExtensions(_extensions, _uattributes, _ukeywords);
  }
  



  static String removePrivateuseVariant(String privuseVal)
  {
    StringTokenIterator itr = new StringTokenIterator(privuseVal, "-");
    



    int prefixStart = -1;
    boolean sawPrivuseVar = false;
    while (!itr.isDone()) {
      if (prefixStart != -1)
      {

        sawPrivuseVar = true;
        break;
      }
      if (AsciiUtil.caseIgnoreMatch(itr.current(), "lvariant")) {
        prefixStart = itr.currentStart();
      }
      itr.next();
    }
    if (!sawPrivuseVar) {
      return privuseVal;
    }
    
    assert ((prefixStart == 0) || (prefixStart > 1));
    return prefixStart == 0 ? null : privuseVal.substring(0, prefixStart - 1);
  }
  



  private int checkVariants(String variants, String sep)
  {
    StringTokenIterator itr = new StringTokenIterator(variants, sep);
    while (!itr.isDone()) {
      String s = itr.current();
      if (!LanguageTag.isVariant(s)) {
        return itr.currentStart();
      }
      itr.next();
    }
    return -1;
  }
  





  private void setUnicodeLocaleExtension(String subtags)
  {
    if (_uattributes != null) {
      _uattributes.clear();
    }
    if (_ukeywords != null) {
      _ukeywords.clear();
    }
    
    StringTokenIterator itr = new StringTokenIterator(subtags, "-");
    

    while ((!itr.isDone()) && 
      (UnicodeLocaleExtension.isAttribute(itr.current())))
    {

      if (_uattributes == null) {
        _uattributes = new HashSet(4);
      }
      _uattributes.add(new CaseInsensitiveString(itr.current()));
      itr.next();
    }
    

    CaseInsensitiveString key = null;
    
    int typeStart = -1;
    int typeEnd = -1;
    while (!itr.isDone()) {
      if (key != null) {
        if (UnicodeLocaleExtension.isKey(itr.current()))
        {
          assert ((typeStart == -1) || (typeEnd != -1));
          String type = typeStart == -1 ? "" : subtags.substring(typeStart, typeEnd);
          if (_ukeywords == null) {
            _ukeywords = new HashMap(4);
          }
          _ukeywords.put(key, type);
          

          CaseInsensitiveString tmpKey = new CaseInsensitiveString(itr.current());
          key = _ukeywords.containsKey(tmpKey) ? null : tmpKey;
          typeStart = typeEnd = -1;
        } else {
          if (typeStart == -1) {
            typeStart = itr.currentStart();
          }
          typeEnd = itr.currentEnd();
        }
      } else if (UnicodeLocaleExtension.isKey(itr.current()))
      {

        key = new CaseInsensitiveString(itr.current());
        if ((_ukeywords != null) && (_ukeywords.containsKey(key)))
        {
          key = null;
        }
      }
      
      if (!itr.hasNext()) {
        if (key == null)
          break;
        assert ((typeStart == -1) || (typeEnd != -1));
        String type = typeStart == -1 ? "" : subtags.substring(typeStart, typeEnd);
        if (_ukeywords == null) {
          _ukeywords = new HashMap(4);
        }
        _ukeywords.put(key, type); break;
      }
      


      itr.next();
    }
  }
  
  static class CaseInsensitiveString {
    private String _s;
    
    CaseInsensitiveString(String s) {
      _s = s;
    }
    
    public String value() {
      return _s;
    }
    
    public int hashCode() {
      return AsciiUtil.toLowerString(_s).hashCode();
    }
    
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof CaseInsensitiveString)) {
        return false;
      }
      return AsciiUtil.caseIgnoreMatch(_s, ((CaseInsensitiveString)obj).value());
    }
  }
  
  static class CaseInsensitiveChar {
    private char _c;
    
    CaseInsensitiveChar(char c) {
      _c = c;
    }
    
    public char value() {
      return _c;
    }
    
    public int hashCode() {
      return AsciiUtil.toLower(_c);
    }
    
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof CaseInsensitiveChar)) {
        return false;
      }
      return _c == AsciiUtil.toLower(((CaseInsensitiveChar)obj).value());
    }
  }
}
