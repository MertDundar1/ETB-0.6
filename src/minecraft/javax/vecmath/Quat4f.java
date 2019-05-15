package javax.vecmath;

import java.io.Serializable;













































public class Quat4f
  extends Tuple4f
  implements Serializable
{
  static final long serialVersionUID = 2675933778405442383L;
  static final double EPS = 1.0E-6D;
  static final double EPS2 = 1.0E-30D;
  static final double PIO2 = 1.57079632679D;
  
  public Quat4f(float x, float y, float z, float w)
  {
    float mag = (float)(1.0D / Math.sqrt(x * x + y * y + z * z + w * w));
    this.x = (x * mag);
    this.y = (y * mag);
    this.z = (z * mag);
    this.w = (w * mag);
  }
  






  public Quat4f(float[] q)
  {
    float mag = (float)(1.0D / Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]));
    x = (q[0] * mag);
    y = (q[1] * mag);
    z = (q[2] * mag);
    w = (q[3] * mag);
  }
  






  public Quat4f(Quat4f q1)
  {
    super(q1);
  }
  




  public Quat4f(Quat4d q1)
  {
    super(q1);
  }
  






  public Quat4f(Tuple4f t1)
  {
    float mag = (float)(1.0D / Math.sqrt(x * x + y * y + z * z + w * w));
    x = (x * mag);
    y = (y * mag);
    z = (z * mag);
    w = (w * mag);
  }
  







  public Quat4f(Tuple4d t1)
  {
    double mag = 1.0D / Math.sqrt(x * x + y * y + z * z + w * w);
    x = ((float)(x * mag));
    y = ((float)(y * mag));
    z = ((float)(z * mag));
    w = ((float)(w * mag));
  }
  






  public Quat4f() {}
  






  public final void conjugate(Quat4f q1)
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
  








  public final void mul(Quat4f q1, Quat4f q2)
  {
    if ((this != q1) && (this != q2)) {
      this.w = (w * w - x * x - y * y - z * z);
      this.x = (w * x + w * x + y * z - z * y);
      this.y = (w * y + w * y - x * z + z * x);
      z = (w * z + w * z + x * y - y * x);
    }
    else
    {
      float w = w * w - x * x - y * y - z * z;
      float x = w * x + w * x + y * z - z * y;
      float y = w * y + w * y - x * z + z * x;
      z = (w * z + w * z + x * y - y * x);
      this.w = w;
      this.x = x;
      this.y = y;
    }
  }
  








  public final void mul(Quat4f q1)
  {
    float w = this.w * w - this.x * x - this.y * y - z * z;
    float x = this.w * x + w * this.x + this.y * z - z * y;
    float y = this.w * y + w * this.y - this.x * z + z * x;
    z = (this.w * z + w * z + this.x * y - this.y * x);
    this.w = w;
    this.x = x;
    this.y = y;
  }
  








  public final void mulInverse(Quat4f q1, Quat4f q2)
  {
    Quat4f tempQuat = new Quat4f(q2);
    
    tempQuat.inverse();
    mul(q1, tempQuat);
  }
  








  public final void mulInverse(Quat4f q1)
  {
    Quat4f tempQuat = new Quat4f(q1);
    
    tempQuat.inverse();
    mul(tempQuat);
  }
  








  public final void inverse(Quat4f q1)
  {
    float norm = 1.0F / (w * w + x * x + y * y + z * z);
    w = (norm * w);
    x = (-norm * x);
    y = (-norm * y);
    z = (-norm * z);
  }
  






  public final void inverse()
  {
    float norm = 1.0F / (w * w + x * x + y * y + z * z);
    w *= norm;
    x *= -norm;
    y *= -norm;
    z *= -norm;
  }
  








  public final void normalize(Quat4f q1)
  {
    float norm = x * x + y * y + z * z + w * w;
    
    if (norm > 0.0F) {
      norm = 1.0F / (float)Math.sqrt(norm);
      x = (norm * x);
      y = (norm * y);
      z = (norm * z);
      w = (norm * w);
    } else {
      x = 0.0F;
      y = 0.0F;
      z = 0.0F;
      w = 0.0F;
    }
  }
  






  public final void normalize()
  {
    float norm = x * x + y * y + z * z + w * w;
    
    if (norm > 0.0F) {
      norm = 1.0F / (float)Math.sqrt(norm);
      x *= norm;
      y *= norm;
      z *= norm;
      w *= norm;
    } else {
      x = 0.0F;
      y = 0.0F;
      z = 0.0F;
      w = 0.0F;
    }
  }
  






  public final void set(Matrix4f m1)
  {
    float ww = 0.25F * (m00 + m11 + m22 + m33);
    
    if (ww >= 0.0F) {
      if (ww >= 1.0E-30D) {
        w = ((float)Math.sqrt(ww));
        ww = 0.25F / w;
        x = ((m21 - m12) * ww);
        y = ((m02 - m20) * ww);
        z = ((m10 - m01) * ww);
      }
    }
    else {
      w = 0.0F;
      x = 0.0F;
      y = 0.0F;
      z = 1.0F;
      return;
    }
    
    w = 0.0F;
    ww = -0.5F * (m11 + m22);
    
    if (ww >= 0.0F) {
      if (ww >= 1.0E-30D) {
        x = ((float)Math.sqrt(ww));
        ww = 1.0F / (2.0F * x);
        y = (m10 * ww);
        z = (m20 * ww);
      }
    }
    else {
      x = 0.0F;
      y = 0.0F;
      z = 1.0F;
      return;
    }
    
    x = 0.0F;
    ww = 0.5F * (1.0F - m22);
    
    if (ww >= 1.0E-30D) {
      y = ((float)Math.sqrt(ww));
      z = (m21 / (2.0F * y));
      return;
    }
    
    y = 0.0F;
    z = 1.0F;
  }
  






  public final void set(Matrix4d m1)
  {
    double ww = 0.25D * (m00 + m11 + m22 + m33);
    
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        w = ((float)Math.sqrt(ww));
        ww = 0.25D / w;
        x = ((float)((m21 - m12) * ww));
        y = ((float)((m02 - m20) * ww));
        z = ((float)((m10 - m01) * ww));
      }
    }
    else {
      w = 0.0F;
      x = 0.0F;
      y = 0.0F;
      z = 1.0F;
      return;
    }
    
    w = 0.0F;
    ww = -0.5D * (m11 + m22);
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        x = ((float)Math.sqrt(ww));
        ww = 0.5D / x;
        y = ((float)(m10 * ww));
        z = ((float)(m20 * ww));
      }
    }
    else {
      x = 0.0F;
      y = 0.0F;
      z = 1.0F;
      return;
    }
    
    x = 0.0F;
    ww = 0.5D * (1.0D - m22);
    if (ww >= 1.0E-30D) {
      y = ((float)Math.sqrt(ww));
      z = ((float)(m21 / (2.0D * y)));
      return;
    }
    
    y = 0.0F;
    z = 1.0F;
  }
  






  public final void set(Matrix3f m1)
  {
    float ww = 0.25F * (m00 + m11 + m22 + 1.0F);
    
    if (ww >= 0.0F) {
      if (ww >= 1.0E-30D) {
        w = ((float)Math.sqrt(ww));
        ww = 0.25F / w;
        x = ((m21 - m12) * ww);
        y = ((m02 - m20) * ww);
        z = ((m10 - m01) * ww);
      }
    }
    else {
      w = 0.0F;
      x = 0.0F;
      y = 0.0F;
      z = 1.0F;
      return;
    }
    
    w = 0.0F;
    ww = -0.5F * (m11 + m22);
    if (ww >= 0.0F) {
      if (ww >= 1.0E-30D) {
        x = ((float)Math.sqrt(ww));
        ww = 0.5F / x;
        y = (m10 * ww);
        z = (m20 * ww);
      }
    }
    else {
      x = 0.0F;
      y = 0.0F;
      z = 1.0F;
      return;
    }
    
    x = 0.0F;
    ww = 0.5F * (1.0F - m22);
    if (ww >= 1.0E-30D) {
      y = ((float)Math.sqrt(ww));
      z = (m21 / (2.0F * y));
      return;
    }
    
    y = 0.0F;
    z = 1.0F;
  }
  






  public final void set(Matrix3d m1)
  {
    double ww = 0.25D * (m00 + m11 + m22 + 1.0D);
    
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        w = ((float)Math.sqrt(ww));
        ww = 0.25D / w;
        x = ((float)((m21 - m12) * ww));
        y = ((float)((m02 - m20) * ww));
        z = ((float)((m10 - m01) * ww));
      }
    }
    else {
      w = 0.0F;
      x = 0.0F;
      y = 0.0F;
      z = 1.0F;
      return;
    }
    
    w = 0.0F;
    ww = -0.5D * (m11 + m22);
    if (ww >= 0.0D) {
      if (ww >= 1.0E-30D) {
        x = ((float)Math.sqrt(ww));
        ww = 0.5D / x;
        y = ((float)(m10 * ww));
        z = ((float)(m20 * ww));
      }
    }
    else {
      x = 0.0F;
      y = 0.0F;
      z = 1.0F;
      return;
    }
    
    x = 0.0F;
    ww = 0.5D * (1.0D - m22);
    if (ww >= 1.0E-30D) {
      y = ((float)Math.sqrt(ww));
      z = ((float)(m21 / (2.0D * y)));
      return;
    }
    
    y = 0.0F;
    z = 1.0F;
  }
  








  public final void set(AxisAngle4f a)
  {
    float amag = (float)Math.sqrt(x * x + y * y + z * z);
    if (amag < 1.0E-6D) {
      w = 0.0F;
      x = 0.0F;
      y = 0.0F;
      z = 0.0F;
    } else {
      amag = 1.0F / amag;
      float mag = (float)Math.sin(angle / 2.0D);
      w = ((float)Math.cos(angle / 2.0D));
      x = (x * amag * mag);
      y = (y * amag * mag);
      z = (z * amag * mag);
    }
  }
  









  public final void set(AxisAngle4d a)
  {
    float amag = (float)(1.0D / Math.sqrt(x * x + y * y + z * z));
    
    if (amag < 1.0E-6D) {
      w = 0.0F;
      x = 0.0F;
      y = 0.0F;
      z = 0.0F;
    } else {
      amag = 1.0F / amag;
      float mag = (float)Math.sin(angle / 2.0D);
      w = ((float)Math.cos(angle / 2.0D));
      x = ((float)x * amag * mag);
      y = ((float)y * amag * mag);
      z = ((float)z * amag * mag);
    }
  }
  

















  public final void interpolate(Quat4f q1, float alpha)
  {
    double dot = x * x + y * y + z * z + w * w;
    
    if (dot < 0.0D)
    {
      x = (-x);y = (-y);z = (-z);w = (-w);
      dot = -dot; }
    double s2;
    double s1;
    double s2; if (1.0D - dot > 1.0E-6D) {
      double om = Math.acos(dot);
      double sinom = Math.sin(om);
      double s1 = Math.sin((1.0D - alpha) * om) / sinom;
      s2 = Math.sin(alpha * om) / sinom;
    } else {
      s1 = 1.0D - alpha;
      s2 = alpha;
    }
    
    w = ((float)(s1 * w + s2 * w));
    x = ((float)(s1 * x + s2 * x));
    y = ((float)(s1 * y + s2 * y));
    z = ((float)(s1 * z + s2 * z));
  }
  

















  public final void interpolate(Quat4f q1, Quat4f q2, float alpha)
  {
    double dot = x * x + y * y + z * z + w * w;
    
    if (dot < 0.0D)
    {
      x = (-x);y = (-y);z = (-z);w = (-w);
      dot = -dot; }
    double s2;
    double s1;
    double s2; if (1.0D - dot > 1.0E-6D) {
      double om = Math.acos(dot);
      double sinom = Math.sin(om);
      double s1 = Math.sin((1.0D - alpha) * om) / sinom;
      s2 = Math.sin(alpha * om) / sinom;
    } else {
      s1 = 1.0D - alpha;
      s2 = alpha;
    }
    w = ((float)(s1 * w + s2 * w));
    x = ((float)(s1 * x + s2 * x));
    y = ((float)(s1 * y + s2 * y));
    z = ((float)(s1 * z + s2 * z));
  }
}
