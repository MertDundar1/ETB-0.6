package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PendingInvitesList extends ValueObject
{
  public PendingInvitesList() {}
  
  private static final Logger LOGGER = ;
  
  public List<PendingInvite> pendingInvites = com.google.common.collect.Lists.newArrayList();
  
  public static PendingInvitesList parse(String json) {
    PendingInvitesList list = new PendingInvitesList();
    try {
      JsonParser jsonParser = new JsonParser();
      JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
      if (jsonObject.get("invites").isJsonArray()) {
        Iterator<JsonElement> it = jsonObject.get("invites").getAsJsonArray().iterator();
        while (it.hasNext()) {
          pendingInvites.add(PendingInvite.parse(((JsonElement)it.next()).getAsJsonObject()));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Could not parse PendingInvitesList: " + e.getMessage());
    }
    return list;
  }
}
