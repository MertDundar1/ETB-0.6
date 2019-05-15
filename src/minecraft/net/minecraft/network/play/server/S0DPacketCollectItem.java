package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S0DPacketCollectItem implements Packet
{
  private int field_149357_a;
  private int field_149356_b;
  private static final String __OBFID = "CL_00001339";
  
  public S0DPacketCollectItem() {}
  
  public S0DPacketCollectItem(int p_i45232_1_, int p_i45232_2_)
  {
    field_149357_a = p_i45232_1_;
    field_149356_b = p_i45232_2_;
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    field_149357_a = data.readVarIntFromBuffer();
    field_149356_b = data.readVarIntFromBuffer();
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeVarIntToBuffer(field_149357_a);
    data.writeVarIntToBuffer(field_149356_b);
  }
  



  public void processPacket(INetHandlerPlayClient handler)
  {
    handler.handleCollectItem(this);
  }
  
  public int func_149354_c()
  {
    return field_149357_a;
  }
  
  public int func_149353_d()
  {
    return field_149356_b;
  }
  



  public void processPacket(INetHandler handler)
  {
    processPacket((INetHandlerPlayClient)handler);
  }
}
