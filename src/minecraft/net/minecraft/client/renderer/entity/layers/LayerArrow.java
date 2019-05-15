package net.minecraft.client.renderer.entity.layers;

import java.util.List;
import java.util.Random;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;

public class LayerArrow implements LayerRenderer
{
  private final RendererLivingEntity field_177168_a;
  private static final String __OBFID = "CL_00002426";
  
  public LayerArrow(RendererLivingEntity p_i46124_1_)
  {
    field_177168_a = p_i46124_1_;
  }
  
  public void doRenderLayer(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_)
  {
    int var9 = p_177141_1_.getArrowCountInEntity();
    
    if (var9 > 0)
    {
      EntityArrow var10 = new EntityArrow(worldObj, posX, posY, posZ);
      Random var11 = new Random(p_177141_1_.getEntityId());
      RenderHelper.disableStandardItemLighting();
      
      for (int var12 = 0; var12 < var9; var12++)
      {
        GlStateManager.pushMatrix();
        ModelRenderer var13 = field_177168_a.getMainModel().getRandomModelBox(var11);
        ModelBox var14 = (ModelBox)cubeList.get(var11.nextInt(cubeList.size()));
        var13.postRender(0.0625F);
        float var15 = var11.nextFloat();
        float var16 = var11.nextFloat();
        float var17 = var11.nextFloat();
        float var18 = (posX1 + (posX2 - posX1) * var15) / 16.0F;
        float var19 = (posY1 + (posY2 - posY1) * var16) / 16.0F;
        float var20 = (posZ1 + (posZ2 - posZ1) * var17) / 16.0F;
        GlStateManager.translate(var18, var19, var20);
        var15 = var15 * 2.0F - 1.0F;
        var16 = var16 * 2.0F - 1.0F;
        var17 = var17 * 2.0F - 1.0F;
        var15 *= -1.0F;
        var16 *= -1.0F;
        var17 *= -1.0F;
        float var21 = net.minecraft.util.MathHelper.sqrt_float(var15 * var15 + var17 * var17);
        prevRotationYaw = (var10.rotationYaw = (float)(Math.atan2(var15, var17) * 180.0D / 3.141592653589793D));
        prevRotationPitch = (var10.rotationPitch = (float)(Math.atan2(var16, var21) * 180.0D / 3.141592653589793D));
        double var22 = 0.0D;
        double var24 = 0.0D;
        double var26 = 0.0D;
        field_177168_a.func_177068_d().renderEntityWithPosYaw(var10, var22, var24, var26, 0.0F, p_177141_4_);
        GlStateManager.popMatrix();
      }
      
      RenderHelper.enableStandardItemLighting();
    }
  }
  
  public boolean shouldCombineTextures()
  {
    return false;
  }
}
