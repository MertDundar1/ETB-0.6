package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;

public class S07PacketRespawn implements Packet
{
  private int field_149088_a;
  private EnumDifficulty field_149086_b;
  private WorldSettings.GameType field_149087_c;
  private WorldType field_149085_d;
  private static final String __OBFID = "CL_00001322";
  
  public S07PacketRespawn() {}
  
  public S07PacketRespawn(int p_i45213_1_, EnumDifficulty p_i45213_2_, WorldType p_i45213_3_, WorldSettings.GameType p_i45213_4_)
  {
    field_149088_a = p_i45213_1_;
    field_149086_b = p_i45213_2_;
    field_149087_c = p_i45213_4_;
    field_149085_d = p_i45213_3_;
  }
  



  public void processPacket(INetHandlerPlayClient handler)
  {
    handler.handleRespawn(this);
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    field_149088_a = data.readInt();
    field_149086_b = EnumDifficulty.getDifficultyEnum(data.readUnsignedByte());
    field_149087_c = WorldSettings.GameType.getByID(data.readUnsignedByte());
    field_149085_d = WorldType.parseWorldType(data.readStringFromBuffer(16));
    
    if (field_149085_d == null)
    {
      field_149085_d = WorldType.DEFAULT;
    }
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeInt(field_149088_a);
    data.writeByte(field_149086_b.getDifficultyId());
    data.writeByte(field_149087_c.getID());
    data.writeString(field_149085_d.getWorldTypeName());
  }
  
  public int func_149082_c()
  {
    return field_149088_a;
  }
  
  public EnumDifficulty func_149081_d()
  {
    return field_149086_b;
  }
  
  public WorldSettings.GameType func_149083_e()
  {
    return field_149087_c;
  }
  
  public WorldType func_149080_f()
  {
    return field_149085_d;
  }
  



  public void processPacket(INetHandler handler)
  {
    processPacket((INetHandlerPlayClient)handler);
  }
}
