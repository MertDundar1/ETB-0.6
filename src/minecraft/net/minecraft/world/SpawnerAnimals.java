package net.minecraft.world;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;

public final class SpawnerAnimals
{
  private static final int field_180268_a = (int)Math.pow(17.0D, 2.0D);
  

  private final Set eligibleChunksForSpawning = com.google.common.collect.Sets.newHashSet();
  
  private static final String __OBFID = "CL_00000152";
  

  public SpawnerAnimals() {}
  
  public int findChunksForSpawning(WorldServer p_77192_1_, boolean p_77192_2_, boolean p_77192_3_, boolean p_77192_4_)
  {
    if ((!p_77192_2_) && (!p_77192_3_))
    {
      return 0;
    }
    

    eligibleChunksForSpawning.clear();
    int var5 = 0;
    Iterator var6 = playerEntities.iterator();
    


    while (var6.hasNext())
    {
      EntityPlayer var7 = (EntityPlayer)var6.next();
      
      if (!var7.func_175149_v())
      {
        int var8 = MathHelper.floor_double(posX / 16.0D);
        int var9 = MathHelper.floor_double(posZ / 16.0D);
        byte var10 = 8;
        
        for (int var11 = -var10; var11 <= var10; var11++)
        {
          for (int var12 = -var10; var12 <= var10; var12++)
          {
            boolean var13 = (var11 == -var10) || (var11 == var10) || (var12 == -var10) || (var12 == var10);
            ChunkCoordIntPair var14 = new ChunkCoordIntPair(var11 + var8, var12 + var9);
            
            if (!eligibleChunksForSpawning.contains(var14))
            {
              var5++;
              
              if ((!var13) && (p_77192_1_.getWorldBorder().contains(var14)))
              {
                eligibleChunksForSpawning.add(var14);
              }
            }
          }
        }
      }
    }
    
    int var36 = 0;
    BlockPos var37 = p_77192_1_.getSpawnPoint();
    EnumCreatureType[] var38 = EnumCreatureType.values();
    int var9 = var38.length;
    
    for (int var39 = 0; var39 < var9; var39++)
    {
      EnumCreatureType var40 = var38[var39];
      
      if (((!var40.getPeacefulCreature()) || (p_77192_3_)) && ((var40.getPeacefulCreature()) || (p_77192_2_)) && ((!var40.getAnimal()) || (p_77192_4_)))
      {
        int var12 = p_77192_1_.countEntities(var40.getCreatureClass());
        int var41 = var40.getMaxNumberOfCreature() * var5 / field_180268_a;
        
        if (var12 <= var41)
        {
          Iterator var42 = eligibleChunksForSpawning.iterator();
          
          label817:
          while (var42.hasNext())
          {
            ChunkCoordIntPair var15 = (ChunkCoordIntPair)var42.next();
            BlockPos var16 = func_180621_a(p_77192_1_, chunkXPos, chunkZPos);
            int var17 = var16.getX();
            int var18 = var16.getY();
            int var19 = var16.getZ();
            Block var20 = p_77192_1_.getBlockState(var16).getBlock();
            
            if (!var20.isNormalCube())
            {
              int var21 = 0;
              int var22 = 0;
              
              while (var22 < 3)
              {
                int var23 = var17;
                int var24 = var18;
                int var25 = var19;
                byte var26 = 6;
                BiomeGenBase.SpawnListEntry var27 = null;
                IEntityLivingData var28 = null;
                int var29 = 0;
                


                while (var29 < 4)
                {


                  var23 += rand.nextInt(var26) - rand.nextInt(var26);
                  var24 += rand.nextInt(1) - rand.nextInt(1);
                  var25 += rand.nextInt(var26) - rand.nextInt(var26);
                  BlockPos var30 = new BlockPos(var23, var24, var25);
                  float var31 = var23 + 0.5F;
                  float var32 = var25 + 0.5F;
                  
                  if ((!p_77192_1_.func_175636_b(var31, var24, var32, 24.0D)) && (var37.distanceSq(var31, var24, var32) >= 576.0D))
                  {
                    if (var27 == null)
                    {
                      var27 = p_77192_1_.func_175734_a(var40, var30);
                      
                      if (var27 == null) {
                        break;
                      }
                    }
                    

                    if ((p_77192_1_.func_175732_a(var40, var27, var30)) && (func_180267_a(net.minecraft.entity.EntitySpawnPlacementRegistry.func_180109_a(entityClass), p_77192_1_, var30)))
                    {

                      try
                      {

                        var33 = (EntityLiving)entityClass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { p_77192_1_ });
                      }
                      catch (Exception var35) {
                        EntityLiving var33;
                        var35.printStackTrace();
                        return var36;
                      }
                      EntityLiving var33;
                      var33.setLocationAndAngles(var31, var24, var32, rand.nextFloat() * 360.0F, 0.0F);
                      
                      if ((var33.getCanSpawnHere()) && (var33.handleLavaMovement()))
                      {
                        var28 = var33.func_180482_a(p_77192_1_.getDifficultyForLocation(new BlockPos(var33)), var28);
                        
                        if (var33.handleLavaMovement())
                        {
                          var21++;
                          p_77192_1_.spawnEntityInWorld(var33);
                        }
                        
                        if (var21 >= var33.getMaxSpawnedInChunk()) {
                          break label817;
                        }
                      }
                      

                      var36 += var21;
                    }
                  }
                  
                  var29++;
                }
                


                var22++;
              }
            }
          }
        }
      }
    }
    


