package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkStarterFX extends EntityFX
{
  private int fireworkAge;
  private final EffectRenderer theEffectRenderer;
  private NBTTagList fireworkExplosions;
  boolean twinkle;
  private static final String __OBFID = "CL_00000906";
  
  public EntityFireworkStarterFX(World worldIn, double p_i46355_2_, double p_i46355_4_, double p_i46355_6_, double p_i46355_8_, double p_i46355_10_, double p_i46355_12_, EffectRenderer p_i46355_14_, NBTTagCompound p_i46355_15_)
  {
    super(worldIn, p_i46355_2_, p_i46355_4_, p_i46355_6_, 0.0D, 0.0D, 0.0D);
    motionX = p_i46355_8_;
    motionY = p_i46355_10_;
    motionZ = p_i46355_12_;
    theEffectRenderer = p_i46355_14_;
    particleMaxAge = 8;
    
    if (p_i46355_15_ != null)
    {
      fireworkExplosions = p_i46355_15_.getTagList("Explosions", 10);
      
      if (fireworkExplosions.tagCount() == 0)
      {
        fireworkExplosions = null;
      }
      else
      {
        particleMaxAge = (fireworkExplosions.tagCount() * 2 - 1);
        
        for (int var16 = 0; var16 < fireworkExplosions.tagCount(); var16++)
        {
          NBTTagCompound var17 = fireworkExplosions.getCompoundTagAt(var16);
          
          if (var17.getBoolean("Flicker"))
          {
            twinkle = true;
            particleMaxAge += 15;
            break;
          }
        }
      }
    }
  }
  



  public void func_180434_a(WorldRenderer p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {}
  


  public void onUpdate()
  {
    if ((fireworkAge == 0) && (fireworkExplosions != null))
    {
      boolean var1 = func_92037_i();
      boolean var2 = false;
      
      if (fireworkExplosions.tagCount() >= 3)
      {
        var2 = true;
      }
      else
      {
        for (int var3 = 0; var3 < fireworkExplosions.tagCount(); var3++)
        {
          NBTTagCompound var4 = fireworkExplosions.getCompoundTagAt(var3);
          
          if (var4.getByte("Type") == 1)
          {
            var2 = true;
            break;
          }
        }
      }
      
      String var16 = "fireworks." + (var2 ? "largeBlast" : "blast") + (var1 ? "_far" : "");
      worldObj.playSound(posX, posY, posZ, var16, 20.0F, 0.95F + rand.nextFloat() * 0.1F, true);
    }
    
    if ((fireworkAge % 2 == 0) && (fireworkExplosions != null) && (fireworkAge / 2 < fireworkExplosions.tagCount()))
    {
      int var13 = fireworkAge / 2;
      NBTTagCompound var14 = fireworkExplosions.getCompoundTagAt(var13);
      byte var17 = var14.getByte("Type");
      boolean var18 = var14.getBoolean("Trail");
      boolean var5 = var14.getBoolean("Flicker");
      int[] var6 = var14.getIntArray("Colors");
      int[] var7 = var14.getIntArray("FadeColors");
      
      if (var6.length == 0)
      {
        var6 = new int[] { net.minecraft.item.ItemDye.dyeColors[0] };
      }
      
      if (var17 == 1)
      {
        createBall(0.5D, 4, var6, var7, var18, var5);
      }
      else if (var17 == 2)
      {
        createShaped(0.5D, new double[][] { { 0.0D, 1.0D }, { 0.3455D, 0.309D }, { 0.9511D, 0.309D }, { 0.3795918367346939D, -0.12653061224489795D }, { 0.6122448979591837D, -0.8040816326530612D }, { 0.0D, -0.35918367346938773D } }, var6, var7, var18, var5, false);
      }
      else if (var17 == 3)
      {
        createShaped(0.5D, new double[][] { { 0.0D, 0.2D }, { 0.2D, 0.2D }, { 0.2D, 0.6D }, { 0.6D, 0.6D }, { 0.6D, 0.2D }, { 0.2D, 0.2D }, { 0.2D, 0.0D }, { 0.4D, 0.0D }, { 0.4D, -0.6D }, { 0.2D, -0.6D }, { 0.2D, -0.4D }, { 0.0D, -0.4D } }, var6, var7, var18, var5, true);
      }
      else if (var17 == 4)
      {
        createBurst(var6, var7, var18, var5);
      }
      else
      {
        createBall(0.25D, 2, var6, var7, var18, var5);
      }
      
      int var8 = var6[0];
      float var9 = ((var8 & 0xFF0000) >> 16) / 255.0F;
      float var10 = ((var8 & 0xFF00) >> 8) / 255.0F;
      float var11 = ((var8 & 0xFF) >> 0) / 255.0F;
      EntityFireworkOverlayFX var12 = new EntityFireworkOverlayFX(worldObj, posX, posY, posZ);
      var12.setRBGColorF(var9, var10, var11);
      theEffectRenderer.addEffect(var12);
    }
    
    fireworkAge += 1;
    
    if (fireworkAge > particleMaxAge)
    {
      if (twinkle)
      {
        boolean var1 = func_92037_i();
        String var15 = "fireworks." + (var1 ? "twinkle_far" : "twinkle");
        worldObj.playSound(posX, posY, posZ, var15, 20.0F, 0.9F + rand.nextFloat() * 0.15F, true);
      }
      
      setDead();
    }
  }
  
  private boolean func_92037_i()
  {
    Minecraft var1 = Minecraft.getMinecraft();
    return (var1 == null) || (var1.func_175606_aa() == null) || (var1.func_175606_aa().getDistanceSq(posX, posY, posZ) >= 256.0D);
  }
  




  private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_)
  {
    EntityFireworkSparkFX var17 = new EntityFireworkSparkFX(worldObj, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_, theEffectRenderer);
    var17.setAlphaF(0.99F);
    var17.setTrail(p_92034_15_);
    var17.setTwinkle(p_92034_16_);
    int var18 = rand.nextInt(p_92034_13_.length);
    var17.setColour(p_92034_13_[var18]);
    
    if ((p_92034_14_ != null) && (p_92034_14_.length > 0))
    {
      var17.setFadeColour(p_92034_14_[rand.nextInt(p_92034_14_.length)]);
    }
    
    theEffectRenderer.addEffect(var17);
  }
  




  private void createBall(double p_92035_1_, int p_92035_3_, int[] p_92035_4_, int[] p_92035_5_, boolean p_92035_6_, boolean p_92035_7_)
  {
    double var8 = posX;
    double var10 = posY;
    double var12 = posZ;
    
    for (int var14 = -p_92035_3_; var14 <= p_92035_3_; var14++)
    {
      for (int var15 = -p_92035_3_; var15 <= p_92035_3_; var15++)
      {
        for (int var16 = -p_92035_3_; var16 <= p_92035_3_; var16++)
        {
          double var17 = var15 + (rand.nextDouble() - rand.nextDouble()) * 0.5D;
          double var19 = var14 + (rand.nextDouble() - rand.nextDouble()) * 0.5D;
          double var21 = var16 + (rand.nextDouble() - rand.nextDouble()) * 0.5D;
          double var23 = MathHelper.sqrt_double(var17 * var17 + var19 * var19 + var21 * var21) / p_92035_1_ + rand.nextGaussian() * 0.05D;
          createParticle(var8, var10, var12, var17 / var23, var19 / var23, var21 / var23, p_92035_4_, p_92035_5_, p_92035_6_, p_92035_7_);
          
          if ((var14 != -p_92035_3_) && (var14 != p_92035_3_) && (var15 != -p_92035_3_) && (var15 != p_92035_3_))
          {
            var16 += p_92035_3_ * 2 - 1;
          }
        }
      }
    }
  }
  




  private void createShaped(double p_92038_1_, double[][] p_92038_3_, int[] p_92038_4_, int[] p_92038_5_, boolean p_92038_6_, boolean p_92038_7_, boolean p_92038_8_)
  {
    double var9 = p_92038_3_[0][0];
    double var11 = p_92038_3_[0][1];
    createParticle(posX, posY, posZ, var9 * p_92038_1_, var11 * p_92038_1_, 0.0D, p_92038_4_, p_92038_5_, p_92038_6_, p_92038_7_);
    float var13 = rand.nextFloat() * 3.1415927F;
    double var14 = p_92038_8_ ? 0.034D : 0.34D;
    
    for (int var16 = 0; var16 < 3; var16++)
    {
      double var17 = var13 + var16 * 3.1415927F * var14;
      double var19 = var9;
      double var21 = var11;
      
      for (int var23 = 1; var23 < p_92038_3_.length; var23++)
      {
        double var24 = p_92038_3_[var23][0];
        double var26 = p_92038_3_[var23][1];
        
        for (double var28 = 0.25D; var28 <= 1.0D; var28 += 0.25D)
        {
          double var30 = (var19 + (var24 - var19) * var28) * p_92038_1_;
          double var32 = (var21 + (var26 - var21) * var28) * p_92038_1_;
          double var34 = var30 * Math.sin(var17);
          var30 *= Math.cos(var17);
          
          for (double var36 = -1.0D; var36 <= 1.0D; var36 += 2.0D)
          {
            createParticle(posX, posY, posZ, var30 * var36, var32, var34 * var36, p_92038_4_, p_92038_5_, p_92038_6_, p_92038_7_);
          }
        }
        
        var19 = var24;
        var21 = var26;
      }
    }
  }
  



  private void createBurst(int[] p_92036_1_, int[] p_92036_2_, boolean p_92036_3_, boolean p_92036_4_)
  {
    double var5 = rand.nextGaussian() * 0.05D;
    double var7 = rand.nextGaussian() * 0.05D;
    
    for (int var9 = 0; var9 < 70; var9++)
    {
      double var10 = motionX * 0.5D + rand.nextGaussian() * 0.15D + var5;
      double var12 = motionZ * 0.5D + rand.nextGaussian() * 0.15D + var7;
      double var14 = motionY * 0.5D + rand.nextDouble() * 0.5D;
      createParticle(posX, posY, posZ, var10, var14, var12, p_92036_1_, p_92036_2_, p_92036_3_, p_92036_4_);
    }
  }
  
  public int getFXLayer()
  {
    return 0;
  }
}
