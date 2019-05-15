package com.enjoytheban.api.events.world;

import com.enjoytheban.api.Event;
import net.minecraft.network.Packet;












public class EventPacketSend
  extends Event
{
  private Packet packet;
  
  public EventPacketSend(Packet packet)
  {
    this.packet = packet;
  }
  
  public Packet getPacket()
  {
    return packet;
  }
  
  public void setPacket(Packet packet)
  {
    this.packet = packet;
  }
}
