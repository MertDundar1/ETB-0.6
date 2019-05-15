package net.minecraft.network;

import java.io.IOException;

public abstract interface Packet
{
  public abstract void readPacketData(PacketBuffer paramPacketBuffer)
    throws IOException;
  
  public abstract void writePacketData(PacketBuffer paramPacketBuffer)
    throws IOException;
  
  public abstract void processPacket(INetHandler paramINetHandler);
}
