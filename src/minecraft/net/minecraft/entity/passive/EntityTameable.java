package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable
{
  protected EntityAISit aiSit = new EntityAISit(this);
  private static final String __OBFID = "CL_00001561";
  
  public EntityTameable(World worldIn)
  {
    super(worldIn);
    func_175544_ck();
  }
  
  protected void entityInit()
  {
    super.entityInit();
    dataWatcher.addObject(16, Byte.valueOf((byte)0));
    dataWatcher.addObject(17, "");
  }
  



  public void writeEntityToNBT(NBTTagCompound tagCompound)
  {
    super.writeEntityToNBT(tagCompound);
    
    if (func_152113_b() == null)
    {
      tagCompound.setString("OwnerUUID", "");
    }
    else
    {
      tagCompound.setString("OwnerUUID", func_152113_b());
    }
    
    tagCompound.setBoolean("Sitting", isSitting());
  }
  



  public void readEntityFromNBT(NBTTagCompound tagCompund)
  {
    super.readEntityFromNBT(tagCompund);
    String var2 = "";
    
    if (tagCompund.hasKey("OwnerUUID", 8))
    {
      var2 = tagCompund.getString("OwnerUUID");
    }
    else
    {
      String var3 = tagCompund.getString("Owner");
      var2 = PreYggdrasilConverter.func_152719_a(var3);
    }
    
    if (var2.length() > 0)
    {
      func_152115_b(var2);
      setTamed(true);
    }
    
    aiSit.setSitting(tagCompund.getBoolean("Sitting"));
    setSitting(tagCompund.getBoolean("Sitting"));
  }
  



  protected void playTameEffect(boolean p_70908_1_)
  {
    EnumParticleTypes var2 = EnumParticleTypes.HEART;
    
    if (!p_70908_1_)
    {
      var2 = EnumParticleTypes.SMOKE_NORMAL;
    }
    
    for (int var3 = 0; var3 < 7; var3++)
    {
      double var4 = rand.nextGaussian() * 0.02D;
      double var6 = rand.nextGaussian() * 0.02D;
      double var8 = rand.nextGaussian() * 0.02D;
      worldObj.spawnParticle(var2, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, var4, var6, var8, new int[0]);
    }
  }
  
  public void handleHealthUpdate(byte p_70103_1_)
  {
    if (p_70103_1_ == 7)
    {
      playTameEffect(true);
    }
    else if (p_70103_1_ == 6)
    {
      playTameEffect(false);
    }
    else
    {
      super.handleHealthUpdate(p_70103_1_);
    }
  }
  
  public boolean isTamed()
  {
    return (dataWatcher.getWatchableObjectByte(16) & 0x4) != 0;
  }
  
  public void setTamed(boolean p_70903_1_)
  {
    byte var2 = dataWatcher.getWatchableObjectByte(16);
    
    if (p_70903_1_)
    {
      dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 0x4)));
    }
    else
    {
      dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & 0xFFFFFFFB)));
    }
    
    func_175544_ck();
  }
  
  protected void func_175544_ck() {}
  
  public boolean isSitting()
  {
    return (dataWatcher.getWatchableObjectByte(16) & 0x1) != 0;
  }
  
  public void setSitting(boolean p_70904_1_)
  {
    byte var2 = dataWatcher.getWatchableObjectByte(16);
    
    if (p_70904_1_)
    {
      dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 0x1)));
    }
    else
    {
      dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & 0xFFFFFFFE)));
    }
  }
  
  public String func_152113_b()
  {
    return dataWatcher.getWatchableObjectString(17);
  }
  
  public void func_152115_b(String p_152115_1_)
  {
    dataWatcher.updateObject(17, p_152115_1_);
  }
  
  public EntityLivingBase func_180492_cm()
  {
    try
    {
      UUID var1 = UUID.fromString(func_152113_b());
      return var1 == null ? null : worldObj.getPlayerEntityByUUID(var1);
    }
    catch (IllegalArgumentException var2) {}
    
    return null;
  }
  

  public boolean func_152114_e(EntityLivingBase p_152114_1_)
  {
    return p_152114_1_ == func_180492_cm();
  }
  



  public EntityAISit getAISit()
  {
    return aiSit;
  }
  
  public boolean func_142018_a(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_)
  {
    return true;
  }
  
  public net.minecraft.scoreboard.Team getTeam()
  {
    if (isTamed())
    {
      EntityLivingBase var1 = func_180492_cm();
      
      if (var1 != null)
      {
        return var1.getTeam();
      }
    }
    
    return super.getTeam();
  }
  
  public boolean isOnSameTeam(EntityLivingBase p_142014_1_)
  {
    if (isTamed())
    {
      EntityLivingBase var2 = func_180492_cm();
      
      if (p_142014_1_ == var2)
      {
        return true;
      }
      
      if (var2 != null)
      {
        return var2.isOnSameTeam(p_142014_1_);
      }
    }
    
    return super.isOnSameTeam(p_142014_1_);
  }
  



  public void onDeath(DamageSource cause)
  {
    if ((!worldObj.isRemote) && (worldObj.getGameRules().getGameRuleBooleanValue("showDeathMessages")) && (hasCustomName()) && ((func_180492_cm() instanceof EntityPlayerMP)))
    {
      ((EntityPlayerMP)func_180492_cm()).addChatMessage(getCombatTracker().func_151521_b());
    }
    
    super.onDeath(cause);
  }
  
  public net.minecraft.entity.Entity getOwner()
  {
    return func_180492_cm();
  }
}
