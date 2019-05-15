package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item
{
  private final Class hangingEntityClass;
  private static final String __OBFID = "CL_00000038";
  
  public ItemHangingEntity(Class p_i45342_1_)
  {
    hangingEntityClass = p_i45342_1_;
    setCreativeTab(CreativeTabs.tabDecorations);
  }
  






  public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (side == EnumFacing.DOWN)
    {
      return false;
    }
    if (side == EnumFacing.UP)
    {
      return false;
    }
    

    BlockPos var9 = pos.offset(side);
    
    if (!playerIn.func_175151_a(var9, side, stack))
    {
      return false;
    }
    

    EntityHanging var10 = func_179233_a(worldIn, var9, side);
    
    if ((var10 != null) && (var10.onValidSurface()))
    {
      if (!isRemote)
      {
        worldIn.spawnEntityInWorld(var10);
      }
      
      stackSize -= 1;
    }
    
    return true;
  }
  


  private EntityHanging func_179233_a(World worldIn, BlockPos p_179233_2_, EnumFacing p_179233_3_)
  {
    return hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(worldIn, p_179233_2_, p_179233_3_) : hangingEntityClass == EntityPainting.class ? new EntityPainting(worldIn, p_179233_2_, p_179233_3_) : null;
  }
}
