package net.minecraft.enchantment;

import net.minecraft.util.WeightedRandom.Item;



public class EnchantmentData
  extends WeightedRandom.Item
{
  public final Enchantment enchantmentobj;
  public final int enchantmentLevel;
  private static final String __OBFID = "CL_00000115";
  
  public EnchantmentData(Enchantment p_i1930_1_, int p_i1930_2_)
  {
    super(p_i1930_1_.getWeight());
    enchantmentobj = p_i1930_1_;
    enchantmentLevel = p_i1930_2_;
  }
}
