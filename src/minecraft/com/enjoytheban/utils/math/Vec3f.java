package com.enjoytheban.utils.math;

import com.enjoytheban.utils.render.gl.GLUtils;







public final class Vec3f
{
  private double x;
  private double y;
  private double z;
  
  public Vec3f()
  {
    this(0.0D, 0.0D, 0.0D);
  }
  
  public Vec3f(Vec3f vec) {
    this(x, y, z);
  }
  
  public Vec3f(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  





  public final Vec3f setX(double x)
  {
    this.x = x;
    return this;
  }
  





  public final Vec3f setY(double y)
  {
    this.y = y;
    return this;
  }
  





  public final Vec3f setZ(double z)
  {
    this.z = z;
    return this;
  }
  


  public final double getX()
  {
    return x;
  }
  


  public final double getY()
  {
    return y;
  }
  


  public final double getZ()
  {
    return z;
  }
  





  public final Vec3f add(Vec3f vec)
  {
    return add(x, y, z);
  }
  







  public final Vec3f add(double x, double y, double z)
  {
    return new Vec3f(this.x + x, this.y + y, this.z + z);
  }
  





  public final Vec3f sub(Vec3f vec)
  {
    return new Vec3f(x - x, y - y, z - z);
  }
  







  public final Vec3f sub(double x, double y, double z)
  {
    return new Vec3f(this.x - x, this.y - y, this.z - z);
  }
  





  public final Vec3f scale(float scale)
  {
    return new Vec3f(x * scale, y * scale, z * scale);
  }
  




  public final Vec3f copy()
  {
    return new Vec3f(this);
  }
  





  public final Vec3f transfer(Vec3f vec)
  {
    x = x;
    y = y;
    z = z;
    return this;
  }
  




  public final double distanceTo(Vec3f vec)
  {
    double dx = x - x;
    double dy = y - y;
    double dz = z - z;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }
  







  public final Vec2f rotationsTo(Vec3f vec)
  {
    double[] diff = { x - x, y - y, z - z };
    double hDist = Math.sqrt(diff[0] * diff[0] + diff[2] * diff[2]);
    return new Vec2f(
      Math.toDegrees(Math.atan2(diff[2], diff[0])) - 90.0D, 
      -Math.toDegrees(Math.atan2(diff[1], hDist)));
  }
  





  public final Vec3f toScreen()
  {
    return GLUtils.toScreen(this);
  }
  
  public String toString()
  {
    return 
    


      "Vec3{x=" + x + ", y=" + y + ", z=" + z + '}';
  }
}
