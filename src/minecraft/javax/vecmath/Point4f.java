package javax.vecmath;

import java.io.Serializable;












































public class Point4f
  extends Tuple4f
  implements Serializable
{
  static final long serialVersionUID = 4643134103185764459L;
  
  public Point4f(float x, float y, float z, float w)
  {
    super(x, y, z, w);
  }
  





  public Point4f(float[] p)
  {
    super(p);
  }
  





  public Point4f(Point4f p1)
  {
    super(p1);
  }
  





  public Point4f(Point4d p1)
  {
    super(p1);
  }
  





  public Point4f(Tuple4f t1)
  {
    super(t1);
  }
  





  public Point4f(Tuple4d t1)
  {
    super(t1);
  }
  









  public Point4f(Tuple3f t1)
  {
    super(x, y, z, 1.0F);
  }
  








  public Point4f() {}
  







  public final void set(Tuple3f t1)
  {
    x = x;
    y = y;
    z = z;
    w = 1.0F;
  }
  








  public final float distanceSquared(Point4f p1)
  {
    float dx = x - x;
    float dy = y - y;
    float dz = z - z;
    float dw = w - w;
    return dx * dx + dy * dy + dz * dz + dw * dw;
  }
  








  public final float distance(Point4f p1)
  {
    float dx = x - x;
    float dy = y - y;
    float dz = z - z;
    float dw = w - w;
    return (float)Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
  }
  








  public final float distanceL1(Point4f p1)
  {
    return Math.abs(x - x) + Math.abs(y - y) + Math.abs(z - z) + Math.abs(w - w);
  }
  









  public final float distanceLinf(Point4f p1)
  {
    float t1 = Math.max(Math.abs(x - x), Math.abs(y - y));
    float t2 = Math.max(Math.abs(z - z), Math.abs(w - w));
    
    return Math.max(t1, t2);
  }
  









  public final void project(Point4f p1)
  {
    float oneOw = 1.0F / w;
    x *= oneOw;
    y *= oneOw;
    z *= oneOw;
    w = 1.0F;
  }
}
