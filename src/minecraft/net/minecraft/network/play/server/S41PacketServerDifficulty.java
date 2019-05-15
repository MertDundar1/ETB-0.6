package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;

public class S41PacketServerDifficulty implements Packet
{
  private EnumDifficulty field_179833_a;
  private boolean field_179832_b;
  private static final String __OBFID = "CL_00002303";
  
  public S41PacketServerDifficulty() {}
  
  public S41PacketServerDifficulty(EnumDifficulty p_i45987_1_, boolean p_i45987_2_)
  {
    field_179833_a = p_i45987_1_;
    field_179832_b = p_i45987_2_;
  }
  
  public void func_179829_a(INetHandlerPlayClient p_179829_1_)
  {
    p_179829_1_.func_175101_a(this);
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    field_179833_a = EnumDifficulty.getDifficultyEnum(data.readUnsignedByte());
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeByte(field_179833_a.getDifficultyId());
  }
  
  public boolean func_179830_a()
  {
    return field_179832_b;
  }
  
  public EnumDifficulty func_179831_b()
  {
    return field_179833_a;
  }
  



  public void processPacket(INetHandler handler)
  {
    func_179829_a((INetHandlerPlayClient)handler);
  }
}
