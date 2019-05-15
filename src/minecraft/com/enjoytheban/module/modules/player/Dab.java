package com.enjoytheban.module.modules.player;

import com.enjoytheban.module.Module;
import java.awt.Color;
import java.util.Random;

public class Dab extends Module
{
  public Dab()
  {
    super("Dab", new String[] { "dab" }, com.enjoytheban.module.ModuleType.Player);
    setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
    setRemoved(false);
  }
}
