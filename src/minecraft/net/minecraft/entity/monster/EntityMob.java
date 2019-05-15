package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public abstract class EntityMob extends EntityCreature implements IMob
{
  protected final net.minecraft.entity.ai.EntityAIBase field_175455_a = new EntityAIAvoidEntity(this, new com.google.common.base.Predicate()
  {
    private static final String __OBFID = "CL_00002208";
    
    public boolean func_179911_a(Entity p_179911_1_) {
      return ((p_179911_1_ instanceof EntityCreeper)) && (((EntityCreeper)p_179911_1_).getCreeperState() > 0);
    }
    
    public boolean apply(Object p_apply_1_) {
      return func_179911_a((Entity)p_apply_1_);
    }
  }, 
  









    4.0F, 1.0D, 2.0D);
  private static final String __OBFID = "CL_00001692";
  
  public EntityMob(World worldIn)
  {
    super(worldIn);
    experienceValue = 5;
  }
  




  public void onLivingUpdate()
  {
    updateArmSwingProgress();
    float var1 = getBrightness(1.0F);
    
    if (var1 > 0.5F)
    {
      entityAge += 2;
    }
    
    super.onLivingUpdate();
  }
  



  public void onUpdate()
  {
    super.onUpdate();
    
    if ((!worldObj.isRemote) && (worldObj.getDifficulty() == EnumDifficulty.PEACEFUL))
    {
      setDead();
    }
  }
  
  protected String getSwimSound()
  {
    return "game.hostile.swim";
  }
  
  protected String getSplashSound()
  {
    return "game.hostile.swim.splash";
  }
  



  public boolean attackEntityFrom(DamageSource source, float amount)
  {
    if (func_180431_b(source))
    {
      return false;
    }
    if (super.attackEntityFrom(source, amount))
    {
      Entity var3 = source.getEntity();
      return (riddenByEntity != var3) && (ridingEntity != var3);
    }
    

    return false;
  }
  




  protected String getHurtSound()
  {
    return "game.hostile.hurt";
  }
  



  protected String getDeathSound()
  {
    return "game.hostile.die";
  }
  
  protected String func_146067_o(int p_146067_1_)
  {
    return p_146067_1_ > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
  }
  
  public boolean attackEntityAsMob(Entity p_70652_1_)
  {
    float var2 = (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
    int var3 = 0;
    
    if ((p_70652_1_ instanceof EntityLivingBase))
    {
      var2 += EnchantmentHelper.func_152377_a(getHeldItem(), ((EntityLivingBase)p_70652_1_).getCreatureAttribute());
      var3 += EnchantmentHelper.getRespiration(this);
    }
    
    boolean var4 = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), var2);
    
    if (var4)
    {
      if (var3 > 0)
      {
        p_70652_1_.addVelocity(-MathHelper.sin(rotationYaw * 3.1415927F / 180.0F) * var3 * 0.5F, 0.1D, MathHelper.cos(rotationYaw * 3.1415927F / 180.0F) * var3 * 0.5F);
        motionX *= 0.6D;
        motionZ *= 0.6D;
      }
      
      int var5 = EnchantmentHelper.getFireAspectModifier(this);
      
      if (var5 > 0)
      {
        p_70652_1_.setFire(var5 * 4);
      }
      
      func_174815_a(this, p_70652_1_);
    }
    
    return var4;
  }
  
  public float func_180484_a(BlockPos p_180484_1_)
  {
    return 0.5F - worldObj.getLightBrightness(p_180484_1_);
  }
  



  protected boolean isValidLightLevel()
  {
    BlockPos var1 = new BlockPos(posX, getEntityBoundingBoxminY, posZ);
    
    if (worldObj.getLightFor(net.minecraft.world.EnumSkyBlock.SKY, var1) > rand.nextInt(32))
    {
      return false;
    }
    

    int var2 = worldObj.getLightFromNeighbors(var1);
    
    if (worldObj.isThundering())
    {
      int var3 = worldObj.getSkylightSubtracted();
      worldObj.setSkylightSubtracted(10);
      var2 = worldObj.getLightFromNeighbors(var1);
      worldObj.setSkylightSubtracted(var3);
    }
    
    return var2 <= rand.nextInt(8);
  }
  




  public boolean getCanSpawnHere()
  {
    return (worldObj.getDifficulty() != EnumDifficulty.PEACEFUL) && (isValidLightLevel()) && (super.getCanSpawnHere());
  }
  
  protected void applyEntityAttributes()
  {
    super.applyEntityAttributes();
    getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
  }
  
  protected boolean func_146066_aG()
  {
    return true;
  }
}
