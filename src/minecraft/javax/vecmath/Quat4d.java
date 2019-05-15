package javax.vecmath;

import java.io.Serializable;












































public class Quat4d
  extends Tuple4d
  implements Serializable
{
  static final long serialVersionUID = 7577479888820201099L;
  static final double EPS = 1.0E-12D;
  static final double EPS2 = 1.0E-30D;
  static final double PIO2 = 1.57079632679D;
  
  public Quat4d(double x, double y, double z, double w)
  {
    double mag = 1.0D / Math.sqrt(x * x + y * y + z * z + w * w);
    this.x = (x * mag);
    this.y = (y * mag);
    this.z = (z * mag);
    this.w = (w * mag);
  }
  






  public Quat4d(double[] q)
  {
    double mag = 1.0D / Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
    x = (q[0] * mag);
    y = (q[1] * mag);
    z = (q[2] * mag);
    w = (q[3] * mag);
  }
  





  public Quat4d(Quat4d q1)
  {
    super(q1);
  }
  




  public Quat4d(Quat4f q1)
  {
    super(q1);
  }
  






  public Quat4d(Tuple4f t1)
  {
    double mag = 1.0D / Math.sqrt(x * x + y * y + z * z + w * w);
    x = (x * mag);
    y = (y * mag);
    z = (z * mag);
    w = (w * mag);
  }
  







  public Quat4d(Tuple4d t1)
  {
    double mag = 1.0D / Math.sqrt(x * x + y * y + z * z + w * w);
    x = (x * mag);
    y = (y * mag);
    z = (z * mag);
    w = (w * mag);
  }
  






  public Quat4d() {}
  






  public final void conjugate(Quat4d q1)
  {
    x = (-x);
    y = (-y);
    z = (-z);
    w = w;
  }
  





  public final void conjugate()
  {
    x = (-x);
    y = (-y);
    z = (-z);
  }
  








  public final void mul(Quat4d q1, Quat4d q2)
  {
    if ((this != q1) && (this != q2)) {
      this.w = (w * w - x * x - y * y - z * z);
      this.x = (w * x + w * x + y * z - z * y);
      this.y = (w * y + w * y - x * z + z * x);
      z = (w * z + w * z + x * y - y * x);
    }
    else
    {
      double w = w * w - x * x - y * y - z * z;
      double x = w * x + w * x + y * z - z * y;
      double y = w * y + w * y - x * z + z * x;
      z = (w * z + w * z + x * y - y * x);
      this.w = w;
      this.x = x;
      this.y = y;
    }
  }
  








  public final void mul(Quat4d q1)
  {
    double w = this.w * w - this.x * x - this.y * y - z * z;
    double x = this.w * x + w * this.x + this.y * z - z * y;
    double y = this.w * y + w * this.y - this.x * z + z * x;
    z = (this.w * z + w * z + this.x * y - this.y * x);
    this.w = w;
    this.x = x;
    this.y = y;
  }
  








  public final void mulInverse(Quat4d q1, Quat4d q2)
  {
    Quat4d tempQuat = new Quat4d(q2);
    
    tempQuat.inverse();
    mul(q1, tempQuat);
  }
  








  public final void mulInverse(Quat4d q1)
  {
    Quat4d tempQuat = new Quat4d(q1);
    
    tempQuat.inverse();
    mul(tempQuat);
  }
  







  public final void inverse(Quat4d q1)
  {
    double norm = 1.0D / (w * w + x * x + y * y + z * z);
    w = (norm * w);
    x = (-norm * x);
    y = (-norm * y);
    z = (-norm * z);
  }
  






  public final void inverse()
  {
    double norm = 1.0D / (w * w + x * x + y * y + z * z);
    w *= norm;
    x *= -norm;
    y *= -norm;
    z *= -norm;
  }
  








  public final void normalize(Quat4d q1)
  {
    double norm = x * x + y * y + z * z + w * w;
    
    if (norm > 0.0D) {
      norm = 1.0D / Math.sqrt(norm);
      x = (norm * x);
      y = (norm * y);
      z = (norm * z);
      w = (norm * w);
    } else {
      x = 0.0D;
      y = 0.0D;
      z = 0.0D;
      w = 0.0D;
    }
  }
  






  public final void normalize()
  {
    double norm = x * x + y * y + z * z + w * w;
    
    if (norm > 0.0D) {
      norm = 1.0D / Math.sqrt(norm);
      x *= norm;
      y *= norm;
      z *= norm;
      w *= norm;
    } else {
      x = 0.0D;
      y = 0.0D;
      z = 0.0D;
      w = 0.0D;
    }
  }
  






  public final void set(Matrix4f m1)
  {
    double ww = 0.25D * (m00 + m11 + m22 + m33);
    
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        w = Math.sqrt(ww);
        ww = 0.25D / w;
        x = ((m21 - m12) * ww);
        y = ((m02 - m20) * ww);
        z = ((m10 - m01) * ww);
      }
    }
    else {
      w = 0.0D;
      x = 0.0D;
      y = 0.0D;
      z = 1.0D;
      return;
    }
    
    w = 0.0D;
    ww = -0.5D * (m11 + m22);
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        x = Math.sqrt(ww);
        ww = 1.0D / (2.0D * x);
        y = (m10 * ww);
        z = (m20 * ww);
      }
    }
    else {
      x = 0.0D;
      y = 0.0D;
      z = 1.0D;
      return;
    }
    
    x = 0.0D;
    ww = 0.5D * (1.0D - m22);
    if (ww >= 1.0E-30D) {
      y = Math.sqrt(ww);
      z = (m21 / (2.0D * y));
      return;
    }
    
    y = 0.0D;
    z = 1.0D;
  }
  






  public final void set(Matrix4d m1)
  {
    double ww = 0.25D * (m00 + m11 + m22 + m33);
    
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        w = Math.sqrt(ww);
        ww = 0.25D / w;
        x = ((m21 - m12) * ww);
        y = ((m02 - m20) * ww);
        z = ((m10 - m01) * ww);
      }
    }
    else {
      w = 0.0D;
      x = 0.0D;
      y = 0.0D;
      z = 1.0D;
      return;
    }
    
    w = 0.0D;
    ww = -0.5D * (m11 + m22);
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        x = Math.sqrt(ww);
        ww = 0.5D / x;
        y = (m10 * ww);
        z = (m20 * ww);
      }
    }
    else {
      x = 0.0D;
      y = 0.0D;
      z = 1.0D;
      return;
    }
    
    x = 0.0D;
    ww = 0.5D * (1.0D - m22);
    if (ww >= 1.0E-30D) {
      y = Math.sqrt(ww);
      z = (m21 / (2.0D * y));
      return;
    }
    
    y = 0.0D;
    z = 1.0D;
  }
  






  public final void set(Matrix3f m1)
  {
    double ww = 0.25D * (m00 + m11 + m22 + 1.0D);
    
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        w = Math.sqrt(ww);
        ww = 0.25D / w;
        x = ((m21 - m12) * ww);
        y = ((m02 - m20) * ww);
        z = ((m10 - m01) * ww);
      }
    }
    else {
      w = 0.0D;
      x = 0.0D;
      y = 0.0D;
      z = 1.0D;
      return;
    }
    
    w = 0.0D;
    ww = -0.5D * (m11 + m22);
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        x = Math.sqrt(ww);
        ww = 0.5D / x;
        y = (m10 * ww);
        z = (m20 * ww);
      }
    }
    else {
      x = 0.0D;
      y = 0.0D;
      z = 1.0D;
      return;
    }
    
    x = 0.0D;
    ww = 0.5D * (1.0D - m22);
    if (ww >= 1.0E-30D) {
      y = Math.sqrt(ww);
      z = (m21 / (2.0D * y));
    }
    
    y = 0.0D;
    z = 1.0D;
  }
  






  public final void set(Matrix3d m1)
  {
    double ww = 0.25D * (m00 + m11 + m22 + 1.0D);
    
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        w = Math.sqrt(ww);
        ww = 0.25D / w;
        x = ((m21 - m12) * ww);
        y = ((m02 - m20) * ww);
        z = ((m10 - m01) * ww);
      }
    }
    else {
      w = 0.0D;
      x = 0.0D;
      y = 0.0D;
      z = 1.0D;
      return;
    }
    
    w = 0.0D;
    ww = -0.5D * (m11 + m22);
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        x = Math.sqrt(ww);
        ww = 0.5D / x;
        y = (m10 * ww);
        z = (m20 * ww);
      }
    }
    else {
      x = 0.0D;
      y = 0.0D;
      z = 1.0D;
      return;
    }
    
    x = 0.0D;
    ww = 0.5D * (1.0D - m22);
    if (ww >= 1.0E-30D) {
      y = Math.sqrt(ww);
      z = (m21 / (2.0D * y));
      return;
    }
    
    y = 0.0D;
    z = 1.0D;
  }
  









  public final void set(AxisAngle4f a)
  {
    double amag = Math.sqrt(x * x + y * y + z * z);
    if (amag < 1.0E-12D) {
      w = 0.0D;
      x = 0.0D;
      y = 0.0D;
      z = 0.0D;
    } else {
      double mag = Math.sin(angle / 2.0D);
      amag = 1.0D / amag;
      w = Math.cos(angle / 2.0D);
      x = (x * amag * mag);
      y = (y * amag * mag);
      z = (z * amag * mag);
    }
  }
  









  public final void set(AxisAngle4d a)
  {
    double amag = Math.sqrt(x * x + y * y + z * z);
    if (amag < 1.0E-12D) {
      w = 0.0D;
      x = 0.0D;
      y = 0.0D;
      z = 0.0D;
    } else {
      amag = 1.0D / amag;
      double mag = Math.sin(angle / 2.0D);
      w = Math.cos(angle / 2.0D);
      x = (x * amag * mag);
      y = (y * amag * mag);
      z = (z * amag * mag);
    }
  }
  















  public final void interpolate(Quat4d q1, double alpha)
  {
    double dot = x * x + y * y + z * z + w * w;
    
    if (dot < 0.0D)
    {
      x = (-x);y = (-y);z = (-z);w = (-w);
      dot = -dot; }
    double s2;
    double s1;
    double s2; if (1.0D - dot > 1.0E-12D) {
      double om = Math.acos(dot);
      double sinom = Math.sin(om);
      double s1 = Math.sin((1.0D - alpha) * om) / sinom;
      s2 = Math.sin(alpha * om) / sinom;
    } else {
      s1 = 1.0D - alpha;
      s2 = alpha;
    }
    
    w = (s1 * w + s2 * w);
    x = (s1 * x + s2 * x);
    y = (s1 * y + s2 * y);
    z = (s1 * z + s2 * z);
  }
  














  public final void interpolate(Quat4d q1, Quat4d q2, double alpha)
  {
    double dot = x * x + y * y + z * z + w * w;
    
    if (dot < 0.0D)
    {
      x = (-x);y = (-y);z = (-z);w = (-w);
      dot = -dot; }
    double s2;
    double s1;
    double s2; if (1.0D - dot > 1.0E-12D) {
      double om = Math.acos(dot);
      double sinom = Math.sin(om);
      double s1 = Math.sin((1.0D - alpha) * om) / sinom;
      s2 = Math.sin(alpha * om) / sinom;
    } else {
      s1 = 1.0D - alpha;
      s2 = alpha;
    }
    w = (s1 * w + s2 * w);
    x = (s1 * x + s2 * x);
    y = (s1 * y + s2 * y);
    z = (s1 * z + s2 * z);
  }
}
