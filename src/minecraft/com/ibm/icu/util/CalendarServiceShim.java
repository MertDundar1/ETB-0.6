package com.ibm.icu.util;

import com.ibm.icu.impl.CalendarUtil;
import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICULocaleService.ICUResourceBundleFactory;
import com.ibm.icu.impl.ICULocaleService.LocaleKey;
import com.ibm.icu.impl.ICULocaleService.LocaleKeyFactory;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.impl.ICUService.Factory;
import com.ibm.icu.impl.ICUService.Key;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;



class CalendarServiceShim
  extends Calendar.CalendarShim
{
  CalendarServiceShim() {}
  
  Locale[] getAvailableLocales()
  {
    if (service.isDefault()) {
      return ICUResourceBundle.getAvailableLocales();
    }
    return service.getAvailableLocales();
  }
  
  ULocale[] getAvailableULocales() {
    if (service.isDefault()) {
      return ICUResourceBundle.getAvailableULocales();
    }
    return service.getAvailableULocales();
  }
  
  private static final class CalFactory extends ICULocaleService.LocaleKeyFactory {
    private Calendar.CalendarFactory delegate;
    
    CalFactory(Calendar.CalendarFactory delegate) { super();
      this.delegate = delegate;
    }
    
    public Object create(ICUService.Key key, ICUService srvc) {
      if ((!handlesKey(key)) || (!(key instanceof ICULocaleService.LocaleKey))) {
        return null;
      }
      
      ICULocaleService.LocaleKey lkey = (ICULocaleService.LocaleKey)key;
      Object result = delegate.createCalendar(lkey.canonicalLocale());
      if (result == null) {
        result = srvc.getKey(key, null, this);
      }
      return result;
    }
    
    protected Set<String> getSupportedIDs() {
      return delegate.getSupportedLocaleNames();
    }
  }
  
  Calendar createInstance(ULocale desiredLocale) {
    ULocale[] actualLoc = new ULocale[1];
    if (desiredLocale.equals(ULocale.ROOT)) {
      desiredLocale = ULocale.ROOT;
    }
    
    ULocale useLocale;
    
    ULocale useLocale;
    if (desiredLocale.getKeywordValue("calendar") == null) {
      String calType = CalendarUtil.getCalendarType(desiredLocale);
      useLocale = desiredLocale.setKeywordValue("calendar", calType);
    } else {
      useLocale = desiredLocale;
    }
    
    Calendar cal = (Calendar)service.get(useLocale, actualLoc);
    if (cal == null) {
      throw new MissingResourceException("Unable to construct Calendar", "", "");
    }
    cal = (Calendar)cal.clone();
    









    return cal;
  }
  
  Object registerFactory(Calendar.CalendarFactory factory) {
    return service.registerFactory(new CalFactory(factory));
  }
  
  boolean unregister(Object k) {
    return service.unregisterFactory((ICUService.Factory)k);
  }
  
  private static class CalService extends ICULocaleService {
    CalService() {
      super();
      




      registerFactory(new ICULocaleService.ICUResourceBundleFactory()
      {
        protected Object handleCreate(ULocale loc, int kind, ICUService sercice)
        {
          return Calendar.createInstance(loc);
        }
        
      });
      markDefault();
    }
  }
  
  private static ICULocaleService service = new CalService();
}
