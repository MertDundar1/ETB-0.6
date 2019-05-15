package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable
{
  public static final PropertyInteger AGE_PROP = PropertyInteger.create("age", 0, 7);
  public static final PropertyDirection FACING_PROP = PropertyDirection.create("facing", new Predicate()
  {
    private static final String __OBFID = "CL_00002059";
    
    public boolean apply(EnumFacing p_177218_1_) {
      return p_177218_1_ != EnumFacing.DOWN;
    }
    
    public boolean apply(Object p_apply_1_) {
      return apply((EnumFacing)p_apply_1_);
    }
  });
  



  private final Block cropBlock;
  



  private static final String __OBFID = "CL_00000316";
  



  protected BlockStem(Block p_i45430_1_)
  {
    setDefaultState(blockState.getBaseState().withProperty(AGE_PROP, Integer.valueOf(0)).withProperty(FACING_PROP, EnumFacing.UP));
    cropBlock = p_i45430_1_;
    setTickRandomly(true);
    float var2 = 0.125F;
    setBlockBounds(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.25F, 0.5F + var2);
    setCreativeTab(null);
  }
  




  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
  {
    state = state.withProperty(FACING_PROP, EnumFacing.UP);
    Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();
    
    while (var4.hasNext())
    {
      EnumFacing var5 = (EnumFacing)var4.next();
      
      if (worldIn.getBlockState(pos.offset(var5)).getBlock() == cropBlock)
      {
        state = state.withProperty(FACING_PROP, var5);
        break;
      }
    }
    
    return state;
  }
  



  protected boolean canPlaceBlockOn(Block ground)
  {
    return ground == Blocks.farmland;
  }
  
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
  {
    super.updateTick(worldIn, pos, state, rand);
    
    if (worldIn.getLightFromNeighbors(pos.offsetUp()) >= 9)
    {
      float var5 = BlockCrops.getGrowthChance(this, worldIn, pos);
      
      if (rand.nextInt((int)(25.0F / var5) + 1) == 0)
      {
        int var6 = ((Integer)state.getValue(AGE_PROP)).intValue();
        
        if (var6 < 7)
        {
          state = state.withProperty(AGE_PROP, Integer.valueOf(var6 + 1));
          worldIn.setBlockState(pos, state, 2);
        }
        else
        {
          Iterator var7 = EnumFacing.Plane.HORIZONTAL.iterator();
          
          while (var7.hasNext())
          {
            EnumFacing var8 = (EnumFacing)var7.next();
            
            if (worldIn.getBlockState(pos.offset(var8)).getBlock() == cropBlock)
            {
              return;
            }
          }
          
          pos = pos.offset(EnumFacing.Plane.HORIZONTAL.random(rand));
          Block var9 = worldIn.getBlockState(pos.offsetDown()).getBlock();
          
          if ((getBlockStategetBlockblockMaterial == Material.air) && ((var9 == Blocks.farmland) || (var9 == Blocks.dirt) || (var9 == Blocks.grass)))
          {
            worldIn.setBlockState(pos, cropBlock.getDefaultState());
          }
        }
      }
    }
  }
  
  public void growStem(World worldIn, BlockPos p_176482_2_, IBlockState p_176482_3_)
  {
    int var4 = ((Integer)p_176482_3_.getValue(AGE_PROP)).intValue() + MathHelper.getRandomIntegerInRange(rand, 2, 5);
    worldIn.setBlockState(p_176482_2_, p_176482_3_.withProperty(AGE_PROP, Integer.valueOf(Math.min(7, var4))), 2);
  }
  
  public int getRenderColor(IBlockState state)
  {
    if (state.getBlock() != this)
    {
      return super.getRenderColor(state);
    }
    

    int var2 = ((Integer)state.getValue(AGE_PROP)).intValue();
    int var3 = var2 * 32;
    int var4 = 255 - var2 * 8;
    int var5 = var2 * 4;
    return var3 << 16 | var4 << 8 | var5;
  }
  

  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
  {
    return getRenderColor(worldIn.getBlockState(pos));
  }
  



  public void setBlockBoundsForItemRender()
  {
    float var1 = 0.125F;
    setBlockBounds(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
  }
  
  public void setBlockBoundsBasedOnState(IBlockAccess access, BlockPos pos)
  {
    maxY = ((((Integer)access.getBlockState(pos).getValue(AGE_PROP)).intValue() * 2 + 2) / 16.0F);
    float var3 = 0.125F;
    setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, (float)maxY, 0.5F + var3);
  }
  






  public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
  {
    super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    
    if (!isRemote)
    {
      Item var6 = getSeedItem();
      
      if (var6 != null)
      {
        int var7 = ((Integer)state.getValue(AGE_PROP)).intValue();
        
        for (int var8 = 0; var8 < 3; var8++)
        {
          if (rand.nextInt(15) <= var7)
          {
            spawnAsEntity(worldIn, pos, new ItemStack(var6));
          }
        }
      }
    }
  }
  
  protected Item getSeedItem()
  {
    return cropBlock == Blocks.melon_block ? Items.melon_seeds : cropBlock == Blocks.pumpkin ? Items.pumpkin_seeds : null;
  }
  





  public Item getItemDropped(IBlockState state, Random rand, int fortune)
  {
    return null;
  }
  
  public Item getItem(World worldIn, BlockPos pos)
  {
    Item var3 = getSeedItem();
    return var3 != null ? var3 : null;
  }
  
  public boolean isStillGrowing(World worldIn, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_)
  {
    return ((Integer)p_176473_3_.getValue(AGE_PROP)).intValue() != 7;
  }
  
  public boolean canUseBonemeal(World worldIn, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_)
  {
    return true;
  }
  
  public void grow(World worldIn, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_)
  {
    growStem(worldIn, p_176474_3_, p_176474_4_);
  }
  



  public IBlockState getStateFromMeta(int meta)
  {
    return getDefaultState().withProperty(AGE_PROP, Integer.valueOf(meta));
  }
  



  public int getMetaFromState(IBlockState state)
  {
    return ((Integer)state.getValue(AGE_PROP)).intValue();
  }
  
  protected BlockState createBlockState()
  {
    return new BlockState(this, new IProperty[] { AGE_PROP, FACING_PROP });
  }
}
