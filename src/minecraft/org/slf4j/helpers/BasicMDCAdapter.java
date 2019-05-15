package org.slf4j.helpers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.spi.MDCAdapter;



































public class BasicMDCAdapter
  implements MDCAdapter
{
  private InheritableThreadLocal<Map<String, String>> inheritableThreadLocal = new InheritableThreadLocal();
  
  public BasicMDCAdapter() {}
  
  static boolean isJDK14() {
    try { String javaVersion = System.getProperty("java.version");
      return javaVersion.startsWith("1.4");
    }
    catch (SecurityException se) {}
    return false;
  }
  

  static boolean IS_JDK14 = ;
  












  public void put(String key, String val)
  {
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    Map<String, String> map = (Map)inheritableThreadLocal.get();
    if (map == null) {
      map = Collections.synchronizedMap(new HashMap());
      inheritableThreadLocal.set(map);
    }
    map.put(key, val);
  }
  


  public String get(String key)
  {
    Map<String, String> Map = (Map)inheritableThreadLocal.get();
    if ((Map != null) && (key != null)) {
      return (String)Map.get(key);
    }
    return null;
  }
  



  public void remove(String key)
  {
    Map<String, String> map = (Map)inheritableThreadLocal.get();
    if (map != null) {
      map.remove(key);
    }
  }
  


  public void clear()
  {
    Map<String, String> map = (Map)inheritableThreadLocal.get();
    if (map != null) {
      map.clear();
      

      if (isJDK14()) {
        inheritableThreadLocal.set(null);
      } else {
        inheritableThreadLocal.remove();
      }
    }
  }
  





  public Set<String> getKeys()
  {
    Map<String, String> map = (Map)inheritableThreadLocal.get();
    if (map != null) {
      return map.keySet();
    }
    return null;
  }
  




  public Map<String, String> getCopyOfContextMap()
  {
    Map<String, String> oldMap = (Map)inheritableThreadLocal.get();
    if (oldMap != null) {
      Map<String, String> newMap = Collections.synchronizedMap(new HashMap());
      synchronized (oldMap) {
        newMap.putAll(oldMap);
      }
      return newMap;
    }
    return null;
  }
  
  public void setContextMap(Map<String, String> contextMap)
  {
    Map<String, String> map = Collections.synchronizedMap(new HashMap(contextMap));
    inheritableThreadLocal.set(map);
  }
}
