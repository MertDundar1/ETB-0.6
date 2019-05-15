package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public class ShapelessRecipes
  implements IRecipe
{
  private final ItemStack recipeOutput;
  private final List recipeItems;
  private static final String __OBFID = "CL_00000094";
  
  public ShapelessRecipes(ItemStack p_i1918_1_, List p_i1918_2_)
  {
    recipeOutput = p_i1918_1_;
    recipeItems = p_i1918_2_;
  }
  
  public ItemStack getRecipeOutput()
  {
    return recipeOutput;
  }
  
  public ItemStack[] func_179532_b(InventoryCrafting p_179532_1_)
  {
    ItemStack[] var2 = new ItemStack[p_179532_1_.getSizeInventory()];
    
    for (int var3 = 0; var3 < var2.length; var3++)
    {
      ItemStack var4 = p_179532_1_.getStackInSlot(var3);
      
      if ((var4 != null) && (var4.getItem().hasContainerItem()))
      {
        var2[var3] = new ItemStack(var4.getItem().getContainerItem());
      }
    }
    
    return var2;
  }
  



  public boolean matches(InventoryCrafting p_77569_1_, World worldIn)
  {
    ArrayList var3 = Lists.newArrayList(recipeItems);
    
    for (int var4 = 0; var4 < p_77569_1_.func_174923_h(); var4++)
    {
      for (int var5 = 0; var5 < p_77569_1_.func_174922_i(); var5++)
      {
        ItemStack var6 = p_77569_1_.getStackInRowAndColumn(var5, var4);
        
        if (var6 != null)
        {
          boolean var7 = false;
          Iterator var8 = var3.iterator();
          
          while (var8.hasNext())
          {
            ItemStack var9 = (ItemStack)var8.next();
            
            if ((var6.getItem() == var9.getItem()) && ((var9.getMetadata() == 32767) || (var6.getMetadata() == var9.getMetadata())))
            {
              var7 = true;
              var3.remove(var9);
              break;
            }
          }
          
          if (!var7)
          {
            return false;
          }
        }
      }
    }
    
    return var3.isEmpty();
  }
  



  public ItemStack getCraftingResult(InventoryCrafting p_77572_1_)
  {
    return recipeOutput.copy();
  }
  



  public int getRecipeSize()
  {
    return recipeItems.size();
  }
}
