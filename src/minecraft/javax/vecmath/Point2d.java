package javax.vecmath;

import java.io.Serializable;









































public class Point2d
  extends Tuple2d
  implements Serializable
{
  static final long serialVersionUID = 1133748791492571954L;
  
  public Point2d(double x, double y)
  {
    super(x, y);
  }
  





  public Point2d(double[] p)
  {
    super(p);
  }
  





  public Point2d(Point2d p1)
  {
    super(p1);
  }
  





  public Point2d(Point2f p1)
  {
    super(p1);
  }
  





  public Point2d(Tuple2d t1)
  {
    super(t1);
  }
  





  public Point2d(Tuple2f t1)
  {
    super(t1);
  }
  







  public Point2d() {}
  






  public final double distanceSquared(Point2d p1)
  {
    double dx = x - x;
    double dy = y - y;
    return dx * dx + dy * dy;
  }
  






  public final double distance(Point2d p1)
  {
    double dx = x - x;
    double dy = y - y;
    return Math.sqrt(dx * dx + dy * dy);
  }
  






  public final double distanceL1(Point2d p1)
  {
    return Math.abs(x - x) + Math.abs(y - y);
  }
  






  public final double distanceLinf(Point2d p1)
  {
    return Math.max(Math.abs(x - x), Math.abs(y - y));
  }
}
