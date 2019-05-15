package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSkeletonHead extends ModelBase
{
  public ModelRenderer skeletonHead;
  private static final String __OBFID = "CL_00000856";
  
  public ModelSkeletonHead()
  {
    this(0, 35, 64, 64);
  }
  
  public ModelSkeletonHead(int p_i1155_1_, int p_i1155_2_, int p_i1155_3_, int p_i1155_4_)
  {
    textureWidth = p_i1155_3_;
    textureHeight = p_i1155_4_;
    skeletonHead = new ModelRenderer(this, p_i1155_1_, p_i1155_2_);
    skeletonHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
    skeletonHead.setRotationPoint(0.0F, 0.0F, 0.0F);
  }
  



  public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
  {
    setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
    skeletonHead.render(p_78088_7_);
  }
  





  public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
  {
    super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
    skeletonHead.rotateAngleY = (p_78087_4_ / 57.295776F);
    skeletonHead.rotateAngleX = (p_78087_5_ / 57.295776F);
  }
}
