package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsOptions;
import com.mojang.realmsclient.dto.RealmsServer.WorldType;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSliderButton;
import org.lwjgl.input.Keyboard;







public class RealmsSlotOptionsScreen
  extends RealmsScreen
{
  private static final int BUTTON_CANCEL_ID = 0;
  private static final int BUTTON_DONE_ID = 1;
  private static final int BUTTON_DIFFICULTY_ID = 2;
  private static final int BUTTON_GAMEMODE_ID = 3;
  private static final int BUTTON_PVP_ID = 4;
  private static final int BUTTON_SPAWN_ANIMALS_ID = 5;
  private static final int BUTTON_SPAWN_MONSTERS_ID = 6;
  private static final int BUTTON_SPAWN_NPCS_ID = 7;
  private static final int BUTTON_SPAWN_PROTECTION_ID = 8;
  private static final int BUTTON_COMMANDBLOCKS_ID = 9;
  private static final int BUTTON_FORCE_GAMEMODE_ID = 10;
  private static final int NAME_EDIT_BOX = 11;
  private RealmsEditBox nameEdit;
  protected final RealmsConfigureWorldScreen parent;
  private int column1_x;
  private int column_width;
  private int column2_x;
  private RealmsOptions options;
  private RealmsServer.WorldType worldType;
  private int activeSlot;
  private int difficultyIndex;
  private int gameModeIndex;
  private Boolean pvp;
  private Boolean spawnNPCs;
  private Boolean spawnAnimals;
  private Boolean spawnMonsters;
  private Integer spawnProtection;
  private Boolean commandBlocks;
  private Boolean forceGameMode;
  private RealmsButton pvpButton;
  private RealmsButton spawnAnimalsButton;
  private RealmsButton spawnMonstersButton;
  private RealmsButton spawnNPCsButton;
  private RealmsSliderButton spawnProtectionButton;
  private RealmsButton commandBlocksButton;
  private RealmsButton forceGameModeButton;
  private boolean notNormal = false;
  
  String[] difficulties;
  String[] gameModes;
  String[][] gameModeHints;
  
  public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen configureWorldScreen, RealmsOptions options, RealmsServer.WorldType worldType, int activeSlot)
  {
    parent = configureWorldScreen;
    this.options = options;
    this.worldType = worldType;
    this.activeSlot = activeSlot;
  }
  
  public void removed()
  {
    Keyboard.enableRepeatEvents(false);
  }
  
  public void tick()
  {
    nameEdit.tick();
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) { return;
    }
    switch (button.id()) {
    case 1: 
      saveSettings();
      break;
    case 0: 
      Realms.setScreen(parent);
      break;
    case 2: 
      difficultyIndex = ((difficultyIndex + 1) % difficulties.length);
      button.msg(difficultyTitle());
      
      if (worldType.equals(RealmsServer.WorldType.NORMAL)) {
        spawnMonstersButton.active(difficultyIndex != 0);
        spawnMonstersButton.msg(spawnMonstersTitle());
      }
      break;
    case 3: 
      gameModeIndex = ((gameModeIndex + 1) % gameModes.length);
      button.msg(gameModeTitle());
      break;
    case 4: 
      pvp = Boolean.valueOf(!pvp.booleanValue());
      button.msg(pvpTitle());
      break;
    case 5: 
      spawnAnimals = Boolean.valueOf(!spawnAnimals.booleanValue());
      button.msg(spawnAnimalsTitle());
      break;
    case 7: 
      spawnNPCs = Boolean.valueOf(!spawnNPCs.booleanValue());
      button.msg(spawnNPCsTitle());
      break;
    case 6: 
      spawnMonsters = Boolean.valueOf(!spawnMonsters.booleanValue());
      button.msg(spawnMonstersTitle());
      break;
    case 9: 
      commandBlocks = Boolean.valueOf(!commandBlocks.booleanValue());
      button.msg(commandBlocksTitle());
      break;
    case 10: 
      forceGameMode = Boolean.valueOf(!forceGameMode.booleanValue());
      button.msg(forceGameModeTitle());
      break;
    }
    
  }
  


  public void keyPressed(char eventCharacter, int eventKey)
  {
    nameEdit.keyPressed(eventCharacter, eventKey);
    
    switch (eventKey) {
    case 15: 
      nameEdit.setFocus(!nameEdit.isFocused());
      break;
    case 1: 
      Realms.setScreen(parent);
      break;
    case 28: 
    case 156: 
      saveSettings();
      break;
    }
    
  }
  

  public void mouseClicked(int x, int y, int buttonNum)
  {
    super.mouseClicked(x, y, buttonNum);
    
    nameEdit.mouseClicked(x, y, buttonNum);
  }
  
  public void init()
  {
    column1_x = (width() / 2 - 122);
    column_width = 122;
    column2_x = (width() / 2 + 10);
    
    createDifficultyAndGameMode();
    
    difficultyIndex = options.difficulty.intValue();
    gameModeIndex = options.gameMode.intValue();
    
    if (!worldType.equals(RealmsServer.WorldType.NORMAL)) {
      notNormal = true;
      
      pvp = Boolean.valueOf(true);
      spawnProtection = Integer.valueOf(0);
      forceGameMode = Boolean.valueOf(false);
      
      spawnAnimals = Boolean.valueOf(true);
      spawnMonsters = Boolean.valueOf(true);
      spawnNPCs = Boolean.valueOf(true);
      commandBlocks = Boolean.valueOf(true);
    } else {
      pvp = options.pvp;
      spawnProtection = options.spawnProtection;
      forceGameMode = options.forceGameMode;
      
      spawnAnimals = options.spawnAnimals;
      spawnMonsters = options.spawnMonsters;
      spawnNPCs = options.spawnNPCs;
      commandBlocks = options.commandBlocks;
    }
    
    nameEdit = newEditBox(11, column1_x + 2, RealmsConstants.row(2), column_width - 4, 20);
    nameEdit.setFocus(true);
    nameEdit.setMaxLength(10);
    nameEdit.setValue(options.getSlotName(activeSlot));
    
    buttonsAdd(newButton(3, column2_x, RealmsConstants.row(2), column_width, 20, gameModeTitle()));
    
    buttonsAdd(this.pvpButton = newButton(4, column1_x, RealmsConstants.row(4), column_width, 20, pvpTitle()));
    buttonsAdd(this.spawnAnimalsButton = newButton(5, column2_x, RealmsConstants.row(4), column_width, 20, spawnAnimalsTitle()));
    
    buttonsAdd(newButton(2, column1_x, RealmsConstants.row(6), column_width, 20, difficultyTitle()));
    buttonsAdd(this.spawnMonstersButton = newButton(6, column2_x, RealmsConstants.row(6), column_width, 20, spawnMonstersTitle()));
    
    buttonsAdd(this.spawnProtectionButton = new SettingsSlider(8, column1_x, RealmsConstants.row(8), column_width, 17, spawnProtection.intValue(), 0.0F, 16.0F));
    buttonsAdd(this.spawnNPCsButton = newButton(7, column2_x, RealmsConstants.row(8), column_width, 20, spawnNPCsTitle()));
    

    buttonsAdd(this.forceGameModeButton = newButton(10, column1_x, RealmsConstants.row(10), column_width, 20, forceGameModeTitle()));
    buttonsAdd(this.commandBlocksButton = newButton(9, column2_x, RealmsConstants.row(10), column_width, 20, commandBlocksTitle()));
    

    if (!worldType.equals(RealmsServer.WorldType.NORMAL)) {
      pvpButton.active(false);
      spawnAnimalsButton.active(false);
      spawnNPCsButton.active(false);
      spawnMonstersButton.active(false);
      spawnProtectionButton.active(false);
      commandBlocksButton.active(false);
      spawnProtectionButton.active(false);
      forceGameModeButton.active(false);
    }
    
    if (difficultyIndex == 0) {
      spawnMonstersButton.active(false);
    }
    
    buttonsAdd(newButton(1, column1_x, RealmsConstants.row(13), column_width, 20, getLocalizedString("mco.configure.world.buttons.done")));
    buttonsAdd(newButton(0, column2_x, RealmsConstants.row(13), column_width, 20, getLocalizedString("gui.cancel")));
  }
  
  private void createDifficultyAndGameMode()
  {
    difficulties = new String[] { getLocalizedString("options.difficulty.peaceful"), getLocalizedString("options.difficulty.easy"), getLocalizedString("options.difficulty.normal"), getLocalizedString("options.difficulty.hard") };
    





    gameModes = new String[] { getLocalizedString("selectWorld.gameMode.survival"), getLocalizedString("selectWorld.gameMode.creative"), getLocalizedString("selectWorld.gameMode.adventure") };
    




    gameModeHints = new String[][] { { getLocalizedString("selectWorld.gameMode.survival.line1"), getLocalizedString("selectWorld.gameMode.survival.line2") }, { getLocalizedString("selectWorld.gameMode.creative.line1"), getLocalizedString("selectWorld.gameMode.creative.line2") }, { getLocalizedString("selectWorld.gameMode.adventure.line1"), getLocalizedString("selectWorld.gameMode.adventure.line2") } };
  }
  




  private String difficultyTitle()
  {
    String difficulty = getLocalizedString("options.difficulty");
    return difficulty + ": " + difficulties[difficultyIndex];
  }
  
  private String gameModeTitle() {
    String gameMode = getLocalizedString("selectWorld.gameMode");
    return gameMode + ": " + gameModes[gameModeIndex];
  }
  
  private String pvpTitle() {
    return getLocalizedString("mco.configure.world.pvp") + ": " + (pvp.booleanValue() ? getLocalizedString("mco.configure.world.on") : getLocalizedString("mco.configure.world.off"));
  }
  
  private String spawnAnimalsTitle() {
    return getLocalizedString("mco.configure.world.spawnAnimals") + ": " + (spawnAnimals.booleanValue() ? getLocalizedString("mco.configure.world.on") : getLocalizedString("mco.configure.world.off"));
  }
  
  private String spawnMonstersTitle() {
    if (difficultyIndex == 0) {
      return getLocalizedString("mco.configure.world.spawnMonsters") + ": " + getLocalizedString("mco.configure.world.off");
    }
    return getLocalizedString("mco.configure.world.spawnMonsters") + ": " + (spawnMonsters.booleanValue() ? getLocalizedString("mco.configure.world.on") : getLocalizedString("mco.configure.world.off"));
  }
  
  private String spawnNPCsTitle()
  {
    return getLocalizedString("mco.configure.world.spawnNPCs") + ": " + (spawnNPCs.booleanValue() ? getLocalizedString("mco.configure.world.on") : getLocalizedString("mco.configure.world.off"));
  }
  
  private String commandBlocksTitle() {
    return getLocalizedString("mco.configure.world.commandBlocks") + ": " + (commandBlocks.booleanValue() ? getLocalizedString("mco.configure.world.on") : getLocalizedString("mco.configure.world.off"));
  }
  
  private String forceGameModeTitle() {
    return getLocalizedString("mco.configure.world.forceGameMode") + ": " + (forceGameMode.booleanValue() ? getLocalizedString("mco.configure.world.on") : getLocalizedString("mco.configure.world.off"));
  }
  
  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    String slotName = getLocalizedString("mco.configure.world.edit.slot.name");
    
    drawString(slotName, column1_x + fontWidth(slotName) / 2, RealmsConstants.row(0) + 5, 16777215);
    drawCenteredString(getLocalizedString("mco.configure.world.buttons.options"), width() / 2, 17, 16777215);
    
    if (notNormal) {
      drawCenteredString(getLocalizedString("mco.configure.world.edit.subscreen.adventuremap"), width() / 2, 30, 16711680);
    }
    
    nameEdit.render();
    


    super.render(xm, ym, a);
  }
  
  public void renderHints() {
    drawString(gameModeHints[gameModeIndex][0], column2_x + 2, RealmsConstants.row(0), 10526880);
    drawString(gameModeHints[gameModeIndex][1], column2_x + 2, RealmsConstants.row(0) + fontLineHeight() + 2, 10526880);
  }
  
  public void mouseReleased(int x, int y, int buttonNum)
  {
    if (!spawnProtectionButton.active()) {
      return;
    }
    
    spawnProtectionButton.released(x, y);
  }
  

  public void mouseDragged(int x, int y, int buttonNum, long delta)
  {
    if (!spawnProtectionButton.active()) {
      return;
    }
    
    if ((x < column1_x + spawnProtectionButton.getWidth()) && (x > column1_x) && (y < spawnProtectionButton.y() + 20) && (y > spawnProtectionButton.y())) {
      spawnProtectionButton.clicked(x, y);
    }
  }
  
  private class SettingsSlider extends RealmsSliderButton
  {
    public SettingsSlider(int id, int x, int y, int width, int steps, int currentValue, float minValue, float maxValue)
    {
      super(x, y, width, steps, currentValue, minValue, maxValue);
    }
    
    public String getMessage()
    {
      return RealmsScreen.getLocalizedString("mco.configure.world.spawnProtection") + ": " + (spawnProtection.intValue() == 0 ? RealmsScreen.getLocalizedString("mco.configure.world.off") : spawnProtection);
    }
    
    public void clicked(float value)
    {
      if (!spawnProtectionButton.active()) {
        return;
      }
      
      spawnProtection = Integer.valueOf((int)value);
    }
  }
  
  private String getSlotName() {
    if (nameEdit.getValue().equals(options.getDefaultSlotName(activeSlot))) {
      return "";
    }
    return nameEdit.getValue();
  }
  
  private void saveSettings()
  {
    if (worldType.equals(RealmsServer.WorldType.ADVENTUREMAP)) {
      parent.saveSlotSettings(new RealmsOptions(options.pvp, options.spawnAnimals, options.spawnMonsters, options.spawnNPCs, options.spawnProtection, options.commandBlocks, Integer.valueOf(difficultyIndex), Integer.valueOf(gameModeIndex), options.forceGameMode, getSlotName()));
    } else {
      parent.saveSlotSettings(new RealmsOptions(pvp, spawnAnimals, spawnMonsters, spawnNPCs, spawnProtection, commandBlocks, Integer.valueOf(difficultyIndex), Integer.valueOf(gameModeIndex), forceGameMode, getSlotName()));
    }
  }
}
