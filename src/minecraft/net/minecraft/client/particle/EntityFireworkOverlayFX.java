package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkOverlayFX extends EntityFX
{
  private static final String __OBFID = "CL_00000904";
  
  protected EntityFireworkOverlayFX(World worldIn, double p_i46357_2_, double p_i46357_4_, double p_i46357_6_)
  {
    super(worldIn, p_i46357_2_, p_i46357_4_, p_i46357_6_);
    particleMaxAge = 4;
  }
  
  public void func_180434_a(WorldRenderer p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_)
  {
    float var9 = 0.25F;
    float var10 = var9 + 0.25F;
    float var11 = 0.125F;
    float var12 = var11 + 0.25F;
    float var13 = 7.1F * MathHelper.sin((particleAge + p_180434_3_ - 1.0F) * 0.25F * 3.1415927F);
    particleAlpha = (0.6F - (particleAge + p_180434_3_ - 1.0F) * 0.25F * 0.5F);
    float var14 = (float)(prevPosX + (posX - prevPosX) * p_180434_3_ - interpPosX);
    float var15 = (float)(prevPosY + (posY - prevPosY) * p_180434_3_ - interpPosY);
    float var16 = (float)(prevPosZ + (posZ - prevPosZ) * p_180434_3_ - interpPosZ);
    p_180434_1_.func_178960_a(particleRed, particleGreen, particleBlue, particleAlpha);
    p_180434_1_.addVertexWithUV(var14 - p_180434_4_ * var13 - p_180434_7_ * var13, var15 - p_180434_5_ * var13, var16 - p_180434_6_ * var13 - p_180434_8_ * var13, var10, var12);
    p_180434_1_.addVertexWithUV(var14 - p_180434_4_ * var13 + p_180434_7_ * var13, var15 + p_180434_5_ * var13, var16 - p_180434_6_ * var13 + p_180434_8_ * var13, var10, var11);
    p_180434_1_.addVertexWithUV(var14 + p_180434_4_ * var13 + p_180434_7_ * var13, var15 + p_180434_5_ * var13, var16 + p_180434_6_ * var13 + p_180434_8_ * var13, var9, var11);
    p_180434_1_.addVertexWithUV(var14 + p_180434_4_ * var13 - p_180434_7_ * var13, var15 - p_180434_5_ * var13, var16 + p_180434_6_ * var13 - p_180434_8_ * var13, var9, var12);
  }
}
