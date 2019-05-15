package com.mojang.realmsclient.dto;

import com.google.common.collect.ComparisonChain;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.realms.RealmsServerPing;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.Logger;

public class RealmsServer extends ValueObject
{
  private static final Logger LOGGER = ;
  
  public long id;
  
  public String remoteSubscriptionId;
  
  public String name;
  
  public String motd;
  
  public State state;
  
  public String owner;
  
  public String ownerUUID;
  
  public java.util.List<PlayerInfo> players;
  public Map<Integer, RealmsOptions> slots;
  public String ip;
  public boolean expired;
  
  public RealmsServer()
  {
    status = "";
    






    serverPing = new RealmsServerPing();
  }
  
  public String getDescription() { return motd; }
  
  public String getName()
  {
    return name;
  }
  
  public String getMinigameName() {
    return minigameName;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setDescription(String motd) {
    this.motd = motd;
  }
  
  public void latestStatFrom(RealmsServer oldServer) {
    status = status;
    protocol = protocol;
    serverPing.nrOfPlayers = serverPing.nrOfPlayers;
    serverPing.lastPingSnapshot = serverPing.lastPingSnapshot;
    serverPing.playerList = serverPing.playerList;
  }
  
  public static RealmsServer parse(JsonObject node) {
    RealmsServer server = new RealmsServer();
    try {
      id = JsonUtils.getLongOr("id", node, -1L);
      remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", node, null);
      name = JsonUtils.getStringOr("name", node, null);
      motd = JsonUtils.getStringOr("motd", node, null);
      state = getState(JsonUtils.getStringOr("state", node, State.CLOSED.name()));
      owner = JsonUtils.getStringOr("owner", node, null);
      if ((node.get("players") != null) && (node.get("players").isJsonArray())) {
        players = parseInvited(node.get("players").getAsJsonArray());
        sortInvited(server);
      } else {
        players = new ArrayList();
      }
      daysLeft = JsonUtils.getIntOr("daysLeft", node, 0);
      ip = JsonUtils.getStringOr("ip", node, null);
      expired = JsonUtils.getBooleanOr("expired", node, false);
      worldType = getWorldType(JsonUtils.getStringOr("worldType", node, WorldType.NORMAL.name()));
      ownerUUID = JsonUtils.getStringOr("ownerUUID", node, "");
      
      if ((node.get("slots") != null) && (node.get("slots").isJsonArray())) {
        slots = parseSlots(node.get("slots").getAsJsonArray());
      } else {
        slots = getEmptySlots();
      }
      
      minigameName = JsonUtils.getStringOr("minigameName", node, null);
      activeSlot = JsonUtils.getIntOr("activeSlot", node, -1);
      minigameId = JsonUtils.getIntOr("minigameId", node, -1);
      minigameImage = JsonUtils.getStringOr("minigameImage", node, null);
      resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", node, null);
      resourcePackHash = JsonUtils.getStringOr("resourcePackHash", node, null);
    } catch (Exception e) {
      LOGGER.error("Could not parse McoServer: " + e.getMessage());
    }
    return server;
  }
  
  private static void sortInvited(RealmsServer server) {
    java.util.Collections.sort(players, new java.util.Comparator()
    {
      public int compare(PlayerInfo o1, PlayerInfo o2) {
        return ComparisonChain.start().compare(Boolean.valueOf(o2.getAccepted()), Boolean.valueOf(o1.getAccepted())).compare(o1.getName().toLowerCase(), o2.getName().toLowerCase()).result();
      }
    });
  }
  
  private static java.util.List<PlayerInfo> parseInvited(JsonArray jsonArray) {
    ArrayList<PlayerInfo> invited = new ArrayList();
    for (JsonElement aJsonArray : jsonArray) {
      try {
        JsonObject node = aJsonArray.getAsJsonObject();
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setName(JsonUtils.getStringOr("name", node, null));
        playerInfo.setUuid(JsonUtils.getStringOr("uuid", node, null));
        playerInfo.setOperator(JsonUtils.getBooleanOr("operator", node, false));
        playerInfo.setAccepted(JsonUtils.getBooleanOr("accepted", node, false));
        invited.add(playerInfo);
      }
      catch (Exception e) {}
    }
    return invited;
  }
  
  private static Map<Integer, RealmsOptions> parseSlots(JsonArray jsonArray) {
    Map<Integer, RealmsOptions> slots = new HashMap();
    for (JsonElement aJsonArray : jsonArray) {
      try
      {
        JsonObject node = aJsonArray.getAsJsonObject();
        
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(node.get("options").getAsString());
        RealmsOptions options;
        RealmsOptions options; if (element == null) {
          options = RealmsOptions.getDefaults();
        } else {
          options = RealmsOptions.parse(element.getAsJsonObject());
        }
        
        int slot = JsonUtils.getIntOr("slotId", node, -1);
        
        slots.put(Integer.valueOf(slot), options);
      }
      catch (Exception e) {}
    }
    
    for (int i = 1; i <= 3; i++) {
      if (!slots.containsKey(Integer.valueOf(i))) {
        slots.put(Integer.valueOf(i), RealmsOptions.getEmptyDefaults());
      }
    }
    
    return slots;
  }
  
  private static Map<Integer, RealmsOptions> getEmptySlots() {
    HashMap slots = new HashMap();
    slots.put(Integer.valueOf(1), RealmsOptions.getEmptyDefaults());
    slots.put(Integer.valueOf(2), RealmsOptions.getEmptyDefaults());
    slots.put(Integer.valueOf(3), RealmsOptions.getEmptyDefaults());
    
    return slots;
  }
  
  public static RealmsServer parse(String json)
  {
    RealmsServer server = new RealmsServer();
    try {
      JsonParser parser = new JsonParser();
      JsonObject object = parser.parse(json).getAsJsonObject();
      server = parse(object);
    } catch (Exception e) {
      LOGGER.error("Could not parse McoServer: " + e.getMessage());
    }
    return server;
  }
  
  private static State getState(String state) {
    try {
      return State.valueOf(state);
    } catch (Exception e) {}
    return State.CLOSED;
  }
  
  private static WorldType getWorldType(String state)
  {
    try {
      return WorldType.valueOf(state);
    } catch (Exception e) {}
    return WorldType.NORMAL;
  }
  




  public boolean shouldPing(long now)
  {
    return now - serverPing.lastPingSnapshot >= 6000L;
  }
  
  public int hashCode()
  {
    return new HashCodeBuilder(17, 37).append(id).append(name).append(motd).append(state).append(owner).append(expired).toHashCode();
  }
  

  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    RealmsServer rhs = (RealmsServer)obj;
    
    return new EqualsBuilder().append(id, id).append(name, name).append(motd, motd).append(state, state).append(owner, owner).append(expired, expired).append(worldType, worldType).isEquals();
  }
  






