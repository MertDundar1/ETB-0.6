package com.enjoytheban.module.modules.world;

import com.enjoytheban.api.events.world.EventPacketSend;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.TimerUtil;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import optifine.MathUtils;

public class PingSpoof extends Module
{
  private List<Packet> packetList = new CopyOnWriteArrayList();
  private TimerUtil timer = new TimerUtil();
  
  public PingSpoof() {
    super("PingSpoof", new String[] { "spoofping", "ping" }, ModuleType.World);
    setColor(new Color(117, 52, 203).getRGB());
  }
  
  @com.enjoytheban.api.EventHandler
  private void onPacketSend(EventPacketSend e) {
    if (((e.getPacket() instanceof C00PacketKeepAlive)) && (mc.thePlayer.isEntityAlive())) {
      packetList.add(e.getPacket());
      e.setCancelled(true);
    }
    if (timer.hasReached(750.0D)) {
      if (!packetList.isEmpty()) {
        int i = 0;
        double totalPackets = MathUtils.getIncremental(Math.random() * 10.0D, 1.0D);
        for (Packet packet : packetList)
          if (i < totalPackets)
          {
            i++;
            mc.getNetHandler().getNetworkManager().sendPacket(packet);
            packetList.remove(packet);
          }
      }
      mc.getNetHandler().getNetworkManager().sendPacket(new C00PacketKeepAlive(10000));
      timer.reset();
    }
  }
}
