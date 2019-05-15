package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityCow extends EntityAnimal
{
  private static final String __OBFID = "CL_00001640";
  
  public EntityCow(World worldIn)
  {
    super(worldIn);
    setSize(0.9F, 1.3F);
    ((PathNavigateGround)getNavigator()).func_179690_a(true);
    tasks.addTask(0, new net.minecraft.entity.ai.EntityAISwimming(this));
    tasks.addTask(1, new net.minecraft.entity.ai.EntityAIPanic(this, 2.0D));
    tasks.addTask(2, new EntityAIMate(this, 1.0D));
    tasks.addTask(3, new net.minecraft.entity.ai.EntityAITempt(this, 1.25D, Items.wheat, false));
    tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
    tasks.addTask(5, new EntityAIWander(this, 1.0D));
    tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    tasks.addTask(7, new net.minecraft.entity.ai.EntityAILookIdle(this));
  }
  
  protected void applyEntityAttributes()
  {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
  }
  



  protected String getLivingSound()
  {
    return "mob.cow.say";
  }
  



  protected String getHurtSound()
  {
    return "mob.cow.hurt";
  }
  



  protected String getDeathSound()
  {
    return "mob.cow.hurt";
  }
  
  protected void func_180429_a(BlockPos p_180429_1_, Block p_180429_2_)
  {
    playSound("mob.cow.step", 0.15F, 1.0F);
  }
  



  protected float getSoundVolume()
  {
    return 0.4F;
  }
  
  protected net.minecraft.item.Item getDropItem()
  {
    return Items.leather;
  }
  



  protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
  {
    int var3 = rand.nextInt(3) + rand.nextInt(1 + p_70628_2_);
    

    for (int var4 = 0; var4 < var3; var4++)
    {
      dropItem(Items.leather, 1);
    }
    
    var3 = rand.nextInt(3) + 1 + rand.nextInt(1 + p_70628_2_);
    
    for (var4 = 0; var4 < var3; var4++)
    {
      if (isBurning())
      {
        dropItem(Items.cooked_beef, 1);
      }
      else
      {
        dropItem(Items.beef, 1);
      }
    }
  }
  



  public boolean interact(EntityPlayer p_70085_1_)
  {
    ItemStack var2 = inventory.getCurrentItem();
    
    if ((var2 != null) && (var2.getItem() == Items.bucket) && (!capabilities.isCreativeMode))
    {
      if (stackSize-- == 1)
      {
        inventory.setInventorySlotContents(inventory.currentItem, new ItemStack(Items.milk_bucket));
      }
      else if (!inventory.addItemStackToInventory(new ItemStack(Items.milk_bucket)))
      {
        p_70085_1_.dropPlayerItemWithRandomChoice(new ItemStack(Items.milk_bucket, 1, 0), false);
      }
      
      return true;
    }
    

    return super.interact(p_70085_1_);
  }
  

  public EntityCow createChild(EntityAgeable p_90011_1_)
  {
    return new EntityCow(worldObj);
  }
  
  public float getEyeHeight()
  {
    return height;
  }
}
