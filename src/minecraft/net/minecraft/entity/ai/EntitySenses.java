package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;

public class EntitySenses
{
  EntityLiving entityObj;
  List seenEntities = Lists.newArrayList();
  

  List unseenEntities = Lists.newArrayList();
  private static final String __OBFID = "CL_00001628";
  
  public EntitySenses(EntityLiving p_i1672_1_)
  {
    entityObj = p_i1672_1_;
  }
  



  public void clearSensingCache()
  {
    seenEntities.clear();
    unseenEntities.clear();
  }
  



  public boolean canSee(Entity p_75522_1_)
  {
    if (seenEntities.contains(p_75522_1_))
    {
      return true;
    }
    if (unseenEntities.contains(p_75522_1_))
    {
      return false;
    }
    

    entityObj.worldObj.theProfiler.startSection("canSee");
    boolean var2 = entityObj.canEntityBeSeen(p_75522_1_);
    entityObj.worldObj.theProfiler.endSection();
    
    if (var2)
    {
      seenEntities.add(p_75522_1_);
    }
    else
    {
      unseenEntities.add(p_75522_1_);
    }
    
    return var2;
  }
}
