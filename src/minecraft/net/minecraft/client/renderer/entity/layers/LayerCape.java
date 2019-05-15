package net.minecraft.client.renderer.entity.layers;

import com.enjoytheban.api.EventBus;
import com.enjoytheban.api.events.rendering.EventRenderCape;
import java.awt.Color;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class LayerCape implements LayerRenderer
{
  private final RenderPlayer playerRenderer;
  private static final String __OBFID = "CL_00002425";
  
  public LayerCape(RenderPlayer p_i46123_1_)
  {
    playerRenderer = p_i46123_1_;
  }
  

  public void doRenderLayer(AbstractClientPlayer p_177166_1_, float p_177166_2_, float p_177166_3_, float p_177166_4_, float p_177166_5_, float p_177166_6_, float p_177166_7_, float p_177166_8_)
  {
    EventRenderCape event = new EventRenderCape(p_177166_1_.getLocationCape(), p_177166_1_);
    EventBus.getInstance().call(event);
    if ((p_177166_1_.getLocationCape() != event.getLocation()) && (event.getLocation() != null) && 
      (p_177166_1_.func_175148_a(EnumPlayerModelParts.CAPE))) {
      Color color = com.enjoytheban.utils.render.ColorUtils.rainbow(0L, 1.0F);
      GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
      playerRenderer.bindTexture(event.getLocation());
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 0.0F, 0.125F);
      double var9 = field_71091_bM + 
        (field_71094_bP - field_71091_bM) * p_177166_4_ - (
        prevPosX + (posX - prevPosX) * p_177166_4_);
      double var10 = field_71096_bN + 
        (field_71095_bQ - field_71096_bN) * p_177166_4_ - (
        prevPosY + (posY - prevPosY) * p_177166_4_);
      double var11 = field_71097_bO + 
        (field_71085_bR - field_71097_bO) * p_177166_4_ - (
        prevPosZ + (posZ - prevPosZ) * p_177166_4_);
      float var12 = prevRenderYawOffset + 
        (renderYawOffset - prevRenderYawOffset) * p_177166_4_;
      double var13 = MathHelper.sin(var12 * 3.1415927F / 180.0F);
      double var14 = -MathHelper.cos(var12 * 3.1415927F / 180.0F);
      float var15 = (float)var10 * 10.0F;
      var15 = MathHelper.clamp_float(var15, -6.0F, 32.0F);
      float var16 = (float)(var9 * var13 + var11 * var14) * 100.0F;
      float var17 = (float)(var9 * var14 - var11 * var13) * 100.0F;
      if (var16 < 0.0F) {
        var16 = 0.0F;
      }
      if (var16 > 165.0F) {
        var16 = 165.0F;
      }
      float var18 = prevCameraYaw + 
        (cameraYaw - prevCameraYaw) * p_177166_4_;
      var15 += MathHelper.sin((prevDistanceWalkedModified + 
        (distanceWalkedModified - prevDistanceWalkedModified) * p_177166_4_) * 
        6.0F) * 32.0F * var18;
      if (p_177166_1_.isSneaking()) {
        var15 += 25.0F;
        GlStateManager.translate(0.0F, 0.142F, -0.0178F);
      }
      GlStateManager.rotate(6.0F + var16 / 2.0F + var15, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(var17 / 2.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-var17 / 2.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      playerRenderer.func_177136_g().func_178728_c(0.0625F);
      GlStateManager.popMatrix();
    }
    if (event.isCancelled()) {
      return;
    }
    if ((p_177166_1_.hasCape()) && (!p_177166_1_.isInvisible()) && (p_177166_1_.func_175148_a(EnumPlayerModelParts.CAPE)) && 
      (p_177166_1_.getLocationCape() != null)) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      playerRenderer.bindTexture(p_177166_1_.getLocationCape());
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 0.0F, 0.125F);
      double var19 = field_71091_bM + 
        (field_71094_bP - field_71091_bM) * p_177166_4_ - (
        prevPosX + (posX - prevPosX) * p_177166_4_);
      double var20 = field_71096_bN + 
        (field_71095_bQ - field_71096_bN) * p_177166_4_ - (
        prevPosY + (posY - prevPosY) * p_177166_4_);
      double var21 = field_71097_bO + 
        (field_71085_bR - field_71097_bO) * p_177166_4_ - (
        prevPosZ + (posZ - prevPosZ) * p_177166_4_);
      float var22 = prevRenderYawOffset + 
        (renderYawOffset - prevRenderYawOffset) * p_177166_4_;
      double var23 = MathHelper.sin(var22 * 3.1415927F / 180.0F);
      double var24 = -MathHelper.cos(var22 * 3.1415927F / 180.0F);
      float var25 = (float)var20 * 10.0F;
      var25 = MathHelper.clamp_float(var25, -6.0F, 32.0F);
      float var26 = (float)(var19 * var23 + var21 * var24) * 100.0F;
      float var27 = (float)(var19 * var24 - var21 * var23) * 100.0F;
      if (var26 < 0.0F) {
        var26 = 0.0F;
      }
      if (var26 > 165.0F) {
        var26 = 165.0F;
      }
      float var28 = prevCameraYaw + 
        (cameraYaw - prevCameraYaw) * p_177166_4_;
      var25 += MathHelper.sin((prevDistanceWalkedModified + 
        (distanceWalkedModified - prevDistanceWalkedModified) * p_177166_4_) * 
        6.0F) * 32.0F * var28;
      if (p_177166_1_.isSneaking()) {
        var25 += 25.0F;
        GlStateManager.translate(0.0F, 0.142F, -0.0178F);
      }
      GlStateManager.rotate(6.0F + var26 / 2.0F + var25, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(var27 / 2.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-var27 / 2.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      playerRenderer.func_177136_g().func_178728_c(0.0625F);
      GlStateManager.popMatrix();
    }
  }
  
  public boolean shouldCombineTextures() {
    return false;
  }
  
  public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_)
  {
    doRenderLayer((AbstractClientPlayer)p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, 
      p_177141_6_, p_177141_7_, p_177141_8_);
  }
}
