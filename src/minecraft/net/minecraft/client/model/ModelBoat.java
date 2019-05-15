package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelBoat extends ModelBase
{
  public ModelRenderer[] boatSides = new ModelRenderer[5];
  private static final String __OBFID = "CL_00000832";
  
  public ModelBoat()
  {
    boatSides[0] = new ModelRenderer(this, 0, 8);
    boatSides[1] = new ModelRenderer(this, 0, 0);
    boatSides[2] = new ModelRenderer(this, 0, 0);
    boatSides[3] = new ModelRenderer(this, 0, 0);
    boatSides[4] = new ModelRenderer(this, 0, 0);
    byte var1 = 24;
    byte var2 = 6;
    byte var3 = 20;
    byte var4 = 4;
    boatSides[0].addBox(-var1 / 2, -var3 / 2 + 2, -3.0F, var1, var3 - 4, 4, 0.0F);
    boatSides[0].setRotationPoint(0.0F, var4, 0.0F);
    boatSides[1].addBox(-var1 / 2 + 2, -var2 - 1, -1.0F, var1 - 4, var2, 2, 0.0F);
    boatSides[1].setRotationPoint(-var1 / 2 + 1, var4, 0.0F);
    boatSides[2].addBox(-var1 / 2 + 2, -var2 - 1, -1.0F, var1 - 4, var2, 2, 0.0F);
    boatSides[2].setRotationPoint(var1 / 2 - 1, var4, 0.0F);
    boatSides[3].addBox(-var1 / 2 + 2, -var2 - 1, -1.0F, var1 - 4, var2, 2, 0.0F);
    boatSides[3].setRotationPoint(0.0F, var4, -var3 / 2 + 1);
    boatSides[4].addBox(-var1 / 2 + 2, -var2 - 1, -1.0F, var1 - 4, var2, 2, 0.0F);
    boatSides[4].setRotationPoint(0.0F, var4, var3 / 2 - 1);
    boatSides[0].rotateAngleX = 1.5707964F;
    boatSides[1].rotateAngleY = 4.712389F;
    boatSides[2].rotateAngleY = 1.5707964F;
    boatSides[3].rotateAngleY = 3.1415927F;
  }
  



  public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
  {
    for (int var8 = 0; var8 < 5; var8++)
    {
      boatSides[var8].render(p_78088_7_);
    }
  }
}
