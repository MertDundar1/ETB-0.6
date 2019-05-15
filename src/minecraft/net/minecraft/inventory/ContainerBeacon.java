package net.minecraft.inventory;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;




public class ContainerBeacon
  extends Container
{
  private IInventory tileBeacon;
  private final BeaconSlot beaconSlot;
  private static final String __OBFID = "CL_00001735";
  
  public ContainerBeacon(IInventory p_i45804_1_, IInventory p_i45804_2_)
  {
    tileBeacon = p_i45804_2_;
    addSlotToContainer(this.beaconSlot = new BeaconSlot(p_i45804_2_, 0, 136, 110));
    byte var3 = 36;
    short var4 = 137;
    

    for (int var5 = 0; var5 < 3; var5++)
    {
      for (int var6 = 0; var6 < 9; var6++)
      {
        addSlotToContainer(new Slot(p_i45804_1_, var6 + var5 * 9 + 9, var3 + var6 * 18, var4 + var5 * 18));
      }
    }
    
    for (var5 = 0; var5 < 9; var5++)
    {
      addSlotToContainer(new Slot(p_i45804_1_, var5, var3 + var5 * 18, 58 + var4));
    }
  }
  
  public void onCraftGuiOpened(ICrafting p_75132_1_)
  {
    super.onCraftGuiOpened(p_75132_1_);
    p_75132_1_.func_175173_a(this, tileBeacon);
  }
  
  public void updateProgressBar(int p_75137_1_, int p_75137_2_)
  {
    tileBeacon.setField(p_75137_1_, p_75137_2_);
  }
  
  public IInventory func_180611_e()
  {
    return tileBeacon;
  }
  
  public boolean canInteractWith(EntityPlayer playerIn)
  {
    return tileBeacon.isUseableByPlayer(playerIn);
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
        if (!mergeItemStack(var5, 1, 37, true))
        {
          return null;
        }
        
        var4.onSlotChange(var5, var3);
      }
      else if ((!beaconSlot.getHasStack()) && (beaconSlot.isItemValid(var5)) && (stackSize == 1))
      {
        if (!mergeItemStack(var5, 0, 1, false))
        {
          return null;
        }
      }
      else if ((index >= 1) && (index < 28))
      {
        if (!mergeItemStack(var5, 28, 37, false))
        {
          return null;
        }
      }
      else if ((index >= 28) && (index < 37))
      {
        if (!mergeItemStack(var5, 1, 28, false))
        {
          return null;
        }
      }
      else if (!mergeItemStack(var5, 1, 37, false))
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
  
  class BeaconSlot extends Slot
  {
    private static final String __OBFID = "CL_00001736";
    
    public BeaconSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_)
    {
      super(p_i1801_3_, p_i1801_4_, p_i1801_5_);
    }
    
    public boolean isItemValid(ItemStack stack)
    {
      return stack != null;
    }
    
    public int getSlotStackLimit()
    {
      return 1;
    }
  }
}
