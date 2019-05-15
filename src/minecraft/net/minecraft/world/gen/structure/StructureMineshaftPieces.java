package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureMineshaftPieces
{
  private static final List field_175893_a = Lists.newArrayList(new WeightedRandomChestContent[] { new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5), new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5), new WeightedRandomChestContent(Items.dye, EnumDyeColor.BLUE.getDyeColorDamage(), 4, 9, 5), new WeightedRandomChestContent(Items.diamond, 0, 1, 2, 3), new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10), new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15), new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1), new WeightedRandomChestContent(net.minecraft.item.Item.getItemFromBlock(Blocks.rail), 0, 4, 8, 1), new WeightedRandomChestContent(Items.melon_seeds, 0, 2, 4, 10), new WeightedRandomChestContent(Items.pumpkin_seeds, 0, 2, 4, 10), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1) });
  private static final String __OBFID = "CL_00000444";
  
  public StructureMineshaftPieces() {}
  
  public static void registerStructurePieces() { MapGenStructureIO.registerStructureComponent(Corridor.class, "MSCorridor");
    MapGenStructureIO.registerStructureComponent(Cross.class, "MSCrossing");
    MapGenStructureIO.registerStructureComponent(Room.class, "MSRoom");
    MapGenStructureIO.registerStructureComponent(Stairs.class, "MSStairs");
  }
  
  private static StructureComponent func_175892_a(List p_175892_0_, Random p_175892_1_, int p_175892_2_, int p_175892_3_, int p_175892_4_, EnumFacing p_175892_5_, int p_175892_6_)
  {
    int var7 = p_175892_1_.nextInt(100);
    

    if (var7 >= 80)
    {
      StructureBoundingBox var8 = Cross.func_175813_a(p_175892_0_, p_175892_1_, p_175892_2_, p_175892_3_, p_175892_4_, p_175892_5_);
      
      if (var8 != null)
      {
        return new Cross(p_175892_6_, p_175892_1_, var8, p_175892_5_);
      }
    }
    else if (var7 >= 70)
    {
      StructureBoundingBox var8 = Stairs.func_175812_a(p_175892_0_, p_175892_1_, p_175892_2_, p_175892_3_, p_175892_4_, p_175892_5_);
      
      if (var8 != null)
      {
        return new Stairs(p_175892_6_, p_175892_1_, var8, p_175892_5_);
      }
    }
    else
    {
      StructureBoundingBox var8 = Corridor.func_175814_a(p_175892_0_, p_175892_1_, p_175892_2_, p_175892_3_, p_175892_4_, p_175892_5_);
      
      if (var8 != null)
      {
        return new Corridor(p_175892_6_, p_175892_1_, var8, p_175892_5_);
      }
    }
    
    return null;
  }
  
  private static StructureComponent func_175890_b(StructureComponent p_175890_0_, List p_175890_1_, Random p_175890_2_, int p_175890_3_, int p_175890_4_, int p_175890_5_, EnumFacing p_175890_6_, int p_175890_7_)
  {
    if (p_175890_7_ > 8)
    {
      return null;
    }
    if ((Math.abs(p_175890_3_ - getBoundingBoxminX) <= 80) && (Math.abs(p_175890_5_ - getBoundingBoxminZ) <= 80))
    {
      StructureComponent var8 = func_175892_a(p_175890_1_, p_175890_2_, p_175890_3_, p_175890_4_, p_175890_5_, p_175890_6_, p_175890_7_ + 1);
      
      if (var8 != null)
      {
        p_175890_1_.add(var8);
        var8.buildComponent(p_175890_0_, p_175890_1_, p_175890_2_);
      }
      
      return var8;
    }
    

    return null;
  }
  
  public static class Corridor
    extends StructureComponent
  {
    private boolean hasRails;
    private boolean hasSpiders;
    private boolean spawnerPlaced;
    private int sectionCount;
    private static final String __OBFID = "CL_00000445";
    
    public Corridor() {}
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      p_143012_1_.setBoolean("hr", hasRails);
      p_143012_1_.setBoolean("sc", hasSpiders);
      p_143012_1_.setBoolean("hps", spawnerPlaced);
      p_143012_1_.setInteger("Num", sectionCount);
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      hasRails = p_143011_1_.getBoolean("hr");
      hasSpiders = p_143011_1_.getBoolean("sc");
      spawnerPlaced = p_143011_1_.getBoolean("hps");
      sectionCount = p_143011_1_.getInteger("Num");
    }
    
    public Corridor(int p_i45625_1_, Random p_i45625_2_, StructureBoundingBox p_i45625_3_, EnumFacing p_i45625_4_)
    {
      super();
      coordBaseMode = p_i45625_4_;
      boundingBox = p_i45625_3_;
      hasRails = (p_i45625_2_.nextInt(3) == 0);
      hasSpiders = ((!hasRails) && (p_i45625_2_.nextInt(23) == 0));
      
      if ((coordBaseMode != EnumFacing.NORTH) && (coordBaseMode != EnumFacing.SOUTH))
      {
        sectionCount = (p_i45625_3_.getXSize() / 5);
      }
      else
      {
        sectionCount = (p_i45625_3_.getZSize() / 5);
      }
    }
    
    public static StructureBoundingBox func_175814_a(List p_175814_0_, Random p_175814_1_, int p_175814_2_, int p_175814_3_, int p_175814_4_, EnumFacing p_175814_5_)
    {
      StructureBoundingBox var6 = new StructureBoundingBox(p_175814_2_, p_175814_3_, p_175814_4_, p_175814_2_, p_175814_3_ + 2, p_175814_4_);
      

      for (int var7 = p_175814_1_.nextInt(3) + 2; var7 > 0; var7--)
      {
        int var8 = var7 * 5;
        
        switch (StructureMineshaftPieces.SwitchEnumFacing.field_175894_a[p_175814_5_.ordinal()])
        {
        case 1: 
          maxX = (p_175814_2_ + 2);
          minZ = (p_175814_4_ - (var8 - 1));
          break;
        
        case 2: 
          maxX = (p_175814_2_ + 2);
          maxZ = (p_175814_4_ + (var8 - 1));
          break;
        
        case 3: 
          minX = (p_175814_2_ - (var8 - 1));
          maxZ = (p_175814_4_ + 2);
          break;
        
        case 4: 
          maxX = (p_175814_2_ + (var8 - 1));
          maxZ = (p_175814_4_ + 2);
        }
        
        if (StructureComponent.findIntersecting(p_175814_0_, var6) == null) {
          break;
        }
      }
      

      return var7 > 0 ? var6 : null;
    }
    
    public void buildComponent(StructureComponent p_74861_1_, List p_74861_2_, Random p_74861_3_)
    {
      int var4 = getComponentType();
      int var5 = p_74861_3_.nextInt(4);
      
      if (coordBaseMode != null)
      {
        switch (StructureMineshaftPieces.SwitchEnumFacing.field_175894_a[coordBaseMode.ordinal()])
        {
        case 1: 
          if (var5 <= 1)
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.minZ - 1, coordBaseMode, var4);
          }
          else if (var5 == 2)
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.minZ, EnumFacing.WEST, var4);
          }
          else
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.minZ, EnumFacing.EAST, var4);
          }
          
          break;
        
        case 2: 
          if (var5 <= 1)
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.maxZ + 1, coordBaseMode, var4);
          }
          else if (var5 == 2)
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.maxZ - 3, EnumFacing.WEST, var4);
          }
          else
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.maxZ - 3, EnumFacing.EAST, var4);
          }
          
          break;
        
        case 3: 
          if (var5 <= 1)
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.minZ, coordBaseMode, var4);
          }
          else if (var5 == 2)
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.minZ - 1, EnumFacing.NORTH, var4);
          }
          else
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
          }
          
          break;
        
        case 4: 
          if (var5 <= 1)
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.minZ, coordBaseMode, var4);
          }
          else if (var5 == 2)
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX - 3, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.minZ - 1, EnumFacing.NORTH, var4);
          }
          else
          {
            StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX - 3, boundingBox.minY - 1 + p_74861_3_.nextInt(3), boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
          }
          break;
        }
      }
      if (var4 < 8)
      {



        if ((coordBaseMode != EnumFacing.NORTH) && (coordBaseMode != EnumFacing.SOUTH))
        {
          for (int var6 = boundingBox.minX + 3; var6 + 3 <= boundingBox.maxX; var6 += 5)
          {
            int var7 = p_74861_3_.nextInt(5);
            
            if (var7 == 0)
            {
              StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, var6, boundingBox.minY, boundingBox.minZ - 1, EnumFacing.NORTH, var4 + 1);
            }
            else if (var7 == 1)
            {
              StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, var6, boundingBox.minY, boundingBox.maxZ + 1, EnumFacing.SOUTH, var4 + 1);
            }
            
          }
          
        } else {
          for (int var6 = boundingBox.minZ + 3; var6 + 3 <= boundingBox.maxZ; var6 += 5)
          {
            int var7 = p_74861_3_.nextInt(5);
            
            if (var7 == 0)
            {
              StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY, var6, EnumFacing.WEST, var4 + 1);
            }
            else if (var7 == 1)
            {
              StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY, var6, EnumFacing.EAST, var4 + 1);
            }
          }
        }
      }
    }
    
    protected boolean func_180778_a(World worldIn, StructureBoundingBox p_180778_2_, Random p_180778_3_, int p_180778_4_, int p_180778_5_, int p_180778_6_, List p_180778_7_, int p_180778_8_)
    {
      BlockPos var9 = new BlockPos(getXWithOffset(p_180778_4_, p_180778_6_), getYWithOffset(p_180778_5_), getZWithOffset(p_180778_4_, p_180778_6_));
      
      if ((p_180778_2_.func_175898_b(var9)) && (worldIn.getBlockState(var9).getBlock().getMaterial() == Material.air))
      {
        int var10 = p_180778_3_.nextBoolean() ? 1 : 0;
        worldIn.setBlockState(var9, Blocks.rail.getStateFromMeta(getMetadataWithOffset(Blocks.rail, var10)), 2);
        EntityMinecartChest var11 = new EntityMinecartChest(worldIn, var9.getX() + 0.5F, var9.getY() + 0.5F, var9.getZ() + 0.5F);
        WeightedRandomChestContent.generateChestContents(p_180778_3_, p_180778_7_, var11, p_180778_8_);
        worldIn.spawnEntityInWorld(var11);
        return true;
      }
      

      return false;
    }
    

    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (isLiquidInStructureBoundingBox(worldIn, p_74875_3_))
      {
        return false;
      }
      

      boolean var4 = false;
      boolean var5 = true;
      boolean var6 = false;
      boolean var7 = true;
      int var8 = sectionCount * 5 - 1;
      func_175804_a(worldIn, p_74875_3_, 0, 0, 0, 2, 1, var8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175805_a(worldIn, p_74875_3_, p_74875_2_, 0.8F, 0, 2, 0, 2, 2, var8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      
      if (hasSpiders)
      {
        func_175805_a(worldIn, p_74875_3_, p_74875_2_, 0.6F, 0, 0, 0, 2, 1, var8, Blocks.web.getDefaultState(), Blocks.air.getDefaultState(), false);
      }
      



      for (int var9 = 0; var9 < sectionCount; var9++)
      {
        int var10 = 2 + var9 * 5;
        func_175804_a(worldIn, p_74875_3_, 0, 0, var10, 0, 1, var10, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);
        func_175804_a(worldIn, p_74875_3_, 2, 0, var10, 2, 1, var10, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);
        
        if (p_74875_2_.nextInt(4) == 0)
        {
          func_175804_a(worldIn, p_74875_3_, 0, 2, var10, 0, 2, var10, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
          func_175804_a(worldIn, p_74875_3_, 2, 2, var10, 2, 2, var10, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
        }
        else
        {
          func_175804_a(worldIn, p_74875_3_, 0, 2, var10, 2, 2, var10, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
        }
        
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.1F, 0, 2, var10 - 1, Blocks.web.getDefaultState());
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.1F, 2, 2, var10 - 1, Blocks.web.getDefaultState());
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.1F, 0, 2, var10 + 1, Blocks.web.getDefaultState());
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.1F, 2, 2, var10 + 1, Blocks.web.getDefaultState());
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.05F, 0, 2, var10 - 2, Blocks.web.getDefaultState());
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.05F, 2, 2, var10 - 2, Blocks.web.getDefaultState());
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.05F, 0, 2, var10 + 2, Blocks.web.getDefaultState());
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.05F, 2, 2, var10 + 2, Blocks.web.getDefaultState());
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.05F, 1, 2, var10 - 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
        func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.05F, 1, 2, var10 + 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
        
        if (p_74875_2_.nextInt(100) == 0)
        {
          func_180778_a(worldIn, p_74875_3_, p_74875_2_, 2, 0, var10 - 1, WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.field_175893_a, new WeightedRandomChestContent[] { Items.enchanted_book.getRandomEnchantedBook(p_74875_2_) }), 3 + p_74875_2_.nextInt(4));
        }
        
        if (p_74875_2_.nextInt(100) == 0)
        {
          func_180778_a(worldIn, p_74875_3_, p_74875_2_, 0, 0, var10 + 1, WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.field_175893_a, new WeightedRandomChestContent[] { Items.enchanted_book.getRandomEnchantedBook(p_74875_2_) }), 3 + p_74875_2_.nextInt(4));
        }
        
        if ((hasSpiders) && (!spawnerPlaced))
        {
          int var11 = getYWithOffset(0);
          int var12 = var10 - 1 + p_74875_2_.nextInt(3);
          int var13 = getXWithOffset(1, var12);
          var12 = getZWithOffset(1, var12);
          BlockPos var14 = new BlockPos(var13, var11, var12);
          
          if (p_74875_3_.func_175898_b(var14))
          {
            spawnerPlaced = true;
            worldIn.setBlockState(var14, Blocks.mob_spawner.getDefaultState(), 2);
            net.minecraft.tileentity.TileEntity var15 = worldIn.getTileEntity(var14);
            
            if ((var15 instanceof TileEntityMobSpawner))
            {
              ((TileEntityMobSpawner)var15).getSpawnerBaseLogic().setEntityName("CaveSpider");
            }
          }
        }
      }
      
      for (var9 = 0; var9 <= 2; var9++)
      {
        for (int var10 = 0; var10 <= var8; var10++)
        {
          byte var17 = -1;
          IBlockState var18 = func_175807_a(worldIn, var9, var17, var10, p_74875_3_);
          
          if (var18.getBlock().getMaterial() == Material.air)
          {
            byte var19 = -1;
            func_175811_a(worldIn, Blocks.planks.getDefaultState(), var9, var19, var10, p_74875_3_);
          }
        }
      }
      
      if (hasRails)
      {
        for (var9 = 0; var9 <= var8; var9++)
        {
          IBlockState var16 = func_175807_a(worldIn, 1, -1, var9, p_74875_3_);
          
          if ((var16.getBlock().getMaterial() != Material.air) && (var16.getBlock().isFullBlock()))
          {
            func_175809_a(worldIn, p_74875_3_, p_74875_2_, 0.7F, 1, 0, var9, Blocks.rail.getStateFromMeta(getMetadataWithOffset(Blocks.rail, 0)));
          }
        }
      }
      
      return true;
    }
  }
  
  public static class Cross
    extends StructureComponent
  {
    private EnumFacing corridorDirection;
    private boolean isMultipleFloors;
    private static final String __OBFID = "CL_00000446";
    
    public Cross() {}
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      p_143012_1_.setBoolean("tf", isMultipleFloors);
      p_143012_1_.setInteger("D", corridorDirection.getHorizontalIndex());
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      isMultipleFloors = p_143011_1_.getBoolean("tf");
      corridorDirection = EnumFacing.getHorizontal(p_143011_1_.getInteger("D"));
    }
    
    public Cross(int p_i45624_1_, Random p_i45624_2_, StructureBoundingBox p_i45624_3_, EnumFacing p_i45624_4_)
    {
      super();
      corridorDirection = p_i45624_4_;
      boundingBox = p_i45624_3_;
      isMultipleFloors = (p_i45624_3_.getYSize() > 3);
    }
    
    public static StructureBoundingBox func_175813_a(List p_175813_0_, Random p_175813_1_, int p_175813_2_, int p_175813_3_, int p_175813_4_, EnumFacing p_175813_5_)
    {
      StructureBoundingBox var6 = new StructureBoundingBox(p_175813_2_, p_175813_3_, p_175813_4_, p_175813_2_, p_175813_3_ + 2, p_175813_4_);
      
      if (p_175813_1_.nextInt(4) == 0)
      {
        maxY += 4;
      }
      
      switch (StructureMineshaftPieces.SwitchEnumFacing.field_175894_a[p_175813_5_.ordinal()])
      {
      case 1: 
        minX = (p_175813_2_ - 1);
        maxX = (p_175813_2_ + 3);
        minZ = (p_175813_4_ - 4);
        break;
      
      case 2: 
        minX = (p_175813_2_ - 1);
        maxX = (p_175813_2_ + 3);
        maxZ = (p_175813_4_ + 4);
        break;
      
      case 3: 
        minX = (p_175813_2_ - 4);
        minZ = (p_175813_4_ - 1);
        maxZ = (p_175813_4_ + 3);
        break;
      
      case 4: 
        maxX = (p_175813_2_ + 4);
        minZ = (p_175813_4_ - 1);
        maxZ = (p_175813_4_ + 3);
      }
      
      return StructureComponent.findIntersecting(p_175813_0_, var6) != null ? null : var6;
    }
    
    public void buildComponent(StructureComponent p_74861_1_, List p_74861_2_, Random p_74861_3_)
    {
      int var4 = getComponentType();
      
      switch (StructureMineshaftPieces.SwitchEnumFacing.field_175894_a[corridorDirection.ordinal()])
      {
      case 1: 
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.minZ - 1, EnumFacing.NORTH, var4);
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY, boundingBox.minZ + 1, EnumFacing.WEST, var4);
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY, boundingBox.minZ + 1, EnumFacing.EAST, var4);
        break;
      
      case 2: 
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY, boundingBox.minZ + 1, EnumFacing.WEST, var4);
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY, boundingBox.minZ + 1, EnumFacing.EAST, var4);
        break;
      
      case 3: 
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.minZ - 1, EnumFacing.NORTH, var4);
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY, boundingBox.minZ + 1, EnumFacing.WEST, var4);
        break;
      
      case 4: 
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.minZ - 1, EnumFacing.NORTH, var4);
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
        StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY, boundingBox.minZ + 1, EnumFacing.EAST, var4);
      }
      
      if (isMultipleFloors)
      {
        if (p_74861_3_.nextBoolean())
        {
          StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.minY + 3 + 1, boundingBox.minZ - 1, EnumFacing.NORTH, var4);
        }
        
        if (p_74861_3_.nextBoolean())
        {
          StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY + 3 + 1, boundingBox.minZ + 1, EnumFacing.WEST, var4);
        }
        
        if (p_74861_3_.nextBoolean())
        {
          StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY + 3 + 1, boundingBox.minZ + 1, EnumFacing.EAST, var4);
        }
        
        if (p_74861_3_.nextBoolean())
        {
          StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + 1, boundingBox.minY + 3 + 1, boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
        }
      }
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (isLiquidInStructureBoundingBox(worldIn, p_74875_3_))
      {
        return false;
      }
      

      if (isMultipleFloors)
      {
        func_175804_a(worldIn, p_74875_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.minZ, boundingBox.maxX - 1, boundingBox.minY + 3 - 1, boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
        func_175804_a(worldIn, p_74875_3_, boundingBox.minX, boundingBox.minY, boundingBox.minZ + 1, boundingBox.maxX, boundingBox.minY + 3 - 1, boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
        func_175804_a(worldIn, p_74875_3_, boundingBox.minX + 1, boundingBox.maxY - 2, boundingBox.minZ, boundingBox.maxX - 1, boundingBox.maxY, boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
        func_175804_a(worldIn, p_74875_3_, boundingBox.minX, boundingBox.maxY - 2, boundingBox.minZ + 1, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
        func_175804_a(worldIn, p_74875_3_, boundingBox.minX + 1, boundingBox.minY + 3, boundingBox.minZ + 1, boundingBox.maxX - 1, boundingBox.minY + 3, boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      }
      else
      {
        func_175804_a(worldIn, p_74875_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.minZ, boundingBox.maxX - 1, boundingBox.maxY, boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
        func_175804_a(worldIn, p_74875_3_, boundingBox.minX, boundingBox.minY, boundingBox.minZ + 1, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      }
      
      func_175804_a(worldIn, p_74875_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.minZ + 1, boundingBox.minX + 1, boundingBox.maxY, boundingBox.minZ + 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, boundingBox.minX + 1, boundingBox.minY, boundingBox.maxZ - 1, boundingBox.minX + 1, boundingBox.maxY, boundingBox.maxZ - 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, boundingBox.maxX - 1, boundingBox.minY, boundingBox.minZ + 1, boundingBox.maxX - 1, boundingBox.maxY, boundingBox.minZ + 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, boundingBox.maxX - 1, boundingBox.minY, boundingBox.maxZ - 1, boundingBox.maxX - 1, boundingBox.maxY, boundingBox.maxZ - 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
      
      for (int var4 = boundingBox.minX; var4 <= boundingBox.maxX; var4++)
      {
        for (int var5 = boundingBox.minZ; var5 <= boundingBox.maxZ; var5++)
        {
          if (func_175807_a(worldIn, var4, boundingBox.minY - 1, var5, p_74875_3_).getBlock().getMaterial() == Material.air)
          {
            func_175811_a(worldIn, Blocks.planks.getDefaultState(), var4, boundingBox.minY - 1, var5, p_74875_3_);
          }
        }
      }
      
      return true;
    }
  }
  
  public static class Room
    extends StructureComponent
  {
    private List roomsLinkedToTheRoom = Lists.newLinkedList();
    private static final String __OBFID = "CL_00000447";
    
    public Room() {}
    
    public Room(int p_i2037_1_, Random p_i2037_2_, int p_i2037_3_, int p_i2037_4_)
    {
      super();
      boundingBox = new StructureBoundingBox(p_i2037_3_, 50, p_i2037_4_, p_i2037_3_ + 7 + p_i2037_2_.nextInt(6), 54 + p_i2037_2_.nextInt(6), p_i2037_4_ + 7 + p_i2037_2_.nextInt(6));
    }
    
    public void buildComponent(StructureComponent p_74861_1_, List p_74861_2_, Random p_74861_3_)
    {
      int var4 = getComponentType();
      int var6 = boundingBox.getYSize() - 3 - 1;
      
      if (var6 <= 0)
      {
        var6 = 1;
      }
      




      for (int var5 = 0; var5 < boundingBox.getXSize(); var5 += 4)
      {
        var5 += p_74861_3_.nextInt(boundingBox.getXSize());
        
        if (var5 + 3 > boundingBox.getXSize()) {
          break;
        }
        

        StructureComponent var7 = StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + var5, boundingBox.minY + p_74861_3_.nextInt(var6) + 1, boundingBox.minZ - 1, EnumFacing.NORTH, var4);
        
        if (var7 != null)
        {
          StructureBoundingBox var8 = var7.getBoundingBox();
          roomsLinkedToTheRoom.add(new StructureBoundingBox(minX, minY, boundingBox.minZ, maxX, maxY, boundingBox.minZ + 1));
        }
      }
      
      for (var5 = 0; var5 < boundingBox.getXSize(); var5 += 4)
      {
        var5 += p_74861_3_.nextInt(boundingBox.getXSize());
        
        if (var5 + 3 > boundingBox.getXSize()) {
          break;
        }
        

        StructureComponent var7 = StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX + var5, boundingBox.minY + p_74861_3_.nextInt(var6) + 1, boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
        
        if (var7 != null)
        {
          StructureBoundingBox var8 = var7.getBoundingBox();
          roomsLinkedToTheRoom.add(new StructureBoundingBox(minX, minY, boundingBox.maxZ - 1, maxX, maxY, boundingBox.maxZ));
        }
      }
      
      for (var5 = 0; var5 < boundingBox.getZSize(); var5 += 4)
      {
        var5 += p_74861_3_.nextInt(boundingBox.getZSize());
        
        if (var5 + 3 > boundingBox.getZSize()) {
          break;
        }
        

        StructureComponent var7 = StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY + p_74861_3_.nextInt(var6) + 1, boundingBox.minZ + var5, EnumFacing.WEST, var4);
        
        if (var7 != null)
        {
          StructureBoundingBox var8 = var7.getBoundingBox();
          roomsLinkedToTheRoom.add(new StructureBoundingBox(boundingBox.minX, minY, minZ, boundingBox.minX + 1, maxY, maxZ));
        }
      }
      
      for (var5 = 0; var5 < boundingBox.getZSize(); var5 += 4)
      {
        var5 += p_74861_3_.nextInt(boundingBox.getZSize());
        
        if (var5 + 3 > boundingBox.getZSize()) {
          break;
        }
        

        StructureComponent var7 = StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY + p_74861_3_.nextInt(var6) + 1, boundingBox.minZ + var5, EnumFacing.EAST, var4);
        
        if (var7 != null)
        {
          StructureBoundingBox var8 = var7.getBoundingBox();
          roomsLinkedToTheRoom.add(new StructureBoundingBox(boundingBox.maxX - 1, minY, minZ, boundingBox.maxX, maxY, maxZ));
        }
      }
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (isLiquidInStructureBoundingBox(worldIn, p_74875_3_))
      {
        return false;
      }
      

      func_175804_a(worldIn, p_74875_3_, boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.minY, boundingBox.maxZ, Blocks.dirt.getDefaultState(), Blocks.air.getDefaultState(), true);
      func_175804_a(worldIn, p_74875_3_, boundingBox.minX, boundingBox.minY + 1, boundingBox.minZ, boundingBox.maxX, Math.min(boundingBox.minY + 3, boundingBox.maxY), boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      Iterator var4 = roomsLinkedToTheRoom.iterator();
      
      while (var4.hasNext())
      {
        StructureBoundingBox var5 = (StructureBoundingBox)var4.next();
        func_175804_a(worldIn, p_74875_3_, minX, maxY - 2, minZ, maxX, maxY, maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      }
      
      func_180777_a(worldIn, p_74875_3_, boundingBox.minX, boundingBox.minY + 4, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ, Blocks.air.getDefaultState(), false);
      return true;
    }
    

    protected void writeStructureToNBT(NBTTagCompound p_143012_1_)
    {
      NBTTagList var2 = new NBTTagList();
      Iterator var3 = roomsLinkedToTheRoom.iterator();
      
      while (var3.hasNext())
      {
        StructureBoundingBox var4 = (StructureBoundingBox)var3.next();
        var2.appendTag(var4.func_151535_h());
      }
      
      p_143012_1_.setTag("Entrances", var2);
    }
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_)
    {
      NBTTagList var2 = p_143011_1_.getTagList("Entrances", 11);
      
      for (int var3 = 0; var3 < var2.tagCount(); var3++)
      {
        roomsLinkedToTheRoom.add(new StructureBoundingBox(var2.getIntArray(var3)));
      }
    }
  }
  
  public static class Stairs extends StructureComponent
  {
    private static final String __OBFID = "CL_00000449";
    
    public Stairs() {}
    
    public Stairs(int p_i45623_1_, Random p_i45623_2_, StructureBoundingBox p_i45623_3_, EnumFacing p_i45623_4_)
    {
      super();
      coordBaseMode = p_i45623_4_;
      boundingBox = p_i45623_3_;
    }
    
    protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {}
    
    protected void readStructureFromNBT(NBTTagCompound p_143011_1_) {}
    
    public static StructureBoundingBox func_175812_a(List p_175812_0_, Random p_175812_1_, int p_175812_2_, int p_175812_3_, int p_175812_4_, EnumFacing p_175812_5_)
    {
      StructureBoundingBox var6 = new StructureBoundingBox(p_175812_2_, p_175812_3_ - 5, p_175812_4_, p_175812_2_, p_175812_3_ + 2, p_175812_4_);
      
      switch (StructureMineshaftPieces.SwitchEnumFacing.field_175894_a[p_175812_5_.ordinal()])
      {
      case 1: 
        maxX = (p_175812_2_ + 2);
        minZ = (p_175812_4_ - 8);
        break;
      
      case 2: 
        maxX = (p_175812_2_ + 2);
        maxZ = (p_175812_4_ + 8);
        break;
      
      case 3: 
        minX = (p_175812_2_ - 8);
        maxZ = (p_175812_4_ + 2);
        break;
      
      case 4: 
        maxX = (p_175812_2_ + 8);
        maxZ = (p_175812_4_ + 2);
      }
      
      return StructureComponent.findIntersecting(p_175812_0_, var6) != null ? null : var6;
    }
    
    public void buildComponent(StructureComponent p_74861_1_, List p_74861_2_, Random p_74861_3_)
    {
      int var4 = getComponentType();
      
      if (coordBaseMode != null)
      {
        switch (StructureMineshaftPieces.SwitchEnumFacing.field_175894_a[coordBaseMode.ordinal()])
        {
        case 1: 
          StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX, boundingBox.minY, boundingBox.minZ - 1, EnumFacing.NORTH, var4);
          break;
        
        case 2: 
          StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX, boundingBox.minY, boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
          break;
        
        case 3: 
          StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.minX - 1, boundingBox.minY, boundingBox.minZ, EnumFacing.WEST, var4);
          break;
        
        case 4: 
          StructureMineshaftPieces.func_175890_b(p_74861_1_, p_74861_2_, p_74861_3_, boundingBox.maxX + 1, boundingBox.minY, boundingBox.minZ, EnumFacing.EAST, var4);
        }
      }
    }
    
    public boolean addComponentParts(World worldIn, Random p_74875_2_, StructureBoundingBox p_74875_3_)
    {
      if (isLiquidInStructureBoundingBox(worldIn, p_74875_3_))
      {
        return false;
      }
      

      func_175804_a(worldIn, p_74875_3_, 0, 5, 0, 2, 7, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      func_175804_a(worldIn, p_74875_3_, 0, 0, 7, 2, 2, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      
      for (int var4 = 0; var4 < 5; var4++)
      {
        func_175804_a(worldIn, p_74875_3_, 0, 5 - var4 - (var4 < 4 ? 1 : 0), 2 + var4, 2, 7 - var4, 2 + var4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
      }
      
      return true;
    }
  }
  

  static final class SwitchEnumFacing
  {
    static final int[] field_175894_a = new int[EnumFacing.values().length];
    private static final String __OBFID = "CL_00001998";
    
    static
    {
      try
      {
        field_175894_a[EnumFacing.NORTH.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError1) {}
      



      try
      {
        field_175894_a[EnumFacing.SOUTH.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {}
      



      try
      {
        field_175894_a[EnumFacing.WEST.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {}
      



      try
      {
        field_175894_a[EnumFacing.EAST.ordinal()] = 4;
      }
      catch (NoSuchFieldError localNoSuchFieldError4) {}
    }
    
    SwitchEnumFacing() {}
  }
}
