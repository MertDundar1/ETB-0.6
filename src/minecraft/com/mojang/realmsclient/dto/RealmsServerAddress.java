package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerAddress extends ValueObject
{
  private static final Logger LOGGER = ;
  public String address;
  
  public RealmsServerAddress() {}
  
  public static RealmsServerAddress parse(String json) { JsonParser parser = new JsonParser();
    RealmsServerAddress serverAddress = new RealmsServerAddress();
    try {
      JsonObject object = parser.parse(json).getAsJsonObject();
      address = JsonUtils.getStringOr("address", object, null);
    } catch (Exception e) {
      LOGGER.error("Could not parse McoServerAddress: " + e.getMessage());
    }
    return serverAddress;
  }
}
