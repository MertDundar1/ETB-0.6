package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator
{
  private static final String __OBFID = "CL_00000417";
  
  public WorldGenIceSpike() {}
  
  public boolean generate(World worldIn, Random p_180709_2_, BlockPos p_180709_3_)
  {
    while ((worldIn.isAirBlock(p_180709_3_)) && (p_180709_3_.getY() > 2))
    {
      p_180709_3_ = p_180709_3_.offsetDown();
    }
    
    if (worldIn.getBlockState(p_180709_3_).getBlock() != Blocks.snow)
    {
      return false;
    }
    

    p_180709_3_ = p_180709_3_.offsetUp(p_180709_2_.nextInt(4));
    int var4 = p_180709_2_.nextInt(4) + 7;
    int var5 = var4 / 4 + p_180709_2_.nextInt(2);
    
    if ((var5 > 1) && (p_180709_2_.nextInt(60) == 0))
    {
      p_180709_3_ = p_180709_3_.offsetUp(10 + p_180709_2_.nextInt(30));
    }
    



    for (int var6 = 0; var6 < var4; var6++)
    {
      float var7 = (1.0F - var6 / var4) * var5;
      int var8 = net.minecraft.util.MathHelper.ceiling_float_int(var7);
      
      for (int var9 = -var8; var9 <= var8; var9++)
      {
        float var10 = net.minecraft.util.MathHelper.abs_int(var9) - 0.25F;
        
        for (int var11 = -var8; var11 <= var8; var11++)
        {
          float var12 = net.minecraft.util.MathHelper.abs_int(var11) - 0.25F;
          
          if (((var9 == 0) && (var11 == 0)) || ((var10 * var10 + var12 * var12 <= var7 * var7) && (((var9 != -var8) && (var9 != var8) && (var11 != -var8) && (var11 != var8)) || (p_180709_2_.nextFloat() <= 0.75F))))
          {
            Block var13 = worldIn.getBlockState(p_180709_3_.add(var9, var6, var11)).getBlock();
            
            if ((var13.getMaterial() == net.minecraft.block.material.Material.air) || (var13 == Blocks.dirt) || (var13 == Blocks.snow) || (var13 == Blocks.ice))
            {
              func_175906_a(worldIn, p_180709_3_.add(var9, var6, var11), Blocks.packed_ice);
            }
            
            if ((var6 != 0) && (var8 > 1))
            {
              var13 = worldIn.getBlockState(p_180709_3_.add(var9, -var6, var11)).getBlock();
              
              if ((var13.getMaterial() == net.minecraft.block.material.Material.air) || (var13 == Blocks.dirt) || (var13 == Blocks.snow) || (var13 == Blocks.ice))
              {
                func_175906_a(worldIn, p_180709_3_.add(var9, -var6, var11), Blocks.packed_ice);
              }
            }
          }
        }
      }
    }
    
    var6 = var5 - 1;
    
    if (var6 < 0)
    {
      var6 = 0;
    }
    else if (var6 > 1)
    {
      var6 = 1;
    }
    
    for (int var14 = -var6; var14 <= var6; var14++)
    {
      int var8 = -var6;
      
      while (var8 <= var6)
      {
        BlockPos var15 = p_180709_3_.add(var14, -1, var8);
        int var16 = 50;
        
        if ((Math.abs(var14) == 1) && (Math.abs(var8) == 1))
        {
          var16 = p_180709_2_.nextInt(5);
        }
        


        while (var15.getY() > 50)
        {
          Block var17 = worldIn.getBlockState(var15).getBlock();
          
          if ((var17.getMaterial() != net.minecraft.block.material.Material.air) && (var17 != Blocks.dirt) && (var17 != Blocks.snow) && (var17 != Blocks.ice) && (var17 != Blocks.packed_ice))
            break;
          func_175906_a(worldIn, var15, Blocks.packed_ice);
          var15 = var15.offsetDown();
          var16--;
          
          if (var16 <= 0)
          {
            var15 = var15.offsetDown(p_180709_2_.nextInt(5) + 1);
            var16 = p_180709_2_.nextInt(5);
          }
        }
        



        var8++;
      }
    }
    


    return true;
  }
}
