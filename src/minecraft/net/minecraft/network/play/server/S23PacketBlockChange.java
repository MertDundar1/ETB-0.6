package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.World;

public class S23PacketBlockChange implements net.minecraft.network.Packet
{
  private BlockPos field_179828_a;
  private IBlockState field_148883_d;
  private static final String __OBFID = "CL_00001287";
  
  public S23PacketBlockChange() {}
  
  public S23PacketBlockChange(World worldIn, BlockPos p_i45988_2_)
  {
    field_179828_a = p_i45988_2_;
    field_148883_d = worldIn.getBlockState(p_i45988_2_);
  }
  


  public void readPacketData(PacketBuffer data)
    throws IOException
  {
    field_179828_a = data.readBlockPos();
    field_148883_d = ((IBlockState)Block.BLOCK_STATE_IDS.getByValue(data.readVarIntFromBuffer()));
  }
  


  public void writePacketData(PacketBuffer data)
    throws IOException
  {
    data.writeBlockPos(field_179828_a);
    data.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(field_148883_d));
  }
  
  public void func_180727_a(INetHandlerPlayClient p_180727_1_)
  {
    p_180727_1_.handleBlockChange(this);
  }
  
  public IBlockState func_180728_a()
  {
    return field_148883_d;
  }
  
  public BlockPos func_179827_b()
  {
    return field_179828_a;
  }
  



  public void processPacket(INetHandler handler)
  {
    func_180727_a((INetHandlerPlayClient)handler);
  }
}
