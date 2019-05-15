package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;




public class RenderHelper
{
  private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
  private static final Vec3 field_82884_b = new Vec3(0.20000000298023224D, 1.0D, -0.699999988079071D).normalize();
  private static final Vec3 field_82885_c = new Vec3(-0.20000000298023224D, 1.0D, 0.699999988079071D).normalize();
  private static final String __OBFID = "CL_00000629";
  
  public RenderHelper() {}
  
  public static void disableStandardItemLighting()
  {
    GlStateManager.disableLighting();
    GlStateManager.disableBooleanStateAt(0);
    GlStateManager.disableBooleanStateAt(1);
    GlStateManager.disableColorMaterial();
  }
  


  public static void enableStandardItemLighting()
  {
    GlStateManager.enableLighting();
    GlStateManager.enableBooleanStateAt(0);
    GlStateManager.enableBooleanStateAt(1);
    GlStateManager.enableColorMaterial();
    GlStateManager.colorMaterial(1032, 5634);
    float var0 = 0.4F;
    float var1 = 0.6F;
    float var2 = 0.0F;
    GL11.glLight(16384, 4611, setColorBuffer(field_82884_bxCoord, field_82884_byCoord, field_82884_bzCoord, 0.0D));
    GL11.glLight(16384, 4609, setColorBuffer(var1, var1, var1, 1.0F));
    GL11.glLight(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
    GL11.glLight(16384, 4610, setColorBuffer(var2, var2, var2, 1.0F));
    GL11.glLight(16385, 4611, setColorBuffer(field_82885_cxCoord, field_82885_cyCoord, field_82885_czCoord, 0.0D));
    GL11.glLight(16385, 4609, setColorBuffer(var1, var1, var1, 1.0F));
    GL11.glLight(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
    GL11.glLight(16385, 4610, setColorBuffer(var2, var2, var2, 1.0F));
    GlStateManager.shadeModel(7424);
    GL11.glLightModel(2899, setColorBuffer(var0, var0, var0, 1.0F));
  }
  


  private static FloatBuffer setColorBuffer(double p_74517_0_, double p_74517_2_, double p_74517_4_, double p_74517_6_)
  {
    return setColorBuffer((float)p_74517_0_, (float)p_74517_2_, (float)p_74517_4_, (float)p_74517_6_);
  }
  


  private static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_)
  {
    colorBuffer.clear();
    colorBuffer.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
    colorBuffer.flip();
    return colorBuffer;
  }
  
  public static void drawRect(float x, float y, float x1, float y1)
  {
    GL11.glBegin(7);
    GL11.glVertex2f(x, y1);
    GL11.glVertex2f(x1, y1);
    GL11.glVertex2f(x1, y);
    GL11.glVertex2f(x, y);
    GL11.glEnd();
  }
  
  public static void glColor(int hex) {
    float alpha = (hex >> 24 & 0xFF) / 255.0F;
    float red = (hex >> 16 & 0xFF) / 255.0F;
    float green = (hex >> 8 & 0xFF) / 255.0F;
    float blue = (hex & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
  }
  
  public static void drawOutlineBox(AxisAlignedBB axisalignedbb, float width, int color) {
    GL11.glLineWidth(width);
    GL11.glEnable(2848);
    GL11.glEnable(2881);
    GL11.glHint(3154, 4354);
    GL11.glHint(3155, 4354);
    glColor(color);
    drawOutlinedBox(axisalignedbb);
    GL11.glDisable(2848);
    GL11.glDisable(2881);
  }
  
  public static void drawCrosses(AxisAlignedBB axisalignedbb, float width, int color) {
    GL11.glLineWidth(width);
    GL11.glEnable(2848);
    GL11.glEnable(2881);
    GL11.glHint(3154, 4354);
    GL11.glHint(3155, 4354);
    glColor(color);
    drawCrosses(axisalignedbb);
    GL11.glDisable(2848);
    GL11.glDisable(2881);
  }
  
  public static void drawCompleteBox(AxisAlignedBB axisalignedbb, float width, int insideColor, int borderColor) {
    GL11.glLineWidth(width);
    GL11.glEnable(2848);
    GL11.glEnable(2881);
    GL11.glHint(3154, 4354);
    GL11.glHint(3155, 4354);
    glColor(insideColor);
    drawBox(axisalignedbb);
    glColor(borderColor);
    drawOutlinedBox(axisalignedbb);
    drawCrosses(axisalignedbb);
    GL11.glDisable(2848);
    GL11.glDisable(2881);
  }
  
  public static void drawCrosses(AxisAlignedBB box) {
    if (box == null) {
      return;
    }
    GL11.glBegin(1);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(maxX, maxY, minZ);
    GL11.glVertex3d(minX, maxY, maxZ);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glVertex3d(maxX, minY, maxZ);
    GL11.glEnd();
  }
  
  public static void drawBox(AxisAlignedBB axisalignedbb, int color) {
    GL11.glEnable(2848);
    GL11.glEnable(2881);
    GL11.glHint(3154, 4354);
    GL11.glHint(3155, 4354);
    glColor(color);
    drawBox(axisalignedbb);
    GL11.glDisable(2848);
    GL11.glDisable(2881);
  }
  
  public static void drawOutlinedBox(AxisAlignedBB box) {
    if (box == null) {
      return;
    }
    GL11.glBegin(3);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(maxX, minY, maxZ);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glEnd();
    GL11.glBegin(3);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glVertex3d(maxX, maxY, minZ);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glVertex3d(minX, maxY, maxZ);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glEnd();
    GL11.glBegin(1);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(maxX, maxY, minZ);
    GL11.glVertex3d(maxX, minY, maxZ);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glVertex3d(minX, maxY, maxZ);
    GL11.glEnd();
  }
  
  public static void drawBox(AxisAlignedBB box) {
    if (box == null) {
      return;
    }
    GL11.glBegin(7);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glVertex3d(maxX, minY, maxZ);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glVertex3d(minX, maxY, maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(maxX, minY, maxZ);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glVertex3d(minX, maxY, maxZ);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glVertex3d(minX, maxY, maxZ);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glVertex3d(minX, maxY, maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(maxX, minY, maxZ);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(maxX, maxY, minZ);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(maxX, minY, maxZ);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glVertex3d(maxX, maxY, minZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(maxX, maxY, minZ);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glVertex3d(maxX, maxY, minZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glVertex3d(maxX, maxY, minZ);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glVertex3d(minX, maxY, maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(maxX, maxY, minZ);
    GL11.glVertex3d(minX, maxY, minZ);
    GL11.glVertex3d(minX, maxY, maxZ);
    GL11.glVertex3d(maxX, maxY, maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(maxX, minY, maxZ);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glEnd();
    GL11.glBegin(7);
    GL11.glVertex3d(maxX, minY, minZ);
    GL11.glVertex3d(minX, minY, minZ);
    GL11.glVertex3d(minX, minY, maxZ);
    GL11.glVertex3d(maxX, minY, maxZ);
    GL11.glEnd();
  }
  
  public static void rectangle(double left, double top, double right, double bottom, int color)
  {
    if (left < right) {
      double var5 = left;
      left = right;
      right = var5;
    }
    if (top < bottom) {
      double var5 = top;
      top = bottom;
      bottom = var5;
    }
    float var11 = (color >> 24 & 0xFF) / 255.0F;
    float var6 = (color >> 16 & 0xFF) / 255.0F;
    float var7 = (color >> 8 & 0xFF) / 255.0F;
    float var8 = (color & 0xFF) / 255.0F;
    WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(var6, var7, var8, var11);
    worldRenderer.startDrawingQuads();
    worldRenderer.addVertex(left, bottom, 0.0D);
    worldRenderer.addVertex(right, bottom, 0.0D);
    worldRenderer.addVertex(right, top, 0.0D);
    worldRenderer.addVertex(left, top, 0.0D);
    Tessellator.getInstance().draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  


  public static void enableGUIStandardItemLighting()
  {
    GlStateManager.pushMatrix();
    GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
    enableStandardItemLighting();
    GlStateManager.popMatrix();
  }
}