  public RealmsServer clone()
  {
    RealmsServer server = new RealmsServer();
    id = id;
    remoteSubscriptionId = remoteSubscriptionId;
    name = name;
    motd = motd;
    state = state;
    owner = owner;
    players = players;
    slots = cloneSlots(slots);
    ip = ip;
    expired = expired;
    daysLeft = daysLeft;
    protocol = protocol;
    status = status;
    serverPing = new RealmsServerPing();
    serverPing.nrOfPlayers = serverPing.nrOfPlayers;
    serverPing.lastPingSnapshot = serverPing.lastPingSnapshot;
    serverPing.playerList = serverPing.playerList;
    worldType = worldType;
    ownerUUID = ownerUUID;
    minigameName = minigameName;
    activeSlot = activeSlot;
    minigameId = minigameId;
    minigameImage = minigameImage;
    resourcePackUrl = resourcePackUrl;
    resourcePackHash = resourcePackHash;
    return server;
  }
  
  public Map<Integer, RealmsOptions> cloneSlots(Map<Integer, RealmsOptions> slots) {
    Map<Integer, RealmsOptions> newSlots = new HashMap();
    
    for (Map.Entry<Integer, RealmsOptions> entry : slots.entrySet()) {
      newSlots.put(entry.getKey(), ((RealmsOptions)entry.getValue()).clone());
    }
    
    return newSlots;
  }
  

  public static class McoServerComparator
    implements java.util.Comparator<RealmsServer>
  {
    private final String refOwner;
    
    public McoServerComparator(String owner)
    {
      refOwner = owner;
    }
    


    public int compare(RealmsServer server1, RealmsServer server2) { return ComparisonChain.start().compareTrueFirst(state.equals(RealmsServer.State.UNINITIALIZED), state.equals(RealmsServer.State.UNINITIALIZED)).compareFalseFirst(expired, expired).compareTrueFirst(owner.equals(refOwner), owner.equals(refOwner)).compareTrueFirst(state.equals(RealmsServer.State.OPEN), state.equals(RealmsServer.State.OPEN)).compare(id, id).result(); }
  }
  
  public int daysLeft;
  public WorldType worldType;
  public int activeSlot;
  public String minigameName;
  public int minigameId;
  public int protocol;
  public String status;
  public String minigameImage;
  public String resourcePackUrl;
  public String resourcePackHash;
  public RealmsServerPing serverPing;
  public static enum State { CLOSED, 
    OPEN, 
    ADMIN_LOCK, 
    UNINITIALIZED;
    
    private State() {}
  }
  
  public static enum WorldType
  {
    NORMAL, 
    MINIGAME, 
    ADVENTUREMAP;
    
    private WorldType() {}
  }
}
