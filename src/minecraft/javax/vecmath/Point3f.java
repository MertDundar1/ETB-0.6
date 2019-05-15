package javax.vecmath;

import java.io.Serializable;











































public class Point3f
  extends Tuple3f
  implements Serializable
{
  static final long serialVersionUID = -8689337816398030143L;
  
  public Point3f(float x, float y, float z)
  {
    super(x, y, z);
  }
  





  public Point3f(float[] p)
  {
    super(p);
  }
  





  public Point3f(Point3f p1)
  {
    super(p1);
  }
  





  public Point3f(Point3d p1)
  {
    super(p1);
  }
  





  public Point3f(Tuple3f t1)
  {
    super(t1);
  }
  





  public Point3f(Tuple3d t1)
  {
    super(t1);
  }
  








  public Point3f() {}
  








  public final float distanceSquared(Point3f p1)
  {
    float dx = x - x;
    float dy = y - y;
    float dz = z - z;
    return dx * dx + dy * dy + dz * dz;
  }
  








  public final float distance(Point3f p1)
  {
    float dx = x - x;
    float dy = y - y;
    float dz = z - z;
    return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
  }
  








  public final float distanceL1(Point3f p1)
  {
    return Math.abs(x - x) + Math.abs(y - y) + Math.abs(z - z);
  }
  









  public final float distanceLinf(Point3f p1)
  {
    float tmp = Math.max(Math.abs(x - x), Math.abs(y - y));
    return Math.max(tmp, Math.abs(z - z));
  }
  









  public final void project(Point4f p1)
  {
    float oneOw = 1.0F / w;
    x = (x * oneOw);
    y = (y * oneOw);
    z = (z * oneOw);
  }
}
