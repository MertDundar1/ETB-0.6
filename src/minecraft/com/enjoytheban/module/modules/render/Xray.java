package com.enjoytheban.module.modules.render;

import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;

public class Xray extends Module
{
  public List<Integer> blocks = new java.util.ArrayList();
  
  public Xray() {
    super("Xray", new String[] { "xrai", "oreesp" }, ModuleType.Render);
    setColor(Color.GREEN.getRGB());
    blocks.add(Integer.valueOf(16));
    blocks.add(Integer.valueOf(56));
    blocks.add(Integer.valueOf(14));
    blocks.add(Integer.valueOf(15));
    blocks.add(Integer.valueOf(129));
    blocks.add(Integer.valueOf(73));
  }
  
  public void onEnable()
  {
    mc.renderGlobal.loadRenderers();
  }
  
  public void onDisable()
  {
    mc.renderGlobal.loadRenderers();
  }
  
  public List<Integer> getBlocks() {
    return blocks;
  }
}
