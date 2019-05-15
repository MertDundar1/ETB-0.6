package net.minecraft.inventory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class ContainerRepair extends Container
{
  private static final org.apache.logging.log4j.Logger logger = ;
  

  private IInventory outputSlot;
  

  private IInventory inputSlots;
  
  private World theWorld;
  
  private BlockPos field_178156_j;
  
  public int maximumCost;
  
  private int materialCost;
  
  private String repairedItemName;
  
  private final EntityPlayer thePlayer;
  
  private static final String __OBFID = "CL_00001732";
  

  public ContainerRepair(InventoryPlayer p_i45806_1_, World worldIn, EntityPlayer p_i45806_3_)
  {
    this(p_i45806_1_, worldIn, BlockPos.ORIGIN, p_i45806_3_);
  }
  
  public ContainerRepair(InventoryPlayer p_i45807_1_, final World worldIn, final BlockPos p_i45807_3_, EntityPlayer p_i45807_4_)
  {
    outputSlot = new InventoryCraftResult();
    inputSlots = new InventoryBasic("Repair", true, 2)
    {
      private static final String __OBFID = "CL_00001733";
      
      public void markDirty() {
        super.markDirty();
        onCraftMatrixChanged(this);
      }
    };
    field_178156_j = p_i45807_3_;
    theWorld = worldIn;
    thePlayer = p_i45807_4_;
    addSlotToContainer(new Slot(inputSlots, 0, 27, 47));
    addSlotToContainer(new Slot(inputSlots, 1, 76, 47));
    addSlotToContainer(new Slot(outputSlot, 2, 134, 47)
    {
      private static final String __OBFID = "CL_00001734";
      
      public boolean isItemValid(ItemStack stack) {
        return false;
      }
      
      public boolean canTakeStack(EntityPlayer p_82869_1_) {
        return ((capabilities.isCreativeMode) || (experienceLevel >= maximumCost)) && (maximumCost > 0) && (getHasStack());
      }
      
      public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
        if (!capabilities.isCreativeMode)
        {
          playerIn.addExperienceLevel(-maximumCost);
        }
        
        inputSlots.setInventorySlotContents(0, null);
        
        if (materialCost > 0)
        {
          ItemStack var3 = inputSlots.getStackInSlot(1);
          
          if ((var3 != null) && (stackSize > materialCost))
          {
            stackSize -= materialCost;
            inputSlots.setInventorySlotContents(1, var3);
          }
          else
          {
            inputSlots.setInventorySlotContents(1, null);
          }
        }
        else
        {
          inputSlots.setInventorySlotContents(1, null);
        }
        
        maximumCost = 0;
        IBlockState var5 = worldIn.getBlockState(p_i45807_3_);
        
        if ((!capabilities.isCreativeMode) && (!worldInisRemote) && (var5.getBlock() == Blocks.anvil) && (playerIn.getRNG().nextFloat() < 0.12F))
        {
          int var4 = ((Integer)var5.getValue(BlockAnvil.DAMAGE)).intValue();
          var4++;
          
          if (var4 > 2)
          {
            worldIn.setBlockToAir(p_i45807_3_);
            worldIn.playAuxSFX(1020, p_i45807_3_, 0);
          }
          else
          {
            worldIn.setBlockState(p_i45807_3_, var5.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(var4)), 2);
            worldIn.playAuxSFX(1021, p_i45807_3_, 0);
          }
        }
        else if (!worldInisRemote)
        {
          worldIn.playAuxSFX(1021, p_i45807_3_, 0);
        }
      }
    });
    

    for (int var5 = 0; var5 < 3; var5++)
    {
      for (int var6 = 0; var6 < 9; var6++)
      {
        addSlotToContainer(new Slot(p_i45807_1_, var6 + var5 * 9 + 9, 8 + var6 * 18, 84 + var5 * 18));
      }
    }
    
    for (var5 = 0; var5 < 9; var5++)
    {
      addSlotToContainer(new Slot(p_i45807_1_, var5, 8 + var5 * 18, 142));
    }
  }
  



  public void onCraftMatrixChanged(IInventory p_75130_1_)
  {
    super.onCraftMatrixChanged(p_75130_1_);
    
    if (p_75130_1_ == inputSlots)
    {
      updateRepairOutput();
    }
  }
  



  public void updateRepairOutput()
  {
    boolean var1 = false;
    boolean var2 = true;
    boolean var3 = true;
    boolean var4 = true;
    boolean var5 = true;
    boolean var6 = true;
    boolean var7 = true;
    ItemStack var8 = inputSlots.getStackInSlot(0);
    maximumCost = 1;
    int var9 = 0;
    byte var10 = 0;
    byte var11 = 0;
    
    if (var8 == null)
    {
      outputSlot.setInventorySlotContents(0, null);
      maximumCost = 0;
    }
    else
    {
      ItemStack var12 = var8.copy();
      ItemStack var13 = inputSlots.getStackInSlot(1);
      Map var14 = EnchantmentHelper.getEnchantments(var12);
      boolean var15 = false;
      int var25 = var10 + var8.getRepairCost() + (var13 == null ? 0 : var13.getRepairCost());
      materialCost = 0;
      

      if (var13 != null)
      {
        var15 = (var13.getItem() == Items.enchanted_book) && (Items.enchanted_book.func_92110_g(var13).tagCount() > 0);
        


        if ((var12.isItemStackDamageable()) && (var12.getItem().getIsRepairable(var8, var13)))
        {
          int var16 = Math.min(var12.getItemDamage(), var12.getMaxDamage() / 4);
          
          if (var16 <= 0)
          {
            outputSlot.setInventorySlotContents(0, null);
            maximumCost = 0;
            return;
          }
          
          for (int var17 = 0; (var16 > 0) && (var17 < stackSize); var17++)
          {
            int var18 = var12.getItemDamage() - var16;
            var12.setItemDamage(var18);
            var9++;
            var16 = Math.min(var12.getItemDamage(), var12.getMaxDamage() / 4);
          }
          
          materialCost = var17;
        }
        else
        {
          if ((!var15) && ((var12.getItem() != var13.getItem()) || (!var12.isItemStackDamageable())))
          {
            outputSlot.setInventorySlotContents(0, null);
            maximumCost = 0;
            return;
          }
          


          if ((var12.isItemStackDamageable()) && (!var15))
          {
            int var16 = var8.getMaxDamage() - var8.getItemDamage();
            int var17 = var13.getMaxDamage() - var13.getItemDamage();
            int var18 = var17 + var12.getMaxDamage() * 12 / 100;
            int var19 = var16 + var18;
            int var20 = var12.getMaxDamage() - var19;
            
            if (var20 < 0)
            {
              var20 = 0;
            }
            
            if (var20 < var12.getMetadata())
            {
              var12.setItemDamage(var20);
              var9 += 2;
            }
          }
          
          Map var26 = EnchantmentHelper.getEnchantments(var13);
          Iterator var27 = var26.keySet().iterator();
          
          while (var27.hasNext())
          {
            int var18 = ((Integer)var27.next()).intValue();
            Enchantment var28 = Enchantment.func_180306_c(var18);
            
            if (var28 != null)
            {
              int var20 = var14.containsKey(Integer.valueOf(var18)) ? ((Integer)var14.get(Integer.valueOf(var18))).intValue() : 0;
              int var21 = ((Integer)var26.get(Integer.valueOf(var18))).intValue();
              int var10000;
              int var10000;
              if (var20 == var21)
              {
                var21++;
                var10000 = var21;
              }
              else
              {
                var10000 = Math.max(var21, var20);
              }
              
              var21 = var10000;
              boolean var22 = var28.canApply(var8);
              
              if ((thePlayer.capabilities.isCreativeMode) || (var8.getItem() == Items.enchanted_book))
              {
                var22 = true;
              }
              
              Iterator var23 = var14.keySet().iterator();
              
              while (var23.hasNext())
              {
                int var24 = ((Integer)var23.next()).intValue();
                
                if ((var24 != var18) && (!var28.canApplyTogether(Enchantment.func_180306_c(var24))))
                {
                  var22 = false;
                  var9++;
                }
              }
              
              if (var22)
              {
                if (var21 > var28.getMaxLevel())
                {
                  var21 = var28.getMaxLevel();
                }
                
                var14.put(Integer.valueOf(var18), Integer.valueOf(var21));
                int var29 = 0;
                
                switch (var28.getWeight())
                {
                case 1: 
                  var29 = 8;
                  break;
                
                case 2: 
                  var29 = 4;
                
                case 3: 
                case 4: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                default: 
                  break;
                
                case 5: 
                  var29 = 2;
                  break;
                
                case 10: 
                  var29 = 1;
                }
                
                if (var15)
                {
                  var29 = Math.max(1, var29 / 2);
                }
                
                var9 += var29 * var21;
              }
            }
          }
        }
      }
      
      if (StringUtils.isBlank(repairedItemName))
      {
        if (var8.hasDisplayName())
        {
          var11 = 1;
          var9 += var11;
          var12.clearCustomName();
        }
      }
      else if (!repairedItemName.equals(var8.getDisplayName()))
      {
        var11 = 1;
        var9 += var11;
        var12.setStackDisplayName(repairedItemName);
      }
      
      maximumCost = (var25 + var9);
      
      if (var9 <= 0)
      {
        var12 = null;
      }
      
      if ((var11 == var9) && (var11 > 0) && (maximumCost >= 40))
      {
        maximumCost = 39;
      }
      
      if ((maximumCost >= 40) && (!thePlayer.capabilities.isCreativeMode))
      {
        var12 = null;
      }
      
      if (var12 != null)
      {
        int var16 = var12.getRepairCost();
        
        if ((var13 != null) && (var16 < var13.getRepairCost()))
        {
          var16 = var13.getRepairCost();
        }
        
        var16 = var16 * 2 + 1;
        var12.setRepairCost(var16);
        EnchantmentHelper.setEnchantments(var14, var12);
      }
      
      outputSlot.setInventorySlotContents(0, var12);
      detectAndSendChanges();
    }
  }
  
  public void onCraftGuiOpened(ICrafting p_75132_1_)
  {
    super.onCraftGuiOpened(p_75132_1_);
    p_75132_1_.sendProgressBarUpdate(this, 0, maximumCost);
  }
  
  public void updateProgressBar(int p_75137_1_, int p_75137_2_)
  {
    if (p_75137_1_ == 0)
    {
      maximumCost = p_75137_2_;
    }
  }
  



  public void onContainerClosed(EntityPlayer p_75134_1_)
  {
    super.onContainerClosed(p_75134_1_);
    
    if (!theWorld.isRemote)
    {
      for (int var2 = 0; var2 < inputSlots.getSizeInventory(); var2++)
      {
        ItemStack var3 = inputSlots.getStackInSlotOnClosing(var2);
        
        if (var3 != null)
        {
          p_75134_1_.dropPlayerItemWithRandomChoice(var3, false);
        }
      }
    }
  }
  
  public boolean canInteractWith(EntityPlayer playerIn)
  {
    return theWorld.getBlockState(field_178156_j).getBlock() == Blocks.anvil;
  }
  



  public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
  {
    ItemStack var3 = null;
    Slot var4 = (Slot)inventorySlots.get(index);
    
    if ((var4 != null) && (var4.getHasStack()))
    {
      ItemStack var5 = var4.getStack();
      var3 = var5.copy();
      
      if (index == 2)
      {
        if (!mergeItemStack(var5, 3, 39, true))
        {
          return null;
        }
        
        var4.onSlotChange(var5, var3);
      }
      else if ((index != 0) && (index != 1))
      {
        if ((index >= 3) && (index < 39) && (!mergeItemStack(var5, 0, 2, false)))
        {
          return null;
        }
      }
      else if (!mergeItemStack(var5, 3, 39, false))
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
  



  public void updateItemName(String p_82850_1_)
  {
    repairedItemName = p_82850_1_;
    
    if (getSlot(2).getHasStack())
    {
      ItemStack var2 = getSlot(2).getStack();
      
      if (StringUtils.isBlank(p_82850_1_))
      {
        var2.clearCustomName();
      }
      else
      {
        var2.setStackDisplayName(repairedItemName);
      }
    }
    
    updateRepairOutput();
  }
}
