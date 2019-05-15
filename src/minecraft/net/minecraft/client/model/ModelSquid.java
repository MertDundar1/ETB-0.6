package net.minecraft.client.model;

import net.minecraft.entity.Entity;



public class ModelSquid
  extends ModelBase
{
  ModelRenderer squidBody;
  ModelRenderer[] squidTentacles = new ModelRenderer[8];
  private static final String __OBFID = "CL_00000861";
  
  public ModelSquid()
  {
    byte var1 = -16;
    squidBody = new ModelRenderer(this, 0, 0);
    squidBody.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
    squidBody.rotationPointY += 24 + var1;
    
    for (int var2 = 0; var2 < squidTentacles.length; var2++)
    {
      squidTentacles[var2] = new ModelRenderer(this, 48, 0);
      double var3 = var2 * 3.141592653589793D * 2.0D / squidTentacles.length;
      float var5 = (float)Math.cos(var3) * 5.0F;
      float var6 = (float)Math.sin(var3) * 5.0F;
      squidTentacles[var2].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
      squidTentacles[var2].rotationPointX = var5;
      squidTentacles[var2].rotationPointZ = var6;
      squidTentacles[var2].rotationPointY = (31 + var1);
      var3 = var2 * 3.141592653589793D * -2.0D / squidTentacles.length + 1.5707963267948966D;
      squidTentacles[var2].rotateAngleY = ((float)var3);
    }
  }
  





  public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
  {
    ModelRenderer[] var8 = squidTentacles;
    int var9 = var8.length;
    
    for (int var10 = 0; var10 < var9; var10++)
    {
      ModelRenderer var11 = var8[var10];
      rotateAngleX = p_78087_3_;
    }
  }
  



  public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
  {
    setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
    squidBody.render(p_78088_7_);
    
    for (int var8 = 0; var8 < squidTentacles.length; var8++)
    {
      squidTentacles[var8].render(p_78088_7_);
    }
  }
}
