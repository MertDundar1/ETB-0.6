package net.minecraft.world.biome;

import java.util.List;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;

public class BiomeGenHell extends BiomeGenBase
{
  private static final String __OBFID = "CL_00000173";
  
  public BiomeGenHell(int p_i1981_1_)
  {
    super(p_i1981_1_);
    spawnableMonsterList.clear();
    spawnableCreatureList.clear();
    spawnableWaterCreatureList.clear();
    spawnableCaveCreatureList.clear();
    spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityGhast.class, 50, 4, 4));
    spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityPigZombie.class, 100, 4, 4));
    spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(net.minecraft.entity.monster.EntityMagmaCube.class, 1, 4, 4));
  }
}
