package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.Backup;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;

public class RealmsBackupInfoScreen extends RealmsScreen
{
  private final RealmsScreen lastScreen;
  private final int BUTTON_BACK_ID = 0;
  
  private final Backup backup;
  private List<String> keys = new java.util.ArrayList();
  
  private BackupInfoList backupInfoList;
  String[] difficulties = { getLocalizedString("options.difficulty.peaceful"), getLocalizedString("options.difficulty.easy"), getLocalizedString("options.difficulty.normal"), getLocalizedString("options.difficulty.hard") };
  





  String[] gameModes = { getLocalizedString("selectWorld.gameMode.survival"), getLocalizedString("selectWorld.gameMode.creative"), getLocalizedString("selectWorld.gameMode.adventure") };
  




  public RealmsBackupInfoScreen(RealmsScreen lastScreen, Backup backup)
  {
    this.lastScreen = lastScreen;
    this.backup = backup;
    
    if (changeList != null) {
      for (Map.Entry<String, String> entry : changeList.entrySet()) {
        keys.add(entry.getKey());
      }
    }
  }
  
  public void mouseEvent()
  {
    super.mouseEvent();
    backupInfoList.mouseEvent();
  }
  

  public void tick() {}
  

  public void init()
  {
    org.lwjgl.input.Keyboard.enableRepeatEvents(true);
    
    buttonsAdd(newButton(0, width() / 2 - 100, height() / 4 + 120 + 24, getLocalizedString("gui.back")));
    
    backupInfoList = new BackupInfoList();
  }
  
  public void removed()
  {
    org.lwjgl.input.Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) {
      return;
    }
    if (button.id() == 0) {
      Realms.setScreen(lastScreen);
    }
  }
  
  public void keyPressed(char ch, int eventKey)
  {
    if (eventKey == 1) {
      Realms.setScreen(lastScreen);
    }
  }
  
  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    drawCenteredString("Changes from last backup", width() / 2, 10, 16777215);
    
    backupInfoList.render(xm, ym, a);
    
    super.render(xm, ym, a);
  }
  
  private String checkForSpecificMetadata(String key, String value) {
    String k = key.toLowerCase();
    if ((k.contains("game")) && (k.contains("mode")))
      return gameModeMetadata(value);
    if ((k.contains("game")) && (k.contains("difficulty"))) {
      return gameDifficultyMetadata(value);
    }
    return value;
  }
  
  private String gameDifficultyMetadata(String value) {
    try {
      return difficulties[Integer.parseInt(value)];
    } catch (Exception e) {}
    return "UNKNOWN";
  }
  
  private String gameModeMetadata(String value)
  {
    try {
      return gameModes[Integer.parseInt(value)];
    } catch (Exception e) {}
    return "UNKNOWN";
  }
  
  private class BackupInfoList extends RealmsSimpleScrolledSelectionList
  {
    public BackupInfoList() {
      super(height(), 32, height() - 64, 36);
    }
    
    public int getItemCount()
    {
      return backup.changeList.size();
    }
    

    public void selectItem(int item, boolean doubleClick, int xMouse, int yMouse) {}
    

    public boolean isSelectedItem(int item)
    {
      return false;
    }
    
    public int getMaxPosition()
    {
      return getItemCount() * 36;
    }
    

    public void renderBackground() {}
    

    protected void renderItem(int i, int x, int y, int h, net.minecraft.realms.Tezzelator t, int mouseX, int mouseY)
    {
      String key = (String)keys.get(i);
      drawString(key, width() / 2 - 40, y, 10526880);
      String metadataValue = (String)backup.changeList.get(key);
      drawString(RealmsBackupInfoScreen.this.checkForSpecificMetadata(key, metadataValue), width() / 2 - 40, y + 12, 16777215);
    }
  }
}
