package com.enjoytheban.module.modules.movement;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Numbers;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

public class Step extends Module
{
  private Numbers<Integer> height = new Numbers("Height", "height", Double.valueOf(1.0D), Double.valueOf(1.0D), Double.valueOf(10.0D), Double.valueOf(0.5D));
  private Option<Boolean> ncp = new Option("NCP", "ncp", Boolean.valueOf(false));
  
  public Step() {
    super("Step", new String[] { "autojump" }, ModuleType.Movement);
    setColor(new Color(165, 238, 65).getRGB());
    addValues(new com.enjoytheban.api.value.Value[] { ncp });
  }
  
  public void onDisable()
  {
    mc.thePlayer.stepHeight = 0.6F;
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e) {
    if (((Boolean)ncp.getValue()).booleanValue()) {
      mc.thePlayer.stepHeight = 0.6F;
      if ((mc.thePlayer.isCollidedHorizontally) && (mc.thePlayer.onGround) && (mc.thePlayer.isCollidedVertically) && 
        (mc.thePlayer.isCollided) && 
        (mc.thePlayer.isCollidedHorizontally) && (mc.thePlayer.onGround) && 
        (mc.thePlayer.isCollidedVertically) && (mc.thePlayer.isCollided)) {
        mc.thePlayer.setSprinting(true);
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
          mc.thePlayer.posX, mc.thePlayer.posY + 0.42D, mc.thePlayer.posZ, 
          mc.thePlayer.onGround));
        mc.thePlayer.setSprinting(true);
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
          mc.thePlayer.posX, mc.thePlayer.posY + 0.753D, mc.thePlayer.posZ, 
          mc.thePlayer.onGround));
        mc.thePlayer.setSprinting(true);
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42D, 
          mc.thePlayer.posZ);
        mc.timer.timerSpeed = 0.5F;
        mc.thePlayer.setSprinting(true);
        new Thread(new Runnable()
        {
          public void run() {
            try {
              Thread.sleep(100L);
            } catch (InterruptedException ex) {
              ex.printStackTrace();
            }
            mc.timer.timerSpeed = 1.0F;
          }
        })
        








          .start();
      }
    }
    else {
      mc.thePlayer.stepHeight = 1.0F;
    }
  }
}
