package javax.vecmath;

import java.io.Serializable;











































public class Vector3f
  extends Tuple3f
  implements Serializable
{
  static final long serialVersionUID = -7031930069184524614L;
  
  public Vector3f(float x, float y, float z)
  {
    super(x, y, z);
  }
  





  public Vector3f(float[] v)
  {
    super(v);
  }
  





  public Vector3f(Vector3f v1)
  {
    super(v1);
  }
  





  public Vector3f(Vector3d v1)
  {
    super(v1);
  }
  




  public Vector3f(Tuple3f t1)
  {
    super(t1);
  }
  




  public Vector3f(Tuple3d t1)
  {
    super(t1);
  }
  






  public Vector3f() {}
  






  public final float lengthSquared()
  {
    return x * x + y * y + z * z;
  }
  





  public final float length()
  {
    return (float)Math.sqrt(x * x + y * y + z * z);
  }
  








  public final void cross(Vector3f v1, Vector3f v2)
  {
    float x = y * z - z * y;
    float y = x * z - z * x;
    z = (x * y - y * x);
    this.x = x;
    this.y = y;
  }
  





  public final float dot(Vector3f v1)
  {
    return x * x + y * y + z * z;
  }
  






  public final void normalize(Vector3f v1)
  {
    float norm = (float)(1.0D / Math.sqrt(x * x + y * y + z * z));
    x *= norm;
    y *= norm;
    z *= norm;
  }
  






  public final void normalize()
  {
    float norm = (float)(1.0D / Math.sqrt(x * x + y * y + z * z));
    x *= norm;
    y *= norm;
    z *= norm;
  }
  







  public final float angle(Vector3f v1)
  {
    double vDot = dot(v1) / (length() * v1.length());
    if (vDot < -1.0D) vDot = -1.0D;
    if (vDot > 1.0D) vDot = 1.0D;
    return (float)Math.acos(vDot);
  }
}
