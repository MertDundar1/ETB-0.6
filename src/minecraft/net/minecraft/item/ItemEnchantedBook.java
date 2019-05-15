package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item
{
  private static final String __OBFID = "CL_00000025";
  
  public ItemEnchantedBook() {}
  
  public boolean hasEffect(ItemStack stack)
  {
    return true;
  }
  



  public boolean isItemTool(ItemStack stack)
  {
    return false;
  }
  



  public EnumRarity getRarity(ItemStack stack)
  {
    return func_92110_g(stack).tagCount() > 0 ? EnumRarity.UNCOMMON : super.getRarity(stack);
  }
  
  public NBTTagList func_92110_g(ItemStack p_92110_1_)
  {
    NBTTagCompound var2 = p_92110_1_.getTagCompound();
    return (var2 != null) && (var2.hasKey("StoredEnchantments", 9)) ? (NBTTagList)var2.getTag("StoredEnchantments") : new NBTTagList();
  }
  






  public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
  {
    super.addInformation(stack, playerIn, tooltip, advanced);
    NBTTagList var5 = func_92110_g(stack);
    
    if (var5 != null)
    {
      for (int var6 = 0; var6 < var5.tagCount(); var6++)
      {
        short var7 = var5.getCompoundTagAt(var6).getShort("id");
        short var8 = var5.getCompoundTagAt(var6).getShort("lvl");
        
        if (Enchantment.func_180306_c(var7) != null)
        {
          tooltip.add(Enchantment.func_180306_c(var7).getTranslatedName(var8));
        }
      }
    }
  }
  



  public void addEnchantment(ItemStack p_92115_1_, EnchantmentData p_92115_2_)
  {
    NBTTagList var3 = func_92110_g(p_92115_1_);
    boolean var4 = true;
    
    for (int var5 = 0; var5 < var3.tagCount(); var5++)
    {
      NBTTagCompound var6 = var3.getCompoundTagAt(var5);
      
      if (var6.getShort("id") == enchantmentobj.effectId)
      {
        if (var6.getShort("lvl") < enchantmentLevel)
        {
          var6.setShort("lvl", (short)enchantmentLevel);
        }
        
        var4 = false;
        break;
      }
    }
    
    if (var4)
    {
      NBTTagCompound var7 = new NBTTagCompound();
      var7.setShort("id", (short)enchantmentobj.effectId);
      var7.setShort("lvl", (short)enchantmentLevel);
      var3.appendTag(var7);
    }
    
    if (!p_92115_1_.hasTagCompound())
    {
      p_92115_1_.setTagCompound(new NBTTagCompound());
    }
    
    p_92115_1_.getTagCompound().setTag("StoredEnchantments", var3);
  }
  



  public ItemStack getEnchantedItemStack(EnchantmentData p_92111_1_)
  {
    ItemStack var2 = new ItemStack(this);
    addEnchantment(var2, p_92111_1_);
    return var2;
  }
  
  public void func_92113_a(Enchantment p_92113_1_, List p_92113_2_)
  {
    for (int var3 = p_92113_1_.getMinLevel(); var3 <= p_92113_1_.getMaxLevel(); var3++)
    {
      p_92113_2_.add(getEnchantedItemStack(new EnchantmentData(p_92113_1_, var3)));
    }
  }
  
  public WeightedRandomChestContent getRandomEnchantedBook(Random p_92114_1_)
  {
    return func_92112_a(p_92114_1_, 1, 1, 1);
  }
  
  public WeightedRandomChestContent func_92112_a(Random p_92112_1_, int p_92112_2_, int p_92112_3_, int p_92112_4_)
  {
    ItemStack var5 = new ItemStack(net.minecraft.init.Items.book, 1, 0);
    net.minecraft.enchantment.EnchantmentHelper.addRandomEnchantment(p_92112_1_, var5, 30);
    return new WeightedRandomChestContent(var5, p_92112_2_, p_92112_3_, p_92112_4_);
  }
}
