package net.minecraft.network.status.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class S01PacketPong implements Packet
{
  private long clientTime;
  private static final String __OBFID = "CL_00001383";
  
  public S01PacketPong() {}
  
  public S01PacketPong(long time)
  {
    clientTime = time;
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    clientTime = data.readLong();
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeLong(clientTime);
  }
  



  public void processPacket(INetHandlerStatusClient handler)
  {
    handler.handlePong(this);
  }
  



  public void processPacket(INetHandler handler)
  {
    processPacket((INetHandlerStatusClient)handler);
  }
}
