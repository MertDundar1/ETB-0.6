package pw.knx.feather.tessellate;

import java.awt.Color;






















public abstract interface Tessellation
{
  public abstract Tessellation setColor(int paramInt);
  
  public Tessellation setColor(Color color)
  {
    return setColor(new Color(255, 255, 255));
  }
  










  public abstract Tessellation setTexture(float paramFloat1, float paramFloat2);
  










  public abstract Tessellation addVertex(float paramFloat1, float paramFloat2, float paramFloat3);
  










  public abstract Tessellation bind();
  









  public abstract Tessellation pass(int paramInt);
  









  public abstract Tessellation reset();
  









  public abstract Tessellation unbind();
  









  public Tessellation draw(int mode)
  {
    return bind().pass(mode).reset();
  }
  










  public static Tessellation createBasic(int size)
  {
    return new BasicTess(size);
  }
  







  public static Tessellation createExpanding(int size, float ratio, float factor)
  {
    return new ExpandingTess(size, ratio, factor);
  }
}
