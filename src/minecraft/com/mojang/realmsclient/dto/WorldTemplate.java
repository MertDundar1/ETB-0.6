package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplate
  extends ValueObject
{
  private static final Logger LOGGER = ;
  
  public String id;
  public String name;
  public String version;
  public String author;
  public String link;
  public boolean minigame = false;
  public String image;
  
  public WorldTemplate() {}
  
  public static WorldTemplate parse(JsonObject node) {
    WorldTemplate template = new WorldTemplate();
    try {
      id = JsonUtils.getStringOr("id", node, "");
      name = JsonUtils.getStringOr("name", node, "");
      version = JsonUtils.getStringOr("version", node, "");
      author = JsonUtils.getStringOr("author", node, "");
      link = JsonUtils.getStringOr("link", node, "");
      image = JsonUtils.getStringOr("image", node, null);
      trailer = JsonUtils.getStringOr("trailer", node, "");
      recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", node, "");
    } catch (Exception e) {
      LOGGER.error("Could not parse WorldTemplate: " + e.getMessage());
    }
    return template; }
  
  public String trailer;
  public String recommendedPlayers;
  public void setMinigame(boolean minigame) { this.minigame = minigame; }
}
