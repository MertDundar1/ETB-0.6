package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsConstants;
import java.text.DateFormat;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class RealmsSelectFileToUploadScreen extends RealmsScreen
{
  private static final Logger LOGGER = ;
  
  private static final int CANCEL_BUTTON = 1;
  
  private static final int UPLOAD_BUTTON = 2;
  
  private final RealmsResetWorldScreen lastScreen;
  
  private final long worldId;
  private int slotId;
  private RealmsButton uploadButton;
  private final DateFormat DATE_FORMAT = new java.text.SimpleDateFormat();
  
  private List<RealmsLevelSummary> levelList = new java.util.ArrayList();
  private int selectedWorld = -1;
  private WorldSelectionList worldSelectionList;
  private String worldLang;
  private String conversionLang;
  private String[] gameModesLang = new String[4];
  
  public RealmsSelectFileToUploadScreen(long worldId, int slotId, RealmsResetWorldScreen lastScreen) {
    this.lastScreen = lastScreen;
    this.worldId = worldId;
    this.slotId = slotId;
  }
  
  private void loadLevelList() throws Exception {
    RealmsAnvilLevelStorageSource levelSource = getLevelStorageSource();
    levelList = levelSource.getLevelList();
    java.util.Collections.sort(levelList);
  }
  
  public void init()
  {
    Keyboard.enableRepeatEvents(true);
    buttonsClear();
    try
    {
      loadLevelList();
    } catch (Exception e) {
      LOGGER.error("Couldn't load level list", e);
      Realms.setScreen(new RealmsGenericErrorScreen("Unable to load worlds", e.getMessage(), lastScreen));
      return;
    }
    
    worldLang = getLocalizedString("selectWorld.world");
    conversionLang = getLocalizedString("selectWorld.conversion");
    gameModesLang[Realms.survivalId()] = getLocalizedString("gameMode.survival");
    gameModesLang[Realms.creativeId()] = getLocalizedString("gameMode.creative");
    gameModesLang[Realms.adventureId()] = getLocalizedString("gameMode.adventure");
    gameModesLang[Realms.spectatorId()] = getLocalizedString("gameMode.spectator");
    
    buttonsAdd(newButton(1, width() / 2 + 6, height() - 32, 153, 20, getLocalizedString("gui.back")));
    buttonsAdd(this.uploadButton = newButton(2, width() / 2 - 154, height() - 32, 153, 20, getLocalizedString("mco.upload.button.name")));
    

    uploadButton.active((selectedWorld >= 0) && (selectedWorld < levelList.size()));
    
    worldSelectionList = new WorldSelectionList();
  }
  
  public void removed()
  {
    Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) {
      return;
    }
    
    if (button.id() == 1) {
      Realms.setScreen(lastScreen);
    } else if (button.id() == 2) {
      upload();
    }
  }
  
  private void upload() {
    if ((selectedWorld != -1) && (!((RealmsLevelSummary)levelList.get(selectedWorld)).isHardcore())) {
      RealmsLevelSummary selectedLevel = (RealmsLevelSummary)levelList.get(selectedWorld);
      Realms.setScreen(new RealmsUploadScreen(worldId, slotId, lastScreen, selectedLevel));
    }
  }
  

  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    worldSelectionList.render(xm, ym, a);
    
    drawCenteredString(getLocalizedString("mco.upload.select.world.title"), width() / 2, 13, 16777215);
    drawCenteredString(getLocalizedString("mco.upload.select.world.subtitle"), width() / 2, RealmsConstants.row(-1), 10526880);
    
    if (levelList.size() == 0) {
      drawCenteredString(getLocalizedString("mco.upload.select.world.none"), width() / 2, height() / 2 - 20, 16777215);
    }
    
    super.render(xm, ym, a);
  }
  
  public void keyPressed(char eventCharacter, int eventKey)
  {
    if (eventKey == 1) {
      Realms.setScreen(lastScreen);
    }
  }
  
  public void mouseEvent()
  {
    super.mouseEvent();
    worldSelectionList.mouseEvent();
  }
  
  public void tick()
  {
    super.tick();
  }
  
  private class WorldSelectionList extends RealmsScrolledSelectionList
  {
    public WorldSelectionList() {
      super(height(), RealmsConstants.row(0), height() - 40, 36);
    }
    
    public int getItemCount()
    {
      return levelList.size();
    }
    
    public void selectItem(int item, boolean doubleClick, int xMouse, int yMouse)
    {
      selectedWorld = item;
      uploadButton.active((selectedWorld >= 0) && (selectedWorld < getItemCount()) && (!((RealmsLevelSummary)levelList.get(selectedWorld)).isHardcore()));
      
      if (doubleClick) {
        RealmsSelectFileToUploadScreen.this.upload();
      }
    }
    
    public boolean isSelectedItem(int item)
    {
      return item == selectedWorld;
    }
    
    public int getMaxPosition()
    {
      return levelList.size() * 36;
    }
    
    public void renderBackground()
    {
      RealmsSelectFileToUploadScreen.this.renderBackground();
    }
    
    protected void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY)
    {
      RealmsLevelSummary levelSummary = (RealmsLevelSummary)levelList.get(i);
      
      String name = levelSummary.getLevelName();
      
      if ((name == null) || (name.isEmpty())) {
        name = worldLang + " " + (i + 1);
      }
      
      String id = levelSummary.getLevelId();
      id = id + " (" + DATE_FORMAT.format(new java.util.Date(levelSummary.getLastPlayed()));
      id = id + ")";
      String info = "";
      
      if (levelSummary.isRequiresConversion()) {
        info = conversionLang + " " + info;
      } else {
        info = gameModesLang[levelSummary.getGameMode()];
        
        if (levelSummary.isHardcore()) {
          info = ChatFormatting.DARK_RED + RealmsScreen.getLocalizedString("mco.upload.hardcore") + ChatFormatting.RESET;
        }
        
        if (levelSummary.hasCheats()) {
          info = info + ", " + RealmsScreen.getLocalizedString("selectWorld.cheats");
        }
      }
      
      drawString(name, x + 2, y + 1, 16777215);
      drawString(id, x + 2, y + 12, 8421504);
      drawString(info, x + 2, y + 12 + 10, 8421504);
    }
  }
}
