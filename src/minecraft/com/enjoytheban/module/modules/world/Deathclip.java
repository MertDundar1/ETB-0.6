package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.TimerUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;



public class Deathclip
  extends Module
{
  private TimerUtil timer = new TimerUtil();
  
  public Deathclip() {
    super("DeathClip", new String[] { "deathc", "dc" }, ModuleType.World);
    setColor(new Color(157, 58, 157).getRGB());
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e) {
    if ((mc.thePlayer.getHealth() == 0.0F) && (mc.thePlayer.onGround)) {
      mc.thePlayer.boundingBox.offsetAndUpdate(mc.thePlayer.posX, -10.0D, mc.thePlayer.posZ);
      if (timer.hasReached(500.0D)) {
        mc.thePlayer.sendChatMessage("/home");
      }
    }
  }
}
