package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySheep extends EntityAnimal
{
  private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container()
  {
    private static final String __OBFID = "CL_00001649";
    
    public boolean canInteractWith(EntityPlayer playerIn) {
      return false;
    }
  }, 
  





    2, 1);
  private static final Map field_175514_bm = Maps.newEnumMap(EnumDyeColor.class);
  


  private int sheepTimer;
  

  private EntityAIEatGrass entityAIEatGrass = new EntityAIEatGrass(this);
  private static final String __OBFID = "CL_00001648";
  
  public static float[] func_175513_a(EnumDyeColor p_175513_0_)
  {
    return (float[])field_175514_bm.get(p_175513_0_);
  }
  
  public EntitySheep(World worldIn)
  {
    super(worldIn);
    setSize(0.9F, 1.3F);
    ((PathNavigateGround)getNavigator()).func_179690_a(true);
    tasks.addTask(0, new EntityAISwimming(this));
    tasks.addTask(1, new EntityAIPanic(this, 1.25D));
    tasks.addTask(2, new EntityAIMate(this, 1.0D));
    tasks.addTask(3, new EntityAITempt(this, 1.1D, Items.wheat, false));
    tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
    tasks.addTask(5, entityAIEatGrass);
    tasks.addTask(6, new EntityAIWander(this, 1.0D));
    tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    tasks.addTask(8, new EntityAILookIdle(this));
    inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.dye, 1, 0));
    inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.dye, 1, 0));
  }
  
  protected void updateAITasks()
  {
    sheepTimer = entityAIEatGrass.getEatingGrassTimer();
    super.updateAITasks();
  }
  




  public void onLivingUpdate()
  {
    if (worldObj.isRemote)
    {
      sheepTimer = Math.max(0, sheepTimer - 1);
    }
    
    super.onLivingUpdate();
  }
  
  protected void applyEntityAttributes()
  {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
  }
  
  protected void entityInit()
  {
    super.entityInit();
    dataWatcher.addObject(16, new Byte((byte)0));
  }
  



  protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
  {
    if (!getSheared())
    {
      entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, func_175509_cj().func_176765_a()), 0.0F);
    }
    
    int var3 = rand.nextInt(2) + 1 + rand.nextInt(1 + p_70628_2_);
    
    for (int var4 = 0; var4 < var3; var4++)
    {
      if (isBurning())
      {
        dropItem(Items.cooked_mutton, 1);
      }
      else
      {
        dropItem(Items.mutton, 1);
      }
    }
  }
  
  protected Item getDropItem()
  {
    return Item.getItemFromBlock(Blocks.wool);
  }
  
  public void handleHealthUpdate(byte p_70103_1_)
  {
    if (p_70103_1_ == 10)
    {
      sheepTimer = 40;
    }
    else
    {
      super.handleHealthUpdate(p_70103_1_);
    }
  }
  
  public float getHeadRotationPointY(float p_70894_1_)
  {
    return sheepTimer < 4 ? (sheepTimer - p_70894_1_) / 4.0F : (sheepTimer >= 4) && (sheepTimer <= 36) ? 1.0F : sheepTimer <= 0 ? 0.0F : -(sheepTimer - 40 - p_70894_1_) / 4.0F;
  }
  
  public float getHeadRotationAngleX(float p_70890_1_)
  {
    if ((sheepTimer > 4) && (sheepTimer <= 36))
    {
      float var2 = (sheepTimer - 4 - p_70890_1_) / 32.0F;
      return 0.62831855F + 0.2199115F * MathHelper.sin(var2 * 28.7F);
    }
    

    return sheepTimer > 0 ? 0.62831855F : rotationPitch / 57.295776F;
  }
  




  public boolean interact(EntityPlayer p_70085_1_)
  {
    ItemStack var2 = inventory.getCurrentItem();
    
    if ((var2 != null) && (var2.getItem() == Items.shears) && (!getSheared()) && (!isChild()))
    {
      if (!worldObj.isRemote)
      {
        setSheared(true);
        int var3 = 1 + rand.nextInt(3);
        
        for (int var4 = 0; var4 < var3; var4++)
        {
          EntityItem var5 = entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, func_175509_cj().func_176765_a()), 1.0F);
          motionY += rand.nextFloat() * 0.05F;
          motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
          motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
        }
      }
      
      var2.damageItem(1, p_70085_1_);
      playSound("mob.sheep.shear", 1.0F, 1.0F);
    }
    
    return super.interact(p_70085_1_);
  }
  



  public void writeEntityToNBT(NBTTagCompound tagCompound)
  {
    super.writeEntityToNBT(tagCompound);
    tagCompound.setBoolean("Sheared", getSheared());
    tagCompound.setByte("Color", (byte)func_175509_cj().func_176765_a());
  }
  



  public void readEntityFromNBT(NBTTagCompound tagCompund)
  {
    super.readEntityFromNBT(tagCompund);
    setSheared(tagCompund.getBoolean("Sheared"));
    func_175512_b(EnumDyeColor.func_176764_b(tagCompund.getByte("Color")));
  }
  



  protected String getLivingSound()
  {
    return "mob.sheep.say";
  }
  



  protected String getHurtSound()
  {
    return "mob.sheep.say";
  }
  



  protected String getDeathSound()
  {
    return "mob.sheep.say";
  }
  
  protected void func_180429_a(BlockPos p_180429_1_, Block p_180429_2_)
  {
    playSound("mob.sheep.step", 0.15F, 1.0F);
  }
  
  public EnumDyeColor func_175509_cj()
  {
    return EnumDyeColor.func_176764_b(dataWatcher.getWatchableObjectByte(16) & 0xF);
  }
  
  public void func_175512_b(EnumDyeColor p_175512_1_)
  {
    byte var2 = dataWatcher.getWatchableObjectByte(16);
    dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & 0xF0 | p_175512_1_.func_176765_a() & 0xF)));
  }
  



  public boolean getSheared()
  {
    return (dataWatcher.getWatchableObjectByte(16) & 0x10) != 0;
  }
  



  public void setSheared(boolean p_70893_1_)
  {
    byte var2 = dataWatcher.getWatchableObjectByte(16);
    
    if (p_70893_1_)
    {
      dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 0x10)));
    }
    else
    {
      dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & 0xFFFFFFEF)));
    }
  }
  
  public static EnumDyeColor func_175510_a(Random p_175510_0_)
  {
    int var1 = p_175510_0_.nextInt(100);
    return p_175510_0_.nextInt(500) == 0 ? EnumDyeColor.PINK : var1 < 18 ? EnumDyeColor.BROWN : var1 < 15 ? EnumDyeColor.SILVER : var1 < 10 ? EnumDyeColor.GRAY : var1 < 5 ? EnumDyeColor.BLACK : EnumDyeColor.WHITE;
  }
  
  public EntitySheep func_180491_b(EntityAgeable p_180491_1_)
  {
    EntitySheep var2 = (EntitySheep)p_180491_1_;
    EntitySheep var3 = new EntitySheep(worldObj);
    var3.func_175512_b(func_175511_a(this, var2));
    return var3;
  }
  




  public void eatGrassBonus()
  {
    setSheared(false);
    
    if (isChild())
    {
      addGrowth(60);
    }
  }
  
  public IEntityLivingData func_180482_a(DifficultyInstance p_180482_1_, IEntityLivingData p_180482_2_)
  {
    p_180482_2_ = super.func_180482_a(p_180482_1_, p_180482_2_);
    func_175512_b(func_175510_a(worldObj.rand));
    return p_180482_2_;
  }
  
  private EnumDyeColor func_175511_a(EntityAnimal p_175511_1_, EntityAnimal p_175511_2_)
  {
    int var3 = ((EntitySheep)p_175511_1_).func_175509_cj().getDyeColorDamage();
    int var4 = ((EntitySheep)p_175511_2_).func_175509_cj().getDyeColorDamage();
    inventoryCrafting.getStackInSlot(0).setItemDamage(var3);
    inventoryCrafting.getStackInSlot(1).setItemDamage(var4);
    ItemStack var5 = CraftingManager.getInstance().findMatchingRecipe(inventoryCrafting, worldObj);
    int var6;
    int var6;
    if ((var5 != null) && (var5.getItem() == Items.dye))
    {
      var6 = var5.getMetadata();
    }
    else
    {
      var6 = worldObj.rand.nextBoolean() ? var3 : var4;
    }
    
    return EnumDyeColor.func_176766_a(var6);
  }
  
  public float getEyeHeight()
  {
    return 0.95F * height;
  }
  
  public EntityAgeable createChild(EntityAgeable p_90011_1_)
  {
    return func_180491_b(p_90011_1_);
  }
  
  static
  {
    field_175514_bm.put(EnumDyeColor.WHITE, new float[] { 1.0F, 1.0F, 1.0F });
    field_175514_bm.put(EnumDyeColor.ORANGE, new float[] { 0.85F, 0.5F, 0.2F });
    field_175514_bm.put(EnumDyeColor.MAGENTA, new float[] { 0.7F, 0.3F, 0.85F });
    field_175514_bm.put(EnumDyeColor.LIGHT_BLUE, new float[] { 0.4F, 0.6F, 0.85F });
    field_175514_bm.put(EnumDyeColor.YELLOW, new float[] { 0.9F, 0.9F, 0.2F });
    field_175514_bm.put(EnumDyeColor.LIME, new float[] { 0.5F, 0.8F, 0.1F });
    field_175514_bm.put(EnumDyeColor.PINK, new float[] { 0.95F, 0.5F, 0.65F });
    field_175514_bm.put(EnumDyeColor.GRAY, new float[] { 0.3F, 0.3F, 0.3F });
    field_175514_bm.put(EnumDyeColor.SILVER, new float[] { 0.6F, 0.6F, 0.6F });
    field_175514_bm.put(EnumDyeColor.CYAN, new float[] { 0.3F, 0.5F, 0.6F });
    field_175514_bm.put(EnumDyeColor.PURPLE, new float[] { 0.5F, 0.25F, 0.7F });
    field_175514_bm.put(EnumDyeColor.BLUE, new float[] { 0.2F, 0.3F, 0.7F });
    field_175514_bm.put(EnumDyeColor.BROWN, new float[] { 0.4F, 0.3F, 0.2F });
    field_175514_bm.put(EnumDyeColor.GREEN, new float[] { 0.4F, 0.5F, 0.2F });
    field_175514_bm.put(EnumDyeColor.RED, new float[] { 0.6F, 0.2F, 0.2F });
    field_175514_bm.put(EnumDyeColor.BLACK, new float[] { 0.1F, 0.1F, 0.1F });
  }
}
