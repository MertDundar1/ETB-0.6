package javax.vecmath;

import java.io.Serializable;










































public class Point3d
  extends Tuple3d
  implements Serializable
{
  static final long serialVersionUID = 5718062286069042927L;
  
  public Point3d(double x, double y, double z)
  {
    super(x, y, z);
  }
  





  public Point3d(double[] p)
  {
    super(p);
  }
  





  public Point3d(Point3d p1)
  {
    super(p1);
  }
  





  public Point3d(Point3f p1)
  {
    super(p1);
  }
  





  public Point3d(Tuple3f t1)
  {
    super(t1);
  }
  





  public Point3d(Tuple3d t1)
  {
    super(t1);
  }
  








  public Point3d() {}
  







  public final double distanceSquared(Point3d p1)
  {
    double dx = x - x;
    double dy = y - y;
    double dz = z - z;
    return dx * dx + dy * dy + dz * dz;
  }
  








  public final double distance(Point3d p1)
  {
    double dx = x - x;
    double dy = y - y;
    double dz = z - z;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }
  








  public final double distanceL1(Point3d p1)
  {
    return Math.abs(x - x) + Math.abs(y - y) + Math.abs(z - z);
  }
  








  public final double distanceLinf(Point3d p1)
  {
    double tmp = Math.max(Math.abs(x - x), Math.abs(y - y));
    
    return Math.max(tmp, Math.abs(z - z));
  }
  








  public final void project(Point4d p1)
  {
    double oneOw = 1.0D / w;
    x = (x * oneOw);
    y = (y * oneOw);
    z = (z * oneOw);
  }
}
