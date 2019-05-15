package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees
{
  private static final String __OBFID = "CL_00000420";
  
  public WorldGenMegaJungle(boolean p_i45456_1_, int p_i45456_2_, int p_i45456_3_, int p_i45456_4_, int p_i45456_5_)
  {
    super(p_i45456_1_, p_i45456_2_, p_i45456_3_, p_i45456_4_, p_i45456_5_);
  }
  
  public boolean generate(World worldIn, Random p_180709_2_, BlockPos p_180709_3_)
  {
    int var4 = func_150533_a(p_180709_2_);
    
    if (!func_175929_a(worldIn, p_180709_2_, p_180709_3_, var4))
    {
      return false;
    }
    

    func_175930_c(worldIn, p_180709_3_.offsetUp(var4), 2);
    
    for (int var5 = p_180709_3_.getY() + var4 - 2 - p_180709_2_.nextInt(4); var5 > p_180709_3_.getY() + var4 / 2; var5 -= 2 + p_180709_2_.nextInt(4))
    {
      float var6 = p_180709_2_.nextFloat() * 3.1415927F * 2.0F;
      int var7 = p_180709_3_.getX() + (int)(0.5F + MathHelper.cos(var6) * 4.0F);
      int var8 = p_180709_3_.getZ() + (int)(0.5F + MathHelper.sin(var6) * 4.0F);
      

      for (int var9 = 0; var9 < 5; var9++)
      {
        var7 = p_180709_3_.getX() + (int)(1.5F + MathHelper.cos(var6) * var9);
        var8 = p_180709_3_.getZ() + (int)(1.5F + MathHelper.sin(var6) * var9);
        func_175905_a(worldIn, new BlockPos(var7, var5 - 3 + var9 / 2, var8), Blocks.log, woodMetadata);
      }
      
      var9 = 1 + p_180709_2_.nextInt(2);
      int var10 = var5;
      
      for (int var11 = var5 - var9; var11 <= var10; var11++)
      {
        int var12 = var11 - var10;
        func_175928_b(worldIn, new BlockPos(var7, var11, var8), 1 - var12);
      }
    }
    
    for (int var13 = 0; var13 < var4; var13++)
    {
      BlockPos var14 = p_180709_3_.offsetUp(var13);
      
      if (func_175931_a(worldIn.getBlockState(var14).getBlock().getMaterial()))
      {
        func_175905_a(worldIn, var14, Blocks.log, woodMetadata);
        
        if (var13 > 0)
        {
          func_175932_b(worldIn, p_180709_2_, var14.offsetWest(), BlockVine.field_176275_S);
          func_175932_b(worldIn, p_180709_2_, var14.offsetNorth(), BlockVine.field_176272_Q);
        }
      }
      
      if (var13 < var4 - 1)
      {
        BlockPos var15 = var14.offsetEast();
        
        if (func_175931_a(worldIn.getBlockState(var15).getBlock().getMaterial()))
        {
          func_175905_a(worldIn, var15, Blocks.log, woodMetadata);
          
          if (var13 > 0)
          {
            func_175932_b(worldIn, p_180709_2_, var15.offsetEast(), BlockVine.field_176271_T);
            func_175932_b(worldIn, p_180709_2_, var15.offsetNorth(), BlockVine.field_176272_Q);
          }
        }
        
        BlockPos var16 = var14.offsetSouth().offsetEast();
        
        if (func_175931_a(worldIn.getBlockState(var16).getBlock().getMaterial()))
        {
          func_175905_a(worldIn, var16, Blocks.log, woodMetadata);
          
          if (var13 > 0)
          {
            func_175932_b(worldIn, p_180709_2_, var16.offsetEast(), BlockVine.field_176271_T);
            func_175932_b(worldIn, p_180709_2_, var16.offsetSouth(), BlockVine.field_176276_R);
          }
        }
        
        BlockPos var17 = var14.offsetSouth();
        
        if (func_175931_a(worldIn.getBlockState(var17).getBlock().getMaterial()))
        {
          func_175905_a(worldIn, var17, Blocks.log, woodMetadata);
          
          if (var13 > 0)
          {
            func_175932_b(worldIn, p_180709_2_, var17.offsetWest(), BlockVine.field_176275_S);
            func_175932_b(worldIn, p_180709_2_, var17.offsetSouth(), BlockVine.field_176276_R);
          }
        }
      }
    }
    
    return true;
  }
  

  private boolean func_175931_a(net.minecraft.block.material.Material p_175931_1_)
  {
    return (p_175931_1_ == net.minecraft.block.material.Material.air) || (p_175931_1_ == net.minecraft.block.material.Material.leaves);
  }
  
  private void func_175932_b(World worldIn, Random p_175932_2_, BlockPos p_175932_3_, int p_175932_4_)
  {
    if ((p_175932_2_.nextInt(3) > 0) && (worldIn.isAirBlock(p_175932_3_)))
    {
      func_175905_a(worldIn, p_175932_3_, Blocks.vine, p_175932_4_);
    }
  }
  
  private void func_175930_c(World worldIn, BlockPos p_175930_2_, int p_175930_3_)
  {
    byte var4 = 2;
    
    for (int var5 = -var4; var5 <= 0; var5++)
    {
      func_175925_a(worldIn, p_175930_2_.offsetUp(var5), p_175930_3_ + 1 - var5);
    }
  }
}
