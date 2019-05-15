package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBlaze
  extends ModelBase
{
  private ModelRenderer[] blazeSticks = new ModelRenderer[12];
  private ModelRenderer blazeHead;
  private static final String __OBFID = "CL_00000831";
  
  public ModelBlaze()
  {
    for (int var1 = 0; var1 < blazeSticks.length; var1++)
    {
      blazeSticks[var1] = new ModelRenderer(this, 0, 16);
      blazeSticks[var1].addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
    }
    
    blazeHead = new ModelRenderer(this, 0, 0);
    blazeHead.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
  }
  



  public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
  {
    setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
    blazeHead.render(p_78088_7_);
    
    for (int var8 = 0; var8 < blazeSticks.length; var8++)
    {
      blazeSticks[var8].render(p_78088_7_);
    }
  }
  





  public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
  {
    float var8 = p_78087_3_ * 3.1415927F * -0.1F;
    

    for (int var9 = 0; var9 < 4; var9++)
    {
      blazeSticks[var9].rotationPointY = (-2.0F + MathHelper.cos((var9 * 2 + p_78087_3_) * 0.25F));
      blazeSticks[var9].rotationPointX = (MathHelper.cos(var8) * 9.0F);
      blazeSticks[var9].rotationPointZ = (MathHelper.sin(var8) * 9.0F);
      var8 += 1.0F;
    }
    
    var8 = 0.7853982F + p_78087_3_ * 3.1415927F * 0.03F;
    
    for (var9 = 4; var9 < 8; var9++)
    {
      blazeSticks[var9].rotationPointY = (2.0F + MathHelper.cos((var9 * 2 + p_78087_3_) * 0.25F));
      blazeSticks[var9].rotationPointX = (MathHelper.cos(var8) * 7.0F);
      blazeSticks[var9].rotationPointZ = (MathHelper.sin(var8) * 7.0F);
      var8 += 1.0F;
    }
    
    var8 = 0.47123894F + p_78087_3_ * 3.1415927F * -0.05F;
    
    for (var9 = 8; var9 < 12; var9++)
    {
      blazeSticks[var9].rotationPointY = (11.0F + MathHelper.cos((var9 * 1.5F + p_78087_3_) * 0.5F));
      blazeSticks[var9].rotationPointX = (MathHelper.cos(var8) * 5.0F);
      blazeSticks[var9].rotationPointZ = (MathHelper.sin(var8) * 5.0F);
      var8 += 1.0F;
    }
    
    blazeHead.rotateAngleY = (p_78087_4_ / 57.295776F);
    blazeHead.rotateAngleX = (p_78087_5_ / 57.295776F);
  }
}
