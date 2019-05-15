package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase
{
  public static final PropertyEnum field_176573_b = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, new Predicate()
  {
    private static final String __OBFID = "CL_00002126";
    
    public boolean func_180344_a(BlockRailBase.EnumRailDirection p_180344_1_) {
      return (p_180344_1_ != BlockRailBase.EnumRailDirection.NORTH_EAST) && (p_180344_1_ != BlockRailBase.EnumRailDirection.NORTH_WEST) && (p_180344_1_ != BlockRailBase.EnumRailDirection.SOUTH_EAST) && (p_180344_1_ != BlockRailBase.EnumRailDirection.SOUTH_WEST);
    }
    
    public boolean apply(Object p_apply_1_) {
      return func_180344_a((BlockRailBase.EnumRailDirection)p_apply_1_);
    }
  });
  










  public static final PropertyBool field_176574_M = PropertyBool.create("powered");
  private static final String __OBFID = "CL_00000225";
  
  public BlockRailDetector()
  {
    super(true);
    setDefaultState(blockState.getBaseState().withProperty(field_176574_M, Boolean.valueOf(false)).withProperty(field_176573_b, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
    setTickRandomly(true);
  }
  



  public int tickRate(World worldIn)
  {
    return 20;
  }
  



  public boolean canProvidePower()
  {
    return true;
  }
  



  public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
  {
    if (!isRemote)
    {
      if (!((Boolean)state.getValue(field_176574_M)).booleanValue())
      {
        func_176570_e(worldIn, pos, state);
      }
    }
  }
  


  public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {}
  

  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
  {
    if ((!isRemote) && (((Boolean)state.getValue(field_176574_M)).booleanValue()))
    {
      func_176570_e(worldIn, pos, state);
    }
  }
  
  public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
  {
    return ((Boolean)state.getValue(field_176574_M)).booleanValue() ? 15 : 0;
  }
  
  public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
  {
    return side == EnumFacing.UP ? 15 : !((Boolean)state.getValue(field_176574_M)).booleanValue() ? 0 : 0;
  }
  
  private void func_176570_e(World worldIn, BlockPos p_176570_2_, IBlockState p_176570_3_)
  {
    boolean var4 = ((Boolean)p_176570_3_.getValue(field_176574_M)).booleanValue();
    boolean var5 = false;
    List var6 = func_176571_a(worldIn, p_176570_2_, EntityMinecart.class, new Predicate[0]);
    
    if (!var6.isEmpty())
    {
      var5 = true;
    }
    
    if ((var5) && (!var4))
    {
      worldIn.setBlockState(p_176570_2_, p_176570_3_.withProperty(field_176574_M, Boolean.valueOf(true)), 3);
      worldIn.notifyNeighborsOfStateChange(p_176570_2_, this);
      worldIn.notifyNeighborsOfStateChange(p_176570_2_.offsetDown(), this);
      worldIn.markBlockRangeForRenderUpdate(p_176570_2_, p_176570_2_);
    }
    
    if ((!var5) && (var4))
    {
      worldIn.setBlockState(p_176570_2_, p_176570_3_.withProperty(field_176574_M, Boolean.valueOf(false)), 3);
      worldIn.notifyNeighborsOfStateChange(p_176570_2_, this);
      worldIn.notifyNeighborsOfStateChange(p_176570_2_.offsetDown(), this);
      worldIn.markBlockRangeForRenderUpdate(p_176570_2_, p_176570_2_);
    }
    
    if (var5)
    {
      worldIn.scheduleUpdate(p_176570_2_, this, tickRate(worldIn));
    }
    
    worldIn.updateComparatorOutputLevel(p_176570_2_, this);
  }
  
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
  {
    super.onBlockAdded(worldIn, pos, state);
    func_176570_e(worldIn, pos, state);
  }
  
  public IProperty func_176560_l()
  {
    return field_176573_b;
  }
  
  public boolean hasComparatorInputOverride()
  {
    return true;
  }
  
  public int getComparatorInputOverride(World worldIn, BlockPos pos)
  {
    if (((Boolean)worldIn.getBlockState(pos).getValue(field_176574_M)).booleanValue())
    {
      List var3 = func_176571_a(worldIn, pos, EntityMinecartCommandBlock.class, new Predicate[0]);
      
      if (!var3.isEmpty())
      {
        return ((EntityMinecartCommandBlock)var3.get(0)).func_145822_e().getSuccessCount();
      }
      
      List var4 = func_176571_a(worldIn, pos, EntityMinecart.class, new Predicate[] { net.minecraft.command.IEntitySelector.selectInventories });
      
      if (!var4.isEmpty())
      {
        return Container.calcRedstoneFromInventory((IInventory)var4.get(0));
      }
    }
    
    return 0;
  }
  
  protected List func_176571_a(World worldIn, BlockPos p_176571_2_, Class p_176571_3_, Predicate... p_176571_4_)
  {
    AxisAlignedBB var5 = func_176572_a(p_176571_2_);
    return p_176571_4_.length != 1 ? worldIn.getEntitiesWithinAABB(p_176571_3_, var5) : worldIn.func_175647_a(p_176571_3_, var5, p_176571_4_[0]);
  }
  
  private AxisAlignedBB func_176572_a(BlockPos p_176572_1_)
  {
    float var2 = 0.2F;
    return new AxisAlignedBB(p_176572_1_.getX() + 0.2F, p_176572_1_.getY(), p_176572_1_.getZ() + 0.2F, p_176572_1_.getX() + 1 - 0.2F, p_176572_1_.getY() + 1 - 0.2F, p_176572_1_.getZ() + 1 - 0.2F);
  }
  



  public IBlockState getStateFromMeta(int meta)
  {
    return getDefaultState().withProperty(field_176573_b, BlockRailBase.EnumRailDirection.func_177016_a(meta & 0x7)).withProperty(field_176574_M, Boolean.valueOf((meta & 0x8) > 0));
  }
  



  public int getMetaFromState(IBlockState state)
  {
    byte var2 = 0;
    int var3 = var2 | ((BlockRailBase.EnumRailDirection)state.getValue(field_176573_b)).func_177015_a();
    
    if (((Boolean)state.getValue(field_176574_M)).booleanValue())
    {
      var3 |= 0x8;
    }
    
    return var3;
  }
  
  protected BlockState createBlockState()
  {
    return new BlockState(this, new IProperty[] { field_176573_b, field_176574_M });
  }
}
