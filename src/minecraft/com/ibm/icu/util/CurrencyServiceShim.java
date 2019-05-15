package com.ibm.icu.util;

import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICULocaleService.ICUResourceBundleFactory;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.impl.ICUService.Factory;
import java.util.Locale;










final class CurrencyServiceShim
  extends Currency.ServiceShim
{
  CurrencyServiceShim() {}
  
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
  

  Currency createInstance(ULocale loc)
  {
    if (service.isDefault()) {
      return Currency.createCurrency(loc);
    }
    Currency curr = (Currency)service.get(loc);
    return curr;
  }
  
  Object registerInstance(Currency currency, ULocale locale) {
    return service.registerObject(currency, locale);
  }
  
  boolean unregister(Object registryKey) {
    return service.unregisterFactory((ICUService.Factory)registryKey);
  }
  
  private static class CFService extends ICULocaleService {
    CFService() {
      super();
      






      registerFactory(new ICULocaleService.ICUResourceBundleFactory()
      {
        protected Object handleCreate(ULocale loc, int kind, ICUService srvc)
        {
          return Currency.createCurrency(loc);
        }
        

      });
      markDefault();
    } }
  
  static final ICULocaleService service = new CFService();
}
