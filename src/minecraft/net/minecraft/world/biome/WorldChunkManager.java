package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;






public class WorldChunkManager
{
  private GenLayer genBiomes;
  private GenLayer biomeIndexLayer;
  private BiomeCache biomeCache;
  private List biomesToSpawnIn;
  private String field_180301_f;
  private static final String __OBFID = "CL_00000166";
  
  protected WorldChunkManager()
  {
    biomeCache = new BiomeCache(this);
    field_180301_f = "";
    biomesToSpawnIn = Lists.newArrayList();
    biomesToSpawnIn.add(BiomeGenBase.forest);
    biomesToSpawnIn.add(BiomeGenBase.plains);
    biomesToSpawnIn.add(BiomeGenBase.taiga);
    biomesToSpawnIn.add(BiomeGenBase.taigaHills);
    biomesToSpawnIn.add(BiomeGenBase.forestHills);
    biomesToSpawnIn.add(BiomeGenBase.jungle);
    biomesToSpawnIn.add(BiomeGenBase.jungleHills);
  }
  
  public WorldChunkManager(long p_i45744_1_, WorldType p_i45744_3_, String p_i45744_4_)
  {
    this();
    field_180301_f = p_i45744_4_;
    GenLayer[] var5 = GenLayer.func_180781_a(p_i45744_1_, p_i45744_3_, p_i45744_4_);
    genBiomes = var5[0];
    biomeIndexLayer = var5[1];
  }
  
  public WorldChunkManager(World worldIn)
  {
    this(worldIn.getSeed(), worldIn.getWorldInfo().getTerrainType(), worldIn.getWorldInfo().getGeneratorOptions());
  }
  



  public List getBiomesToSpawnIn()
  {
    return biomesToSpawnIn;
  }
  
  public BiomeGenBase func_180631_a(BlockPos p_180631_1_)
  {
    return func_180300_a(p_180631_1_, null);
  }
  
  public BiomeGenBase func_180300_a(BlockPos p_180300_1_, BiomeGenBase p_180300_2_)
  {
    return biomeCache.func_180284_a(p_180300_1_.getX(), p_180300_1_.getZ(), p_180300_2_);
  }
  


  public float[] getRainfall(float[] p_76936_1_, int p_76936_2_, int p_76936_3_, int p_76936_4_, int p_76936_5_)
  {
    
    

    if ((p_76936_1_ == null) || (p_76936_1_.length < p_76936_4_ * p_76936_5_))
    {
      p_76936_1_ = new float[p_76936_4_ * p_76936_5_];
    }
    
    int[] var6 = biomeIndexLayer.getInts(p_76936_2_, p_76936_3_, p_76936_4_, p_76936_5_);
    
    for (int var7 = 0; var7 < p_76936_4_ * p_76936_5_; var7++)
    {
      try
      {
        float var8 = BiomeGenBase.getBiomeFromBiomeList(var6[var7], BiomeGenBase.field_180279_ad).getIntRainfall() / 65536.0F;
        
        if (var8 > 1.0F)
        {
          var8 = 1.0F;
        }
        
        p_76936_1_[var7] = var8;
      }
      catch (Throwable var11)
      {
        CrashReport var9 = CrashReport.makeCrashReport(var11, "Invalid Biome id");
        CrashReportCategory var10 = var9.makeCategory("DownfallBlock");
        var10.addCrashSection("biome id", Integer.valueOf(var7));
        var10.addCrashSection("downfalls[] size", Integer.valueOf(p_76936_1_.length));
        var10.addCrashSection("x", Integer.valueOf(p_76936_2_));
        var10.addCrashSection("z", Integer.valueOf(p_76936_3_));
        var10.addCrashSection("w", Integer.valueOf(p_76936_4_));
        var10.addCrashSection("h", Integer.valueOf(p_76936_5_));
        throw new ReportedException(var9);
      }
    }
    
    return p_76936_1_;
  }
  



  public float getTemperatureAtHeight(float p_76939_1_, int p_76939_2_)
  {
    return p_76939_1_;
  }
  


