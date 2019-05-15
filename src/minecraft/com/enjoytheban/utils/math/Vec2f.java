package com.enjoytheban.utils.math;

import com.enjoytheban.utils.render.gl.GLUtils;








public final class Vec2f
{
  private float x;
  private float y;
  
  public Vec2f()
  {
    this(0.0F, 0.0F);
  }
  
  public Vec2f(Vec2f vec) {
    this(x, y);
  }
  
  public Vec2f(double x, double y) {
    this((float)x, (float)y);
  }
  
  public Vec2f(float x, float y) {
    this.x = x;
    this.y = y;
  }
  





  public final Vec2f setX(float x)
  {
    this.x = x;
    return this;
  }
  





  public final Vec2f setY(float y)
  {
    this.y = y;
    return this;
  }
  


  public final float getX()
  {
    return x;
  }
  


  public final float getY()
  {
    return y;
  }
  





  public final Vec2f add(Vec2f vec)
  {
    return new Vec2f(x + x, y + y);
  }
  






  public final Vec2f add(double x, double y)
  {
    return add(new Vec2f(x, y));
  }
  






  public final Vec2f add(float x, float y)
  {
    return add(new Vec2f(x, y));
  }
  





  public final Vec2f sub(Vec2f vec)
  {
    return new Vec2f(x - x, y - y);
  }
  






  public final Vec2f sub(double x, double y)
  {
    return sub(new Vec2f(x, y));
  }
  






  public final Vec2f sub(float x, float y)
  {
    return sub(new Vec2f(x, y));
  }
  





  public final Vec2f scale(float scale)
  {
    return new Vec2f(x * scale, y * scale);
  }
  




  public final Vec3f toVec3()
  {
    return new Vec3f(x, y, 0.0D);
  }
  




  public final Vec2f copy()
  {
    return new Vec2f(this);
  }
  





  public final Vec2f transfer(Vec2f vec)
  {
    x = x;
    y = y;
    return this;
  }
  




  public final float distanceTo(Vec2f vec)
  {
    double dx = x - x;
    double dy = y - y;
    return (float)Math.sqrt(dx * dx + dy * dy);
  }
  




  public final Vec3f toScreen()
  {
    return GLUtils.toWorld(toVec3());
  }
  
  public String toString()
  {
    return 
    

      "Vec2{x=" + x + ", y=" + y + '}';
  }
}
