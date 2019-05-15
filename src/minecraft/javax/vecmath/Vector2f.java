package javax.vecmath;

import java.io.Serializable;









































public class Vector2f
  extends Tuple2f
  implements Serializable
{
  static final long serialVersionUID = -2168194326883512320L;
  
  public Vector2f(float x, float y)
  {
    super(x, y);
  }
  





  public Vector2f(float[] v)
  {
    super(v);
  }
  





  public Vector2f(Vector2f v1)
  {
    super(v1);
  }
  





  public Vector2f(Vector2d v1)
  {
    super(v1);
  }
  





  public Vector2f(Tuple2f t1)
  {
    super(t1);
  }
  





  public Vector2f(Tuple2d t1)
  {
    super(t1);
  }
  







  public Vector2f() {}
  






  public final float dot(Vector2f v1)
  {
    return x * x + y * y;
  }
  





  public final float length()
  {
    return (float)Math.sqrt(x * x + y * y);
  }
  




  public final float lengthSquared()
  {
    return x * x + y * y;
  }
  






  public final void normalize(Vector2f v1)
  {
    float norm = (float)(1.0D / Math.sqrt(x * x + y * y));
    x *= norm;
    y *= norm;
  }
  






  public final void normalize()
  {
    float norm = (float)(1.0D / Math.sqrt(x * x + y * y));
    x *= norm;
    y *= norm;
  }
  







  public final float angle(Vector2f v1)
  {
    double vDot = dot(v1) / (length() * v1.length());
    if (vDot < -1.0D) vDot = -1.0D;
    if (vDot > 1.0D) vDot = 1.0D;
    return (float)Math.acos(vDot);
  }
}
