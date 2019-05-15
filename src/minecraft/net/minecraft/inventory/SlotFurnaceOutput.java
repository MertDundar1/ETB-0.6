package net.minecraft.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class SlotFurnaceOutput extends Slot
{
  private EntityPlayer thePlayer;
  private int field_75228_b;
  private static final String __OBFID = "CL_00002183";
  
  public SlotFurnaceOutput(EntityPlayer p_i45793_1_, IInventory p_i45793_2_, int p_i45793_3_, int p_i45793_4_, int p_i45793_5_)
  {
    super(p_i45793_2_, p_i45793_3_, p_i45793_4_, p_i45793_5_);
    thePlayer = p_i45793_1_;
  }
  



  public boolean isItemValid(ItemStack stack)
  {
    return false;
  }
  




  public ItemStack decrStackSize(int p_75209_1_)
  {
    if (getHasStack())
    {
      field_75228_b += Math.min(p_75209_1_, getStackstackSize);
    }
    
    return super.decrStackSize(p_75209_1_);
  }
  
  public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
  {
    onCrafting(stack);
    super.onPickupFromSlot(playerIn, stack);
  }
  




  protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_)
  {
    field_75228_b += p_75210_2_;
    onCrafting(p_75210_1_);
  }
  



  protected void onCrafting(ItemStack p_75208_1_)
  {
    p_75208_1_.onCrafting(thePlayer.worldObj, thePlayer, field_75228_b);
    
    if (!thePlayer.worldObj.isRemote)
    {
      int var2 = field_75228_b;
      float var3 = FurnaceRecipes.instance().getSmeltingExperience(p_75208_1_);
      

      if (var3 == 0.0F)
      {
        var2 = 0;
      }
      else if (var3 < 1.0F)
      {
        int var4 = MathHelper.floor_float(var2 * var3);
        
        if ((var4 < MathHelper.ceiling_float_int(var2 * var3)) && (Math.random() < var2 * var3 - var4))
        {
          var4++;
        }
        
        var2 = var4;
      }
      
      while (var2 > 0)
      {
        int var4 = EntityXPOrb.getXPSplit(var2);
        var2 -= var4;
        thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(thePlayer.worldObj, thePlayer.posX, thePlayer.posY + 0.5D, thePlayer.posZ + 0.5D, var4));
      }
    }
    
    field_75228_b = 0;
    
    if (p_75208_1_.getItem() == Items.iron_ingot)
    {
      thePlayer.triggerAchievement(AchievementList.acquireIron);
    }
    
    if (p_75208_1_.getItem() == Items.cooked_fish)
    {
      thePlayer.triggerAchievement(AchievementList.cookFish);
    }
  }
}
