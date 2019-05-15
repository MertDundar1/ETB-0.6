package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPacketSend;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;


public class Blink
  extends Module
{
  private EntityOtherPlayerMP blinkEntity;
  private List<Packet> packetList;
  
  public Blink()
  {
    super("Blink", new String[] { "blonk" }, ModuleType.Player);
    packetList = new ArrayList();
  }
  
  public void onEnable()
  {
    setColor(new Color(200, 100, 200).getRGB());
    if (mc.thePlayer == null) {
      return;
    }
    blinkEntity = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(new UUID(69L, 96L), "Blink"));
    blinkEntity.inventory = mc.thePlayer.inventory;
    blinkEntity.inventoryContainer = mc.thePlayer.inventoryContainer;
    blinkEntity.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, 
      mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
    blinkEntity.rotationYawHead = mc.thePlayer.rotationYawHead;
    mc.theWorld.addEntityToWorld(blinkEntity.getEntityId(), blinkEntity);
  }
  
  @EventHandler
  private void onPacketSend(EventPacketSend event) {
    if (((event.getPacket() instanceof C0BPacketEntityAction)) || ((event.getPacket() instanceof C03PacketPlayer)) || 
      ((event.getPacket() instanceof C02PacketUseEntity)) || 
      ((event.getPacket() instanceof C0APacketAnimation)) || 
      ((event.getPacket() instanceof C08PacketPlayerBlockPlacement))) {
      packetList.add(event.getPacket());
      event.setCancelled(true);
    }
  }
  
  public void onDisable()
  {
    for (Packet packet : packetList) {
      mc.getNetHandler().addToSendQueue(packet);
    }
    packetList.clear();
    mc.theWorld.removeEntityFromWorld(blinkEntity.getEntityId());
  }
}
