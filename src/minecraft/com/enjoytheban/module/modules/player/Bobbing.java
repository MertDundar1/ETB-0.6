package com.enjoytheban.module.modules.player;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;


public class Bobbing
  extends Module
{
  private Numbers<Double> boob = new Numbers("Amount", "Amount", Double.valueOf(1.0D), Double.valueOf(0.1D), Double.valueOf(5.0D), Double.valueOf(0.5D));
  
  public Bobbing() {
    super("Bobbing+", new String[] { "bobbing+" }, ModuleType.Player);
    addValues(new Value[] { boob });
  }
  
  @EventHandler
  public void onUpdate(EventPreUpdate event) {
    setColor(new Color(20, 200, 100).getRGB());
    if (mc.thePlayer.onGround) {
      mc.thePlayer.cameraYaw = ((float)(0.09090908616781235D * ((Double)boob.getValue()).doubleValue()));
    }
  }
}
