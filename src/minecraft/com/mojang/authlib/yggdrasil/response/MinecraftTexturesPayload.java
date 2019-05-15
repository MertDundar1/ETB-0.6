package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

public class MinecraftTexturesPayload
{
  private long timestamp;
  private java.util.UUID profileId;
  private String profileName;
  private boolean isPublic;
  private java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type, MinecraftProfileTexture> textures;
  
  public MinecraftTexturesPayload() {}
  
  public long getTimestamp() {
    return timestamp;
  }
  
  public java.util.UUID getProfileId() {
    return profileId;
  }
  
  public String getProfileName() {
    return profileName;
  }
  
  public boolean isPublic() {
    return isPublic;
  }
  
  public java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures() {
    return textures;
  }
}
