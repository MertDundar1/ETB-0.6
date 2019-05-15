package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSandStone.EnumType;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;

public class StructureVillagePieces
{
  private static final String __OBFID = "CL_00000516";
  
  public StructureVillagePieces() {}
  
  public static void registerVillagePieces()
  {
    MapGenStructureIO.registerStructureComponent(House1.class, "ViBH");
    MapGenStructureIO.registerStructureComponent(Field1.class, "ViDF");
    MapGenStructureIO.registerStructureComponent(Field2.class, "ViF");
    MapGenStructureIO.registerStructureComponent(Torch.class, "ViL");
    MapGenStructureIO.registerStructureComponent(Hall.class, "ViPH");
    MapGenStructureIO.registerStructureComponent(House4Garden.class, "ViSH");
    MapGenStructureIO.registerStructureComponent(WoodHut.class, "ViSmH");
    MapGenStructureIO.registerStructureComponent(Church.class, "ViST");
    MapGenStructureIO.registerStructureComponent(House2.class, "ViS");
    MapGenStructureIO.registerStructureComponent(Start.class, "ViStart");
    MapGenStructureIO.registerStructureComponent(Path.class, "ViSR");
    MapGenStructureIO.registerStructureComponent(House3.class, "ViTRH");
    MapGenStructureIO.registerStructureComponent(Well.class, "ViW");
  }
  
  public static List getStructureVillageWeightedPieceList(Random p_75084_0_, int p_75084_1_)
  {
    ArrayList var2 = com.google.common.collect.Lists.newArrayList();
    var2.add(new PieceWeight(House4Garden.class, 4, MathHelper.getRandomIntegerInRange(p_75084_0_, 2 + p_75084_1_, 4 + p_75084_1_ * 2)));
    var2.add(new PieceWeight(Church.class, 20, MathHelper.getRandomIntegerInRange(p_75084_0_, 0 + p_75084_1_, 1 + p_75084_1_)));
    var2.add(new PieceWeight(House1.class, 20, MathHelper.getRandomIntegerInRange(p_75084_0_, 0 + p_75084_1_, 2 + p_75084_1_)));
    var2.add(new PieceWeight(WoodHut.class, 3, MathHelper.getRandomIntegerInRange(p_75084_0_, 2 + p_75084_1_, 5 + p_75084_1_ * 3)));
    var2.add(new PieceWeight(Hall.class, 15, MathHelper.getRandomIntegerInRange(p_75084_0_, 0 + p_75084_1_, 2 + p_75084_1_)));
    var2.add(new PieceWeight(Field1.class, 3, MathHelper.getRandomIntegerInRange(p_75084_0_, 1 + p_75084_1_, 4 + p_75084_1_)));
    var2.add(new PieceWeight(Field2.class, 3, MathHelper.getRandomIntegerInRange(p_75084_0_, 2 + p_75084_1_, 4 + p_75084_1_ * 2)));
    var2.add(new PieceWeight(House2.class, 15, MathHelper.getRandomIntegerInRange(p_75084_0_, 0, 1 + p_75084_1_)));
    var2.add(new PieceWeight(House3.class, 8, MathHelper.getRandomIntegerInRange(p_75084_0_, 0 + p_75084_1_, 3 + p_75084_1_ * 2)));
    Iterator var3 = var2.iterator();
    
    while (var3.hasNext())
    {
      if (nextvillagePiecesLimit == 0)
      {
        var3.remove();
      }
    }
    
    return var2;
  }
  
  private static int func_75079_a(List p_75079_0_)
  {
    boolean var1 = false;
    int var2 = 0;
    
    PieceWeight var4;
    for (Iterator var3 = p_75079_0_.iterator(); var3.hasNext(); var2 += villagePieceWeight)
    {
      var4 = (PieceWeight)var3.next();
      
      if ((villagePiecesLimit > 0) && (villagePiecesSpawned < villagePiecesLimit))
      {
        var1 = true;
      }
    }
    
    return var1 ? var2 : -1;
  }
  
