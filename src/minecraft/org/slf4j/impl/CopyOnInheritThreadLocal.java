package org.slf4j.impl;

import java.util.HashMap;









public class CopyOnInheritThreadLocal
  extends InheritableThreadLocal<HashMap<String, String>>
{
  public CopyOnInheritThreadLocal() {}
  
  protected HashMap<String, String> childValue(HashMap<String, String> parentValue)
  {
    if (parentValue == null) {
      return null;
    }
    HashMap<String, String> hm = new HashMap(parentValue);
    return hm;
  }
}
