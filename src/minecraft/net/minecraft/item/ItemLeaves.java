package net.minecraft.item;

import net.minecraft.block.BlockLeaves;

public class ItemLeaves extends ItemBlock
{
  private final BlockLeaves field_150940_b;
  private static final String __OBFID = "CL_00000046";
  
  public ItemLeaves(BlockLeaves p_i45344_1_)
  {
    super(p_i45344_1_);
    field_150940_b = p_i45344_1_;
    setMaxDamage(0);
    setHasSubtypes(true);
  }
  




  public int getMetadata(int damage)
  {
    return damage | 0x4;
  }
  
  public int getColorFromItemStack(ItemStack stack, int renderPass)
  {
    return field_150940_b.getRenderColor(field_150940_b.getStateFromMeta(stack.getMetadata()));
  }
  




  public String getUnlocalizedName(ItemStack stack)
  {
    return super.getUnlocalizedName() + "." + field_150940_b.func_176233_b(stack.getMetadata()).func_176840_c();
  }
}
