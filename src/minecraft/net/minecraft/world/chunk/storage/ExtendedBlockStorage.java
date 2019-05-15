package net.minecraft.world.chunk.storage;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.world.chunk.NibbleArray;
import optifine.Reflector;
import optifine.ReflectorClass;















public class ExtendedBlockStorage
{
  private int yBase;
  private int blockRefCount;
  private int tickRefCount;
  private char[] data;
  private NibbleArray blocklightArray;
  private NibbleArray skylightArray;
  private static final String __OBFID = "CL_00000375";
  
  public ExtendedBlockStorage(int y, boolean storeSkylight)
  {
    yBase = y;
    data = new char['က'];
    blocklightArray = new NibbleArray();
    
    if (storeSkylight)
    {
      skylightArray = new NibbleArray();
    }
  }
  
  public IBlockState get(int x, int y, int z)
  {
    IBlockState var4 = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(data[(y << 8 | z << 4 | x)]);
    return var4 != null ? var4 : Blocks.air.getDefaultState();
  }
  
  public void set(int x, int y, int z, IBlockState state)
  {
    if (Reflector.IExtendedBlockState.isInstance(state))
    {
      state = (IBlockState)Reflector.call(state, Reflector.IExtendedBlockState_getClean, new Object[0]);
    }
    
    IBlockState var5 = get(x, y, z);
    Block var6 = var5.getBlock();
    Block var7 = state.getBlock();
    
    if (var6 != Blocks.air)
    {
      blockRefCount -= 1;
      
      if (var6.getTickRandomly())
      {
        tickRefCount -= 1;
      }
    }
    
    if (var7 != Blocks.air)
    {
      blockRefCount += 1;
      
      if (var7.getTickRandomly())
      {
        tickRefCount += 1;
      }
    }
    
    data[(y << 8 | z << 4 | x)] = ((char)Block.BLOCK_STATE_IDS.get(state));
  }
  




  public Block getBlockByExtId(int x, int y, int z)
  {
    return get(x, y, z).getBlock();
  }
  



  public int getExtBlockMetadata(int x, int y, int z)
  {
    IBlockState var4 = get(x, y, z);
    return var4.getBlock().getMetaFromState(var4);
  }
  



  public boolean isEmpty()
  {
    return blockRefCount == 0;
  }
  




  public boolean getNeedsRandomTick()
  {
    return tickRefCount > 0;
  }
  



  public int getYLocation()
  {
    return yBase;
  }
  



  public void setExtSkylightValue(int x, int y, int z, int value)
  {
    skylightArray.set(x, y, z, value);
  }
  



  public int getExtSkylightValue(int x, int y, int z)
  {
    return skylightArray.get(x, y, z);
  }
  



  public void setExtBlocklightValue(int x, int y, int z, int value)
  {
    blocklightArray.set(x, y, z, value);
  }
  



  public int getExtBlocklightValue(int x, int y, int z)
  {
    return blocklightArray.get(x, y, z);
  }
  
  public void removeInvalidBlocks()
  {
    List blockStates = Block.BLOCK_STATE_IDS.getObjectList();
    int maxStateId = blockStates.size();
    int localBlockRefCount = 0;
    int localTickRefCount = 0;
    
    for (int y = 0; y < 16; y++)
    {
      int by = y << 8;
      
      for (int z = 0; z < 16; z++)
      {
        int byz = by | z << 4;
        
        for (int x = 0; x < 16; x++)
        {
          char stateId = data[(byz | x)];
          
          if (stateId > 0)
          {
            localBlockRefCount++;
            
            if (stateId < maxStateId)
            {
              IBlockState bs = (IBlockState)blockStates.get(stateId);
              
              if (bs != null)
              {
                Block var4 = bs.getBlock();
                
                if (var4.getTickRandomly())
                {
                  localTickRefCount++;
                }
              }
            }
          }
        }
      }
    }
    
    blockRefCount = localBlockRefCount;
    tickRefCount = localTickRefCount;
  }
  
  public char[] getData()
  {
    return data;
  }
  
  public void setData(char[] dataArray)
  {
    data = dataArray;
  }
  



  public NibbleArray getBlocklightArray()
  {
    return blocklightArray;
  }
  



  public NibbleArray getSkylightArray()
  {
    return skylightArray;
  }
  



  public void setBlocklightArray(NibbleArray newBlocklightArray)
  {
    blocklightArray = newBlocklightArray;
  }
  



  public void setSkylightArray(NibbleArray newSkylightArray)
  {
    skylightArray = newSkylightArray;
  }
}
