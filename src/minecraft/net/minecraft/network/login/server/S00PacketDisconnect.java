package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.IChatComponent;

public class S00PacketDisconnect implements Packet
{
  private IChatComponent reason;
  private static final String __OBFID = "CL_00001377";
  
  public S00PacketDisconnect() {}
  
  public S00PacketDisconnect(IChatComponent reasonIn)
  {
    reason = reasonIn;
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    reason = data.readChatComponent();
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeChatComponent(reason);
  }
  
  public void func_180772_a(INetHandlerLoginClient p_180772_1_)
  {
    p_180772_1_.handleDisconnect(this);
  }
  
  public IChatComponent func_149603_c()
  {
    return reason;
  }
  



  public void processPacket(INetHandler handler)
  {
    func_180772_a((INetHandlerLoginClient)handler);
  }
}
