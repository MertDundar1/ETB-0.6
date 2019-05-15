package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsState;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;


public class RealmsBuyRealmsScreen
  extends RealmsScreen
{
  private static final Logger LOGGER = ;
  
  private RealmsScreen lastScreen;
  
  private static int BUTTON_BACK_ID = 0;
  
  private volatile RealmsState realmsStatus;
  private boolean onLink = false;
  
  public RealmsBuyRealmsScreen(RealmsScreen lastScreen) {
    this.lastScreen = lastScreen;
  }
  
  public void tick()
  {
    super.tick();
  }
  

  public void init()
  {
    Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    int buttonLength = 212;
    
    buttonsAdd(newButton(BUTTON_BACK_ID, width() / 2 - buttonLength / 2, RealmsConstants.row(12), buttonLength, 20, getLocalizedString("gui.back")));
    
    fetchMessage();
  }
  
  private void fetchMessage() {
    final RealmsClient client = RealmsClient.createRealmsClient();
    new Thread("Realms-stat-message")
    {
      public void run() {
        try {
          realmsStatus = client.fetchRealmsState();
        } catch (RealmsServiceException e) {
          RealmsBuyRealmsScreen.LOGGER.error("Could not get state");
          Realms.setScreen(new RealmsGenericErrorScreen(e, lastScreen));
        }
      }
    }.start();
  }
  
  public void removed()
  {
    Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (!button.active()) { return;
    }
    if (button.id() == BUTTON_BACK_ID) {
      Realms.setScreen(lastScreen);
    }
  }
  
  public void keyPressed(char ch, int eventKey)
  {
    if (eventKey == 1) {
      Realms.setScreen(lastScreen);
    }
  }
  
  public void mouseClicked(int x, int y, int buttonNum)
  {
    super.mouseClicked(x, y, buttonNum);
    
    if (onLink) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(new StringSelection(realmsStatus.getBuyLink()), null);
      RealmsUtil.browseTo(realmsStatus.getBuyLink());
    }
  }
  

  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    drawCenteredString(getLocalizedString("mco.buy.realms.title"), width() / 2, 17, 16777215);
    
    if (realmsStatus == null) {
      return;
    }
    
    String[] lines = realmsStatus.getStatusMessage().split("\n");
    
    int i = 1;
    
    for (String line : lines) {
      drawCenteredString(line, width() / 2, RealmsConstants.row(i), 10526880);
      i += 2;
    }
    
    if (realmsStatus.getBuyLink() != null) {
      String buyLink = realmsStatus.getBuyLink();
      
      int height = RealmsConstants.row(i + 1);
      
      int textWidth = fontWidth(buyLink);
      int x1 = width() / 2 - textWidth / 2 - 1;
      int y1 = height - 1;
      int x2 = x1 + textWidth + 1;
      int y2 = height + 1 + fontLineHeight();
      
      if ((x1 <= xm) && (xm <= x2) && (y1 <= ym) && (ym <= y2)) {
        onLink = true;
        drawString(buyLink, width() / 2 - textWidth / 2, height, 7107012);
      } else {
        onLink = false;
        drawString(buyLink, width() / 2 - textWidth / 2, height, 3368635);
      }
    }
    
    super.render(xm, ym, a);
  }
}
