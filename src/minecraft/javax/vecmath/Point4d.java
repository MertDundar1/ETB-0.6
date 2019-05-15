package javax.vecmath;

import java.io.Serializable;












































public class Point4d
  extends Tuple4d
  implements Serializable
{
  static final long serialVersionUID = 1733471895962736949L;
  
  public Point4d(double x, double y, double z, double w)
  {
    super(x, y, z, w);
  }
  





  public Point4d(double[] p)
  {
    super(p);
  }
  





  public Point4d(Point4d p1)
  {
    super(p1);
  }
  





  public Point4d(Point4f p1)
  {
    super(p1);
  }
  





  public Point4d(Tuple4f t1)
  {
    super(t1);
  }
  





  public Point4d(Tuple4d t1)
  {
    super(t1);
  }
  









  public Point4d(Tuple3d t1)
  {
    super(x, y, z, 1.0D);
  }
  








  public Point4d() {}
  







  public final void set(Tuple3d t1)
  {
    x = x;
    y = y;
    z = z;
    w = 1.0D;
  }
  








  public final double distanceSquared(Point4d p1)
  {
    double dx = x - x;
    double dy = y - y;
    double dz = z - z;
    double dw = w - w;
    return dx * dx + dy * dy + dz * dz + dw * dw;
  }
  








  public final double distance(Point4d p1)
  {
    double dx = x - x;
    double dy = y - y;
    double dz = z - z;
    double dw = w - w;
    return Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
  }
  








  public final double distanceL1(Point4d p1)
  {
    return Math.abs(x - x) + Math.abs(y - y) + Math.abs(z - z) + Math.abs(w - w);
  }
  







  public final double distanceLinf(Point4d p1)
  {
    double t1 = Math.max(Math.abs(x - x), Math.abs(y - y));
    double t2 = Math.max(Math.abs(z - z), Math.abs(w - w));
    
    return Math.max(t1, t2);
  }
  








  public final void project(Point4d p1)
  {
    double oneOw = 1.0D / w;
    x *= oneOw;
    y *= oneOw;
    z *= oneOw;
    w = 1.0D;
  }
}
