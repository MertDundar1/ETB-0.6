package javax.vecmath;

import java.io.Serializable;





































































public abstract class Tuple4b
  implements Serializable, Cloneable
{
  static final long serialVersionUID = -8226727741811898211L;
  public byte x;
  public byte y;
  public byte z;
  public byte w;
  
  public Tuple4b(byte b1, byte b2, byte b3, byte b4)
  {
    x = b1;
    y = b2;
    z = b3;
    w = b4;
  }
  





  public Tuple4b(byte[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
    w = t[3];
  }
  





  public Tuple4b(Tuple4b t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  




  public Tuple4b()
  {
    x = 0;
    y = 0;
    z = 0;
    w = 0;
  }
  





  public String toString()
  {
    return "(" + (x & 0xFF) + ", " + (y & 0xFF) + ", " + (z & 0xFF) + ", " + (w & 0xFF) + ")";
  }
  









  public final void get(byte[] b)
  {
    b[0] = x;
    b[1] = y;
    b[2] = z;
    b[3] = w;
  }
  






  public final void get(Tuple4b t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  






  public final void set(Tuple4b t1)
  {
    x = x;
    y = y;
    z = z;
    w = w;
  }
  






  public final void set(byte[] b)
  {
    x = b[0];
    y = b[1];
    z = b[2];
    w = b[3];
  }
  





  public boolean equals(Tuple4b t1)
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
      Tuple4b t2 = (Tuple4b)t1;
      return (x == x) && (y == y) && (z == z) && (w == w);
    }
    catch (NullPointerException e2) {
      return false; } catch (ClassCastException e1) {}
    return false;
  }
  









  public int hashCode()
  {
    return (x & 0xFF) << 0 | (y & 0xFF) << 8 | (z & 0xFF) << 16 | (w & 0xFF) << 24;
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
  







  public final byte getX()
  {
    return x;
  }
  







  public final void setX(byte x)
  {
    this.x = x;
  }
  







  public final byte getY()
  {
    return y;
  }
  







  public final void setY(byte y)
  {
    this.y = y;
  }
  






  public final byte getZ()
  {
    return z;
  }
  







  public final void setZ(byte z)
  {
    this.z = z;
  }
  







  public final byte getW()
  {
    return w;
  }
  







  public final void setW(byte w)
  {
    this.w = w;
  }
}
