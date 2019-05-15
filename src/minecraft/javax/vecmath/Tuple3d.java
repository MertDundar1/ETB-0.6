package javax.vecmath;

import java.io.Serializable;























































public abstract class Tuple3d
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 5542096614926168415L;
  public double x;
  public double y;
  public double z;
  
  public Tuple3d(double x, double y, double z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  




  public Tuple3d(double[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
  }
  




  public Tuple3d(Tuple3d t1)
  {
    x = x;
    y = y;
    z = z;
  }
  




  public Tuple3d(Tuple3f t1)
  {
    x = x;
    y = y;
    z = z;
  }
  



  public Tuple3d()
  {
    x = 0.0D;
    y = 0.0D;
    z = 0.0D;
  }
  






  public final void set(double x, double y, double z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  





  public final void set(double[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
  }
  




  public final void set(Tuple3d t1)
  {
    x = x;
    y = y;
    z = z;
  }
  




  public final void set(Tuple3f t1)
  {
    x = x;
    y = y;
    z = z;
  }
  





  public final void get(double[] t)
  {
    t[0] = x;
    t[1] = y;
    t[2] = z;
  }
  





  public final void get(Tuple3d t)
  {
    x = x;
    y = y;
    z = z;
  }
  






  public final void add(Tuple3d t1, Tuple3d t2)
  {
    x += x;
    y += y;
    z += z;
  }
  





  public final void add(Tuple3d t1)
  {
    x += x;
    y += y;
    z += z;
  }
  






  public final void sub(Tuple3d t1, Tuple3d t2)
  {
    x -= x;
    y -= y;
    z -= z;
  }
  





  public final void sub(Tuple3d t1)
  {
    x -= x;
    y -= y;
    z -= z;
  }
  





  public final void negate(Tuple3d t1)
  {
    x = (-x);
    y = (-y);
    z = (-z);
  }
  




  public final void negate()
  {
    x = (-x);
    y = (-y);
    z = (-z);
  }
  







  public final void scale(double s, Tuple3d t1)
  {
    x = (s * x);
    y = (s * y);
    z = (s * z);
  }
  






  public final void scale(double s)
  {
    x *= s;
    y *= s;
    z *= s;
  }
  








  public final void scaleAdd(double s, Tuple3d t1, Tuple3d t2)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
  }
  
  /**
   * @deprecated
   */
  public final void scaleAdd(double s, Tuple3f t1)
  {
    scaleAdd(s, new Point3d(t1));
  }
  






  public final void scaleAdd(double s, Tuple3d t1)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
  }
  






  public String toString()
  {
    return "(" + x + ", " + y + ", " + z + ")";
  }
  








  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + VecMathUtil.doubleToLongBits(x);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(y);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(z);
    return (int)(bits ^ bits >> 32);
  }
  






  public boolean equals(Tuple3d t1)
  {
    try
    {
      return (x == x) && (y == y) && (z == z);
    } catch (NullPointerException e2) {}
    return false;
  }
  






  public boolean equals(Object t1)
  {
    try
    {
      Tuple3d t2 = (Tuple3d)t1;
      return (x == x) && (y == y) && (z == z);
    } catch (ClassCastException e1) {
      return false; } catch (NullPointerException e2) {}
    return false;
  }
  












  public boolean epsilonEquals(Tuple3d t1, double epsilon)
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
    return true;
  }
  

  /**
   * @deprecated
   */
  public final void clamp(float min, float max, Tuple3d t)
  {
    clamp(min, max, t);
  }
  







  public final void clamp(double min, double max, Tuple3d t)
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
  }
  

  /**
   * @deprecated
   */
  public final void clampMin(float min, Tuple3d t)
  {
    clampMin(min, t);
  }
  






  public final void clampMin(double min, Tuple3d t)
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
  }
  

  /**
   * @deprecated
   */
  public final void clampMax(float max, Tuple3d t)
  {
    clampMax(max, t);
  }
  






  public final void clampMax(double max, Tuple3d t)
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
  }
  







  public final void absolute(Tuple3d t)
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
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
    if (z < min) { z = min;
    }
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
    if (z > max) { z = max;
    }
  }
  



  public final void absolute()
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
  }
  
  /**
   * @deprecated
   */
  public final void interpolate(Tuple3d t1, Tuple3d t2, float alpha)
  {
    interpolate(t1, t2, alpha);
  }
  







  public final void interpolate(Tuple3d t1, Tuple3d t2, double alpha)
  {
    x = ((1.0D - alpha) * x + alpha * x);
    y = ((1.0D - alpha) * y + alpha * y);
    z = ((1.0D - alpha) * z + alpha * z);
  }
  
  /**
   * @deprecated
   */
  public final void interpolate(Tuple3d t1, float alpha)
  {
    interpolate(t1, alpha);
  }
  






  public final void interpolate(Tuple3d t1, double alpha)
  {
    x = ((1.0D - alpha) * x + alpha * x);
    y = ((1.0D - alpha) * y + alpha * y);
    z = ((1.0D - alpha) * z + alpha * z);
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
}
