package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;

public class MinecraftProfilePropertiesResponse extends Response {
  private java.util.UUID id;
  private String name;
  private PropertyMap properties;
  
  public MinecraftProfilePropertiesResponse() {}
  
  public java.util.UUID getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public PropertyMap getProperties() {
    return properties;
  }
}
