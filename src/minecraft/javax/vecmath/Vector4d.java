package javax.vecmath;

import java.io.Serializable;











































public class Vector4d
  extends Tuple4d
  implements Serializable
{
  static final long serialVersionUID = 3938123424117448700L;
  
  public Vector4d(double x, double y, double z, double w)
  {
    super(x, y, z, w);
  }
  





  public Vector4d(double[] v)
  {
    super(v);
  }
  




  public Vector4d(Vector4d v1)
  {
    super(v1);
  }
  




  public Vector4d(Vector4f v1)
  {
    super(v1);
  }
  




  public Vector4d(Tuple4f t1)
  {
    super(t1);
  }
  




  public Vector4d(Tuple4d t1)
  {
    super(t1);
  }
  









  public Vector4d(Tuple3d t1)
  {
    super(x, y, z, 0.0D);
  }
  








  public Vector4d() {}
  







  public final void set(Tuple3d t1)
  {
    x = x;
    y = y;
    z = z;
    w = 0.0D;
  }
  





  public final double length()
  {
    return Math.sqrt(x * x + y * y + z * z + w * w);
  }
  






  public final double lengthSquared()
  {
    return x * x + y * y + z * z + w * w;
  }
  







  public final double dot(Vector4d v1)
  {
    return x * x + y * y + z * z + w * w;
  }
  







  public final void normalize(Vector4d v1)
  {
    double norm = 1.0D / Math.sqrt(x * x + y * y + z * z + w * w);
    x *= norm;
    y *= norm;
    z *= norm;
    w *= norm;
  }
  






  public final void normalize()
  {
    double norm = 1.0D / Math.sqrt(x * x + y * y + z * z + w * w);
    
    x *= norm;
    y *= norm;
    z *= norm;
    w *= norm;
  }
  








  public final double angle(Vector4d v1)
  {
    double vDot = dot(v1) / (length() * v1.length());
    if (vDot < -1.0D) vDot = -1.0D;
    if (vDot > 1.0D) vDot = 1.0D;
    return Math.acos(vDot);
  }
}
