package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySnowball extends EntityThrowable
{
  private static final String __OBFID = "CL_00001722";
  
  public EntitySnowball(World worldIn)
  {
    super(worldIn);
  }
  
  public EntitySnowball(World worldIn, EntityLivingBase p_i1774_2_)
  {
    super(worldIn, p_i1774_2_);
  }
  
  public EntitySnowball(World worldIn, double p_i1775_2_, double p_i1775_4_, double p_i1775_6_)
  {
    super(worldIn, p_i1775_2_, p_i1775_4_, p_i1775_6_);
  }
  



  protected void onImpact(MovingObjectPosition p_70184_1_)
  {
    if (entityHit != null)
    {
      byte var2 = 0;
      
      if ((entityHit instanceof net.minecraft.entity.monster.EntityBlaze))
      {
        var2 = 3;
      }
      
      entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), var2);
    }
    
    for (int var3 = 0; var3 < 8; var3++)
    {
      worldObj.spawnParticle(EnumParticleTypes.SNOWBALL, posX, posY, posZ, 0.0D, 0.0D, 0.0D, new int[0]);
    }
    
    if (!worldObj.isRemote)
    {
      setDead();
    }
  }
}
