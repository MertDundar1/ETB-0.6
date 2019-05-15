package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional
{
  public static final PropertyBool field_176466_a = PropertyBool.create("open");
  public static final PropertyBool field_176465_b = PropertyBool.create("powered");
  public static final PropertyBool field_176467_M = PropertyBool.create("in_wall");
  private static final String __OBFID = "CL_00000243";
  
  public BlockFenceGate()
  {
    super(Material.wood);
    setDefaultState(blockState.getBaseState().withProperty(field_176466_a, Boolean.valueOf(false)).withProperty(field_176465_b, Boolean.valueOf(false)).withProperty(field_176467_M, Boolean.valueOf(false)));
    setCreativeTab(CreativeTabs.tabRedstone);
  }
  




  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
  {
    EnumFacing.Axis var4 = ((EnumFacing)state.getValue(AGE)).getAxis();
    
    if (((var4 == EnumFacing.Axis.Z) && ((worldIn.getBlockState(pos.offsetWest()).getBlock() == Blocks.cobblestone_wall) || (worldIn.getBlockState(pos.offsetEast()).getBlock() == Blocks.cobblestone_wall))) || ((var4 == EnumFacing.Axis.X) && ((worldIn.getBlockState(pos.offsetNorth()).getBlock() == Blocks.cobblestone_wall) || (worldIn.getBlockState(pos.offsetSouth()).getBlock() == Blocks.cobblestone_wall))))
    {
      state = state.withProperty(field_176467_M, Boolean.valueOf(true));
    }
    
    return state;
  }
  
  public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
  {
    return worldIn.getBlockState(pos.offsetDown()).getBlock().getMaterial().isSolid() ? super.canPlaceBlockAt(worldIn, pos) : false;
  }
  
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
  {
    if (((Boolean)state.getValue(field_176466_a)).booleanValue())
    {
      return null;
    }
    

    EnumFacing.Axis var4 = ((EnumFacing)state.getValue(AGE)).getAxis();
    return var4 == EnumFacing.Axis.Z ? new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ() + 0.375F, pos.getX() + 1, pos.getY() + 1.5F, pos.getZ() + 0.625F) : new AxisAlignedBB(pos.getX() + 0.375F, pos.getY(), pos.getZ(), pos.getX() + 0.625F, pos.getY() + 1.5F, pos.getZ() + 1);
  }
  

  public void setBlockBoundsBasedOnState(IBlockAccess access, BlockPos pos)
  {
    EnumFacing.Axis var3 = ((EnumFacing)access.getBlockState(pos).getValue(AGE)).getAxis();
    
    if (var3 == EnumFacing.Axis.Z)
    {
      setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
    }
    else
    {
      setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
    }
  }
  
  public boolean isOpaqueCube()
  {
    return false;
  }
  
  public boolean isFullCube()
  {
    return false;
  }
  
  public boolean isPassable(IBlockAccess blockAccess, BlockPos pos)
  {
    return ((Boolean)blockAccess.getBlockState(pos).getValue(field_176466_a)).booleanValue();
  }
  
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
  {
    return getDefaultState().withProperty(AGE, placer.func_174811_aO()).withProperty(field_176466_a, Boolean.valueOf(false)).withProperty(field_176465_b, Boolean.valueOf(false)).withProperty(field_176467_M, Boolean.valueOf(false));
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (((Boolean)state.getValue(field_176466_a)).booleanValue())
    {
      state = state.withProperty(field_176466_a, Boolean.valueOf(false));
      worldIn.setBlockState(pos, state, 2);
    }
    else
    {
      EnumFacing var9 = EnumFacing.fromAngle(rotationYaw);
      
      if (state.getValue(AGE) == var9.getOpposite())
      {
        state = state.withProperty(AGE, var9);
      }
      
      state = state.withProperty(field_176466_a, Boolean.valueOf(true));
      worldIn.setBlockState(pos, state, 2);
    }
    
    worldIn.playAuxSFXAtEntity(playerIn, ((Boolean)state.getValue(field_176466_a)).booleanValue() ? 1003 : 1006, pos, 0);
    return true;
  }
  
  public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
  {
    if (!isRemote)
    {
      boolean var5 = worldIn.isBlockPowered(pos);
      
      if ((var5) || (neighborBlock.canProvidePower()))
      {
        if ((var5) && (!((Boolean)state.getValue(field_176466_a)).booleanValue()) && (!((Boolean)state.getValue(field_176465_b)).booleanValue()))
        {
          worldIn.setBlockState(pos, state.withProperty(field_176466_a, Boolean.valueOf(true)).withProperty(field_176465_b, Boolean.valueOf(true)), 2);
          worldIn.playAuxSFXAtEntity(null, 1003, pos, 0);
        }
        else if ((!var5) && (((Boolean)state.getValue(field_176466_a)).booleanValue()) && (((Boolean)state.getValue(field_176465_b)).booleanValue()))
        {
          worldIn.setBlockState(pos, state.withProperty(field_176466_a, Boolean.valueOf(false)).withProperty(field_176465_b, Boolean.valueOf(false)), 2);
          worldIn.playAuxSFXAtEntity(null, 1006, pos, 0);
        }
        else if (var5 != ((Boolean)state.getValue(field_176465_b)).booleanValue())
        {
          worldIn.setBlockState(pos, state.withProperty(field_176465_b, Boolean.valueOf(var5)), 2);
        }
      }
    }
  }
  
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
  {
    return true;
  }
  



  public IBlockState getStateFromMeta(int meta)
  {
    return getDefaultState().withProperty(AGE, EnumFacing.getHorizontal(meta)).withProperty(field_176466_a, Boolean.valueOf((meta & 0x4) != 0)).withProperty(field_176465_b, Boolean.valueOf((meta & 0x8) != 0));
  }
  



  public int getMetaFromState(IBlockState state)
  {
    byte var2 = 0;
    int var3 = var2 | ((EnumFacing)state.getValue(AGE)).getHorizontalIndex();
    
    if (((Boolean)state.getValue(field_176465_b)).booleanValue())
    {
      var3 |= 0x8;
    }
    
    if (((Boolean)state.getValue(field_176466_a)).booleanValue())
    {
      var3 |= 0x4;
    }
    
    return var3;
  }
  
  protected BlockState createBlockState()
  {
    return new BlockState(this, new net.minecraft.block.properties.IProperty[] { AGE, field_176466_a, field_176465_b, field_176467_M });
  }
}
