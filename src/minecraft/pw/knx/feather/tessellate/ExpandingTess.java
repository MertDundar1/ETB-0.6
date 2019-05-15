package pw.knx.feather.tessellate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;































public class ExpandingTess
  extends BasicTess
{
  private final float ratio;
  private final float factor;
  
  ExpandingTess(int initial, float ratio, float factor)
  {
    super(initial);
    this.ratio = ratio;
    this.factor = factor;
  }
  
















  public Tessellation addVertex(float x, float y, float z)
  {
    int capacity = raw.length;
    if (index * 6 >= capacity * ratio) {
      capacity = (int)(capacity * factor);
      int[] newBuffer = new int[capacity];
      System.arraycopy(raw, 0, newBuffer, 0, raw.length);
      raw = newBuffer;
      buffer = ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder());
      

      iBuffer = buffer.asIntBuffer();
      fBuffer = buffer.asFloatBuffer();
    }
    return super.addVertex(x, y, z);
  }
}
