package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C01PacketPing implements Packet
{
  private long clientTime;
  private static final String __OBFID = "CL_00001392";
  
  public C01PacketPing() {}
  
  public C01PacketPing(long p_i45276_1_)
  {
    clientTime = p_i45276_1_;
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
  
  public void func_180774_a(INetHandlerStatusServer p_180774_1_)
  {
    p_180774_1_.processPing(this);
  }
  
  public long getClientTime()
  {
    return clientTime;
  }
  



  public void processPacket(INetHandler handler)
  {
    func_180774_a((INetHandlerStatusServer)handler);
  }
}