  private static Village func_176065_a(Start p_176065_0_, PieceWeight p_176065_1_, List p_176065_2_, Random p_176065_3_, int p_176065_4_, int p_176065_5_, int p_176065_6_, EnumFacing p_176065_7_, int p_176065_8_)
  {
    Class var9 = villagePieceClass;
    Object var10 = null;
    
    if (var9 == House4Garden.class)
    {
      var10 = House4Garden.func_175858_a(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
    }
    else if (var9 == Church.class)
    {
      var10 = Church.func_175854_a(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
    }
    else if (var9 == House1.class)
    {
      var10 = House1.func_175850_a(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
    }
    else if (var9 == WoodHut.class)
    {
      var10 = WoodHut.func_175853_a(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
    }
    else if (var9 == Hall.class)
    {
      var10 = Hall.func_175857_a(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
    }
    else if (var9 == Field1.class)
    {
      var10 = Field1.func_175851_a(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
    }
    else if (var9 == Field2.class)
    {
      var10 = Field2.func_175852_a(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
    }
    else if (var9 == House2.class)
    {
      var10 = House2.func_175855_a(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
    }
    else if (var9 == House3.class)
    {
      var10 = House3.func_175849_a(p_176065_0_, p_176065_2_, p_176065_3_, p_176065_4_, p_176065_5_, p_176065_6_, p_176065_7_, p_176065_8_);
    }
    
    return (Village)var10;
  }
  
  private static Village func_176067_c(Start p_176067_0_, List p_176067_1_, Random p_176067_2_, int p_176067_3_, int p_176067_4_, int p_176067_5_, EnumFacing p_176067_6_, int p_176067_7_)
  {
    int var8 = func_75079_a(structureVillageWeightedPieceList);
    
    if (var8 <= 0)
    {
      return null;
    }
    

    int var9 = 0;
    Iterator var11;
    label183: for (; var9 < 5; 
        




        var11.hasNext())
    {
      var9++;
      int var10 = p_176067_2_.nextInt(var8);
      var11 = structureVillageWeightedPieceList.iterator();
      
      continue;
      
      PieceWeight var12 = (PieceWeight)var11.next();
      var10 -= villagePieceWeight;
      
      if (var10 < 0)
      {
        if ((!var12.canSpawnMoreVillagePiecesOfType(p_176067_7_)) || ((var12 == structVillagePieceWeight) && (structureVillageWeightedPieceList.size() > 1))) {
          break label183;
        }
        

        Village var13 = func_176065_a(p_176067_0_, var12, p_176067_1_, p_176067_2_, p_176067_3_, p_176067_4_, p_176067_5_, p_176067_6_, p_176067_7_);
        
        if (var13 != null)
        {
          villagePiecesSpawned += 1;
          structVillagePieceWeight = var12;
          
          if (!var12.canSpawnMoreVillagePieces())
          {
            structureVillageWeightedPieceList.remove(var12);
          }
          
          return var13;
        }
      }
    }
    

    StructureBoundingBox var14 = Torch.func_175856_a(p_176067_0_, p_176067_1_, p_176067_2_, p_176067_3_, p_176067_4_, p_176067_5_, p_176067_6_);
    
    if (var14 != null)
    {
      return new Torch(p_176067_0_, p_176067_7_, p_176067_2_, var14, p_176067_6_);
    }
    

    return null;
  }
  


  private static StructureComponent func_176066_d(Start p_176066_0_, List p_176066_1_, Random p_176066_2_, int p_176066_3_, int p_176066_4_, int p_176066_5_, EnumFacing p_176066_6_, int p_176066_7_)
  {
    if (p_176066_7_ > 50)
    {
      return null;
    }
    if ((Math.abs(p_176066_3_ - getBoundingBoxminX) <= 112) && (Math.abs(p_176066_5_ - getBoundingBoxminZ) <= 112))
    {
      Village var8 = func_176067_c(p_176066_0_, p_176066_1_, p_176066_2_, p_176066_3_, p_176066_4_, p_176066_5_, p_176066_6_, p_176066_7_ + 1);
      
      if (var8 != null)
      {
        int var9 = (boundingBox.minX + boundingBox.maxX) / 2;
        int var10 = (boundingBox.minZ + boundingBox.maxZ) / 2;
        int var11 = boundingBox.maxX - boundingBox.minX;
        int var12 = boundingBox.maxZ - boundingBox.minZ;
        int var13 = var11 > var12 ? var11 : var12;
        
        if (p_176066_0_.getWorldChunkManager().areBiomesViable(var9, var10, var13 / 2 + 4, MapGenVillage.villageSpawnBiomes))
        {
          p_176066_1_.add(var8);
          field_74932_i.add(var8);
          return var8;
        }
      }
      
      return null;
    }
    

    return null;
  }
  

  private static StructureComponent func_176069_e(Start p_176069_0_, List p_176069_1_, Random p_176069_2_, int p_176069_3_, int p_176069_4_, int p_176069_5_, EnumFacing p_176069_6_, int p_176069_7_)
  {
    if (p_176069_7_ > 3 + terrainType)
    {
      return null;
    }
    if ((Math.abs(p_176069_3_ - getBoundingBoxminX) <= 112) && (Math.abs(p_176069_5_ - getBoundingBoxminZ) <= 112))
    {
      StructureBoundingBox var8 = Path.func_175848_a(p_176069_0_, p_176069_1_, p_176069_2_, p_176069_3_, p_176069_4_, p_176069_5_, p_176069_6_);
      
      if ((var8 != null) && (minY > 10))
      {
        Path var9 = new Path(p_176069_0_, p_176069_7_, p_176069_2_, var8, p_176069_6_);
        int var10 = (boundingBox.minX + boundingBox.maxX) / 2;
        int var11 = (boundingBox.minZ + boundingBox.maxZ) / 2;
        int var12 = boundingBox.maxX - boundingBox.minX;
        int var13 = boundingBox.maxZ - boundingBox.minZ;
        int var14 = var12 > var13 ? var12 : var13;
        
        if (p_176069_0_.getWorldChunkManager().areBiomesViable(var10, var11, var14 / 2 + 4, MapGenVillage.villageSpawnBiomes))
        {
          p_176069_1_.add(var9);
          field_74930_j.add(var9);
          return var9;
        }
      }
      
      return null;
    }
    

    return null;
  }
  
  public static class Church
    extends StructureVillagePieces.Village
  {
    private static final String __OBFID = "CL_00000525";
    
    public Church() {}
    
    public Church(StructureVillagePieces.Start p_i45564_1_, int p_i45564_2_, Random p_i45564_3_, StructureBoundingBox p_i45564_4_, EnumFacing p_i45564_5_)
    {
      super(p_i45564_2_);
      coordBaseMode = p_i45564_5_;
      boundingBox = p_i45564_4_;
    }
    
    public static Church func_175854_a(StructureVillagePieces.Start p_175854_0_, List p_175854_1_, Random p_175854_2_, int p_175854_3_, int p_175854_4_, int p_175854_5_, EnumFacing p_175854_6_, int p_175854_7_)
    {
      StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175854_3_, p_175854_4_, p_175854_5_, 0, 0, 0, 5, 12, 9, p_175854_6_);
      return (canVillageGoDeeper(var8)) && (StructureComponent.findIntersecting(p_175854_1_, var8) == null) ? new Church(p_175854_0_, p_175854_7_, p_175854_2_, var8, p_175854_6_) : null;
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 12 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 1, 1, 1, 3, 3, 7, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 5, 1, 3, 9, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 0, 3, 0, 8, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 1, 0, 3, 10, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 1, 1, 0, 10, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 4, 1, 1, 4, 10, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 0, 4, 0, 4, 7, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 4, 0, 4, 4, 4, 7, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 1, 8, 3, 4, 8, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 5, 4, 3, 10, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 5, 5, 3, 5, 7, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 9, 0, 4, 9, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 4, 0, 4, 4, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 0, 11, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 4, 11, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 2, 11, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 2, 11, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 1, 1, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 1, 1, 7, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 2, 1, 7, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 3, 1, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 3, 1, 7, p_74875_3_);
      func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 1, 1, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 1, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 3, 1, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 1)), 1, 2, 7, p_74875_3_);
      func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 0)), 3, 2, 7, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 3, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 4, 3, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 6, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 7, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 4, 6, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 4, 7, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 6, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 7, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 6, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 7, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 3, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 4, 3, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 3, 8, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode.getOpposite()), 2, 4, 7, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode.rotateY()), 1, 4, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode.rotateYCCW()), 3, 4, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode), 2, 4, 5, p_74875_3_);
      int var4 = getMetadataWithOffset(Blocks.ladder, 4);
      

      for (int var5 = 1; var5 <= 9; var5++)
      {
        func_175811_a(worldIn, Blocks.ladder.getStateFromMeta(var4), 3, var5, 3, p_74875_3_);
      }
      
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 2, 1, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 2, 2, 0, p_74875_3_);
      func_175810_a(worldIn, p_74875_3_, p_74875_2_, 2, 1, 0, EnumFacing.getHorizontal(getMetadataWithOffset(Blocks.oak_door, 1)));
      
      if ((func_175807_a(worldIn, 2, 0, -1, p_74875_3_).getBlock().getMaterial() == Material.air) && (func_175807_a(worldIn, 2, -1, -1, p_74875_3_).getBlock().getMaterial() != Material.air))
      {
        func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, p_74875_3_);
      }
      
      for (var5 = 0; var5 < 9; var5++)
      {
        for (int var6 = 0; var6 < 5; var6++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var6, 12, var5, p_74875_3_);
          func_175808_b(worldIn, Blocks.cobblestone.getDefaultState(), var6, -1, var5, p_74875_3_);
        }
      }
      
      spawnVillagers(worldIn, p_74875_3_, 2, 1, 2, 1);
      return true;
    }
    
    protected int func_180779_c(int p_180779_1_, int p_180779_2_)
    {
      return 2;
    }
  }
  
  public static class Field1 extends StructureVillagePieces.Village
  {
    private Block cropTypeA;
    private Block cropTypeB;
    private Block cropTypeC;
    private Block cropTypeD;
    private static final String __OBFID = "CL_00000518";
    
    public Field1() {}
    
    public Field1(StructureVillagePieces.Start p_i45570_1_, int p_i45570_2_, Random p_i45570_3_, StructureBoundingBox p_i45570_4_, EnumFacing p_i45570_5_)
    {
      super(p_i45570_2_);
      coordBaseMode = p_i45570_5_;
      boundingBox = p_i45570_4_;
      cropTypeA = func_151559_a(p_i45570_3_);
      cropTypeB = func_151559_a(p_i45570_3_);
      cropTypeC = func_151559_a(p_i45570_3_);
      cropTypeD = func_151559_a(p_i45570_3_);
    }
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      super.writeStructureToNBT(p_143012_1_);
      p_143012_1_.setInteger("CA", Block.blockRegistry.getIDForObject(cropTypeA));
      p_143012_1_.setInteger("CB", Block.blockRegistry.getIDForObject(cropTypeB));
      p_143012_1_.setInteger("CC", Block.blockRegistry.getIDForObject(cropTypeC));
      p_143012_1_.setInteger("CD", Block.blockRegistry.getIDForObject(cropTypeD));
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      super.readStructureFromNBT(p_143011_1_);
      cropTypeA = Block.getBlockById(p_143011_1_.getInteger("CA"));
      cropTypeB = Block.getBlockById(p_143011_1_.getInteger("CB"));
      cropTypeC = Block.getBlockById(p_143011_1_.getInteger("CC"));
      cropTypeD = Block.getBlockById(p_143011_1_.getInteger("CD"));
    }
    
    private Block func_151559_a(Random p_151559_1_)
    {
      switch (p_151559_1_.nextInt(5))
      {
      case 0: 
        return Blocks.carrots;
      
      case 1: 
        return Blocks.potatoes;
      }
      
      return Blocks.wheat;
    }
    

    public static Field1 func_175851_a(StructureVillagePieces.Start p_175851_0_, List p_175851_1_, Random p_175851_2_, int p_175851_3_, int p_175851_4_, int p_175851_5_, EnumFacing p_175851_6_, int p_175851_7_)
    {
      StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175851_3_, p_175851_4_, p_175851_5_, 0, 0, 0, 13, 4, 9, p_175851_6_);
      return (canVillageGoDeeper(var8)) && (StructureComponent.findIntersecting(p_175851_1_, var8) == null) ? new Field1(p_175851_0_, p_175851_7_, p_175851_2_, var8, p_175851_6_) : null;
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 4 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 0, 1, 0, 12, 4, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 1, 2, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 4, 0, 1, 5, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 7, 0, 1, 8, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 10, 0, 1, 11, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 0, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 6, 0, 0, 6, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 12, 0, 0, 12, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 0, 11, 0, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 8, 11, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 3, 0, 1, 3, 0, 7, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 9, 0, 1, 9, 0, 7, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);
      

      for (int var4 = 1; var4 <= 7; var4++)
      {
        func_175811_a(worldIn, cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 1, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 2, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 4, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 5, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeC.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 7, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeC.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 8, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeD.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 10, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeD.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 11, 1, var4, p_74875_3_);
      }
      
      for (var4 = 0; var4 < 9; var4++)
      {
        for (int var5 = 0; var5 < 13; var5++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var5, 4, var4, p_74875_3_);
          func_175808_b(worldIn, Blocks.dirt.getDefaultState(), var5, -1, var4, p_74875_3_);
        }
      }
      
      return true;
    }
  }
  
  public static class Field2 extends StructureVillagePieces.Village
  {
    private Block cropTypeA;
    private Block cropTypeB;
    private static final String __OBFID = "CL_00000519";
    
    public Field2() {}
    
    public Field2(StructureVillagePieces.Start p_i45569_1_, int p_i45569_2_, Random p_i45569_3_, StructureBoundingBox p_i45569_4_, EnumFacing p_i45569_5_)
    {
      super(p_i45569_2_);
      coordBaseMode = p_i45569_5_;
      boundingBox = p_i45569_4_;
      cropTypeA = func_151560_a(p_i45569_3_);
      cropTypeB = func_151560_a(p_i45569_3_);
    }
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      super.writeStructureToNBT(p_143012_1_);
      p_143012_1_.setInteger("CA", Block.blockRegistry.getIDForObject(cropTypeA));
      p_143012_1_.setInteger("CB", Block.blockRegistry.getIDForObject(cropTypeB));
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      super.readStructureFromNBT(p_143011_1_);
      cropTypeA = Block.getBlockById(p_143011_1_.getInteger("CA"));
      cropTypeB = Block.getBlockById(p_143011_1_.getInteger("CB"));
    }
    
    private Block func_151560_a(Random p_151560_1_)
    {
      switch (p_151560_1_.nextInt(5))
      {
      case 0: 
        return Blocks.carrots;
      
      case 1: 
        return Blocks.potatoes;
      }
      
      return Blocks.wheat;
    }
    

    public static Field2 func_175852_a(StructureVillagePieces.Start p_175852_0_, List p_175852_1_, Random p_175852_2_, int p_175852_3_, int p_175852_4_, int p_175852_5_, EnumFacing p_175852_6_, int p_175852_7_)
    {
      StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175852_3_, p_175852_4_, p_175852_5_, 0, 0, 0, 7, 4, 9, p_175852_6_);
      return (canVillageGoDeeper(var8)) && (StructureComponent.findIntersecting(p_175852_1_, var8) == null) ? new Field2(p_175852_0_, p_175852_7_, p_175852_2_, var8, p_175852_6_) : null;
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 4 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 0, 1, 0, 6, 4, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 1, 2, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 4, 0, 1, 5, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 0, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 6, 0, 0, 6, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 0, 5, 0, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 8, 5, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 3, 0, 1, 3, 0, 7, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);
      

      for (int var4 = 1; var4 <= 7; var4++)
      {
        func_175811_a(worldIn, cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 1, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 2, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 4, 1, var4, p_74875_3_);
        func_175811_a(worldIn, cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(p_74875_2_, 2, 7)), 5, 1, var4, p_74875_3_);
      }
      
      for (var4 = 0; var4 < 9; var4++)
      {
        for (int var5 = 0; var5 < 7; var5++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var5, 4, var4, p_74875_3_);
          func_175808_b(worldIn, Blocks.dirt.getDefaultState(), var5, -1, var4, p_74875_3_);
        }
      }
      
      return true;
    }
  }
  
  public static class Hall extends StructureVillagePieces.Village
  {
    private static final String __OBFID = "CL_00000522";
    
    public Hall() {}
    
    public Hall(StructureVillagePieces.Start p_i45567_1_, int p_i45567_2_, Random p_i45567_3_, StructureBoundingBox p_i45567_4_, EnumFacing p_i45567_5_)
    {
      super(p_i45567_2_);
      coordBaseMode = p_i45567_5_;
      boundingBox = p_i45567_4_;
    }
    
    public static Hall func_175857_a(StructureVillagePieces.Start p_175857_0_, List p_175857_1_, Random p_175857_2_, int p_175857_3_, int p_175857_4_, int p_175857_5_, EnumFacing p_175857_6_, int p_175857_7_)
    {
      StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175857_3_, p_175857_4_, p_175857_5_, 0, 0, 0, 9, 7, 11, p_175857_6_);
      return (canVillageGoDeeper(var8)) && (StructureComponent.findIntersecting(p_175857_1_, var8) == null) ? new Hall(p_175857_0_, p_175857_7_, p_175857_2_, var8, p_175857_6_) : null;
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 7 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 1, 1, 1, 7, 4, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 2, 1, 6, 8, 4, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 2, 0, 6, 8, 0, 10, Blocks.dirt.getDefaultState(), Blocks.dirt.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 6, 0, 6, p_74875_3_);
      func_175804_a(worldIn, p_74875_3_, 2, 1, 6, 2, 1, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 8, 1, 6, 8, 1, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 3, 1, 10, 7, 1, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 1, 7, 0, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 0, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 8, 0, 0, 8, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 0, 7, 1, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 5, 7, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 2, 0, 7, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 2, 5, 7, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 4, 1, 8, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 4, 4, 8, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 5, 2, 8, 5, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 0, 4, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 0, 4, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 8, 4, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 8, 4, 3, p_74875_3_);
      int var4 = getMetadataWithOffset(Blocks.oak_stairs, 3);
      int var5 = getMetadataWithOffset(Blocks.oak_stairs, 2);
      


      for (int var6 = -1; var6 <= 2; var6++)
      {
        for (int var7 = 0; var7 <= 8; var7++)
        {
          func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var4), var7, 4 + var6, var6, p_74875_3_);
          func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var5), var7, 4 + var6, 5 - var6, p_74875_3_);
        }
      }
      
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 0, 2, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 0, 2, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 8, 2, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 8, 2, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 3, 2, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 6, 2, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 2, 1, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), 2, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 1, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.oak_stairs, 3)), 2, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.oak_stairs, 1)), 1, 1, 3, p_74875_3_);
      func_175804_a(worldIn, p_74875_3_, 5, 0, 1, 7, 0, 3, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.double_stone_slab.getDefaultState(), 6, 1, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.double_stone_slab.getDefaultState(), 6, 1, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 2, 1, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 2, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode), 2, 3, 1, p_74875_3_);
      func_175810_a(worldIn, p_74875_3_, p_74875_2_, 2, 1, 0, EnumFacing.getHorizontal(getMetadataWithOffset(Blocks.oak_door, 1)));
      
      if ((func_175807_a(worldIn, 2, 0, -1, p_74875_3_).getBlock().getMaterial() == Material.air) && (func_175807_a(worldIn, 2, -1, -1, p_74875_3_).getBlock().getMaterial() != Material.air))
      {
        func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, p_74875_3_);
      }
      
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 6, 1, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 6, 2, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode.getOpposite()), 6, 3, 4, p_74875_3_);
      func_175810_a(worldIn, p_74875_3_, p_74875_2_, 6, 1, 5, EnumFacing.getHorizontal(getMetadataWithOffset(Blocks.oak_door, 1)));
      
      for (var6 = 0; var6 < 5; var6++)
      {
        for (int var7 = 0; var7 < 9; var7++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var7, 7, var6, p_74875_3_);
          func_175808_b(worldIn, Blocks.cobblestone.getDefaultState(), var7, -1, var6, p_74875_3_);
        }
      }
      
      spawnVillagers(worldIn, p_74875_3_, 4, 1, 2, 2);
      return true;
    }
    
    protected int func_180779_c(int p_180779_1_, int p_180779_2_)
    {
      return p_180779_1_ == 0 ? 4 : super.func_180779_c(p_180779_1_, p_180779_2_);
    }
  }
  
  public static class House1 extends StructureVillagePieces.Village
  {
    private static final String __OBFID = "CL_00000517";
    
    public House1() {}
    
    public House1(StructureVillagePieces.Start p_i45571_1_, int p_i45571_2_, Random p_i45571_3_, StructureBoundingBox p_i45571_4_, EnumFacing p_i45571_5_)
    {
      super(p_i45571_2_);
      coordBaseMode = p_i45571_5_;
      boundingBox = p_i45571_4_;
    }
    
    public static House1 func_175850_a(StructureVillagePieces.Start p_175850_0_, List p_175850_1_, Random p_175850_2_, int p_175850_3_, int p_175850_4_, int p_175850_5_, EnumFacing p_175850_6_, int p_175850_7_)
    {
      StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175850_3_, p_175850_4_, p_175850_5_, 0, 0, 0, 9, 9, 6, p_175850_6_);
      return (canVillageGoDeeper(var8)) && (StructureComponent.findIntersecting(p_175850_1_, var8) == null) ? new House1(p_175850_0_, p_175850_7_, p_175850_2_, var8, p_175850_6_) : null;
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 9 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 1, 1, 1, 7, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 8, 0, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 5, 0, 8, 5, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 6, 1, 8, 6, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 7, 2, 8, 7, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      int var4 = getMetadataWithOffset(Blocks.oak_stairs, 3);
      int var5 = getMetadataWithOffset(Blocks.oak_stairs, 2);
      


      for (int var6 = -1; var6 <= 2; var6++)
      {
        for (int var7 = 0; var7 <= 8; var7++)
        {
          func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var4), var7, 6 + var6, var6, p_74875_3_);
          func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var5), var7, 6 + var6, 5 - var6, p_74875_3_);
        }
      }
      
      func_175804_a(worldIn, p_74875_3_, 0, 1, 0, 0, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 1, 5, 8, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 8, 1, 0, 8, 1, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 2, 1, 0, 7, 1, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 2, 0, 0, 4, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 2, 5, 0, 4, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 8, 2, 5, 8, 4, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 8, 2, 0, 8, 4, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 2, 1, 0, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 2, 5, 7, 4, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 8, 2, 1, 8, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 2, 0, 7, 4, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 6, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 4, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 5, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 6, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 3, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 3, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 3, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 3, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 3, 2, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 6, 2, 5, p_74875_3_);
      func_175804_a(worldIn, p_74875_3_, 1, 4, 1, 7, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 4, 4, 7, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 3, 4, 7, 3, 4, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 7, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.oak_stairs, 0)), 7, 1, 3, p_74875_3_);
      var6 = getMetadataWithOffset(Blocks.oak_stairs, 3);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var6), 6, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var6), 5, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var6), 4, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var6), 3, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 6, 1, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), 6, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 1, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), 4, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.crafting_table.getDefaultState(), 7, 1, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 1, 1, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 1, 2, 0, p_74875_3_);
      func_175810_a(worldIn, p_74875_3_, p_74875_2_, 1, 1, 0, EnumFacing.getHorizontal(getMetadataWithOffset(Blocks.oak_door, 1)));
      
      if ((func_175807_a(worldIn, 1, 0, -1, p_74875_3_).getBlock().getMaterial() == Material.air) && (func_175807_a(worldIn, 1, -1, -1, p_74875_3_).getBlock().getMaterial() != Material.air))
      {
        func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 1, 0, -1, p_74875_3_);
      }
      
      for (int var7 = 0; var7 < 6; var7++)
      {
        for (int var8 = 0; var8 < 9; var8++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var8, 9, var7, p_74875_3_);
          func_175808_b(worldIn, Blocks.cobblestone.getDefaultState(), var8, -1, var7, p_74875_3_);
        }
      }
      
      spawnVillagers(worldIn, p_74875_3_, 2, 1, 2, 1);
      return true;
    }
    
    protected int func_180779_c(int p_180779_1_, int p_180779_2_)
    {
      return 1;
    }
  }
  
  public static class House2 extends StructureVillagePieces.Village
  {
    private static final List villageBlacksmithChestContents = com.google.common.collect.Lists.newArrayList(new WeightedRandomChestContent[] { new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5), new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15), new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15), new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_sword, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_chestplate, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_helmet, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_leggings, 0, 1, 1, 5), new WeightedRandomChestContent(Items.iron_boots, 0, 1, 1, 5), new WeightedRandomChestContent(net.minecraft.item.Item.getItemFromBlock(Blocks.obsidian), 0, 3, 7, 5), new WeightedRandomChestContent(net.minecraft.item.Item.getItemFromBlock(Blocks.sapling), 0, 3, 7, 5), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1) });
    private boolean hasMadeChest;
    private static final String __OBFID = "CL_00000526";
    
    public House2() {}
    
    public House2(StructureVillagePieces.Start p_i45563_1_, int p_i45563_2_, Random p_i45563_3_, StructureBoundingBox p_i45563_4_, EnumFacing p_i45563_5_)
    {
      super(p_i45563_2_);
      coordBaseMode = p_i45563_5_;
      boundingBox = p_i45563_4_;
    }
    
    public static House2 func_175855_a(StructureVillagePieces.Start p_175855_0_, List p_175855_1_, Random p_175855_2_, int p_175855_3_, int p_175855_4_, int p_175855_5_, EnumFacing p_175855_6_, int p_175855_7_)
    {
      StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175855_3_, p_175855_4_, p_175855_5_, 0, 0, 0, 10, 6, 7, p_175855_6_);
      return (canVillageGoDeeper(var8)) && (StructureComponent.findIntersecting(p_175855_1_, var8) == null) ? new House2(p_175855_0_, p_175855_7_, p_175855_2_, var8, p_175855_6_) : null;
    }
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      super.writeStructureToNBT(p_143012_1_);
      p_143012_1_.setBoolean("Chest", hasMadeChest);
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      super.readStructureFromNBT(p_143011_1_);
      hasMadeChest = p_143011_1_.getBoolean("Chest");
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 6 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 0, 1, 0, 9, 4, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 9, 0, 6, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 4, 0, 9, 4, 6, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 5, 0, 9, 5, 6, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 5, 1, 8, 5, 5, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 1, 0, 2, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 1, 0, 0, 4, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 3, 1, 0, 3, 4, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 1, 6, 0, 4, 6, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 3, 3, 1, p_74875_3_);
      func_175804_a(worldIn, p_74875_3_, 3, 1, 2, 3, 3, 2, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 4, 1, 3, 5, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 1, 1, 0, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 1, 6, 5, 3, 6, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 5, 1, 0, 5, 3, 0, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 9, 1, 0, 9, 3, 0, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 6, 1, 4, 9, 4, 6, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.flowing_lava.getDefaultState(), 7, 1, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.flowing_lava.getDefaultState(), 8, 1, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.iron_bars.getDefaultState(), 9, 2, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.iron_bars.getDefaultState(), 9, 2, 4, p_74875_3_);
      func_175804_a(worldIn, p_74875_3_, 7, 2, 4, 8, 2, 5, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 6, 1, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.furnace.getDefaultState(), 6, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.furnace.getDefaultState(), 6, 3, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.double_stone_slab.getDefaultState(), 8, 1, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 2, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), 2, 2, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 1, 1, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.oak_stairs, 3)), 2, 1, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.oak_stairs, 1)), 1, 1, 4, p_74875_3_);
      
      if ((!hasMadeChest) && (p_74875_3_.func_175898_b(new BlockPos(getXWithOffset(5, 5), getYWithOffset(1), getZWithOffset(5, 5)))))
      {
        hasMadeChest = true;
        func_180778_a(worldIn, p_74875_3_, p_74875_2_, 5, 1, 5, villageBlacksmithChestContents, 3 + p_74875_2_.nextInt(6));
      }
      


      for (int var4 = 6; var4 <= 8; var4++)
      {
        if ((func_175807_a(worldIn, var4, 0, -1, p_74875_3_).getBlock().getMaterial() == Material.air) && (func_175807_a(worldIn, var4, -1, -1, p_74875_3_).getBlock().getMaterial() != Material.air))
        {
          func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), var4, 0, -1, p_74875_3_);
        }
      }
      
      for (var4 = 0; var4 < 7; var4++)
      {
        for (int var5 = 0; var5 < 10; var5++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var5, 6, var4, p_74875_3_);
          func_175808_b(worldIn, Blocks.cobblestone.getDefaultState(), var5, -1, var4, p_74875_3_);
        }
      }
      
      spawnVillagers(worldIn, p_74875_3_, 7, 1, 1, 1);
      return true;
    }
    
    protected int func_180779_c(int p_180779_1_, int p_180779_2_)
    {
      return 3;
    }
  }
  
  public static class House3 extends StructureVillagePieces.Village
  {
    private static final String __OBFID = "CL_00000530";
    
    public House3() {}
    
    public House3(StructureVillagePieces.Start p_i45561_1_, int p_i45561_2_, Random p_i45561_3_, StructureBoundingBox p_i45561_4_, EnumFacing p_i45561_5_)
    {
      super(p_i45561_2_);
      coordBaseMode = p_i45561_5_;
      boundingBox = p_i45561_4_;
    }
    
    public static House3 func_175849_a(StructureVillagePieces.Start p_175849_0_, List p_175849_1_, Random p_175849_2_, int p_175849_3_, int p_175849_4_, int p_175849_5_, EnumFacing p_175849_6_, int p_175849_7_)
    {
      StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175849_3_, p_175849_4_, p_175849_5_, 0, 0, 0, 9, 7, 12, p_175849_6_);
      return (canVillageGoDeeper(var8)) && (StructureComponent.findIntersecting(p_175849_1_, var8) == null) ? new House3(p_175849_0_, p_175849_7_, p_175849_2_, var8, p_175849_6_) : null;
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 7 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 1, 1, 1, 7, 4, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 2, 1, 6, 8, 4, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 2, 0, 5, 8, 0, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 1, 7, 0, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 0, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 8, 0, 0, 8, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 0, 7, 2, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 5, 2, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 2, 0, 6, 2, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 3, 0, 10, 7, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 2, 0, 7, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 2, 5, 2, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 4, 1, 8, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 4, 4, 3, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 5, 2, 8, 5, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 0, 4, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 0, 4, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 8, 4, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 8, 4, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 8, 4, 4, p_74875_3_);
      int var4 = getMetadataWithOffset(Blocks.oak_stairs, 3);
      int var5 = getMetadataWithOffset(Blocks.oak_stairs, 2);
      


      for (int var6 = -1; var6 <= 2; var6++)
      {
        for (int var7 = 0; var7 <= 8; var7++)
        {
          func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var4), var7, 4 + var6, var6, p_74875_3_);
          
          if (((var6 > -1) || (var7 <= 1)) && ((var6 > 0) || (var7 <= 3)) && ((var6 > 1) || (var7 <= 4) || (var7 >= 6)))
          {
            func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var5), var7, 4 + var6, 5 - var6, p_74875_3_);
          }
        }
      }
      
      func_175804_a(worldIn, p_74875_3_, 3, 4, 5, 3, 4, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 7, 4, 2, 7, 4, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 4, 5, 4, 4, 5, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 6, 5, 4, 6, 5, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 5, 6, 3, 5, 6, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      var6 = getMetadataWithOffset(Blocks.oak_stairs, 0);
      

      for (int var7 = 4; var7 >= 1; var7--)
      {
        func_175811_a(worldIn, Blocks.planks.getDefaultState(), var7, 2 + var7, 7 - var7, p_74875_3_);
        
        for (int var8 = 8 - var7; var8 <= 10; var8++)
        {
          func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var6), var7, 2 + var7, var8, p_74875_3_);
        }
      }
      
      var7 = getMetadataWithOffset(Blocks.oak_stairs, 1);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 6, 6, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 7, 5, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var7), 6, 6, 4, p_74875_3_);
      

      for (int var8 = 6; var8 <= 8; var8++)
      {
        for (int var9 = 5; var9 <= 10; var9++)
        {
          func_175811_a(worldIn, Blocks.oak_stairs.getStateFromMeta(var7), var8, 12 - var8, var9, p_74875_3_);
        }
      }
      
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 0, 2, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 0, 2, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 4, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 6, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 8, 2, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 8, 2, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 8, 2, 5, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 8, 2, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 7, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 8, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 8, 2, 9, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 2, 2, 6, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 7, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 8, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 2, 2, 9, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 4, 4, 10, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 5, 4, 10, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 6, 4, 10, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 5, 5, 10, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 2, 1, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 2, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode), 2, 3, 1, p_74875_3_);
      func_175810_a(worldIn, p_74875_3_, p_74875_2_, 2, 1, 0, EnumFacing.getHorizontal(getMetadataWithOffset(Blocks.oak_door, 1)));
      func_175804_a(worldIn, p_74875_3_, 1, 0, -1, 3, 2, -1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      
      if ((func_175807_a(worldIn, 2, 0, -1, p_74875_3_).getBlock().getMaterial() == Material.air) && (func_175807_a(worldIn, 2, -1, -1, p_74875_3_).getBlock().getMaterial() != Material.air))
      {
        func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, p_74875_3_);
      }
      
      for (var8 = 0; var8 < 5; var8++)
      {
        for (int var9 = 0; var9 < 9; var9++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var9, 7, var8, p_74875_3_);
          func_175808_b(worldIn, Blocks.cobblestone.getDefaultState(), var9, -1, var8, p_74875_3_);
        }
      }
      
      for (var8 = 5; var8 < 11; var8++)
      {
        for (int var9 = 2; var9 < 9; var9++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var9, 7, var8, p_74875_3_);
          func_175808_b(worldIn, Blocks.cobblestone.getDefaultState(), var9, -1, var8, p_74875_3_);
        }
      }
      
      spawnVillagers(worldIn, p_74875_3_, 4, 1, 2, 2);
      return true;
    }
  }
  
  public static class House4Garden extends StructureVillagePieces.Village
  {
    private boolean isRoofAccessible;
    private static final String __OBFID = "CL_00000523";
    
    public House4Garden() {}
    
    public House4Garden(StructureVillagePieces.Start p_i45566_1_, int p_i45566_2_, Random p_i45566_3_, StructureBoundingBox p_i45566_4_, EnumFacing p_i45566_5_)
    {
      super(p_i45566_2_);
      coordBaseMode = p_i45566_5_;
      boundingBox = p_i45566_4_;
      isRoofAccessible = p_i45566_3_.nextBoolean();
    }
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      super.writeStructureToNBT(p_143012_1_);
      p_143012_1_.setBoolean("Terrace", isRoofAccessible);
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      super.readStructureFromNBT(p_143011_1_);
      isRoofAccessible = p_143011_1_.getBoolean("Terrace");
    }
    
    public static House4Garden func_175858_a(StructureVillagePieces.Start p_175858_0_, List p_175858_1_, Random p_175858_2_, int p_175858_3_, int p_175858_4_, int p_175858_5_, EnumFacing p_175858_6_, int p_175858_7_)
    {
      StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175858_3_, p_175858_4_, p_175858_5_, 0, 0, 0, 5, 6, 5, p_175858_6_);
      return StructureComponent.findIntersecting(p_175858_1_, var8) != null ? null : new House4Garden(p_175858_0_, p_175858_7_, p_175858_2_, var8, p_175858_6_);
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 6 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 4, 0, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 4, 0, 4, 4, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 4, 1, 3, 4, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 0, 1, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 0, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 0, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 4, 1, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 4, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 4, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 0, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 0, 2, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 0, 3, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 4, 1, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 4, 2, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.cobblestone.getDefaultState(), 4, 3, 4, p_74875_3_);
      func_175804_a(worldIn, p_74875_3_, 0, 1, 1, 0, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 4, 1, 1, 4, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 1, 4, 3, 3, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 1, 1, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 1, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 1, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 2, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 3, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 3, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.planks.getDefaultState(), 3, 1, 0, p_74875_3_);
      
      if ((func_175807_a(worldIn, 2, 0, -1, p_74875_3_).getBlock().getMaterial() == Material.air) && (func_175807_a(worldIn, 2, -1, -1, p_74875_3_).getBlock().getMaterial() != Material.air))
      {
        func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, p_74875_3_);
      }
      
      func_175804_a(worldIn, p_74875_3_, 1, 1, 1, 3, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      
      if (isRoofAccessible)
      {
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 0, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 1, 5, 0, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 2, 5, 0, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 3, 5, 0, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 0, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 4, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 1, 5, 4, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 2, 5, 4, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 3, 5, 4, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 4, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 1, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 2, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 3, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 1, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 2, p_74875_3_);
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 3, p_74875_3_);
      }
      


      if (isRoofAccessible)
      {
        int var4 = getMetadataWithOffset(Blocks.ladder, 3);
        func_175811_a(worldIn, Blocks.ladder.getStateFromMeta(var4), 3, 1, 3, p_74875_3_);
        func_175811_a(worldIn, Blocks.ladder.getStateFromMeta(var4), 3, 2, 3, p_74875_3_);
        func_175811_a(worldIn, Blocks.ladder.getStateFromMeta(var4), 3, 3, 3, p_74875_3_);
        func_175811_a(worldIn, Blocks.ladder.getStateFromMeta(var4), 3, 4, 3, p_74875_3_);
      }
      
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode), 2, 3, 1, p_74875_3_);
      
      for (int var4 = 0; var4 < 5; var4++)
      {
        for (int var5 = 0; var5 < 5; var5++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var5, 6, var4, p_74875_3_);
          func_175808_b(worldIn, Blocks.cobblestone.getDefaultState(), var5, -1, var4, p_74875_3_);
        }
      }
      
      spawnVillagers(worldIn, p_74875_3_, 1, 1, 2, 1);
      return true;
    }
  }
  
  public static class Path extends StructureVillagePieces.Road
  {
    private int averageGroundLevel;
    private static final String __OBFID = "CL_00000528";
    
    public Path() {}
    
    public Path(StructureVillagePieces.Start p_i45562_1_, int p_i45562_2_, Random p_i45562_3_, StructureBoundingBox p_i45562_4_, EnumFacing p_i45562_5_)
    {
      super(p_i45562_2_);
      coordBaseMode = p_i45562_5_;
      boundingBox = p_i45562_4_;
      averageGroundLevel = Math.max(p_i45562_4_.getXSize(), p_i45562_4_.getZSize());
    }
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      super.writeStructureToNBT(p_143012_1_);
      p_143012_1_.setInteger("Length", averageGroundLevel);
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      super.readStructureFromNBT(p_143011_1_);
      averageGroundLevel = p_143011_1_.getInteger("Length");
    }
    
    public void buildComponent(StructureComponent p_74861_1_, List p_74861_2_, Random p_74861_3_)
    {
      boolean var4 = false;
      


      for (int var5 = p_74861_3_.nextInt(5); var5 < averageGroundLevel - 8; var5 += 2 + p_74861_3_.nextInt(5))
      {
        StructureComponent var6 = getNextComponentNN((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, var5);
        
        if (var6 != null)
        {
          var5 += Math.max(boundingBox.getXSize(), boundingBox.getZSize());
          var4 = true;
        }
      }
      
      for (var5 = p_74861_3_.nextInt(5); var5 < averageGroundLevel - 8; var5 += 2 + p_74861_3_.nextInt(5))
      {
        StructureComponent var6 = getNextComponentPP((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, var5);
        
        if (var6 != null)
        {
          var5 += Math.max(boundingBox.getXSize(), boundingBox.getZSize());
          var4 = true;
        }
      }
      
      if ((var4) && (p_74861_3_.nextInt(3) > 0) && (coordBaseMode != null))
      {
        switch (StructureVillagePieces.SwitchEnumFacing.field_176064_a[coordBaseMode.ordinal()])
        {
        case 1: 
          StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY, boundingBox.minZ, EnumFacing.WEST, getComponentType());
          break;
        
        case 2: 
          StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY, boundingBox.maxZ - 2, EnumFacing.WEST, getComponentType());
          break;
        
        case 3: 
          StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX, boundingBox.minY, boundingBox.minZ - 1, EnumFacing.NORTH, getComponentType());
          break;
        
        case 4: 
          StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX - 2, boundingBox.minY, boundingBox.minZ - 1, EnumFacing.NORTH, getComponentType());
        }
        
      }
      if ((var4) && (p_74861_3_.nextInt(3) > 0) && (coordBaseMode != null))
      {
        switch (StructureVillagePieces.SwitchEnumFacing.field_176064_a[coordBaseMode.ordinal()])
        {
        case 1: 
          StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY, boundingBox.minZ, EnumFacing.EAST, getComponentType());
          break;
        
        case 2: 
          StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY, boundingBox.maxZ - 2, EnumFacing.EAST, getComponentType());
          break;
        
        case 3: 
          StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX, boundingBox.minY, boundingBox.maxZ + 1, EnumFacing.SOUTH, getComponentType());
          break;
        
        case 4: 
          StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX - 2, boundingBox.minY, boundingBox.maxZ + 1, EnumFacing.SOUTH, getComponentType());
        }
      }
    }
    
    public static StructureBoundingBox func_175848_a(StructureVillagePieces.Start p_175848_0_, List p_175848_1_, Random p_175848_2_, int p_175848_3_, int p_175848_4_, int p_175848_5_, EnumFacing p_175848_6_)
    {
      for (int var7 = 7 * MathHelper.getRandomIntegerInRange(p_175848_2_, 3, 5); var7 >= 7; var7 -= 7)
      {
        StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175848_3_, p_175848_4_, p_175848_5_, 0, 0, 0, 3, 3, var7, p_175848_6_);
        
        if (StructureComponent.findIntersecting(p_175848_1_, var8) == null)
        {
          return var8;
        }
      }
      
      return null;
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      IBlockState var4 = func_175847_a(Blocks.gravel.getDefaultState());
      IBlockState var5 = func_175847_a(Blocks.cobblestone.getDefaultState());
      
      for (int var6 = boundingBox.minX; var6 <= boundingBox.maxX; var6++)
      {
        for (int var7 = boundingBox.minZ; var7 <= boundingBox.maxZ; var7++)
        {
          BlockPos var8 = new BlockPos(var6, 64, var7);
          
          if (p_74875_3_.func_175898_b(var8))
          {
            var8 = worldIn.func_175672_r(var8).offsetDown();
            worldIn.setBlockState(var8, var4, 2);
            worldIn.setBlockState(var8.offsetDown(), var5, 2);
          }
        }
      }
      
      return true;
    }
  }
  
  public static class PieceWeight
  {
    public Class villagePieceClass;
    public final int villagePieceWeight;
    public int villagePiecesSpawned;
    public int villagePiecesLimit;
    private static final String __OBFID = "CL_00000521";
    
    public PieceWeight(Class p_i2098_1_, int p_i2098_2_, int p_i2098_3_)
    {
      villagePieceClass = p_i2098_1_;
      villagePieceWeight = p_i2098_2_;
      villagePiecesLimit = p_i2098_3_;
    }
    
    public boolean canSpawnMoreVillagePiecesOfType(int p_75085_1_)
    {
      return (villagePiecesLimit == 0) || (villagePiecesSpawned < villagePiecesLimit);
    }
    
    public boolean canSpawnMoreVillagePieces()
    {
      return (villagePiecesLimit == 0) || (villagePiecesSpawned < villagePiecesLimit);
    }
  }
  
  public static abstract class Road extends StructureVillagePieces.Village
  {
    private static final String __OBFID = "CL_00000532";
    
    public Road() {}
    
    protected Road(StructureVillagePieces.Start p_i2108_1_, int p_i2108_2_)
    {
      super(p_i2108_2_);
    }
  }
  
  public static class Start extends StructureVillagePieces.Well
  {
    public WorldChunkManager worldChunkMngr;
    public boolean inDesert;
    public int terrainType;
    public StructureVillagePieces.PieceWeight structVillagePieceWeight;
    public List structureVillageWeightedPieceList;
    public List field_74932_i = com.google.common.collect.Lists.newArrayList();
    public List field_74930_j = com.google.common.collect.Lists.newArrayList();
    private static final String __OBFID = "CL_00000527";
    
    public Start() {}
    
    public Start(WorldChunkManager p_i2104_1_, int p_i2104_2_, Random p_i2104_3_, int p_i2104_4_, int p_i2104_5_, List p_i2104_6_, int p_i2104_7_)
    {
      super(0, p_i2104_3_, p_i2104_4_, p_i2104_5_);
      worldChunkMngr = p_i2104_1_;
      structureVillageWeightedPieceList = p_i2104_6_;
      terrainType = p_i2104_7_;
      net.minecraft.world.biome.BiomeGenBase var8 = p_i2104_1_.func_180300_a(new BlockPos(p_i2104_4_, 0, p_i2104_5_), net.minecraft.world.biome.BiomeGenBase.field_180279_ad);
      inDesert = ((var8 == net.minecraft.world.biome.BiomeGenBase.desert) || (var8 == net.minecraft.world.biome.BiomeGenBase.desertHills));
      func_175846_a(inDesert);
    }
    
    public WorldChunkManager getWorldChunkManager()
    {
      return worldChunkMngr;
    }
  }
  
  static final class SwitchEnumFacing
  {
    static final int[] field_176064_a = new int[EnumFacing.values().length];
    private static final String __OBFID = "CL_00001968";
    
    static
    {
      try
      {
        field_176064_a[EnumFacing.NORTH.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError1) {}
      



      try
      {
        field_176064_a[EnumFacing.SOUTH.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {}
      



      try
      {
        field_176064_a[EnumFacing.WEST.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {}
      



      try
      {
        field_176064_a[EnumFacing.EAST.ordinal()] = 4;
      }
      catch (NoSuchFieldError localNoSuchFieldError4) {}
    }
    
    SwitchEnumFacing() {}
  }
  
  public static class Torch
    extends StructureVillagePieces.Village
  {
    private static final String __OBFID = "CL_00000520";
    
    public Torch() {}
    
    public Torch(StructureVillagePieces.Start p_i45568_1_, int p_i45568_2_, Random p_i45568_3_, StructureBoundingBox p_i45568_4_, EnumFacing p_i45568_5_)
    {
      super(p_i45568_2_);
      coordBaseMode = p_i45568_5_;
      boundingBox = p_i45568_4_;
    }
    
    public static StructureBoundingBox func_175856_a(StructureVillagePieces.Start p_175856_0_, List p_175856_1_, Random p_175856_2_, int p_175856_3_, int p_175856_4_, int p_175856_5_, EnumFacing p_175856_6_)
    {
      StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(p_175856_3_, p_175856_4_, p_175856_5_, 0, 0, 0, 3, 4, 2, p_175856_6_);
      return StructureComponent.findIntersecting(p_175856_1_, var7) != null ? null : var7;
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 4 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 2, 3, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 1, 0, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 1, 1, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 1, 2, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.wool.getStateFromMeta(net.minecraft.item.EnumDyeColor.WHITE.getDyeColorDamage()), 1, 3, 0, p_74875_3_);
      boolean var4 = (coordBaseMode == EnumFacing.EAST) || (coordBaseMode == EnumFacing.NORTH);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode.rotateY()), var4 ? 2 : 0, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode), 1, 3, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode.rotateYCCW()), var4 ? 0 : 2, 3, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING_PROP, coordBaseMode.getOpposite()), 1, 3, -1, p_74875_3_);
      return true;
    }
  }
  
  static abstract class Village extends StructureComponent
  {
    protected int field_143015_k = -1;
    private int villagersSpawned;
    private boolean field_143014_b;
    private static final String __OBFID = "CL_00000531";
    
    public Village() {}
    
    protected Village(StructureVillagePieces.Start p_i2107_1_, int p_i2107_2_)
    {
      super();
      
      if (p_i2107_1_ != null)
      {
        field_143014_b = inDesert;
      }
    }
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      p_143012_1_.setInteger("HPos", field_143015_k);
      p_143012_1_.setInteger("VCount", villagersSpawned);
      p_143012_1_.setBoolean("Desert", field_143014_b);
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      field_143015_k = p_143011_1_.getInteger("HPos");
      villagersSpawned = p_143011_1_.getInteger("VCount");
      field_143014_b = p_143011_1_.getBoolean("Desert");
    }
    
    protected StructureComponent getNextComponentNN(StructureVillagePieces.Start p_74891_1_, List p_74891_2_, Random p_74891_3_, int p_74891_4_, int p_74891_5_)
    {
      if (coordBaseMode != null)
      {
        switch (StructureVillagePieces.SwitchEnumFacing.field_176064_a[coordBaseMode.ordinal()])
        {
        case 1: 
          return StructureVillagePieces.func_176066_d(p_74891_1_, p_74891_2_, p_74891_3_, boundingBox.minX - 1, boundingBox.minY + p_74891_4_, boundingBox.minZ + p_74891_5_, EnumFacing.WEST, getComponentType());
        
        case 2: 
          return StructureVillagePieces.func_176066_d(p_74891_1_, p_74891_2_, p_74891_3_, boundingBox.minX - 1, boundingBox.minY + p_74891_4_, boundingBox.minZ + p_74891_5_, EnumFacing.WEST, getComponentType());
        
        case 3: 
          return StructureVillagePieces.func_176066_d(p_74891_1_, p_74891_2_, p_74891_3_, boundingBox.minX + p_74891_5_, boundingBox.minY + p_74891_4_, boundingBox.minZ - 1, EnumFacing.NORTH, getComponentType());
        
        case 4: 
          return StructureVillagePieces.func_176066_d(p_74891_1_, p_74891_2_, p_74891_3_, boundingBox.minX + p_74891_5_, boundingBox.minY + p_74891_4_, boundingBox.minZ - 1, EnumFacing.NORTH, getComponentType());
        }
        
      }
      return null;
    }
    
    protected StructureComponent getNextComponentPP(StructureVillagePieces.Start p_74894_1_, List p_74894_2_, Random p_74894_3_, int p_74894_4_, int p_74894_5_)
    {
      if (coordBaseMode != null)
      {
        switch (StructureVillagePieces.SwitchEnumFacing.field_176064_a[coordBaseMode.ordinal()])
        {
        case 1: 
          return StructureVillagePieces.func_176066_d(p_74894_1_, p_74894_2_, p_74894_3_, boundingBox.maxX + 1, boundingBox.minY + p_74894_4_, boundingBox.minZ + p_74894_5_, EnumFacing.EAST, getComponentType());
        
        case 2: 
          return StructureVillagePieces.func_176066_d(p_74894_1_, p_74894_2_, p_74894_3_, boundingBox.maxX + 1, boundingBox.minY + p_74894_4_, boundingBox.minZ + p_74894_5_, EnumFacing.EAST, getComponentType());
        
        case 3: 
          return StructureVillagePieces.func_176066_d(p_74894_1_, p_74894_2_, p_74894_3_, boundingBox.minX + p_74894_5_, boundingBox.minY + p_74894_4_, boundingBox.maxZ + 1, EnumFacing.SOUTH, getComponentType());
        
        case 4: 
          return StructureVillagePieces.func_176066_d(p_74894_1_, p_74894_2_, p_74894_3_, boundingBox.minX + p_74894_5_, boundingBox.minY + p_74894_4_, boundingBox.maxZ + 1, EnumFacing.SOUTH, getComponentType());
        }
        
      }
      return null;
    }
    
    protected int getAverageGroundLevel(World worldIn, StructureBoundingBox p_74889_2_)
    {
      int var3 = 0;
      int var4 = 0;
      
      for (int var5 = boundingBox.minZ; var5 <= boundingBox.maxZ; var5++)
      {
        for (int var6 = boundingBox.minX; var6 <= boundingBox.maxX; var6++)
        {
          BlockPos var7 = new BlockPos(var6, 64, var5);
          
          if (p_74889_2_.func_175898_b(var7))
          {
            var3 += Math.max(worldIn.func_175672_r(var7).getY(), provider.getAverageGroundLevel());
            var4++;
          }
        }
      }
      
      if (var4 == 0)
      {
        return -1;
      }
      

      return var3 / var4;
    }
    

    protected static boolean canVillageGoDeeper(StructureBoundingBox p_74895_0_)
    {
      return (p_74895_0_ != null) && (minY > 10);
    }
    
    protected void spawnVillagers(World worldIn, StructureBoundingBox p_74893_2_, int p_74893_3_, int p_74893_4_, int p_74893_5_, int p_74893_6_)
    {
      if (villagersSpawned < p_74893_6_)
      {
        for (int var7 = villagersSpawned; var7 < p_74893_6_; var7++)
        {
          int var8 = getXWithOffset(p_74893_3_ + var7, p_74893_5_);
          int var9 = getYWithOffset(p_74893_4_);
          int var10 = getZWithOffset(p_74893_3_ + var7, p_74893_5_);
          
          if (!p_74893_2_.func_175898_b(new BlockPos(var8, var9, var10))) {
            break;
          }
          

          villagersSpawned += 1;
          EntityVillager var11 = new EntityVillager(worldIn);
          var11.setLocationAndAngles(var8 + 0.5D, var9, var10 + 0.5D, 0.0F, 0.0F);
          var11.func_180482_a(worldIn.getDifficultyForLocation(new BlockPos(var11)), null);
          var11.setProfession(func_180779_c(var7, var11.getProfession()));
          worldIn.spawnEntityInWorld(var11);
        }
      }
    }
    
    protected int func_180779_c(int p_180779_1_, int p_180779_2_)
    {
      return p_180779_2_;
    }
    
    protected IBlockState func_175847_a(IBlockState p_175847_1_)
    {
      if (field_143014_b)
      {
        if ((p_175847_1_.getBlock() == Blocks.log) || (p_175847_1_.getBlock() == Blocks.log2))
        {
          return Blocks.sandstone.getDefaultState();
        }
        
        if (p_175847_1_.getBlock() == Blocks.cobblestone)
        {
          return Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.DEFAULT.func_176675_a());
        }
        
        if (p_175847_1_.getBlock() == Blocks.planks)
        {
          return Blocks.sandstone.getStateFromMeta(BlockSandStone.EnumType.SMOOTH.func_176675_a());
        }
        
        if (p_175847_1_.getBlock() == Blocks.oak_stairs)
        {
          return Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, p_175847_1_.getValue(BlockStairs.FACING));
        }
        
        if (p_175847_1_.getBlock() == Blocks.stone_stairs)
        {
          return Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, p_175847_1_.getValue(BlockStairs.FACING));
        }
        
        if (p_175847_1_.getBlock() == Blocks.gravel)
        {
          return Blocks.sandstone.getDefaultState();
        }
      }
      
      return p_175847_1_;
    }
    
    protected void func_175811_a(World worldIn, IBlockState p_175811_2_, int p_175811_3_, int p_175811_4_, int p_175811_5_, StructureBoundingBox p_175811_6_)
    {
      IBlockState var7 = func_175847_a(p_175811_2_);
      super.func_175811_a(worldIn, var7, p_175811_3_, p_175811_4_, p_175811_5_, p_175811_6_);
    }
    
    protected void func_175804_a(World worldIn, StructureBoundingBox p_175804_2_, int p_175804_3_, int p_175804_4_, int p_175804_5_, int p_175804_6_, int p_175804_7_, int p_175804_8_, IBlockState p_175804_9_, IBlockState p_175804_10_, boolean p_175804_11_)
    {
      IBlockState var12 = func_175847_a(p_175804_9_);
      IBlockState var13 = func_175847_a(p_175804_10_);
      super.func_175804_a(worldIn, p_175804_2_, p_175804_3_, p_175804_4_, p_175804_5_, p_175804_6_, p_175804_7_, p_175804_8_, var12, var13, p_175804_11_);
    }
    
    protected void func_175808_b(World worldIn, IBlockState p_175808_2_, int p_175808_3_, int p_175808_4_, int p_175808_5_, StructureBoundingBox p_175808_6_)
    {
      IBlockState var7 = func_175847_a(p_175808_2_);
      super.func_175808_b(worldIn, var7, p_175808_3_, p_175808_4_, p_175808_5_, p_175808_6_);
    }
    
    protected void func_175846_a(boolean p_175846_1_)
    {
      field_143014_b = p_175846_1_;
    }
  }
  
  public static class Well extends StructureVillagePieces.Village
  {
    private static final String __OBFID = "CL_00000533";
    
    public Well() {}
    
    public Well(StructureVillagePieces.Start p_i2109_1_, int p_i2109_2_, Random p_i2109_3_, int p_i2109_4_, int p_i2109_5_)
    {
      super(p_i2109_2_);
      coordBaseMode = net.minecraft.util.EnumFacing.Plane.HORIZONTAL.random(p_i2109_3_);
      
      switch (StructureVillagePieces.SwitchEnumFacing.field_176064_a[coordBaseMode.ordinal()])
      {
      case 1: 
      case 2: 
        boundingBox = new StructureBoundingBox(p_i2109_4_, 64, p_i2109_5_, p_i2109_4_ + 6 - 1, 78, p_i2109_5_ + 6 - 1);
        break;
      
      default: 
        boundingBox = new StructureBoundingBox(p_i2109_4_, 64, p_i2109_5_, p_i2109_4_ + 6 - 1, 78, p_i2109_5_ + 6 - 1);
      }
    }
    
    public void buildComponent(StructureComponent p_74861_1_, List p_74861_2_, Random p_74861_3_)
    {
      StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.maxY - 4, boundingBox.minZ + 1, EnumFacing.WEST, getComponentType());
      StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.maxY - 4, boundingBox.minZ + 1, EnumFacing.EAST, getComponentType());
      StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.maxY - 4, boundingBox.minZ - 1, EnumFacing.NORTH, getComponentType());
      StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.maxY - 4, boundingBox.maxZ + 1, EnumFacing.SOUTH, getComponentType());
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 3, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 1, 0, 1, 4, 12, 4, Blocks.cobblestone.getDefaultState(), Blocks.flowing_water.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 2, 12, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 3, 12, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 2, 12, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 3, 12, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 1, 13, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 1, 14, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 13, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 14, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 1, 13, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 1, 14, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 13, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), 4, 14, 4, p_74875_3_);
      func_175804_a(worldIn, p_74875_3_, 1, 15, 1, 4, 15, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      
      for (int var4 = 0; var4 <= 5; var4++)
      {
        for (int var5 = 0; var5 <= 5; var5++)
        {
          if ((var5 == 0) || (var5 == 5) || (var4 == 0) || (var4 == 5))
          {
            func_175811_a(worldIn, Blocks.gravel.getDefaultState(), var5, 11, var4, p_74875_3_);
            clearCurrentPositionBlocksUpwards(worldIn, var5, 12, var4, p_74875_3_);
          }
        }
      }
      
      return true;
    }
  }
  
  public static class WoodHut extends StructureVillagePieces.Village
  {
    private boolean isTallHouse;
    private int tablePosition;
    private static final String __OBFID = "CL_00000524";
    
    public WoodHut() {}
    
    public WoodHut(StructureVillagePieces.Start p_i45565_1_, int p_i45565_2_, Random p_i45565_3_, StructureBoundingBox p_i45565_4_, EnumFacing p_i45565_5_)
    {
      super(p_i45565_2_);
      coordBaseMode = p_i45565_5_;
      boundingBox = p_i45565_4_;
      isTallHouse = p_i45565_3_.nextBoolean();
      tablePosition = p_i45565_3_.nextInt(3);
    }
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      super.writeStructureToNBT(p_143012_1_);
      p_143012_1_.setInteger("T", tablePosition);
      p_143012_1_.setBoolean("C", isTallHouse);
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      super.readStructureFromNBT(p_143011_1_);
      tablePosition = p_143011_1_.getInteger("T");
      isTallHouse = p_143011_1_.getBoolean("C");
    }
    
    public static WoodHut func_175853_a(StructureVillagePieces.Start p_175853_0_, List p_175853_1_, Random p_175853_2_, int p_175853_3_, int p_175853_4_, int p_175853_5_, EnumFacing p_175853_6_, int p_175853_7_)
    {
      StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(p_175853_3_, p_175853_4_, p_175853_5_, 0, 0, 0, 4, 6, 5, p_175853_6_);
      return (canVillageGoDeeper(var8)) && (StructureComponent.findIntersecting(p_175853_1_, var8) == null) ? new WoodHut(p_175853_0_, p_175853_7_, p_175853_2_, var8, p_175853_6_) : null;
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (field_143015_k < 0)
      {
        field_143015_k = getAverageGroundLevel(worldIn, p_74875_3_);
        
        if (field_143015_k < 0)
        {
          return true;
        }
        
        boundingBox.offset(0, field_143015_k - boundingBox.maxY + 6 - 1, 0);
      }
      
      func_175804_a(worldIn, p_74875_3_, 1, 1, 1, 3, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 3, 0, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 0, 1, 2, 0, 3, Blocks.dirt.getDefaultState(), Blocks.dirt.getDefaultState(), false);
      
      if (isTallHouse)
      {
        func_175804_a(worldIn, p_74875_3_, 1, 4, 1, 2, 4, 3, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      }
      else
      {
        func_175804_a(worldIn, p_74875_3_, 1, 5, 1, 2, 5, 3, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      }
      
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 1, 4, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 2, 4, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 1, 4, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 2, 4, 4, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 0, 4, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 0, 4, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 0, 4, 3, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 3, 4, 1, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 3, 4, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.log.getDefaultState(), 3, 4, 3, p_74875_3_);
      func_175804_a(worldIn, p_74875_3_, 0, 1, 0, 0, 3, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 3, 1, 0, 3, 3, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 1, 4, 0, 3, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 3, 1, 4, 3, 3, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 1, 1, 0, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 3, 1, 1, 3, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 1, 0, 2, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 1, 1, 4, 2, 3, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, p_74875_3_);
      func_175811_a(worldIn, Blocks.glass_pane.getDefaultState(), 3, 2, 2, p_74875_3_);
      
      if (tablePosition > 0)
      {
        func_175811_a(worldIn, Blocks.oak_fence.getDefaultState(), tablePosition, 1, 3, p_74875_3_);
        func_175811_a(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), tablePosition, 2, 3, p_74875_3_);
      }
      
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 1, 1, 0, p_74875_3_);
      func_175811_a(worldIn, Blocks.air.getDefaultState(), 1, 2, 0, p_74875_3_);
      func_175810_a(worldIn, p_74875_3_, p_74875_2_, 1, 1, 0, EnumFacing.getHorizontal(getMetadataWithOffset(Blocks.oak_door, 1)));
      
      if ((func_175807_a(worldIn, 1, 0, -1, p_74875_3_).getBlock().getMaterial() == Material.air) && (func_175807_a(worldIn, 1, -1, -1, p_74875_3_).getBlock().getMaterial() != Material.air))
      {
        func_175811_a(worldIn, Blocks.stone_stairs.getStateFromMeta(getMetadataWithOffset(Blocks.stone_stairs, 3)), 1, 0, -1, p_74875_3_);
      }
      
      for (int var4 = 0; var4 < 5; var4++)
      {
        for (int var5 = 0; var5 < 4; var5++)
        {
          clearCurrentPositionBlocksUpwards(worldIn, var5, 6, var4, p_74875_3_);
          func_175808_b(worldIn, Blocks.cobblestone.getDefaultState(), var5, -1, var4, p_74875_3_);
        }
      }
      
      spawnVillagers(worldIn, p_74875_3_, 1, 1, 2, 1);
      return true;
    }
  }
}
