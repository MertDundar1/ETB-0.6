package net.minecraft.tileentity;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockable implements IHopper, IUpdatePlayerListBox
{
  private ItemStack[] inventory = new ItemStack[5];
  private String customName;
  private int transferCooldown = -1;
  private static final String __OBFID = "CL_00000359";
  
  public TileEntityHopper() {}
  
  public void readFromNBT(NBTTagCompound compound) { super.readFromNBT(compound);
    NBTTagList var2 = compound.getTagList("Items", 10);
    inventory = new ItemStack[getSizeInventory()];
    
    if (compound.hasKey("CustomName", 8))
    {
      customName = compound.getString("CustomName");
    }
    
    transferCooldown = compound.getInteger("TransferCooldown");
    
    for (int var3 = 0; var3 < var2.tagCount(); var3++)
    {
      NBTTagCompound var4 = var2.getCompoundTagAt(var3);
      byte var5 = var4.getByte("Slot");
      
      if ((var5 >= 0) && (var5 < inventory.length))
      {
        inventory[var5] = ItemStack.loadItemStackFromNBT(var4);
      }
    }
  }
  
  public void writeToNBT(NBTTagCompound compound)
  {
    super.writeToNBT(compound);
    NBTTagList var2 = new NBTTagList();
    
    for (int var3 = 0; var3 < inventory.length; var3++)
    {
      if (inventory[var3] != null)
      {
        NBTTagCompound var4 = new NBTTagCompound();
        var4.setByte("Slot", (byte)var3);
        inventory[var3].writeToNBT(var4);
        var2.appendTag(var4);
      }
    }
    
    compound.setTag("Items", var2);
    compound.setInteger("TransferCooldown", transferCooldown);
    
    if (hasCustomName())
    {
      compound.setString("CustomName", customName);
    }
  }
  




  public void markDirty()
  {
    super.markDirty();
  }
  



  public int getSizeInventory()
  {
    return inventory.length;
  }
  



  public ItemStack getStackInSlot(int slotIn)
  {
    return inventory[slotIn];
  }
  




  public ItemStack decrStackSize(int index, int count)
  {
    if (inventory[index] != null)
    {


      if (inventory[index].stackSize <= count)
      {
        ItemStack var3 = inventory[index];
        inventory[index] = null;
        return var3;
      }
      

      ItemStack var3 = inventory[index].splitStack(count);
      
      if (inventory[index].stackSize == 0)
      {
        inventory[index] = null;
      }
      
      return var3;
    }
    


    return null;
  }
  





  public ItemStack getStackInSlotOnClosing(int index)
  {
    if (inventory[index] != null)
    {
      ItemStack var2 = inventory[index];
      inventory[index] = null;
      return var2;
    }
    

    return null;
  }
  




  public void setInventorySlotContents(int index, ItemStack stack)
  {
    inventory[index] = stack;
    
    if ((stack != null) && (stackSize > getInventoryStackLimit()))
    {
      stackSize = getInventoryStackLimit();
    }
  }
  



  public String getName()
  {
    return hasCustomName() ? customName : "container.hopper";
  }
  



  public boolean hasCustomName()
  {
    return (customName != null) && (customName.length() > 0);
  }
  
  public void setCustomName(String customNameIn)
  {
    customName = customNameIn;
  }
  




  public int getInventoryStackLimit()
  {
    return 64;
  }
  



  public boolean isUseableByPlayer(EntityPlayer playerIn)
  {
    return worldObj.getTileEntity(pos) == this;
  }
  

  public void openInventory(EntityPlayer playerIn) {}
  

  public void closeInventory(EntityPlayer playerIn) {}
  

  public boolean isItemValidForSlot(int index, ItemStack stack)
  {
    return true;
  }
  



  public void update()
  {
    if ((worldObj != null) && (!worldObj.isRemote))
    {
      transferCooldown -= 1;
      
      if (!isOnTransferCooldown())
      {
        setTransferCooldown(0);
        func_145887_i();
      }
    }
  }
  
  public boolean func_145887_i()
  {
    if ((worldObj != null) && (!worldObj.isRemote))
    {
      if ((!isOnTransferCooldown()) && (BlockHopper.getActiveStateFromMetadata(getBlockMetadata())))
      {
        boolean var1 = false;
        
        if (!func_152104_k())
        {
          var1 = func_145883_k();
        }
        
        if (!func_152105_l())
        {
          var1 = (func_145891_a(this)) || (var1);
        }
        
        if (var1)
        {
          setTransferCooldown(8);
          markDirty();
          return true;
        }
      }
      
      return false;
    }
    

    return false;
  }
  

  private boolean func_152104_k()
  {
    ItemStack[] var1 = inventory;
    int var2 = var1.length;
    
    for (int var3 = 0; var3 < var2; var3++)
    {
      ItemStack var4 = var1[var3];
      
      if (var4 != null)
      {
        return false;
      }
    }
    
    return true;
  }
  
  private boolean func_152105_l()
  {
    ItemStack[] var1 = inventory;
    int var2 = var1.length;
    
    for (int var3 = 0; var3 < var2; var3++)
    {
      ItemStack var4 = var1[var3];
      
      if ((var4 == null) || (stackSize != var4.getMaxStackSize()))
      {
        return false;
      }
    }
    
    return true;
  }
  
  private boolean func_145883_k()
  {
    IInventory var1 = func_145895_l();
    
    if (var1 == null)
    {
      return false;
    }
    

    EnumFacing var2 = BlockHopper.func_176428_b(getBlockMetadata()).getOpposite();
    
    if (func_174919_a(var1, var2))
    {
      return false;
    }
    

    for (int var3 = 0; var3 < getSizeInventory(); var3++)
    {
      if (getStackInSlot(var3) != null)
      {
        ItemStack var4 = getStackInSlot(var3).copy();
        ItemStack var5 = func_174918_a(var1, decrStackSize(var3, 1), var2);
        
        if ((var5 == null) || (stackSize == 0))
        {
          var1.markDirty();
          return true;
        }
        
        setInventorySlotContents(var3, var4);
      }
    }
    
    return false;
  }
  


  private boolean func_174919_a(IInventory p_174919_1_, EnumFacing p_174919_2_)
  {
    if ((p_174919_1_ instanceof ISidedInventory))
    {
      ISidedInventory var3 = (ISidedInventory)p_174919_1_;
      int[] var4 = var3.getSlotsForFace(p_174919_2_);
      
      for (int var5 = 0; var5 < var4.length; var5++)
      {
        ItemStack var6 = var3.getStackInSlot(var4[var5]);
        
        if ((var6 == null) || (stackSize != var6.getMaxStackSize()))
        {
          return false;
        }
      }
    }
    else
    {
      int var7 = p_174919_1_.getSizeInventory();
      
      for (int var8 = 0; var8 < var7; var8++)
      {
        ItemStack var9 = p_174919_1_.getStackInSlot(var8);
        
        if ((var9 == null) || (stackSize != var9.getMaxStackSize()))
        {
          return false;
        }
      }
    }
    
    return true;
  }
  
  private static boolean func_174917_b(IInventory p_174917_0_, EnumFacing p_174917_1_)
  {
    if ((p_174917_0_ instanceof ISidedInventory))
    {
      ISidedInventory var2 = (ISidedInventory)p_174917_0_;
      int[] var3 = var2.getSlotsForFace(p_174917_1_);
      
      for (int var4 = 0; var4 < var3.length; var4++)
      {
        if (var2.getStackInSlot(var3[var4]) != null)
        {
          return false;
        }
      }
    }
    else
    {
      int var5 = p_174917_0_.getSizeInventory();
      
      for (int var6 = 0; var6 < var5; var6++)
      {
        if (p_174917_0_.getStackInSlot(var6) != null)
        {
          return false;
        }
      }
    }
    
    return true;
  }
  
  public static boolean func_145891_a(IHopper p_145891_0_)
  {
    IInventory var1 = func_145884_b(p_145891_0_);
    
    if (var1 != null)
    {
      EnumFacing var2 = EnumFacing.DOWN;
      
      if (func_174917_b(var1, var2))
      {
        return false;
      }
      
      if ((var1 instanceof ISidedInventory))
      {
        ISidedInventory var3 = (ISidedInventory)var1;
        int[] var4 = var3.getSlotsForFace(var2);
        
        for (int var5 = 0; var5 < var4.length; var5++)
        {
          if (func_174915_a(p_145891_0_, var1, var4[var5], var2))
          {
            return true;
          }
        }
      }
      else
      {
        int var7 = var1.getSizeInventory();
        
        for (int var8 = 0; var8 < var7; var8++)
        {
          if (func_174915_a(p_145891_0_, var1, var8, var2))
          {
            return true;
          }
        }
      }
    }
    else
    {
      EntityItem var6 = func_145897_a(p_145891_0_.getWorld(), p_145891_0_.getXPos(), p_145891_0_.getYPos() + 1.0D, p_145891_0_.getZPos());
      
      if (var6 != null)
      {
        return func_145898_a(p_145891_0_, var6);
      }
    }
    
    return false;
  }
  
  private static boolean func_174915_a(IHopper p_174915_0_, IInventory p_174915_1_, int p_174915_2_, EnumFacing p_174915_3_)
  {
    ItemStack var4 = p_174915_1_.getStackInSlot(p_174915_2_);
    
    if ((var4 != null) && (func_174921_b(p_174915_1_, var4, p_174915_2_, p_174915_3_)))
    {
      ItemStack var5 = var4.copy();
      ItemStack var6 = func_174918_a(p_174915_0_, p_174915_1_.decrStackSize(p_174915_2_, 1), null);
      
      if ((var6 == null) || (stackSize == 0))
      {
        p_174915_1_.markDirty();
        return true;
      }
      
      p_174915_1_.setInventorySlotContents(p_174915_2_, var5);
    }
    
    return false;
  }
  
  public static boolean func_145898_a(IInventory p_145898_0_, EntityItem p_145898_1_)
  {
    boolean var2 = false;
    
    if (p_145898_1_ == null)
    {
      return false;
    }
    

    ItemStack var3 = p_145898_1_.getEntityItem().copy();
    ItemStack var4 = func_174918_a(p_145898_0_, var3, null);
    
    if ((var4 != null) && (stackSize != 0))
    {
      p_145898_1_.setEntityItemStack(var4);
    }
    else
    {
      var2 = true;
      p_145898_1_.setDead();
    }
    
    return var2;
  }
  

  public static ItemStack func_174918_a(IInventory p_174918_0_, ItemStack p_174918_1_, EnumFacing p_174918_2_)
  {
    if (((p_174918_0_ instanceof ISidedInventory)) && (p_174918_2_ != null))
    {
      ISidedInventory var6 = (ISidedInventory)p_174918_0_;
      int[] var7 = var6.getSlotsForFace(p_174918_2_);
      
      int var5 = 0;
      do {
        p_174918_1_ = func_174916_c(p_174918_0_, p_174918_1_, var7[var5], p_174918_2_);var5++;
        if ((var5 >= var7.length) || (p_174918_1_ == null)) break; } while (stackSize > 0);


    }
    else
    {

      int var3 = p_174918_0_.getSizeInventory();
      
      for (int var4 = 0; (var4 < var3) && (p_174918_1_ != null) && (stackSize > 0); var4++)
      {
        p_174918_1_ = func_174916_c(p_174918_0_, p_174918_1_, var4, p_174918_2_);
      }
    }
    
    if ((p_174918_1_ != null) && (stackSize == 0))
    {
      p_174918_1_ = null;
    }
    
    return p_174918_1_;
  }
  
  private static boolean func_174920_a(IInventory p_174920_0_, ItemStack p_174920_1_, int p_174920_2_, EnumFacing p_174920_3_)
  {
    return p_174920_0_.isItemValidForSlot(p_174920_2_, p_174920_1_);
  }
  
  private static boolean func_174921_b(IInventory p_174921_0_, ItemStack p_174921_1_, int p_174921_2_, EnumFacing p_174921_3_)
  {
    return (!(p_174921_0_ instanceof ISidedInventory)) || (((ISidedInventory)p_174921_0_).canExtractItem(p_174921_2_, p_174921_1_, p_174921_3_));
  }
  
  private static ItemStack func_174916_c(IInventory p_174916_0_, ItemStack p_174916_1_, int p_174916_2_, EnumFacing p_174916_3_)
  {
    ItemStack var4 = p_174916_0_.getStackInSlot(p_174916_2_);
    
    if (func_174920_a(p_174916_0_, p_174916_1_, p_174916_2_, p_174916_3_))
    {
      boolean var5 = false;
      
      if (var4 == null)
      {
        p_174916_0_.setInventorySlotContents(p_174916_2_, p_174916_1_);
        p_174916_1_ = null;
        var5 = true;
      }
      else if (canCombine(var4, p_174916_1_))
      {
        int var6 = p_174916_1_.getMaxStackSize() - stackSize;
        int var7 = Math.min(stackSize, var6);
        stackSize -= var7;
        stackSize += var7;
        var5 = var7 > 0;
      }
      
      if (var5)
      {
        if ((p_174916_0_ instanceof TileEntityHopper))
        {
          TileEntityHopper var8 = (TileEntityHopper)p_174916_0_;
          
          if (var8.mayTransfer())
          {
            var8.setTransferCooldown(8);
          }
          
          p_174916_0_.markDirty();
        }
        
        p_174916_0_.markDirty();
      }
    }
    
    return p_174916_1_;
  }
  
  private IInventory func_145895_l()
  {
    EnumFacing var1 = BlockHopper.func_176428_b(getBlockMetadata());
    return func_145893_b(getWorld(), pos.getX() + var1.getFrontOffsetX(), pos.getY() + var1.getFrontOffsetY(), pos.getZ() + var1.getFrontOffsetZ());
  }
  
  public static IInventory func_145884_b(IHopper p_145884_0_)
  {
    return func_145893_b(p_145884_0_.getWorld(), p_145884_0_.getXPos(), p_145884_0_.getYPos() + 1.0D, p_145884_0_.getZPos());
  }
  
  public static EntityItem func_145897_a(World worldIn, double p_145897_1_, double p_145897_3_, double p_145897_5_)
  {
    List var7 = worldIn.func_175647_a(EntityItem.class, new AxisAlignedBB(p_145897_1_, p_145897_3_, p_145897_5_, p_145897_1_ + 1.0D, p_145897_3_ + 1.0D, p_145897_5_ + 1.0D), IEntitySelector.selectAnything);
    return var7.size() > 0 ? (EntityItem)var7.get(0) : null;
  }
  
  public static IInventory func_145893_b(World worldIn, double p_145893_1_, double p_145893_3_, double p_145893_5_)
  {
    Object var7 = null;
    int var8 = MathHelper.floor_double(p_145893_1_);
    int var9 = MathHelper.floor_double(p_145893_3_);
    int var10 = MathHelper.floor_double(p_145893_5_);
    BlockPos var11 = new BlockPos(var8, var9, var10);
    TileEntity var12 = worldIn.getTileEntity(new BlockPos(var8, var9, var10));
    
    if ((var12 instanceof IInventory))
    {
      var7 = (IInventory)var12;
      
      if ((var7 instanceof TileEntityChest))
      {
        Block var13 = worldIn.getBlockState(new BlockPos(var8, var9, var10)).getBlock();
        
        if ((var13 instanceof BlockChest))
        {
          var7 = ((BlockChest)var13).getLockableContainer(worldIn, var11);
        }
      }
    }
    
    if (var7 == null)
    {
      List var14 = worldIn.func_175674_a(null, new AxisAlignedBB(p_145893_1_, p_145893_3_, p_145893_5_, p_145893_1_ + 1.0D, p_145893_3_ + 1.0D, p_145893_5_ + 1.0D), IEntitySelector.selectInventories);
      
      if (var14.size() > 0)
      {
        var7 = (IInventory)var14.get(rand.nextInt(var14.size()));
      }
    }
    
    return (IInventory)var7;
  }
  
  private static boolean canCombine(ItemStack stack1, ItemStack stack2)
  {
    return stackSize > stack1.getMaxStackSize() ? false : stack1.getMetadata() != stack2.getMetadata() ? false : stack1.getItem() != stack2.getItem() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2);
  }
  



  public double getXPos()
  {
    return pos.getX();
  }
  



  public double getYPos()
  {
    return pos.getY();
  }
  



  public double getZPos()
  {
    return pos.getZ();
  }
  
  public void setTransferCooldown(int ticks)
  {
    transferCooldown = ticks;
  }
  
  public boolean isOnTransferCooldown()
  {
    return transferCooldown > 0;
  }
  
  public boolean mayTransfer()
  {
    return transferCooldown <= 1;
  }
  
  public String getGuiID()
  {
    return "minecraft:hopper";
  }
  
  public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
  {
    return new net.minecraft.inventory.ContainerHopper(playerInventory, this, playerIn);
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
    for (int var1 = 0; var1 < inventory.length; var1++)
    {
      inventory[var1] = null;
    }
  }
}
