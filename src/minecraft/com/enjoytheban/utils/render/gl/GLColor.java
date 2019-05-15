package com.enjoytheban.utils.render.gl;

import org.lwjgl.opengl.GL11;








public class GLColor
{
  public GLColor() {}
  
  public static void color(int color)
  {
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
  }
}
