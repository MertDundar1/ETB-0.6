package com.mojang.realmsclient.util;

import com.google.gson.JsonObject;

public class JsonUtils
{
  public JsonUtils() {}
  
  public static String getStringOr(String key, JsonObject node, String defaultValue)
  {
    com.google.gson.JsonElement element = node.get(key);
    if (element != null) {
      return element.isJsonNull() ? defaultValue : element.getAsString();
    }
    return defaultValue;
  }
  
  public static int getIntOr(String key, JsonObject node, int defaultValue)
  {
    com.google.gson.JsonElement element = node.get(key);
    if (element != null) {
      return element.isJsonNull() ? defaultValue : element.getAsInt();
    }
    return defaultValue;
  }
  
  public static long getLongOr(String key, JsonObject node, long defaultValue)
  {
    com.google.gson.JsonElement element = node.get(key);
    if (element != null) {
      return element.isJsonNull() ? defaultValue : element.getAsLong();
    }
    return defaultValue;
  }
  
  public static boolean getBooleanOr(String key, JsonObject node, boolean defaultValue)
  {
    com.google.gson.JsonElement element = node.get(key);
    if (element != null) {
      return element.isJsonNull() ? defaultValue : element.getAsBoolean();
    }
    return defaultValue;
  }
  
  public static java.util.Date getDateOr(String key, JsonObject node)
  {
    com.google.gson.JsonElement element = node.get(key);
    if (element != null) {
      return new java.util.Date(Long.parseLong(element.getAsString()));
    }
    return new java.util.Date();
  }
}
