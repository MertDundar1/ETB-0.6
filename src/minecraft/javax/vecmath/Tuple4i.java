package javax.vecmath;

import java.io.Serializable;





























































public abstract class Tuple4i
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 8064614250942616720L;
  public int x;
  public int y;
  public int z;
  public int w;
  
  public Tuple4i(int x, int y, int z, int w)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  




  public Tuple4i(int[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
    w = t[3];
  }
  





  public Tuple4i(Tuple4i t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  



  public Tuple4i()
  {
    x = 0;
    y = 0;
    z = 0;
    w = 0;
  }
  








  public final void set(int x, int y, int z, int w)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  





  public final void set(int[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
    w = t[3];
  }
  




  public final void set(Tuple4i t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  




  public final void get(int[] t)
  {
    t[0] = x;
    t[1] = y;
    t[2] = z;
    t[3] = w;
  }
  




  public final void get(Tuple4i t)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  





  public final void add(Tuple4i t1, Tuple4i t2)
  {
    x += x;
    y += y;
    z += z;
    w += w;
  }
  




  public final void add(Tuple4i t1)
  {
    x += x;
    y += y;
    z += z;
    w += w;
  }
  






  public final void sub(Tuple4i t1, Tuple4i t2)
  {
    x -= x;
    y -= y;
    z -= z;
    w -= w;
  }
  





  public final void sub(Tuple4i t1)
  {
    x -= x;
    y -= y;
    z -= z;
    w -= w;
  }
  




  public final void negate(Tuple4i t1)
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
  






  public final void scale(int s, Tuple4i t1)
  {
    x = (s * x);
    y = (s * y);
    z = (s * z);
    w = (s * w);
  }
  





  public final void scale(int s)
  {
    x *= s;
    y *= s;
    z *= s;
    w *= s;
  }
  







  public final void scaleAdd(int s, Tuple4i t1, Tuple4i t2)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
    w = (s * w + w);
  }
  






  public final void scaleAdd(int s, Tuple4i t1)
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
  






  public boolean equals(Object t1)
  {
    try
    {
      Tuple4i t2 = (Tuple4i)t1;
      return (x == x) && (y == y) && (z == z) && (w == w);
    }
    catch (NullPointerException e2)
    {
      return false;
    }
    catch (ClassCastException e1) {}
    return false;
  }
  









  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + x;
    bits = 31L * bits + y;
    bits = 31L * bits + z;
    bits = 31L * bits + w;
    return (int)(bits ^ bits >> 32);
  }
  







  public final void clamp(int min, int max, Tuple4i t)
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
  






  public final void clampMin(int min, Tuple4i t)
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
  








  public final void clampMax(int max, Tuple4i t)
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
  





  public final void absolute(Tuple4i t)
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
    w = Math.abs(w);
  }
  





  public final void clamp(int min, int max)
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
  




  public final void clampMin(int min)
  {
    if (x < min) {
      x = min;
    }
    if (y < min) {
      y = min;
    }
    if (z < min) {
      z = min;
    }
    if (w < min) {
      w = min;
    }
  }
  



  public final void clampMax(int max)
  {
    if (x > max) {
      x = max;
    }
    if (y > max) {
      y = max;
    }
    if (z > max) {
      z = max;
    }
    if (w > max) {
      w = max;
    }
  }
  


  public final void absolute()
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
    w = Math.abs(w);
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
  








  public final int getX()
  {
    return x;
  }
  







  public final void setX(int x)
  {
    this.x = x;
  }
  







  public final int getY()
  {
    return y;
  }
  







  public final void setY(int y)
  {
    this.y = y;
  }
  






  public final int getZ()
  {
    return z;
  }
  







  public final void setZ(int z)
  {
    this.z = z;
  }
  





  public final int getW()
  {
    return w;
  }
  







  public final void setW(int w)
  {
    this.w = w;
  }
}
