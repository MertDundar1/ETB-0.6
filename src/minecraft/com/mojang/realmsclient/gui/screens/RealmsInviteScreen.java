package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class RealmsInviteScreen extends RealmsScreen
{
  private static final Logger LOGGER = ;
  
  private RealmsEditBox profileName;
  
  private RealmsServer serverData;
  
  private final RealmsConfigureWorldScreen configureScreen;
  private final RealmsScreen lastScreen;
  private final int BUTTON_INVITE_ID = 0;
  private final int BUTTON_CANCEL_ID = 1;
  
  private RealmsButton inviteButton;
  
  private final int PROFILENAME_EDIT_BOX = 2;
  
  private String errorMsg;
  private boolean showError;
  
  public RealmsInviteScreen(RealmsConfigureWorldScreen configureScreen, RealmsScreen lastScreen, RealmsServer serverData)
  {
    this.configureScreen = configureScreen;
    this.lastScreen = lastScreen;
    this.serverData = serverData;
  }
  
  public void tick()
  {
    profileName.tick();
  }
  
  public void init()
  {
    Keyboard.enableRepeatEvents(true);
    buttonsClear();
    buttonsAdd(this.inviteButton = newButton(0, width() / 2 - 100, RealmsConstants.row(10), getLocalizedString("mco.configure.world.buttons.invite")));
    buttonsAdd(newButton(1, width() / 2 - 100, RealmsConstants.row(12), getLocalizedString("gui.cancel")));
    
    profileName = newEditBox(2, width() / 2 - 100, RealmsConstants.row(2), 200, 20);
    profileName.setFocus(true);
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
    
    switch (button.id()) {
    case 1: 
      Realms.setScreen(lastScreen);
      break;
    case 0: 
      RealmsClient client = RealmsClient.createRealmsClient();
      
      if ((profileName.getValue() == null) || (profileName.getValue().isEmpty())) {
        return;
      }
      try
      {
        RealmsServer realmsServer = client.invite(serverData.id, profileName.getValue());
        
        if (realmsServer != null) {
          serverData.players = players;
          Realms.setScreen(new RealmsPlayerScreen(configureScreen, serverData));
        } else {
          showError(getLocalizedString("mco.configure.world.players.error"));
        }
      } catch (Exception e) {
        LOGGER.error("Couldn't invite user");
        showError(getLocalizedString("mco.configure.world.players.error"));
      }
    }
    
  }
  

  private void showError(String errorMsg)
  {
    showError = true;
    this.errorMsg = errorMsg;
  }
  
  public void keyPressed(char ch, int eventKey)
  {
    profileName.keyPressed(ch, eventKey);
    
    if (eventKey == 15) {
      if (profileName.isFocused()) {
        profileName.setFocus(false);
      } else {
        profileName.setFocus(true);
      }
    }
    
    if ((eventKey == 28) || (eventKey == 156)) {
      buttonClicked(inviteButton);
    }
    
    if (eventKey == 1) {
      Realms.setScreen(lastScreen);
    }
  }
  

  public void mouseClicked(int x, int y, int buttonNum)
  {
    super.mouseClicked(x, y, buttonNum);
    
    profileName.mouseClicked(x, y, buttonNum);
  }
  
  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    drawString(getLocalizedString("mco.configure.world.invite.profile.name"), width() / 2 - 100, RealmsConstants.row(1), 10526880);
    
    if (showError) {
      drawCenteredString(errorMsg, width() / 2, RealmsConstants.row(5), 16711680);
    }
    
    profileName.render();
    
    super.render(xm, ym, a);
  }
}
