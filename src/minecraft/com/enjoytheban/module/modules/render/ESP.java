package com.enjoytheban.module.modules.render;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.rendering.EventRender2D;
import com.enjoytheban.api.value.Mode;
import com.enjoytheban.api.value.Value;
import com.enjoytheban.management.FriendManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.math.Vec3f;
import com.enjoytheban.utils.render.RenderUtil;
import com.enjoytheban.utils.render.gl.GLUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;


public class ESP
  extends Module
{
  private ArrayList<Vec3f> points = new ArrayList();
  public Mode<Enum> mode = new Mode("Mode", "mode", ESPMode.values(), ESPMode.TwoDimensional);
  
  public ESP() {
    super("ESP", new String[] { "outline", "wallhack" }, ModuleType.Render);
    addValues(new Value[] { mode });
    for (int i = 0; i < 8; i++)
    {
      points.add(new Vec3f());
    }
    setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
  }
  
  @EventHandler
  public void onScreen(EventRender2D eventRender) {
    if (mode.getValue() == ESPMode.TwoDimensional) {
      GlStateManager.pushMatrix();
      ScaledResolution scaledRes = new ScaledResolution(mc);
      double twoDscale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0D);
      GlStateManager.scale(twoDscale, twoDscale, twoDscale);
      for (Object o : mc.theWorld.getLoadedEntityList()) {
        if (((o instanceof EntityLivingBase)) && (o != mc.thePlayer) && ((o instanceof EntityPlayer))) {
          EntityLivingBase ent = (EntityLivingBase)o;
          render(ent);
        }
      }
      GlStateManager.popMatrix();
    }
  }
  






  private void render(Entity entity)
  {
    Entity extended = entity;
    
    RenderManager renderManager = mc.getRenderManager();
    

    Vec3f offset = extended.interpolate(mc.timer.renderPartialTicks).sub(extended.getPos()).add(0.0D, 0.1D, 0.0D);
    if (entity.isInvisible()) {
      return;
    }
    
    AxisAlignedBB bb = entity.getEntityBoundingBox().offset(offset.getX() - RenderManager.renderPosX, 
      offset.getY() - RenderManager.renderPosY, offset.getZ() - RenderManager.renderPosZ);
    

    ((Vec3f)points.get(0)).setX(minX).setY(minY).setZ(minZ);
    ((Vec3f)points.get(1)).setX(maxX).setY(minY).setZ(minZ);
    ((Vec3f)points.get(2)).setX(maxX).setY(minY).setZ(maxZ);
    ((Vec3f)points.get(3)).setX(minX).setY(minY).setZ(maxZ);
    ((Vec3f)points.get(4)).setX(minX).setY(maxY).setZ(minZ);
    ((Vec3f)points.get(5)).setX(maxX).setY(maxY).setZ(minZ);
    ((Vec3f)points.get(6)).setX(maxX).setY(maxY).setZ(maxZ);
    ((Vec3f)points.get(7)).setX(minX).setY(maxY).setZ(maxZ);
    




    float left = Float.MAX_VALUE;float right = 0.0F;float top = Float.MAX_VALUE;float bottom = 0.0F;
    

    for (Vec3f point : points)
    {
      Vec3f screen = point.toScreen();
      
      if ((screen.getZ() >= 0.0D) && (screen.getZ() < 1.0D))
      {





        if (screen.getX() < left)
          left = (float)screen.getX();
        if (screen.getY() < top)
          top = (float)screen.getY();
        if (screen.getX() > right)
          right = (float)screen.getX();
        if (screen.getY() > bottom) {
          bottom = (float)screen.getY();
        }
      }
    }
    
    if ((bottom <= 1.0F) && (right <= 1.0F))
    {
      return;
    }
    

    box(left, top, right, bottom);
    


    name(entity, left, top, right, bottom);
    

    if (!(entity instanceof EntityLivingBase))
    {
      return;
    }
    EntityLivingBase living = (EntityLivingBase)entity;
    

    health(living, left, top, right, bottom);
  }
  













  private void box(float left, float top, float right, float bottom)
  {
    GL11.glColor4d(1.0D, 1.0D, 1.0D, 0.5D);
    

    RenderUtil.drawLine(left, top, right, top, 2.0F);
    RenderUtil.drawLine(left, bottom, right, bottom, 2.0F);
    RenderUtil.drawLine(left, top, left, bottom, 2.0F);
    RenderUtil.drawLine(right, top, right, bottom, 2.0F);
    





    RenderUtil.drawLine(left + 1.0F, top + 1.0F, right - 1.0F, top + 1.0F, 1.0F);
    RenderUtil.drawLine(left + 1.0F, bottom - 1.0F, right - 1.0F, bottom - 1.0F, 1.0F);
    RenderUtil.drawLine(left + 1.0F, top + 1.0F, left + 1.0F, bottom - 1.0F, 1.0F);
    RenderUtil.drawLine(right - 1.0F, top + 1.0F, right - 1.0F, bottom - 1.0F, 1.0F);
    

    RenderUtil.drawLine(left - 1.0F, top - 1.0F, right + 1.0F, top - 1.0F, 1.0F);
    RenderUtil.drawLine(left - 1.0F, bottom + 1.0F, right + 1.0F, bottom + 1.0F, 1.0F);
    RenderUtil.drawLine(left - 1.0F, top + 1.0F, left - 1.0F, bottom + 1.0F, 1.0F);
    RenderUtil.drawLine(right + 1.0F, top - 1.0F, right + 1.0F, bottom + 1.0F, 1.0F);
  }
  















  private void name(Entity entity, float left, float top, float right, float bottom)
  {
    mc.fontRendererObj.drawCenteredString(FriendManager.isFriend(entity.getName()) ? "§b" + FriendManager.getAlias(entity.getName()) : entity.getName(), (int)(left + right) / 2, 
      (int)(top - mc.fontRendererObj.FONT_HEIGHT - 2.0F + 1.0F), -1);
    if (((EntityPlayer)entity).getCurrentEquippedItem() != null)
    {
      String stack = ((EntityPlayer)entity).getCurrentEquippedItem().getDisplayName();
      mc.fontRendererObj.drawCenteredString(stack, (int)(left + right) / 2, (int)bottom, -1);
    }
  }
  















  private void health(EntityLivingBase entity, float left, float top, float right, float bottom)
  {
    float height = bottom - top;
    

    float currentHealth = entity.getHealth();
    
    float maxHealth = entity.getMaxHealth();
    
    float healthPercent = currentHealth / maxHealth;
    


    GLUtils.glColor(getHealthColor(entity));
    


    RenderUtil.drawLine(left - 5.0F, top + height * (1.0F - healthPercent) + 1.0F, left - 5.0F, bottom, 2.0F);
  }
  




  private int getHealthColor(EntityLivingBase player)
  {
    float f = player.getHealth();
    float f1 = player.getMaxHealth();
    float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
    return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
  }
  
  public static enum ESPMode {
    Outline,  TwoDimensional;
  }
}
