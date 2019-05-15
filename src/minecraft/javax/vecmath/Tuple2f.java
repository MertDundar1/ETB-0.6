package javax.vecmath;

import java.io.Serializable;


















































public abstract class Tuple2f
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 9011180388985266884L;
  public float x;
  public float y;
  
  public Tuple2f(float x, float y)
  {
    this.x = x;
    this.y = y;
  }
  





  public Tuple2f(float[] t)
  {
    x = t[0];
    y = t[1];
  }
  





  public Tuple2f(Tuple2f t1)
  {
    x = x;
    y = y;
  }
  





  public Tuple2f(Tuple2d t1)
  {
    x = ((float)x);
    y = ((float)y);
  }
  




  public Tuple2f()
  {
    x = 0.0F;
    y = 0.0F;
  }
  






  public final void set(float x, float y)
  {
    this.x = x;
    this.y = y;
  }
  






  public final void set(float[] t)
  {
    x = t[0];
    y = t[1];
  }
  





  public final void set(Tuple2f t1)
  {
    x = x;
    y = y;
  }
  





  public final void set(Tuple2d t1)
  {
    x = ((float)x);
    y = ((float)y);
  }
  





  public final void get(float[] t)
  {
    t[0] = x;
    t[1] = y;
  }
  






  public final void add(Tuple2f t1, Tuple2f t2)
  {
    x += x;
    y += y;
  }
  





  public final void add(Tuple2f t1)
  {
    x += x;
    y += y;
  }
  







  public final void sub(Tuple2f t1, Tuple2f t2)
  {
    x -= x;
    y -= y;
  }
  






  public final void sub(Tuple2f t1)
  {
    x -= x;
    y -= y;
  }
  





  public final void negate(Tuple2f t1)
  {
    x = (-x);
    y = (-y);
  }
  




  public final void negate()
  {
    x = (-x);
    y = (-y);
  }
  







  public final void scale(float s, Tuple2f t1)
  {
    x = (s * x);
    y = (s * y);
  }
  






  public final void scale(float s)
  {
    x *= s;
    y *= s;
  }
  








  public final void scaleAdd(float s, Tuple2f t1, Tuple2f t2)
  {
    x = (s * x + x);
    y = (s * y + y);
  }
  







  public final void scaleAdd(float s, Tuple2f t1)
  {
    x = (s * x + x);
    y = (s * y + y);
  }
  









  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + VecMathUtil.floatToIntBits(x);
    bits = 31L * bits + VecMathUtil.floatToIntBits(y);
    return (int)(bits ^ bits >> 32);
  }
  






  public boolean equals(Tuple2f t1)
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
      Tuple2f t2 = (Tuple2f)t1;
      return (x == x) && (y == y);
    } catch (NullPointerException e2) {
      return false; } catch (ClassCastException e1) {}
    return false;
  }
  












  public boolean epsilonEquals(Tuple2f t1, float epsilon)
  {
    float diff = x - x;
    if (Float.isNaN(diff)) return false;
    if ((diff < 0.0F ? -diff : diff) > epsilon) { return false;
    }
    diff = y - y;
    if (Float.isNaN(diff)) return false;
    if ((diff < 0.0F ? -diff : diff) > epsilon) { return false;
    }
    return true;
  }
  





  public String toString()
  {
    return "(" + x + ", " + y + ")";
  }
  








  public final void clamp(float min, float max, Tuple2f t)
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
  








  public final void clampMin(float min, Tuple2f t)
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
  








  public final void clampMax(float max, Tuple2f t)
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
  







  public final void absolute(Tuple2f t)
  {
    x = Math.abs(x);
    y = Math.abs(y);
  }
  







  public final void clamp(float min, float max)
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
  






  public final void clampMin(float min)
  {
    if (x < min) x = min;
    if (y < min) { y = min;
    }
  }
  




  public final void clampMax(float max)
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
  








  public final void interpolate(Tuple2f t1, Tuple2f t2, float alpha)
  {
    x = ((1.0F - alpha) * x + alpha * x);
    y = ((1.0F - alpha) * y + alpha * y);
  }
  









  public final void interpolate(Tuple2f t1, float alpha)
  {
    x = ((1.0F - alpha) * x + alpha * x);
    y = ((1.0F - alpha) * y + alpha * y);
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
}
