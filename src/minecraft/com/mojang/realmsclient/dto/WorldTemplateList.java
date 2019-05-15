package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplateList extends ValueObject
{
  private static final Logger LOGGER = ;
  public List<WorldTemplate> templates;
  
  public WorldTemplateList() {}
  
  public static WorldTemplateList parse(String json) { WorldTemplateList list = new WorldTemplateList();
    templates = new ArrayList();
    try {
      JsonParser parser = new JsonParser();
      JsonObject object = parser.parse(json).getAsJsonObject();
      if (object.get("templates").isJsonArray()) {
        Iterator<JsonElement> it = object.get("templates").getAsJsonArray().iterator();
        while (it.hasNext()) {
          templates.add(WorldTemplate.parse(((JsonElement)it.next()).getAsJsonObject()));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Could not parse WorldTemplateList: " + e.getMessage());
    }
    return list;
  }
}
