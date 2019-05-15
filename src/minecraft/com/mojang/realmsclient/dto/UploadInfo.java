package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UploadInfo
{
  public UploadInfo() {}
  
  private static final Logger LOGGER = ;
  private boolean worldClosed;
  
  private String token = "";
  private String uploadEndpoint = "";
  
  public static UploadInfo parse(String json) {
    UploadInfo uploadInfo = new UploadInfo();
    try {
      JsonParser parser = new JsonParser();
      com.google.gson.JsonObject jsonObject = parser.parse(json).getAsJsonObject();
      worldClosed = JsonUtils.getBooleanOr("worldClosed", jsonObject, false);
      token = JsonUtils.getStringOr("token", jsonObject, null);
      uploadEndpoint = JsonUtils.getStringOr("uploadEndpoint", jsonObject, null);
    } catch (Exception e) {
      LOGGER.error("Could not parse UploadInfo: " + e.getMessage());
    }
    return uploadInfo;
  }
  
  public String getToken() {
    return token;
  }
  
  public String getUploadEndpoint() {
    return uploadEndpoint;
  }
  
  public boolean isWorldClosed() {
    return worldClosed;
  }
  
  public void setToken(String token) {
    this.token = token;
  }
}
