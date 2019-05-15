package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;



















































public class Color3b
  extends Tuple3b
  implements Serializable
{
  static final long serialVersionUID = 6632576088353444794L;
  
  public Color3b(byte c1, byte c2, byte c3)
  {
    super(c1, c2, c3);
  }
  




  public Color3b(byte[] c)
  {
    super(c);
  }
  




  public Color3b(Color3b c1)
  {
    super(c1);
  }
  




  public Color3b(Tuple3b t1)
  {
    super(t1);
  }
  











  public Color3b(Color color)
  {
    super((byte)color.getRed(), 
      (byte)color.getGreen(), 
      (byte)color.getBlue());
  }
  








  public Color3b() {}
  








  public final void set(Color color)
  {
    x = ((byte)color.getRed());
    y = ((byte)color.getGreen());
    z = ((byte)color.getBlue());
  }
  








  public final Color get()
  {
    int r = x & 0xFF;
    int g = y & 0xFF;
    int b = z & 0xFF;
    
    return new Color(r, g, b);
  }
}
