package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderGuardian extends RenderLiving
{
  private static final ResourceLocation field_177114_e = new ResourceLocation("textures/entity/guardian.png");
  private static final ResourceLocation field_177116_j = new ResourceLocation("textures/entity/guardian_elder.png");
  private static final ResourceLocation field_177117_k = new ResourceLocation("textures/entity/guardian_beam.png");
  int field_177115_a;
  private static final String __OBFID = "CL_00002443";
  
  public RenderGuardian(RenderManager p_i46171_1_)
  {
    super(p_i46171_1_, new ModelGuardian(), 0.5F);
    field_177115_a = ((ModelGuardian)mainModel).func_178706_a();
  }
  
  public boolean func_177113_a(EntityGuardian p_177113_1_, ICamera p_177113_2_, double p_177113_3_, double p_177113_5_, double p_177113_7_)
  {
    if (super.func_177104_a(p_177113_1_, p_177113_2_, p_177113_3_, p_177113_5_, p_177113_7_))
    {
      return true;
    }
    

    if (p_177113_1_.func_175474_cn())
    {
      EntityLivingBase var9 = p_177113_1_.func_175466_co();
      
      if (var9 != null)
      {
        Vec3 var10 = func_177110_a(var9, height * 0.5D, 1.0F);
        Vec3 var11 = func_177110_a(p_177113_1_, p_177113_1_.getEyeHeight(), 1.0F);
        
        if (p_177113_2_.isBoundingBoxInFrustum(net.minecraft.util.AxisAlignedBB.fromBounds(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord)))
        {
          return true;
        }
      }
    }
    
    return false;
  }
  

  private Vec3 func_177110_a(EntityLivingBase p_177110_1_, double p_177110_2_, float p_177110_4_)
  {
    double var5 = lastTickPosX + (posX - lastTickPosX) * p_177110_4_;
    double var7 = p_177110_2_ + lastTickPosY + (posY - lastTickPosY) * p_177110_4_;
    double var9 = lastTickPosZ + (posZ - lastTickPosZ) * p_177110_4_;
    return new Vec3(var5, var7, var9);
  }
  
  public void func_177109_a(EntityGuardian p_177109_1_, double p_177109_2_, double p_177109_4_, double p_177109_6_, float p_177109_8_, float p_177109_9_)
  {
    if (field_177115_a != ((ModelGuardian)mainModel).func_178706_a())
    {
      mainModel = new ModelGuardian();
      field_177115_a = ((ModelGuardian)mainModel).func_178706_a();
    }
    
    super.doRender(p_177109_1_, p_177109_2_, p_177109_4_, p_177109_6_, p_177109_8_, p_177109_9_);
    EntityLivingBase var10 = p_177109_1_.func_175466_co();
    
    if (var10 != null)
    {
      float var11 = p_177109_1_.func_175477_p(p_177109_9_);
      Tessellator var12 = Tessellator.getInstance();
      WorldRenderer var13 = var12.getWorldRenderer();
      bindTexture(field_177117_k);
      GL11.glTexParameterf(3553, 10242, 10497.0F);
      GL11.glTexParameterf(3553, 10243, 10497.0F);
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      float var14 = 240.0F;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var14, var14);
      GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
      float var15 = (float)worldObj.getTotalWorldTime() + p_177109_9_;
      float var16 = var15 * 0.5F % 1.0F;
      float var17 = p_177109_1_.getEyeHeight();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)p_177109_2_, (float)p_177109_4_ + var17, (float)p_177109_6_);
      Vec3 var18 = func_177110_a(var10, height * 0.5D, p_177109_9_);
      Vec3 var19 = func_177110_a(p_177109_1_, var17, p_177109_9_);
      Vec3 var20 = var18.subtract(var19);
      double var21 = var20.lengthVector() + 1.0D;
      var20 = var20.normalize();
      float var23 = (float)Math.acos(yCoord);
      float var24 = (float)Math.atan2(zCoord, xCoord);
      GlStateManager.rotate((1.5707964F + -var24) * 57.295776F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(var23 * 57.295776F, 1.0F, 0.0F, 0.0F);
      byte var25 = 1;
      double var26 = var15 * 0.05D * (1.0D - (var25 & 0x1) * 2.5D);
      var13.startDrawingQuads();
      float var28 = var11 * var11;
      var13.func_178961_b(64 + (int)(var28 * 240.0F), 32 + (int)(var28 * 192.0F), 128 - (int)(var28 * 64.0F), 255);
      double var29 = var25 * 0.2D;
      double var31 = var29 * 1.41D;
      double var33 = 0.0D + Math.cos(var26 + 2.356194490192345D) * var31;
      double var35 = 0.0D + Math.sin(var26 + 2.356194490192345D) * var31;
      double var37 = 0.0D + Math.cos(var26 + 0.7853981633974483D) * var31;
      double var39 = 0.0D + Math.sin(var26 + 0.7853981633974483D) * var31;
      double var41 = 0.0D + Math.cos(var26 + 3.9269908169872414D) * var31;
      double var43 = 0.0D + Math.sin(var26 + 3.9269908169872414D) * var31;
      double var45 = 0.0D + Math.cos(var26 + 5.497787143782138D) * var31;
      double var47 = 0.0D + Math.sin(var26 + 5.497787143782138D) * var31;
      double var49 = 0.0D + Math.cos(var26 + 3.141592653589793D) * var29;
      double var51 = 0.0D + Math.sin(var26 + 3.141592653589793D) * var29;
      double var53 = 0.0D + Math.cos(var26 + 0.0D) * var29;
      double var55 = 0.0D + Math.sin(var26 + 0.0D) * var29;
      double var57 = 0.0D + Math.cos(var26 + 1.5707963267948966D) * var29;
      double var59 = 0.0D + Math.sin(var26 + 1.5707963267948966D) * var29;
      double var61 = 0.0D + Math.cos(var26 + 4.71238898038469D) * var29;
      double var63 = 0.0D + Math.sin(var26 + 4.71238898038469D) * var29;
      double var67 = 0.0D;
      double var69 = 0.4999D;
      double var71 = -1.0F + var16;
      double var73 = var21 * (0.5D / var29) + var71;
      var13.addVertexWithUV(var49, var21, var51, var69, var73);
      var13.addVertexWithUV(var49, 0.0D, var51, var69, var71);
      var13.addVertexWithUV(var53, 0.0D, var55, var67, var71);
      var13.addVertexWithUV(var53, var21, var55, var67, var73);
      var13.addVertexWithUV(var57, var21, var59, var69, var73);
      var13.addVertexWithUV(var57, 0.0D, var59, var69, var71);
      var13.addVertexWithUV(var61, 0.0D, var63, var67, var71);
      var13.addVertexWithUV(var61, var21, var63, var67, var73);
      double var75 = 0.0D;
      
      if (ticksExisted % 2 == 0)
      {
        var75 = 0.5D;
      }
      
      var13.addVertexWithUV(var33, var21, var35, 0.5D, var75 + 0.5D);
      var13.addVertexWithUV(var37, var21, var39, 1.0D, var75 + 0.5D);
      var13.addVertexWithUV(var45, var21, var47, 1.0D, var75);
      var13.addVertexWithUV(var41, var21, var43, 0.5D, var75);
      var12.draw();
      GlStateManager.popMatrix();
    }
  }
  
  protected void func_177112_a(EntityGuardian p_177112_1_, float p_177112_2_)
  {
    if (p_177112_1_.func_175461_cl())
    {
      GlStateManager.scale(2.35F, 2.35F, 2.35F);
    }
  }
  
  protected ResourceLocation func_177111_a(EntityGuardian p_177111_1_)
  {
    return p_177111_1_.func_175461_cl() ? field_177116_j : field_177114_e;
  }
  






  public void doRender(EntityLiving p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
  {
    func_177109_a((EntityGuardian)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }
  
  public boolean func_177104_a(EntityLiving p_177104_1_, ICamera p_177104_2_, double p_177104_3_, double p_177104_5_, double p_177104_7_)
  {
    return func_177113_a((EntityGuardian)p_177104_1_, p_177104_2_, p_177104_3_, p_177104_5_, p_177104_7_);
  }
  




  protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_)
  {
    func_177112_a((EntityGuardian)p_77041_1_, p_77041_2_);
  }
  






  public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
  {
    func_177109_a((EntityGuardian)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }
  



  protected ResourceLocation getEntityTexture(Entity p_110775_1_)
  {
    return func_177111_a((EntityGuardian)p_110775_1_);
  }
  






  public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
  {
    func_177109_a((EntityGuardian)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }
  
  public boolean func_177071_a(Entity p_177071_1_, ICamera p_177071_2_, double p_177071_3_, double p_177071_5_, double p_177071_7_)
  {
    return func_177113_a((EntityGuardian)p_177071_1_, p_177071_2_, p_177071_3_, p_177071_5_, p_177071_7_);
  }
}
