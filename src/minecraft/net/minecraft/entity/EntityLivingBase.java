package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;

public abstract class EntityLivingBase extends Entity
{
  private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
  private static final AttributeModifier sprintingSpeedBoostModifier = new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896D, 2).setSaved(false);
  private BaseAttributeMap attributeMap;
  private final CombatTracker _combatTracker = new CombatTracker(this);
  private final Map activePotionsMap = Maps.newHashMap();
  

  private final ItemStack[] previousEquipment = new ItemStack[5];
  

  public boolean isSwingInProgress;
  

  public int swingProgressInt;
  

  public int arrowHitTimer;
  

  public int hurtTime;
  

  public int maxHurtTime;
  

  public float attackedAtYaw;
  
  public int deathTime;
  
  public float prevSwingProgress;
  
  public float swingProgress;
  
  public float prevLimbSwingAmount;
  
  public float limbSwingAmount;
  
  public float limbSwing;
  
  public int maxHurtResistantTime = 20;
  
  public float prevCameraPitch;
  
  public float cameraPitch;
  
  public float field_70769_ao;
  
  public float field_70770_ap;
  
  public float renderYawOffset;
  
  public float prevRenderYawOffset;
  
  public float rotationYawHead;
  
  public float prevRotationYawHead;
  public float jumpMovementFactor = 0.02F;
  

  protected EntityPlayer attackingPlayer;
  

  protected int recentlyHit;
  

  protected boolean dead;
  

  protected int entityAge;
  

  protected float field_70768_au;
  

  protected float field_110154_aX;
  

  protected float field_70764_aw;
  

  protected float field_70763_ax;
  

  protected float field_70741_aB;
  

  protected int scoreValue;
  

  protected float lastDamage;
  

  protected boolean isJumping;
  

  public float moveStrafing;
  
  public float moveForward;
  
  protected float randomYawVelocity;
  
  protected int newPosRotationIncrements;
  
  protected double newPosX;
  
  protected double newPosY;
  
  protected double newPosZ;
  
  protected double newRotationYaw;
  
  protected double newRotationPitch;
  
  private boolean potionsNeedUpdate = true;
  

  private EntityLivingBase entityLivingToAttack;
  

  private int revengeTimer;
  
  private EntityLivingBase lastAttacker;
  
  private int lastAttackerTime;
  
  private float landMovementFactor;
  
  private int jumpTicks;
  
  private float field_110151_bq;
  
  private static final String __OBFID = "CL_00001549";
  

  public void func_174812_G()
  {
    attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
  }
  
  public EntityLivingBase(World worldIn)
  {
    super(worldIn);
    applyEntityAttributes();
    setHealth(getMaxHealth());
    preventEntitySpawning = true;
    field_70770_ap = ((float)((Math.random() + 1.0D) * 0.009999999776482582D));
    setPosition(posX, posY, posZ);
    field_70769_ao = ((float)Math.random() * 12398.0F);
    rotationYaw = ((float)(Math.random() * 3.141592653589793D * 2.0D));
    rotationYawHead = rotationYaw;
    stepHeight = 0.6F;
  }
  
  protected void entityInit()
  {
    dataWatcher.addObject(7, Integer.valueOf(0));
    dataWatcher.addObject(8, Byte.valueOf((byte)0));
    dataWatcher.addObject(9, Byte.valueOf((byte)0));
    dataWatcher.addObject(6, Float.valueOf(1.0F));
  }
  
  protected void applyEntityAttributes()
  {
    getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
    getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
    getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
  }
  
  protected void func_180433_a(double p_180433_1_, boolean p_180433_3_, Block p_180433_4_, BlockPos p_180433_5_)
  {
    if (!isInWater())
    {
      handleWaterMovement();
    }
    
    if ((!worldObj.isRemote) && (fallDistance > 3.0F) && (p_180433_3_))
    {
      IBlockState var6 = worldObj.getBlockState(p_180433_5_);
      Block var7 = var6.getBlock();
      float var8 = MathHelper.ceiling_float_int(fallDistance - 3.0F);
      
      if (var7.getMaterial() != Material.air)
      {
        double var9 = Math.min(0.2F + var8 / 15.0F, 10.0F);
        
        if (var9 > 2.5D)
        {
          var9 = 2.5D;
        }
        
        int var11 = (int)(150.0D * var9);
        ((WorldServer)worldObj).func_175739_a(EnumParticleTypes.BLOCK_DUST, posX, posY, posZ, var11, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, new int[] { Block.getStateId(var6) });
      }
    }
    
    super.func_180433_a(p_180433_1_, p_180433_3_, p_180433_4_, p_180433_5_);
  }
  
  public boolean canBreatheUnderwater()
  {
    return false;
  }
  



  public void onEntityUpdate()
  {
    prevSwingProgress = swingProgress;
    super.onEntityUpdate();
    worldObj.theProfiler.startSection("livingEntityBaseTick");
    boolean var1 = this instanceof EntityPlayer;
    
    if (isEntityAlive())
    {
      if (isEntityInsideOpaqueBlock())
      {
        attackEntityFrom(DamageSource.inWall, 1.0F);
      }
      else if ((var1) && (!worldObj.getWorldBorder().contains(getEntityBoundingBox())))
      {
        double var2 = worldObj.getWorldBorder().getClosestDistance(this) + worldObj.getWorldBorder().getDamageBuffer();
        
        if (var2 < 0.0D)
        {
          attackEntityFrom(DamageSource.inWall, Math.max(1, MathHelper.floor_double(-var2 * worldObj.getWorldBorder().func_177727_n())));
        }
      }
    }
    
    if ((isImmuneToFire()) || (worldObj.isRemote))
    {
      extinguish();
    }
    
    boolean var7 = (var1) && (capabilities.disableDamage);
    
    if ((isEntityAlive()) && (isInsideOfMaterial(Material.water)))
    {
      if ((!canBreatheUnderwater()) && (!isPotionActive(waterBreathingid)) && (!var7))
      {
        setAir(decreaseAirSupply(getAir()));
        
        if (getAir() == -20)
        {
          setAir(0);
          
          for (int var3 = 0; var3 < 8; var3++)
          {
            float var4 = rand.nextFloat() - rand.nextFloat();
            float var5 = rand.nextFloat() - rand.nextFloat();
            float var6 = rand.nextFloat() - rand.nextFloat();
            worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX + var4, posY + var5, posZ + var6, motionX, motionY, motionZ, new int[0]);
          }
          
          attackEntityFrom(DamageSource.drown, 2.0F);
        }
      }
      
      if ((!worldObj.isRemote) && (isRiding()) && ((ridingEntity instanceof EntityLivingBase)))
      {
        mountEntity(null);
      }
    }
    else
    {
      setAir(300);
    }
    
    if ((isEntityAlive()) && (isWet()))
    {
      extinguish();
    }
    
    prevCameraPitch = cameraPitch;
    
    if (hurtTime > 0)
    {
      hurtTime -= 1;
    }
    
    if ((hurtResistantTime > 0) && (!(this instanceof net.minecraft.entity.player.EntityPlayerMP)))
    {
      hurtResistantTime -= 1;
    }
    
    if (getHealth() <= 0.0F)
    {
      onDeathUpdate();
    }
    
    if (recentlyHit > 0)
    {
      recentlyHit -= 1;
    }
    else
    {
      attackingPlayer = null;
    }
    
    if ((lastAttacker != null) && (!lastAttacker.isEntityAlive()))
    {
      lastAttacker = null;
    }
    
    if (entityLivingToAttack != null)
    {
      if (!entityLivingToAttack.isEntityAlive())
      {
        setRevengeTarget(null);
      }
      else if (ticksExisted - revengeTimer > 100)
      {
        setRevengeTarget(null);
      }
    }
    
    updatePotionEffects();
    field_70763_ax = field_70764_aw;
    prevRenderYawOffset = renderYawOffset;
    prevRotationYawHead = rotationYawHead;
    prevRotationYaw = rotationYaw;
    prevRotationPitch = rotationPitch;
    worldObj.theProfiler.endSection();
  }
  



  public boolean isChild()
  {
    return false;
  }
  



  protected void onDeathUpdate()
  {
    deathTime += 1;
    
    if (deathTime == 20)
    {


      if ((!worldObj.isRemote) && ((recentlyHit > 0) || (isPlayer())) && (func_146066_aG()) && (worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")))
      {
        int var1 = getExperiencePoints(attackingPlayer);
        
        while (var1 > 0)
        {
          int var2 = EntityXPOrb.getXPSplit(var1);
          var1 -= var2;
          worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj, posX, posY, posZ, var2));
        }
      }
      
      setDead();
      
      for (int var1 = 0; var1 < 20; var1++)
      {
        double var8 = rand.nextGaussian() * 0.02D;
        double var4 = rand.nextGaussian() * 0.02D;
        double var6 = rand.nextGaussian() * 0.02D;
        worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX + rand.nextFloat() * width * 2.0F - width, posY + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, var8, var4, var6, new int[0]);
      }
    }
  }
  
  protected boolean func_146066_aG()
  {
    return !isChild();
  }
  



  protected int decreaseAirSupply(int p_70682_1_)
  {
    int var2 = EnchantmentHelper.func_180319_a(this);
    return (var2 > 0) && (rand.nextInt(var2 + 1) > 0) ? p_70682_1_ : p_70682_1_ - 1;
  }
  



  protected int getExperiencePoints(EntityPlayer p_70693_1_)
  {
    return 0;
  }
  



  protected boolean isPlayer()
  {
    return false;
  }
  
  public Random getRNG()
  {
    return rand;
  }
  
  public EntityLivingBase getAITarget()
  {
    return entityLivingToAttack;
  }
  
  public int getRevengeTimer()
  {
    return revengeTimer;
  }
  
  public void setRevengeTarget(EntityLivingBase p_70604_1_)
  {
    entityLivingToAttack = p_70604_1_;
    revengeTimer = ticksExisted;
  }
  
  public EntityLivingBase getLastAttacker()
  {
    return lastAttacker;
  }
  
  public int getLastAttackerTime()
  {
    return lastAttackerTime;
  }
  
  public void setLastAttacker(Entity p_130011_1_)
  {
    if ((p_130011_1_ instanceof EntityLivingBase))
    {
      lastAttacker = ((EntityLivingBase)p_130011_1_);
    }
    else
    {
      lastAttacker = null;
    }
    
    lastAttackerTime = ticksExisted;
  }
  
  public int getAge()
  {
    return entityAge;
  }
  



  public void writeEntityToNBT(NBTTagCompound tagCompound)
  {
    tagCompound.setFloat("HealF", getHealth());
    tagCompound.setShort("Health", (short)(int)Math.ceil(getHealth()));
    tagCompound.setShort("HurtTime", (short)hurtTime);
    tagCompound.setInteger("HurtByTimestamp", revengeTimer);
    tagCompound.setShort("DeathTime", (short)deathTime);
    tagCompound.setFloat("AbsorptionAmount", getAbsorptionAmount());
    ItemStack[] var2 = getInventory();
    int var3 = var2.length;
    


    for (int var4 = 0; var4 < var3; var4++)
    {
      ItemStack var5 = var2[var4];
      
      if (var5 != null)
      {
        attributeMap.removeAttributeModifiers(var5.getAttributeModifiers());
      }
    }
    
    tagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(getAttributeMap()));
    var2 = getInventory();
    var3 = var2.length;
    
    for (var4 = 0; var4 < var3; var4++)
    {
      ItemStack var5 = var2[var4];
      
      if (var5 != null)
      {
        attributeMap.applyAttributeModifiers(var5.getAttributeModifiers());
      }
    }
    
    if (!activePotionsMap.isEmpty())
    {
      NBTTagList var6 = new NBTTagList();
      Iterator var7 = activePotionsMap.values().iterator();
      
      while (var7.hasNext())
      {
        PotionEffect var8 = (PotionEffect)var7.next();
        var6.appendTag(var8.writeCustomPotionEffectToNBT(new NBTTagCompound()));
      }
      
      tagCompound.setTag("ActiveEffects", var6);
    }
  }
  



  public void readEntityFromNBT(NBTTagCompound tagCompund)
  {
    setAbsorptionAmount(tagCompund.getFloat("AbsorptionAmount"));
    
    if ((tagCompund.hasKey("Attributes", 9)) && (worldObj != null) && (!worldObj.isRemote))
    {
      SharedMonsterAttributes.func_151475_a(getAttributeMap(), tagCompund.getTagList("Attributes", 10));
    }
    
    if (tagCompund.hasKey("ActiveEffects", 9))
    {
      NBTTagList var2 = tagCompund.getTagList("ActiveEffects", 10);
      
      for (int var3 = 0; var3 < var2.tagCount(); var3++)
      {
        NBTTagCompound var4 = var2.getCompoundTagAt(var3);
        PotionEffect var5 = PotionEffect.readCustomPotionEffectFromNBT(var4);
        
        if (var5 != null)
        {
          activePotionsMap.put(Integer.valueOf(var5.getPotionID()), var5);
        }
      }
    }
    
    if (tagCompund.hasKey("HealF", 99))
    {
      setHealth(tagCompund.getFloat("HealF"));
    }
    else
    {
      NBTBase var6 = tagCompund.getTag("Health");
      
      if (var6 == null)
      {
        setHealth(getMaxHealth());
      }
      else if (var6.getId() == 5)
      {
        setHealth(((NBTTagFloat)var6).getFloat());
      }
      else if (var6.getId() == 2)
      {
        setHealth(((NBTTagShort)var6).getShort());
      }
    }
    
    hurtTime = tagCompund.getShort("HurtTime");
    deathTime = tagCompund.getShort("DeathTime");
    revengeTimer = tagCompund.getInteger("HurtByTimestamp");
  }
  
  protected void updatePotionEffects()
  {
    Iterator var1 = activePotionsMap.keySet().iterator();
    
    while (var1.hasNext())
    {
      Integer var2 = (Integer)var1.next();
      PotionEffect var3 = (PotionEffect)activePotionsMap.get(var2);
      
      if (!var3.onUpdate(this))
      {
        if (!worldObj.isRemote)
        {
          var1.remove();
          onFinishedPotionEffect(var3);
        }
      }
      else if (var3.getDuration() % 600 == 0)
      {
        onChangedPotionEffect(var3, false);
      }
    }
    
    if (potionsNeedUpdate)
    {
      if (!worldObj.isRemote)
      {
        func_175135_B();
      }
      
      potionsNeedUpdate = false;
    }
    
    int var11 = dataWatcher.getWatchableObjectInt(7);
    boolean var12 = dataWatcher.getWatchableObjectByte(8) > 0;
    
    if (var11 > 0)
    {
      boolean var4 = false;
      
      if (!isInvisible())
      {
        var4 = rand.nextBoolean();
      }
      else
      {
        var4 = rand.nextInt(15) == 0;
      }
      
      if (var12)
      {
        var4 &= rand.nextInt(5) == 0;
      }
      
      if ((var4) && (var11 > 0))
      {
        double var5 = (var11 >> 16 & 0xFF) / 255.0D;
        double var7 = (var11 >> 8 & 0xFF) / 255.0D;
        double var9 = (var11 >> 0 & 0xFF) / 255.0D;
        worldObj.spawnParticle(var12 ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, var5, var7, var9, new int[0]);
      }
    }
  }
  
  protected void func_175135_B()
  {
    if (activePotionsMap.isEmpty())
    {
      func_175133_bi();
      setInvisible(false);
    }
    else
    {
      int var1 = PotionHelper.calcPotionLiquidColor(activePotionsMap.values());
      dataWatcher.updateObject(8, Byte.valueOf((byte)(PotionHelper.func_82817_b(activePotionsMap.values()) ? 1 : 0)));
      dataWatcher.updateObject(7, Integer.valueOf(var1));
      setInvisible(isPotionActive(invisibilityid));
    }
  }
  
  protected void func_175133_bi()
  {
    dataWatcher.updateObject(8, Byte.valueOf((byte)0));
    dataWatcher.updateObject(7, Integer.valueOf(0));
  }
  
  public void clearActivePotions()
  {
    Iterator var1 = activePotionsMap.keySet().iterator();
    
    while (var1.hasNext())
    {
      Integer var2 = (Integer)var1.next();
      PotionEffect var3 = (PotionEffect)activePotionsMap.get(var2);
      
      if (!worldObj.isRemote)
      {
        var1.remove();
        onFinishedPotionEffect(var3);
      }
    }
  }
  
  public Collection getActivePotionEffects()
  {
    return activePotionsMap.values();
  }
  
  public boolean isPotionActive(int p_82165_1_)
  {
    return activePotionsMap.containsKey(Integer.valueOf(p_82165_1_));
  }
  
  public boolean isPotionActive(Potion p_70644_1_)
  {
    return activePotionsMap.containsKey(Integer.valueOf(id));
  }
  



  public PotionEffect getActivePotionEffect(Potion p_70660_1_)
  {
    return (PotionEffect)activePotionsMap.get(Integer.valueOf(id));
  }
  



  public void addPotionEffect(PotionEffect p_70690_1_)
  {
    if (isPotionApplicable(p_70690_1_))
    {
      if (activePotionsMap.containsKey(Integer.valueOf(p_70690_1_.getPotionID())))
      {
        ((PotionEffect)activePotionsMap.get(Integer.valueOf(p_70690_1_.getPotionID()))).combine(p_70690_1_);
        onChangedPotionEffect((PotionEffect)activePotionsMap.get(Integer.valueOf(p_70690_1_.getPotionID())), true);
      }
      else
      {
        activePotionsMap.put(Integer.valueOf(p_70690_1_.getPotionID()), p_70690_1_);
        onNewPotionEffect(p_70690_1_);
      }
    }
  }
  
  public boolean isPotionApplicable(PotionEffect p_70687_1_)
  {
    if (getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
    {
      int var2 = p_70687_1_.getPotionID();
      
      if ((var2 == regenerationid) || (var2 == poisonid))
      {
        return false;
      }
    }
    
    return true;
  }
  



  public boolean isEntityUndead()
  {
    return getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
  }
  



  public void removePotionEffectClient(int p_70618_1_)
  {
    activePotionsMap.remove(Integer.valueOf(p_70618_1_));
  }
  



  public void removePotionEffect(int p_82170_1_)
  {
    PotionEffect var2 = (PotionEffect)activePotionsMap.remove(Integer.valueOf(p_82170_1_));
    
    if (var2 != null)
    {
      onFinishedPotionEffect(var2);
    }
  }
  
  protected void onNewPotionEffect(PotionEffect p_70670_1_)
  {
    potionsNeedUpdate = true;
    
    if (!worldObj.isRemote)
    {
      Potion.potionTypes[p_70670_1_.getPotionID()].applyAttributesModifiersToEntity(this, getAttributeMap(), p_70670_1_.getAmplifier());
    }
  }
  
  protected void onChangedPotionEffect(PotionEffect p_70695_1_, boolean p_70695_2_)
  {
    potionsNeedUpdate = true;
    
    if ((p_70695_2_) && (!worldObj.isRemote))
    {
      Potion.potionTypes[p_70695_1_.getPotionID()].removeAttributesModifiersFromEntity(this, getAttributeMap(), p_70695_1_.getAmplifier());
      Potion.potionTypes[p_70695_1_.getPotionID()].applyAttributesModifiersToEntity(this, getAttributeMap(), p_70695_1_.getAmplifier());
    }
  }
  
  protected void onFinishedPotionEffect(PotionEffect p_70688_1_)
  {
    potionsNeedUpdate = true;
    
    if (!worldObj.isRemote)
    {
      Potion.potionTypes[p_70688_1_.getPotionID()].removeAttributesModifiersFromEntity(this, getAttributeMap(), p_70688_1_.getAmplifier());
    }
  }
  



  public void heal(float p_70691_1_)
  {
    float var2 = getHealth();
    
    if (var2 > 0.0F)
    {
      setHealth(var2 + p_70691_1_);
    }
  }
  
  public final float getHealth()
  {
    return dataWatcher.getWatchableObjectFloat(6);
  }
  
  public void setHealth(float p_70606_1_)
  {
    dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(p_70606_1_, 0.0F, getMaxHealth())));
  }
  



  public boolean attackEntityFrom(DamageSource source, float amount)
  {
    if (func_180431_b(source))
    {
      return false;
    }
    if (worldObj.isRemote)
    {
      return false;
    }
    

    entityAge = 0;
    
    if (getHealth() <= 0.0F)
    {
      return false;
    }
    if ((source.isFireDamage()) && (isPotionActive(Potion.fireResistance)))
    {
      return false;
    }
    

    if (((source == DamageSource.anvil) || (source == DamageSource.fallingBlock)) && (getEquipmentInSlot(4) != null))
    {
      getEquipmentInSlot(4).damageItem((int)(amount * 4.0F + rand.nextFloat() * amount * 2.0F), this);
      amount *= 0.75F;
    }
    
    limbSwingAmount = 1.5F;
    boolean var3 = true;
    
    if (hurtResistantTime > maxHurtResistantTime / 2.0F)
    {
      if (amount <= lastDamage)
      {
        return false;
      }
      
      damageEntity(source, amount - lastDamage);
      lastDamage = amount;
      var3 = false;
    }
    else
    {
      lastDamage = amount;
      hurtResistantTime = maxHurtResistantTime;
      damageEntity(source, amount);
      hurtTime = (this.maxHurtTime = 10);
    }
    
    attackedAtYaw = 0.0F;
    Entity var4 = source.getEntity();
    
    if (var4 != null)
    {
      if ((var4 instanceof EntityLivingBase))
      {
        setRevengeTarget((EntityLivingBase)var4);
      }
      
      if ((var4 instanceof EntityPlayer))
      {
        recentlyHit = 100;
        attackingPlayer = ((EntityPlayer)var4);
      }
      else if ((var4 instanceof EntityWolf))
      {
        EntityWolf var5 = (EntityWolf)var4;
        
        if (var5.isTamed())
        {
          recentlyHit = 100;
          attackingPlayer = null;
        }
      }
    }
    
    if (var3)
    {
      worldObj.setEntityState(this, (byte)2);
      
      if (source != DamageSource.drown)
      {
        setBeenAttacked();
      }
      
      if (var4 != null)
      {
        double var9 = posX - posX;
        

        for (double var7 = posZ - posZ; var9 * var9 + var7 * var7 < 1.0E-4D; var7 = (Math.random() - Math.random()) * 0.01D)
        {
          var9 = (Math.random() - Math.random()) * 0.01D;
        }
        
        attackedAtYaw = ((float)(Math.atan2(var7, var9) * 180.0D / 3.141592653589793D - rotationYaw));
        knockBack(var4, amount, var9, var7);
      }
      else
      {
        attackedAtYaw = ((int)(Math.random() * 2.0D) * 180);
      }
    }
    


    if (getHealth() <= 0.0F)
    {
      String var10 = getDeathSound();
      
      if ((var3) && (var10 != null))
      {
        playSound(var10, getSoundVolume(), getSoundPitch());
      }
      
      onDeath(source);
    }
    else
    {
      String var10 = getHurtSound();
      
      if ((var3) && (var10 != null))
      {
        playSound(var10, getSoundVolume(), getSoundPitch());
      }
    }
    
    return true;
  }
  





  public void renderBrokenItemStack(ItemStack p_70669_1_)
  {
    playSound("random.break", 0.8F, 0.8F + worldObj.rand.nextFloat() * 0.4F);
    
    for (int var2 = 0; var2 < 5; var2++)
    {
      Vec3 var3 = new Vec3((rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
      var3 = var3.rotatePitch(-rotationPitch * 3.1415927F / 180.0F);
      var3 = var3.rotateYaw(-rotationYaw * 3.1415927F / 180.0F);
      double var4 = -rand.nextFloat() * 0.6D - 0.3D;
      Vec3 var6 = new Vec3((rand.nextFloat() - 0.5D) * 0.3D, var4, 0.6D);
      var6 = var6.rotatePitch(-rotationPitch * 3.1415927F / 180.0F);
      var6 = var6.rotateYaw(-rotationYaw * 3.1415927F / 180.0F);
      var6 = var6.addVector(posX, posY + getEyeHeight(), posZ);
      worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, xCoord, yCoord, zCoord, xCoord, yCoord + 0.05D, zCoord, new int[] { net.minecraft.item.Item.getIdFromItem(p_70669_1_.getItem()) });
    }
  }
  



  public void onDeath(DamageSource cause)
  {
    Entity var2 = cause.getEntity();
    EntityLivingBase var3 = func_94060_bK();
    
    if ((scoreValue >= 0) && (var3 != null))
    {
      var3.addToPlayerScore(this, scoreValue);
    }
    
    if (var2 != null)
    {
      var2.onKillEntity(this);
    }
    
    dead = true;
    getCombatTracker().func_94549_h();
    
    if (!worldObj.isRemote)
    {
      int var4 = 0;
      
      if ((var2 instanceof EntityPlayer))
      {
        var4 = EnchantmentHelper.getLootingModifier((EntityLivingBase)var2);
      }
      
      if ((func_146066_aG()) && (worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")))
      {
        dropFewItems(recentlyHit > 0, var4);
        dropEquipment(recentlyHit > 0, var4);
        
        if ((recentlyHit > 0) && (rand.nextFloat() < 0.025F + var4 * 0.01F))
        {
          addRandomArmor();
        }
      }
    }
    
    worldObj.setEntityState(this, (byte)3);
  }
  



  protected void dropEquipment(boolean p_82160_1_, int p_82160_2_) {}
  



  public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_)
  {
    if (rand.nextDouble() >= getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue())
    {
      isAirBorne = true;
      float var7 = MathHelper.sqrt_double(p_70653_3_ * p_70653_3_ + p_70653_5_ * p_70653_5_);
      float var8 = 0.4F;
      motionX /= 2.0D;
      motionY /= 2.0D;
      motionZ /= 2.0D;
      motionX -= p_70653_3_ / var7 * var8;
      motionY += var8;
      motionZ -= p_70653_5_ / var7 * var8;
      
      if (motionY > 0.4000000059604645D)
      {
        motionY = 0.4000000059604645D;
      }
    }
  }
  



  protected String getHurtSound()
  {
    return "game.neutral.hurt";
  }
  



  protected String getDeathSound()
  {
    return "game.neutral.die";
  }
  



  protected void addRandomArmor() {}
  



  protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {}
  



  public boolean isOnLadder()
  {
    int var1 = MathHelper.floor_double(posX);
    int var2 = MathHelper.floor_double(getEntityBoundingBoxminY);
    int var3 = MathHelper.floor_double(posZ);
    Block var4 = worldObj.getBlockState(new BlockPos(var1, var2, var3)).getBlock();
    return ((var4 == Blocks.ladder) || (var4 == Blocks.vine)) && ((!(this instanceof EntityPlayer)) || (!((EntityPlayer)this).func_175149_v()));
  }
  



  public boolean isEntityAlive()
  {
    return (!isDead) && (getHealth() > 0.0F);
  }
  
  public void fall(float distance, float damageMultiplier)
  {
    super.fall(distance, damageMultiplier);
    PotionEffect var3 = getActivePotionEffect(Potion.jump);
    float var4 = var3 != null ? var3.getAmplifier() + 1 : 0.0F;
    int var5 = MathHelper.ceiling_float_int((distance - 3.0F - var4) * damageMultiplier);
    
    if (var5 > 0)
    {
      playSound(func_146067_o(var5), 1.0F, 1.0F);
      attackEntityFrom(DamageSource.fall, var5);
      int var6 = MathHelper.floor_double(posX);
      int var7 = MathHelper.floor_double(posY - 0.20000000298023224D);
      int var8 = MathHelper.floor_double(posZ);
      Block var9 = worldObj.getBlockState(new BlockPos(var6, var7, var8)).getBlock();
      
      if (var9.getMaterial() != Material.air)
      {
        Block.SoundType var10 = stepSound;
        playSound(var10.getStepSound(), var10.getVolume() * 0.5F, var10.getFrequency() * 0.75F);
      }
    }
  }
  
  protected String func_146067_o(int p_146067_1_)
  {
    return p_146067_1_ > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
  }
  



  public void performHurtAnimation()
  {
    hurtTime = (this.maxHurtTime = 10);
    attackedAtYaw = 0.0F;
  }
  



  public int getTotalArmorValue()
  {
    int var1 = 0;
    ItemStack[] var2 = getInventory();
    int var3 = var2.length;
    
    for (int var4 = 0; var4 < var3; var4++)
    {
      ItemStack var5 = var2[var4];
      
      if ((var5 != null) && ((var5.getItem() instanceof ItemArmor)))
      {
        int var6 = getItemdamageReduceAmount;
        var1 += var6;
      }
    }
    
    return var1;
  }
  


  protected void damageArmor(float p_70675_1_) {}
  

  protected float applyArmorCalculations(DamageSource p_70655_1_, float p_70655_2_)
  {
    if (!p_70655_1_.isUnblockable())
    {
      int var3 = 25 - getTotalArmorValue();
      float var4 = p_70655_2_ * var3;
      damageArmor(p_70655_2_);
      p_70655_2_ = var4 / 25.0F;
    }
    
    return p_70655_2_;
  }
  



  protected float applyPotionDamageCalculations(DamageSource p_70672_1_, float p_70672_2_)
  {
    if (p_70672_1_.isDamageAbsolute())
    {
      return p_70672_2_;
    }
    





    if ((isPotionActive(Potion.resistance)) && (p_70672_1_ != DamageSource.outOfWorld))
    {
      int var3 = (getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
      int var4 = 25 - var3;
      float var5 = p_70672_2_ * var4;
      p_70672_2_ = var5 / 25.0F;
    }
    
    if (p_70672_2_ <= 0.0F)
    {
      return 0.0F;
    }
    

    int var3 = EnchantmentHelper.getEnchantmentModifierDamage(getInventory(), p_70672_1_);
    
    if (var3 > 20)
    {
      var3 = 20;
    }
    
    if ((var3 > 0) && (var3 <= 20))
    {
      int var4 = 25 - var3;
      float var5 = p_70672_2_ * var4;
      p_70672_2_ = var5 / 25.0F;
    }
    
    return p_70672_2_;
  }
  






  protected void damageEntity(DamageSource p_70665_1_, float p_70665_2_)
  {
    if (!func_180431_b(p_70665_1_))
    {
      p_70665_2_ = applyArmorCalculations(p_70665_1_, p_70665_2_);
      p_70665_2_ = applyPotionDamageCalculations(p_70665_1_, p_70665_2_);
      float var3 = p_70665_2_;
      p_70665_2_ = Math.max(p_70665_2_ - getAbsorptionAmount(), 0.0F);
      setAbsorptionAmount(getAbsorptionAmount() - (var3 - p_70665_2_));
      
      if (p_70665_2_ != 0.0F)
      {
        float var4 = getHealth();
        setHealth(var4 - p_70665_2_);
        getCombatTracker().func_94547_a(p_70665_1_, var4, p_70665_2_);
        setAbsorptionAmount(getAbsorptionAmount() - p_70665_2_);
      }
    }
  }
  
  public CombatTracker getCombatTracker()
  {
    return _combatTracker;
  }
  
  public EntityLivingBase func_94060_bK()
  {
    return entityLivingToAttack != null ? entityLivingToAttack : attackingPlayer != null ? attackingPlayer : _combatTracker.func_94550_c() != null ? _combatTracker.func_94550_c() : null;
  }
  
  public final float getMaxHealth()
  {
    return (float)getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
  }
  



  public final int getArrowCountInEntity()
  {
    return dataWatcher.getWatchableObjectByte(9);
  }
  



  public final void setArrowCountInEntity(int p_85034_1_)
  {
    dataWatcher.updateObject(9, Byte.valueOf((byte)p_85034_1_));
  }
  




  private int getArmSwingAnimationEnd()
  {
    return isPotionActive(Potion.digSlowdown) ? 6 + (1 + getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : isPotionActive(Potion.digSpeed) ? 6 - (1 + getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : 6;
  }
  



  public void swingItem()
  {
    if ((!isSwingInProgress) || (swingProgressInt >= getArmSwingAnimationEnd() / 2) || (swingProgressInt < 0))
    {
      swingProgressInt = -1;
      isSwingInProgress = true;
      
      if ((worldObj instanceof WorldServer))
      {
        ((WorldServer)worldObj).getEntityTracker().sendToAllTrackingEntity(this, new net.minecraft.network.play.server.S0BPacketAnimation(this, 0));
      }
    }
  }
  


  public void handleHealthUpdate(byte p_70103_1_)
  {
    if (p_70103_1_ == 2)
    {
      limbSwingAmount = 1.5F;
      hurtResistantTime = maxHurtResistantTime;
      hurtTime = (this.maxHurtTime = 10);
      attackedAtYaw = 0.0F;
      String var2 = getHurtSound();
      
      if (var2 != null)
      {
        playSound(getHurtSound(), getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
      }
      
      attackEntityFrom(DamageSource.generic, 0.0F);
    }
    else if (p_70103_1_ == 3)
    {
      String var2 = getDeathSound();
      
      if (var2 != null)
      {
        playSound(getDeathSound(), getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
      }
      
      setHealth(0.0F);
      onDeath(DamageSource.generic);
    }
    else
    {
      super.handleHealthUpdate(p_70103_1_);
    }
  }
  



  protected void kill()
  {
    attackEntityFrom(DamageSource.outOfWorld, 4.0F);
  }
  



  protected void updateArmSwingProgress()
  {
    int var1 = getArmSwingAnimationEnd();
    
    if (isSwingInProgress)
    {
      swingProgressInt += 1;
      
      if (swingProgressInt >= var1)
      {
        swingProgressInt = 0;
        isSwingInProgress = false;
      }
    }
    else
    {
      swingProgressInt = 0;
    }
    
    swingProgress = (swingProgressInt / var1);
  }
  
  public IAttributeInstance getEntityAttribute(IAttribute p_110148_1_)
  {
    return getAttributeMap().getAttributeInstance(p_110148_1_);
  }
  
  public BaseAttributeMap getAttributeMap()
  {
    if (attributeMap == null)
    {
      attributeMap = new net.minecraft.entity.ai.attributes.ServersideAttributeMap();
    }
    
    return attributeMap;
  }
  



  public EnumCreatureAttribute getCreatureAttribute()
  {
    return EnumCreatureAttribute.UNDEFINED;
  }
  



  public abstract ItemStack getHeldItem();
  



  public abstract ItemStack getEquipmentInSlot(int paramInt);
  


  public abstract ItemStack getCurrentArmor(int paramInt);
  


  public abstract void setCurrentItemOrArmor(int paramInt, ItemStack paramItemStack);
  


  public void setSprinting(boolean sprinting)
  {
    super.setSprinting(sprinting);
    IAttributeInstance var2 = getEntityAttribute(SharedMonsterAttributes.movementSpeed);
    
    if (var2.getModifier(sprintingSpeedBoostModifierUUID) != null)
    {
      var2.removeModifier(sprintingSpeedBoostModifier);
    }
    
    if (sprinting)
    {
      var2.applyModifier(sprintingSpeedBoostModifier);
    }
  }
  



  public abstract ItemStack[] getInventory();
  



  protected float getSoundVolume()
  {
    return 1.0F;
  }
  



  protected float getSoundPitch()
  {
    return isChild() ? (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.5F : (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F;
  }
  



  protected boolean isMovementBlocked()
  {
    return getHealth() <= 0.0F;
  }
  



  public void dismountEntity(Entity p_110145_1_)
  {
    double var3 = posX;
    double var5 = getEntityBoundingBoxminY + height;
    double var7 = posZ;
    byte var9 = 1;
    
    for (int var10 = -var9; var10 <= var9; var10++)
    {
      for (int var11 = -var9; var11 < var9; var11++)
      {
        if ((var10 != 0) || (var11 != 0))
        {
          int var12 = (int)(posX + var10);
          int var13 = (int)(posZ + var11);
          AxisAlignedBB var2 = getEntityBoundingBox().offset(var10, 1.0D, var11);
          
          if (worldObj.func_147461_a(var2).isEmpty())
          {
            if (World.doesBlockHaveSolidTopSurface(worldObj, new BlockPos(var12, (int)posY, var13)))
            {
              setPositionAndUpdate(posX + var10, posY + 1.0D, posZ + var11);
              return;
            }
            
            if ((World.doesBlockHaveSolidTopSurface(worldObj, new BlockPos(var12, (int)posY - 1, var13))) || (worldObj.getBlockState(new BlockPos(var12, (int)posY - 1, var13)).getBlock().getMaterial() == Material.water))
            {
              var3 = posX + var10;
              var5 = posY + 1.0D;
              var7 = posZ + var11;
            }
          }
        }
      }
    }
    
    setPositionAndUpdate(var3, var5, var7);
  }
  
  public boolean getAlwaysRenderNameTagForRender()
  {
    return false;
  }
  
  protected float func_175134_bD()
  {
    return 0.42F;
  }
  



  protected void jump()
  {
    motionY = func_175134_bD();
    
    if (isPotionActive(Potion.jump))
    {
      motionY += (getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
    }
    
    if (isSprinting())
    {
      float var1 = rotationYaw * 0.017453292F;
      motionX -= MathHelper.sin(var1) * 0.2F;
      motionZ += MathHelper.cos(var1) * 0.2F;
    }
    
    isAirBorne = true;
  }
  



  protected void updateAITick()
  {
    motionY += 0.03999999910593033D;
  }
  
  protected void func_180466_bG()
  {
    motionY += 0.03999999910593033D;
  }
  






  public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_)
  {
    if (isServerWorld())
    {



      if ((isInWater()) && ((!(this instanceof EntityPlayer)) || (!capabilities.isFlying)))
      {
        double var8 = posY;
        float var5 = 0.8F;
        float var6 = 0.02F;
        float var10 = EnchantmentHelper.func_180318_b(this);
        
        if (var10 > 3.0F)
        {
          var10 = 3.0F;
        }
        
        if (!onGround)
        {
          var10 *= 0.5F;
        }
        
        if (var10 > 0.0F)
        {
          var5 += (0.54600006F - var5) * var10 / 3.0F;
          var6 += (getAIMoveSpeed() * 1.0F - var6) * var10 / 3.0F;
        }
        
        moveFlying(p_70612_1_, p_70612_2_, var6);
        moveEntity(motionX, motionY, motionZ);
        motionX *= var5;
        motionY *= 0.800000011920929D;
        motionZ *= var5;
        motionY -= 0.02D;
        
        if ((isCollidedHorizontally) && (isOffsetPositionInLiquid(motionX, motionY + 0.6000000238418579D - posY + var8, motionZ)))
        {
          motionY = 0.30000001192092896D;
        }
      }
      else if ((func_180799_ab()) && ((!(this instanceof EntityPlayer)) || (!capabilities.isFlying)))
      {
        double var8 = posY;
        moveFlying(p_70612_1_, p_70612_2_, 0.02F);
        moveEntity(motionX, motionY, motionZ);
        motionX *= 0.5D;
        motionY *= 0.5D;
        motionZ *= 0.5D;
        motionY -= 0.02D;
        
        if ((isCollidedHorizontally) && (isOffsetPositionInLiquid(motionX, motionY + 0.6000000238418579D - posY + var8, motionZ)))
        {
          motionY = 0.30000001192092896D;
        }
      }
      else
      {
        float var3 = 0.91F;
        
        if (onGround)
        {
          var3 = worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(getEntityBoundingBoxminY) - 1, MathHelper.floor_double(posZ))).getBlock().slipperiness * 0.91F;
        }
        
        float var4 = 0.16277136F / (var3 * var3 * var3);
        float var5;
        float var5; if (onGround)
        {
          var5 = getAIMoveSpeed() * var4;
        }
        else
        {
          var5 = jumpMovementFactor;
        }
        
        moveFlying(p_70612_1_, p_70612_2_, var5);
        var3 = 0.91F;
        
        if (onGround)
        {
          var3 = worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(getEntityBoundingBoxminY) - 1, MathHelper.floor_double(posZ))).getBlock().slipperiness * 0.91F;
        }
        
        if (isOnLadder())
        {
          float var6 = 0.15F;
          motionX = MathHelper.clamp_double(motionX, -var6, var6);
          motionZ = MathHelper.clamp_double(motionZ, -var6, var6);
          fallDistance = 0.0F;
          
          if (motionY < -0.15D)
          {
            motionY = -0.15D;
          }
          
          boolean var7 = (isSneaking()) && ((this instanceof EntityPlayer));
          
          if ((var7) && (motionY < 0.0D))
          {
            motionY = 0.0D;
          }
        }
        
        moveEntity(motionX, motionY, motionZ);
        
        if ((isCollidedHorizontally) && (isOnLadder()))
        {
          motionY = 0.2D;
        }
        
        if ((worldObj.isRemote) && ((!worldObj.isBlockLoaded(new BlockPos((int)posX, 0, (int)posZ))) || (!worldObj.getChunkFromBlockCoords(new BlockPos((int)posX, 0, (int)posZ)).isLoaded())))
        {
          if (posY > 0.0D)
          {
            motionY = -0.1D;
          }
          else
          {
            motionY = 0.0D;
          }
          
        }
        else {
          motionY -= 0.08D;
        }
        
        motionY *= 0.9800000190734863D;
        motionX *= var3;
        motionZ *= var3;
      }
    }
    
    prevLimbSwingAmount = limbSwingAmount;
    double var8 = posX - prevPosX;
    double var9 = posZ - prevPosZ;
    float var10 = MathHelper.sqrt_double(var8 * var8 + var9 * var9) * 4.0F;
    
    if (var10 > 1.0F)
    {
      var10 = 1.0F;
    }
    
    limbSwingAmount += (var10 - limbSwingAmount) * 0.4F;
    limbSwing += limbSwingAmount;
  }
  



  public float getAIMoveSpeed()
  {
    return landMovementFactor;
  }
  



  public void setAIMoveSpeed(float p_70659_1_)
  {
    landMovementFactor = p_70659_1_;
  }
  
  public boolean attackEntityAsMob(Entity p_70652_1_)
  {
    setLastAttacker(p_70652_1_);
    return false;
  }
  



  public boolean isPlayerSleeping()
  {
    return false;
  }
  



  public void onUpdate()
  {
    super.onUpdate();
    
    if (!worldObj.isRemote)
    {
      int var1 = getArrowCountInEntity();
      
      if (var1 > 0)
      {
        if (arrowHitTimer <= 0)
        {
          arrowHitTimer = (20 * (30 - var1));
        }
        
        arrowHitTimer -= 1;
        
        if (arrowHitTimer <= 0)
        {
          setArrowCountInEntity(var1 - 1);
        }
      }
      
      for (int var2 = 0; var2 < 5; var2++)
      {
        ItemStack var3 = previousEquipment[var2];
        ItemStack var4 = getEquipmentInSlot(var2);
        
        if (!ItemStack.areItemStacksEqual(var4, var3))
        {
          ((WorldServer)worldObj).getEntityTracker().sendToAllTrackingEntity(this, new net.minecraft.network.play.server.S04PacketEntityEquipment(getEntityId(), var2, var4));
          
          if (var3 != null)
          {
            attributeMap.removeAttributeModifiers(var3.getAttributeModifiers());
          }
          
          if (var4 != null)
          {
            attributeMap.applyAttributeModifiers(var4.getAttributeModifiers());
          }
          
          previousEquipment[var2] = (var4 == null ? null : var4.copy());
        }
      }
      
      if (ticksExisted % 20 == 0)
      {
        getCombatTracker().func_94549_h();
      }
    }
    
    onLivingUpdate();
    double var9 = posX - prevPosX;
    double var10 = posZ - prevPosZ;
    float var5 = (float)(var9 * var9 + var10 * var10);
    float var6 = renderYawOffset;
    float var7 = 0.0F;
    field_70768_au = field_110154_aX;
    float var8 = 0.0F;
    
    if (var5 > 0.0025000002F)
    {
      var8 = 1.0F;
      var7 = (float)Math.sqrt(var5) * 3.0F;
      var6 = (float)Math.atan2(var10, var9) * 180.0F / 3.1415927F - 90.0F;
    }
    
    if (swingProgress > 0.0F)
    {
      var6 = rotationYaw;
    }
    
    if (!onGround)
    {
      var8 = 0.0F;
    }
    
    field_110154_aX += (var8 - field_110154_aX) * 0.3F;
    worldObj.theProfiler.startSection("headTurn");
    var7 = func_110146_f(var6, var7);
    worldObj.theProfiler.endSection();
    worldObj.theProfiler.startSection("rangeChecks");
    
    while (rotationYaw - prevRotationYaw < -180.0F)
    {
      prevRotationYaw -= 360.0F;
    }
    
    while (rotationYaw - prevRotationYaw >= 180.0F)
    {
      prevRotationYaw += 360.0F;
    }
    
    while (renderYawOffset - prevRenderYawOffset < -180.0F)
    {
      prevRenderYawOffset -= 360.0F;
    }
    
    while (renderYawOffset - prevRenderYawOffset >= 180.0F)
    {
      prevRenderYawOffset += 360.0F;
    }
    
    while (rotationPitch - prevRotationPitch < -180.0F)
    {
      prevRotationPitch -= 360.0F;
    }
    
    while (rotationPitch - prevRotationPitch >= 180.0F)
    {
      prevRotationPitch += 360.0F;
    }
    
    while (rotationYawHead - prevRotationYawHead < -180.0F)
    {
      prevRotationYawHead -= 360.0F;
    }
    
    while (rotationYawHead - prevRotationYawHead >= 180.0F)
    {
      prevRotationYawHead += 360.0F;
    }
    
    worldObj.theProfiler.endSection();
    field_70764_aw += var7;
  }
  
  protected float func_110146_f(float p_110146_1_, float p_110146_2_)
  {
    float var3 = MathHelper.wrapAngleTo180_float(p_110146_1_ - renderYawOffset);
    renderYawOffset += var3 * 0.3F;
    float var4 = MathHelper.wrapAngleTo180_float(rotationYaw - renderYawOffset);
    boolean var5 = (var4 < -90.0F) || (var4 >= 90.0F);
    
    if (var4 < -75.0F)
    {
      var4 = -75.0F;
    }
    
    if (var4 >= 75.0F)
    {
      var4 = 75.0F;
    }
    
    renderYawOffset = (rotationYaw - var4);
    
    if (var4 * var4 > 2500.0F)
    {
      renderYawOffset += var4 * 0.2F;
    }
    
    if (var5)
    {
      p_110146_2_ *= -1.0F;
    }
    
    return p_110146_2_;
  }
  




  public void onLivingUpdate()
  {
    if (jumpTicks > 0)
    {
      jumpTicks -= 1;
    }
    
    if (newPosRotationIncrements > 0)
    {
      double var1 = posX + (newPosX - posX) / newPosRotationIncrements;
      double var3 = posY + (newPosY - posY) / newPosRotationIncrements;
      double var5 = posZ + (newPosZ - posZ) / newPosRotationIncrements;
      double var7 = MathHelper.wrapAngleTo180_double(newRotationYaw - rotationYaw);
      rotationYaw = ((float)(rotationYaw + var7 / newPosRotationIncrements));
      rotationPitch = ((float)(rotationPitch + (newRotationPitch - rotationPitch) / newPosRotationIncrements));
      newPosRotationIncrements -= 1;
      setPosition(var1, var3, var5);
      setRotation(rotationYaw, rotationPitch);
    }
    else if (!isServerWorld())
    {
      motionX *= 0.98D;
      motionY *= 0.98D;
      motionZ *= 0.98D;
    }
    
    if (Math.abs(motionX) < 0.005D)
    {
      motionX = 0.0D;
    }
    
    if (Math.abs(motionY) < 0.005D)
    {
      motionY = 0.0D;
    }
    
    if (Math.abs(motionZ) < 0.005D)
    {
      motionZ = 0.0D;
    }
    
    worldObj.theProfiler.startSection("ai");
    
    if (isMovementBlocked())
    {
      isJumping = false;
      moveStrafing = 0.0F;
      moveForward = 0.0F;
      randomYawVelocity = 0.0F;
    }
    else if (isServerWorld())
    {
      worldObj.theProfiler.startSection("newAi");
      updateEntityActionState();
      worldObj.theProfiler.endSection();
    }
    
    worldObj.theProfiler.endSection();
    worldObj.theProfiler.startSection("jump");
    
    if (isJumping)
    {
      if (isInWater())
      {
        updateAITick();
      }
      else if (func_180799_ab())
      {
        func_180466_bG();
      }
      else if ((onGround) && (jumpTicks == 0))
      {
        jump();
        jumpTicks = 10;
      }
      
    }
    else {
      jumpTicks = 0;
    }
    
    worldObj.theProfiler.endSection();
    worldObj.theProfiler.startSection("travel");
    moveStrafing *= 0.98F;
    moveForward *= 0.98F;
    randomYawVelocity *= 0.9F;
    moveEntityWithHeading(moveStrafing, moveForward);
    worldObj.theProfiler.endSection();
    worldObj.theProfiler.startSection("push");
    
    if (!worldObj.isRemote)
    {
      collideWithNearbyEntities();
    }
    
    worldObj.theProfiler.endSection();
  }
  
  protected void updateEntityActionState() {}
  
  protected void collideWithNearbyEntities()
  {
    List var1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
    
    if ((var1 != null) && (!var1.isEmpty()))
    {
      for (int var2 = 0; var2 < var1.size(); var2++)
      {
        Entity var3 = (Entity)var1.get(var2);
        
        if (var3.canBePushed())
        {
          collideWithEntity(var3);
        }
      }
    }
  }
  
  protected void collideWithEntity(Entity p_82167_1_)
  {
    p_82167_1_.applyEntityCollision(this);
  }
  



  public void mountEntity(Entity entityIn)
  {
    if ((ridingEntity != null) && (entityIn == null))
    {
      if (!worldObj.isRemote)
      {
        dismountEntity(ridingEntity);
      }
      
      if (ridingEntity != null)
      {
        ridingEntity.riddenByEntity = null;
      }
      
      ridingEntity = null;
    }
    else
    {
      super.mountEntity(entityIn);
    }
  }
  



  public void updateRidden()
  {
    super.updateRidden();
    field_70768_au = field_110154_aX;
    field_110154_aX = 0.0F;
    fallDistance = 0.0F;
  }
  
  public void func_180426_a(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_)
  {
    newPosX = p_180426_1_;
    newPosY = p_180426_3_;
    newPosZ = p_180426_5_;
    newRotationYaw = p_180426_7_;
    newRotationPitch = p_180426_8_;
    newPosRotationIncrements = p_180426_9_;
  }
  
  public void setJumping(boolean p_70637_1_)
  {
    isJumping = p_70637_1_;
  }
  



  public void onItemPickup(Entity p_71001_1_, int p_71001_2_)
  {
    if ((!isDead) && (!worldObj.isRemote))
    {
      EntityTracker var3 = ((WorldServer)worldObj).getEntityTracker();
      
      if ((p_71001_1_ instanceof EntityItem))
      {
        var3.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), getEntityId()));
      }
      
      if ((p_71001_1_ instanceof net.minecraft.entity.projectile.EntityArrow))
      {
        var3.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), getEntityId()));
      }
      
      if ((p_71001_1_ instanceof EntityXPOrb))
      {
        var3.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), getEntityId()));
      }
    }
  }
  



  public boolean canEntityBeSeen(Entity p_70685_1_)
  {
    return worldObj.rayTraceBlocks(new Vec3(posX, posY + getEyeHeight(), posZ), new Vec3(posX, posY + p_70685_1_.getEyeHeight(), posZ)) == null;
  }
  



  public Vec3 getLookVec()
  {
    return getLook(1.0F);
  }
  



  public Vec3 getLook(float p_70676_1_)
  {
    if (p_70676_1_ == 1.0F)
    {
      return func_174806_f(rotationPitch, rotationYawHead);
    }
    

    float var2 = prevRotationPitch + (rotationPitch - prevRotationPitch) * p_70676_1_;
    float var3 = prevRotationYawHead + (rotationYawHead - prevRotationYawHead) * p_70676_1_;
    return func_174806_f(var2, var3);
  }
  




  public float getSwingProgress(float p_70678_1_)
  {
    float var2 = swingProgress - prevSwingProgress;
    
    if (var2 < 0.0F)
    {
      var2 += 1.0F;
    }
    
    return prevSwingProgress + var2 * p_70678_1_;
  }
  



  public boolean isServerWorld()
  {
    return !worldObj.isRemote;
  }
  



  public boolean canBeCollidedWith()
  {
    return !isDead;
  }
  



  public boolean canBePushed()
  {
    return !isDead;
  }
  



  protected void setBeenAttacked()
  {
    velocityChanged = (rand.nextDouble() >= getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue());
  }
  
  public float getRotationYawHead()
  {
    return rotationYawHead;
  }
  



  public void setRotationYawHead(float rotation)
  {
    rotationYawHead = rotation;
  }
  
  public float getAbsorptionAmount()
  {
    return field_110151_bq;
  }
  
  public void setAbsorptionAmount(float p_110149_1_)
  {
    if (p_110149_1_ < 0.0F)
    {
      p_110149_1_ = 0.0F;
    }
    
    field_110151_bq = p_110149_1_;
  }
  
  public Team getTeam()
  {
    return worldObj.getScoreboard().getPlayersTeam(getUniqueID().toString());
  }
  
  public boolean isOnSameTeam(EntityLivingBase p_142014_1_)
  {
    return isOnTeam(p_142014_1_.getTeam());
  }
  



  public boolean isOnTeam(Team p_142012_1_)
  {
    return getTeam() != null ? getTeam().isSameTeam(p_142012_1_) : false;
  }
  
  public void func_152111_bt() {}
  
  public void func_152112_bu() {}
  
  protected void func_175136_bO()
  {
    potionsNeedUpdate = true;
  }
}
