package com.ibm.icu.text;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;



















public class NumberingSystem
{
  private String desc;
  private int radix;
  private boolean algorithmic;
  private String name;
  
  public NumberingSystem()
  {
    radix = 10;
    algorithmic = false;
    desc = "0123456789";
    name = "latn";
  }
  













  public static NumberingSystem getInstance(int radix_in, boolean isAlgorithmic_in, String desc_in)
  {
    return getInstance(null, radix_in, isAlgorithmic_in, desc_in);
  }
  















  private static NumberingSystem getInstance(String name_in, int radix_in, boolean isAlgorithmic_in, String desc_in)
  {
    if (radix_in < 2) {
      throw new IllegalArgumentException("Invalid radix for numbering system");
    }
    
    if ((!isAlgorithmic_in) && (
      (desc_in.length() != radix_in) || (!isValidDigitString(desc_in)))) {
      throw new IllegalArgumentException("Invalid digit string for numbering system");
    }
    
    NumberingSystem ns = new NumberingSystem();
    radix = radix_in;
    algorithmic = isAlgorithmic_in;
    desc = desc_in;
    name = name_in;
    return ns;
  }
  



  public static NumberingSystem getInstance(Locale inLocale)
  {
    return getInstance(ULocale.forLocale(inLocale));
  }
  




  public static NumberingSystem getInstance(ULocale locale)
  {
    String[] OTHER_NS_KEYWORDS = { "native", "traditional", "finance" };
    

    Boolean nsResolved = Boolean.valueOf(true);
    

    String numbersKeyword = locale.getKeywordValue("numbers");
    if (numbersKeyword != null) {
      for (String keyword : OTHER_NS_KEYWORDS) {
        if (numbersKeyword.equals(keyword)) {
          nsResolved = Boolean.valueOf(false);
          break;
        }
      }
    } else {
      numbersKeyword = "default";
      nsResolved = Boolean.valueOf(false);
    }
    
    if (nsResolved.booleanValue()) {
      NumberingSystem ns = getInstanceByName(numbersKeyword);
      if (ns != null) {
        return ns;
      }
      numbersKeyword = "default";
      nsResolved = Boolean.valueOf(false);
    }
    


    String baseName = locale.getBaseName();
    NumberingSystem ns = (NumberingSystem)cachedLocaleData.get(baseName + "@numbers=" + numbersKeyword);
    if (ns != null) {
      return ns;
    }
    


    String originalNumbersKeyword = numbersKeyword;
    String resolvedNumberingSystem = null;
    while (!nsResolved.booleanValue()) {
      try {
        ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
        rb = rb.getWithFallback("NumberElements");
        resolvedNumberingSystem = rb.getStringWithFallback(numbersKeyword);
        nsResolved = Boolean.valueOf(true);
      } catch (MissingResourceException ex) {
        if ((numbersKeyword.equals("native")) || (numbersKeyword.equals("finance"))) {
          numbersKeyword = "default";
        } else if (numbersKeyword.equals("traditional")) {
          numbersKeyword = "native";
        } else {
          nsResolved = Boolean.valueOf(true);
        }
      }
    }
    
    if (resolvedNumberingSystem != null) {
      ns = getInstanceByName(resolvedNumberingSystem);
    }
    
    if (ns == null) {
      ns = new NumberingSystem();
    }
    
    cachedLocaleData.put(baseName + "@numbers=" + originalNumbersKeyword, ns);
    return ns;
  }
  





  public static NumberingSystem getInstance()
  {
    return getInstance(ULocale.getDefault(ULocale.Category.FORMAT));
  }
  














  public static NumberingSystem getInstanceByName(String name)
  {
    NumberingSystem ns = (NumberingSystem)cachedStringData.get(name);
    if (ns != null)
      return ns;
    String description;
    int radix;
    boolean isAlgorithmic;
    try { UResourceBundle numberingSystemsInfo = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "numberingSystems");
      UResourceBundle nsCurrent = numberingSystemsInfo.get("numberingSystems");
      UResourceBundle nsTop = nsCurrent.get(name);
      
      description = nsTop.getString("desc");
      UResourceBundle nsRadixBundle = nsTop.get("radix");
      UResourceBundle nsAlgBundle = nsTop.get("algorithmic");
      radix = nsRadixBundle.getInt();
      int algorithmic = nsAlgBundle.getInt();
      
      isAlgorithmic = algorithmic == 1;
    }
    catch (MissingResourceException ex) {
      return null;
    }
    
    ns = getInstance(name, radix, isAlgorithmic, description);
    cachedStringData.put(name, ns);
    return ns;
  }
  





  public static String[] getAvailableNames()
  {
    UResourceBundle numberingSystemsInfo = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "numberingSystems");
    UResourceBundle nsCurrent = numberingSystemsInfo.get("numberingSystems");
    


    ArrayList<String> output = new ArrayList();
    UResourceBundleIterator it = nsCurrent.getIterator();
    while (it.hasNext()) {
      UResourceBundle temp = it.next();
      String nsName = temp.getKey();
      output.add(nsName);
    }
    return (String[])output.toArray(new String[output.size()]);
  }
  








  public static boolean isValidDigitString(String str)
  {
    int i = 0;
    UCharacterIterator it = UCharacterIterator.getInstance(str);
    
    it.setToStart();
    int c; while ((c = it.nextCodePoint()) != -1) {
      if (UCharacter.isSupplementary(c)) {
        return false;
      }
      i++;
    }
    if (i != 10) {
      return false;
    }
    return true;
  }
  



  public int getRadix()
  {
    return radix;
  }
  









  public String getDescription()
  {
    return desc;
  }
  



  public String getName()
  {
    return name;
  }
  





  public boolean isAlgorithmic()
  {
    return algorithmic;
  }
  








  private static ICUCache<String, NumberingSystem> cachedLocaleData = new SimpleCache();
  



  private static ICUCache<String, NumberingSystem> cachedStringData = new SimpleCache();
}
