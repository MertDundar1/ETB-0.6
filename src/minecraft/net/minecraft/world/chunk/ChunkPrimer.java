package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ObjectIntIdentityMap;

public class ChunkPrimer
{
  private final short[] data = new short[65536];
  private final IBlockState defaultState;
  private static final String __OBFID = "CL_00002007";
  
  public ChunkPrimer()
  {
    defaultState = net.minecraft.init.Blocks.air.getDefaultState();
  }
  
  public IBlockState getBlockState(int x, int y, int z)
  {
    int var4 = x << 12 | z << 8 | y;
    return getBlockState(var4);
  }
  
  public IBlockState getBlockState(int index)
  {
    if ((index >= 0) && (index < data.length))
    {
      IBlockState var2 = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(data[index]);
      return var2 != null ? var2 : defaultState;
    }
    

    throw new IndexOutOfBoundsException("The coordinate is out of range");
  }
  

  public void setBlockState(int x, int y, int z, IBlockState state)
  {
    int var5 = x << 12 | z << 8 | y;
    setBlockState(var5, state);
  }
  
  public void setBlockState(int index, IBlockState state)
  {
    if ((index >= 0) && (index < data.length))
    {
      data[index] = ((short)Block.BLOCK_STATE_IDS.get(state));
    }
    else
    {
      throw new IndexOutOfBoundsException("The coordinate is out of range");
    }
  }
}
