package javax.vecmath;

import java.io.Serializable;























































public abstract class Tuple3f
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 5019834619484343712L;
  public float x;
  public float y;
  public float z;
  
  public Tuple3f(float x, float y, float z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  





  public Tuple3f(float[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
  }
  





  public Tuple3f(Tuple3f t1)
  {
    x = x;
    y = y;
    z = z;
  }
  





  public Tuple3f(Tuple3d t1)
  {
    x = ((float)x);
    y = ((float)y);
    z = ((float)z);
  }
  




  public Tuple3f()
  {
    x = 0.0F;
    y = 0.0F;
    z = 0.0F;
  }
  





  public String toString()
  {
    return "(" + x + ", " + y + ", " + z + ")";
  }
  







  public final void set(float x, float y, float z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  






  public final void set(float[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
  }
  





  public final void set(Tuple3f t1)
  {
    x = x;
    y = y;
    z = z;
  }
  





  public final void set(Tuple3d t1)
  {
    x = ((float)x);
    y = ((float)y);
    z = ((float)z);
  }
  





  public final void get(float[] t)
  {
    t[0] = x;
    t[1] = y;
    t[2] = z;
  }
  





  public final void get(Tuple3f t)
  {
    x = x;
    y = y;
    z = z;
  }
  






  public final void add(Tuple3f t1, Tuple3f t2)
  {
    x += x;
    y += y;
    z += z;
  }
  





  public final void add(Tuple3f t1)
  {
    x += x;
    y += y;
    z += z;
  }
  







  public final void sub(Tuple3f t1, Tuple3f t2)
  {
    x -= x;
    y -= y;
    z -= z;
  }
  






  public final void sub(Tuple3f t1)
  {
    x -= x;
    y -= y;
    z -= z;
  }
  





  public final void negate(Tuple3f t1)
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
  







  public final void scale(float s, Tuple3f t1)
  {
    x = (s * x);
    y = (s * y);
    z = (s * z);
  }
  






  public final void scale(float s)
  {
    x *= s;
    y *= s;
    z *= s;
  }
  








  public final void scaleAdd(float s, Tuple3f t1, Tuple3f t2)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
  }
  








  public final void scaleAdd(float s, Tuple3f t1)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
  }
  







  public boolean equals(Tuple3f t1)
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
      Tuple3f t2 = (Tuple3f)t1;
      return (x == x) && (y == y) && (z == z);
    } catch (NullPointerException e2) {
      return false; } catch (ClassCastException e1) {}
    return false;
  }
  












  public boolean epsilonEquals(Tuple3f t1, float epsilon)
  {
    float diff = x - x;
    if (Float.isNaN(diff)) return false;
    if ((diff < 0.0F ? -diff : diff) > epsilon) { return false;
    }
    diff = y - y;
    if (Float.isNaN(diff)) return false;
    if ((diff < 0.0F ? -diff : diff) > epsilon) { return false;
    }
    diff = z - z;
    if (Float.isNaN(diff)) return false;
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
    return (int)(bits ^ bits >> 32);
  }
  









  public final void clamp(float min, float max, Tuple3f t)
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
  








  public final void clampMin(float min, Tuple3f t)
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
  








  public final void clampMax(float max, Tuple3f t)
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
  







  public final void absolute(Tuple3f t)
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
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
    
    if (z > max) {
      z = max;
    } else if (z < min) {
      z = min;
    }
  }
  






  public final void clampMin(float min)
  {
    if (x < min) x = min;
    if (y < min) y = min;
    if (z < min) { z = min;
    }
  }
  





  public final void clampMax(float max)
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
  









  public final void interpolate(Tuple3f t1, Tuple3f t2, float alpha)
  {
    x = ((1.0F - alpha) * x + alpha * x);
    y = ((1.0F - alpha) * y + alpha * y);
    z = ((1.0F - alpha) * z + alpha * z);
  }
  









  public final void interpolate(Tuple3f t1, float alpha)
  {
    x = ((1.0F - alpha) * x + alpha * x);
    y = ((1.0F - alpha) * y + alpha * y);
    z = ((1.0F - alpha) * z + alpha * z);
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
  






  public final float getZ()
  {
    return z;
  }
  







  public final void setZ(float z)
  {
    this.z = z;
  }
}
