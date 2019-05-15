package com.enjoytheban.module.modules.movement;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.FoodStats;


public class Sprint
  extends Module
{
  private Option<Boolean> omni = new Option("Omni-Directional", "omni", Boolean.valueOf(true));
  
  public Sprint() {
    super("Sprint", new String[] { "run" }, ModuleType.Movement);
    
    setColor(new Color(158, 205, 125).getRGB());
    addValues(new Value[] { omni });
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate event) {
    if ((mc.thePlayer.getFoodStats().getFoodLevel() > 6) && (((Boolean)omni.getValue()).booleanValue()) ? mc.thePlayer.moving() : mc.thePlayer.moveForward > 0.0F) {
      mc.thePlayer.setSprinting(true);
    }
  }
}
