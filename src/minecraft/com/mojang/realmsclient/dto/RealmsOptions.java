package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.realms.RealmsScreen;


public class RealmsOptions
{
  public Boolean pvp;
  public Boolean spawnAnimals;
  public Boolean spawnMonsters;
  public Boolean spawnNPCs;
  public Integer spawnProtection;
  public Boolean commandBlocks;
  public Boolean forceGameMode;
  public Integer difficulty;
  public Integer gameMode;
  public String slotName;
  public long templateId;
  public String templateImage;
  public boolean empty = false;
  
  private static boolean forceGameModeDefault = false;
  private static boolean pvpDefault = true;
  private static boolean spawnAnimalsDefault = true;
  private static boolean spawnMonstersDefault = true;
  private static boolean spawnNPCsDefault = true;
  private static int spawnProtectionDefault = 0;
  private static boolean commandBlocksDefault = false;
  private static int difficultyDefault = 2;
  private static int gameModeDefault = 0;
  private static String slotNameDefault = null;
  private static long templateIdDefault = -1L;
  private static String templateImageDefault = null;
  
  public RealmsOptions(Boolean pvp, Boolean spawnAnimals, Boolean spawnMonsters, Boolean spawnNPCs, Integer spawnProtection, Boolean commandBlocks, Integer difficulty, Integer gameMode, Boolean forceGameMode, String slotName) {
    this.pvp = pvp;
    this.spawnAnimals = spawnAnimals;
    this.spawnMonsters = spawnMonsters;
    this.spawnNPCs = spawnNPCs;
    this.spawnProtection = spawnProtection;
    this.commandBlocks = commandBlocks;
    this.difficulty = difficulty;
    this.gameMode = gameMode;
    this.forceGameMode = forceGameMode;
    this.slotName = slotName;
  }
  
  public static RealmsOptions getDefaults() {
    return new RealmsOptions(Boolean.valueOf(pvpDefault), Boolean.valueOf(spawnAnimalsDefault), Boolean.valueOf(spawnMonstersDefault), Boolean.valueOf(spawnNPCsDefault), Integer.valueOf(spawnProtectionDefault), Boolean.valueOf(commandBlocksDefault), Integer.valueOf(difficultyDefault), Integer.valueOf(gameModeDefault), Boolean.valueOf(forceGameModeDefault), slotNameDefault);
  }
  
  public static RealmsOptions getEmptyDefaults() {
    RealmsOptions options = new RealmsOptions(Boolean.valueOf(pvpDefault), Boolean.valueOf(spawnAnimalsDefault), Boolean.valueOf(spawnMonstersDefault), Boolean.valueOf(spawnNPCsDefault), Integer.valueOf(spawnProtectionDefault), Boolean.valueOf(commandBlocksDefault), Integer.valueOf(difficultyDefault), Integer.valueOf(gameModeDefault), Boolean.valueOf(forceGameModeDefault), slotNameDefault);
    options.setEmpty(true);
    return options;
  }
  
  public void setEmpty(boolean empty) {
    this.empty = empty;
  }
  
  public static RealmsOptions parse(JsonObject jsonObject) {
    RealmsOptions newOptions = new RealmsOptions(Boolean.valueOf(JsonUtils.getBooleanOr("pvp", jsonObject, pvpDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("spawnAnimals", jsonObject, spawnAnimalsDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("spawnMonsters", jsonObject, spawnMonstersDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("spawnNPCs", jsonObject, spawnNPCsDefault)), Integer.valueOf(JsonUtils.getIntOr("spawnProtection", jsonObject, spawnProtectionDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("commandBlocks", jsonObject, commandBlocksDefault)), Integer.valueOf(JsonUtils.getIntOr("difficulty", jsonObject, difficultyDefault)), Integer.valueOf(JsonUtils.getIntOr("gameMode", jsonObject, gameModeDefault)), Boolean.valueOf(JsonUtils.getBooleanOr("forceGameMode", jsonObject, forceGameModeDefault)), JsonUtils.getStringOr("slotName", jsonObject, slotNameDefault));
    









    templateId = JsonUtils.getLongOr("worldTemplateId", jsonObject, templateIdDefault);
    templateImage = JsonUtils.getStringOr("worldTemplateImage", jsonObject, templateImageDefault);
    return newOptions;
  }
  
  public String getSlotName(int i)
  {
    if ((slotName == null) || (slotName.equals(""))) {
      if (empty) {
        return RealmsScreen.getLocalizedString("mco.configure.world.slot.empty");
      }
      
      return RealmsScreen.getLocalizedString("mco.configure.world.slot", new Object[] { Integer.valueOf(i) });
    }
    return slotName;
  }
  
  public String getDefaultSlotName(int i)
  {
    return RealmsScreen.getLocalizedString("mco.configure.world.slot", new Object[] { Integer.valueOf(i) });
  }
  
  public String toJson() {
    JsonObject jsonObject = new JsonObject();
    
    if (pvp.booleanValue() != pvpDefault) {
      jsonObject.addProperty("pvp", pvp);
    }
    
    if (spawnAnimals.booleanValue() != spawnAnimalsDefault) {
      jsonObject.addProperty("spawnAnimals", spawnAnimals);
    }
    
    if (spawnMonsters.booleanValue() != spawnMonstersDefault) {
      jsonObject.addProperty("spawnMonsters", spawnMonsters);
    }
    
    if (spawnNPCs.booleanValue() != spawnNPCsDefault) {
      jsonObject.addProperty("spawnNPCs", spawnNPCs);
    }
    
    if (spawnProtection.intValue() != spawnProtectionDefault) {
      jsonObject.addProperty("spawnProtection", spawnProtection);
    }
    
    if (commandBlocks.booleanValue() != commandBlocksDefault) {
      jsonObject.addProperty("commandBlocks", commandBlocks);
    }
    
    if (difficulty.intValue() != difficultyDefault) {
      jsonObject.addProperty("difficulty", difficulty);
    }
    
    if (gameMode.intValue() != gameModeDefault) {
      jsonObject.addProperty("gameMode", gameMode);
    }
    
    if (forceGameMode.booleanValue() != forceGameModeDefault) {
      jsonObject.addProperty("forceGameMode", forceGameMode);
    }
    
    if ((!slotName.equals(slotNameDefault)) && (!slotName.equals(""))) {
      jsonObject.addProperty("slotName", slotName);
    }
    
    return jsonObject.toString();
  }
  
  public RealmsOptions clone() {
    return new RealmsOptions(pvp, spawnAnimals, spawnMonsters, spawnNPCs, spawnProtection, commandBlocks, difficulty, gameMode, forceGameMode, slotName);
  }
}
