package com.enjoytheban.module.modules.player;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.misc.EventCollideWithBlock;
import com.enjoytheban.api.events.world.EventPacketRecieve;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;


public class Freecam
  extends Module
{
  private EntityOtherPlayerMP copy;
  private double x;
  private double y;
  private double z;
  
  public Freecam()
  {
    super("Freecam", new String[] { "outofbody" }, ModuleType.Player);
    setColor(new Color(221, 214, 51).getRGB());
  }
  



  public void onEnable()
  {
    (this.copy = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile())).clonePlayer(mc.thePlayer, 
      true);
    copy.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, 
      mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
    copy.rotationYawHead = mc.thePlayer.rotationYawHead;
    copy.setEntityId(64199);
    copy.setSneaking(mc.thePlayer.isSneaking());
    mc.theWorld.addEntityToWorld(copy.getEntityId(), copy);
    x = mc.thePlayer.posX;
    y = mc.thePlayer.posY;
    z = mc.thePlayer.posZ;
  }
  
  @EventHandler
  private void onPreMotion(EventPreUpdate e) {
    mc.thePlayer.capabilities.isFlying = true;
    mc.thePlayer.noClip = true;
    mc.thePlayer.capabilities.setFlySpeed(0.1F);
    e.setCancelled(true);
  }
  
  @EventHandler
  private void onPacketSend(EventPacketRecieve e)
  {
    if ((e.getPacket() instanceof C03PacketPlayer)) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  private void onBB(EventCollideWithBlock e) {
    e.setBoundingBox(null);
  }
  

  public void onDisable()
  {
    mc.thePlayer.setSpeed(0.0D);
    mc.thePlayer.setLocationAndAngles(copy.posX, copy.posY, copy.posZ, copy.rotationYaw, 
      copy.rotationPitch);
    mc.thePlayer.rotationYawHead = copy.rotationYawHead;
    mc.theWorld.removeEntityFromWorld(copy.getEntityId());
    mc.thePlayer.setSneaking(copy.isSneaking());
    copy = null;
    mc.renderGlobal.loadRenderers();
    mc.thePlayer.setPosition(x, y, z);
    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 
      mc.thePlayer.posY + 0.01D, mc.thePlayer.posZ, mc.thePlayer.onGround));
    mc.thePlayer.capabilities.isFlying = false;
    mc.thePlayer.noClip = false;
    mc.theWorld.removeEntityFromWorld(-1);
  }
}
