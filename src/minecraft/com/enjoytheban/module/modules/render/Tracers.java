package com.enjoytheban.module.modules.render;

import com.enjoytheban.api.EventHandler;
import com.enjoytheban.api.events.rendering.EventRender3D;
import com.enjoytheban.management.FriendManager;
import com.enjoytheban.module.Module;
import com.enjoytheban.module.ModuleType;
import com.enjoytheban.utils.math.MathUtil;
import com.enjoytheban.utils.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

public class Tracers
  extends Module
{
  public Tracers()
  {
    super("Tracers", new String[] { "lines", "tracer" }, ModuleType.Render);
    setColor(new Color(60, 136, 166).getRGB());
  }
  
  @EventHandler
  private void on3DRender(EventRender3D e)
  {
    for (Object o : mc.theWorld.loadedEntityList)
    {
      Entity entity = (Entity)o;
      
      if ((entity.isEntityAlive()) && ((entity instanceof EntityPlayer)) && (entity != mc.thePlayer)) {
        double posX = lastTickPosX + (posX - lastTickPosX) * e.getPartialTicks() - 
          RenderManager.renderPosX;
        double posY = lastTickPosY + (posY - lastTickPosY) * e.getPartialTicks() - 
          RenderManager.renderPosY;
        double posZ = lastTickPosZ + (posZ - lastTickPosZ) * e.getPartialTicks() - 
          RenderManager.renderPosZ;
        boolean old = mc.gameSettings.viewBobbing;
        RenderUtil.startDrawing();
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        mc.gameSettings.viewBobbing = old;
        float color = (float)Math.round(255.0D - mc.thePlayer.getDistanceSqToEntity(entity) * 255.0D / 
          MathUtil.square(mc.gameSettings.renderDistanceChunks * 2.5D)) / 255.0F;
        drawLine(entity, new double[] { color, 1.0F - color, FriendManager.isFriend(entity.getName()) ? new double[] { 0.0D, 1.0D, 1.0D } : 0.0D }, posX, posY, posZ);
        RenderUtil.stopDrawing();
      }
    }
  }
  
  private void drawLine(Entity entity, double[] color, double x, double y, double z)
  {
    float distance = mc.thePlayer.getDistanceToEntity(entity);
    float xD = distance / 48.0F;
    if (xD >= 1.0F) xD = 1.0F;
    boolean entityesp = false;
    
    GL11.glEnable(2848);
    if (color.length >= 4) {
      if (color[3] <= 0.1D) {
        return;
      }
      GL11.glColor4d(color[0], color[1], color[2], color[3]);
    } else {
      GL11.glColor3d(color[0], color[1], color[2]);
    }
    GL11.glLineWidth(1.0F);
    GL11.glBegin(1);
    GL11.glVertex3d(0.0D, mc.thePlayer.getEyeHeight(), 0.0D);
    GL11.glVertex3d(x, y, z);
    GL11.glEnd();
    GL11.glDisable(2848);
  }
}
