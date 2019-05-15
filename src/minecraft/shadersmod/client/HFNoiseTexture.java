package shadersmod.client;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class HFNoiseTexture
{
  public int texID = GL11.glGenTextures();
  public int textureUnit = 15;
  
  public HFNoiseTexture(int width, int height)
  {
    byte[] image = genHFNoiseImage(width, height);
    ByteBuffer data = BufferUtils.createByteBuffer(image.length);
    data.put(image);
    data.flip();
    GlStateManager.func_179144_i(texID);
    GL11.glTexImage2D(3553, 0, 6407, width, height, 0, 6407, 5121, data);
    GL11.glTexParameteri(3553, 10242, 10497);
    GL11.glTexParameteri(3553, 10243, 10497);
    GL11.glTexParameteri(3553, 10240, 9729);
    GL11.glTexParameteri(3553, 10241, 9729);
    GlStateManager.func_179144_i(0);
  }
  
  public int getID()
  {
    return texID;
  }
  
  public void destroy()
  {
    GlStateManager.func_179150_h(texID);
    texID = 0;
  }
  
  private int random(int seed)
  {
    seed ^= seed << 13;
    seed ^= seed >> 17;
    seed ^= seed << 5;
    return seed;
  }
  
  private byte random(int x, int y, int z)
  {
    int seed = (random(x) + random(y * 19)) * random(z * 23) - z;
    return (byte)(random(seed) % 128);
  }
  
  private byte[] genHFNoiseImage(int width, int height)
  {
    byte[] image = new byte[width * height * 3];
    int index = 0;
    
    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
        for (int z = 1; z < 4; z++)
        {
          image[(index++)] = random(x, y, z);
        }
      }
    }
    
    return image;
  }
}
