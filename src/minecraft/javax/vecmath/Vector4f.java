package javax.vecmath;

import java.io.Serializable;











































public class Vector4f
  extends Tuple4f
  implements Serializable
{
  static final long serialVersionUID = 8749319902347760659L;
  
  public Vector4f(float x, float y, float z, float w)
  {
    super(x, y, z, w);
  }
  





  public Vector4f(float[] v)
  {
    super(v);
  }
  





  public Vector4f(Vector4f v1)
  {
    super(v1);
  }
  





  public Vector4f(Vector4d v1)
  {
    super(v1);
  }
  





  public Vector4f(Tuple4f t1)
  {
    super(t1);
  }
  





  public Vector4f(Tuple4d t1)
  {
    super(t1);
  }
  









  public Vector4f(Tuple3f t1)
  {
    super(x, y, z, 0.0F);
  }
  








  public Vector4f() {}
  







  public final void set(Tuple3f t1)
  {
    x = x;
    y = y;
    z = z;
    w = 0.0F;
  }
  






  public final float length()
  {
    return (float)Math.sqrt(x * x + y * y + z * z + w * w);
  }
  





  public final float lengthSquared()
  {
    return x * x + y * y + z * z + w * w;
  }
  






  public final float dot(Vector4f v1)
  {
    return x * x + y * y + z * z + w * w;
  }
  







  public final void normalize(Vector4f v1)
  {
    float norm = (float)(1.0D / Math.sqrt(x * x + y * y + z * z + w * w));
    
    x *= norm;
    y *= norm;
    z *= norm;
    w *= norm;
  }
  






  public final void normalize()
  {
    float norm = (float)(1.0D / Math.sqrt(x * x + y * y + z * z + w * w));
    
    x *= norm;
    y *= norm;
    z *= norm;
    w *= norm;
  }
  








  public final float angle(Vector4f v1)
  {
    double vDot = dot(v1) / (length() * v1.length());
    if (vDot < -1.0D) vDot = -1.0D;
    if (vDot > 1.0D) vDot = 1.0D;
    return (float)Math.acos(vDot);
  }
}
