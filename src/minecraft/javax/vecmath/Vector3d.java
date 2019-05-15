package javax.vecmath;

import java.io.Serializable;











































public class Vector3d
  extends Tuple3d
  implements Serializable
{
  static final long serialVersionUID = 3761969948420550442L;
  
  public Vector3d(double x, double y, double z)
  {
    super(x, y, z);
  }
  





  public Vector3d(double[] v)
  {
    super(v);
  }
  





  public Vector3d(Vector3d v1)
  {
    super(v1);
  }
  





  public Vector3d(Vector3f v1)
  {
    super(v1);
  }
  





  public Vector3d(Tuple3f t1)
  {
    super(t1);
  }
  





  public Vector3d(Tuple3d t1)
  {
    super(t1);
  }
  








  public Vector3d() {}
  







  public final void cross(Vector3d v1, Vector3d v2)
  {
    double x = y * z - z * y;
    double y = x * z - z * x;
    z = (x * y - y * x);
    this.x = x;
    this.y = y;
  }
  







  public final void normalize(Vector3d v1)
  {
    double norm = 1.0D / Math.sqrt(x * x + y * y + z * z);
    x *= norm;
    y *= norm;
    z *= norm;
  }
  






  public final void normalize()
  {
    double norm = 1.0D / Math.sqrt(x * x + y * y + z * z);
    x *= norm;
    y *= norm;
    z *= norm;
  }
  






  public final double dot(Vector3d v1)
  {
    return x * x + y * y + z * z;
  }
  





  public final double lengthSquared()
  {
    return x * x + y * y + z * z;
  }
  





  public final double length()
  {
    return Math.sqrt(x * x + y * y + z * z);
  }
  







  public final double angle(Vector3d v1)
  {
    double vDot = dot(v1) / (length() * v1.length());
    if (vDot < -1.0D) vDot = -1.0D;
    if (vDot > 1.0D) vDot = 1.0D;
    return Math.acos(vDot);
  }
}
