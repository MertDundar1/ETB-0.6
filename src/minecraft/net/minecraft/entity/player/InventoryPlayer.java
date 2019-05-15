package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandTestForBlock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;


public class InventoryPlayer
  implements IInventory
{
  public ItemStack[] mainInventory = new ItemStack[36];
  

  public ItemStack[] armorInventory = new ItemStack[4];
  

  public int currentItem;
  

  public EntityPlayer player;
  

  private ItemStack itemStack;
  
  public boolean inventoryChanged;
  
  private static final String __OBFID = "CL_00001709";
  

  public InventoryPlayer(EntityPlayer p_i1750_1_)
  {
    player = p_i1750_1_;
  }
  



  public ItemStack getCurrentItem()
  {
    return (currentItem < 9) && (currentItem >= 0) ? mainInventory[currentItem] : null;
  }
  



  public static int getHotbarSize()
  {
    return 9;
  }
  
  private int getInventorySlotContainItem(Item itemIn)
  {
    for (int var2 = 0; var2 < mainInventory.length; var2++)
    {
      if ((mainInventory[var2] != null) && (mainInventory[var2].getItem() == itemIn))
      {
        return var2;
      }
    }
    
    return -1;
  }
  
  private int getInventorySlotContainItemAndDamage(Item p_146024_1_, int p_146024_2_)
  {
    for (int var3 = 0; var3 < mainInventory.length; var3++)
    {
      if ((mainInventory[var3] != null) && (mainInventory[var3].getItem() == p_146024_1_) && (mainInventory[var3].getMetadata() == p_146024_2_))
      {
        return var3;
      }
    }
    
    return -1;
  }
  



  private int storeItemStack(ItemStack p_70432_1_)
  {
    for (int var2 = 0; var2 < mainInventory.length; var2++)
    {
      if ((mainInventory[var2] != null) && (mainInventory[var2].getItem() == p_70432_1_.getItem()) && (mainInventory[var2].isStackable()) && (mainInventory[var2].stackSize < mainInventory[var2].getMaxStackSize()) && (mainInventory[var2].stackSize < getInventoryStackLimit()) && ((!mainInventory[var2].getHasSubtypes()) || (mainInventory[var2].getMetadata() == p_70432_1_.getMetadata())) && (ItemStack.areItemStackTagsEqual(mainInventory[var2], p_70432_1_)))
      {
        return var2;
      }
    }
    
    return -1;
  }
  



  public int getFirstEmptyStack()
  {
    for (int var1 = 0; var1 < mainInventory.length; var1++)
    {
      if (mainInventory[var1] == null)
      {
        return var1;
      }
    }
    
    return -1;
  }
  
  public void setCurrentItem(Item p_146030_1_, int p_146030_2_, boolean p_146030_3_, boolean p_146030_4_)
  {
    ItemStack var5 = getCurrentItem();
    int var6 = p_146030_3_ ? getInventorySlotContainItemAndDamage(p_146030_1_, p_146030_2_) : getInventorySlotContainItem(p_146030_1_);
    
    if ((var6 >= 0) && (var6 < 9))
    {
      currentItem = var6;
    }
    else if ((p_146030_4_) && (p_146030_1_ != null))
    {
      int var7 = getFirstEmptyStack();
      
      if ((var7 >= 0) && (var7 < 9))
      {
        currentItem = var7;
      }
      
      if ((var5 == null) || (!var5.isItemEnchantable()) || (getInventorySlotContainItemAndDamage(var5.getItem(), var5.getItemDamage()) != currentItem))
      {
        int var8 = getInventorySlotContainItemAndDamage(p_146030_1_, p_146030_2_);
        
        int var9;
        if (var8 >= 0)
        {
          int var9 = mainInventory[var8].stackSize;
          mainInventory[var8] = mainInventory[currentItem];
        }
        else
        {
          var9 = 1;
        }
        
        mainInventory[currentItem] = new ItemStack(p_146030_1_, var9, p_146030_2_);
      }
    }
  }
  



  public void changeCurrentItem(int p_70453_1_)
  {
    if (p_70453_1_ > 0)
    {
      p_70453_1_ = 1;
    }
    
    if (p_70453_1_ < 0)
    {
      p_70453_1_ = -1;
    }
    
    for (currentItem -= p_70453_1_; currentItem < 0; currentItem += 9) {}
    



    while (currentItem >= 9)
    {
      currentItem -= 9;
    }
  }
  
  public int func_174925_a(Item p_174925_1_, int p_174925_2_, int p_174925_3_, NBTTagCompound p_174925_4_)
  {
    int var5 = 0;
    



    for (int var6 = 0; var6 < mainInventory.length; var6++)
    {
      ItemStack var7 = mainInventory[var6];
      
      if ((var7 != null) && ((p_174925_1_ == null) || (var7.getItem() == p_174925_1_)) && ((p_174925_2_ <= -1) || (var7.getMetadata() == p_174925_2_)) && ((p_174925_4_ == null) || (CommandTestForBlock.func_175775_a(p_174925_4_, var7.getTagCompound(), true))))
      {
        int var8 = p_174925_3_ <= 0 ? stackSize : Math.min(p_174925_3_ - var5, stackSize);
        var5 += var8;
        
        if (p_174925_3_ != 0)
        {
          mainInventory[var6].stackSize -= var8;
          
          if (mainInventory[var6].stackSize == 0)
          {
            mainInventory[var6] = null;
          }
          
          if ((p_174925_3_ > 0) && (var5 >= p_174925_3_))
          {
            return var5;
          }
        }
      }
    }
    
    for (var6 = 0; var6 < armorInventory.length; var6++)
    {
      ItemStack var7 = armorInventory[var6];
      
      if ((var7 != null) && ((p_174925_1_ == null) || (var7.getItem() == p_174925_1_)) && ((p_174925_2_ <= -1) || (var7.getMetadata() == p_174925_2_)) && ((p_174925_4_ == null) || (CommandTestForBlock.func_175775_a(p_174925_4_, var7.getTagCompound(), false))))
      {
        int var8 = p_174925_3_ <= 0 ? stackSize : Math.min(p_174925_3_ - var5, stackSize);
        var5 += var8;
        
        if (p_174925_3_ != 0)
        {
          armorInventory[var6].stackSize -= var8;
          
          if (armorInventory[var6].stackSize == 0)
          {
            armorInventory[var6] = null;
          }
          
          if ((p_174925_3_ > 0) && (var5 >= p_174925_3_))
          {
            return var5;
          }
        }
      }
    }
    
    if (itemStack != null)
    {
      if ((p_174925_1_ != null) && (itemStack.getItem() != p_174925_1_))
      {
        return var5;
      }
      
      if ((p_174925_2_ > -1) && (itemStack.getMetadata() != p_174925_2_))
      {
        return var5;
      }
      
      if ((p_174925_4_ != null) && (!CommandTestForBlock.func_175775_a(p_174925_4_, itemStack.getTagCompound(), false)))
      {
        return var5;
      }
      
      var6 = p_174925_3_ <= 0 ? itemStack.stackSize : Math.min(p_174925_3_ - var5, itemStack.stackSize);
      var5 += var6;
      
      if (p_174925_3_ != 0)
      {
        itemStack.stackSize -= var6;
        
        if (itemStack.stackSize == 0)
        {
          itemStack = null;
        }
        
        if ((p_174925_3_ > 0) && (var5 >= p_174925_3_))
        {
          return var5;
        }
      }
    }
    
    return var5;
  }
  




  private int storePartialItemStack(ItemStack p_70452_1_)
  {
    Item var2 = p_70452_1_.getItem();
    int var3 = stackSize;
    int var4 = storeItemStack(p_70452_1_);
    
    if (var4 < 0)
    {
      var4 = getFirstEmptyStack();
    }
    
    if (var4 < 0)
    {
      return var3;
    }
    

    if (mainInventory[var4] == null)
    {
      mainInventory[var4] = new ItemStack(var2, 0, p_70452_1_.getMetadata());
      
      if (p_70452_1_.hasTagCompound())
      {
        mainInventory[var4].setTagCompound((NBTTagCompound)p_70452_1_.getTagCompound().copy());
      }
    }
    
    int var5 = var3;
    
    if (var3 > mainInventory[var4].getMaxStackSize() - mainInventory[var4].stackSize)
    {
      var5 = mainInventory[var4].getMaxStackSize() - mainInventory[var4].stackSize;
    }
    
    if (var5 > getInventoryStackLimit() - mainInventory[var4].stackSize)
    {
      var5 = getInventoryStackLimit() - mainInventory[var4].stackSize;
    }
    
    if (var5 == 0)
    {
      return var3;
    }
    

    var3 -= var5;
    mainInventory[var4].stackSize += var5;
    mainInventory[var4].animationsToGo = 5;
    return var3;
  }
  






  public void decrementAnimations()
  {
    for (int var1 = 0; var1 < mainInventory.length; var1++)
    {
      if (mainInventory[var1] != null)
      {
        mainInventory[var1].updateAnimation(player.worldObj, player, var1, currentItem == var1);
      }
    }
  }
  



  public boolean consumeInventoryItem(Item p_146026_1_)
  {
    int var2 = getInventorySlotContainItem(p_146026_1_);
    
    if (var2 < 0)
    {
      return false;
    }
    

    if (--mainInventory[var2].stackSize <= 0)
    {
      mainInventory[var2] = null;
    }
    
    return true;
  }
  




  public boolean hasItem(Item p_146028_1_)
  {
    int var2 = getInventorySlotContainItem(p_146028_1_);
    return var2 >= 0;
  }
  



  public boolean addItemStackToInventory(final ItemStack p_70441_1_)
  {
    if ((p_70441_1_ != null) && (stackSize != 0) && (p_70441_1_.getItem() != null))
    {

      try
      {

        if (p_70441_1_.isItemDamaged())
        {
          int var2 = getFirstEmptyStack();
          
          if (var2 >= 0)
          {
            mainInventory[var2] = ItemStack.copyItemStack(p_70441_1_);
            mainInventory[var2].animationsToGo = 5;
            stackSize = 0;
            return true;
          }
          if (player.capabilities.isCreativeMode)
          {
            stackSize = 0;
            return true;
          }
          

          return false;
        }
        
        int var2;
        
        do
        {
          var2 = stackSize;
          stackSize = storePartialItemStack(p_70441_1_);
        }
        while ((stackSize > 0) && (stackSize < var2));
        
        if ((stackSize == var2) && (player.capabilities.isCreativeMode))
        {
          stackSize = 0;
          return true;
        }
        

        return stackSize < var2;

      }
      catch (Throwable var5)
      {

        CrashReport var3 = CrashReport.makeCrashReport(var5, "Adding item to inventory");
        CrashReportCategory var4 = var3.makeCategory("Item being added");
        var4.addCrashSection("Item ID", Integer.valueOf(Item.getIdFromItem(p_70441_1_.getItem())));
        var4.addCrashSection("Item data", Integer.valueOf(p_70441_1_.getMetadata()));
        var4.addCrashSectionCallable("Item name", new Callable()
        {
          private static final String __OBFID = "CL_00001710";
          
          public String call() {
            return p_70441_1_.getDisplayName();
          }
        });
        throw new ReportedException(var3);
      }
    }
    

    return false;
  }
  





  public ItemStack decrStackSize(int index, int count)
  {
    ItemStack[] var3 = mainInventory;
    
    if (index >= mainInventory.length)
    {
      var3 = armorInventory;
      index -= mainInventory.length;
    }
    
    if (var3[index] != null)
    {


      if (stackSize <= count)
      {
        ItemStack var4 = var3[index];
        var3[index] = null;
        return var4;
      }
      

      ItemStack var4 = var3[index].splitStack(count);
      
      if (stackSize == 0)
      {
        var3[index] = null;
      }
      
      return var4;
    }
    


    return null;
  }
  





  public ItemStack getStackInSlotOnClosing(int index)
  {
    ItemStack[] var2 = mainInventory;
    
    if (index >= mainInventory.length)
    {
      var2 = armorInventory;
      index -= mainInventory.length;
    }
    
    if (var2[index] != null)
    {
      ItemStack var3 = var2[index];
      var2[index] = null;
      return var3;
    }
    

    return null;
  }
  




  public void setInventorySlotContents(int index, ItemStack stack)
  {
    ItemStack[] var3 = mainInventory;
    
    if (index >= var3.length)
    {
      index -= var3.length;
      var3 = armorInventory;
    }
    
    var3[index] = stack;
  }
  
  public float getStrVsBlock(Block p_146023_1_)
  {
    float var2 = 1.0F;
    
    if (mainInventory[currentItem] != null)
    {
      var2 *= mainInventory[currentItem].getStrVsBlock(p_146023_1_);
    }
    
    return var2;
  }
  







  public NBTTagList writeToNBT(NBTTagList p_70442_1_)
  {
    for (int var2 = 0; var2 < mainInventory.length; var2++)
    {
      if (mainInventory[var2] != null)
      {
        NBTTagCompound var3 = new NBTTagCompound();
        var3.setByte("Slot", (byte)var2);
        mainInventory[var2].writeToNBT(var3);
        p_70442_1_.appendTag(var3);
      }
    }
    
    for (var2 = 0; var2 < armorInventory.length; var2++)
    {
      if (armorInventory[var2] != null)
      {
        NBTTagCompound var3 = new NBTTagCompound();
        var3.setByte("Slot", (byte)(var2 + 100));
        armorInventory[var2].writeToNBT(var3);
        p_70442_1_.appendTag(var3);
      }
    }
    
    return p_70442_1_;
  }
  



  public void readFromNBT(NBTTagList p_70443_1_)
  {
    mainInventory = new ItemStack[36];
    armorInventory = new ItemStack[4];
    
    for (int var2 = 0; var2 < p_70443_1_.tagCount(); var2++)
    {
      NBTTagCompound var3 = p_70443_1_.getCompoundTagAt(var2);
      int var4 = var3.getByte("Slot") & 0xFF;
      ItemStack var5 = ItemStack.loadItemStackFromNBT(var3);
      
      if (var5 != null)
      {
        if ((var4 >= 0) && (var4 < mainInventory.length))
        {
          mainInventory[var4] = var5;
        }
        
        if ((var4 >= 100) && (var4 < armorInventory.length + 100))
        {
          armorInventory[(var4 - 100)] = var5;
        }
      }
    }
  }
  



  public int getSizeInventory()
  {
    return mainInventory.length + 4;
  }
  



  public ItemStack getStackInSlot(int slotIn)
  {
    ItemStack[] var2 = mainInventory;
    
    if (slotIn >= var2.length)
    {
      slotIn -= var2.length;
      var2 = armorInventory;
    }
    
    return var2[slotIn];
  }
  



  public String getName()
  {
    return "container.inventory";
  }
  



  public boolean hasCustomName()
  {
    return false;
  }
  
  public IChatComponent getDisplayName()
  {
    return hasCustomName() ? new ChatComponentText(getName()) : new ChatComponentTranslation(getName(), new Object[0]);
  }
  




  public int getInventoryStackLimit()
  {
    return 64;
  }
  
  public boolean func_146025_b(Block p_146025_1_)
  {
    if (p_146025_1_.getMaterial().isToolNotRequired())
    {
      return true;
    }
    

    ItemStack var2 = getStackInSlot(currentItem);
    return var2 != null ? var2.canHarvestBlock(p_146025_1_) : false;
  }
  




  public ItemStack armorItemInSlot(int p_70440_1_)
  {
    return armorInventory[p_70440_1_];
  }
  



  public int getTotalArmorValue()
  {
    int var1 = 0;
    
    for (int var2 = 0; var2 < armorInventory.length; var2++)
    {
      if ((armorInventory[var2] != null) && ((armorInventory[var2].getItem() instanceof ItemArmor)))
      {
        int var3 = armorInventory[var2].getItem()).damageReduceAmount;
        var1 += var3;
      }
    }
    
    return var1;
  }
  



  public void damageArmor(float p_70449_1_)
  {
    p_70449_1_ /= 4.0F;
    
    if (p_70449_1_ < 1.0F)
    {
      p_70449_1_ = 1.0F;
    }
    
    for (int var2 = 0; var2 < armorInventory.length; var2++)
    {
      if ((armorInventory[var2] != null) && ((armorInventory[var2].getItem() instanceof ItemArmor)))
      {
        armorInventory[var2].damageItem((int)p_70449_1_, player);
        
        if (armorInventory[var2].stackSize == 0)
        {
          armorInventory[var2] = null;
        }
      }
    }
  }
  





  public void dropAllItems()
  {
    for (int var1 = 0; var1 < mainInventory.length; var1++)
    {
      if (mainInventory[var1] != null)
      {
        player.func_146097_a(mainInventory[var1], true, false);
        mainInventory[var1] = null;
      }
    }
    
    for (var1 = 0; var1 < armorInventory.length; var1++)
    {
      if (armorInventory[var1] != null)
      {
        player.func_146097_a(armorInventory[var1], true, false);
        armorInventory[var1] = null;
      }
    }
  }
  




  public void markDirty()
  {
    inventoryChanged = true;
  }
  



  public void setItemStack(ItemStack p_70437_1_)
  {
    itemStack = p_70437_1_;
  }
  



  public ItemStack getItemStack()
  {
    return itemStack;
  }
  



  public boolean isUseableByPlayer(EntityPlayer playerIn)
  {
    return !player.isDead;
  }
  





  public boolean hasItemStack(ItemStack p_70431_1_)
  {
    for (int var2 = 0; var2 < armorInventory.length; var2++)
    {
      if ((armorInventory[var2] != null) && (armorInventory[var2].isItemEqual(p_70431_1_)))
      {
        return true;
      }
    }
    
    for (var2 = 0; var2 < mainInventory.length; var2++)
    {
      if ((mainInventory[var2] != null) && (mainInventory[var2].isItemEqual(p_70431_1_)))
      {
        return true;
      }
    }
    
    return false;
  }
  

  public void openInventory(EntityPlayer playerIn) {}
  

  public void closeInventory(EntityPlayer playerIn) {}
  

  public boolean isItemValidForSlot(int index, ItemStack stack)
  {
    return true;
  }
  





  public void copyInventory(InventoryPlayer p_70455_1_)
  {
    for (int var2 = 0; var2 < mainInventory.length; var2++)
    {
      mainInventory[var2] = ItemStack.copyItemStack(mainInventory[var2]);
    }
    
    for (var2 = 0; var2 < armorInventory.length; var2++)
    {
      armorInventory[var2] = ItemStack.copyItemStack(armorInventory[var2]);
    }
    
    currentItem = currentItem;
  }
  
  public int getField(int id)
  {
    return 0;
  }
  
  public void setField(int id, int value) {}
  
  public int getFieldCount()
  {
    return 0;
  }
  


  public void clearInventory()
  {
    for (int var1 = 0; var1 < mainInventory.length; var1++)
    {
      mainInventory[var1] = null;
    }
    
    for (var1 = 0; var1 < armorInventory.length; var1++)
    {
      armorInventory[var1] = null;
    }
  }
}