  public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] p_76937_1_, int p_76937_2_, int p_76937_3_, int p_76937_4_, int p_76937_5_)
  {
    
    

    if ((p_76937_1_ == null) || (p_76937_1_.length < p_76937_4_ * p_76937_5_))
    {
      p_76937_1_ = new BiomeGenBase[p_76937_4_ * p_76937_5_];
    }
    
    int[] var6 = genBiomes.getInts(p_76937_2_, p_76937_3_, p_76937_4_, p_76937_5_);
    
    try
    {
      for (int var7 = 0; var7 < p_76937_4_ * p_76937_5_; var7++)
      {
        p_76937_1_[var7] = BiomeGenBase.getBiomeFromBiomeList(var6[var7], BiomeGenBase.field_180279_ad);
      }
      
      return p_76937_1_;
    }
    catch (Throwable var10)
    {
      CrashReport var8 = CrashReport.makeCrashReport(var10, "Invalid Biome id");
      CrashReportCategory var9 = var8.makeCategory("RawBiomeBlock");
      var9.addCrashSection("biomes[] size", Integer.valueOf(p_76937_1_.length));
      var9.addCrashSection("x", Integer.valueOf(p_76937_2_));
      var9.addCrashSection("z", Integer.valueOf(p_76937_3_));
      var9.addCrashSection("w", Integer.valueOf(p_76937_4_));
      var9.addCrashSection("h", Integer.valueOf(p_76937_5_));
      throw new ReportedException(var8);
    }
  }
  




  public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] p_76933_1_, int p_76933_2_, int p_76933_3_, int p_76933_4_, int p_76933_5_)
  {
    return getBiomeGenAt(p_76933_1_, p_76933_2_, p_76933_3_, p_76933_4_, p_76933_5_, true);
  }
  



  public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] p_76931_1_, int p_76931_2_, int p_76931_3_, int p_76931_4_, int p_76931_5_, boolean p_76931_6_)
  {
    
    

    if ((p_76931_1_ == null) || (p_76931_1_.length < p_76931_4_ * p_76931_5_))
    {
      p_76931_1_ = new BiomeGenBase[p_76931_4_ * p_76931_5_];
    }
    
    if ((p_76931_6_) && (p_76931_4_ == 16) && (p_76931_5_ == 16) && ((p_76931_2_ & 0xF) == 0) && ((p_76931_3_ & 0xF) == 0))
    {
      BiomeGenBase[] var9 = biomeCache.getCachedBiomes(p_76931_2_, p_76931_3_);
      System.arraycopy(var9, 0, p_76931_1_, 0, p_76931_4_ * p_76931_5_);
      return p_76931_1_;
    }
    

    int[] var7 = biomeIndexLayer.getInts(p_76931_2_, p_76931_3_, p_76931_4_, p_76931_5_);
    
    for (int var8 = 0; var8 < p_76931_4_ * p_76931_5_; var8++)
    {
      p_76931_1_[var8] = BiomeGenBase.getBiomeFromBiomeList(var7[var8], BiomeGenBase.field_180279_ad);
    }
    
    return p_76931_1_;
  }
  




  public boolean areBiomesViable(int p_76940_1_, int p_76940_2_, int p_76940_3_, List p_76940_4_)
  {
    IntCache.resetIntCache();
    int var5 = p_76940_1_ - p_76940_3_ >> 2;
    int var6 = p_76940_2_ - p_76940_3_ >> 2;
    int var7 = p_76940_1_ + p_76940_3_ >> 2;
    int var8 = p_76940_2_ + p_76940_3_ >> 2;
    int var9 = var7 - var5 + 1;
    int var10 = var8 - var6 + 1;
    int[] var11 = genBiomes.getInts(var5, var6, var9, var10);
    
    try
    {
      for (int var12 = 0; var12 < var9 * var10; var12++)
      {
        BiomeGenBase var16 = BiomeGenBase.getBiome(var11[var12]);
        
        if (!p_76940_4_.contains(var16))
        {
          return false;
        }
      }
      
      return true;
    }
    catch (Throwable var15)
    {
      CrashReport var13 = CrashReport.makeCrashReport(var15, "Invalid Biome id");
      CrashReportCategory var14 = var13.makeCategory("Layer");
      var14.addCrashSection("Layer", genBiomes.toString());
      var14.addCrashSection("x", Integer.valueOf(p_76940_1_));
      var14.addCrashSection("z", Integer.valueOf(p_76940_2_));
      var14.addCrashSection("radius", Integer.valueOf(p_76940_3_));
      var14.addCrashSection("allowed", p_76940_4_);
      throw new ReportedException(var13);
    }
  }
  
  public BlockPos findBiomePosition(int x, int z, int range, List biomes, Random random)
  {
    IntCache.resetIntCache();
    int var6 = x - range >> 2;
    int var7 = z - range >> 2;
    int var8 = x + range >> 2;
    int var9 = z + range >> 2;
    int var10 = var8 - var6 + 1;
    int var11 = var9 - var7 + 1;
    int[] var12 = genBiomes.getInts(var6, var7, var10, var11);
    BlockPos var13 = null;
    int var14 = 0;
    
    for (int var15 = 0; var15 < var10 * var11; var15++)
    {
      int var16 = var6 + var15 % var10 << 2;
      int var17 = var7 + var15 / var10 << 2;
      BiomeGenBase var18 = BiomeGenBase.getBiome(var12[var15]);
      
      if ((biomes.contains(var18)) && ((var13 == null) || (random.nextInt(var14 + 1) == 0)))
      {
        var13 = new BlockPos(var16, 0, var17);
        var14++;
      }
    }
    
    return var13;
  }
  



  public void cleanupCache()
  {
    biomeCache.cleanupCache();
  }
}
