package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityBreakingFX extends EntityFX
{
  private static final String __OBFID = "CL_00000897";
  
  protected EntityBreakingFX(World worldIn, double p_i1195_2_, double p_i1195_4_, double p_i1195_6_, Item p_i1195_8_)
  {
    this(worldIn, p_i1195_2_, p_i1195_4_, p_i1195_6_, p_i1195_8_, 0);
  }
  
  protected EntityBreakingFX(World worldIn, double p_i1197_2_, double p_i1197_4_, double p_i1197_6_, double p_i1197_8_, double p_i1197_10_, double p_i1197_12_, Item p_i1197_14_, int p_i1197_15_)
  {
    this(worldIn, p_i1197_2_, p_i1197_4_, p_i1197_6_, p_i1197_14_, p_i1197_15_);
    motionX *= 0.10000000149011612D;
    motionY *= 0.10000000149011612D;
    motionZ *= 0.10000000149011612D;
    motionX += p_i1197_8_;
    motionY += p_i1197_10_;
    motionZ += p_i1197_12_;
  }
  
  protected EntityBreakingFX(World worldIn, double p_i1196_2_, double p_i1196_4_, double p_i1196_6_, Item p_i1196_8_, int p_i1196_9_)
  {
    super(worldIn, p_i1196_2_, p_i1196_4_, p_i1196_6_, 0.0D, 0.0D, 0.0D);
    func_180435_a(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(p_i1196_8_, p_i1196_9_));
    particleRed = (this.particleGreen = this.particleBlue = 1.0F);
    particleGravity = snowblockParticleGravity;
    particleScale /= 2.0F;
  }
  
  public int getFXLayer()
  {
    return 1;
  }
  
  public void func_180434_a(WorldRenderer p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_)
  {
    float var9 = (particleTextureIndexX + particleTextureJitterX / 4.0F) / 16.0F;
    float var10 = var9 + 0.015609375F;
    float var11 = (particleTextureIndexY + particleTextureJitterY / 4.0F) / 16.0F;
    float var12 = var11 + 0.015609375F;
    float var13 = 0.1F * particleScale;
    
    if (particleIcon != null)
    {
      var9 = particleIcon.getInterpolatedU(particleTextureJitterX / 4.0F * 16.0F);
      var10 = particleIcon.getInterpolatedU((particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
      var11 = particleIcon.getInterpolatedV(particleTextureJitterY / 4.0F * 16.0F);
      var12 = particleIcon.getInterpolatedV((particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
    }
    
    float var14 = (float)(prevPosX + (posX - prevPosX) * p_180434_3_ - interpPosX);
    float var15 = (float)(prevPosY + (posY - prevPosY) * p_180434_3_ - interpPosY);
    float var16 = (float)(prevPosZ + (posZ - prevPosZ) * p_180434_3_ - interpPosZ);
    p_180434_1_.func_178986_b(particleRed, particleGreen, particleBlue);
    p_180434_1_.addVertexWithUV(var14 - p_180434_4_ * var13 - p_180434_7_ * var13, var15 - p_180434_5_ * var13, var16 - p_180434_6_ * var13 - p_180434_8_ * var13, var9, var12);
    p_180434_1_.addVertexWithUV(var14 - p_180434_4_ * var13 + p_180434_7_ * var13, var15 + p_180434_5_ * var13, var16 - p_180434_6_ * var13 + p_180434_8_ * var13, var9, var11);
    p_180434_1_.addVertexWithUV(var14 + p_180434_4_ * var13 + p_180434_7_ * var13, var15 + p_180434_5_ * var13, var16 + p_180434_6_ * var13 + p_180434_8_ * var13, var10, var11);
    p_180434_1_.addVertexWithUV(var14 + p_180434_4_ * var13 - p_180434_7_ * var13, var15 - p_180434_5_ * var13, var16 + p_180434_6_ * var13 - p_180434_8_ * var13, var10, var12);
  }
  
  public static class Factory implements IParticleFactory {
    private static final String __OBFID = "CL_00002613";
    
    public Factory() {}
    
    public EntityFX func_178902_a(int p_178902_1_, World worldIn, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_) {
      int var16 = p_178902_15_.length > 1 ? p_178902_15_[1] : 0;
      return new EntityBreakingFX(worldIn, p_178902_3_, p_178902_5_, p_178902_7_, p_178902_9_, p_178902_11_, p_178902_13_, Item.getItemById(p_178902_15_[0]), var16);
    }
  }
  
  public static class SlimeFactory implements IParticleFactory {
    private static final String __OBFID = "CL_00002612";
    
    public SlimeFactory() {}
    
    public EntityFX func_178902_a(int p_178902_1_, World worldIn, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_) {
      return new EntityBreakingFX(worldIn, p_178902_3_, p_178902_5_, p_178902_7_, Items.slime_ball);
    }
  }
  
  public static class SnowballFactory implements IParticleFactory {
    private static final String __OBFID = "CL_00002611";
    
    public SnowballFactory() {}
    
    public EntityFX func_178902_a(int p_178902_1_, World worldIn, double p_178902_3_, double p_178902_5_, double p_178902_7_, double p_178902_9_, double p_178902_11_, double p_178902_13_, int... p_178902_15_) {
      return new EntityBreakingFX(worldIn, p_178902_3_, p_178902_5_, p_178902_7_, Items.snowball);
    }
  }
}
