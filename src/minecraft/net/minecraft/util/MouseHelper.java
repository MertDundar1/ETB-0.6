package net.minecraft.util;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;





public class MouseHelper
{
  public int deltaX;
  public int deltaY;
  private static final String __OBFID = "CL_00000648";
  
  public MouseHelper() {}
  
  public void grabMouseCursor()
  {
    Mouse.setGrabbed(true);
    deltaX = 0;
    deltaY = 0;
  }
  



  public void ungrabMouseCursor()
  {
    Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
    Mouse.setGrabbed(false);
  }
  
  public void mouseXYChange()
  {
    deltaX = Mouse.getDX();
    deltaY = Mouse.getDY();
  }
}
