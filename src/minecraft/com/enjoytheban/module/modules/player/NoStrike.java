package com.enjoytheban.module.modules.player;

import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import java.util.Random;




public class NoStrike
  extends Module
{
  public NoStrike()
  {
    super("NoStrike", new String[] { "antistrike" }, ModuleType.Player);
    setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
  }
}
