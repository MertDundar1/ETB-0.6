package net.minecraft.inventory;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerWorkbench extends Container
{
  public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
  public IInventory craftResult = new InventoryCraftResult();
  private World worldObj;
  private BlockPos field_178145_h;
  private static final String __OBFID = "CL_00001744";
  
  public ContainerWorkbench(InventoryPlayer p_i45800_1_, World worldIn, BlockPos p_i45800_3_)
  {
    worldObj = worldIn;
    field_178145_h = p_i45800_3_;
    addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 124, 35));
    


    for (int var4 = 0; var4 < 3; var4++)
    {
      for (int var5 = 0; var5 < 3; var5++)
      {
        addSlotToContainer(new Slot(craftMatrix, var5 + var4 * 3, 30 + var5 * 18, 17 + var4 * 18));
      }
    }
    
    for (var4 = 0; var4 < 3; var4++)
    {
      for (int var5 = 0; var5 < 9; var5++)
      {
        addSlotToContainer(new Slot(p_i45800_1_, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
      }
    }
    
    for (var4 = 0; var4 < 9; var4++)
    {
      addSlotToContainer(new Slot(p_i45800_1_, var4, 8 + var4 * 18, 142));
    }
    
    onCraftMatrixChanged(craftMatrix);
  }
  



  public void onCraftMatrixChanged(IInventory p_75130_1_)
  {
    craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj));
  }
  



  public void onContainerClosed(EntityPlayer p_75134_1_)
  {
    super.onContainerClosed(p_75134_1_);
    
    if (!worldObj.isRemote)
    {
      for (int var2 = 0; var2 < 9; var2++)
      {
        ItemStack var3 = craftMatrix.getStackInSlotOnClosing(var2);
        
        if (var3 != null)
        {
          p_75134_1_.dropPlayerItemWithRandomChoice(var3, false);
        }
      }
    }
  }
  
  public boolean canInteractWith(EntityPlayer playerIn)
  {
    return worldObj.getBlockState(field_178145_h).getBlock() == net.minecraft.init.Blocks.crafting_table;
  }
  



  public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
  {
    ItemStack var3 = null;
    Slot var4 = (Slot)inventorySlots.get(index);
    
    if ((var4 != null) && (var4.getHasStack()))
    {
      ItemStack var5 = var4.getStack();
      var3 = var5.copy();
      
      if (index == 0)
      {
        if (!mergeItemStack(var5, 10, 46, true))
        {
          return null;
        }
        
        var4.onSlotChange(var5, var3);
      }
      else if ((index >= 10) && (index < 37))
      {
        if (!mergeItemStack(var5, 37, 46, false))
        {
          return null;
        }
      }
      else if ((index >= 37) && (index < 46))
      {
        if (!mergeItemStack(var5, 10, 37, false))
        {
          return null;
        }
      }
      else if (!mergeItemStack(var5, 10, 46, false))
      {
        return null;
      }
      
      if (stackSize == 0)
      {
        var4.putStack(null);
      }
      else
      {
        var4.onSlotChanged();
      }
      
      if (stackSize == stackSize)
      {
        return null;
      }
      
      var4.onPickupFromSlot(playerIn, var5);
    }
    
    return var3;
  }
  
  public boolean func_94530_a(ItemStack p_94530_1_, Slot p_94530_2_)
  {
    return (inventory != craftResult) && (super.func_94530_a(p_94530_1_, p_94530_2_));
  }
}
