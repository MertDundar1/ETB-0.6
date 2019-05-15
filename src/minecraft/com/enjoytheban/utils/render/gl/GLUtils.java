package com.enjoytheban.utils.render.gl;

import com.enjoytheban.utils.math.Vec3f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;















public final class GLUtils
{
  public static final FloatBuffer MODELVIEW = BufferUtils.createFloatBuffer(16);
  public static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer(16);
  public static final IntBuffer VIEWPORT = BufferUtils.createIntBuffer(16);
  public static final FloatBuffer TO_SCREEN_BUFFER = BufferUtils.createFloatBuffer(3);
  public static final FloatBuffer TO_WORLD_BUFFER = BufferUtils.createFloatBuffer(3);
  


  private GLUtils() {}
  

  public static void init() {}
  

  public static float[] getColor(int hex)
  {
    return new float[] {
      (hex >> 16 & 0xFF) / 255.0F, 
      (hex >> 8 & 0xFF) / 255.0F, 
      (hex & 0xFF) / 255.0F, 
      (hex >> 24 & 0xFF) / 255.0F };
  }
  





  public static void glColor(int hex)
  {
    float[] color = getColor(hex);
    GlStateManager.color(color[0], color[1], color[2], color[3]);
  }
  








  public static void rotateX(float angle, double x, double y, double z)
  {
    GlStateManager.translate(x, y, z);
    GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
    GlStateManager.translate(-x, -y, -z);
  }
  








  public static void rotateY(float angle, double x, double y, double z)
  {
    GlStateManager.translate(x, y, z);
    GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
    GlStateManager.translate(-x, -y, -z);
  }
  








  public static void rotateZ(float angle, double x, double y, double z)
  {
    GlStateManager.translate(x, y, z);
    GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
    GlStateManager.translate(-x, -y, -z);
  }
  






  public static Vec3f toScreen(Vec3f pos)
  {
    return toScreen(pos.getX(), pos.getY(), pos.getZ());
  }
  








  public static Vec3f toScreen(double x, double y, double z)
  {
    boolean result = GLU.gluProject((float)x, (float)y, (float)z, MODELVIEW, PROJECTION, VIEWPORT, (FloatBuffer)TO_SCREEN_BUFFER.clear());
    if (result) {
      return new Vec3f(TO_SCREEN_BUFFER.get(0), Display.getHeight() - TO_SCREEN_BUFFER.get(1), TO_SCREEN_BUFFER.get(2));
    }
    return null;
  }
  






  public static Vec3f toWorld(Vec3f pos)
  {
    return toWorld(pos.getX(), pos.getY(), pos.getZ());
  }
  







  public static Vec3f toWorld(double x, double y, double z)
  {
    boolean result = GLU.gluUnProject((float)x, (float)y, (float)z, MODELVIEW, PROJECTION, VIEWPORT, (FloatBuffer)TO_WORLD_BUFFER.clear());
    if (result) {
      return new Vec3f(TO_WORLD_BUFFER.get(0), TO_WORLD_BUFFER.get(1), TO_WORLD_BUFFER.get(2));
    }
    return null;
  }
  




  public static FloatBuffer getModelview()
  {
    return MODELVIEW;
  }
  




  public static FloatBuffer getProjection()
  {
    return PROJECTION;
  }
  




  public static IntBuffer getViewport()
  {
    return VIEWPORT;
  }
}
