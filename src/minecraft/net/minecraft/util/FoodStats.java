package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class FoodStats
{
  private int foodLevel = 20;
  

  private float foodSaturationLevel = 5.0F;
  

  private float foodExhaustionLevel;
  
  private int foodTimer;
  
  private int prevFoodLevel = 20;
  
  private static final String __OBFID = "CL_00001729";
  
  public FoodStats() {}
  
  public void addStats(int p_75122_1_, float p_75122_2_)
  {
    foodLevel = Math.min(p_75122_1_ + foodLevel, 20);
    foodSaturationLevel = Math.min(foodSaturationLevel + p_75122_1_ * p_75122_2_ * 2.0F, foodLevel);
  }
  
  public void addStats(ItemFood p_151686_1_, ItemStack p_151686_2_)
  {
    addStats(p_151686_1_.getHealAmount(p_151686_2_), p_151686_1_.getSaturationModifier(p_151686_2_));
  }
  



  public void onUpdate(EntityPlayer p_75118_1_)
  {
    EnumDifficulty var2 = worldObj.getDifficulty();
    prevFoodLevel = foodLevel;
    
    if (foodExhaustionLevel > 4.0F)
    {
      foodExhaustionLevel -= 4.0F;
      
      if (foodSaturationLevel > 0.0F)
      {
        foodSaturationLevel = Math.max(foodSaturationLevel - 1.0F, 0.0F);
      }
      else if (var2 != EnumDifficulty.PEACEFUL)
      {
        foodLevel = Math.max(foodLevel - 1, 0);
      }
    }
    
    if ((worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration")) && (foodLevel >= 18) && (p_75118_1_.shouldHeal()))
    {
      foodTimer += 1;
      
      if (foodTimer >= 80)
      {
        p_75118_1_.heal(1.0F);
        addExhaustion(3.0F);
        foodTimer = 0;
      }
    }
    else if (foodLevel <= 0)
    {
      foodTimer += 1;
      
      if (foodTimer >= 80)
      {
        if ((p_75118_1_.getHealth() > 10.0F) || (var2 == EnumDifficulty.HARD) || ((p_75118_1_.getHealth() > 1.0F) && (var2 == EnumDifficulty.NORMAL)))
        {
          p_75118_1_.attackEntityFrom(DamageSource.starve, 1.0F);
        }
        
        foodTimer = 0;
      }
    }
    else
    {
      foodTimer = 0;
    }
  }
  



  public void readNBT(NBTTagCompound p_75112_1_)
  {
    if (p_75112_1_.hasKey("foodLevel", 99))
    {
      foodLevel = p_75112_1_.getInteger("foodLevel");
      foodTimer = p_75112_1_.getInteger("foodTickTimer");
      foodSaturationLevel = p_75112_1_.getFloat("foodSaturationLevel");
      foodExhaustionLevel = p_75112_1_.getFloat("foodExhaustionLevel");
    }
  }
  



  public void writeNBT(NBTTagCompound p_75117_1_)
  {
    p_75117_1_.setInteger("foodLevel", foodLevel);
    p_75117_1_.setInteger("foodTickTimer", foodTimer);
    p_75117_1_.setFloat("foodSaturationLevel", foodSaturationLevel);
    p_75117_1_.setFloat("foodExhaustionLevel", foodExhaustionLevel);
  }
  



  public int getFoodLevel()
  {
    return foodLevel;
  }
  
  public int getPrevFoodLevel()
  {
    return prevFoodLevel;
  }
  



  public boolean needFood()
  {
    return foodLevel < 20;
  }
  



  public void addExhaustion(float p_75113_1_)
  {
    foodExhaustionLevel = Math.min(foodExhaustionLevel + p_75113_1_, 40.0F);
  }
  



  public float getSaturationLevel()
  {
    return foodSaturationLevel;
  }
  
  public void setFoodLevel(int p_75114_1_)
  {
    foodLevel = p_75114_1_;
  }
  
  public void setFoodSaturationLevel(float p_75119_1_)
  {
    foodSaturationLevel = p_75119_1_;
  }
}
