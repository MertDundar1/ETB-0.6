package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServer.State;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsScreen;
import org.lwjgl.input.Keyboard;





public class RealmsSettingsScreen
  extends RealmsScreen
{
  private RealmsConfigureWorldScreen configureWorldScreen;
  private RealmsServer serverData;
  private static final int BUTTON_CANCEL_ID = 0;
  private static final int BUTTON_DONE_ID = 1;
  private static final int NAME_EDIT_BOX = 2;
  private static final int DESC_EDIT_BOX = 3;
  private static final int BUTTON_OPEN_CLOSE_ID = 5;
  private final int COMPONENT_WIDTH = 212;
  
  private RealmsButton doneButton;
  private RealmsEditBox descEdit;
  private RealmsEditBox nameEdit;
  
  public RealmsSettingsScreen(RealmsConfigureWorldScreen configureWorldScreen, RealmsServer serverData)
  {
    this.configureWorldScreen = configureWorldScreen;
    this.serverData = serverData;
  }
  
  public void tick()
  {
    nameEdit.tick();
    descEdit.tick();
  }
  
  public void init()
  {
    Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    int center = width() / 2 - 106;
    
    buttonsAdd(this.doneButton = newButton(1, center - 2, RealmsConstants.row(12), 106, 20, getLocalizedString("mco.configure.world.buttons.done")));
    buttonsAdd(newButton(0, width() / 2 + 2, RealmsConstants.row(12), 106, 20, getLocalizedString("gui.cancel")));
    buttonsAdd(newButton(5, width() / 2 - 53, RealmsConstants.row(0), 106, 20, serverData.state.equals(RealmsServer.State.OPEN) ? getLocalizedString("mco.configure.world.buttons.close") : getLocalizedString("mco.configure.world.buttons.open")));
    
    nameEdit = newEditBox(2, center, RealmsConstants.row(4), 212, 20);
    nameEdit.setFocus(true);
    nameEdit.setMaxLength(32);
    
    if (serverData.getName() != null) {
      nameEdit.setValue(serverData.getName());
    }
    
    descEdit = newEditBox(3, center, RealmsConstants.row(8), 212, 20);
    descEdit.setMaxLength(32);
    
    if (serverData.getDescription() != null) {
      descEdit.setValue(serverData.getDescription());
    }
  }
  
  public void removed()
  {
    Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) { return;
    }
    switch (button.id()) {
    case 0: 
      Realms.setScreen(configureWorldScreen);
      break;
    case 1: 
      save();
      break;
    case 5: 
      if (serverData.state.equals(RealmsServer.State.OPEN)) {
        String line2 = getLocalizedString("mco.configure.world.close.question.line1");
        String line3 = getLocalizedString("mco.configure.world.close.question.line2");
        Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 5));
      } else {
        configureWorldScreen.openTheWorld(false, this);
      }
      break;
    }
    
  }
  

  public void confirmResult(boolean result, int id)
  {
    switch (id) {
    case 5: 
      if (result) {
        configureWorldScreen.closeTheWorld(this);
      } else {
        Realms.setScreen(this);
      }
      break;
    }
    
  }
  
  public void keyPressed(char ch, int eventKey)
  {
    nameEdit.keyPressed(ch, eventKey);
    descEdit.keyPressed(ch, eventKey);
    
    switch (eventKey) {
    case 15: 
      nameEdit.setFocus(!nameEdit.isFocused());
      descEdit.setFocus(!descEdit.isFocused());
      break;
    case 28: 
    case 156: 
      save();
      break;
    case 1: 
      Realms.setScreen(configureWorldScreen);
    default: 
      return;
    }
    
    doneButton.active((nameEdit.getValue() != null) && (!nameEdit.getValue().trim().equals("")));
  }
  
  public void mouseClicked(int x, int y, int buttonNum)
  {
    super.mouseClicked(x, y, buttonNum);
    
    descEdit.mouseClicked(x, y, buttonNum);
    nameEdit.mouseClicked(x, y, buttonNum);
  }
  
  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    drawCenteredString(getLocalizedString("mco.configure.world.settings.title"), width() / 2, 17, 16777215);
    
    drawString(getLocalizedString("mco.configure.world.name"), width() / 2 - 106, RealmsConstants.row(3), 10526880);
    drawString(getLocalizedString("mco.configure.world.description"), width() / 2 - 106, RealmsConstants.row(7), 10526880);
    
    nameEdit.render();
    descEdit.render();
    
    super.render(xm, ym, a);
  }
  
  public void save() {
    configureWorldScreen.saveSettings(nameEdit.getValue(), descEdit.getValue());
  }
}
