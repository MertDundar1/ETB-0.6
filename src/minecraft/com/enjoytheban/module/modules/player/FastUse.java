package com.enjoytheban.module.modules.player;

import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Option;
import com.enjoytheban.module.Module;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class FastUse extends Module
{
  private Option<Boolean> guardian = new Option("Guardian", "guardian", Boolean.valueOf(true));
  
  public FastUse() {
    super("FastUse", new String[] { "fasteat", "fuse" }, com.enjoytheban.module.ModuleType.Player);
    addValues(new com.enjoytheban.api.value.Value[] { guardian });
  }
  
  private boolean canConsume() {
    return ((mc.thePlayer.getCurrentEquippedItem() != null) && 
      ((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof net.minecraft.item.ItemPotion))) || 
      ((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof net.minecraft.item.ItemFood));
  }
  
  @com.enjoytheban.api.EventHandler
  private void onUpdate(EventPreUpdate e) {
    setColor(new Color(100, 200, 200).getRGB());
    if (((Boolean)guardian.getValue()).booleanValue()) {
      if ((mc.thePlayer.onGround) && 
        (mc.thePlayer.getItemInUseDuration() == 1) && (getMinecraftgameSettings.keyBindUseItem.pressed) && 
        (!(mc.thePlayer.getItemInUse().getItem() instanceof ItemBow)) && 
        (!(mc.thePlayer.getItemInUse().getItem() instanceof ItemSword))) {
        for (int i = 0; i < 40; i++) {
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(
            mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
          if ((((Boolean)guardian.getValue()).booleanValue()) && 
            (mc.thePlayer.ticksExisted % 2 == 0)) {
            mc.thePlayer.sendQueue.addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition(
              mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ, false));
          }
        }
        
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
          C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      }
      
    }
    else if ((mc.thePlayer.onGround) && 
      (mc.thePlayer.getItemInUseDuration() == 16) && (getMinecraftgameSettings.keyBindUseItem.pressed) && 
      (!(mc.thePlayer.getItemInUse().getItem() instanceof ItemBow)) && 
      (!(mc.thePlayer.getItemInUse().getItem() instanceof ItemSword))) {
      for (int i = 0; i < 17; i++) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(
          mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
      }
      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }
  }
}
