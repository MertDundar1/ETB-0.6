package com.mojang.realmsclient.dto;

import com.mojang.realmsclient.util.JsonUtils;

public class ServerActivity
{
  public String profileUuid;
  public long joinTime;
  public long leaveTime;
  
  public ServerActivity() {}
  
  public static ServerActivity parse(com.google.gson.JsonObject element)
  {
    ServerActivity sa = new ServerActivity();
    try {
      profileUuid = JsonUtils.getStringOr("profileUuid", element, null);
      joinTime = JsonUtils.getLongOr("joinTime", element, Long.MIN_VALUE);
      leaveTime = JsonUtils.getLongOr("leaveTime", element, Long.MIN_VALUE);
    }
    catch (Exception e) {}
    
    return sa;
  }
}
