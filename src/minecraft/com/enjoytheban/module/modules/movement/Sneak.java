package com.enjoytheban.module.modules.movement;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;



public class Sneak
  extends Module
{
  public Sneak()
  {
    super("Sneak", new String[] { "stealth", "snek" }, ModuleType.Movement);
    setColor(new Color(84, 194, 110).getRGB());
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e)
  {
    if (e.getType() == 0) {
      if ((mc.thePlayer.isSneaking()) || (mc.thePlayer.moving()))
        return;
      mc.thePlayer.sendQueue.addToSendQueue(
        new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
    }
    else if (e.getType() == 1) {
      mc.thePlayer.sendQueue.addToSendQueue(
        new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
    }
  }
  
  public void onDisable()
  {
    mc.thePlayer.sendQueue.addToSendQueue(
      new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
  }
}
