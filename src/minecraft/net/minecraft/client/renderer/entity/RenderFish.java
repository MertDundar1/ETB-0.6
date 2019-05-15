package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderFish extends Render
{
  private static final ResourceLocation field_110792_a = new ResourceLocation("textures/particle/particles.png");
  private static final String __OBFID = "CL_00000996";
  
  public RenderFish(RenderManager p_i46175_1_)
  {
    super(p_i46175_1_);
  }
  
  public void func_180558_a(EntityFishHook p_180558_1_, double p_180558_2_, double p_180558_4_, double p_180558_6_, float p_180558_8_, float p_180558_9_)
  {
    GlStateManager.pushMatrix();
    GlStateManager.translate((float)p_180558_2_, (float)p_180558_4_, (float)p_180558_6_);
    GlStateManager.enableRescaleNormal();
    GlStateManager.scale(0.5F, 0.5F, 0.5F);
    bindEntityTexture(p_180558_1_);
    Tessellator var10 = Tessellator.getInstance();
    WorldRenderer var11 = var10.getWorldRenderer();
    byte var12 = 1;
    byte var13 = 2;
    float var14 = (var12 * 8 + 0) / 128.0F;
    float var15 = (var12 * 8 + 8) / 128.0F;
    float var16 = (var13 * 8 + 0) / 128.0F;
    float var17 = (var13 * 8 + 8) / 128.0F;
    float var18 = 1.0F;
    float var19 = 0.5F;
    float var20 = 0.5F;
    GlStateManager.rotate(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    var11.startDrawingQuads();
    var11.func_178980_d(0.0F, 1.0F, 0.0F);
    var11.addVertexWithUV(0.0F - var19, 0.0F - var20, 0.0D, var14, var17);
    var11.addVertexWithUV(var18 - var19, 0.0F - var20, 0.0D, var15, var17);
    var11.addVertexWithUV(var18 - var19, 1.0F - var20, 0.0D, var15, var16);
    var11.addVertexWithUV(0.0F - var19, 1.0F - var20, 0.0D, var14, var16);
    var10.draw();
    GlStateManager.disableRescaleNormal();
    GlStateManager.popMatrix();
    
    if (angler != null)
    {
      float var21 = angler.getSwingProgress(p_180558_9_);
      float var22 = MathHelper.sin(MathHelper.sqrt_float(var21) * 3.1415927F);
      Vec3 var23 = new Vec3(-0.36D, 0.03D, 0.35D);
      var23 = var23.rotatePitch(-(angler.prevRotationPitch + (angler.rotationPitch - angler.prevRotationPitch) * p_180558_9_) * 3.1415927F / 180.0F);
      var23 = var23.rotateYaw(-(angler.prevRotationYaw + (angler.rotationYaw - angler.prevRotationYaw) * p_180558_9_) * 3.1415927F / 180.0F);
      var23 = var23.rotateYaw(var22 * 0.5F);
      var23 = var23.rotatePitch(-var22 * 0.7F);
      double var24 = angler.prevPosX + (angler.posX - angler.prevPosX) * p_180558_9_ + xCoord;
      double var26 = angler.prevPosY + (angler.posY - angler.prevPosY) * p_180558_9_ + yCoord;
      double var28 = angler.prevPosZ + (angler.posZ - angler.prevPosZ) * p_180558_9_ + zCoord;
      double var30 = angler.getEyeHeight();
      
      if (((renderManager.options != null) && (renderManager.options.thirdPersonView > 0)) || (angler != getMinecraftthePlayer))
      {
        float var32 = (angler.prevRenderYawOffset + (angler.renderYawOffset - angler.prevRenderYawOffset) * p_180558_9_) * 3.1415927F / 180.0F;
        double var33 = MathHelper.sin(var32);
        double var35 = MathHelper.cos(var32);
        double var37 = 0.35D;
        double var39 = 0.8D;
        var24 = angler.prevPosX + (angler.posX - angler.prevPosX) * p_180558_9_ - var35 * 0.35D - var33 * 0.8D;
        var26 = angler.prevPosY + var30 + (angler.posY - angler.prevPosY) * p_180558_9_ - 0.45D;
        var28 = angler.prevPosZ + (angler.posZ - angler.prevPosZ) * p_180558_9_ - var33 * 0.35D + var35 * 0.8D;
        var30 = angler.isSneaking() ? -0.1875D : 0.0D;
      }
      
      double var47 = prevPosX + (posX - prevPosX) * p_180558_9_;
      double var34 = prevPosY + (posY - prevPosY) * p_180558_9_ + 0.25D;
      double var36 = prevPosZ + (posZ - prevPosZ) * p_180558_9_;
      double var38 = (float)(var24 - var47);
      double var40 = (float)(var26 - var34) + var30;
      double var42 = (float)(var28 - var36);
      GlStateManager.func_179090_x();
      GlStateManager.disableLighting();
      var11.startDrawing(3);
      var11.func_178991_c(0);
      byte var44 = 16;
      
      for (int var45 = 0; var45 <= var44; var45++)
      {
        float var46 = var45 / var44;
        var11.addVertex(p_180558_2_ + var38 * var46, p_180558_4_ + var40 * (var46 * var46 + var46) * 0.5D + 0.25D, p_180558_6_ + var42 * var46);
      }
      
      var10.draw();
      GlStateManager.enableLighting();
      GlStateManager.func_179098_w();
      super.doRender(p_180558_1_, p_180558_2_, p_180558_4_, p_180558_6_, p_180558_8_, p_180558_9_);
    }
  }
  



  protected ResourceLocation getEntityTexture(EntityFishHook p_110775_1_)
  {
    return field_110792_a;
  }
  



  protected ResourceLocation getEntityTexture(Entity p_110775_1_)
  {
    return getEntityTexture((EntityFishHook)p_110775_1_);
  }
  






  public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
  {
    func_180558_a((EntityFishHook)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }
}
