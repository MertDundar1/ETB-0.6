package com.ibm.icu.impl.locale;





public final class BaseLocale
{
  private static final boolean JDKIMPL = false;
  


  public static final String SEP = "_";
  


  private static final Cache CACHE = new Cache();
  public static final BaseLocale ROOT = getInstance("", "", "", "");
  
  private String _language = "";
  private String _script = "";
  private String _region = "";
  private String _variant = "";
  
  private volatile transient int _hash = 0;
  
  private BaseLocale(String language, String script, String region, String variant) {
    if (language != null) {
      _language = AsciiUtil.toLowerString(language).intern();
    }
    if (script != null) {
      _script = AsciiUtil.toTitleString(script).intern();
    }
    if (region != null) {
      _region = AsciiUtil.toUpperString(region).intern();
    }
    if (variant != null)
    {



      _variant = AsciiUtil.toUpperString(variant).intern();
    }
  }
  










  public static BaseLocale getInstance(String language, String script, String region, String variant)
  {
    Key key = new Key(language, script, region, variant);
    BaseLocale baseLocale = (BaseLocale)CACHE.get(key);
    return baseLocale;
  }
  
  public String getLanguage() {
    return _language;
  }
  
  public String getScript() {
    return _script;
  }
  
  public String getRegion() {
    return _region;
  }
  
  public String getVariant() {
    return _variant;
  }
  
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof BaseLocale)) {
      return false;
    }
    BaseLocale other = (BaseLocale)obj;
    return (hashCode() == other.hashCode()) && (_language.equals(_language)) && (_script.equals(_script)) && (_region.equals(_region)) && (_variant.equals(_variant));
  }
  



  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    if (_language.length() > 0) {
      buf.append("language=");
      buf.append(_language);
    }
    if (_script.length() > 0) {
      if (buf.length() > 0) {
        buf.append(", ");
      }
      buf.append("script=");
      buf.append(_script);
    }
    if (_region.length() > 0) {
      if (buf.length() > 0) {
        buf.append(", ");
      }
      buf.append("region=");
      buf.append(_region);
    }
    if (_variant.length() > 0) {
      if (buf.length() > 0) {
        buf.append(", ");
      }
      buf.append("variant=");
      buf.append(_variant);
    }
    return buf.toString();
  }
  
  public int hashCode() {
    int h = _hash;
    if (h == 0)
    {
      for (int i = 0; i < _language.length(); i++) {
        h = 31 * h + _language.charAt(i);
      }
      for (int i = 0; i < _script.length(); i++) {
        h = 31 * h + _script.charAt(i);
      }
      for (int i = 0; i < _region.length(); i++) {
        h = 31 * h + _region.charAt(i);
      }
      for (int i = 0; i < _variant.length(); i++) {
        h = 31 * h + _variant.charAt(i);
      }
      _hash = h;
    }
    return h;
  }
  
  private static class Key implements Comparable<Key> {
    private String _lang = "";
    private String _scrt = "";
    private String _regn = "";
    private String _vart = "";
    private volatile int _hash;
    
    public Key(String language, String script, String region, String variant)
    {
      if (language != null) {
        _lang = language;
      }
      if (script != null) {
        _scrt = script;
      }
      if (region != null) {
        _regn = region;
      }
      if (variant != null) {
        _vart = variant;
      }
    }
    







    public boolean equals(Object obj)
    {
      return (this == obj) || (((obj instanceof Key)) && (AsciiUtil.caseIgnoreMatch(_lang, _lang)) && (AsciiUtil.caseIgnoreMatch(_scrt, _scrt)) && (AsciiUtil.caseIgnoreMatch(_regn, _regn)) && (AsciiUtil.caseIgnoreMatch(_vart, _vart)));
    }
    




    public int compareTo(Key other)
    {
      int res = AsciiUtil.caseIgnoreCompare(_lang, _lang);
      if (res == 0) {
        res = AsciiUtil.caseIgnoreCompare(_scrt, _scrt);
        if (res == 0) {
          res = AsciiUtil.caseIgnoreCompare(_regn, _regn);
          if (res == 0)
          {


            res = AsciiUtil.caseIgnoreCompare(_vart, _vart);
          }
        }
      }
      
      return res;
    }
    
    public int hashCode() {
      int h = _hash;
      if (h == 0)
      {
        for (int i = 0; i < _lang.length(); i++) {
          h = 31 * h + AsciiUtil.toLower(_lang.charAt(i));
        }
        for (int i = 0; i < _scrt.length(); i++) {
          h = 31 * h + AsciiUtil.toLower(_scrt.charAt(i));
        }
        for (int i = 0; i < _regn.length(); i++) {
          h = 31 * h + AsciiUtil.toLower(_regn.charAt(i));
        }
        for (int i = 0; i < _vart.length(); i++)
        {


          h = 31 * h + AsciiUtil.toLower(_vart.charAt(i));
        }
        
        _hash = h;
      }
      return h;
    }
    
    public static Key normalize(Key key) {
      String lang = AsciiUtil.toLowerString(_lang).intern();
      String scrt = AsciiUtil.toTitleString(_scrt).intern();
      String regn = AsciiUtil.toUpperString(_regn).intern();
      




      String vart = AsciiUtil.toUpperString(_vart).intern();
      
      return new Key(lang, scrt, regn, vart);
    }
  }
  
  private static class Cache extends LocaleObjectCache<BaseLocale.Key, BaseLocale>
  {
    public Cache() {}
    
    protected BaseLocale.Key normalizeKey(BaseLocale.Key key)
    {
      return BaseLocale.Key.normalize(key);
    }
    
    protected BaseLocale createObject(BaseLocale.Key key) {
      return new BaseLocale(BaseLocale.Key.access$000(key), BaseLocale.Key.access$100(key), BaseLocale.Key.access$200(key), BaseLocale.Key.access$300(key), null);
    }
  }
}
