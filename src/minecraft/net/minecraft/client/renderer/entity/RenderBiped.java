package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderBiped extends RenderLiving
{
  private static final ResourceLocation field_177118_j = new ResourceLocation("textures/entity/steve.png");
  protected ModelBiped modelBipedMain;
  protected float field_77070_b;
  private static final String __OBFID = "CL_00001001";
  
  public RenderBiped(RenderManager p_i46168_1_, ModelBiped p_i46168_2_, float p_i46168_3_)
  {
    this(p_i46168_1_, p_i46168_2_, p_i46168_3_, 1.0F);
    addLayer(new LayerHeldItem(this));
  }
  
  public RenderBiped(RenderManager p_i46169_1_, ModelBiped p_i46169_2_, float p_i46169_3_, float p_i46169_4_)
  {
    super(p_i46169_1_, p_i46169_2_, p_i46169_3_);
    modelBipedMain = p_i46169_2_;
    field_77070_b = p_i46169_4_;
    addLayer(new LayerCustomHead(bipedHead));
  }
  



  protected ResourceLocation getEntityTexture(EntityLiving p_110775_1_)
  {
    return field_177118_j;
  }
  
  public void func_82422_c()
  {
    GlStateManager.translate(0.0F, 0.1875F, 0.0F);
  }
  



  protected ResourceLocation getEntityTexture(Entity p_110775_1_)
  {
    return getEntityTexture((EntityLiving)p_110775_1_);
  }
}
