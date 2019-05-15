package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class RealmsConfirmScreen extends RealmsScreen
{
  protected RealmsScreen parent;
  protected String title1;
  private String title2;
  protected String yesButton;
  protected String noButton;
  protected int id;
  private int delayTicker;
  
  public RealmsConfirmScreen(RealmsScreen parent, String title1, String title2, int id)
  {
    this.parent = parent;
    this.title1 = title1;
    this.title2 = title2;
    this.id = id;
    
    yesButton = getLocalizedString("gui.yes");
    noButton = getLocalizedString("gui.no");
  }
  
  public RealmsConfirmScreen(RealmsScreen parent, String title1, String title2, String yesButton, String noButton, int id) {
    this.parent = parent;
    this.title1 = title1;
    this.title2 = title2;
    this.yesButton = yesButton;
    this.noButton = noButton;
    this.id = id;
  }
  
  public void init()
  {
    buttonsAdd(newButton(0, width() / 2 - 105, com.mojang.realmsclient.gui.RealmsConstants.row(9), 100, 20, yesButton));
    buttonsAdd(newButton(1, width() / 2 + 5, com.mojang.realmsclient.gui.RealmsConstants.row(9), 100, 20, noButton));
  }
  
  public void buttonClicked(RealmsButton button)
  {
    parent.confirmResult(button.id() == 0, id);
  }
  
  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    drawCenteredString(title1, width() / 2, com.mojang.realmsclient.gui.RealmsConstants.row(3), 16777215);
    drawCenteredString(title2, width() / 2, com.mojang.realmsclient.gui.RealmsConstants.row(5), 16777215);
    
    super.render(xm, ym, a);
  }
  
  public void setDelay(int delay) {
    delayTicker = delay;
    
    for (RealmsButton button : buttons()) {
      button.active(false);
    }
  }
  
  public void tick()
  {
    super.tick();
    
    if (--delayTicker == 0) {
      for (RealmsButton button : buttons()) {
        button.active(true);
      }
    }
  }
}
