package com.ibm.icu.util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;








































































/**
 * @deprecated
 */
public class OverlayBundle
  extends ResourceBundle
{
  private String[] baseNames;
  private Locale locale;
  private ResourceBundle[] bundles;
  
  /**
   * @deprecated
   */
  public OverlayBundle(String[] baseNames, Locale locale)
  {
    this.baseNames = baseNames;
    this.locale = locale;
    bundles = new ResourceBundle[baseNames.length];
  }
  



  /**
   * @deprecated
   */
  protected Object handleGetObject(String key)
    throws MissingResourceException
  {
    Object o = null;
    
    for (int i = 0; i < bundles.length; i++) {
      load(i);
      try {
        o = bundles[i].getObject(key);
      } catch (MissingResourceException e) {
        if (i == bundles.length - 1) {
          throw e;
        }
      }
      if (o != null) {
        break;
      }
    }
    
    return o;
  }
  




  /**
   * @deprecated
   */
  public Enumeration<String> getKeys()
  {
    int i = bundles.length - 1;
    load(i);
    return bundles[i].getKeys();
  }
  



  private void load(int i)
    throws MissingResourceException
  {
    if (bundles[i] == null) {
      boolean tryWildcard = false;
      try {
        bundles[i] = ResourceBundle.getBundle(baseNames[i], locale);
        if (bundles[i].getLocale().equals(locale)) {
          return;
        }
        if ((locale.getCountry().length() != 0) && (i != bundles.length - 1)) {
          tryWildcard = true;
        }
      } catch (MissingResourceException e) {
        if (i == bundles.length - 1) {
          throw e;
        }
        tryWildcard = true;
      }
      if (tryWildcard) {
        Locale wildcard = new Locale("xx", locale.getCountry(), locale.getVariant());
        try
        {
          bundles[i] = ResourceBundle.getBundle(baseNames[i], wildcard);
        } catch (MissingResourceException e) {
          if (bundles[i] == null) {
            throw e;
          }
        }
      }
    }
  }
}
