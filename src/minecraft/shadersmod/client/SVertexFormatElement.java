package shadersmod.client;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexFormatElement extends VertexFormatElement
{
  int sUsage;
  
  public SVertexFormatElement(int sUsage, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType type, int count)
  {
    super(0, type, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, count);
    this.sUsage = sUsage;
  }
}
