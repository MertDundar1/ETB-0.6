package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;

public class WorldProviderEnd extends WorldProvider
{
  private static final String __OBFID = "CL_00000389";
  
  public WorldProviderEnd() {}
  
  public void registerWorldChunkManager()
  {
    worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F);
    dimensionId = 1;
    hasNoSky = true;
  }
  



  public IChunkProvider createChunkGenerator()
  {
    return new ChunkProviderEnd(worldObj, worldObj.getSeed());
  }
  



  public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_)
  {
    return 0.0F;
  }
  



  public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_)
  {
    return null;
  }
  



  public Vec3 getFogColor(float p_76562_1_, float p_76562_2_)
  {
    int var3 = 10518688;
    float var4 = MathHelper.cos(p_76562_1_ * 3.1415927F * 2.0F) * 2.0F + 0.5F;
    var4 = MathHelper.clamp_float(var4, 0.0F, 1.0F);
    float var5 = (var3 >> 16 & 0xFF) / 255.0F;
    float var6 = (var3 >> 8 & 0xFF) / 255.0F;
    float var7 = (var3 & 0xFF) / 255.0F;
    var5 *= (var4 * 0.0F + 0.15F);
    var6 *= (var4 * 0.0F + 0.15F);
    var7 *= (var4 * 0.0F + 0.15F);
    return new Vec3(var5, var6, var7);
  }
  
  public boolean isSkyColored()
  {
    return false;
  }
  



  public boolean canRespawnHere()
  {
    return false;
  }
  



  public boolean isSurfaceWorld()
  {
    return false;
  }
  



  public float getCloudHeight()
  {
    return 8.0F;
  }
  



  public boolean canCoordinateBeSpawn(int x, int z)
  {
    return worldObj.getGroundAboveSeaLevel(new BlockPos(x, 0, z)).getMaterial().blocksMovement();
  }
  
  public BlockPos func_177496_h()
  {
    return new BlockPos(100, 50, 0);
  }
  
  public int getAverageGroundLevel()
  {
    return 50;
  }
  



  public boolean doesXZShowFog(int p_76568_1_, int p_76568_2_)
  {
    return true;
  }
  



  public String getDimensionName()
  {
    return "The End";
  }
  
  public String getInternalNameSuffix()
  {
    return "_end";
  }
}
