package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesWeapons
{
  private String[][] recipePatterns = { { "X", "X", "#" } };
  private Object[][] recipeItems;
  private static final String __OBFID = "CL_00000097";
  
  public RecipesWeapons()
  {
    recipeItems = new Object[][] { { Blocks.planks, Blocks.cobblestone, Items.iron_ingot, Items.diamond, Items.gold_ingot }, { Items.wooden_sword, Items.stone_sword, Items.iron_sword, Items.diamond_sword, Items.golden_sword } };
  }
  



  public void addRecipes(CraftingManager p_77583_1_)
  {
    for (int var2 = 0; var2 < recipeItems[0].length; var2++)
    {
      Object var3 = recipeItems[0][var2];
      
      for (int var4 = 0; var4 < recipeItems.length - 1; var4++)
      {
        Item var5 = (Item)recipeItems[(var4 + 1)][var2];
        p_77583_1_.addRecipe(new ItemStack(var5), new Object[] { recipePatterns[var4], Character.valueOf('#'), Items.stick, Character.valueOf('X'), var3 });
      }
    }
    
    p_77583_1_.addRecipe(new ItemStack(Items.bow, 1), new Object[] { " #X", "# X", " #X", Character.valueOf('X'), Items.string, Character.valueOf('#'), Items.stick });
    p_77583_1_.addRecipe(new ItemStack(Items.arrow, 4), new Object[] { "X", "#", "Y", Character.valueOf('Y'), Items.feather, Character.valueOf('X'), Items.flint, Character.valueOf('#'), Items.stick });
  }
}
