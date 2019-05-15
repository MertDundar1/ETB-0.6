package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class PendingInvite
  extends ValueObject
{
  private static final Logger LOGGER = ;
  public String invitationId;
  public String worldName;
  public String worldOwnerName;
  public String worldOwnerUuid;
  public Date date;
  
  public PendingInvite() {}
  
  public static PendingInvite parse(JsonObject json) { PendingInvite invite = new PendingInvite();
    try {
      invitationId = JsonUtils.getStringOr("invitationId", json, "");
      worldName = JsonUtils.getStringOr("worldName", json, "");
      worldOwnerName = JsonUtils.getStringOr("worldOwnerName", json, "");
      worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", json, "");
      date = JsonUtils.getDateOr("date", json);
    } catch (Exception e) {
      LOGGER.error("Could not parse PendingInvite: " + e.getMessage());
    }
    return invite;
  }
}
