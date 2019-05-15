package javax.vecmath;

import java.io.Serializable;




























































public abstract class Tuple4f
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 7068460319248845763L;
  public float x;
  public float y;
  public float z;
  public float w;
  
  public Tuple4f(float x, float y, float z, float w)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  





  public Tuple4f(float[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
    w = t[3];
  }
  





  public Tuple4f(Tuple4f t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  





  public Tuple4f(Tuple4d t1)
  {
    x = ((float)x);
    y = ((float)y);
    z = ((float)z);
    w = ((float)w);
  }
  




  public Tuple4f()
  {
    x = 0.0F;
    y = 0.0F;
    z = 0.0F;
    w = 0.0F;
  }
  








  public final void set(float x, float y, float z, float w)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  






  public final void set(float[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
    w = t[3];
  }
  





  public final void set(Tuple4f t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  





  public final void set(Tuple4d t1)
  {
    x = ((float)x);
    y = ((float)y);
    z = ((float)z);
    w = ((float)w);
  }
  





  public final void get(float[] t)
  {
    t[0] = x;
    t[1] = y;
    t[2] = z;
    t[3] = w;
  }
  





  public final void get(Tuple4f t)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  






  public final void add(Tuple4f t1, Tuple4f t2)
  {
    x += x;
    y += y;
    z += z;
    w += w;
  }
  





  public final void add(Tuple4f t1)
  {
    x += x;
    y += y;
    z += z;
    w += w;
  }
  







  public final void sub(Tuple4f t1, Tuple4f t2)
  {
    x -= x;
    y -= y;
    z -= z;
    w -= w;
  }
  






  public final void sub(Tuple4f t1)
  {
    x -= x;
    y -= y;
    z -= z;
    w -= w;
  }
  





  public final void negate(Tuple4f t1)
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
  







  public final void scale(float s, Tuple4f t1)
  {
    x = (s * x);
    y = (s * y);
    z = (s * z);
    w = (s * w);
  }
  






  public final void scale(float s)
  {
    x *= s;
    y *= s;
    z *= s;
    w *= s;
  }
  








  public final void scaleAdd(float s, Tuple4f t1, Tuple4f t2)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
    w = (s * w + w);
  }
  







  public final void scaleAdd(float s, Tuple4f t1)
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
  





  public boolean equals(Tuple4f t1)
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
      Tuple4f t2 = (Tuple4f)t1;
      return (x == x) && (y == y) && (z == z) && (w == w);
    }
    catch (NullPointerException e2) {
      return false; } catch (ClassCastException e1) {}
    return false;
  }
  













  public boolean epsilonEquals(Tuple4f t1, float epsilon)
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
    diff = w - w;
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
    bits = 31L * bits + VecMathUtil.floatToIntBits(w);
    return (int)(bits ^ bits >> 32);
  }
  








  public final void clamp(float min, float max, Tuple4f t)
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
  








  public final void clampMin(float min, Tuple4f t)
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
  









  public final void clampMax(float max, Tuple4f t)
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
  







  public final void absolute(Tuple4f t)
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
    w = Math.abs(w);
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
    
    if (w > max) {
      w = max;
    } else if (w < min) {
      w = min;
    }
  }
  






  public final void clampMin(float min)
  {
    if (x < min) x = min;
    if (y < min) y = min;
    if (z < min) z = min;
    if (w < min) { w = min;
    }
  }
  





  public final void clampMax(float max)
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
  








  public void interpolate(Tuple4f t1, Tuple4f t2, float alpha)
  {
    x = ((1.0F - alpha) * x + alpha * x);
    y = ((1.0F - alpha) * y + alpha * y);
    z = ((1.0F - alpha) * z + alpha * z);
    w = ((1.0F - alpha) * w + alpha * w);
  }
  








  public void interpolate(Tuple4f t1, float alpha)
  {
    x = ((1.0F - alpha) * x + alpha * x);
    y = ((1.0F - alpha) * y + alpha * y);
    z = ((1.0F - alpha) * z + alpha * z);
    w = ((1.0F - alpha) * w + alpha * w);
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
  







  public final float getW()
  {
    return w;
  }
  







  public final void setW(float w)
  {
    this.w = w;
  }
}
