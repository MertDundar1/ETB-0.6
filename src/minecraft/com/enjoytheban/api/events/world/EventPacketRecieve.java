package com.enjoytheban.api.events.world;

import com.enjoytheban.api.Event;
import net.minecraft.network.Packet;








public class EventPacketRecieve
  extends Event
{
  private Packet packet;
  
  public EventPacketRecieve(Packet packet)
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
