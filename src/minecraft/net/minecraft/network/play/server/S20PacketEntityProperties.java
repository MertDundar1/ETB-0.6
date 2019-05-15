package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S20PacketEntityProperties implements Packet
{
  private int field_149445_a;
  private final List field_149444_b = Lists.newArrayList();
  private static final String __OBFID = "CL_00001341";
  
  public S20PacketEntityProperties() {}
  
  public S20PacketEntityProperties(int p_i45236_1_, Collection p_i45236_2_)
  {
    field_149445_a = p_i45236_1_;
    Iterator var3 = p_i45236_2_.iterator();
    
    while (var3.hasNext())
    {
      IAttributeInstance var4 = (IAttributeInstance)var3.next();
      field_149444_b.add(new Snapshot(var4.getAttribute().getAttributeUnlocalizedName(), var4.getBaseValue(), var4.func_111122_c()));
    }
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    field_149445_a = data.readVarIntFromBuffer();
    int var2 = data.readInt();
    
    for (int var3 = 0; var3 < var2; var3++)
    {
      String var4 = data.readStringFromBuffer(64);
      double var5 = data.readDouble();
      ArrayList var7 = Lists.newArrayList();
      int var8 = data.readVarIntFromBuffer();
      
      for (int var9 = 0; var9 < var8; var9++)
      {
        java.util.UUID var10 = data.readUuid();
        var7.add(new AttributeModifier(var10, "Unknown synced attribute modifier", data.readDouble(), data.readByte()));
      }
      
      field_149444_b.add(new Snapshot(var4, var5, var7));
    }
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeVarIntToBuffer(field_149445_a);
    data.writeInt(field_149444_b.size());
    Iterator var2 = field_149444_b.iterator();
    Iterator var4;
    for (; var2.hasNext(); 
        






        var4.hasNext())
    {
      Snapshot var3 = (Snapshot)var2.next();
      data.writeString(var3.func_151409_a());
      data.writeDouble(var3.func_151410_b());
      data.writeVarIntToBuffer(var3.func_151408_c().size());
      var4 = var3.func_151408_c().iterator();
      
      continue;
      
      AttributeModifier var5 = (AttributeModifier)var4.next();
      data.writeUuid(var5.getID());
      data.writeDouble(var5.getAmount());
      data.writeByte(var5.getOperation());
    }
  }
  

  public void func_180754_a(INetHandlerPlayClient p_180754_1_)
  {
    p_180754_1_.handleEntityProperties(this);
  }
  
  public int func_149442_c()
  {
    return field_149445_a;
  }
  
  public List func_149441_d()
  {
    return field_149444_b;
  }
  



  public void processPacket(INetHandler handler)
  {
    func_180754_a((INetHandlerPlayClient)handler);
  }
  
  public class Snapshot
  {
    private final String field_151412_b;
    private final double field_151413_c;
    private final Collection field_151411_d;
    private static final String __OBFID = "CL_00001342";
    
    public Snapshot(String p_i45235_2_, double p_i45235_3_, Collection p_i45235_5_)
    {
      field_151412_b = p_i45235_2_;
      field_151413_c = p_i45235_3_;
      field_151411_d = p_i45235_5_;
    }
    
    public String func_151409_a()
    {
      return field_151412_b;
    }
    
    public double func_151410_b()
    {
      return field_151413_c;
    }
    
    public Collection func_151408_c()
    {
      return field_151411_d;
    }
  }
}
