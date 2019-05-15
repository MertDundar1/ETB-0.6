package javax.vecmath;

import java.io.Serializable;
























































public abstract class Tuple3i
  implements Serializable, Cloneable
{
  static final long serialVersionUID = -732740491767276200L;
  public int x;
  public int y;
  public int z;
  
  public Tuple3i(int x, int y, int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  




  public Tuple3i(int[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
  }
  





  public Tuple3i(Tuple3i t1)
  {
    x = x;
    y = y;
    z = z;
  }
  



  public Tuple3i()
  {
    x = 0;
    y = 0;
    z = 0;
  }
  







  public final void set(int x, int y, int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  





  public final void set(int[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
  }
  




  public final void set(Tuple3i t1)
  {
    x = x;
    y = y;
    z = z;
  }
  




  public final void get(int[] t)
  {
    t[0] = x;
    t[1] = y;
    t[2] = z;
  }
  




  public final void get(Tuple3i t)
  {
    x = x;
    y = y;
    z = z;
  }
  





  public final void add(Tuple3i t1, Tuple3i t2)
  {
    x += x;
    y += y;
    z += z;
  }
  




  public final void add(Tuple3i t1)
  {
    x += x;
    y += y;
    z += z;
  }
  






  public final void sub(Tuple3i t1, Tuple3i t2)
  {
    x -= x;
    y -= y;
    z -= z;
  }
  





  public final void sub(Tuple3i t1)
  {
    x -= x;
    y -= y;
    z -= z;
  }
  




  public final void negate(Tuple3i t1)
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
  






  public final void scale(int s, Tuple3i t1)
  {
    x = (s * x);
    y = (s * y);
    z = (s * z);
  }
  





  public final void scale(int s)
  {
    x *= s;
    y *= s;
    z *= s;
  }
  







  public final void scaleAdd(int s, Tuple3i t1, Tuple3i t2)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
  }
  






  public final void scaleAdd(int s, Tuple3i t1)
  {
    x = (s * x + x);
    y = (s * y + y);
    z = (s * z + z);
  }
  





  public String toString()
  {
    return "(" + x + ", " + y + ", " + z + ")";
  }
  





  public boolean equals(Object t1)
  {
    try
    {
      Tuple3i t2 = (Tuple3i)t1;
      return (x == x) && (y == y) && (z == z);
    }
    catch (NullPointerException e2) {
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
    return (int)(bits ^ bits >> 32);
  }
  







  public final void clamp(int min, int max, Tuple3i t)
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
  






  public final void clampMin(int min, Tuple3i t)
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
  






  public final void clampMax(int max, Tuple3i t)
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
  





  public final void absolute(Tuple3i t)
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
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
  }
  


  public final void absolute()
  {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
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
}
