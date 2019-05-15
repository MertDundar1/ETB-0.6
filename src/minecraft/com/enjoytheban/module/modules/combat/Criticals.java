package com.enjoytheban.module.modules.combat;

import com.enjoytheban.Client;
import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.world.EventPacketSend;
import com.enjoytheban.api.events.world.EventPreUpdate;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.management.ModuleManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.module.modules.movement.Speed;
import com.enjoytheban.utils.Helper;
import com.enjoytheban.utils.TimerUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;


public class Criticals
  extends Module
{
  private Mode mode = new Mode("Mode", "mode", CritMode.values(), CritMode.Packet);
  
  private TimerUtil timer = new TimerUtil();
  
  public Criticals()
  {
    super("Criticals", new String[] { "crits", "crit" }, ModuleType.Combat);
    setColor(new Color(235, 194, 138).getRGB());
    addValues(new Value[] { mode });
  }
  
  @EventHandler
  private void onUpdate(EventPreUpdate e) {
    setSuffix(mode.getValue());
  }
  
  private boolean canCrit()
  {
    return (mc.thePlayer.onGround) && (!mc.thePlayer.isInWater()) && (!Client.instance.getModuleManager().getModuleByClass(Speed.class).isEnabled());
  }
  
  @EventHandler
  private void onPacket(EventPacketSend e)
  {
    if (((e.getPacket() instanceof C02PacketUseEntity)) && (canCrit()) && (mode.getValue() == CritMode.Minijumps)) {
      mc.thePlayer.motionY = 0.2D;
    }
  }
  

  void packetCrit()
  {
    if ((timer.hasReached(Helper.onServer("hypixel") ? 500 : 10)) && (mode.getValue() == CritMode.Packet) && (canCrit()))
    {
      double[] offsets = { 0.0625D, 0.0D, 1.0E-4D, 0.0D };
      
      for (int i = 0; i < offsets.length; i++) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offsets[i], mc.thePlayer.posZ, false));
      }
      
      timer.reset();
    }
  }
  
  void offsetCrit()
  {
    if ((canCrit()) && (!mc.getCurrentServerData().serverIP.contains("hypixel"))) {
      double[] offsets = { 0.0624D, 0.0D, 1.0E-4D, 0.0D };
      for (int i = 0; i < offsets.length; i++) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offsets[i], mc.thePlayer.posZ, false));
      }
    }
  }
  
  public void hypixelCrit() {
    if ((mode.getValue() == CritMode.Hypixel) && 
      (canCrit())) {
      for (double offset : new double[] { 0.06142999976873398D, 0.0D, 0.012511000037193298D, 0.0D }) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, true));
      }
    }
  }
  
  static enum CritMode
  {
    Packet,  Hypixel,  Minijumps;
  }
}
