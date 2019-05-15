package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;
















































public class Color4f
  extends Tuple4f
  implements Serializable
{
  static final long serialVersionUID = 8577680141580006740L;
  
  public Color4f(float x, float y, float z, float w)
  {
    super(x, y, z, w);
  }
  




  public Color4f(float[] c)
  {
    super(c);
  }
  




  public Color4f(Color4f c1)
  {
    super(c1);
  }
  




  public Color4f(Tuple4f t1)
  {
    super(t1);
  }
  




  public Color4f(Tuple4d t1)
  {
    super(t1);
  }
  











  public Color4f(Color color)
  {
    super(color.getRed() / 255.0F, color
      .getGreen() / 255.0F, color
      .getBlue() / 255.0F, color
      .getAlpha() / 255.0F);
  }
  








  public Color4f() {}
  








  public final void set(Color color)
  {
    x = (color.getRed() / 255.0F);
    y = (color.getGreen() / 255.0F);
    z = (color.getBlue() / 255.0F);
    w = (color.getAlpha() / 255.0F);
  }
  








  public final Color get()
  {
    int r = Math.round(x * 255.0F);
    int g = Math.round(y * 255.0F);
    int b = Math.round(z * 255.0F);
    int a = Math.round(w * 255.0F);
    
    return new Color(r, g, b, a);
  }
}
