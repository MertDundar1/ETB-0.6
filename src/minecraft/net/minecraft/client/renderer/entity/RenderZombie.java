package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderZombie extends RenderBiped
{
  private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
  private static final ResourceLocation zombieVillagerTextures = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
  private final ModelBiped field_82434_o;
  private final ModelZombieVillager zombieVillagerModel;
  private final List field_177121_n;
  private final List field_177122_o;
  private static final String __OBFID = "CL_00001037";
  
  public RenderZombie(RenderManager p_i46127_1_)
  {
    super(p_i46127_1_, new ModelZombie(), 0.5F, 1.0F);
    LayerRenderer var2 = (LayerRenderer)field_177097_h.get(0);
    field_82434_o = modelBipedMain;
    zombieVillagerModel = new ModelZombieVillager();
    addLayer(new LayerHeldItem(this));
    LayerBipedArmor var3 = new LayerBipedArmor(this)
    {
      private static final String __OBFID = "CL_00002429";
      
      protected void func_177177_a() {
        field_177189_c = new ModelZombie(0.5F, true);
        field_177186_d = new ModelZombie(1.0F, true);
      }
    };
    addLayer(var3);
    field_177122_o = Lists.newArrayList(field_177097_h);
    
    if ((var2 instanceof LayerCustomHead))
    {
      func_177089_b(var2);
      addLayer(new LayerCustomHead(zombieVillagerModel.bipedHead));
    }
    
    func_177089_b(var3);
    addLayer(new LayerVillagerArmor(this));
    field_177121_n = Lists.newArrayList(field_177097_h);
  }
  
  public void func_180579_a(EntityZombie p_180579_1_, double p_180579_2_, double p_180579_4_, double p_180579_6_, float p_180579_8_, float p_180579_9_)
  {
    func_82427_a(p_180579_1_);
    super.doRender(p_180579_1_, p_180579_2_, p_180579_4_, p_180579_6_, p_180579_8_, p_180579_9_);
  }
  
  protected ResourceLocation func_180578_a(EntityZombie p_180578_1_)
  {
    return p_180578_1_.isVillager() ? zombieVillagerTextures : zombieTextures;
  }
  
  private void func_82427_a(EntityZombie p_82427_1_)
  {
    if (p_82427_1_.isVillager())
    {
      mainModel = zombieVillagerModel;
      field_177097_h = field_177121_n;
    }
    else
    {
      mainModel = field_82434_o;
      field_177097_h = field_177122_o;
    }
    
    modelBipedMain = ((ModelBiped)mainModel);
  }
  
  protected void rotateCorpse(EntityZombie p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
  {
    if (p_77043_1_.isConverting())
    {
      p_77043_3_ += (float)(Math.cos(ticksExisted * 3.25D) * 3.141592653589793D * 0.25D);
    }
    
    super.rotateCorpse(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
  }
  



  protected ResourceLocation getEntityTexture(EntityLiving p_110775_1_)
  {
    return func_180578_a((EntityZombie)p_110775_1_);
  }
  






  public void doRender(EntityLiving p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
  {
    func_180579_a((EntityZombie)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }
  
  protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
  {
    rotateCorpse((EntityZombie)p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
  }
  






  public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
  {
    func_180579_a((EntityZombie)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }
  



  protected ResourceLocation getEntityTexture(Entity p_110775_1_)
  {
    return func_180578_a((EntityZombie)p_110775_1_);
  }
  






  public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
  {
    func_180579_a((EntityZombie)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }
}
