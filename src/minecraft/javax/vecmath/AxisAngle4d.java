package javax.vecmath;

import java.io.Serializable;































































public class AxisAngle4d
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 3644296204459140589L;
  public double x;
  public double y;
  public double z;
  public double angle;
  static final double EPS = 1.0E-12D;
  
  public AxisAngle4d(double x, double y, double z, double angle)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.angle = angle;
  }
  






  public AxisAngle4d(double[] a)
  {
    x = a[0];
    y = a[1];
    z = a[2];
    angle = a[3];
  }
  



  public AxisAngle4d(AxisAngle4d a1)
  {
    x = x;
    y = y;
    z = z;
    angle = angle;
  }
  






  public AxisAngle4d(AxisAngle4f a1)
  {
    x = x;
    y = y;
    z = z;
    angle = angle;
  }
  








  public AxisAngle4d(Vector3d axis, double angle)
  {
    x = x;
    y = y;
    z = z;
    this.angle = angle;
  }
  




  public AxisAngle4d()
  {
    x = 0.0D;
    y = 0.0D;
    z = 1.0D;
    angle = 0.0D;
  }
  








  public final void set(double x, double y, double z, double angle)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.angle = angle;
  }
  





  public final void set(double[] a)
  {
    x = a[0];
    y = a[1];
    z = a[2];
    angle = a[3];
  }
  





  public final void set(AxisAngle4d a1)
  {
    x = x;
    y = y;
    z = z;
    angle = angle;
  }
  





  public final void set(AxisAngle4f a1)
  {
    x = x;
    y = y;
    z = z;
    angle = angle;
  }
  








  public final void set(Vector3d axis, double angle)
  {
    x = x;
    y = y;
    z = z;
    this.angle = angle;
  }
  






  public final void get(double[] a)
  {
    a[0] = x;
    a[1] = y;
    a[2] = z;
    a[3] = angle;
  }
  









  public final void set(Matrix4f m1)
  {
    Matrix3d m3d = new Matrix3d();
    
    m1.get(m3d);
    
    x = ((float)(m21 - m12));
    y = ((float)(m02 - m20));
    z = ((float)(m10 - m01));
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-12D) {
      mag = Math.sqrt(mag);
      double sin = 0.5D * mag;
      double cos = 0.5D * (m00 + m11 + m22 - 1.0D);
      
      angle = ((float)Math.atan2(sin, cos));
      
      double invMag = 1.0D / mag;
      x *= invMag;
      y *= invMag;
      z *= invMag;
    } else {
      x = 0.0D;
      y = 1.0D;
      z = 0.0D;
      angle = 0.0D;
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
    
    if (mag > 1.0E-12D) {
      mag = Math.sqrt(mag);
      
      double sin = 0.5D * mag;
      double cos = 0.5D * (m00 + m11 + m22 - 1.0D);
      angle = ((float)Math.atan2(sin, cos));
      
      double invMag = 1.0D / mag;
      x *= invMag;
      y *= invMag;
      z *= invMag;
    } else {
      x = 0.0D;
      y = 1.0D;
      z = 0.0D;
      angle = 0.0D;
    }
  }
  








  public final void set(Matrix3f m1)
  {
    x = (m21 - m12);
    y = (m02 - m20);
    z = (m10 - m01);
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-12D) {
      mag = Math.sqrt(mag);
      
      double sin = 0.5D * mag;
      double cos = 0.5D * (m00 + m11 + m22 - 1.0D);
      angle = ((float)Math.atan2(sin, cos));
      
      double invMag = 1.0D / mag;
      x *= invMag;
      y *= invMag;
      z *= invMag;
    } else {
      x = 0.0D;
      y = 1.0D;
      z = 0.0D;
      angle = 0.0D;
    }
  }
  








  public final void set(Matrix3d m1)
  {
    x = ((float)(m21 - m12));
    y = ((float)(m02 - m20));
    z = ((float)(m10 - m01));
    
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-12D) {
      mag = Math.sqrt(mag);
      
      double sin = 0.5D * mag;
      double cos = 0.5D * (m00 + m11 + m22 - 1.0D);
      
      angle = ((float)Math.atan2(sin, cos));
      
      double invMag = 1.0D / mag;
      x *= invMag;
      y *= invMag;
      z *= invMag;
    } else {
      x = 0.0D;
      y = 1.0D;
      z = 0.0D;
      angle = 0.0D;
    }
  }
  










  public final void set(Quat4f q1)
  {
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-12D) {
      mag = Math.sqrt(mag);
      double invMag = 1.0D / mag;
      
      x = (x * invMag);
      y = (y * invMag);
      z = (z * invMag);
      angle = (2.0D * Math.atan2(mag, w));
    } else {
      x = 0.0D;
      y = 1.0D;
      z = 0.0D;
      angle = 0.0D;
    }
  }
  








  public final void set(Quat4d q1)
  {
    double mag = x * x + y * y + z * z;
    
    if (mag > 1.0E-12D) {
      mag = Math.sqrt(mag);
      double invMag = 1.0D / mag;
      
      x = (x * invMag);
      y = (y * invMag);
      z = (z * invMag);
      angle = (2.0D * Math.atan2(mag, w));
    } else {
      x = 0.0D;
      y = 1.0D;
      z = 0.0D;
      angle = 0.0D;
    }
  }
  





  public String toString()
  {
    return "(" + x + ", " + y + ", " + z + ", " + angle + ")";
  }
  






  public boolean equals(AxisAngle4d a1)
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
      AxisAngle4d a2 = (AxisAngle4d)o1;
      return (x == x) && (y == y) && (z == z) && (angle == angle);
    }
    catch (NullPointerException e2) {
      return false; } catch (ClassCastException e1) {}
    return false;
  }
  













  public boolean epsilonEquals(AxisAngle4d a1, double epsilon)
  {
    double diff = x - x;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = y - y;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = z - z;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = angle - angle;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    return true;
  }
  








  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + VecMathUtil.doubleToLongBits(x);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(y);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(z);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(angle);
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
  








  public final double getAngle()
  {
    return angle;
  }
  








  public final void setAngle(double angle)
  {
    this.angle = angle;
  }
  







  public double getX()
  {
    return x;
  }
  







  public final void setX(double x)
  {
    this.x = x;
  }
  







  public final double getY()
  {
    return y;
  }
  







  public final void setY(double y)
  {
    this.y = y;
  }
  







  public double getZ()
  {
    return z;
  }
  







  public final void setZ(double z)
  {
    this.z = z;
  }
}
