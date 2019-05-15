package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemAppleGold extends ItemFood
{
  private static final String __OBFID = "CL_00000037";
  
  public ItemAppleGold(int p_i45341_1_, float p_i45341_2_, boolean p_i45341_3_)
  {
    super(p_i45341_1_, p_i45341_2_, p_i45341_3_);
    setHasSubtypes(true);
  }
  
  public boolean hasEffect(ItemStack stack)
  {
    return stack.getMetadata() > 0;
  }
  



  public EnumRarity getRarity(ItemStack stack)
  {
    return stack.getMetadata() == 0 ? EnumRarity.RARE : EnumRarity.EPIC;
  }
  
  protected void onFoodEaten(ItemStack p_77849_1_, World worldIn, EntityPlayer p_77849_3_)
  {
    if (!isRemote)
    {
      p_77849_3_.addPotionEffect(new PotionEffect(absorptionid, 2400, 0));
    }
    
    if (p_77849_1_.getMetadata() > 0)
    {
      if (!isRemote)
      {
        p_77849_3_.addPotionEffect(new PotionEffect(regenerationid, 600, 4));
        p_77849_3_.addPotionEffect(new PotionEffect(resistanceid, 6000, 0));
        p_77849_3_.addPotionEffect(new PotionEffect(fireResistanceid, 6000, 0));
      }
      
    }
    else {
      super.onFoodEaten(p_77849_1_, worldIn, p_77849_3_);
    }
  }
  





  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
  {
    subItems.add(new ItemStack(itemIn, 1, 0));
    subItems.add(new ItemStack(itemIn, 1, 1));
  }
}
