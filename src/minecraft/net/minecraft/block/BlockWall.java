package net.minecraft.block;

import java.util.List;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWall
  extends Block
{
  public static final PropertyBool field_176256_a = PropertyBool.create("up");
  public static final PropertyBool field_176254_b = PropertyBool.create("north");
  public static final PropertyBool field_176257_M = PropertyBool.create("east");
  public static final PropertyBool field_176258_N = PropertyBool.create("south");
  public static final PropertyBool field_176259_O = PropertyBool.create("west");
  public static final PropertyEnum field_176255_P = PropertyEnum.create("variant", EnumType.class);
  private static final String __OBFID = "CL_00000331";
  
  public BlockWall(Block p_i45435_1_)
  {
    super(blockMaterial);
    setDefaultState(blockState.getBaseState().withProperty(field_176256_a, Boolean.valueOf(false)).withProperty(field_176254_b, Boolean.valueOf(false)).withProperty(field_176257_M, Boolean.valueOf(false)).withProperty(field_176258_N, Boolean.valueOf(false)).withProperty(field_176259_O, Boolean.valueOf(false)).withProperty(field_176255_P, EnumType.NORMAL));
    setHardness(blockHardness);
    setResistance(blockResistance / 3.0F);
    setStepSound(stepSound);
    setCreativeTab(CreativeTabs.tabBlock);
  }
  
  public boolean isFullCube()
  {
    return false;
  }
  
  public boolean isPassable(IBlockAccess blockAccess, BlockPos pos)
  {
    return false;
  }
  
  public boolean isOpaqueCube()
  {
    return false;
  }
  
  public void setBlockBoundsBasedOnState(IBlockAccess access, BlockPos pos)
  {
    boolean var3 = func_176253_e(access, pos.offsetNorth());
    boolean var4 = func_176253_e(access, pos.offsetSouth());
    boolean var5 = func_176253_e(access, pos.offsetWest());
    boolean var6 = func_176253_e(access, pos.offsetEast());
    float var7 = 0.25F;
    float var8 = 0.75F;
    float var9 = 0.25F;
    float var10 = 0.75F;
    float var11 = 1.0F;
    
    if (var3)
    {
      var9 = 0.0F;
    }
    
    if (var4)
    {
      var10 = 1.0F;
    }
    
    if (var5)
    {
      var7 = 0.0F;
    }
    
    if (var6)
    {
      var8 = 1.0F;
    }
    
    if ((var3) && (var4) && (!var5) && (!var6))
    {
      var11 = 0.8125F;
      var7 = 0.3125F;
      var8 = 0.6875F;
    }
    else if ((!var3) && (!var4) && (var5) && (var6))
    {
      var11 = 0.8125F;
      var9 = 0.3125F;
      var10 = 0.6875F;
    }
    
    setBlockBounds(var7, 0.0F, var9, var8, var11, var10);
  }
  
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
  {
    setBlockBoundsBasedOnState(worldIn, pos);
    maxY = 1.5D;
    return super.getCollisionBoundingBox(worldIn, pos, state);
  }
  
  public boolean func_176253_e(IBlockAccess p_176253_1_, BlockPos p_176253_2_)
  {
    Block var3 = p_176253_1_.getBlockState(p_176253_2_).getBlock();
    return var3 != Blocks.barrier;
  }
  



  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
  {
    EnumType[] var4 = EnumType.values();
    int var5 = var4.length;
    
    for (int var6 = 0; var6 < var5; var6++)
    {
      EnumType var7 = var4[var6];
      list.add(new ItemStack(itemIn, 1, var7.func_176657_a()));
    }
  }
  



  public int damageDropped(IBlockState state)
  {
    return ((EnumType)state.getValue(field_176255_P)).func_176657_a();
  }
  
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
  {
    return side == EnumFacing.DOWN ? super.shouldSideBeRendered(worldIn, pos, side) : true;
  }
  



  public IBlockState getStateFromMeta(int meta)
  {
    return getDefaultState().withProperty(field_176255_P, EnumType.func_176660_a(meta));
  }
  



  public int getMetaFromState(IBlockState state)
  {
    return ((EnumType)state.getValue(field_176255_P)).func_176657_a();
  }
  




  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
  {
    return state.withProperty(field_176256_a, Boolean.valueOf(!worldIn.isAirBlock(pos.offsetUp()))).withProperty(field_176254_b, Boolean.valueOf(func_176253_e(worldIn, pos.offsetNorth()))).withProperty(field_176257_M, Boolean.valueOf(func_176253_e(worldIn, pos.offsetEast()))).withProperty(field_176258_N, Boolean.valueOf(func_176253_e(worldIn, pos.offsetSouth()))).withProperty(field_176259_O, Boolean.valueOf(func_176253_e(worldIn, pos.offsetWest())));
  }
  
  protected BlockState createBlockState()
  {
    return new BlockState(this, new IProperty[] { field_176256_a, field_176254_b, field_176257_M, field_176259_O, field_176258_N, field_176255_P });
  }
  
  public static enum EnumType implements IStringSerializable
  {
    NORMAL("NORMAL", 0, 0, "cobblestone", "normal"), 
    MOSSY("MOSSY", 1, 1, "mossy_cobblestone", "mossy");
    
    private static final EnumType[] field_176666_c;
    private final int field_176663_d;
    private final String field_176664_e;
    private String field_176661_f;
    private static final EnumType[] $VALUES;
    private static final String __OBFID = "CL_00002048";
    
    private EnumType(String p_i45673_1_, int p_i45673_2_, int p_i45673_3_, String p_i45673_4_, String p_i45673_5_)
    {
      field_176663_d = p_i45673_3_;
      field_176664_e = p_i45673_4_;
      field_176661_f = p_i45673_5_;
    }
    
    public int func_176657_a()
    {
      return field_176663_d;
    }
    
    public String toString()
    {
      return field_176664_e;
    }
    
    public static EnumType func_176660_a(int p_176660_0_)
    {
      if ((p_176660_0_ < 0) || (p_176660_0_ >= field_176666_c.length))
      {
        p_176660_0_ = 0;
      }
      
      return field_176666_c[p_176660_0_];
    }
    
    public String getName()
    {
      return field_176664_e;
    }
    
    public String func_176659_c()
    {
      return field_176661_f;
    }
    
    static
    {
      field_176666_c = new EnumType[values().length];
      



      $VALUES = new EnumType[] { NORMAL, MOSSY };
      







































      EnumType[] var0 = values();
      int var1 = var0.length;
      
      for (int var2 = 0; var2 < var1; var2++)
      {
        EnumType var3 = var0[var2];
        field_176666_c[var3.func_176657_a()] = var3;
      }
    }
  }
}
