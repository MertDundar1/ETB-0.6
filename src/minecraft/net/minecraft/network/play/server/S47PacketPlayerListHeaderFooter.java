package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S47PacketPlayerListHeaderFooter implements Packet
{
  private IChatComponent field_179703_a;
  private IChatComponent field_179702_b;
  private static final String __OBFID = "CL_00002285";
  
  public S47PacketPlayerListHeaderFooter() {}
  
  public S47PacketPlayerListHeaderFooter(IChatComponent p_i45950_1_)
  {
    field_179703_a = p_i45950_1_;
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    field_179703_a = data.readChatComponent();
    field_179702_b = data.readChatComponent();
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeChatComponent(field_179703_a);
    data.writeChatComponent(field_179702_b);
  }
  
  public void func_179699_a(INetHandlerPlayClient p_179699_1_)
  {
    p_179699_1_.func_175096_a(this);
  }
  
  public IChatComponent func_179700_a()
  {
    return field_179703_a;
  }
  
  public IChatComponent func_179701_b()
  {
    return field_179702_b;
  }
  



  public void processPacket(INetHandler handler)
  {
    func_179699_a((INetHandlerPlayClient)handler);
  }
}
