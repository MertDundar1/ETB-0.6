package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.ICUData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;















public class ResourceBasedPeriodFormatterDataService
  extends PeriodFormatterDataService
{
  private Collection<String> availableLocales;
  private PeriodFormatterData lastData = null;
  private String lastLocale = null;
  private Map<String, PeriodFormatterData> cache = new HashMap();
  

  private static final String PATH = "data/";
  
  private static final ResourceBasedPeriodFormatterDataService singleton = new ResourceBasedPeriodFormatterDataService();
  


  public static ResourceBasedPeriodFormatterDataService getInstance()
  {
    return singleton;
  }
  


  private ResourceBasedPeriodFormatterDataService()
  {
    List<String> localeNames = new ArrayList();
    InputStream is = ICUData.getRequiredStream(getClass(), "data/index.txt");
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      
      String string = null;
      while (null != (string = br.readLine())) {
        string = string.trim();
        if ((!string.startsWith("#")) && (string.length() != 0))
        {

          localeNames.add(string); }
      }
      br.close();
    } catch (IOException e) {
      throw new IllegalStateException("IO Error reading data/index.txt: " + e.toString());
    }
    
    availableLocales = Collections.unmodifiableList(localeNames);
  }
  
  public PeriodFormatterData get(String localeName)
  {
    int x = localeName.indexOf('@');
    if (x != -1) {
      localeName = localeName.substring(0, x);
    }
    
    synchronized (this) {
      if ((lastLocale != null) && (lastLocale.equals(localeName))) {
        return lastData;
      }
      
      PeriodFormatterData ld = (PeriodFormatterData)cache.get(localeName);
      if (ld == null) {
        String ln = localeName;
        while (!availableLocales.contains(ln)) {
          int ix = ln.lastIndexOf("_");
          if (ix > -1) {
            ln = ln.substring(0, ix);
          } else if (!"test".equals(ln)) {
            ln = "test";
          } else {
            ln = null;
            break;
          }
        }
        if (ln != null) {
          String name = "data/pfd_" + ln + ".xml";
          try {
            InputStream is = ICUData.getStream(getClass(), name);
            if (is == null) {
              throw new MissingResourceException("no resource named " + name, name, "");
            }
            
            DataRecord dr = DataRecord.read(ln, new XMLRecordReader(new InputStreamReader(is, "UTF-8")));
            

            if (dr != null)
            {








              ld = new PeriodFormatterData(localeName, dr);
            }
          }
          catch (UnsupportedEncodingException e) {
            throw new MissingResourceException("Unhandled Encoding for resource " + name, name, "");
          }
        }
        else
        {
          throw new MissingResourceException("Duration data not found for  " + localeName, "data/", localeName);
        }
        





        cache.put(localeName, ld);
      }
      lastData = ld;
      lastLocale = localeName;
      
      return ld;
    }
  }
  
  public Collection<String> getAvailableLocales() {
    return availableLocales;
  }
}