    return var36;
  }
  

  protected static BlockPos func_180621_a(World worldIn, int p_180621_1_, int p_180621_2_)
  {
    Chunk var3 = worldIn.getChunkFromChunkCoords(p_180621_1_, p_180621_2_);
    int var4 = p_180621_1_ * 16 + rand.nextInt(16);
    int var5 = p_180621_2_ * 16 + rand.nextInt(16);
    int var6 = MathHelper.func_154354_b(var3.getHeight(new BlockPos(var4, 0, var5)) + 1, 16);
    int var7 = rand.nextInt(var6 > 0 ? var6 : var3.getTopFilledSegment() + 16 - 1);
    return new BlockPos(var4, var7, var5);
  }
  
  public static boolean func_180267_a(EntityLiving.SpawnPlacementType p_180267_0_, World worldIn, BlockPos p_180267_2_)
  {
    if (!worldIn.getWorldBorder().contains(p_180267_2_))
    {
      return false;
    }
    

    Block var3 = worldIn.getBlockState(p_180267_2_).getBlock();
    
    if (p_180267_0_ == EntityLiving.SpawnPlacementType.IN_WATER)
    {
      return (var3.getMaterial().isLiquid()) && (worldIn.getBlockState(p_180267_2_.offsetDown()).getBlock().getMaterial().isLiquid()) && (!worldIn.getBlockState(p_180267_2_.offsetUp()).getBlock().isNormalCube());
    }
    

    BlockPos var4 = p_180267_2_.offsetDown();
    
    if (!World.doesBlockHaveSolidTopSurface(worldIn, var4))
    {
      return false;
    }
    

    Block var5 = worldIn.getBlockState(var4).getBlock();
    boolean var6 = (var5 != net.minecraft.init.Blocks.bedrock) && (var5 != net.minecraft.init.Blocks.barrier);
    return (var6) && (!var3.isNormalCube()) && (!var3.getMaterial().isLiquid()) && (!worldIn.getBlockState(p_180267_2_.offsetUp()).getBlock().isNormalCube());
  }
  






  public static void performWorldGenSpawning(World worldIn, BiomeGenBase p_77191_1_, int p_77191_2_, int p_77191_3_, int p_77191_4_, int p_77191_5_, Random p_77191_6_)
  {
    List var7 = p_77191_1_.getSpawnableList(EnumCreatureType.CREATURE);
    
    if (!var7.isEmpty()) { int var9;
      int var15;
      for (; p_77191_6_.nextFloat() < p_77191_1_.getSpawningChance(); 
          








          var15 < var9)
      {
        BiomeGenBase.SpawnListEntry var8 = (BiomeGenBase.SpawnListEntry)net.minecraft.util.WeightedRandom.getRandomItem(rand, var7);
        var9 = minGroupCount + p_77191_6_.nextInt(1 + maxGroupCount - minGroupCount);
        IEntityLivingData var10 = null;
        int var11 = p_77191_2_ + p_77191_6_.nextInt(p_77191_4_);
        int var12 = p_77191_3_ + p_77191_6_.nextInt(p_77191_5_);
        int var13 = var11;
        int var14 = var12;
        
        var15 = 0; continue;
        
        boolean var16 = false;
        
        for (int var17 = 0; (!var16) && (var17 < 4); var17++)
        {
          BlockPos var18 = worldIn.func_175672_r(new BlockPos(var11, 0, var12));
          
          if (func_180267_a(EntityLiving.SpawnPlacementType.ON_GROUND, worldIn, var18))
          {

            try
            {

              var19 = (EntityLiving)entityClass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { worldIn });
            }
            catch (Exception var21) {
              EntityLiving var19;
              var21.printStackTrace();
              continue;
            }
            EntityLiving var19;
            var19.setLocationAndAngles(var11 + 0.5F, var18.getY(), var12 + 0.5F, p_77191_6_.nextFloat() * 360.0F, 0.0F);
            worldIn.spawnEntityInWorld(var19);
            var10 = var19.func_180482_a(worldIn.getDifficultyForLocation(new BlockPos(var19)), var10);
            var16 = true;
          }
          
          var11 += p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5);
          
          for (var12 += p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5); (var11 < p_77191_2_) || (var11 >= p_77191_2_ + p_77191_4_) || (var12 < p_77191_3_) || (var12 >= p_77191_3_ + p_77191_4_); var12 = var14 + p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5))
          {
            var11 = var13 + p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5);
          }
        }
        var15++;
      }
    }
  }
}
