package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0BPacketEntityAction implements Packet
{
  private int field_149517_a;
  private Action field_149515_b;
  private int field_149516_c;
  private static final String __OBFID = "CL_00001366";
  
  public C0BPacketEntityAction() {}
  
  public C0BPacketEntityAction(Entity p_i45937_1_, Action p_i45937_2_)
  {
    this(p_i45937_1_, p_i45937_2_, 0);
  }
  
  public C0BPacketEntityAction(Entity p_i45938_1_, Action p_i45938_2_, int p_i45938_3_)
  {
    field_149517_a = p_i45938_1_.getEntityId();
    field_149515_b = p_i45938_2_;
    field_149516_c = p_i45938_3_;
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    field_149517_a = data.readVarIntFromBuffer();
    field_149515_b = ((Action)data.readEnumValue(Action.class));
    field_149516_c = data.readVarIntFromBuffer();
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeVarIntToBuffer(field_149517_a);
    data.writeEnumValue(field_149515_b);
    data.writeVarIntToBuffer(field_149516_c);
  }
  
  public void func_180765_a(INetHandlerPlayServer p_180765_1_)
  {
    p_180765_1_.processEntityAction(this);
  }
  
  public Action func_180764_b()
  {
    return field_149515_b;
  }
  
  public int func_149512_e()
  {
    return field_149516_c;
  }
  



  public void processPacket(INetHandler handler)
  {
    func_180765_a((INetHandlerPlayServer)handler);
  }
  
  public static enum Action
  {
    START_SNEAKING("START_SNEAKING", 0), 
    STOP_SNEAKING("STOP_SNEAKING", 1), 
    STOP_SLEEPING("STOP_SLEEPING", 2), 
    START_SPRINTING("START_SPRINTING", 3), 
    STOP_SPRINTING("STOP_SPRINTING", 4), 
    RIDING_JUMP("RIDING_JUMP", 5), 
    OPEN_INVENTORY("OPEN_INVENTORY", 6);
    
    private static final Action[] $VALUES = { START_SNEAKING, STOP_SNEAKING, STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, RIDING_JUMP, OPEN_INVENTORY };
    private static final String __OBFID = "CL_00002283";
    
    private Action(String p_i45936_1_, int p_i45936_2_) {}
  }
}
