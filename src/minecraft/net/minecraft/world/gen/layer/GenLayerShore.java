package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenMesa;

public class GenLayerShore extends GenLayer
{
  private static final String __OBFID = "CL_00000568";
  
  public GenLayerShore(long p_i2130_1_, GenLayer p_i2130_3_)
  {
    super(p_i2130_1_);
    parent = p_i2130_3_;
  }
  




  public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
  {
    int[] var5 = parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
    int[] var6 = IntCache.getIntCache(areaWidth * areaHeight);
    
    for (int var7 = 0; var7 < areaHeight; var7++)
    {
      for (int var8 = 0; var8 < areaWidth; var8++)
      {
        initChunkSeed(var8 + areaX, var7 + areaY);
        int var9 = var5[(var8 + 1 + (var7 + 1) * (areaWidth + 2))];
        BiomeGenBase var10 = BiomeGenBase.getBiome(var9);
        




        if (var9 == mushroomIslandbiomeID)
        {
          int var11 = var5[(var8 + 1 + (var7 + 1 - 1) * (areaWidth + 2))];
          int var12 = var5[(var8 + 1 + 1 + (var7 + 1) * (areaWidth + 2))];
          int var13 = var5[(var8 + 1 - 1 + (var7 + 1) * (areaWidth + 2))];
          int var14 = var5[(var8 + 1 + (var7 + 1 + 1) * (areaWidth + 2))];
          
          if ((var11 != oceanbiomeID) && (var12 != oceanbiomeID) && (var13 != oceanbiomeID) && (var14 != oceanbiomeID))
          {
            var6[(var8 + var7 * areaWidth)] = var9;
          }
          else
          {
            var6[(var8 + var7 * areaWidth)] = mushroomIslandShorebiomeID;
          }
        }
        else if ((var10 != null) && (var10.getBiomeClass() == BiomeGenJungle.class))
        {
          int var11 = var5[(var8 + 1 + (var7 + 1 - 1) * (areaWidth + 2))];
          int var12 = var5[(var8 + 1 + 1 + (var7 + 1) * (areaWidth + 2))];
          int var13 = var5[(var8 + 1 - 1 + (var7 + 1) * (areaWidth + 2))];
          int var14 = var5[(var8 + 1 + (var7 + 1 + 1) * (areaWidth + 2))];
          
          if ((func_151631_c(var11)) && (func_151631_c(var12)) && (func_151631_c(var13)) && (func_151631_c(var14)))
          {
            if ((!isBiomeOceanic(var11)) && (!isBiomeOceanic(var12)) && (!isBiomeOceanic(var13)) && (!isBiomeOceanic(var14)))
            {
              var6[(var8 + var7 * areaWidth)] = var9;
            }
            else
            {
              var6[(var8 + var7 * areaWidth)] = beachbiomeID;
            }
            
          }
          else {
            var6[(var8 + var7 * areaWidth)] = jungleEdgebiomeID;
          }
        }
        else if ((var9 != extremeHillsbiomeID) && (var9 != extremeHillsPlusbiomeID) && (var9 != extremeHillsEdgebiomeID))
        {
          if ((var10 != null) && (var10.isSnowyBiome()))
          {
            func_151632_a(var5, var6, var8, var7, areaWidth, var9, coldBeachbiomeID);
          }
          else if ((var9 != mesabiomeID) && (var9 != mesaPlateau_FbiomeID))
          {
            if ((var9 != oceanbiomeID) && (var9 != deepOceanbiomeID) && (var9 != riverbiomeID) && (var9 != swamplandbiomeID))
            {
              int var11 = var5[(var8 + 1 + (var7 + 1 - 1) * (areaWidth + 2))];
              int var12 = var5[(var8 + 1 + 1 + (var7 + 1) * (areaWidth + 2))];
              int var13 = var5[(var8 + 1 - 1 + (var7 + 1) * (areaWidth + 2))];
              int var14 = var5[(var8 + 1 + (var7 + 1 + 1) * (areaWidth + 2))];
              
              if ((!isBiomeOceanic(var11)) && (!isBiomeOceanic(var12)) && (!isBiomeOceanic(var13)) && (!isBiomeOceanic(var14)))
              {
                var6[(var8 + var7 * areaWidth)] = var9;
              }
              else
              {
                var6[(var8 + var7 * areaWidth)] = beachbiomeID;
              }
            }
            else
            {
              var6[(var8 + var7 * areaWidth)] = var9;
            }
          }
          else
          {
            int var11 = var5[(var8 + 1 + (var7 + 1 - 1) * (areaWidth + 2))];
            int var12 = var5[(var8 + 1 + 1 + (var7 + 1) * (areaWidth + 2))];
            int var13 = var5[(var8 + 1 - 1 + (var7 + 1) * (areaWidth + 2))];
            int var14 = var5[(var8 + 1 + (var7 + 1 + 1) * (areaWidth + 2))];
            
            if ((!isBiomeOceanic(var11)) && (!isBiomeOceanic(var12)) && (!isBiomeOceanic(var13)) && (!isBiomeOceanic(var14)))
            {
              if ((func_151633_d(var11)) && (func_151633_d(var12)) && (func_151633_d(var13)) && (func_151633_d(var14)))
              {
                var6[(var8 + var7 * areaWidth)] = var9;
              }
              else
              {
                var6[(var8 + var7 * areaWidth)] = desertbiomeID;
              }
              
            }
            else {
              var6[(var8 + var7 * areaWidth)] = var9;
            }
          }
        }
        else
        {
          func_151632_a(var5, var6, var8, var7, areaWidth, var9, stoneBeachbiomeID);
        }
      }
    }
    
    return var6;
  }
  
  private void func_151632_a(int[] p_151632_1_, int[] p_151632_2_, int p_151632_3_, int p_151632_4_, int p_151632_5_, int p_151632_6_, int p_151632_7_)
  {
    if (isBiomeOceanic(p_151632_6_))
    {
      p_151632_2_[(p_151632_3_ + p_151632_4_ * p_151632_5_)] = p_151632_6_;
    }
    else
    {
      int var8 = p_151632_1_[(p_151632_3_ + 1 + (p_151632_4_ + 1 - 1) * (p_151632_5_ + 2))];
      int var9 = p_151632_1_[(p_151632_3_ + 1 + 1 + (p_151632_4_ + 1) * (p_151632_5_ + 2))];
      int var10 = p_151632_1_[(p_151632_3_ + 1 - 1 + (p_151632_4_ + 1) * (p_151632_5_ + 2))];
      int var11 = p_151632_1_[(p_151632_3_ + 1 + (p_151632_4_ + 1 + 1) * (p_151632_5_ + 2))];
      
      if ((!isBiomeOceanic(var8)) && (!isBiomeOceanic(var9)) && (!isBiomeOceanic(var10)) && (!isBiomeOceanic(var11)))
      {
        p_151632_2_[(p_151632_3_ + p_151632_4_ * p_151632_5_)] = p_151632_6_;
      }
      else
      {
        p_151632_2_[(p_151632_3_ + p_151632_4_ * p_151632_5_)] = p_151632_7_;
      }
    }
  }
  
  private boolean func_151631_c(int p_151631_1_)
  {
    return (BiomeGenBase.getBiome(p_151631_1_) != null) && (BiomeGenBase.getBiome(p_151631_1_).getBiomeClass() == BiomeGenJungle.class);
  }
  
  private boolean func_151633_d(int p_151633_1_)
  {
    return BiomeGenBase.getBiome(p_151633_1_) instanceof BiomeGenMesa;
  }
}
