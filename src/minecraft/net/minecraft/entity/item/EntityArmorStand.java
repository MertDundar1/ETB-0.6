package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Rotations;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityArmorStand extends EntityLivingBase
{
  private static final Rotations DEFAULT_HEAD_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
  private static final Rotations DEFAULT_BODY_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
  private static final Rotations DEFAULT_LEFTARM_ROTATION = new Rotations(-10.0F, 0.0F, -10.0F);
  private static final Rotations DEFAULT_RIGHTARM_ROTATION = new Rotations(-15.0F, 0.0F, 10.0F);
  private static final Rotations DEFAULT_LEFTLEG_ROTATION = new Rotations(-1.0F, 0.0F, -1.0F);
  private static final Rotations DEFAULT_RIGHTLEG_ROTATION = new Rotations(1.0F, 0.0F, 1.0F);
  private final ItemStack[] contents;
  private boolean canInteract;
  private long field_175437_i;
  private int disabledSlots;
  private Rotations headRotation;
  private Rotations bodyRotation;
  private Rotations leftArmRotation;
  private Rotations rightArmRotation;
  private Rotations leftLegRotation;
  private Rotations rightLegRotation;
  private static final String __OBFID = "CL_00002228";
  
  public EntityArmorStand(World worldIn)
  {
    super(worldIn);
    contents = new ItemStack[5];
    headRotation = DEFAULT_HEAD_ROTATION;
    bodyRotation = DEFAULT_BODY_ROTATION;
    leftArmRotation = DEFAULT_LEFTARM_ROTATION;
    rightArmRotation = DEFAULT_RIGHTARM_ROTATION;
    leftLegRotation = DEFAULT_LEFTLEG_ROTATION;
    rightLegRotation = DEFAULT_RIGHTLEG_ROTATION;
    func_174810_b(true);
    noClip = hasNoGravity();
    setSize(0.5F, 1.975F);
  }
  
  public EntityArmorStand(World worldIn, double p_i45855_2_, double p_i45855_4_, double p_i45855_6_)
  {
    this(worldIn);
    setPosition(p_i45855_2_, p_i45855_4_, p_i45855_6_);
  }
  



  public boolean isServerWorld()
  {
    return (super.isServerWorld()) && (!hasNoGravity());
  }
  
  protected void entityInit()
  {
    super.entityInit();
    dataWatcher.addObject(10, Byte.valueOf((byte)0));
    dataWatcher.addObject(11, DEFAULT_HEAD_ROTATION);
    dataWatcher.addObject(12, DEFAULT_BODY_ROTATION);
    dataWatcher.addObject(13, DEFAULT_LEFTARM_ROTATION);
    dataWatcher.addObject(14, DEFAULT_RIGHTARM_ROTATION);
    dataWatcher.addObject(15, DEFAULT_LEFTLEG_ROTATION);
    dataWatcher.addObject(16, DEFAULT_RIGHTLEG_ROTATION);
  }
  



  public ItemStack getHeldItem()
  {
    return contents[0];
  }
  



  public ItemStack getEquipmentInSlot(int p_71124_1_)
  {
    return contents[p_71124_1_];
  }
  
  public ItemStack getCurrentArmor(int p_82169_1_)
  {
    return contents[(p_82169_1_ + 1)];
  }
  



  public void setCurrentItemOrArmor(int slotIn, ItemStack itemStackIn)
  {
    contents[slotIn] = itemStackIn;
  }
  



  public ItemStack[] getInventory()
  {
    return contents;
  }
  
  public boolean func_174820_d(int p_174820_1_, ItemStack p_174820_2_)
  {
    int var3;
    int var3;
    if (p_174820_1_ == 99)
    {
      var3 = 0;
    }
    else
    {
      var3 = p_174820_1_ - 100 + 1;
      
      if ((var3 < 0) || (var3 >= contents.length))
      {
        return false;
      }
    }
    
    if ((p_174820_2_ != null) && (net.minecraft.entity.EntityLiving.getArmorPosition(p_174820_2_) != var3) && ((var3 != 4) || (!(p_174820_2_.getItem() instanceof net.minecraft.item.ItemBlock))))
    {
      return false;
    }
    

    setCurrentItemOrArmor(var3, p_174820_2_);
    return true;
  }
  




  public void writeEntityToNBT(NBTTagCompound tagCompound)
  {
    super.writeEntityToNBT(tagCompound);
    NBTTagList var2 = new NBTTagList();
    
    for (int var3 = 0; var3 < contents.length; var3++)
    {
      NBTTagCompound var4 = new NBTTagCompound();
      
      if (contents[var3] != null)
      {
        contents[var3].writeToNBT(var4);
      }
      
      var2.appendTag(var4);
    }
    
    tagCompound.setTag("Equipment", var2);
    
    if ((getAlwaysRenderNameTag()) && ((getCustomNameTag() == null) || (getCustomNameTag().length() == 0)))
    {
      tagCompound.setBoolean("CustomNameVisible", getAlwaysRenderNameTag());
    }
    
    tagCompound.setBoolean("Invisible", isInvisible());
    tagCompound.setBoolean("Small", isSmall());
    tagCompound.setBoolean("ShowArms", getShowArms());
    tagCompound.setInteger("DisabledSlots", disabledSlots);
    tagCompound.setBoolean("NoGravity", hasNoGravity());
    tagCompound.setBoolean("NoBasePlate", hasNoBasePlate());
    tagCompound.setTag("Pose", readPoseFromNBT());
  }
  



  public void readEntityFromNBT(NBTTagCompound tagCompund)
  {
    super.readEntityFromNBT(tagCompund);
    
    if (tagCompund.hasKey("Equipment", 9))
    {
      NBTTagList var2 = tagCompund.getTagList("Equipment", 10);
      
      for (int var3 = 0; var3 < contents.length; var3++)
      {
        contents[var3] = ItemStack.loadItemStackFromNBT(var2.getCompoundTagAt(var3));
      }
    }
    
    setInvisible(tagCompund.getBoolean("Invisible"));
    setSmall(tagCompund.getBoolean("Small"));
    setShowArms(tagCompund.getBoolean("ShowArms"));
    disabledSlots = tagCompund.getInteger("DisabledSlots");
    setNoGravity(tagCompund.getBoolean("NoGravity"));
    setNoBasePlate(tagCompund.getBoolean("NoBasePlate"));
    noClip = hasNoGravity();
    NBTTagCompound var4 = tagCompund.getCompoundTag("Pose");
    writePoseToNBT(var4);
  }
  
  private void writePoseToNBT(NBTTagCompound p_175416_1_)
  {
    NBTTagList var2 = p_175416_1_.getTagList("Head", 5);
    
    if (var2.tagCount() > 0)
    {
      setHeadRotation(new Rotations(var2));
    }
    else
    {
      setHeadRotation(DEFAULT_HEAD_ROTATION);
    }
    
    NBTTagList var3 = p_175416_1_.getTagList("Body", 5);
    
    if (var3.tagCount() > 0)
    {
      setBodyRotation(new Rotations(var3));
    }
    else
    {
      setBodyRotation(DEFAULT_BODY_ROTATION);
    }
    
    NBTTagList var4 = p_175416_1_.getTagList("LeftArm", 5);
    
    if (var4.tagCount() > 0)
    {
      setLeftArmRotation(new Rotations(var4));
    }
    else
    {
      setLeftArmRotation(DEFAULT_LEFTARM_ROTATION);
    }
    
    NBTTagList var5 = p_175416_1_.getTagList("RightArm", 5);
    
    if (var5.tagCount() > 0)
    {
      setRightArmRotation(new Rotations(var5));
    }
    else
    {
      setRightArmRotation(DEFAULT_RIGHTARM_ROTATION);
    }
    
    NBTTagList var6 = p_175416_1_.getTagList("LeftLeg", 5);
    
    if (var6.tagCount() > 0)
    {
      setLeftLegRotation(new Rotations(var6));
    }
    else
    {
      setLeftLegRotation(DEFAULT_LEFTLEG_ROTATION);
    }
    
    NBTTagList var7 = p_175416_1_.getTagList("RightLeg", 5);
    
    if (var7.tagCount() > 0)
    {
      setRightLegRotation(new Rotations(var7));
    }
    else
    {
      setRightLegRotation(DEFAULT_RIGHTLEG_ROTATION);
    }
  }
  
  private NBTTagCompound readPoseFromNBT()
  {
    NBTTagCompound var1 = new NBTTagCompound();
    
    if (!DEFAULT_HEAD_ROTATION.equals(headRotation))
    {
      var1.setTag("Head", headRotation.func_179414_a());
    }
    
    if (!DEFAULT_BODY_ROTATION.equals(bodyRotation))
    {
      var1.setTag("Body", bodyRotation.func_179414_a());
    }
    
    if (!DEFAULT_LEFTARM_ROTATION.equals(leftArmRotation))
    {
      var1.setTag("LeftArm", leftArmRotation.func_179414_a());
    }
    
    if (!DEFAULT_RIGHTARM_ROTATION.equals(rightArmRotation))
    {
      var1.setTag("RightArm", rightArmRotation.func_179414_a());
    }
    
    if (!DEFAULT_LEFTLEG_ROTATION.equals(leftLegRotation))
    {
      var1.setTag("LeftLeg", leftLegRotation.func_179414_a());
    }
    
    if (!DEFAULT_RIGHTLEG_ROTATION.equals(rightLegRotation))
    {
      var1.setTag("RightLeg", rightLegRotation.func_179414_a());
    }
    
    return var1;
  }
  



  public boolean canBePushed()
  {
    return false;
  }
  
  protected void collideWithEntity(Entity p_82167_1_) {}
  
  protected void collideWithNearbyEntities()
  {
    List var1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox());
    
    if ((var1 != null) && (!var1.isEmpty()))
    {
      for (int var2 = 0; var2 < var1.size(); var2++)
      {
        Entity var3 = (Entity)var1.get(var2);
        
        if (((var3 instanceof EntityMinecart)) && (((EntityMinecart)var3).func_180456_s() == EntityMinecart.EnumMinecartType.RIDEABLE) && (getDistanceSqToEntity(var3) <= 0.2D))
        {
          var3.applyEntityCollision(this);
        }
      }
    }
  }
  
  public boolean func_174825_a(EntityPlayer p_174825_1_, Vec3 p_174825_2_)
  {
    if ((!worldObj.isRemote) && (!p_174825_1_.func_175149_v()))
    {
      byte var3 = 0;
      ItemStack var4 = p_174825_1_.getCurrentEquippedItem();
      boolean var5 = var4 != null;
      
      if ((var5) && ((var4.getItem() instanceof ItemArmor)))
      {
        ItemArmor var6 = (ItemArmor)var4.getItem();
        
        if (armorType == 3)
        {
          var3 = 1;
        }
        else if (armorType == 2)
        {
          var3 = 2;
        }
        else if (armorType == 1)
        {
          var3 = 3;
        }
        else if (armorType == 0)
        {
          var3 = 4;
        }
      }
      
      if ((var5) && ((var4.getItem() == Items.skull) || (var4.getItem() == Item.getItemFromBlock(Blocks.pumpkin))))
      {
        var3 = 4;
      }
      
      double var19 = 0.1D;
      double var8 = 0.9D;
      double var10 = 0.4D;
      double var12 = 1.6D;
      byte var14 = 0;
      boolean var15 = isSmall();
      double var16 = var15 ? yCoord * 2.0D : yCoord;
      
      if (var16 >= 0.1D) if ((var16 < 0.1D + (var15 ? 0.8D : 0.45D)) && (contents[1] != null))
        {
          var14 = 1;
          break label372; }
      if (var16 >= 0.9D + (var15 ? 0.3D : 0.0D)) if ((var16 < 0.9D + (var15 ? 1.0D : 0.7D)) && (contents[3] != null))
        {
          var14 = 3;
          break label372; }
      if (var16 >= 0.4D) if ((var16 < 0.4D + (var15 ? 1.0D : 0.8D)) && (contents[2] != null))
        {
          var14 = 2;
          break label372; }
      if ((var16 >= 1.6D) && (contents[4] != null))
      {
        var14 = 4;
      }
      label372:
      boolean var18 = contents[var14] != null;
      
      if (((disabledSlots & 1 << var14) != 0) || ((disabledSlots & 1 << var3) != 0))
      {
        var14 = var3;
        
        if ((disabledSlots & 1 << var3) != 0)
        {
          if ((disabledSlots & 0x1) != 0)
          {
            return true;
          }
          
          var14 = 0;
        }
      }
      
      if ((var5) && (var3 == 0) && (!getShowArms()))
      {
        return true;
      }
      

      if (var5)
      {
        func_175422_a(p_174825_1_, var3);
      }
      else if (var18)
      {
        func_175422_a(p_174825_1_, var14);
      }
      
      return true;
    }
    


    return true;
  }
  

  private void func_175422_a(EntityPlayer p_175422_1_, int p_175422_2_)
  {
    ItemStack var3 = contents[p_175422_2_];
    
    if ((var3 == null) || ((disabledSlots & 1 << p_175422_2_ + 8) == 0))
    {
      if ((var3 != null) || ((disabledSlots & 1 << p_175422_2_ + 16) == 0))
      {
        int var4 = inventory.currentItem;
        ItemStack var5 = inventory.getStackInSlot(var4);
        

        if ((capabilities.isCreativeMode) && ((var3 == null) || (var3.getItem() == Item.getItemFromBlock(Blocks.air))) && (var5 != null))
        {
          ItemStack var6 = var5.copy();
          stackSize = 1;
          setCurrentItemOrArmor(p_175422_2_, var6);
        }
        else if ((var5 != null) && (stackSize > 1))
        {
          if (var3 == null)
          {
            ItemStack var6 = var5.copy();
            stackSize = 1;
            setCurrentItemOrArmor(p_175422_2_, var6);
            stackSize -= 1;
          }
        }
        else
        {
          setCurrentItemOrArmor(p_175422_2_, var5);
          inventory.setInventorySlotContents(var4, var3);
        }
      }
    }
  }
  



  public boolean attackEntityFrom(DamageSource source, float amount)
  {
    if ((!worldObj.isRemote) && (!canInteract))
    {
      if (DamageSource.outOfWorld.equals(source))
      {
        setDead();
        return false;
      }
      if (func_180431_b(source))
      {
        return false;
      }
      if (source.isExplosion())
      {
        dropContents();
        setDead();
        return false;
      }
      if (DamageSource.inFire.equals(source))
      {
        if (!isBurning())
        {
          setFire(5);
        }
        else
        {
          func_175406_a(0.15F);
        }
        
        return false;
      }
      if ((DamageSource.onFire.equals(source)) && (getHealth() > 0.5F))
      {
        func_175406_a(4.0F);
        return false;
      }
      

      boolean var3 = "arrow".equals(source.getDamageType());
      boolean var4 = "player".equals(source.getDamageType());
      
      if ((!var4) && (!var3))
      {
        return false;
      }
      

      if ((source.getSourceOfDamage() instanceof EntityArrow))
      {
        source.getSourceOfDamage().setDead();
      }
      
      if (((source.getEntity() instanceof EntityPlayer)) && (!getEntitycapabilities.allowEdit))
      {
        return false;
      }
      if (source.func_180136_u())
      {
        playParticles();
        setDead();
        return false;
      }
      

      long var5 = worldObj.getTotalWorldTime();
      
      if ((var5 - field_175437_i > 5L) && (!var3))
      {
        field_175437_i = var5;
      }
      else
      {
        dropBlock();
        playParticles();
        setDead();
      }
      
      return false;
    }
    




    return false;
  }
  

  private void playParticles()
  {
    if ((worldObj instanceof WorldServer))
    {
      ((WorldServer)worldObj).func_175739_a(net.minecraft.util.EnumParticleTypes.BLOCK_DUST, posX, posY + height / 1.5D, posZ, 10, width / 4.0F, height / 4.0F, width / 4.0F, 0.05D, new int[] { Block.getStateId(Blocks.planks.getDefaultState()) });
    }
  }
  
  private void func_175406_a(float p_175406_1_)
  {
    float var2 = getHealth();
    var2 -= p_175406_1_;
    
    if (var2 <= 0.5F)
    {
      dropContents();
      setDead();
    }
    else
    {
      setHealth(var2);
    }
  }
  
  private void dropBlock()
  {
    Block.spawnAsEntity(worldObj, new BlockPos(this), new ItemStack(Items.armor_stand));
    dropContents();
  }
  
  private void dropContents()
  {
    for (int var1 = 0; var1 < contents.length; var1++)
    {
      if ((contents[var1] != null) && (contents[var1].stackSize > 0))
      {
        if (contents[var1] != null)
        {
          Block.spawnAsEntity(worldObj, new BlockPos(this).offsetUp(), contents[var1]);
        }
        
        contents[var1] = null;
      }
    }
  }
  
  protected float func_110146_f(float p_110146_1_, float p_110146_2_)
  {
    prevRenderYawOffset = prevRotationYaw;
    renderYawOffset = rotationYaw;
    return 0.0F;
  }
  
  public float getEyeHeight()
  {
    return isChild() ? height * 0.5F : height * 0.9F;
  }
  



  public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_)
  {
    if (!hasNoGravity())
    {
      super.moveEntityWithHeading(p_70612_1_, p_70612_2_);
    }
  }
  



  public void onUpdate()
  {
    super.onUpdate();
    Rotations var1 = dataWatcher.getWatchableObjectRotations(11);
    
    if (!headRotation.equals(var1))
    {
      setHeadRotation(var1);
    }
    
    Rotations var2 = dataWatcher.getWatchableObjectRotations(12);
    
    if (!bodyRotation.equals(var2))
    {
      setBodyRotation(var2);
    }
    
    Rotations var3 = dataWatcher.getWatchableObjectRotations(13);
    
    if (!leftArmRotation.equals(var3))
    {
      setLeftArmRotation(var3);
    }
    
    Rotations var4 = dataWatcher.getWatchableObjectRotations(14);
    
    if (!rightArmRotation.equals(var4))
    {
      setRightArmRotation(var4);
    }
    
    Rotations var5 = dataWatcher.getWatchableObjectRotations(15);
    
    if (!leftLegRotation.equals(var5))
    {
      setLeftLegRotation(var5);
    }
    
    Rotations var6 = dataWatcher.getWatchableObjectRotations(16);
    
    if (!rightLegRotation.equals(var6))
    {
      setRightLegRotation(var6);
    }
  }
  
  protected void func_175135_B()
  {
    setInvisible(canInteract);
  }
  
  public void setInvisible(boolean invisible)
  {
    canInteract = invisible;
    super.setInvisible(invisible);
  }
  



  public boolean isChild()
  {
    return isSmall();
  }
  
  public void func_174812_G()
  {
    setDead();
  }
  
  public boolean func_180427_aV()
  {
    return isInvisible();
  }
  
  private void setSmall(boolean p_175420_1_)
  {
    byte var2 = dataWatcher.getWatchableObjectByte(10);
    
    if (p_175420_1_)
    {
      var2 = (byte)(var2 | 0x1);
    }
    else
    {
      var2 = (byte)(var2 & 0xFFFFFFFE);
    }
    
    dataWatcher.updateObject(10, Byte.valueOf(var2));
  }
  
  public boolean isSmall()
  {
    return (dataWatcher.getWatchableObjectByte(10) & 0x1) != 0;
  }
  
  private void setNoGravity(boolean p_175425_1_)
  {
    byte var2 = dataWatcher.getWatchableObjectByte(10);
    
    if (p_175425_1_)
    {
      var2 = (byte)(var2 | 0x2);
    }
    else
    {
      var2 = (byte)(var2 & 0xFFFFFFFD);
    }
    
    dataWatcher.updateObject(10, Byte.valueOf(var2));
  }
  
  public boolean hasNoGravity()
  {
    return (dataWatcher.getWatchableObjectByte(10) & 0x2) != 0;
  }
  
  private void setShowArms(boolean p_175413_1_)
  {
    byte var2 = dataWatcher.getWatchableObjectByte(10);
    
    if (p_175413_1_)
    {
      var2 = (byte)(var2 | 0x4);
    }
    else
    {
      var2 = (byte)(var2 & 0xFFFFFFFB);
    }
    
    dataWatcher.updateObject(10, Byte.valueOf(var2));
  }
  
  public boolean getShowArms()
  {
    return (dataWatcher.getWatchableObjectByte(10) & 0x4) != 0;
  }
  
  private void setNoBasePlate(boolean p_175426_1_)
  {
    byte var2 = dataWatcher.getWatchableObjectByte(10);
    
    if (p_175426_1_)
    {
      var2 = (byte)(var2 | 0x8);
    }
    else
    {
      var2 = (byte)(var2 & 0xFFFFFFF7);
    }
    
    dataWatcher.updateObject(10, Byte.valueOf(var2));
  }
  
  public boolean hasNoBasePlate()
  {
    return (dataWatcher.getWatchableObjectByte(10) & 0x8) != 0;
  }
  
  public void setHeadRotation(Rotations p_175415_1_)
  {
    headRotation = p_175415_1_;
    dataWatcher.updateObject(11, p_175415_1_);
  }
  
  public void setBodyRotation(Rotations p_175424_1_)
  {
    bodyRotation = p_175424_1_;
    dataWatcher.updateObject(12, p_175424_1_);
  }
  
  public void setLeftArmRotation(Rotations p_175405_1_)
  {
    leftArmRotation = p_175405_1_;
    dataWatcher.updateObject(13, p_175405_1_);
  }
  
  public void setRightArmRotation(Rotations p_175428_1_)
  {
    rightArmRotation = p_175428_1_;
    dataWatcher.updateObject(14, p_175428_1_);
  }
  
  public void setLeftLegRotation(Rotations p_175417_1_)
  {
    leftLegRotation = p_175417_1_;
    dataWatcher.updateObject(15, p_175417_1_);
  }
  
  public void setRightLegRotation(Rotations p_175427_1_)
  {
    rightLegRotation = p_175427_1_;
    dataWatcher.updateObject(16, p_175427_1_);
  }
  
  public Rotations getHeadRotation()
  {
    return headRotation;
  }
  
  public Rotations getBodyRotation()
  {
    return bodyRotation;
  }
  
  public Rotations getLeftArmRotation()
  {
    return leftArmRotation;
  }
  
  public Rotations getRightArmRotation()
  {
    return rightArmRotation;
  }
  
  public Rotations getLeftLegRotation()
  {
    return leftLegRotation;
  }
  
  public Rotations getRightLegRotation()
  {
    return rightLegRotation;
  }
}
