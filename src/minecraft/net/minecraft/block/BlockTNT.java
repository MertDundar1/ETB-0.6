package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block
{
  public static final PropertyBool field_176246_a = PropertyBool.create("explode");
  private static final String __OBFID = "CL_00000324";
  
  public BlockTNT()
  {
    super(net.minecraft.block.material.Material.tnt);
    setDefaultState(blockState.getBaseState().withProperty(field_176246_a, Boolean.valueOf(false)));
    setCreativeTab(CreativeTabs.tabRedstone);
  }
  
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
  {
    super.onBlockAdded(worldIn, pos, state);
    
    if (worldIn.isBlockPowered(pos))
    {
      onBlockDestroyedByPlayer(worldIn, pos, state.withProperty(field_176246_a, Boolean.valueOf(true)));
      worldIn.setBlockToAir(pos);
    }
  }
  
  public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
  {
    if (worldIn.isBlockPowered(pos))
    {
      onBlockDestroyedByPlayer(worldIn, pos, state.withProperty(field_176246_a, Boolean.valueOf(true)));
      worldIn.setBlockToAir(pos);
    }
  }
  



  public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
  {
    if (!isRemote)
    {
      EntityTNTPrimed var4 = new EntityTNTPrimed(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, explosionIn.getExplosivePlacedBy());
      fuse = (rand.nextInt(fuse / 4) + fuse / 8);
      worldIn.spawnEntityInWorld(var4);
    }
  }
  



  public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
  {
    func_180692_a(worldIn, pos, state, null);
  }
  
  public void func_180692_a(World worldIn, BlockPos p_180692_2_, IBlockState p_180692_3_, EntityLivingBase p_180692_4_)
  {
    if (!isRemote)
    {
      if (((Boolean)p_180692_3_.getValue(field_176246_a)).booleanValue())
      {
        EntityTNTPrimed var5 = new EntityTNTPrimed(worldIn, p_180692_2_.getX() + 0.5F, p_180692_2_.getY() + 0.5F, p_180692_2_.getZ() + 0.5F, p_180692_4_);
        worldIn.spawnEntityInWorld(var5);
        worldIn.playSoundAtEntity(var5, "game.tnt.primed", 1.0F, 1.0F);
      }
    }
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (playerIn.getCurrentEquippedItem() != null)
    {
      net.minecraft.item.Item var9 = playerIn.getCurrentEquippedItem().getItem();
      
      if ((var9 == Items.flint_and_steel) || (var9 == Items.fire_charge))
      {
        func_180692_a(worldIn, pos, state.withProperty(field_176246_a, Boolean.valueOf(true)), playerIn);
        worldIn.setBlockToAir(pos);
        
        if (var9 == Items.flint_and_steel)
        {
          playerIn.getCurrentEquippedItem().damageItem(1, playerIn);
        }
        else if (!capabilities.isCreativeMode)
        {
          getCurrentEquippedItemstackSize -= 1;
        }
        
        return true;
      }
    }
    
    return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
  }
  



  public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
  {
    if ((!isRemote) && ((entityIn instanceof EntityArrow)))
    {
      EntityArrow var5 = (EntityArrow)entityIn;
      
      if (var5.isBurning())
      {
        func_180692_a(worldIn, pos, worldIn.getBlockState(pos).withProperty(field_176246_a, Boolean.valueOf(true)), (shootingEntity instanceof EntityLivingBase) ? (EntityLivingBase)shootingEntity : null);
        worldIn.setBlockToAir(pos);
      }
    }
  }
  



  public boolean canDropFromExplosion(Explosion explosionIn)
  {
    return false;
  }
  



  public IBlockState getStateFromMeta(int meta)
  {
    return getDefaultState().withProperty(field_176246_a, Boolean.valueOf((meta & 0x1) > 0));
  }
  



  public int getMetaFromState(IBlockState state)
  {
    return ((Boolean)state.getValue(field_176246_a)).booleanValue() ? 1 : 0;
  }
  
  protected BlockState createBlockState()
  {
    return new BlockState(this, new net.minecraft.block.properties.IProperty[] { field_176246_a });
  }
}
