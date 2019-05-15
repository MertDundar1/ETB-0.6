package net.minecraft.client.renderer;

import java.util.ArrayDeque;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import optifine.Config;
import optifine.DynamicLights;

public class RegionRenderCache extends ChunkCache
{
  private static final IBlockState field_175632_f = net.minecraft.init.Blocks.air.getDefaultState();
  private final BlockPos field_175633_g;
  private int[] field_175634_h;
  private IBlockState[] field_175635_i;
  private static final String __OBFID = "CL_00002565";
  private static ArrayDeque<int[]> cacheLights = new ArrayDeque();
  private static ArrayDeque<IBlockState[]> cacheStates = new ArrayDeque();
  private static int maxCacheSize = Config.limit(Runtime.getRuntime().availableProcessors(), 1, 32);
  
  public RegionRenderCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn)
  {
    super(worldIn, posFromIn, posToIn, subIn);
    field_175633_g = posFromIn.subtract(new net.minecraft.util.Vec3i(subIn, subIn, subIn));
    boolean var5 = true;
    field_175634_h = allocateLights(8000);
    Arrays.fill(field_175634_h, -1);
    field_175635_i = allocateStates(8000);
  }
  
  public TileEntity getTileEntity(BlockPos pos)
  {
    int var2 = (pos.getX() >> 4) - chunkX;
    int var3 = (pos.getZ() >> 4) - chunkZ;
    return chunkArray[var2][var3].func_177424_a(pos, Chunk.EnumCreateEntityType.QUEUED);
  }
  
  public int getCombinedLight(BlockPos p_175626_1_, int p_175626_2_)
  {
    int var3 = func_175630_e(p_175626_1_);
    int var4 = field_175634_h[var3];
    
    if (var4 == -1)
    {
      var4 = super.getCombinedLight(p_175626_1_, p_175626_2_);
      
      if ((Config.isDynamicLights()) && (!getBlockState(p_175626_1_).getBlock().isOpaqueCube()))
      {
        var4 = DynamicLights.getCombinedLight(p_175626_1_, var4);
      }
      
      field_175634_h[var3] = var4;
    }
    
    return var4;
  }
  
  public IBlockState getBlockState(BlockPos pos)
  {
    int var2 = func_175630_e(pos);
    IBlockState var3 = field_175635_i[var2];
    
    if (var3 == null)
    {
      var3 = func_175631_c(pos);
      field_175635_i[var2] = var3;
    }
    
    return var3;
  }
  
  private IBlockState func_175631_c(BlockPos pos)
  {
    if ((pos.getY() >= 0) && (pos.getY() < 256))
    {
      int var2 = (pos.getX() >> 4) - chunkX;
      int var3 = (pos.getZ() >> 4) - chunkZ;
      return chunkArray[var2][var3].getBlockState(pos);
    }
    

    return field_175632_f;
  }
  

  private int func_175630_e(BlockPos p_175630_1_)
  {
    int var2 = p_175630_1_.getX() - field_175633_g.getX();
    int var3 = p_175630_1_.getY() - field_175633_g.getY();
    int var4 = p_175630_1_.getZ() - field_175633_g.getZ();
    return var2 * 400 + var4 * 20 + var3;
  }
  
  public void freeBuffers()
  {
    freeLights(field_175634_h);
    freeStates(field_175635_i);
  }
  
  private static int[] allocateLights(int size)
  {
    ArrayDeque var1 = cacheLights;
    
    synchronized (cacheLights)
    {
      int[] ints = (int[])cacheLights.pollLast();
      
      if ((ints == null) || (ints.length < size))
      {
        ints = new int[size];
      }
      
      return ints;
    }
  }
  
  public static void freeLights(int[] ints)
  {
    ArrayDeque var1 = cacheLights;
    
    synchronized (cacheLights)
    {
      if (cacheLights.size() < maxCacheSize)
      {
        cacheLights.add(ints);
      }
    }
  }
  
  private static IBlockState[] allocateStates(int size)
  {
    ArrayDeque var1 = cacheStates;
    
    synchronized (cacheStates)
    {
      IBlockState[] states = (IBlockState[])cacheStates.pollLast();
      
      if ((states != null) && (states.length >= size))
      {
        Arrays.fill(states, null);
      }
      else
      {
        states = new IBlockState[size];
      }
      
      return states;
    }
  }
  
  public static void freeStates(IBlockState[] states)
  {
    ArrayDeque var1 = cacheStates;
    
    synchronized (cacheStates)
    {
      if (cacheStates.size() < maxCacheSize)
      {
        cacheStates.add(states);
      }
    }
  }
}
