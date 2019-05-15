package com.enjoytheban.module.modules.render;

import com.enjoytheban.api.events.world.EventTick;
import java.awt.Color;
import net.minecraft.client.Minecraft;

public class FullBright extends com.enjoytheban.module.Module
{
  private float old;
  
  public FullBright()
  {
    super("FullBright", new String[] { "fbright", "brightness", "bright" }, com.enjoytheban.module.ModuleType.Render);
    setColor(new Color(244, 255, 149).getRGB());
  }
  


  public void onEnable()
  {
    old = mc.gameSettings.gammaSetting;
  }
  
  @com.enjoytheban.api.EventHandler
  private void onTick(EventTick e) {
    mc.gameSettings.gammaSetting = 1.5999999E7F;
  }
  
  public void onDisable()
  {
    mc.gameSettings.gammaSetting = old;
  }
}
