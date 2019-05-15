package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemDye extends Item
{
  public static final int[] dyeColors = { 1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320 };
  private static final String __OBFID = "CL_00000022";
  
  public ItemDye()
  {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(CreativeTabs.tabMaterials);
  }
  




  public String getUnlocalizedName(ItemStack stack)
  {
    int var2 = stack.getMetadata();
    return super.getUnlocalizedName() + "." + EnumDyeColor.func_176766_a(var2).func_176762_d();
  }
  






  public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (!playerIn.func_175151_a(pos.offset(side), side, stack))
    {
      return false;
    }
    

    EnumDyeColor var9 = EnumDyeColor.func_176766_a(stack.getMetadata());
    
    if (var9 == EnumDyeColor.WHITE)
    {
      if (func_179234_a(stack, worldIn, pos))
      {
        if (!isRemote)
        {
          worldIn.playAuxSFX(2005, pos, 0);
        }
        
        return true;
      }
    }
    else if (var9 == EnumDyeColor.BROWN)
    {
      IBlockState var10 = worldIn.getBlockState(pos);
      Block var11 = var10.getBlock();
      
      if ((var11 == Blocks.log) && (var10.getValue(net.minecraft.block.BlockPlanks.VARIANT_PROP) == net.minecraft.block.BlockPlanks.EnumType.JUNGLE))
      {
        if (side == EnumFacing.DOWN)
        {
          return false;
        }
        
        if (side == EnumFacing.UP)
        {
          return false;
        }
        
        pos = pos.offset(side);
        
        if (worldIn.isAirBlock(pos))
        {
          IBlockState var12 = Blocks.cocoa.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
          worldIn.setBlockState(pos, var12, 2);
          
          if (!capabilities.isCreativeMode)
          {
            stackSize -= 1;
          }
        }
        
        return true;
      }
    }
    
    return false;
  }
  

  public static boolean func_179234_a(ItemStack p_179234_0_, World worldIn, BlockPos p_179234_2_)
  {
    IBlockState var3 = worldIn.getBlockState(p_179234_2_);
    
    if ((var3.getBlock() instanceof IGrowable))
    {
      IGrowable var4 = (IGrowable)var3.getBlock();
      
      if (var4.isStillGrowing(worldIn, p_179234_2_, var3, isRemote))
      {
        if (!isRemote)
        {
          if (var4.canUseBonemeal(worldIn, rand, p_179234_2_, var3))
          {
            var4.grow(worldIn, rand, p_179234_2_, var3);
          }
          
          stackSize -= 1;
        }
        
        return true;
      }
    }
    
    return false;
  }
  
  public static void func_180617_a(World worldIn, BlockPos p_180617_1_, int p_180617_2_)
  {
    if (p_180617_2_ == 0)
    {
      p_180617_2_ = 15;
    }
    
    Block var3 = worldIn.getBlockState(p_180617_1_).getBlock();
    
    if (var3.getMaterial() != net.minecraft.block.material.Material.air)
    {
      var3.setBlockBoundsBasedOnState(worldIn, p_180617_1_);
      
      for (int var4 = 0; var4 < p_180617_2_; var4++)
      {
        double var5 = itemRand.nextGaussian() * 0.02D;
        double var7 = itemRand.nextGaussian() * 0.02D;
        double var9 = itemRand.nextGaussian() * 0.02D;
        worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, p_180617_1_.getX() + itemRand.nextFloat(), p_180617_1_.getY() + itemRand.nextFloat() * var3.getBlockBoundsMaxY(), p_180617_1_.getZ() + itemRand.nextFloat(), var5, var7, var9, new int[0]);
      }
    }
  }
  



  public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target)
  {
    if ((target instanceof EntitySheep))
    {
      EntitySheep var4 = (EntitySheep)target;
      EnumDyeColor var5 = EnumDyeColor.func_176766_a(stack.getMetadata());
      
      if ((!var4.getSheared()) && (var4.func_175509_cj() != var5))
      {
        var4.func_175512_b(var5);
        stackSize -= 1;
      }
      
      return true;
    }
    

    return false;
  }
  






  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
  {
    for (int var4 = 0; var4 < 16; var4++)
    {
      subItems.add(new ItemStack(itemIn, 1, var4));
    }
  }
}
