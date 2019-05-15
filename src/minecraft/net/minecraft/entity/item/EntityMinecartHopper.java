package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper
{
  private boolean isBlocked = true;
  private int transferTicker = -1;
  private BlockPos field_174900_c;
  private static final String __OBFID = "CL_00001676";
  
  public EntityMinecartHopper(World worldIn)
  {
    super(worldIn);
    field_174900_c = BlockPos.ORIGIN;
  }
  
  public EntityMinecartHopper(World worldIn, double p_i1721_2_, double p_i1721_4_, double p_i1721_6_)
  {
    super(worldIn, p_i1721_2_, p_i1721_4_, p_i1721_6_);
    field_174900_c = BlockPos.ORIGIN;
  }
  
  public EntityMinecart.EnumMinecartType func_180456_s()
  {
    return EntityMinecart.EnumMinecartType.HOPPER;
  }
  
  public IBlockState func_180457_u()
  {
    return Blocks.hopper.getDefaultState();
  }
  
  public int getDefaultDisplayTileOffset()
  {
    return 1;
  }
  



  public int getSizeInventory()
  {
    return 5;
  }
  



  public boolean interactFirst(EntityPlayer playerIn)
  {
    if (!worldObj.isRemote)
    {
      playerIn.displayGUIChest(this);
    }
    
    return true;
  }
  



  public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_)
  {
    boolean var5 = !p_96095_4_;
    
    if (var5 != getBlocked())
    {
      setBlocked(var5);
    }
  }
  



  public boolean getBlocked()
  {
    return isBlocked;
  }
  



  public void setBlocked(boolean p_96110_1_)
  {
    isBlocked = p_96110_1_;
  }
  



  public World getWorld()
  {
    return worldObj;
  }
  



  public double getXPos()
  {
    return posX;
  }
  



  public double getYPos()
  {
    return posY;
  }
  



  public double getZPos()
  {
    return posZ;
  }
  



  public void onUpdate()
  {
    super.onUpdate();
    
    if ((!worldObj.isRemote) && (isEntityAlive()) && (getBlocked()))
    {
      BlockPos var1 = new BlockPos(this);
      
      if (var1.equals(field_174900_c))
      {
        transferTicker -= 1;
      }
      else
      {
        setTransferTicker(0);
      }
      
      if (!canTransfer())
      {
        setTransferTicker(0);
        
        if (func_96112_aD())
        {
          setTransferTicker(4);
          markDirty();
        }
      }
    }
  }
  
  public boolean func_96112_aD()
  {
    if (TileEntityHopper.func_145891_a(this))
    {
      return true;
    }
    

    List var1 = worldObj.func_175647_a(EntityItem.class, getEntityBoundingBox().expand(0.25D, 0.0D, 0.25D), IEntitySelector.selectAnything);
    
    if (var1.size() > 0)
    {
      TileEntityHopper.func_145898_a(this, (EntityItem)var1.get(0));
    }
    
    return false;
  }
  

  public void killMinecart(DamageSource p_94095_1_)
  {
    super.killMinecart(p_94095_1_);
    dropItemWithOffset(Item.getItemFromBlock(Blocks.hopper), 1, 0.0F);
  }
  



  protected void writeEntityToNBT(NBTTagCompound tagCompound)
  {
    super.writeEntityToNBT(tagCompound);
    tagCompound.setInteger("TransferCooldown", transferTicker);
  }
  



  protected void readEntityFromNBT(NBTTagCompound tagCompund)
  {
    super.readEntityFromNBT(tagCompund);
    transferTicker = tagCompund.getInteger("TransferCooldown");
  }
  



  public void setTransferTicker(int p_98042_1_)
  {
    transferTicker = p_98042_1_;
  }
  



  public boolean canTransfer()
  {
    return transferTicker > 0;
  }
  
  public String getGuiID()
  {
    return "minecraft:hopper";
  }
  
  public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
  {
    return new ContainerHopper(playerInventory, this, playerIn);
  }
}
