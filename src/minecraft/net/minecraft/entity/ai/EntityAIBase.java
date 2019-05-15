package net.minecraft.entity.ai;



public abstract class EntityAIBase
{
  private int mutexBits;
  

  private static final String __OBFID = "CL_00001587";
  


  public EntityAIBase() {}
  

  public abstract boolean shouldExecute();
  

  public boolean continueExecuting()
  {
    return shouldExecute();
  }
  




  public boolean isInterruptible()
  {
    return true;
  }
  




  public void startExecuting() {}
  



  public void resetTask() {}
  



  public void updateTask() {}
  



  public void setMutexBits(int p_75248_1_)
  {
    mutexBits = p_75248_1_;
  }
  




  public int getMutexBits()
  {
    return mutexBits;
  }
}
