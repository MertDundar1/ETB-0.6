package javax.vecmath;

import java.io.Serializable;



















































public abstract class Tuple2i
  implements Serializable, Cloneable
{
  static final long serialVersionUID = -3555701650170169638L;
  public int x;
  public int y;
  
  public Tuple2i(int x, int y)
  {
    this.x = x;
    this.y = y;
  }
  




  public Tuple2i(int[] t)
  {
    x = t[0];
    y = t[1];
  }
  





  public Tuple2i(Tuple2i t1)
  {
    x = x;
    y = y;
  }
  



  public Tuple2i()
  {
    x = 0;
    y = 0;
  }
  






  public final void set(int x, int y)
  {
    this.x = x;
    this.y = y;
  }
  





  public final void set(int[] t)
  {
    x = t[0];
    y = t[1];
  }
  




  public final void set(Tuple2i t1)
  {
    x = x;
    y = y;
  }
  




  public final void get(int[] t)
  {
    t[0] = x;
    t[1] = y;
  }
  




  public final void get(Tuple2i t)
  {
    x = x;
    y = y;
  }
  





  public final void add(Tuple2i t1, Tuple2i t2)
  {
    x += x;
    y += y;
  }
  




  public final void add(Tuple2i t1)
  {
    x += x;
    y += y;
  }
  






  public final void sub(Tuple2i t1, Tuple2i t2)
  {
    x -= x;
    y -= y;
  }
  





  public final void sub(Tuple2i t1)
  {
    x -= x;
    y -= y;
  }
  




  public final void negate(Tuple2i t1)
  {
    x = (-x);
    y = (-y);
  }
  



  public final void negate()
  {
    x = (-x);
    y = (-y);
  }
  






  public final void scale(int s, Tuple2i t1)
  {
    x = (s * x);
    y = (s * y);
  }
  





  public final void scale(int s)
  {
    x *= s;
    y *= s;
  }
  







  public final void scaleAdd(int s, Tuple2i t1, Tuple2i t2)
  {
    x = (s * x + x);
    y = (s * y + y);
  }
  






  public final void scaleAdd(int s, Tuple2i t1)
  {
    x = (s * x + x);
    y = (s * y + y);
  }
  





  public String toString()
  {
    return "(" + x + ", " + y + ")";
  }
  





  public boolean equals(Object t1)
  {
    try
    {
      Tuple2i t2 = (Tuple2i)t1;
      return (x == x) && (y == y);
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
    return (int)(bits ^ bits >> 32);
  }
  







  public final void clamp(int min, int max, Tuple2i t)
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
  






  public final void clampMin(int min, Tuple2i t)
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
  






  public final void clampMax(int max, Tuple2i t)
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
  





  public final void absolute(Tuple2i t)
  {
    x = Math.abs(x);
    y = Math.abs(y);
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
  }
  




  public final void clampMin(int min)
  {
    if (x < min) {
      x = min;
    }
    if (y < min) {
      y = min;
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
  }
  


  public final void absolute()
  {
    x = Math.abs(x);
    y = Math.abs(y);
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
}
