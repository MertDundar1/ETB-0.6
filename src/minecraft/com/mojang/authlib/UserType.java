package com.mojang.authlib;

import java.util.Map;

public enum UserType
{
  LEGACY("legacy"), 
  MOJANG("mojang");
  
  private static final Map<String, UserType> BY_NAME;
  private final String name;
  
  private UserType(String name) {
    this.name = name;
  }
  
  public static UserType byName(String name) {
    return (UserType)BY_NAME.get(name.toLowerCase());
  }
  
  public String getName() {
    return name;
  }
  
  static
  {
    BY_NAME = new java.util.HashMap();
    














    for (UserType type : values()) {
      BY_NAME.put(name, type);
    }
  }
}
