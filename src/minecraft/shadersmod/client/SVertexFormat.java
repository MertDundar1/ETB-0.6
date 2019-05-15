package shadersmod.client;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexFormat
{
  public static final int vertexSizeBlock = 14;
  public static final int offsetMidTexCoord = 8;
  public static final int offsetTangent = 10;
  public static final int offsetEntity = 12;
  public static final VertexFormat defVertexFormatTextured = ;
  
  public SVertexFormat() {}
  
  public static VertexFormat makeDefVertexFormatBlock() { VertexFormat vf = new VertexFormat();
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.POSITION, 3));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.UBYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.COLOR, 4));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.UV, 2));
    vf.func_177349_a(new VertexFormatElement(1, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.UV, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.NORMAL, 3));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 1));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 4));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 4));
    return vf;
  }
  
  public static VertexFormat makeDefVertexFormatItem()
  {
    VertexFormat vf = new VertexFormat();
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.POSITION, 3));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.UBYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.COLOR, 4));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.UV, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.NORMAL, 3));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 1));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 4));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 4));
    return vf;
  }
  
  public static VertexFormat makeDefVertexFormatTextured()
  {
    VertexFormat vf = new VertexFormat();
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.POSITION, 3));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.UBYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 4));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.UV, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.NORMAL, 3));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 1));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 4));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 4));
    return vf;
  }
  
  public static void setDefBakedFormat(VertexFormat vf)
  {
    vf.clear();
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.POSITION, 3));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.UBYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.COLOR, 4));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.UV, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.NORMAL, 3));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.BYTE, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 1));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 2));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 4));
    vf.func_177349_a(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.SHORT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUseage.PADDING, 4));
  }
}
