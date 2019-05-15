package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenVillage extends MapGenStructure
{
  public static final List villageSpawnBiomes = java.util.Arrays.asList(new BiomeGenBase[] { BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.savanna });
  
  private int terrainType;
  
  private int field_82665_g;
  private int field_82666_h;
  private static final String __OBFID = "CL_00000514";
  
  public MapGenVillage()
  {
    field_82665_g = 32;
    field_82666_h = 8;
  }
  
  public MapGenVillage(Map p_i2093_1_)
  {
    this();
    Iterator var2 = p_i2093_1_.entrySet().iterator();
    
    while (var2.hasNext())
    {
      Map.Entry var3 = (Map.Entry)var2.next();
      
      if (((String)var3.getKey()).equals("size"))
      {
        terrainType = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), terrainType, 0);
      }
      else if (((String)var3.getKey()).equals("distance"))
      {
        field_82665_g = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), field_82665_g, field_82666_h + 1);
      }
    }
  }
  
  public String getStructureName()
  {
    return "Village";
  }
  
  protected boolean canSpawnStructureAtCoords(int p_75047_1_, int p_75047_2_)
  {
    int var3 = p_75047_1_;
    int var4 = p_75047_2_;
    
    if (p_75047_1_ < 0)
    {
      p_75047_1_ -= field_82665_g - 1;
    }
    
    if (p_75047_2_ < 0)
    {
      p_75047_2_ -= field_82665_g - 1;
    }
    
    int var5 = p_75047_1_ / field_82665_g;
    int var6 = p_75047_2_ / field_82665_g;
    Random var7 = worldObj.setRandomSeed(var5, var6, 10387312);
    var5 *= field_82665_g;
    var6 *= field_82665_g;
    var5 += var7.nextInt(field_82665_g - field_82666_h);
    var6 += var7.nextInt(field_82665_g - field_82666_h);
    
    if ((var3 == var5) && (var4 == var6))
    {
      boolean var8 = worldObj.getWorldChunkManager().areBiomesViable(var3 * 16 + 8, var4 * 16 + 8, 0, villageSpawnBiomes);
      
      if (var8)
      {
        return true;
      }
    }
    
    return false;
  }
  
  protected StructureStart getStructureStart(int p_75049_1_, int p_75049_2_)
  {
    return new Start(worldObj, rand, p_75049_1_, p_75049_2_, terrainType);
  }
  
  public static class Start extends StructureStart
  {
    private boolean hasMoreThanTwoComponents;
    private static final String __OBFID = "CL_00000515";
    
    public Start() {}
    
    public Start(World worldIn, Random p_i2092_2_, int p_i2092_3_, int p_i2092_4_, int p_i2092_5_)
    {
      super(p_i2092_4_);
      List var6 = StructureVillagePieces.getStructureVillageWeightedPieceList(p_i2092_2_, p_i2092_5_);
      StructureVillagePieces.Start var7 = new StructureVillagePieces.Start(worldIn.getWorldChunkManager(), 0, p_i2092_2_, (p_i2092_3_ << 4) + 2, (p_i2092_4_ << 4) + 2, var6, p_i2092_5_);
      components.add(var7);
      var7.buildComponent(var7, components, p_i2092_2_);
      List var8 = field_74930_j;
      List var9 = field_74932_i;
      

      while ((!var8.isEmpty()) || (!var9.isEmpty()))
      {


        if (var8.isEmpty())
        {
          int var10 = p_i2092_2_.nextInt(var9.size());
          StructureComponent var11 = (StructureComponent)var9.remove(var10);
          var11.buildComponent(var7, components, p_i2092_2_);
        }
        else
        {
          int var10 = p_i2092_2_.nextInt(var8.size());
          StructureComponent var11 = (StructureComponent)var8.remove(var10);
          var11.buildComponent(var7, components, p_i2092_2_);
        }
      }
      
      updateBoundingBox();
      int var10 = 0;
      Iterator var13 = components.iterator();
      
      while (var13.hasNext())
      {
        StructureComponent var12 = (StructureComponent)var13.next();
        
        if (!(var12 instanceof StructureVillagePieces.Road))
        {
          var10++;
        }
      }
      
      hasMoreThanTwoComponents = (var10 > 2);
    }
    
    public boolean isSizeableStructure()
    {
      return hasMoreThanTwoComponents;
    }
    
    public void func_143022_a(NBTTagCompound p_143022_1_)
    {
      super.func_143022_a(p_143022_1_);
      p_143022_1_.setBoolean("Valid", hasMoreThanTwoComponents);
    }
    
    public void func_143017_b(NBTTagCompound p_143017_1_)
    {
      super.func_143017_b(p_143017_1_);
      hasMoreThanTwoComponents = p_143017_1_.getBoolean("Valid");
    }
  }
}
