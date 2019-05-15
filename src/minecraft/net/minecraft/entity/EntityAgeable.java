package net.minecraft.entity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature
{
  protected int field_175504_a;
  protected int field_175502_b;
  protected int field_175503_c;
  private float field_98056_d = -1.0F;
  private float field_98057_e;
  private static final String __OBFID = "CL_00001530";
  
  public EntityAgeable(World worldIn)
  {
    super(worldIn);
  }
  


  public abstract EntityAgeable createChild(EntityAgeable paramEntityAgeable);
  

  public boolean interact(EntityPlayer p_70085_1_)
  {
    ItemStack var2 = inventory.getCurrentItem();
    
    if ((var2 != null) && (var2.getItem() == net.minecraft.init.Items.spawn_egg))
    {
      if (!worldObj.isRemote)
      {
        Class var3 = EntityList.getClassFromID(var2.getMetadata());
        
        if ((var3 != null) && (getClass() == var3))
        {
          EntityAgeable var4 = createChild(this);
          
          if (var4 != null)
          {
            var4.setGrowingAge(41536);
            var4.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
            worldObj.spawnEntityInWorld(var4);
            
            if (var2.hasDisplayName())
            {
              var4.setCustomNameTag(var2.getDisplayName());
            }
            
            if (!capabilities.isCreativeMode)
            {
              stackSize -= 1;
              
              if (stackSize <= 0)
              {
                inventory.setInventorySlotContents(inventory.currentItem, null);
              }
            }
          }
        }
      }
      
      return true;
    }
    

    return false;
  }
  

  protected void entityInit()
  {
    super.entityInit();
    dataWatcher.addObject(12, Byte.valueOf((byte)0));
  }
  





  public int getGrowingAge()
  {
    return worldObj.isRemote ? dataWatcher.getWatchableObjectByte(12) : field_175504_a;
  }
  
  public void func_175501_a(int p_175501_1_, boolean p_175501_2_)
  {
    int var3 = getGrowingAge();
    int var4 = var3;
    var3 += p_175501_1_ * 20;
    
    if (var3 > 0)
    {
      var3 = 0;
      
      if (var4 < 0)
      {
        func_175500_n();
      }
    }
    
    int var5 = var3 - var4;
    setGrowingAge(var3);
    
    if (p_175501_2_)
    {
      field_175502_b += var5;
      
      if (field_175503_c == 0)
      {
        field_175503_c = 40;
      }
    }
    
    if (getGrowingAge() == 0)
    {
      setGrowingAge(field_175502_b);
    }
  }
  




  public void addGrowth(int p_110195_1_)
  {
    func_175501_a(p_110195_1_, false);
  }
  




  public void setGrowingAge(int p_70873_1_)
  {
    dataWatcher.updateObject(12, Byte.valueOf((byte)net.minecraft.util.MathHelper.clamp_int(p_70873_1_, -1, 1)));
    field_175504_a = p_70873_1_;
    setScaleForAge(isChild());
  }
  



  public void writeEntityToNBT(NBTTagCompound tagCompound)
  {
    super.writeEntityToNBT(tagCompound);
    tagCompound.setInteger("Age", getGrowingAge());
    tagCompound.setInteger("ForcedAge", field_175502_b);
  }
  



  public void readEntityFromNBT(NBTTagCompound tagCompund)
  {
    super.readEntityFromNBT(tagCompund);
    setGrowingAge(tagCompund.getInteger("Age"));
    field_175502_b = tagCompund.getInteger("ForcedAge");
  }
  




  public void onLivingUpdate()
  {
    super.onLivingUpdate();
    
    if (worldObj.isRemote)
    {
      if (field_175503_c > 0)
      {
        if (field_175503_c % 4 == 0)
        {
          worldObj.spawnParticle(net.minecraft.util.EnumParticleTypes.VILLAGER_HAPPY, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, 0.0D, 0.0D, 0.0D, new int[0]);
        }
        
        field_175503_c -= 1;
      }
      
      setScaleForAge(isChild());
    }
    else
    {
      int var1 = getGrowingAge();
      
      if (var1 < 0)
      {
        var1++;
        setGrowingAge(var1);
        
        if (var1 == 0)
        {
          func_175500_n();
        }
      }
      else if (var1 > 0)
      {
        var1--;
        setGrowingAge(var1);
      }
    }
  }
  


  protected void func_175500_n() {}
  

  public boolean isChild()
  {
    return getGrowingAge() < 0;
  }
  



  public void setScaleForAge(boolean p_98054_1_)
  {
    setScale(p_98054_1_ ? 0.5F : 1.0F);
  }
  



  protected final void setSize(float width, float height)
  {
    boolean var3 = field_98056_d > 0.0F;
    field_98056_d = width;
    field_98057_e = height;
    
    if (!var3)
    {
      setScale(1.0F);
    }
  }
  
  protected final void setScale(float p_98055_1_)
  {
    super.setSize(field_98056_d * p_98055_1_, field_98057_e * p_98055_1_);
  }
}
