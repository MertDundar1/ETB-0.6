package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsState
{
  private static final Logger LOGGER = ;
  private String statusMessage;
  private String buyLink;
  
  public RealmsState() {}
  
  public static RealmsState parse(String json) { RealmsState realmsState = new RealmsState();
    try {
      JsonParser parser = new JsonParser();
      JsonObject jsonObject = parser.parse(json).getAsJsonObject();
      statusMessage = JsonUtils.getStringOr("statusMessage", jsonObject, null);
      buyLink = JsonUtils.getStringOr("buyLink", jsonObject, null);
    } catch (Exception e) {
      LOGGER.error("Could not parse RealmsState: " + e.getMessage());
    }
    return realmsState;
  }
  
  public String getStatusMessage() {
    return statusMessage;
  }
  
  public String getBuyLink() {
    return buyLink;
  }
}
