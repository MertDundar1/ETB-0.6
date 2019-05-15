package javax.vecmath;

import java.io.Serializable;






























































public class AxisAngle4f
  implements Serializable, Cloneable
{
  static final long serialVersionUID = -163246355858070601L;
  public float x;
  public float y;
  public float z;
  public float angle;
  static final double EPS = 1.0E-6D;
  
  public AxisAngle4f(float x, float y, float z, float angle)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.angle = angle;
  }
  





  public AxisAngle4f(float[] a)
  {
    x = a[0];
    y = a[1];
    z = a[2];
    angle = a[3];
  }
  






  public AxisAngle4f(AxisAngle4f a1)
  {
    x = x;
    y = y;
    z = z;
    angle = angle;
  }
  





  public AxisAngle4f(AxisAngle4d a1)
  {
    x = ((float)x);
    y = ((float)y);
    z = ((float)z);
    angle = ((float)angle);
  }
  








  public AxisAngle4f(Vector3f axis, float angle)
  {
    x = x;
    y = y;
    z = z;
    this.angle = angle;
  }
  




  public AxisAngle4f()
  {
    x = 0.0F;
    y = 0.0F;
    z = 1.0F;
    angle = 0.0F;
  }
  








  public final void set(float x, float y, float z, float angle)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.angle = angle;
  }
  






  public final void set(float[] a)
  {
    x = a[0];
    y = a[1];
    z = a[2];
    angle = a[3];
  }
  





  public final void set(AxisAngle4f a1)
  {
    x = x;
    y = y;
    z = z;
    angle = angle;
  }
  





  public final void set(AxisAngle4d a1)
  {
    x = ((float)x);
    y = ((float)y);
    z = ((float)z);
    angle = ((float)angle);
  }
  








  public final void set(Vector3f axis, float angle)
  {
    x = x;
    y = y;
    z = z;
    this.angle = angle;
  }
  





  public final void get(float[] a)
  {
    a[0] = x;
    a[1] = y;
    a[2] = z;
    a[3] = angle;
  }
  








  public final void set(Quat4f q1)
  {
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-6D) {
      mag = Math.sqrt(mag);
      double invMag = 1.0D / mag;
      
      x = ((float)(x * invMag));
      y = ((float)(y * invMag));
      z = ((float)(z * invMag));
      angle = ((float)(2.0D * Math.atan2(mag, w)));
    } else {
      x = 0.0F;
      y = 1.0F;
      z = 0.0F;
      angle = 0.0F;
    }
  }
  








  public final void set(Quat4d q1)
  {
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-6D) {
      mag = Math.sqrt(mag);
      double invMag = 1.0D / mag;
      
      x = ((float)(x * invMag));
      y = ((float)(y * invMag));
      z = ((float)(z * invMag));
      angle = ((float)(2.0D * Math.atan2(mag, w)));
    } else {
      x = 0.0F;
      y = 1.0F;
      z = 0.0F;
      angle = 0.0F;
    }
  }
  








  public final void set(Matrix4f m1)
  {
    Matrix3f m3f = new Matrix3f();
    
    m1.get(m3f);
    
    x = (m21 - m12);
    y = (m02 - m20);
    z = (m10 - m01);
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-6D) {
      mag = Math.sqrt(mag);
      double sin = 0.5D * mag;
      double cos = 0.5D * (m00 + m11 + m22 - 1.0D);
      
      angle = ((float)Math.atan2(sin, cos));
      double invMag = 1.0D / mag;
      x = ((float)(x * invMag));
      y = ((float)(y * invMag));
      z = ((float)(z * invMag));
    } else {
      x = 0.0F;
      y = 1.0F;
      z = 0.0F;
      angle = 0.0F;
    }
  }
  










  public final void set(Matrix4d m1)
  {
    Matrix3d m3d = new Matrix3d();
    
    m1.get(m3d);
    

    x = ((float)(m21 - m12));
    y = ((float)(m02 - m20));
    z = ((float)(m10 - m01));
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-6D) {
      mag = Math.sqrt(mag);
      double sin = 0.5D * mag;
      double cos = 0.5D * (m00 + m11 + m22 - 1.0D);
      angle = ((float)Math.atan2(sin, cos));
      
      double invMag = 1.0D / mag;
      x = ((float)(x * invMag));
      y = ((float)(y * invMag));
      z = ((float)(z * invMag));
    } else {
      x = 0.0F;
      y = 1.0F;
      z = 0.0F;
      angle = 0.0F;
    }
  }
  









  public final void set(Matrix3f m1)
  {
    x = (m21 - m12);
    y = (m02 - m20);
    z = (m10 - m01);
    double mag = x * x + y * y + z * z;
    if (mag > 1.0E-6D) {
      mag = Math.sqrt(mag);
      double sin = 0.5D * mag;
      double cos = 0.5D * (m00 + m11 + m22 - 1.0D);
      
      angle = ((float)Math.atan2(sin, cos));
      
      double invMag = 1.0D / mag;
      x = ((float)(x * invMag));
      y = ((float)(y * invMag));
      z = ((float)(z * invMag));
    } else {
      x = 0.0F;
      y = 1.0F;
      z = 0.0F;
      angle = 0.0F;
    }
  }
  










  public final void set(Matrix3d m1)
  {
    x = ((float)(m21 - m12));
    y = ((float)(m02 - m20));
    z = ((float)(m10 - m01));
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-6D) {
      mag = Math.sqrt(mag);
      double sin = 0.5D * mag;
      double cos = 0.5D * (m00 + m11 + m22 - 1.0D);
      
      angle = ((float)Math.atan2(sin, cos));
      
      double invMag = 1.0D / mag;
      x = ((float)(x * invMag));
      y = ((float)(y * invMag));
      z = ((float)(z * invMag));
    } else {
      x = 0.0F;
      y = 1.0F;
      z = 0.0F;
      angle = 0.0F;
    }
  }
  





  public String toString()
  {
    return "(" + x + ", " + y + ", " + z + ", " + angle + ")";
  }
  






  public boolean equals(AxisAngle4f a1)
  {
    try
    {
      return (x == x) && (y == y) && (z == z) && (angle == angle);
    }
    catch (NullPointerException e2) {}
    return false;
  }
  







  public boolean equals(Object o1)
  {
    try
    {
      AxisAngle4f a2 = (AxisAngle4f)o1;
      return (x == x) && (y == y) && (z == z) && (angle == angle);
    }
    catch (NullPointerException e2) {
      return false; } catch (ClassCastException e1) {}
    return false;
  }
  












  public boolean epsilonEquals(AxisAngle4f a1, float epsilon)
  {
    float diff = x - x;
    if ((diff < 0.0F ? -diff : diff) > epsilon) { return false;
    }
    diff = y - y;
    if ((diff < 0.0F ? -diff : diff) > epsilon) { return false;
    }
    diff = z - z;
    if ((diff < 0.0F ? -diff : diff) > epsilon) { return false;
    }
    diff = angle - angle;
    if ((diff < 0.0F ? -diff : diff) > epsilon) { return false;
    }
    return true;
  }
  









  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + VecMathUtil.floatToIntBits(x);
    bits = 31L * bits + VecMathUtil.floatToIntBits(y);
    bits = 31L * bits + VecMathUtil.floatToIntBits(z);
    bits = 31L * bits + VecMathUtil.floatToIntBits(angle);
    return (int)(bits ^ bits >> 32);
  }
  







  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
  








  public final float getAngle()
  {
    return angle;
  }
  








  public final void setAngle(float angle)
  {
    this.angle = angle;
  }
  







  public final float getX()
  {
    return x;
  }
  







  public final void setX(float x)
  {
    this.x = x;
  }
  







  public final float getY()
  {
    return y;
  }
  







  public final void setY(float y)
  {
    this.y = y;
  }
  







  public final float getZ()
  {
    return z;
  }
  







  public final void setZ(float z)
  {
    this.z = z;
  }
}
