package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelQuadruped extends ModelBase
{
  public ModelRenderer head = new ModelRenderer(this, 0, 0);
  public ModelRenderer body;
  public ModelRenderer leg1;
  public ModelRenderer leg2;
  public ModelRenderer leg3;
  public ModelRenderer leg4;
  protected float childYOffset = 8.0F;
  protected float childZOffset = 4.0F;
  private static final String __OBFID = "CL_00000851";
  
  public ModelQuadruped(int p_i1154_1_, float p_i1154_2_)
  {
    head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, p_i1154_2_);
    head.setRotationPoint(0.0F, 18 - p_i1154_1_, -6.0F);
    body = new ModelRenderer(this, 28, 8);
    body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, p_i1154_2_);
    body.setRotationPoint(0.0F, 17 - p_i1154_1_, 2.0F);
    leg1 = new ModelRenderer(this, 0, 16);
    leg1.addBox(-2.0F, 0.0F, -2.0F, 4, p_i1154_1_, 4, p_i1154_2_);
    leg1.setRotationPoint(-3.0F, 24 - p_i1154_1_, 7.0F);
    leg2 = new ModelRenderer(this, 0, 16);
    leg2.addBox(-2.0F, 0.0F, -2.0F, 4, p_i1154_1_, 4, p_i1154_2_);
    leg2.setRotationPoint(3.0F, 24 - p_i1154_1_, 7.0F);
    leg3 = new ModelRenderer(this, 0, 16);
    leg3.addBox(-2.0F, 0.0F, -2.0F, 4, p_i1154_1_, 4, p_i1154_2_);
    leg3.setRotationPoint(-3.0F, 24 - p_i1154_1_, -5.0F);
    leg4 = new ModelRenderer(this, 0, 16);
    leg4.addBox(-2.0F, 0.0F, -2.0F, 4, p_i1154_1_, 4, p_i1154_2_);
    leg4.setRotationPoint(3.0F, 24 - p_i1154_1_, -5.0F);
  }
  



  public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
  {
    setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
    
    if (isChild)
    {
      float var8 = 2.0F;
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, childYOffset * p_78088_7_, childZOffset * p_78088_7_);
      head.render(p_78088_7_);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(1.0F / var8, 1.0F / var8, 1.0F / var8);
      GlStateManager.translate(0.0F, 24.0F * p_78088_7_, 0.0F);
      body.render(p_78088_7_);
      leg1.render(p_78088_7_);
      leg2.render(p_78088_7_);
      leg3.render(p_78088_7_);
      leg4.render(p_78088_7_);
      GlStateManager.popMatrix();
    }
    else
    {
      head.render(p_78088_7_);
      body.render(p_78088_7_);
      leg1.render(p_78088_7_);
      leg2.render(p_78088_7_);
      leg3.render(p_78088_7_);
      leg4.render(p_78088_7_);
    }
  }
  





  public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
  {
    float var8 = 57.295776F;
    head.rotateAngleX = (p_78087_5_ / 57.295776F);
    head.rotateAngleY = (p_78087_4_ / 57.295776F);
    body.rotateAngleX = 1.5707964F;
    leg1.rotateAngleX = (MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_);
    leg2.rotateAngleX = (MathHelper.cos(p_78087_1_ * 0.6662F + 3.1415927F) * 1.4F * p_78087_2_);
    leg3.rotateAngleX = (MathHelper.cos(p_78087_1_ * 0.6662F + 3.1415927F) * 1.4F * p_78087_2_);
    leg4.rotateAngleX = (MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_);
  }
}
