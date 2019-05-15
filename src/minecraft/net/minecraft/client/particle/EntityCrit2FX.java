package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCrit2FX extends EntityFX
{
  float field_174839_a;
  private static final String __OBFID = "CL_00000899";
  
  protected EntityCrit2FX(World worldIn, double p_i46284_2_, double p_i46284_4_, double p_i46284_6_, double p_i46284_8_, double p_i46284_10_, double p_i46284_12_)
  {
    this(worldIn, p_i46284_2_, p_i46284_4_, p_i46284_6_, p_i46284_8_, p_i46284_10_, p_i46284_12_, 1.0F);
  }
  
  protected EntityCrit2FX(World worldIn, double p_i46285_2_, double p_i46285_4_, double p_i46285_6_, double p_i46285_8_, double p_i46285_10_, double p_i46285_12_, float p_i46285_14_)
  {
    super(worldIn, p_i46285_2_, p_i46285_4_, p_i46285_6_, 0.0D, 0.0D, 0.0D);
    motionX *= 0.10000000149011612D;
    motionY *= 0.10000000149011612D;
    motionZ *= 0.10000000149011612D;
    motionX += p_i46285_8_ * 0.4D;
    motionY += p_i46285_10_ * 0.4D;
    motionZ += p_i46285_12_ * 0.4D;
    particleRed = (this.particleGreen = this.particleBlue = (float)(Math.random() * 0.30000001192092896D + 0.6000000238418579D));
    particleScale *= 0.75F;
    particleScale *= p_i46285_14_;
    field_174839_a = particleScale;
    particleMaxAge = ((int)(6.0D / (Math.random() * 0.8D + 0.6D)));
    particleMaxAge = ((int)(particleMaxAge * p_i46285_14_));
    noClip = false;
    setParticleTextureIndex(65);
    onUpdate();
  }
  
  public void func_180434_a(WorldRenderer p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_)
  {
    float var9 = (particleAge + p_180434_3_) / particleMaxAge * 32.0F;
    var9 = MathHelper.clamp_float(var9, 0.0F, 1.0F);
    particleScale = (field_174839_a * var9);
    super.func_180434_a(p_180434_1_, p_180434_2_, p_180434_3_, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
  }
  



  public void onUpdate()
  {
    prevPosX = posX;
    prevPosY = posY;
    prevPosZ = posZ;
    
    if (particleAge++ >= particleMaxAge)
    {
      setDead();
    }
    
    moveEntity(motionX, motionY, motionZ);
    particleGreen = ((float)(particleGreen * 0.96D));
    particleBlue = ((float)(particleBlue * 0.9D));
    motionX *= 0.699999988079071D;
    motionY *= 0.699999988079071D;
    motionZ *= 0.699999988079071D;
    motionY -= 0.019999999552965164D;
    
    if (onGround)
    {
      motionX *= 0.699999988079071D;
      motionZ *= 0.699999988079071D;
    }
  }
  
  public static class Factory implements IParticleFactory {
    private static final String __OBFID = "CL_00002608";
    
    public Factory() {}
    
    public EntityFX func_178902_a(int p_178902_1_, World worldIn, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_) {
      return new EntityCrit2FX(worldIn, p_178902_3_, p_178902_5_, p_178902_7_, p_178902_9_, p_178902_11_, p_178902_13_);
    }
  }
  
  public static class MagicFactory implements IParticleFactory {
    private static final String __OBFID = "CL_00002609";
    
    public MagicFactory() {}
    
    public EntityFX func_178902_a(int p_178902_1_, World worldIn, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_) {
      EntityCrit2FX var16 = new EntityCrit2FX(worldIn, p_178902_3_, p_178902_5_, p_178902_7_, p_178902_9_, p_178902_11_, p_178902_13_);
      var16.setRBGColorF(var16.getRedColorF() * 0.3F, var16.getGreenColorF() * 0.8F, var16.getBlueColorF());
      var16.nextTextureIndexX();
      return var16;
    }
  }
}
