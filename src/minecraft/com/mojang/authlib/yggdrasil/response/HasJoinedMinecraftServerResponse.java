package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;

public class HasJoinedMinecraftServerResponse extends Response {
  private java.util.UUID id;
  private PropertyMap properties;
  
  public HasJoinedMinecraftServerResponse() {}
  
  public java.util.UUID getId() {
    return id;
  }
  
  public PropertyMap getProperties() {
    return properties;
  }
}
