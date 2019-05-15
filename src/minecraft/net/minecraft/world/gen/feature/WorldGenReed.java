package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator
{
  private static final String __OBFID = "CL_00000429";
  
  public WorldGenReed() {}
  
  public boolean generate(World worldIn, Random p_180709_2_, BlockPos p_180709_3_)
  {
    for (int var4 = 0; var4 < 20; var4++)
    {
      BlockPos var5 = p_180709_3_.add(p_180709_2_.nextInt(4) - p_180709_2_.nextInt(4), 0, p_180709_2_.nextInt(4) - p_180709_2_.nextInt(4));
      
      if (worldIn.isAirBlock(var5))
      {
        BlockPos var6 = var5.offsetDown();
        
        if ((worldIn.getBlockState(var6.offsetWest()).getBlock().getMaterial() == net.minecraft.block.material.Material.water) || (worldIn.getBlockState(var6.offsetEast()).getBlock().getMaterial() == net.minecraft.block.material.Material.water) || (worldIn.getBlockState(var6.offsetNorth()).getBlock().getMaterial() == net.minecraft.block.material.Material.water) || (worldIn.getBlockState(var6.offsetSouth()).getBlock().getMaterial() == net.minecraft.block.material.Material.water))
        {
          int var7 = 2 + p_180709_2_.nextInt(p_180709_2_.nextInt(3) + 1);
          
          for (int var8 = 0; var8 < var7; var8++)
          {
            if (net.minecraft.init.Blocks.reeds.func_176354_d(worldIn, var5))
            {
              worldIn.setBlockState(var5.offsetUp(var8), net.minecraft.init.Blocks.reeds.getDefaultState(), 2);
            }
          }
        }
      }
    }
    
    return true;
  }
}
