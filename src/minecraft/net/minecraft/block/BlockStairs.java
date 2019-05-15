package net.minecraft.block;

import com.enjoytheban.Client;
import com.enjoytheban.management.ModuleManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.modules.render.Xray;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block
{
  public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
  public static final PropertyEnum HALF = PropertyEnum.create("half", EnumHalf.class);
  public static final PropertyEnum SHAPE = PropertyEnum.create("shape", EnumShape.class);
  private static final int[][] field_150150_a = { { 4, 5 }, { 5, 7 }, { 6, 7 }, { 4, 6 }, { 0, 1 }, { 1, 3 }, { 2, 3 }, { 0, 2 } };
  private final Block modelBlock;
  private final IBlockState modelState;
  private boolean field_150152_N;
  private int field_150153_O;
  private static final String __OBFID = "CL_00000314";
  
  protected BlockStairs(IBlockState modelState)
  {
    super(getBlockblockMaterial);
    setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, EnumHalf.BOTTOM).withProperty(SHAPE, EnumShape.STRAIGHT));
    modelBlock = modelState.getBlock();
    this.modelState = modelState;
    setHardness(modelBlock.blockHardness);
    setResistance(modelBlock.blockResistance / 3.0F);
    setStepSound(modelBlock.stepSound);
    setLightOpacity(255);
    setCreativeTab(CreativeTabs.tabBlock);
  }
  
  public void setBlockBoundsBasedOnState(IBlockAccess access, BlockPos pos)
  {
    if (field_150152_N)
    {
      setBlockBounds(0.5F * (field_150153_O % 2), 0.5F * (field_150153_O / 4 % 2), 0.5F * (field_150153_O / 2 % 2), 0.5F + 0.5F * (field_150153_O % 2), 0.5F + 0.5F * (field_150153_O / 4 % 2), 0.5F + 0.5F * (field_150153_O / 2 % 2));
    }
    else
    {
      setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
  



  public void setBaseCollisionBounds(IBlockAccess worldIn, BlockPos pos)
  {
    if (worldIn.getBlockState(pos).getValue(HALF) == EnumHalf.TOP)
    {
      setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
    }
    else
    {
      setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }
  }
  



  public static boolean isBlockStairs(Block p_150148_0_)
  {
    return p_150148_0_ instanceof BlockStairs;
  }
  



  public static boolean isSameStair(IBlockAccess worldIn, BlockPos pos, IBlockState state)
  {
    IBlockState var3 = worldIn.getBlockState(pos);
    Block var4 = var3.getBlock();
    return (isBlockStairs(var4)) && (var3.getValue(HALF) == state.getValue(HALF)) && (var3.getValue(FACING) == state.getValue(FACING));
  }
  
  public int func_176307_f(IBlockAccess p_176307_1_, BlockPos p_176307_2_)
  {
    IBlockState var3 = p_176307_1_.getBlockState(p_176307_2_);
    EnumFacing var4 = (EnumFacing)var3.getValue(FACING);
    EnumHalf var5 = (EnumHalf)var3.getValue(HALF);
    boolean var6 = var5 == EnumHalf.TOP;
    



    if (var4 == EnumFacing.EAST)
    {
      IBlockState var7 = p_176307_1_.getBlockState(p_176307_2_.offsetEast());
      Block var8 = var7.getBlock();
      
      if ((isBlockStairs(var8)) && (var5 == var7.getValue(HALF)))
      {
        EnumFacing var9 = (EnumFacing)var7.getValue(FACING);
        
        if ((var9 == EnumFacing.NORTH) && (!isSameStair(p_176307_1_, p_176307_2_.offsetSouth(), var3)))
        {
          return var6 ? 1 : 2;
        }
        
        if ((var9 == EnumFacing.SOUTH) && (!isSameStair(p_176307_1_, p_176307_2_.offsetNorth(), var3)))
        {
          return var6 ? 2 : 1;
        }
      }
    }
    else if (var4 == EnumFacing.WEST)
    {
      IBlockState var7 = p_176307_1_.getBlockState(p_176307_2_.offsetWest());
      Block var8 = var7.getBlock();
      
      if ((isBlockStairs(var8)) && (var5 == var7.getValue(HALF)))
      {
        EnumFacing var9 = (EnumFacing)var7.getValue(FACING);
        
        if ((var9 == EnumFacing.NORTH) && (!isSameStair(p_176307_1_, p_176307_2_.offsetSouth(), var3)))
        {
          return var6 ? 2 : 1;
        }
        
        if ((var9 == EnumFacing.SOUTH) && (!isSameStair(p_176307_1_, p_176307_2_.offsetNorth(), var3)))
        {
          return var6 ? 1 : 2;
        }
      }
    }
    else if (var4 == EnumFacing.SOUTH)
    {
      IBlockState var7 = p_176307_1_.getBlockState(p_176307_2_.offsetSouth());
      Block var8 = var7.getBlock();
      
      if ((isBlockStairs(var8)) && (var5 == var7.getValue(HALF)))
      {
        EnumFacing var9 = (EnumFacing)var7.getValue(FACING);
        
        if ((var9 == EnumFacing.WEST) && (!isSameStair(p_176307_1_, p_176307_2_.offsetEast(), var3)))
        {
          return var6 ? 2 : 1;
        }
        
        if ((var9 == EnumFacing.EAST) && (!isSameStair(p_176307_1_, p_176307_2_.offsetWest(), var3)))
        {
          return var6 ? 1 : 2;
        }
      }
    }
    else if (var4 == EnumFacing.NORTH)
    {
      IBlockState var7 = p_176307_1_.getBlockState(p_176307_2_.offsetNorth());
      Block var8 = var7.getBlock();
      
      if ((isBlockStairs(var8)) && (var5 == var7.getValue(HALF)))
      {
        EnumFacing var9 = (EnumFacing)var7.getValue(FACING);
        
        if ((var9 == EnumFacing.WEST) && (!isSameStair(p_176307_1_, p_176307_2_.offsetEast(), var3)))
        {
          return var6 ? 1 : 2;
        }
        
        if ((var9 == EnumFacing.EAST) && (!isSameStair(p_176307_1_, p_176307_2_.offsetWest(), var3)))
        {
          return var6 ? 2 : 1;
        }
      }
    }
    
    return 0;
  }
  
  public int func_176305_g(IBlockAccess p_176305_1_, BlockPos p_176305_2_)
  {
    IBlockState var3 = p_176305_1_.getBlockState(p_176305_2_);
    EnumFacing var4 = (EnumFacing)var3.getValue(FACING);
    EnumHalf var5 = (EnumHalf)var3.getValue(HALF);
    boolean var6 = var5 == EnumHalf.TOP;
    



    if (var4 == EnumFacing.EAST)
    {
      IBlockState var7 = p_176305_1_.getBlockState(p_176305_2_.offsetWest());
      Block var8 = var7.getBlock();
      
      if ((isBlockStairs(var8)) && (var5 == var7.getValue(HALF)))
      {
        EnumFacing var9 = (EnumFacing)var7.getValue(FACING);
        
        if ((var9 == EnumFacing.NORTH) && (!isSameStair(p_176305_1_, p_176305_2_.offsetNorth(), var3)))
        {
          return var6 ? 1 : 2;
        }
        
        if ((var9 == EnumFacing.SOUTH) && (!isSameStair(p_176305_1_, p_176305_2_.offsetSouth(), var3)))
        {
          return var6 ? 2 : 1;
        }
      }
    }
    else if (var4 == EnumFacing.WEST)
    {
      IBlockState var7 = p_176305_1_.getBlockState(p_176305_2_.offsetEast());
      Block var8 = var7.getBlock();
      
      if ((isBlockStairs(var8)) && (var5 == var7.getValue(HALF)))
      {
        EnumFacing var9 = (EnumFacing)var7.getValue(FACING);
        
        if ((var9 == EnumFacing.NORTH) && (!isSameStair(p_176305_1_, p_176305_2_.offsetNorth(), var3)))
        {
          return var6 ? 2 : 1;
        }
        
        if ((var9 == EnumFacing.SOUTH) && (!isSameStair(p_176305_1_, p_176305_2_.offsetSouth(), var3)))
        {
          return var6 ? 1 : 2;
        }
      }
    }
    else if (var4 == EnumFacing.SOUTH)
    {
      IBlockState var7 = p_176305_1_.getBlockState(p_176305_2_.offsetNorth());
      Block var8 = var7.getBlock();
      
      if ((isBlockStairs(var8)) && (var5 == var7.getValue(HALF)))
      {
        EnumFacing var9 = (EnumFacing)var7.getValue(FACING);
        
        if ((var9 == EnumFacing.WEST) && (!isSameStair(p_176305_1_, p_176305_2_.offsetWest(), var3)))
        {
          return var6 ? 2 : 1;
        }
        
        if ((var9 == EnumFacing.EAST) && (!isSameStair(p_176305_1_, p_176305_2_.offsetEast(), var3)))
        {
          return var6 ? 1 : 2;
        }
      }
    }
    else if (var4 == EnumFacing.NORTH)
    {
      IBlockState var7 = p_176305_1_.getBlockState(p_176305_2_.offsetSouth());
      Block var8 = var7.getBlock();
      
      if ((isBlockStairs(var8)) && (var5 == var7.getValue(HALF)))
      {
        EnumFacing var9 = (EnumFacing)var7.getValue(FACING);
        
        if ((var9 == EnumFacing.WEST) && (!isSameStair(p_176305_1_, p_176305_2_.offsetWest(), var3)))
        {
          return var6 ? 1 : 2;
        }
        
        if ((var9 == EnumFacing.EAST) && (!isSameStair(p_176305_1_, p_176305_2_.offsetEast(), var3)))
        {
          return var6 ? 2 : 1;
        }
      }
    }
    
    return 0;
  }
  
  public boolean func_176306_h(IBlockAccess p_176306_1_, BlockPos p_176306_2_)
  {
    IBlockState var3 = p_176306_1_.getBlockState(p_176306_2_);
    EnumFacing var4 = (EnumFacing)var3.getValue(FACING);
    EnumHalf var5 = (EnumHalf)var3.getValue(HALF);
    boolean var6 = var5 == EnumHalf.TOP;
    float var7 = 0.5F;
    float var8 = 1.0F;
    
    if (var6)
    {
      var7 = 0.0F;
      var8 = 0.5F;
    }
    
    float var9 = 0.0F;
    float var10 = 1.0F;
    float var11 = 0.0F;
    float var12 = 0.5F;
    boolean var13 = true;
    



    if (var4 == EnumFacing.EAST)
    {
      var9 = 0.5F;
      var12 = 1.0F;
      IBlockState var14 = p_176306_1_.getBlockState(p_176306_2_.offsetEast());
      Block var15 = var14.getBlock();
      
      if ((isBlockStairs(var15)) && (var5 == var14.getValue(HALF)))
      {
        EnumFacing var16 = (EnumFacing)var14.getValue(FACING);
        
        if ((var16 == EnumFacing.NORTH) && (!isSameStair(p_176306_1_, p_176306_2_.offsetSouth(), var3)))
        {
          var12 = 0.5F;
          var13 = false;
        }
        else if ((var16 == EnumFacing.SOUTH) && (!isSameStair(p_176306_1_, p_176306_2_.offsetNorth(), var3)))
        {
          var11 = 0.5F;
          var13 = false;
        }
      }
    }
    else if (var4 == EnumFacing.WEST)
    {
      var10 = 0.5F;
      var12 = 1.0F;
      IBlockState var14 = p_176306_1_.getBlockState(p_176306_2_.offsetWest());
      Block var15 = var14.getBlock();
      
      if ((isBlockStairs(var15)) && (var5 == var14.getValue(HALF)))
      {
        EnumFacing var16 = (EnumFacing)var14.getValue(FACING);
        
        if ((var16 == EnumFacing.NORTH) && (!isSameStair(p_176306_1_, p_176306_2_.offsetSouth(), var3)))
        {
          var12 = 0.5F;
          var13 = false;
        }
        else if ((var16 == EnumFacing.SOUTH) && (!isSameStair(p_176306_1_, p_176306_2_.offsetNorth(), var3)))
        {
          var11 = 0.5F;
          var13 = false;
        }
      }
    }
    else if (var4 == EnumFacing.SOUTH)
    {
      var11 = 0.5F;
      var12 = 1.0F;
      IBlockState var14 = p_176306_1_.getBlockState(p_176306_2_.offsetSouth());
      Block var15 = var14.getBlock();
      
      if ((isBlockStairs(var15)) && (var5 == var14.getValue(HALF)))
      {
        EnumFacing var16 = (EnumFacing)var14.getValue(FACING);
        
        if ((var16 == EnumFacing.WEST) && (!isSameStair(p_176306_1_, p_176306_2_.offsetEast(), var3)))
        {
          var10 = 0.5F;
          var13 = false;
        }
        else if ((var16 == EnumFacing.EAST) && (!isSameStair(p_176306_1_, p_176306_2_.offsetWest(), var3)))
        {
          var9 = 0.5F;
          var13 = false;
        }
      }
    }
    else if (var4 == EnumFacing.NORTH)
    {
      IBlockState var14 = p_176306_1_.getBlockState(p_176306_2_.offsetNorth());
      Block var15 = var14.getBlock();
      
      if ((isBlockStairs(var15)) && (var5 == var14.getValue(HALF)))
      {
        EnumFacing var16 = (EnumFacing)var14.getValue(FACING);
        
        if ((var16 == EnumFacing.WEST) && (!isSameStair(p_176306_1_, p_176306_2_.offsetEast(), var3)))
        {
          var10 = 0.5F;
          var13 = false;
        }
        else if ((var16 == EnumFacing.EAST) && (!isSameStair(p_176306_1_, p_176306_2_.offsetWest(), var3)))
        {
          var9 = 0.5F;
          var13 = false;
        }
      }
    }
    
    setBlockBounds(var9, var7, var11, var10, var8, var12);
    return var13;
  }
  
  public boolean func_176304_i(IBlockAccess p_176304_1_, BlockPos p_176304_2_)
  {
    IBlockState var3 = p_176304_1_.getBlockState(p_176304_2_);
    EnumFacing var4 = (EnumFacing)var3.getValue(FACING);
    EnumHalf var5 = (EnumHalf)var3.getValue(HALF);
    boolean var6 = var5 == EnumHalf.TOP;
    float var7 = 0.5F;
    float var8 = 1.0F;
    
    if (var6)
    {
      var7 = 0.0F;
      var8 = 0.5F;
    }
    
    float var9 = 0.0F;
    float var10 = 0.5F;
    float var11 = 0.5F;
    float var12 = 1.0F;
    boolean var13 = false;
    



    if (var4 == EnumFacing.EAST)
    {
      IBlockState var14 = p_176304_1_.getBlockState(p_176304_2_.offsetWest());
      Block var15 = var14.getBlock();
      
      if ((isBlockStairs(var15)) && (var5 == var14.getValue(HALF)))
      {
        EnumFacing var16 = (EnumFacing)var14.getValue(FACING);
        
        if ((var16 == EnumFacing.NORTH) && (!isSameStair(p_176304_1_, p_176304_2_.offsetNorth(), var3)))
        {
          var11 = 0.0F;
          var12 = 0.5F;
          var13 = true;
        }
        else if ((var16 == EnumFacing.SOUTH) && (!isSameStair(p_176304_1_, p_176304_2_.offsetSouth(), var3)))
        {
          var11 = 0.5F;
          var12 = 1.0F;
          var13 = true;
        }
      }
    }
    else if (var4 == EnumFacing.WEST)
    {
      IBlockState var14 = p_176304_1_.getBlockState(p_176304_2_.offsetEast());
      Block var15 = var14.getBlock();
      
      if ((isBlockStairs(var15)) && (var5 == var14.getValue(HALF)))
      {
        var9 = 0.5F;
        var10 = 1.0F;
        EnumFacing var16 = (EnumFacing)var14.getValue(FACING);
        
        if ((var16 == EnumFacing.NORTH) && (!isSameStair(p_176304_1_, p_176304_2_.offsetNorth(), var3)))
        {
          var11 = 0.0F;
          var12 = 0.5F;
          var13 = true;
        }
        else if ((var16 == EnumFacing.SOUTH) && (!isSameStair(p_176304_1_, p_176304_2_.offsetSouth(), var3)))
        {
          var11 = 0.5F;
          var12 = 1.0F;
          var13 = true;
        }
      }
    }
    else if (var4 == EnumFacing.SOUTH)
    {
      IBlockState var14 = p_176304_1_.getBlockState(p_176304_2_.offsetNorth());
      Block var15 = var14.getBlock();
      
      if ((isBlockStairs(var15)) && (var5 == var14.getValue(HALF)))
      {
        var11 = 0.0F;
        var12 = 0.5F;
        EnumFacing var16 = (EnumFacing)var14.getValue(FACING);
        
        if ((var16 == EnumFacing.WEST) && (!isSameStair(p_176304_1_, p_176304_2_.offsetWest(), var3)))
        {
          var13 = true;
        }
        else if ((var16 == EnumFacing.EAST) && (!isSameStair(p_176304_1_, p_176304_2_.offsetEast(), var3)))
        {
          var9 = 0.5F;
          var10 = 1.0F;
          var13 = true;
        }
      }
    }
    else if (var4 == EnumFacing.NORTH)
    {
      IBlockState var14 = p_176304_1_.getBlockState(p_176304_2_.offsetSouth());
      Block var15 = var14.getBlock();
      
      if ((isBlockStairs(var15)) && (var5 == var14.getValue(HALF)))
      {
        EnumFacing var16 = (EnumFacing)var14.getValue(FACING);
        
        if ((var16 == EnumFacing.WEST) && (!isSameStair(p_176304_1_, p_176304_2_.offsetWest(), var3)))
        {
          var13 = true;
        }
        else if ((var16 == EnumFacing.EAST) && (!isSameStair(p_176304_1_, p_176304_2_.offsetEast(), var3)))
        {
          var9 = 0.5F;
          var10 = 1.0F;
          var13 = true;
        }
      }
    }
    
    if (var13)
    {
      setBlockBounds(var9, var7, var11, var10, var8, var12);
    }
    
    return var13;
  }
  





  public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity)
  {
    setBaseCollisionBounds(worldIn, pos);
    super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    boolean var7 = func_176306_h(worldIn, pos);
    super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    
    if ((var7) && (func_176304_i(worldIn, pos)))
    {
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }
    
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
  }
  
  public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
  {
    modelBlock.randomDisplayTick(worldIn, pos, state, rand);
  }
  
  public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
  {
    modelBlock.onBlockClicked(worldIn, pos, playerIn);
  }
  



  public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
  {
    modelBlock.onBlockDestroyedByPlayer(worldIn, pos, state);
  }
  
  public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos)
  {
    return modelBlock.getMixedBrightnessForBlock(worldIn, pos);
  }
  



  public float getExplosionResistance(Entity exploder)
  {
    return modelBlock.getExplosionResistance(exploder);
  }
  
  public EnumWorldBlockLayer getBlockLayer()
  {
    if (Client.instance.getModuleManager().getModuleByClass(Xray.class).isEnabled()) {
      return EnumWorldBlockLayer.TRANSLUCENT;
    }
    return modelBlock.getBlockLayer();
  }
  



  public int tickRate(World worldIn)
  {
    return modelBlock.tickRate(worldIn);
  }
  
  public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
  {
    return modelBlock.getSelectedBoundingBox(worldIn, pos);
  }
  
  public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion)
  {
    return modelBlock.modifyAcceleration(worldIn, pos, entityIn, motion);
  }
  



  public boolean isCollidable()
  {
    return modelBlock.isCollidable();
  }
  
  public boolean canCollideCheck(IBlockState state, boolean p_176209_2_)
  {
    return modelBlock.canCollideCheck(state, p_176209_2_);
  }
  
  public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
  {
    return modelBlock.canPlaceBlockAt(worldIn, pos);
  }
  
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
  {
    onNeighborBlockChange(worldIn, pos, modelState, net.minecraft.init.Blocks.air);
    modelBlock.onBlockAdded(worldIn, pos, modelState);
  }
  
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
  {
    modelBlock.breakBlock(worldIn, pos, modelState);
  }
  



  public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn)
  {
    modelBlock.onEntityCollidedWithBlock(worldIn, pos, entityIn);
  }
  
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
  {
    modelBlock.updateTick(worldIn, pos, state, rand);
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
  {
    return modelBlock.onBlockActivated(worldIn, pos, modelState, playerIn, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
  }
  



  public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
  {
    modelBlock.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
  }
  



  public MapColor getMapColor(IBlockState state)
  {
    return modelBlock.getMapColor(modelState);
  }
  
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
  {
    IBlockState var9 = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    var9 = var9.withProperty(FACING, placer.func_174811_aO()).withProperty(SHAPE, EnumShape.STRAIGHT);
    return (facing != EnumFacing.DOWN) && ((facing == EnumFacing.UP) || (hitY <= 0.5D)) ? var9.withProperty(HALF, EnumHalf.BOTTOM) : var9.withProperty(HALF, EnumHalf.TOP);
  }
  






  public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end)
  {
    MovingObjectPosition[] var5 = new MovingObjectPosition[8];
    IBlockState var6 = worldIn.getBlockState(pos);
    int var7 = ((EnumFacing)var6.getValue(FACING)).getHorizontalIndex();
    boolean var8 = var6.getValue(HALF) == EnumHalf.TOP;
    int[] var9 = field_150150_a[(var7 + 0)];
    field_150152_N = true;
    
    for (int var10 = 0; var10 < 8; var10++)
    {
      field_150153_O = var10;
      
      if (Arrays.binarySearch(var9, var10) < 0)
      {
        var5[var10] = super.collisionRayTrace(worldIn, pos, start, end);
      }
    }
    
    int[] var19 = var9;
    int var11 = var9.length;
    
    for (int var12 = 0; var12 < var11; var12++)
    {
      int var13 = var19[var12];
      var5[var13] = null;
    }
    
    MovingObjectPosition var20 = null;
    double var21 = 0.0D;
    MovingObjectPosition[] var22 = var5;
    int var14 = var5.length;
    
    for (int var15 = 0; var15 < var14; var15++)
    {
      MovingObjectPosition var16 = var22[var15];
      
      if (var16 != null)
      {
        double var17 = hitVec.squareDistanceTo(end);
        
        if (var17 > var21)
        {
          var20 = var16;
          var21 = var17;
        }
      }
    }
    
    return var20;
  }
  



  public IBlockState getStateFromMeta(int meta)
  {
    IBlockState var2 = getDefaultState().withProperty(HALF, (meta & 0x4) > 0 ? EnumHalf.TOP : EnumHalf.BOTTOM);
    var2 = var2.withProperty(FACING, EnumFacing.getFront(5 - (meta & 0x3)));
    return var2;
  }
  



  public int getMetaFromState(IBlockState state)
  {
    int var2 = 0;
    
    if (state.getValue(HALF) == EnumHalf.TOP)
    {
      var2 |= 0x4;
    }
    
    var2 |= 5 - ((EnumFacing)state.getValue(FACING)).getIndex();
    return var2;
  }
  




  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
  {
    if (func_176306_h(worldIn, pos))
    {
      switch (func_176305_g(worldIn, pos))
      {
      case 0: 
        state = state.withProperty(SHAPE, EnumShape.STRAIGHT);
        break;
      
      case 1: 
        state = state.withProperty(SHAPE, EnumShape.INNER_RIGHT);
        break;
      
      case 2: 
        state = state.withProperty(SHAPE, EnumShape.INNER_LEFT);
      
      }
      
    } else {
      switch (func_176307_f(worldIn, pos))
      {
      case 0: 
        state = state.withProperty(SHAPE, EnumShape.STRAIGHT);
        break;
      
      case 1: 
        state = state.withProperty(SHAPE, EnumShape.OUTER_RIGHT);
        break;
      
      case 2: 
        state = state.withProperty(SHAPE, EnumShape.OUTER_LEFT);
      }
      
    }
    return state;
  }
  
  protected BlockState createBlockState()
  {
    return new BlockState(this, new IProperty[] { FACING, HALF, SHAPE });
  }
  
  public static enum EnumHalf implements IStringSerializable
  {
    TOP("TOP", 0, "top"), 
    BOTTOM("BOTTOM", 1, "bottom");
    
    private final String field_176709_c;
    private static final EnumHalf[] $VALUES = { TOP, BOTTOM };
    private static final String __OBFID = "CL_00002062";
    
    private EnumHalf(String p_i45683_1_, int p_i45683_2_, String p_i45683_3_)
    {
      field_176709_c = p_i45683_3_;
    }
    
    public String toString()
    {
      return field_176709_c;
    }
    
    public String getName()
    {
      return field_176709_c;
    }
  }
  
  public static enum EnumShape implements IStringSerializable
  {
    STRAIGHT("STRAIGHT", 0, "straight"), 
    INNER_LEFT("INNER_LEFT", 1, "inner_left"), 
    INNER_RIGHT("INNER_RIGHT", 2, "inner_right"), 
    OUTER_LEFT("OUTER_LEFT", 3, "outer_left"), 
    OUTER_RIGHT("OUTER_RIGHT", 4, "outer_right");
    
    private final String field_176699_f;
    private static final EnumShape[] $VALUES = { STRAIGHT, INNER_LEFT, INNER_RIGHT, OUTER_LEFT, OUTER_RIGHT };
    private static final String __OBFID = "CL_00002061";
    
    private EnumShape(String p_i45682_1_, int p_i45682_2_, String p_i45682_3_)
    {
      field_176699_f = p_i45682_3_;
    }
    
    public String toString()
    {
      return field_176699_f;
    }
    
    public String getName()
    {
      return field_176699_f;
    }
  }
}
