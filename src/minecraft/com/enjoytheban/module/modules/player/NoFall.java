package com.enjoytheban.module.modules.player;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;

public class NoFall
  extends Module
{
  public NoFall()
  {
    super("NoFall", new String[] { "Nofalldamage" }, ModuleType.Player);
    setColor(new Color(242, 137, 73).getRGB());
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e) {
    if (mc.thePlayer.fallDistance > 3.0F) {
      mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, 
        mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
      mc.thePlayer.fallDistance = 0.0F;
    }
  }
}
