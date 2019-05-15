package javax.vecmath;

import java.io.Serializable;


















































public abstract class Tuple2d
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 6205762482756093838L;
  public double x;
  public double y;
  
  public Tuple2d(double x, double y)
  {
    this.x = x;
    this.y = y;
  }
  





  public Tuple2d(double[] t)
  {
    x = t[0];
    y = t[1];
  }
  





  public Tuple2d(Tuple2d t1)
  {
    x = x;
    y = y;
  }
  





  public Tuple2d(Tuple2f t1)
  {
    x = x;
    y = y;
  }
  



  public Tuple2d()
  {
    x = 0.0D;
    y = 0.0D;
  }
  






  public final void set(double x, double y)
  {
    this.x = x;
    this.y = y;
  }
  






  public final void set(double[] t)
  {
    x = t[0];
    y = t[1];
  }
  





  public final void set(Tuple2d t1)
  {
    x = x;
    y = y;
  }
  





  public final void set(Tuple2f t1)
  {
    x = x;
    y = y;
  }
  




  public final void get(double[] t)
  {
    t[0] = x;
    t[1] = y;
  }
  






  public final void add(Tuple2d t1, Tuple2d t2)
  {
    x += x;
    y += y;
  }
  





  public final void add(Tuple2d t1)
  {
    x += x;
    y += y;
  }
  







  public final void sub(Tuple2d t1, Tuple2d t2)
  {
    x -= x;
    y -= y;
  }
  






  public final void sub(Tuple2d t1)
  {
    x -= x;
    y -= y;
  }
  





  public final void negate(Tuple2d t1)
  {
    x = (-x);
    y = (-y);
  }
  




  public final void negate()
  {
    x = (-x);
    y = (-y);
  }
  







  public final void scale(double s, Tuple2d t1)
  {
    x = (s * x);
    y = (s * y);
  }
  






  public final void scale(double s)
  {
    x *= s;
    y *= s;
  }
  








  public final void scaleAdd(double s, Tuple2d t1, Tuple2d t2)
  {
    x = (s * x + x);
    y = (s * y + y);
  }
  







  public final void scaleAdd(double s, Tuple2d t1)
  {
    x = (s * x + x);
    y = (s * y + y);
  }
  









  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + VecMathUtil.doubleToLongBits(x);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(y);
    return (int)(bits ^ bits >> 32);
  }
  






  public boolean equals(Tuple2d t1)
  {
    try
    {
      return (x == x) && (y == y);
    } catch (NullPointerException e2) {}
    return false;
  }
  







  public boolean equals(Object t1)
  {
    try
    {
      Tuple2d t2 = (Tuple2d)t1;
      return (x == x) && (y == y);
    } catch (NullPointerException e2) {
      return false; } catch (ClassCastException e1) {}
    return false;
  }
  












  public boolean epsilonEquals(Tuple2d t1, double epsilon)
  {
    double diff = x - x;
    if (Double.isNaN(diff)) return false;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = y - y;
    if (Double.isNaN(diff)) return false;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    return true;
  }
  





  public String toString()
  {
    return "(" + x + ", " + y + ")";
  }
  








  public final void clamp(double min, double max, Tuple2d t)
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
  }
  








  public final void clampMin(double min, Tuple2d t)
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
  }
  








  public final void clampMax(double max, Tuple2d t)
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
  }
  







  public final void absolute(Tuple2d t)
  {
    x = Math.abs(x);
    y = Math.abs(y);
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
  }
  






  public final void clampMin(double min)
  {
    if (x < min) x = min;
    if (y < min) { y = min;
    }
  }
  




  public final void clampMax(double max)
  {
    if (x > max) x = max;
    if (y > max) { y = max;
    }
  }
  



  public final void absolute()
  {
    x = Math.abs(x);
    y = Math.abs(y);
  }
  








  public final void interpolate(Tuple2d t1, Tuple2d t2, double alpha)
  {
    x = ((1.0D - alpha) * x + alpha * x);
    y = ((1.0D - alpha) * y + alpha * y);
  }
  







  public final void interpolate(Tuple2d t1, double alpha)
  {
    x = ((1.0D - alpha) * x + alpha * x);
    y = ((1.0D - alpha) * y + alpha * y);
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
}
