package com.ibm.icu.impl;

import com.ibm.icu.text.PluralRules;
import com.ibm.icu.text.PluralRules.PluralType;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;













public class PluralRulesLoader
{
  private final Map<String, PluralRules> rulesIdToRules;
  private Map<String, String> localeIdToCardinalRulesId;
  private Map<String, String> localeIdToOrdinalRulesId;
  private Map<String, ULocale> rulesIdToEquivalentULocale;
  
  private PluralRulesLoader()
  {
    rulesIdToRules = new HashMap();
  }
  


  public ULocale[] getAvailableULocales()
  {
    Set<String> keys = getLocaleIdToRulesIdMap(PluralRules.PluralType.CARDINAL).keySet();
    ULocale[] locales = new ULocale[keys.size()];
    int n = 0;
    for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
      locales[(n++)] = ULocale.createCanonical((String)iter.next());
    }
    return locales;
  }
  


  public ULocale getFunctionalEquivalent(ULocale locale, boolean[] isAvailable)
  {
    if ((isAvailable != null) && (isAvailable.length > 0)) {
      String localeId = ULocale.canonicalize(locale.getBaseName());
      Map<String, String> idMap = getLocaleIdToRulesIdMap(PluralRules.PluralType.CARDINAL);
      isAvailable[0] = idMap.containsKey(localeId);
    }
    
    String rulesId = getRulesIdForLocale(locale, PluralRules.PluralType.CARDINAL);
    if ((rulesId == null) || (rulesId.trim().length() == 0)) {
      return ULocale.ROOT;
    }
    
    ULocale result = (ULocale)getRulesIdToEquivalentULocaleMap().get(rulesId);
    
    if (result == null) {
      return ULocale.ROOT;
    }
    
    return result;
  }
  


  private Map<String, String> getLocaleIdToRulesIdMap(PluralRules.PluralType type)
  {
    checkBuildRulesIdMaps();
    return type == PluralRules.PluralType.CARDINAL ? localeIdToCardinalRulesId : localeIdToOrdinalRulesId;
  }
  


  private Map<String, ULocale> getRulesIdToEquivalentULocaleMap()
  {
    checkBuildRulesIdMaps();
    return rulesIdToEquivalentULocale;
  }
  


  private void checkBuildRulesIdMaps()
  {
    boolean haveMap;
    

    synchronized (this) {
      haveMap = localeIdToCardinalRulesId != null;
    }
    if (!haveMap) {
      Map<String, String> tempLocaleIdToCardinalRulesId;
      Map<String, ULocale> tempRulesIdToEquivalentULocale;
      Object tempLocaleIdToOrdinalRulesId;
      try {
        UResourceBundle pluralb = getPluralBundle();
        
        UResourceBundle localeb = pluralb.get("locales");
        

        tempLocaleIdToCardinalRulesId = new TreeMap();
        
        tempRulesIdToEquivalentULocale = new HashMap();
        
        for (int i = 0; i < localeb.getSize(); i++) {
          UResourceBundle b = localeb.get(i);
          String id = b.getKey();
          String value = b.getString().intern();
          tempLocaleIdToCardinalRulesId.put(id, value);
          
          if (!tempRulesIdToEquivalentULocale.containsKey(value)) {
            tempRulesIdToEquivalentULocale.put(value, new ULocale(id));
          }
        }
        

        localeb = pluralb.get("locales_ordinals");
        tempLocaleIdToOrdinalRulesId = new TreeMap();
        for (int i = 0; i < localeb.getSize(); i++) {
          UResourceBundle b = localeb.get(i);
          String id = b.getKey();
          String value = b.getString().intern();
          ((Map)tempLocaleIdToOrdinalRulesId).put(id, value);
        }
      }
      catch (MissingResourceException e) {
        tempLocaleIdToCardinalRulesId = Collections.emptyMap();
        tempLocaleIdToOrdinalRulesId = Collections.emptyMap();
        tempRulesIdToEquivalentULocale = Collections.emptyMap();
      }
      
      synchronized (this) {
        if (localeIdToCardinalRulesId == null) {
          localeIdToCardinalRulesId = tempLocaleIdToCardinalRulesId;
          localeIdToOrdinalRulesId = ((Map)tempLocaleIdToOrdinalRulesId);
          rulesIdToEquivalentULocale = tempRulesIdToEquivalentULocale;
        }
      }
    }
  }
  




  public String getRulesIdForLocale(ULocale locale, PluralRules.PluralType type)
  {
    Map<String, String> idMap = getLocaleIdToRulesIdMap(type);
    String localeId = ULocale.canonicalize(locale.getBaseName());
    String rulesId = null;
    while (null == (rulesId = (String)idMap.get(localeId))) {
      int ix = localeId.lastIndexOf("_");
      if (ix == -1) {
        break;
      }
      localeId = localeId.substring(0, ix);
    }
    return rulesId;
  }
  




  public PluralRules getRulesForRulesId(String rulesId)
  {
    PluralRules rules = null;
    boolean hasRules;
    synchronized (rulesIdToRules) {
      hasRules = rulesIdToRules.containsKey(rulesId);
      if (hasRules) {
        rules = (PluralRules)rulesIdToRules.get(rulesId);
      }
    }
    if (!hasRules) {
      try {
        UResourceBundle pluralb = getPluralBundle();
        UResourceBundle rulesb = pluralb.get("rules");
        UResourceBundle setb = rulesb.get(rulesId);
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < setb.getSize(); i++) {
          UResourceBundle b = setb.get(i);
          if (i > 0) {
            sb.append("; ");
          }
          sb.append(b.getKey());
          sb.append(": ");
          sb.append(b.getString());
        }
        rules = PluralRules.parseDescription(sb.toString());
      }
      catch (ParseException e) {}catch (MissingResourceException e) {}
      
      synchronized (rulesIdToRules) {
        if (rulesIdToRules.containsKey(rulesId)) {
          rules = (PluralRules)rulesIdToRules.get(rulesId);
        } else {
          rulesIdToRules.put(rulesId, rules);
        }
      }
    }
    return rules;
  }
  


  public UResourceBundle getPluralBundle()
    throws MissingResourceException
  {
    return ICUResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "plurals", ICUResourceBundle.ICU_DATA_CLASS_LOADER, true);
  }
  





  public PluralRules forLocale(ULocale locale, PluralRules.PluralType type)
  {
    String rulesId = getRulesIdForLocale(locale, type);
    if ((rulesId == null) || (rulesId.trim().length() == 0)) {
      return PluralRules.DEFAULT;
    }
    PluralRules rules = getRulesForRulesId(rulesId);
    if (rules == null) {
      rules = PluralRules.DEFAULT;
    }
    return rules;
  }
  



  public static final PluralRulesLoader loader = new PluralRulesLoader();
}
