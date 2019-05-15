package javax.vecmath;

import java.io.Serializable;




























































public abstract class Tuple4d
  implements Serializable, Cloneable
{
  static final long serialVersionUID = -4748953690425311052L;
  public double x;
  public double y;
  public double z;
  public double w;
  
  public Tuple4d(double x, double y, double z, double w)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  






  public Tuple4d(double[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
    w = t[3];
  }
  





  public Tuple4d(Tuple4d t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  





  public Tuple4d(Tuple4f t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  




  public Tuple4d()
  {
    x = 0.0D;
    y = 0.0D;
    z = 0.0D;
    w = 0.0D;
  }
  








  public final void set(double x, double y, double z, double w)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  





  public final void set(double[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
    w = t[3];
  }
  





  public final void set(Tuple4d t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  





  public final void set(Tuple4f t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  






  public final void get(double[] t)
  {
    t[0] = x;
    t[1] = y;
    t[2] = z;
    t[3] = w;
  }
  







  public final void get(Tuple4d t)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  






  public final void add(Tuple4d t1, Tuple4d t2)
  {
    x += x;
    y += y;
    z += z;
    w += w;
  }
  





  public final void add(Tuple4d t1)
  {
    x += x;
    y += y;
    z += z;
    w += w;
  }
  







  public final void sub(Tuple4d t1, Tuple4d t2)
  {
    x -= x;
    y -= y;
    z -= z;
    w -= w;
  }
  






  public final void sub(Tuple4d t1)
  {
    x -= x;
    y -= y;
    z -= z;
    w -= w;
  }
  





  public final void negate(Tuple4d t1)
  {
    x = (-x);
    y = (-y);
    z = (-z);
    w = (-w);
  }
  




  public final void negate()
  {
    x = (-x);
    y = (-y);
    z = (-z);
    w = (-w);
  }
  







  public final void scale(double s, Tuple4d t1)
  {
    x = (s * x);
    y = (s * y);
    z = (s * z);
    w = (s * w);
  }
  






  public final void scale(double s)
  {
    x *= s;
    y *= s;
    z *= s;
    w *= s;
  }
  








  public final void scaleAdd(double s, Tuple4d t1, Tuple4d t2)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
    w = (s * w + w);
  }
  

  /**
   * @deprecated
   */
  public final void scaleAdd(float s, Tuple4d t1)
  {
    scaleAdd(s, t1);
  }
  






  public final void scaleAdd(double s, Tuple4d t1)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
    w = (s * w + w);
  }
  






  public String toString()
  {
    return "(" + x + ", " + y + ", " + z + ", " + w + ")";
  }
  






  public boolean equals(Tuple4d t1)
  {
    try
    {
      return (x == x) && (y == y) && (z == z) && (w == w);
    }
    catch (NullPointerException e2) {}
    return false;
  }
  







  public boolean equals(Object t1)
  {
    try
    {
      Tuple4d t2 = (Tuple4d)t1;
      return (x == x) && (y == y) && (z == z) && (w == w);
    }
    catch (NullPointerException e2) {
      return false; } catch (ClassCastException e1) {}
    return false;
  }
  













  public boolean epsilonEquals(Tuple4d t1, double epsilon)
  {
    double diff = x - x;
    if (Double.isNaN(diff)) return false;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = y - y;
    if (Double.isNaN(diff)) return false;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = z - z;
    if (Double.isNaN(diff)) return false;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = w - w;
    if (Double.isNaN(diff)) return false;
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
    bits = 31L * bits + VecMathUtil.doubleToLongBits(w);
    return (int)(bits ^ bits >> 32);
  }
  
  /**
   * @deprecated
   */
  public final void clamp(float min, float max, Tuple4d t)
  {
    clamp(min, max, t);
  }
  







  public final void clamp(double min, double max, Tuple4d t)
  {
    if (x > max) {
      x = max;
    } else if (x < min) {
      x = min;
    } else {
      x = x;
    }
    
    if (y > max) {
      y = max;
    } else if (y < min) {
      y = min;
    } else {
      y = y;
    }
    
    if (z > max) {
      z = max;
    } else if (z < min) {
      z = min;
    } else {
      z = z;
    }
    
    if (w > max) {
      w = max;
    } else if (w < min) {
      w = min;
    } else {
      w = w;
    }
  }
  

  /**
   * @deprecated
   */
  public final void clampMin(float min, Tuple4d t)
  {
    clampMin(min, t);
  }
  






  public final void clampMin(double min, Tuple4d t)
  {
    if (x < min) {
      x = min;
    } else {
      x = x;
    }
    
    if (y < min) {
      y = min;
    } else {
      y = y;
    }
    
    if (z < min) {
      z = min;
    } else {
      z = z;
    }
    
    if (w < min) {
      w = min;
    } else {
      w = w;
    }
  }
  

  /**
   * @deprecated
   */
  public final void clampMax(float max, Tuple4d t)
  {
    clampMax(max, t);
  }
  






  public final void clampMax(double max, Tuple4d t)
  {
    if (x > max) {
      x = max;
    } else {
      x = x;
    }
    
    if (y > max) {
      y = max;
    } else {
      y = y;
    }
    
    if (z > max) {
      z = max;
    } else {
      z = z;
    }
    
    if (w > max) {
      w = max;
    } else {
      w = z;
    }
  }
  







  public final void absolute(Tuple4d t)
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
    w = Math.abs(w);
  }
  


  /**
   * @deprecated
   */
  public final void clamp(float min, float max)
  {
    clamp(min, max);
  }
  





  public final void clamp(double min, double max)
  {
    if (x > max) {
      x = max;
    } else if (x < min) {
      x = min;
    }
    
    if (y > max) {
      y = max;
    } else if (y < min) {
      y = min;
    }
    
    if (z > max) {
      z = max;
    } else if (z < min) {
      z = min;
    }
    
    if (w > max) {
      w = max;
    } else if (w < min) {
      w = min;
    }
  }
  

  /**
   * @deprecated
   */
  public final void clampMin(float min)
  {
    clampMin(min);
  }
  




  public final void clampMin(double min)
  {
    if (x < min) x = min;
    if (y < min) y = min;
    if (z < min) z = min;
    if (w < min) w = min;
  }
  
  /**
   * @deprecated
   */
  public final void clampMax(float max)
  {
    clampMax(max);
  }
  




  public final void clampMax(double max)
  {
    if (x > max) x = max;
    if (y > max) y = max;
    if (z > max) z = max;
    if (w > max) { w = max;
    }
  }
  




  public final void absolute()
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
    w = Math.abs(w);
  }
  

  /**
   * @deprecated
   */
  public void interpolate(Tuple4d t1, Tuple4d t2, float alpha)
  {
    interpolate(t1, t2, alpha);
  }
  







  public void interpolate(Tuple4d t1, Tuple4d t2, double alpha)
  {
    x = ((1.0D - alpha) * x + alpha * x);
    y = ((1.0D - alpha) * y + alpha * y);
    z = ((1.0D - alpha) * z + alpha * z);
    w = ((1.0D - alpha) * w + alpha * w);
  }
  
  /**
   * @deprecated
   */
  public void interpolate(Tuple4d t1, float alpha)
  {
    interpolate(t1, alpha);
  }
  






  public void interpolate(Tuple4d t1, double alpha)
  {
    x = ((1.0D - alpha) * x + alpha * x);
    y = ((1.0D - alpha) * y + alpha * y);
    z = ((1.0D - alpha) * z + alpha * z);
    w = ((1.0D - alpha) * w + alpha * w);
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
  






  public final double getX()
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
  






  public final double getZ()
  {
    return z;
  }
  







  public final void setZ(double z)
  {
    this.z = z;
  }
  







  public final double getW()
  {
    return w;
  }
  







  public final void setW(double w)
  {
    this.w = w;
  }
}
