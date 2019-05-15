package com.enjoytheban.module.modules.world;

import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;





public class SafeWalk
  extends Module
{
  public SafeWalk()
  {
    super("SafeWalk", new String[] { "eagle", "parkour" }, ModuleType.World);
    setColor(new Color(198, 253, 191).getRGB());
  }
}
