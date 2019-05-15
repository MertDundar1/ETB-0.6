package org.slf4j.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.spi.MDCAdapter;

















public class LogbackMDCAdapter
  implements MDCAdapter
{
  final CopyOnInheritThreadLocal copyOnInheritThreadLocal = new CopyOnInheritThreadLocal();
  








  LogbackMDCAdapter() {}
  







  public void put(String key, String val)
    throws IllegalArgumentException
  {
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    
    HashMap<String, String> oldMap = (HashMap)copyOnInheritThreadLocal.get();
    
    HashMap<String, String> newMap = new HashMap();
    if (oldMap != null) {
      newMap.putAll(oldMap);
    }
    
    copyOnInheritThreadLocal.set(newMap);
    newMap.put(key, val);
  }
  




  public String get(String key)
  {
    HashMap<String, String> hashMap = (HashMap)copyOnInheritThreadLocal.get();
    
    if ((hashMap != null) && (key != null)) {
      return (String)hashMap.get(key);
    }
    return null;
  }
  








  public void remove(String key)
  {
    HashMap<String, String> oldMap = (HashMap)copyOnInheritThreadLocal.get();
    
    HashMap<String, String> newMap = new HashMap();
    if (oldMap != null) {
      newMap.putAll(oldMap);
    }
    
    copyOnInheritThreadLocal.set(newMap);
    newMap.remove(key);
  }
  


  public void clear()
  {
    HashMap<String, String> hashMap = (HashMap)copyOnInheritThreadLocal.get();
    
    if (hashMap != null) {
      hashMap.clear();
      copyOnInheritThreadLocal.remove();
    }
  }
  



  public Map<String, String> getPropertyMap()
  {
    return (Map)copyOnInheritThreadLocal.get();
  }
  



  public Map getCopyOfContextMap()
  {
    HashMap<String, String> hashMap = (HashMap)copyOnInheritThreadLocal.get();
    if (hashMap == null) {
      return null;
    }
    return new HashMap(hashMap);
  }
  




  public Set<String> getKeys()
  {
    HashMap<String, String> hashMap = (HashMap)copyOnInheritThreadLocal.get();
    
    if (hashMap != null) {
      return hashMap.keySet();
    }
    return null;
  }
  

  public void setContextMap(Map contextMap)
  {
    HashMap<String, String> oldMap = (HashMap)copyOnInheritThreadLocal.get();
    
    HashMap<String, String> newMap = new HashMap();
    newMap.putAll(contextMap);
    

    copyOnInheritThreadLocal.set(newMap);
    

    if (oldMap != null) {
      oldMap.clear();
      oldMap = null;
    }
  }
}
