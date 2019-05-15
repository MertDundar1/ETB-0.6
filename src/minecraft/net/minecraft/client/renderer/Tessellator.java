package net.minecraft.client.renderer;

public class Tessellator
{
  private WorldRenderer worldRenderer;
  private WorldVertexBufferUploader field_178182_b = new WorldVertexBufferUploader();
  

  public static final Tessellator instance = new Tessellator(2097152);
  
  private static final String __OBFID = "CL_00000960";
  

  public static Tessellator getInstance()
  {
    return instance;
  }
  
  public Tessellator(int p_i1250_1_)
  {
    worldRenderer = new WorldRenderer(p_i1250_1_);
  }
  



  public int draw()
  {
    return field_178182_b.draw(worldRenderer, worldRenderer.draw());
  }
  
  public WorldRenderer getWorldRenderer()
  {
    return worldRenderer;
  }
}
