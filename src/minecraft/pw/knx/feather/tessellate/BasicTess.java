package pw.knx.feather.tessellate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;

















































public class BasicTess
  implements Tessellation
{
  int index;
  int[] raw;
  ByteBuffer buffer;
  FloatBuffer fBuffer;
  IntBuffer iBuffer;
  private int colors;
  private float texU;
  private float texV;
  private boolean color;
  private boolean texture;
  
  BasicTess(int capacity)
  {
    capacity *= 6;
    raw = new int[capacity];
    buffer = ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder());
    fBuffer = buffer.asFloatBuffer();
    iBuffer = buffer.asIntBuffer();
  }
  



  public Tessellation setColor(int color)
  {
    this.color = true;
    colors = color;
    return this;
  }
  






  public Tessellation setTexture(float u, float v)
  {
    texture = true;
    texU = u;
    texV = v;
    return this;
  }
  









  public Tessellation addVertex(float x, float y, float z)
  {
    int dex = index * 6;
    raw[dex] = Float.floatToRawIntBits(x);
    raw[(dex + 1)] = Float.floatToRawIntBits(y);
    raw[(dex + 2)] = Float.floatToRawIntBits(z);
    raw[(dex + 3)] = colors;
    raw[(dex + 4)] = Float.floatToRawIntBits(texU);
    raw[(dex + 5)] = Float.floatToRawIntBits(texV);
    index += 1;
    return this;
  }
  






  public Tessellation bind()
  {
    int dex = index * 6;
    iBuffer.put(raw, 0, dex);
    buffer.position(0);
    buffer.limit(dex * 4);
    if (color) {
      buffer.position(12);
      GL11.glColorPointer(4, true, 24, buffer);
    }
    if (texture) {
      fBuffer.position(4);
      GL11.glTexCoordPointer(2, 24, fBuffer);
    }
    fBuffer.position(0);
    GL11.glVertexPointer(3, 24, fBuffer);
    return this;
  }
  










  public Tessellation pass(int mode)
  {
    GL11.glDrawArrays(mode, 0, index);
    return this;
  }
  







  public Tessellation unbind()
  {
    iBuffer.position(0);
    return this;
  }
  







  public Tessellation reset()
  {
    iBuffer.clear();
    index = 0;
    color = false;
    texture = false;
    return this;
  }
}
