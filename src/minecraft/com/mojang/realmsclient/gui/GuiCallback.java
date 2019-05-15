package com.mojang.realmsclient.gui;

import net.minecraft.realms.RealmsButton;

public abstract interface GuiCallback
{
  public abstract void tick();
  
  public abstract void buttonClicked(RealmsButton paramRealmsButton);
}
