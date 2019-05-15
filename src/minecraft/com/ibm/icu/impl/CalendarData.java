package com.ibm.icu.impl;

import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.util.ArrayList;
import java.util.MissingResourceException;












public class CalendarData
{
  private ICUResourceBundle fBundle;
  private String fMainType;
  private String fFallbackType;
  
  public CalendarData(ULocale loc, String type)
  {
    this((ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", loc), type);
  }
  
  public CalendarData(ICUResourceBundle b, String type) {
    fBundle = b;
    if ((type == null) || (type.equals("")) || (type.equals("gregorian"))) {
      fMainType = "gregorian";
      fFallbackType = null;
    } else {
      fMainType = type;
      fFallbackType = "gregorian";
    }
  }
  




  public ICUResourceBundle get(String key)
  {
    try
    {
      return fBundle.getWithFallback("calendar/" + fMainType + "/" + key);
    } catch (MissingResourceException m) {
      if (fFallbackType != null) {
        return fBundle.getWithFallback("calendar/" + fFallbackType + "/" + key);
      }
      throw m;
    }
  }
  









  public ICUResourceBundle get(String key, String subKey)
  {
    try
    {
      return fBundle.getWithFallback("calendar/" + fMainType + "/" + key + "/format/" + subKey);
    } catch (MissingResourceException m) {
      if (fFallbackType != null) {
        return fBundle.getWithFallback("calendar/" + fFallbackType + "/" + key + "/format/" + subKey);
      }
      throw m;
    }
  }
  









  public ICUResourceBundle get(String key, String contextKey, String subKey)
  {
    try
    {
      return fBundle.getWithFallback("calendar/" + fMainType + "/" + key + "/" + contextKey + "/" + subKey);
    } catch (MissingResourceException m) {
      if (fFallbackType != null) {
        return fBundle.getWithFallback("calendar/" + fFallbackType + "/" + key + "/" + contextKey + "/" + subKey);
      }
      throw m;
    }
  }
  










  public ICUResourceBundle get(String key, String set, String contextKey, String subKey)
  {
    try
    {
      return fBundle.getWithFallback("calendar/" + fMainType + "/" + key + "/" + set + "/" + contextKey + "/" + subKey);
    } catch (MissingResourceException m) {
      if (fFallbackType != null) {
        return fBundle.getWithFallback("calendar/" + fFallbackType + "/" + key + "/" + set + "/" + contextKey + "/" + subKey);
      }
      throw m;
    }
  }
  
  public String[] getStringArray(String key)
  {
    return get(key).getStringArray();
  }
  
  public String[] getStringArray(String key, String subKey) {
    return get(key, subKey).getStringArray();
  }
  

  public String[] getStringArray(String key, String contextKey, String subKey) { return get(key, contextKey, subKey).getStringArray(); }
  
  public String[] getEras(String subkey) {
    ICUResourceBundle bundle = get("eras/" + subkey);
    return bundle.getStringArray();
  }
  
  public String[] getDateTimePatterns() { ICUResourceBundle bundle = get("DateTimePatterns");
    ArrayList<String> list = new ArrayList();
    UResourceBundleIterator iter = bundle.getIterator();
    while (iter.hasNext()) {
      UResourceBundle patResource = iter.next();
      int resourceType = patResource.getType();
      switch (resourceType) {
      case 0: 
        list.add(patResource.getString());
        break;
      case 8: 
        String[] items = patResource.getStringArray();
        list.add(items[0]);
      }
      
    }
    
    return (String[])list.toArray(new String[list.size()]);
  }
  
  public String[] getOverrides() {
    ICUResourceBundle bundle = get("DateTimePatterns");
    ArrayList<String> list = new ArrayList();
    UResourceBundleIterator iter = bundle.getIterator();
    while (iter.hasNext()) {
      UResourceBundle patResource = iter.next();
      int resourceType = patResource.getType();
      switch (resourceType) {
      case 0: 
        list.add(null);
        break;
      case 8: 
        String[] items = patResource.getStringArray();
        list.add(items[1]);
      }
      
    }
    return (String[])list.toArray(new String[list.size()]);
  }
  
  public ULocale getULocale() {
    return fBundle.getULocale();
  }
}
