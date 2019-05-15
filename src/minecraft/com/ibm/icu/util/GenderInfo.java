package com.ibm.icu.util;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;















public class GenderInfo
{
  private final ListGenderStyle style;
  
  public static enum Gender
  {
    MALE,  FEMALE,  OTHER;
    

    private Gender() {}
  }
  
  public static GenderInfo getInstance(ULocale uLocale)
  {
    return genderInfoCache.get(uLocale);
  }
  




  public static GenderInfo getInstance(Locale locale)
  {
    return getInstance(ULocale.forLocale(locale));
  }
  










  public static enum ListGenderStyle
  {
    NEUTRAL, 
    





    MIXED_NEUTRAL, 
    





    MALE_TAINTS;
    
    static { fromNameMap = new HashMap(3);
      


      fromNameMap.put("neutral", NEUTRAL);
      fromNameMap.put("maleTaints", MALE_TAINTS);
      fromNameMap.put("mixedNeutral", MIXED_NEUTRAL);
    }
    


    public static ListGenderStyle fromName(String name)
    {
      ListGenderStyle result = (ListGenderStyle)fromNameMap.get(name);
      if (result == null) {
        throw new IllegalArgumentException("Unknown gender style name: " + name);
      }
      return result;
    }
    

    private static Map<String, ListGenderStyle> fromNameMap;
    
    private ListGenderStyle() {}
  }
  
  public Gender getListGender(Gender... genders)
  {
    return getListGender(Arrays.asList(genders));
  }
  





  public Gender getListGender(List<Gender> genders)
  {
    if (genders.size() == 0) {
      return Gender.OTHER;
    }
    if (genders.size() == 1) {
      return (Gender)genders.get(0);
    }
    switch (1.$SwitchMap$com$ibm$icu$util$GenderInfo$ListGenderStyle[style.ordinal()]) {
    case 1: 
      return Gender.OTHER;
    case 2: 
      boolean hasFemale = false;
      boolean hasMale = false;
      for (Gender gender : genders) {
        switch (1.$SwitchMap$com$ibm$icu$util$GenderInfo$Gender[gender.ordinal()]) {
        case 1: 
          if (hasMale) {
            return Gender.OTHER;
          }
          hasFemale = true;
          break;
        case 2: 
          if (hasFemale) {
            return Gender.OTHER;
          }
          hasMale = true;
          break;
        case 3: 
          return Gender.OTHER;
        }
      }
      return hasMale ? Gender.MALE : Gender.FEMALE;
    
    case 3: 
      for (Gender gender : genders) {
        if (gender != Gender.FEMALE) {
          return Gender.MALE;
        }
      }
      return Gender.FEMALE;
    }
    return Gender.OTHER;
  }
  





  public GenderInfo(ListGenderStyle genderStyle)
  {
    style = genderStyle;
  }
  
  private static GenderInfo neutral = new GenderInfo(ListGenderStyle.NEUTRAL);
  
  private static class Cache {
    private final ICUCache<ULocale, GenderInfo> cache = new SimpleCache();
    
    private Cache() {}
    
    public GenderInfo get(ULocale locale) { GenderInfo result = (GenderInfo)cache.get(locale);
      if (result == null) {
        result = load(locale);
        if (result == null) {
          ULocale fallback = locale.getFallback();
          



          result = fallback == null ? GenderInfo.neutral : get(fallback);
        }
        cache.put(locale, result);
      }
      return result;
    }
    
    private static GenderInfo load(ULocale ulocale) {
      UResourceBundle rb = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "genderList", ICUResourceBundle.ICU_DATA_CLASS_LOADER, true);
      


      UResourceBundle genderList = rb.get("genderList");
      try {
        return new GenderInfo(GenderInfo.ListGenderStyle.fromName(genderList.getString(ulocale.toString())));
      }
      catch (MissingResourceException mre) {}
      return null;
    }
  }
  

  private static Cache genderInfoCache = new Cache(null);
}
