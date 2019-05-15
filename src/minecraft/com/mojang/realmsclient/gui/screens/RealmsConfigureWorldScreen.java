package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsOptions;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServer.State;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.RealmsHideableButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSharedConstants;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class RealmsConfigureWorldScreen extends RealmsScreenWithCallback<WorldTemplate>
{
  private static final Logger LOGGER = ;
  
  private static final String ON_ICON_LOCATION = "realms:textures/gui/realms/on_icon.png";
  
  private static final String OFF_ICON_LOCATION = "realms:textures/gui/realms/off_icon.png";
  
  private static final String EXPIRED_ICON_LOCATION = "realms:textures/gui/realms/expired_icon.png";
  
  private static final String SLOT_FRAME_LOCATION = "realms:textures/gui/realms/slot_frame.png";
  
  private static final String EMPTY_FRAME_LOCATION = "realms:textures/gui/realms/empty_frame.png";
  private String toolTip;
  private final RealmsScreen lastScreen;
  private RealmsServer serverData;
  private volatile long serverId;
  private int left_x;
  private int right_x;
  private int default_button_width = 80;
  private int default_button_offset = 5;
  
  private static final int BUTTON_BACK_ID = 0;
  
  private static final int BUTTON_PLAYERS_ID = 2;
  
  private static final int BUTTON_SETTINGS_ID = 3;
  
  private static final int BUTTON_SUBSCRIPTION_ID = 4;
  
  private static final int BUTTON_OPTIONS_ID = 5;
  
  private static final int BUTTON_BACKUP_ID = 6;
  
  private static final int BUTTON_RESET_WORLD_ID = 7;
  
  private static final int BUTTON_SWITCH_MINIGAME_ID = 8;
  
  private static final int SWITCH_SLOT_ID = 9;
  
  private static final int SWITCH_SLOT_ID_EMPTY = 10;
  private static final int SWITCH_SLOT_ID_RESULT = 11;
  private RealmsButton playersButton;
  private RealmsButton settingsButton;
  private RealmsButton subscriptionButton;
  private RealmsHideableButton optionsButton;
  private RealmsHideableButton backupButton;
  private RealmsHideableButton resetWorldButton;
  private RealmsHideableButton switchMinigameButton;
  private boolean stateChanged;
  private int hoveredSlot = -1;
  
  private int animTick;
  
  private int clicks = 0;
  private boolean hoveredActiveSlot = false;
  
  public RealmsConfigureWorldScreen(RealmsScreen lastScreen, long serverId) {
    this.lastScreen = lastScreen;
    this.serverId = serverId;
  }
  
  public void mouseEvent()
  {
    super.mouseEvent();
  }
  
  public void init()
  {
    if (serverData == null) {
      fetchServerData(serverId);
    }
    
    left_x = (width() / 2 - 187);
    right_x = (width() / 2 + 190);
    
    org.lwjgl.input.Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    buttonsAdd(this.playersButton = newButton(2, centerButton(0, 3), RealmsConstants.row(0), default_button_width, 20, getLocalizedString("mco.configure.world.buttons.players")));
    buttonsAdd(this.settingsButton = newButton(3, centerButton(1, 3), RealmsConstants.row(0), default_button_width, 20, getLocalizedString("mco.configure.world.buttons.settings")));
    buttonsAdd(this.subscriptionButton = newButton(4, centerButton(2, 3), RealmsConstants.row(0), default_button_width, 20, getLocalizedString("mco.configure.world.buttons.subscription")));
    
    buttonsAdd(this.optionsButton = new RealmsHideableButton(5, leftButton(0), RealmsConstants.row(13) - 5, default_button_width + 10, 20, getLocalizedString("mco.configure.world.buttons.options")));
    buttonsAdd(this.backupButton = new RealmsHideableButton(6, leftButton(1), RealmsConstants.row(13) - 5, default_button_width + 10, 20, getLocalizedString("mco.configure.world.backup")));
    buttonsAdd(this.resetWorldButton = new RealmsHideableButton(7, leftButton(2), RealmsConstants.row(13) - 5, default_button_width + 10, 20, getLocalizedString("mco.configure.world.buttons.resetworld")));
    
    buttonsAdd(this.switchMinigameButton = new RealmsHideableButton(8, leftButton(0), RealmsConstants.row(13) - 5, default_button_width + 20, 20, getLocalizedString("mco.configure.world.buttons.switchminigame")));
    
    buttonsAdd(newButton(0, right_x - default_button_width + 8, RealmsConstants.row(13) - 5, default_button_width - 10, 20, getLocalizedString("gui.back")));
    
    backupButton.active(true);
    
    if (serverData == null) {
      hideMinigameButtons();
      hideRegularButtons();
      
      playersButton.active(false);
      settingsButton.active(false);
      subscriptionButton.active(false);
    } else {
      disableButtons();
      
      if (isMinigame()) {
        hideRegularButtons();
      } else {
        hideMinigameButtons();
      }
    }
  }
  
  private int leftButton(int i)
  {
    return left_x + i * (default_button_width + 10 + default_button_offset);
  }
  
  private int centerButton(int i, int total) {
    return width() / 2 - (total * (default_button_width + default_button_offset) - default_button_offset) / 2 + i * (default_button_width + default_button_offset);
  }
  
  public void tick()
  {
    animTick += 1;
    clicks -= 1;
    if (clicks < 0) {
      clicks = 0;
    }
  }
  
  public void render(int xm, int ym, float a)
  {
    toolTip = null;
    hoveredActiveSlot = false;
    hoveredSlot = -1;
    
    renderBackground();
    
    drawCenteredString(getLocalizedString("mco.configure.worlds.title"), width() / 2, RealmsConstants.row(4), 16777215);
    
    super.render(xm, ym, a);
    
    if (serverData == null) {
      drawCenteredString(getLocalizedString("mco.configure.world.title"), width() / 2, 17, 16777215);
      return;
    }
    
    String name = serverData.getName();
    int nameWidth = fontWidth(name);
    int nameColor = serverData.state == RealmsServer.State.CLOSED ? 10526880 : 8388479;
    int titleWidth = fontWidth(getLocalizedString("mco.configure.world.title"));
    
    drawCenteredString(getLocalizedString("mco.configure.world.title"), width() / 2 - nameWidth / 2 - 2, 17, 16777215);
    
    drawCenteredString(name, width() / 2 + titleWidth / 2 + 2, 17, nameColor);
    
    int statusX = width() / 2 + nameWidth / 2 + titleWidth / 2 + 5;
    drawServerStatus(statusX, 17, xm, ym);
    
    for (Map.Entry<Integer, RealmsOptions> entry : serverData.slots.entrySet()) {
      if ((getValuetemplateImage != null) && (getValuetemplateId != -1L)) {
        drawSlotFrame(frame(((Integer)entry.getKey()).intValue()), RealmsConstants.row(5) + 5, xm, ym, (serverData.activeSlot == ((Integer)entry.getKey()).intValue()) && (!isMinigame()), ((RealmsOptions)entry.getValue()).getSlotName(((Integer)entry.getKey()).intValue()), ((Integer)entry.getKey()).intValue(), getValuetemplateId, getValuetemplateImage, getValueempty);
      } else {
        drawSlotFrame(frame(((Integer)entry.getKey()).intValue()), RealmsConstants.row(5) + 5, xm, ym, (serverData.activeSlot == ((Integer)entry.getKey()).intValue()) && (!isMinigame()), ((RealmsOptions)entry.getValue()).getSlotName(((Integer)entry.getKey()).intValue()), ((Integer)entry.getKey()).intValue(), -1L, null, getValueempty);
      }
    }
    
    drawSlotFrame(frame(4), RealmsConstants.row(5) + 5, xm, ym, isMinigame(), "Minigame", 4, -1L, null, false);
    
    if (isMinigame()) {
      drawString(getLocalizedString("mco.configure.current.minigame") + ": " + serverData.getMinigameName(), left_x + default_button_width + 20 + default_button_offset * 2, RealmsConstants.row(13), 16777215);
    }
    
    if (toolTip != null) {
      renderMousehoverTooltip(toolTip, xm, ym);
    }
  }
  
  private int frame(int i)
  {
    return left_x + (i - 1) * 98;
  }
  

  public void removed()
  {
    org.lwjgl.input.Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) { return;
    }
    if (((button instanceof RealmsHideableButton)) && (!((RealmsHideableButton)button).getVisible())) { return;
    }
    switch (button.id()) {
    case 2: 
      Realms.setScreen(new RealmsPlayerScreen(this, serverData));
      break;
    case 3: 
      Realms.setScreen(new RealmsSettingsScreen(this, serverData.clone()));
      break;
    case 4: 
      Realms.setScreen(new RealmsSubscriptionInfoScreen(this, serverData.clone(), lastScreen));
      break;
    case 0: 
      backButtonClicked();
      break;
    case 8: 
      Realms.setScreen(new RealmsSelectWorldTemplateScreen(this, null, true));
      break;
    case 6: 
      Realms.setScreen(new RealmsBackupScreen(this, serverData.clone()));
      break;
    case 5: 
      Realms.setScreen(new RealmsSlotOptionsScreen(this, ((RealmsOptions)serverData.slots.get(Integer.valueOf(serverData.activeSlot))).clone(), serverData.worldType, serverData.activeSlot));
      break;
    case 7: 
      Realms.setScreen(new RealmsResetWorldScreen(this, serverData.clone(), getNewScreen()));
      break;
    }
    
  }
  

  public void keyPressed(char ch, int eventKey)
  {
    if (eventKey == 1) {
      backButtonClicked();
    }
  }
  
  private void backButtonClicked() {
    if (stateChanged) {
      ((RealmsMainScreen)lastScreen).removeSelection();
    }
    Realms.setScreen(lastScreen);
  }
  
  private void fetchServerData(final long worldId) {
    new Thread()
    {
      public void run() {
        RealmsClient client = RealmsClient.createRealmsClient();
        try {
          serverData = client.getOwnWorld(worldId);
          
          RealmsConfigureWorldScreen.this.disableButtons();
          
          if (RealmsConfigureWorldScreen.this.isMinigame()) {
            RealmsConfigureWorldScreen.this.showMinigameButtons();
          } else {
            RealmsConfigureWorldScreen.this.showRegularButtons();
          }
        }
        catch (RealmsServiceException e) {
          RealmsConfigureWorldScreen.LOGGER.error("Couldn't get own world");
          Realms.setScreen(new RealmsGenericErrorScreen(e.getMessage(), lastScreen));
        } catch (IOException e) {
          RealmsConfigureWorldScreen.LOGGER.error("Couldn't parse response getting own world");
        }
      }
    }.start();
  }
  
  private void disableButtons() {
    playersButton.active(!serverData.expired);
    settingsButton.active(!serverData.expired);
    subscriptionButton.active(true);
    
    switchMinigameButton.active(!serverData.expired);
    
    optionsButton.active(!serverData.expired);
    resetWorldButton.active(!serverData.expired);
  }
  
  public void confirmResult(boolean result, int id)
  {
    switch (id)
    {
    case 9: 
      if (result) {
        switchSlot();
      } else {
        Realms.setScreen(this);
      }
      
      break;
    
    case 10: 
      if (result) {
        RealmsResetWorldScreen resetWorldScreen = new RealmsResetWorldScreen(this, serverData, getNewScreen(), getLocalizedString("mco.configure.world.switch.slot"), getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, getLocalizedString("gui.cancel"));
        resetWorldScreen.setSlot(hoveredSlot);
        resetWorldScreen.setResetTitle(getLocalizedString("mco.create.world.reset.title"));
        Realms.setScreen(resetWorldScreen);
      } else {
        Realms.setScreen(this);
      }
      
      break;
    
    case 11: 
      Realms.setScreen(this);
    }
    
  }
  

  public void mouseClicked(int x, int y, int buttonNum)
  {
    if (buttonNum == 0) {
      clicks += RealmsSharedConstants.TICKS_PER_SECOND / 3 + 1;
    } else {
      return;
    }
    
    if (hoveredSlot != -1) {
      if (hoveredSlot < 4) {
        String line2 = getLocalizedString("mco.configure.world.slot.switch.question.line1");
        String line3 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
        if (serverData.slots.get(Integer.valueOf(hoveredSlot))).empty) {
          Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 10));
        } else {
          Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 9));
        }
      } else if ((!isMinigame()) && (!serverData.expired)) {
        Realms.setScreen(new RealmsSelectWorldTemplateScreen(this, null, true, true));
      }
    } else if ((clicks >= RealmsSharedConstants.TICKS_PER_SECOND / 2) && (hoveredActiveSlot) && ((serverData.state == RealmsServer.State.OPEN) || (serverData.state == RealmsServer.State.CLOSED))) {
      if (serverData.state == RealmsServer.State.OPEN) {
        ((RealmsMainScreen)lastScreen).play(serverData);
      } else {
        openTheWorld(true, this);
      }
    }
    
    super.mouseClicked(x, y, buttonNum);
  }
  
  protected void renderMousehoverTooltip(String msg, int x, int y) {
    if (msg == null) {
      return;
    }
    
    int rx = x + 12;
    int ry = y - 12;
    int width = fontWidth(msg);
    
    if (rx + width + 3 > right_x) {
      rx = rx - width - 20;
    }
    fillGradient(rx - 3, ry - 3, rx + width + 3, ry + 8 + 3, -1073741824, -1073741824);
    
    fontDrawShadow(msg, rx, ry, 16777215);
  }
  
  private void drawServerStatus(int x, int y, int xm, int ym) {
    if (serverData.expired) {
      drawExpired(x, y, xm, ym);
    } else if (serverData.state == RealmsServer.State.ADMIN_LOCK) {
      drawLocked(x, y, xm, ym, false);
    } else if (serverData.state == RealmsServer.State.CLOSED) {
      drawLocked(x, y, xm, ym, true);
    } else if (serverData.state == RealmsServer.State.OPEN) {
      if (serverData.daysLeft < 7) {
        drawExpiring(x, y, xm, ym, serverData.daysLeft);
      } else {
        drawOpen(x, y, xm, ym);
      }
    }
  }
  
  private void drawExpiring(int x, int y, int xm, int ym, int daysLeft) {
    if (animTick % 20 < 10) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
      GL11.glPopMatrix();
    }
    
    if ((xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9)) {
      if (daysLeft == 0) {
        toolTip = getLocalizedString("mco.selectServer.expires.soon");
      } else if (daysLeft == 1) {
        toolTip = getLocalizedString("mco.selectServer.expires.day");
      } else {
        toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[] { Integer.valueOf(daysLeft) });
      }
    }
  }
  
  private void drawOpen(int x, int y, int xm, int ym) {
    RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
    GL11.glPopMatrix();
    
    if ((xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9)) {
      toolTip = getLocalizedString("mco.selectServer.open");
    }
  }
  
  private void drawExpired(int x, int y, int xm, int ym) {
    bind("realms:textures/gui/realms/expired_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
    GL11.glPopMatrix();
    if ((xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9)) toolTip = getLocalizedString("mco.selectServer.expired");
  }
  
  private void drawLocked(int x, int y, int xm, int ym, boolean closed) {
    bind("realms:textures/gui/realms/off_icon.png");
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
    GL11.glPopMatrix();
    if ((closed) && (xm >= x) && (xm <= x + 9) && (ym >= y) && (ym <= y + 9)) {
      toolTip = getLocalizedString("mco.selectServer.closed");
    }
  }
  
  private boolean isMinigame() {
    return (serverData != null) && (serverData.worldType.equals(com.mojang.realmsclient.dto.RealmsServer.WorldType.MINIGAME));
  }
  
  private void drawSlotFrame(int x, int y, int xm, int ym, boolean active, String text, int i, long imageId, String image, boolean empty) {
    if ((xm >= x) && (xm <= x + 80) && (ym >= y) && (ym <= y + 80) && (((!isMinigame()) && (serverData.activeSlot != i)) || ((isMinigame()) && (i != 4) && (
      (i != 4) || (!serverData.expired)))))
    {
      hoveredSlot = i;
      toolTip = (i == 4 ? getLocalizedString("mco.configure.world.slot.tooltip.minigame") : getLocalizedString("mco.configure.world.slot.tooltip"));
    }
    

    if ((xm >= x) && (xm <= x + 80) && (ym >= y) && (ym <= y + 80) && (((!isMinigame()) && (serverData.activeSlot == i)) || ((isMinigame()) && (i == 4) && (!serverData.expired) && ((serverData.state == RealmsServer.State.OPEN) || (serverData.state == RealmsServer.State.CLOSED))))) {
      hoveredActiveSlot = true;
      toolTip = getLocalizedString("mco.configure.world.slot.tooltip.active");
    }
    
    if (empty) {
      bind("realms:textures/gui/realms/empty_frame.png");
    } else if ((image != null) && (imageId != -1L)) {
      RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
    } else if (i == 1) {
      bind("textures/gui/title/background/panorama_0.png");
    } else if (i == 2) {
      bind("textures/gui/title/background/panorama_2.png");
    } else if (i == 3) {
      bind("textures/gui/title/background/panorama_3.png");
    } else {
      RealmsTextureManager.bindWorldTemplate(String.valueOf(serverData.minigameId), serverData.minigameImage);
    }
    
    if (!active) {
      GL11.glColor4f(0.56F, 0.56F, 0.56F, 1.0F);
    } else if (active) {
      float c = 0.9F + 0.1F * net.minecraft.realms.RealmsMth.cos(animTick * 0.2F);
      GL11.glColor4f(c, c, c, 1.0F);
    }
    
    RealmsScreen.blit(x + 3, y + 3, 0.0F, 0.0F, 74, 74, 74.0F, 74.0F);
    
    bind("realms:textures/gui/realms/slot_frame.png");
    
    if (hoveredSlot == i) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    } else if (!active) {
      GL11.glColor4f(0.56F, 0.56F, 0.56F, 1.0F);
    } else {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    RealmsScreen.blit(x, y, 0.0F, 0.0F, 80, 80, 80.0F, 80.0F);
    
    drawCenteredString(text, x + 40, y + 66, 16777215);
  }
  
  private void hideRegularButtons()
  {
    optionsButton.setVisible(false);
    backupButton.setVisible(false);
    resetWorldButton.setVisible(false);
  }
  
  private void hideMinigameButtons() {
    switchMinigameButton.setVisible(false);
  }
  
  private void showRegularButtons() {
    optionsButton.setVisible(true);
    backupButton.setVisible(true);
    resetWorldButton.setVisible(true);
  }
  
  private void showMinigameButtons() {
    switchMinigameButton.setVisible(true);
  }
  
  public void saveSlotSettings() {
    RealmsClient client = RealmsClient.createRealmsClient();
    try {
      client.updateSlot(serverData.id, (RealmsOptions)serverData.slots.get(Integer.valueOf(serverData.activeSlot)));
    } catch (RealmsServiceException e) {
      LOGGER.error("Couldn't save slot settings");
      Realms.setScreen(new RealmsGenericErrorScreen(e, this));
      return;
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("Couldn't save slot settings");
    }
    Realms.setScreen(this);
  }
  
  public void saveSlotSettings(RealmsOptions options) {
    RealmsOptions oldOptions = (RealmsOptions)serverData.slots.get(Integer.valueOf(serverData.activeSlot));
    templateId = templateId;
    templateImage = templateImage;
    serverData.slots.put(Integer.valueOf(serverData.activeSlot), options);
    saveSlotSettings();
  }
  
  public void saveServerData() {
    RealmsClient client = RealmsClient.createRealmsClient();
    try {
      client.update(serverData.id, serverData.getName(), serverData.getDescription());
    } catch (RealmsServiceException e) {
      LOGGER.error("Couldn't save settings");
      Realms.setScreen(new RealmsGenericErrorScreen(e, this));
      return;
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("Couldn't save settings");
    }
    Realms.setScreen(this);
  }
  
  public void saveSettings(String name, String desc) {
    String description = (desc == null) || (desc.trim().equals("")) ? null : desc;
    
    serverData.setName(name);
    serverData.setDescription(description);
    saveServerData();
  }
  
  public void openTheWorld(boolean join, RealmsScreen screenInCaseOfCancel) {
    com.mojang.realmsclient.util.RealmsTasks.OpenServerTask openServerTask = new com.mojang.realmsclient.util.RealmsTasks.OpenServerTask(serverData, this, lastScreen, join);
    RealmsLongRunningMcoTaskScreen openWorldLongRunningTaskScreen = new RealmsLongRunningMcoTaskScreen(screenInCaseOfCancel, openServerTask);
    openWorldLongRunningTaskScreen.start();
    Realms.setScreen(openWorldLongRunningTaskScreen);
  }
  
  public void closeTheWorld(RealmsScreen screenInCaseOfCancel) {
    com.mojang.realmsclient.util.RealmsTasks.CloseServerTask closeServerTask = new com.mojang.realmsclient.util.RealmsTasks.CloseServerTask(serverData, this);
    RealmsLongRunningMcoTaskScreen closeWorldLongRunningTaskScreen = new RealmsLongRunningMcoTaskScreen(screenInCaseOfCancel, closeServerTask);
    closeWorldLongRunningTaskScreen.start();
    Realms.setScreen(closeWorldLongRunningTaskScreen);
  }
  
  public void stateChanged() {
    stateChanged = true;
  }
  
  void callback(WorldTemplate worldTemplate)
  {
    if (worldTemplate == null) {
      return;
    }
    
    if (minigame) {
      switchMinigame(worldTemplate);
    }
  }
  
  private void switchSlot() {
    com.mojang.realmsclient.util.RealmsTasks.SwitchSlotTask switchSlotTask = new com.mojang.realmsclient.util.RealmsTasks.SwitchSlotTask(serverData.id, hoveredSlot, getNewScreen(), 11);
    RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(lastScreen, switchSlotTask);
    longRunningMcoTaskScreen.start();
    Realms.setScreen(longRunningMcoTaskScreen);
  }
  
  private void switchMinigame(WorldTemplate selectedWorldTemplate) {
    com.mojang.realmsclient.util.RealmsTasks.SwitchMinigameTask startMinigameTask = new com.mojang.realmsclient.util.RealmsTasks.SwitchMinigameTask(serverData.id, selectedWorldTemplate, getNewScreen());
    RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(lastScreen, startMinigameTask);
    longRunningMcoTaskScreen.start();
    Realms.setScreen(longRunningMcoTaskScreen);
  }
  
  public RealmsConfigureWorldScreen getNewScreen() {
    return new RealmsConfigureWorldScreen(lastScreen, serverId);
  }
}
