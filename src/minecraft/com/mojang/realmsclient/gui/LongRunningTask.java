package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;

public abstract class LongRunningTask implements Runnable, ErrorCallback, GuiCallback {
  protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;
  
  public LongRunningTask() {}
  
  public void setScreen(RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen) {
    this.longRunningMcoTaskScreen = longRunningMcoTaskScreen;
  }
  
  public void error(String errorMessage)
  {
    longRunningMcoTaskScreen.error(errorMessage);
  }
  
  public void setTitle(String title) {
    longRunningMcoTaskScreen.setTitle(title);
  }
  
  public boolean aborted() {
    return longRunningMcoTaskScreen.aborted();
  }
  
  public void tick() {}
  
  public void buttonClicked(net.minecraft.realms.RealmsButton button) {}
  
  public void init() {}
  
  public void abortTask() {}
}
