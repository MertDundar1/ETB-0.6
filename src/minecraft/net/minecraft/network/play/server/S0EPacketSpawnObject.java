package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0EPacketSpawnObject implements Packet
{
  private int field_149018_a;
  private int field_149016_b;
  private int field_149017_c;
  private int field_149014_d;
  private int field_149015_e;
  private int field_149012_f;
  private int field_149013_g;
  private int field_149021_h;
  private int field_149022_i;
  private int field_149019_j;
  private int field_149020_k;
  private static final String __OBFID = "CL_00001276";
  
  public S0EPacketSpawnObject() {}
  
  public S0EPacketSpawnObject(Entity p_i45165_1_, int p_i45165_2_)
  {
    this(p_i45165_1_, p_i45165_2_, 0);
  }
  
  public S0EPacketSpawnObject(Entity p_i45166_1_, int p_i45166_2_, int p_i45166_3_)
  {
    field_149018_a = p_i45166_1_.getEntityId();
    field_149016_b = MathHelper.floor_double(posX * 32.0D);
    field_149017_c = MathHelper.floor_double(posY * 32.0D);
    field_149014_d = MathHelper.floor_double(posZ * 32.0D);
    field_149021_h = MathHelper.floor_float(rotationPitch * 256.0F / 360.0F);
    field_149022_i = MathHelper.floor_float(rotationYaw * 256.0F / 360.0F);
    field_149019_j = p_i45166_2_;
    field_149020_k = p_i45166_3_;
    
    if (p_i45166_3_ > 0)
    {
      double var4 = motionX;
      double var6 = motionY;
      double var8 = motionZ;
      double var10 = 3.9D;
      
      if (var4 < -var10)
      {
        var4 = -var10;
      }
      
      if (var6 < -var10)
      {
        var6 = -var10;
      }
      
      if (var8 < -var10)
      {
        var8 = -var10;
      }
      
      if (var4 > var10)
      {
        var4 = var10;
      }
      
      if (var6 > var10)
      {
        var6 = var10;
      }
      
      if (var8 > var10)
      {
        var8 = var10;
      }
      
      field_149015_e = ((int)(var4 * 8000.0D));
      field_149012_f = ((int)(var6 * 8000.0D));
      field_149013_g = ((int)(var8 * 8000.0D));
    }
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    field_149018_a = data.readVarIntFromBuffer();
    field_149019_j = data.readByte();
    field_149016_b = data.readInt();
    field_149017_c = data.readInt();
    field_149014_d = data.readInt();
    field_149021_h = data.readByte();
    field_149022_i = data.readByte();
    field_149020_k = data.readInt();
    
    if (field_149020_k > 0)
    {
      field_149015_e = data.readShort();
      field_149012_f = data.readShort();
      field_149013_g = data.readShort();
    }
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeVarIntToBuffer(field_149018_a);
    data.writeByte(field_149019_j);
    data.writeInt(field_149016_b);
    data.writeInt(field_149017_c);
    data.writeInt(field_149014_d);
    data.writeByte(field_149021_h);
    data.writeByte(field_149022_i);
    data.writeInt(field_149020_k);
    
    if (field_149020_k > 0)
    {
      data.writeShort(field_149015_e);
      data.writeShort(field_149012_f);
      data.writeShort(field_149013_g);
    }
  }
  



  public void processPacket(INetHandlerPlayClient handler)
  {
    handler.handleSpawnObject(this);
  }
  
  public int func_149001_c()
  {
    return field_149018_a;
  }
  
  public int func_148997_d()
  {
    return field_149016_b;
  }
  
  public int func_148998_e()
  {
    return field_149017_c;
  }
  
  public int func_148994_f()
  {
    return field_149014_d;
  }
  
  public int func_149010_g()
  {
    return field_149015_e;
  }
  
  public int func_149004_h()
  {
    return field_149012_f;
  }
  
  public int func_148999_i()
  {
    return field_149013_g;
  }
  
  public int func_149008_j()
  {
    return field_149021_h;
  }
  
  public int func_149006_k()
  {
    return field_149022_i;
  }
  
  public int func_148993_l()
  {
    return field_149019_j;
  }
  
  public int func_149009_m()
  {
    return field_149020_k;
  }
  
  public void func_148996_a(int p_148996_1_)
  {
    field_149016_b = p_148996_1_;
  }
  
  public void func_148995_b(int p_148995_1_)
  {
    field_149017_c = p_148995_1_;
  }
  
  public void func_149005_c(int p_149005_1_)
  {
    field_149014_d = p_149005_1_;
  }
  
  public void func_149003_d(int p_149003_1_)
  {
    field_149015_e = p_149003_1_;
  }
  
  public void func_149000_e(int p_149000_1_)
  {
    field_149012_f = p_149000_1_;
  }
  
  public void func_149007_f(int p_149007_1_)
  {
    field_149013_g = p_149007_1_;
  }
  
  public void func_149002_g(int p_149002_1_)
  {
    field_149020_k = p_149002_1_;
  }
  



  public void processPacket(INetHandler handler)
  {
    processPacket((INetHandlerPlayClient)handler);
  }
}
