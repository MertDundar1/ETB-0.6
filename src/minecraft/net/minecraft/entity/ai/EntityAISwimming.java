package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAISwimming extends EntityAIBase
{
  private EntityLiving theEntity;
  private static final String __OBFID = "CL_00001584";
  
  public EntityAISwimming(EntityLiving p_i1624_1_)
  {
    theEntity = p_i1624_1_;
    setMutexBits(4);
    ((PathNavigateGround)p_i1624_1_.getNavigator()).func_179693_d(true);
  }
  



  public boolean shouldExecute()
  {
    return (theEntity.isInWater()) || (theEntity.func_180799_ab());
  }
  



  public void updateTask()
  {
    if (theEntity.getRNG().nextFloat() < 0.8F)
    {
      theEntity.getJumpHelper().setJumping();
    }
  }
}