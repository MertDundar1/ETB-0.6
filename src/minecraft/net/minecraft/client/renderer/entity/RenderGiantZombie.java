package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;

public class RenderGiantZombie extends RenderLiving
{
  private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
  
  private float scale;
  
  private static final String __OBFID = "CL_00000998";
  
  public RenderGiantZombie(RenderManager p_i46173_1_, ModelBase p_i46173_2_, float p_i46173_3_, float p_i46173_4_)
  {
    super(p_i46173_1_, p_i46173_2_, p_i46173_3_ * p_i46173_4_);
    scale = p_i46173_4_;
    addLayer(new LayerHeldItem(this));
    addLayer(new LayerBipedArmor(this)
    {
      private static final String __OBFID = "CL_00002444";
      
      protected void func_177177_a() {
        field_177189_c = new ModelZombie(0.5F, true);
        field_177186_d = new ModelZombie(1.0F, true);
      }
    });
  }
  
  public void func_82422_c()
  {
    GlStateManager.translate(0.0F, 0.1875F, 0.0F);
  }
  




  protected void preRenderCallback(EntityGiantZombie p_77041_1_, float p_77041_2_)
  {
    GlStateManager.scale(scale, scale, scale);
  }
  



  protected ResourceLocation getEntityTexture(EntityGiantZombie p_110775_1_)
  {
    return zombieTextures;
  }
  




  protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_)
  {
    preRenderCallback((EntityGiantZombie)p_77041_1_, p_77041_2_);
  }
  



  protected ResourceLocation getEntityTexture(Entity p_110775_1_)
  {
    return getEntityTexture((EntityGiantZombie)p_110775_1_);
  }
}
