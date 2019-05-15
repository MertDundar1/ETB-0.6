package net.minecraft.world.gen.layer;

public class GenLayerRiverInit extends GenLayer
{
  private static final String __OBFID = "CL_00000565";
  
  public GenLayerRiverInit(long p_i2127_1_, GenLayer p_i2127_3_)
  {
    super(p_i2127_1_);
    parent = p_i2127_3_;
  }
  




  public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
  {
    int[] var5 = parent.getInts(areaX, areaY, areaWidth, areaHeight);
    int[] var6 = IntCache.getIntCache(areaWidth * areaHeight);
    
    for (int var7 = 0; var7 < areaHeight; var7++)
    {
      for (int var8 = 0; var8 < areaWidth; var8++)
      {
        initChunkSeed(var8 + areaX, var7 + areaY);
        var6[(var8 + var7 * areaWidth)] = (var5[(var8 + var7 * areaWidth)] > 0 ? nextInt(299999) + 2 : 0);
      }
    }
    
    return var6;
  }
}
