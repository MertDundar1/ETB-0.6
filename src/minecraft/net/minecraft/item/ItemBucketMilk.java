package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemBucketMilk extends Item
{
  private static final String __OBFID = "CL_00000048";
  
  public ItemBucketMilk()
  {
    setMaxStackSize(1);
    setCreativeTab(CreativeTabs.tabMisc);
  }
  




  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
  {
    if (!capabilities.isCreativeMode)
    {
      stackSize -= 1;
    }
    
    if (!isRemote)
    {
      playerIn.clearActivePotions();
    }
    
    playerIn.triggerAchievement(net.minecraft.stats.StatList.objectUseStats[Item.getIdFromItem(this)]);
    return stackSize <= 0 ? new ItemStack(Items.bucket) : stack;
  }
  



  public int getMaxItemUseDuration(ItemStack stack)
  {
    return 32;
  }
  



  public EnumAction getItemUseAction(ItemStack stack)
  {
    return EnumAction.DRINK;
  }
  



  public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
  {
    playerIn.setItemInUse(itemStackIn, getMaxItemUseDuration(itemStackIn));
    return itemStackIn;
  }
}
