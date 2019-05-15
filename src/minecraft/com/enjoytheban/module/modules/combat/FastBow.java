package com.enjoytheban.module.modules.combat;

import com.enjoytheban.api.events.world.EventPacketRecieve;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.EnumFacing;

public class FastBow extends com.enjoytheban.module.Module
{
  private TimerUtil timer = new TimerUtil();
  private Option<Boolean> faithful = new Option("Faithful", "faithful", Boolean.valueOf(true));
  int counter;
  
  public FastBow()
  {
    super("FastBow", new String[] { "zoombow", "quickbow" }, com.enjoytheban.module.ModuleType.Combat);
    setColor(new java.awt.Color(255, 99, 99).getRGB());
    addValues(new com.enjoytheban.api.value.Value[] { faithful });
    counter = 0;
  }
  
  private boolean canConsume() {
    return (mc.thePlayer.inventory.getCurrentItem() != null) && 
      ((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow));
  }
  
  private void killGuardian() {
    if (timer.hasReached(1000.0D)) {
      mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
        mc.thePlayer.posY - Double.POSITIVE_INFINITY, mc.thePlayer.posZ, false));
      timer.reset();
    }
  }
  
  @com.enjoytheban.api.EventHandler
  private void onUpdate(com.enjoytheban.api.events.world.EventPreUpdate e) {
    if (((Boolean)faithful.getValue()).booleanValue()) {
      if ((mc.thePlayer.onGround) && (mc.thePlayer.getCurrentEquippedItem() != null) && 
        ((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)) && 
        (mc.gameSettings.keyBindUseItem.pressed)) {
        mc.thePlayer.sendQueue.addToSendQueue(
          new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        for (int index = 0; index < 16; index++) {
          if (!mc.thePlayer.isDead) {
            mc.thePlayer.sendQueue.addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook(
              mc.thePlayer.posX, mc.thePlayer.posY - 0.09D, mc.thePlayer.posZ, 
              mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
          }
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
          C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, net.minecraft.util.BlockPos.ORIGIN, EnumFacing.DOWN));
      }
      if ((mc.thePlayer.onGround) && (mc.thePlayer.getCurrentEquippedItem() != null) && 
        ((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)) && 
        (mc.gameSettings.keyBindUseItem.pressed)) {
        mc.thePlayer.sendQueue.addToSendQueue(
          new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        if (mc.thePlayer.ticksExisted % 2 == 0) {
          mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 12);
          counter += 1;
          if (counter > 0) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
              mc.thePlayer.posX, mc.thePlayer.posY - 0.0D, mc.thePlayer.posZ, 
              mc.thePlayer.onGround));
            counter = 0;
          }
          int dist = 20; for (int index = 0; index < dist; index++) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
              mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-12D, mc.thePlayer.posZ, 
              mc.thePlayer.onGround));
          }
          mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, net.minecraft.util.BlockPos.ORIGIN, EnumFacing.DOWN));
          mc.playerController.onStoppedUsingItem(mc.thePlayer);
        }
      }
    }
    else if ((mc.thePlayer.onGround) && (canConsume()) && (mc.gameSettings.keyBindUseItem.pressed))
    {
      mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
      for (int i = 0; i < 20; i++) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
          mc.thePlayer.posY + 1.0E-9D, mc.thePlayer.posZ, true));
      }
      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, net.minecraft.util.BlockPos.ORIGIN, EnumFacing.DOWN));
    } else {
      mc.timer.timerSpeed = 1.0F;
    }
  }
  
  @com.enjoytheban.api.EventHandler
  public void onRecieve(EventPacketRecieve event)
  {
    if ((event.getPacket() instanceof S18PacketEntityTeleport)) {
      S18PacketEntityTeleport packet = (S18PacketEntityTeleport)event.getPacket();
      if (mc.thePlayer != null) {
        packet.setYaw((byte)(int)mc.thePlayer.rotationYaw);
      }
      packet.setPitch((byte)(int)mc.thePlayer.rotationPitch);
    }
  }
}
