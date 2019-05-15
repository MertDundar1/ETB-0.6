package com.mojang.authlib.minecraft;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MinecraftProfileTexture
{
  private final String url;
  private final java.util.Map<String, String> metadata;
  
  public static enum Type
  {
    SKIN, 
    CAPE;
    

    private Type() {}
  }
  
  public MinecraftProfileTexture(String url, java.util.Map<String, String> metadata)
  {
    this.url = url;
    this.metadata = metadata;
  }
  
  public String getUrl() {
    return url;
  }
  
  @javax.annotation.Nullable
  public String getMetadata(String key) {
    if (metadata == null) {
      return null;
    }
    return (String)metadata.get(key);
  }
  
  public String getHash() {
    return org.apache.commons.io.FilenameUtils.getBaseName(url);
  }
  
  public String toString()
  {
    return new ToStringBuilder(this).append("url", url).append("hash", getHash()).toString();
  }
}
