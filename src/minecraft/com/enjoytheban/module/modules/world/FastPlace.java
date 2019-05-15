package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventTick;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;

public class FastPlace extends Module
{
  public FastPlace()
  {
    super("FastPlace", new String[] { "fplace", "fc" }, ModuleType.World);
    setColor(new Color(226, 197, 78).getRGB());
  }
  
  @EventHandler
  private void onTick(EventTick e)
  {
    mc.rightClickDelayTimer = 0;
  }
}
