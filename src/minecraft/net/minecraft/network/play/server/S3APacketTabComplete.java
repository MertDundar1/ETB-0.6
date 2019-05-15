package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3APacketTabComplete implements Packet
{
  private String[] field_149632_a;
  private static final String __OBFID = "CL_00001288";
  
  public S3APacketTabComplete() {}
  
  public S3APacketTabComplete(String[] p_i45178_1_)
  {
    field_149632_a = p_i45178_1_;
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    field_149632_a = new String[data.readVarIntFromBuffer()];
    
    for (int var2 = 0; var2 < field_149632_a.length; var2++)
    {
      field_149632_a[var2] = data.readStringFromBuffer(32767);
    }
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeVarIntToBuffer(field_149632_a.length);
    String[] var2 = field_149632_a;
    int var3 = var2.length;
    
    for (int var4 = 0; var4 < var3; var4++)
    {
      String var5 = var2[var4];
      data.writeString(var5);
    }
  }
  



  public void processPacket(INetHandlerPlayClient handler)
  {
    handler.handleTabComplete(this);
  }
  
  public String[] func_149630_c()
  {
    return field_149632_a;
  }
  



  public void processPacket(INetHandler handler)
  {
    processPacket((INetHandlerPlayClient)handler);
  }
}
