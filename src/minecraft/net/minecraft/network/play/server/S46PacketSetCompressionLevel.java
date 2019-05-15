package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S46PacketSetCompressionLevel
  implements Packet
{
  private int field_179761_a;
  private static final String __OBFID = "CL_00002300";
  
  public S46PacketSetCompressionLevel() {}
  
  public void readPacketData(PacketBuffer data) throws IOException
  {
    field_179761_a = data.readVarIntFromBuffer();
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeVarIntToBuffer(field_179761_a);
  }
  
  public void func_179759_a(INetHandlerPlayClient p_179759_1_)
  {
    p_179759_1_.func_175100_a(this);
  }
  
  public int func_179760_a()
  {
    return field_179761_a;
  }
  



  public void processPacket(INetHandler handler)
  {
    func_179759_a((INetHandlerPlayClient)handler);
  }
}
