package com.enjoytheban.module.modules.combat;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FoodStats;

public class Regen extends Module
{
  private Option<Boolean> guardian = new Option("Guardian", "guardian", Boolean.valueOf(true));
  
  public Regen() {
    super("Regen", new String[] { "fastgen" }, ModuleType.Combat);
    setColor(new Color(208, 30, 142).getRGB());
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate event) {
    if ((mc.thePlayer.onGround) && (mc.thePlayer.getHealth() < 16.0D) && 
      (mc.thePlayer.getFoodStats().getFoodLevel() > 17) && (mc.thePlayer.isCollidedVertically)) {
      for (int i = 0; i < 60; i++) {
        mc.thePlayer.sendQueue.addToSendQueue(
          new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-9D, 
          mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionZ = 0.0D;
      }
      if ((((Boolean)guardian.getValue()).booleanValue()) && 
        (mc.thePlayer.ticksExisted % 3 == 0)) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
          mc.thePlayer.posY - 999.0D, mc.thePlayer.posZ, true));
      }
      
      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }
  }
}
