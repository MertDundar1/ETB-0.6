package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BackupList
{
  private static final Logger LOGGER = ;
  public List<Backup> backups;
  
  public BackupList() {}
  
  public static BackupList parse(String json) { JsonParser jsonParser = new JsonParser();
    
    BackupList backupList = new BackupList();
    backups = new ArrayList();
    try {
      JsonElement node = jsonParser.parse(json).getAsJsonObject().get("backups");
      if (node.isJsonArray()) {
        Iterator<JsonElement> iterator = node.getAsJsonArray().iterator();
        while (iterator.hasNext()) {
          backups.add(Backup.parse((JsonElement)iterator.next()));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Could not parse BackupList: " + e.getMessage());
    }
    return backupList;
  }
}
