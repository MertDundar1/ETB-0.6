package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C09PacketHeldItemChange implements Packet
{
  private int slotId;
  private static final String __OBFID = "CL_00001368";
  
  public C09PacketHeldItemChange() {}
  
  public C09PacketHeldItemChange(int p_i45262_1_)
  {
    slotId = p_i45262_1_;
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    slotId = data.readShort();
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeShort(slotId);
  }
  



  public void processPacket(INetHandlerPlayServer handler)
  {
    handler.processHeldItemChange(this);
  }
  
  public int getSlotId()
  {
    return slotId;
  }
  



  public void processPacket(INetHandler handler)
  {
    processPacket((INetHandlerPlayServer)handler);
  }
}
