package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;

public class WorldGenSavannaTree extends WorldGenAbstractTree
{
  private static final String __OBFID = "CL_00000432";
  
  public WorldGenSavannaTree(boolean p_i45463_1_)
  {
    super(p_i45463_1_);
  }
  
  public boolean generate(World worldIn, Random p_180709_2_, BlockPos p_180709_3_)
  {
    int var4 = p_180709_2_.nextInt(3) + p_180709_2_.nextInt(3) + 5;
    boolean var5 = true;
    
    if ((p_180709_3_.getY() >= 1) && (p_180709_3_.getY() + var4 + 1 <= 256))
    {



      for (int var6 = p_180709_3_.getY(); var6 <= p_180709_3_.getY() + 1 + var4; var6++)
      {
        byte var7 = 1;
        
        if (var6 == p_180709_3_.getY())
        {
          var7 = 0;
        }
        
        if (var6 >= p_180709_3_.getY() + 1 + var4 - 2)
        {
          var7 = 2;
        }
        
        for (int var8 = p_180709_3_.getX() - var7; (var8 <= p_180709_3_.getX() + var7) && (var5); var8++)
        {
          for (int var9 = p_180709_3_.getZ() - var7; (var9 <= p_180709_3_.getZ() + var7) && (var5); var9++)
          {
            if ((var6 >= 0) && (var6 < 256))
            {
              if (!func_150523_a(worldIn.getBlockState(new BlockPos(var8, var6, var9)).getBlock()))
              {
                var5 = false;
              }
              
            }
            else {
              var5 = false;
            }
          }
        }
      }
      
      if (!var5)
      {
        return false;
      }
      

      net.minecraft.block.Block var20 = worldIn.getBlockState(p_180709_3_.offsetDown()).getBlock();
      
      if (((var20 == Blocks.grass) || (var20 == Blocks.dirt)) && (p_180709_3_.getY() < 256 - var4 - 1))
      {
        func_175921_a(worldIn, p_180709_3_.offsetDown());
        EnumFacing var21 = EnumFacing.Plane.HORIZONTAL.random(p_180709_2_);
        int var8 = var4 - p_180709_2_.nextInt(4) - 1;
        int var9 = 3 - p_180709_2_.nextInt(3);
        int var10 = p_180709_3_.getX();
        int var11 = p_180709_3_.getZ();
        int var12 = 0;
        

        for (int var13 = 0; var13 < var4; var13++)
        {
          int var14 = p_180709_3_.getY() + var13;
          
          if ((var13 >= var8) && (var9 > 0))
          {
            var10 += var21.getFrontOffsetX();
            var11 += var21.getFrontOffsetZ();
            var9--;
          }
          
          BlockPos var15 = new BlockPos(var10, var14, var11);
          Material var16 = worldIn.getBlockState(var15).getBlock().getMaterial();
          
          if ((var16 == Material.air) || (var16 == Material.leaves))
          {
            func_175905_a(worldIn, var15, Blocks.log2, net.minecraft.block.BlockPlanks.EnumType.ACACIA.func_176839_a() - 4);
            var12 = var14;
          }
        }
        
        BlockPos var22 = new BlockPos(var10, var12, var11);
        

        for (int var14 = -3; var14 <= 3; var14++)
        {
          for (int var24 = -3; var24 <= 3; var24++)
          {
            if ((Math.abs(var14) != 3) || (Math.abs(var24) != 3))
            {
              func_175924_b(worldIn, var22.add(var14, 0, var24));
            }
          }
        }
        
        var22 = var22.offsetUp();
        
        for (var14 = -1; var14 <= 1; var14++)
        {
          for (int var24 = -1; var24 <= 1; var24++)
          {
            func_175924_b(worldIn, var22.add(var14, 0, var24));
          }
        }
        
        func_175924_b(worldIn, var22.offsetEast(2));
        func_175924_b(worldIn, var22.offsetWest(2));
        func_175924_b(worldIn, var22.offsetSouth(2));
        func_175924_b(worldIn, var22.offsetNorth(2));
        var10 = p_180709_3_.getX();
        var11 = p_180709_3_.getZ();
        EnumFacing var23 = EnumFacing.Plane.HORIZONTAL.random(p_180709_2_);
        
        if (var23 != var21)
        {
          var14 = var8 - p_180709_2_.nextInt(2) - 1;
          int var24 = 1 + p_180709_2_.nextInt(3);
          var12 = 0;
          

          for (int var25 = var14; (var25 < var4) && (var24 > 0); var24--)
          {
            if (var25 >= 1)
            {
              int var17 = p_180709_3_.getY() + var25;
              var10 += var23.getFrontOffsetX();
              var11 += var23.getFrontOffsetZ();
              BlockPos var18 = new BlockPos(var10, var17, var11);
              Material var19 = worldIn.getBlockState(var18).getBlock().getMaterial();
              
              if ((var19 == Material.air) || (var19 == Material.leaves))
              {
                func_175905_a(worldIn, var18, Blocks.log2, net.minecraft.block.BlockPlanks.EnumType.ACACIA.func_176839_a() - 4);
                var12 = var17;
              }
            }
            
            var25++;
          }
          
          if (var12 > 0)
          {
            BlockPos var26 = new BlockPos(var10, var12, var11);
            

            for (int var17 = -2; var17 <= 2; var17++)
            {
              for (int var27 = -2; var27 <= 2; var27++)
              {
                if ((Math.abs(var17) != 2) || (Math.abs(var27) != 2))
                {
                  func_175924_b(worldIn, var26.add(var17, 0, var27));
                }
              }
            }
            
            var26 = var26.offsetUp();
            
            for (var17 = -1; var17 <= 1; var17++)
            {
              for (int var27 = -1; var27 <= 1; var27++)
              {
                func_175924_b(worldIn, var26.add(var17, 0, var27));
              }
            }
          }
        }
        
        return true;
      }
      

      return false;
    }
    



    return false;
  }
  

  private void func_175924_b(World worldIn, BlockPos p_175924_2_)
  {
    Material var3 = worldIn.getBlockState(p_175924_2_).getBlock().getMaterial();
    
    if ((var3 == Material.air) || (var3 == Material.leaves))
    {
      func_175905_a(worldIn, p_175924_2_, Blocks.leaves2, 0);
    }
  }
}
