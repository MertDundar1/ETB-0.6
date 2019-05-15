package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearestPlayer extends EntityAIBase
{
  private static final Logger field_179436_a = ;
  private EntityLiving field_179434_b;
  private final Predicate field_179435_c;
  private final EntityAINearestAttackableTarget.Sorter field_179432_d;
  private EntityLivingBase field_179433_e;
  private static final String __OBFID = "CL_00002248";
  
  public EntityAIFindEntityNearestPlayer(EntityLiving p_i45882_1_)
  {
    field_179434_b = p_i45882_1_;
    
    if ((p_i45882_1_ instanceof EntityCreature))
    {
      field_179436_a.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
    }
    
    field_179435_c = new Predicate()
    {
      private static final String __OBFID = "CL_00002247";
      
      public boolean func_179880_a(Entity p_179880_1_) {
        if (!(p_179880_1_ instanceof EntityPlayer))
        {
          return false;
        }
        

        double var2 = func_179431_f();
        
        if (p_179880_1_.isSneaking())
        {
          var2 *= 0.800000011920929D;
        }
        
        if (p_179880_1_.isInvisible())
        {
          float var4 = ((EntityPlayer)p_179880_1_).getArmorVisibility();
          
          if (var4 < 0.1F)
          {
            var4 = 0.1F;
          }
          
          var2 *= 0.7F * var4;
        }
        
        return p_179880_1_.getDistanceToEntity(field_179434_b) > var2 ? false : EntityAITarget.func_179445_a(field_179434_b, (EntityLivingBase)p_179880_1_, false, true);
      }
      
      public boolean apply(Object p_apply_1_)
      {
        return func_179880_a((Entity)p_apply_1_);
      }
    };
    field_179432_d = new EntityAINearestAttackableTarget.Sorter(p_i45882_1_);
  }
  



  public boolean shouldExecute()
  {
    double var1 = func_179431_f();
    List var3 = field_179434_b.worldObj.func_175647_a(EntityPlayer.class, field_179434_b.getEntityBoundingBox().expand(var1, 4.0D, var1), field_179435_c);
    java.util.Collections.sort(var3, field_179432_d);
    
    if (var3.isEmpty())
    {
      return false;
    }
    

    field_179433_e = ((EntityLivingBase)var3.get(0));
    return true;
  }
  




  public boolean continueExecuting()
  {
    EntityLivingBase var1 = field_179434_b.getAttackTarget();
    
    if (var1 == null)
    {
      return false;
    }
    if (!var1.isEntityAlive())
    {
      return false;
    }
    

    Team var2 = field_179434_b.getTeam();
    Team var3 = var1.getTeam();
    
    if ((var2 != null) && (var3 == var2))
    {
      return false;
    }
    

    double var4 = func_179431_f();
    return field_179434_b.getDistanceSqToEntity(var1) <= var4 * var4;
  }
  





  public void startExecuting()
  {
    field_179434_b.setAttackTarget(field_179433_e);
    super.startExecuting();
  }
  



  public void resetTask()
  {
    field_179434_b.setAttackTarget(null);
    super.startExecuting();
  }
  
  protected double func_179431_f()
  {
    IAttributeInstance var1 = field_179434_b.getEntityAttribute(SharedMonsterAttributes.followRange);
    return var1 == null ? 16.0D : var1.getAttributeValue();
  }
}
