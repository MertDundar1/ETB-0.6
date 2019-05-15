package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDamage extends Enchantment
{
  private static final String[] protectionName = { "all", "undead", "arthropods" };
  



  private static final int[] baseEnchantability = { 1, 5, 5 };
  



  private static final int[] levelEnchantability = { 11, 8, 8 };
  




  private static final int[] thresholdEnchantability = { 20, 20, 20 };
  

  public final int damageType;
  
  private static final String __OBFID = "CL_00000102";
  

  public EnchantmentDamage(int p_i45774_1_, ResourceLocation p_i45774_2_, int p_i45774_3_, int p_i45774_4_)
  {
    super(p_i45774_1_, p_i45774_2_, p_i45774_3_, EnumEnchantmentType.WEAPON);
    damageType = p_i45774_4_;
  }
  



  public int getMinEnchantability(int p_77321_1_)
  {
    return baseEnchantability[damageType] + (p_77321_1_ - 1) * levelEnchantability[damageType];
  }
  



  public int getMaxEnchantability(int p_77317_1_)
  {
    return getMinEnchantability(p_77317_1_) + thresholdEnchantability[damageType];
  }
  



  public int getMaxLevel()
  {
    return 5;
  }
  
  public float func_152376_a(int p_152376_1_, EnumCreatureAttribute p_152376_2_)
  {
    return (damageType == 2) && (p_152376_2_ == EnumCreatureAttribute.ARTHROPOD) ? p_152376_1_ * 2.5F : (damageType == 1) && (p_152376_2_ == EnumCreatureAttribute.UNDEAD) ? p_152376_1_ * 2.5F : damageType == 0 ? p_152376_1_ * 1.25F : 0.0F;
  }
  



  public String getName()
  {
    return "enchantment.damage." + protectionName[damageType];
  }
  



  public boolean canApplyTogether(Enchantment p_77326_1_)
  {
    return !(p_77326_1_ instanceof EnchantmentDamage);
  }
  
  public boolean canApply(ItemStack p_92089_1_)
  {
    return (p_92089_1_.getItem() instanceof ItemAxe) ? true : super.canApply(p_92089_1_);
  }
  
  public void func_151368_a(EntityLivingBase p_151368_1_, Entity p_151368_2_, int p_151368_3_)
  {
    if ((p_151368_2_ instanceof EntityLivingBase))
    {
      EntityLivingBase var4 = (EntityLivingBase)p_151368_2_;
      
      if ((damageType == 2) && (var4.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD))
      {
        int var5 = 20 + p_151368_1_.getRNG().nextInt(10 * p_151368_3_);
        var4.addPotionEffect(new PotionEffect(moveSlowdownid, var5, 3));
      }
    }
  }
}
