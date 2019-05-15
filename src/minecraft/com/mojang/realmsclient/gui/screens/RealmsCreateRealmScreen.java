package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.util.RealmsTasks.WorldCreationTask;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsScreen;
import org.lwjgl.input.Keyboard;




public class RealmsCreateRealmScreen
  extends RealmsScreen
{
  private final RealmsServer server;
  private RealmsMainScreen lastScreen;
  private RealmsEditBox nameBox;
  private RealmsEditBox descriptionBox;
  private static int CREATE_BUTTON = 0;
  private static int CANCEL_BUTTON = 1;
  private static int NAME_BOX_ID = 3;
  private static int DESCRIPTION_BOX_ID = 4;
  private RealmsButton createButton;
  
  public RealmsCreateRealmScreen(RealmsServer server, RealmsMainScreen lastScreen)
  {
    this.server = server;
    this.lastScreen = lastScreen;
  }
  
  public void tick()
  {
    nameBox.tick();
    descriptionBox.tick();
  }
  
  public void init()
  {
    Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    buttonsAdd(this.createButton = newButton(CREATE_BUTTON, width() / 2 - 100, height() / 4 + 120 + 17, 97, 20, getLocalizedString("mco.create.world")));
    buttonsAdd(newButton(CANCEL_BUTTON, width() / 2 + 5, height() / 4 + 120 + 17, 95, 20, getLocalizedString("gui.cancel")));
    
    createButton.active(false);
    
    nameBox = newEditBox(NAME_BOX_ID, width() / 2 - 100, 65, 200, 20);
    nameBox.setFocus(true);
    
    descriptionBox = newEditBox(DESCRIPTION_BOX_ID, width() / 2 - 100, 115, 200, 20);
  }
  
  public void removed()
  {
    Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) { return;
    }
    if (button.id() == CANCEL_BUTTON) {
      Realms.setScreen(lastScreen);
    } else if (button.id() == CREATE_BUTTON) {
      createWorld();
    }
  }
  
  public void keyPressed(char ch, int eventKey)
  {
    nameBox.keyPressed(ch, eventKey);
    descriptionBox.keyPressed(ch, eventKey);
    
    createButton.active(valid());
    
    switch (eventKey) {
    case 15: 
      nameBox.setFocus(!nameBox.isFocused());
      descriptionBox.setFocus(!descriptionBox.isFocused());
      break;
    case 28: 
    case 156: 
      buttonClicked(createButton);
      break;
    case 1: 
      Realms.setScreen(lastScreen);
    }
  }
  
  private void createWorld()
  {
    if (valid()) {
      RealmsResetWorldScreen resetWorldScreen = new RealmsResetWorldScreen(lastScreen, server, lastScreen.newScreen(), getLocalizedString("mco.selectServer.create"), getLocalizedString("mco.create.world.subtitle"), 10526880, getLocalizedString("mco.create.world.skip"));
      resetWorldScreen.setResetTitle(getLocalizedString("mco.create.world.reset.title"));
      RealmsTasks.WorldCreationTask worldCreationTask = new RealmsTasks.WorldCreationTask(server.id, nameBox.getValue(), descriptionBox.getValue(), resetWorldScreen);
      RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(lastScreen, worldCreationTask);
      longRunningMcoTaskScreen.start();
      Realms.setScreen(longRunningMcoTaskScreen);
    }
  }
  
  private boolean valid() {
    return (nameBox.getValue() != null) && (!nameBox.getValue().trim().equals(""));
  }
  
  public void mouseClicked(int x, int y, int buttonNum)
  {
    nameBox.mouseClicked(x, y, buttonNum);
    descriptionBox.mouseClicked(x, y, buttonNum);
  }
  
  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    drawCenteredString(getLocalizedString("mco.selectServer.create"), width() / 2, 11, 16777215);
    drawString(getLocalizedString("mco.configure.world.name"), width() / 2 - 100, 52, 10526880);
    drawString(getLocalizedString("mco.configure.world.description"), width() / 2 - 100, 102, 10526880);
    
    nameBox.render();
    descriptionBox.render();
    
    super.render(xm, ym, a);
  }
}
