package javax.vecmath;

import java.io.Serializable;
































































public abstract class Tuple3b
  implements Serializable, Cloneable
{
  static final long serialVersionUID = -483782685323607044L;
  public byte x;
  public byte y;
  public byte z;
  
  public Tuple3b(byte b1, byte b2, byte b3)
  {
    x = b1;
    y = b2;
    z = b3;
  }
  





  public Tuple3b(byte[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
  }
  





  public Tuple3b(Tuple3b t1)
  {
    x = x;
    y = y;
    z = z;
  }
  




  public Tuple3b()
  {
    x = 0;
    y = 0;
    z = 0;
  }
  





  public String toString()
  {
    return "(" + (x & 0xFF) + ", " + (y & 0xFF) + ", " + (z & 0xFF) + ")";
  }
  









  public final void get(byte[] t)
  {
    t[0] = x;
    t[1] = y;
    t[2] = z;
  }
  






  public final void get(Tuple3b t1)
  {
    x = x;
    y = y;
    z = z;
  }
  






  public final void set(Tuple3b t1)
  {
    x = x;
    y = y;
    z = z;
  }
  






  public final void set(byte[] t)
  {
    x = t[0];
    y = t[1];
    z = t[2];
  }
  






  public boolean equals(Tuple3b t1)
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
      Tuple3b t2 = (Tuple3b)t1;
      return (x == x) && (y == y) && (z == z);
    } catch (NullPointerException e2) {
      return false; } catch (ClassCastException e1) {}
    return false;
  }
  








  public int hashCode()
  {
    return (x & 0xFF) << 0 | (y & 0xFF) << 8 | (z & 0xFF) << 16;
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
}
