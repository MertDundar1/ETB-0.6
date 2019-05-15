package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiverMix extends GenLayer
{
  private GenLayer biomePatternGeneratorChain;
  private GenLayer riverPatternGeneratorChain;
  private static final String __OBFID = "CL_00000567";
  
  public GenLayerRiverMix(long p_i2129_1_, GenLayer p_i2129_3_, GenLayer p_i2129_4_)
  {
    super(p_i2129_1_);
    biomePatternGeneratorChain = p_i2129_3_;
    riverPatternGeneratorChain = p_i2129_4_;
  }
  




  public void initWorldGenSeed(long p_75905_1_)
  {
    biomePatternGeneratorChain.initWorldGenSeed(p_75905_1_);
    riverPatternGeneratorChain.initWorldGenSeed(p_75905_1_);
    super.initWorldGenSeed(p_75905_1_);
  }
  




  public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
  {
    int[] var5 = biomePatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
    int[] var6 = riverPatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
    int[] var7 = IntCache.getIntCache(areaWidth * areaHeight);
    
    for (int var8 = 0; var8 < areaWidth * areaHeight; var8++)
    {
      if ((var5[var8] != oceanbiomeID) && (var5[var8] != deepOceanbiomeID))
      {
        if (var6[var8] == riverbiomeID)
        {
          if (var5[var8] == icePlainsbiomeID)
          {
            var7[var8] = frozenRiverbiomeID;
          }
          else if ((var5[var8] != mushroomIslandbiomeID) && (var5[var8] != mushroomIslandShorebiomeID))
          {
            var6[var8] &= 0xFF;
          }
          else
          {
            var7[var8] = mushroomIslandShorebiomeID;
          }
          
        }
        else {
          var7[var8] = var5[var8];
        }
        
      }
      else {
        var7[var8] = var5[var8];
      }
    }
    
    return var7;
  }
}
