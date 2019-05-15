package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class MapItemRenderer
{
  private static final ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");
  private final TextureManager textureManager;
  private final Map loadedMaps = Maps.newHashMap();
  private static final String __OBFID = "CL_00000663";
  
  public MapItemRenderer(TextureManager p_i45009_1_)
  {
    textureManager = p_i45009_1_;
  }
  
  public void func_148246_a(MapData p_148246_1_)
  {
    func_148248_b(p_148246_1_).func_148236_a();
  }
  
  public void func_148250_a(MapData p_148250_1_, boolean p_148250_2_)
  {
    func_148248_b(p_148250_1_).func_148237_a(p_148250_2_);
  }
  
  private Instance func_148248_b(MapData p_148248_1_)
  {
    Instance var2 = (Instance)loadedMaps.get(mapName);
    
    if (var2 == null)
    {
      var2 = new Instance(p_148248_1_, null);
      loadedMaps.put(mapName, var2);
    }
    
    return var2;
  }
  
  public void func_148249_a()
  {
    Iterator var1 = loadedMaps.values().iterator();
    
    while (var1.hasNext())
    {
      Instance var2 = (Instance)var1.next();
      textureManager.deleteTexture(field_148240_d);
    }
    
    loadedMaps.clear();
  }
  
  class Instance
  {
    private final MapData field_148242_b;
    private final DynamicTexture field_148243_c;
    private final ResourceLocation field_148240_d;
    private final int[] field_148241_e;
    private static final String __OBFID = "CL_00000665";
    
    private Instance(MapData p_i45007_2_)
    {
      field_148242_b = p_i45007_2_;
      field_148243_c = new DynamicTexture(128, 128);
      field_148241_e = field_148243_c.getTextureData();
      field_148240_d = textureManager.getDynamicTextureLocation("map/" + mapName, field_148243_c);
      
      for (int var3 = 0; var3 < field_148241_e.length; var3++)
      {
        field_148241_e[var3] = 0;
      }
    }
    
    private void func_148236_a()
    {
      for (int var1 = 0; var1 < 16384; var1++)
      {
        int var2 = field_148242_b.colors[var1] & 0xFF;
        
        if (var2 / 4 == 0)
        {
          field_148241_e[var1] = ((var1 + var1 / 128 & 0x1) * 8 + 16 << 24);
        }
        else
        {
          field_148241_e[var1] = net.minecraft.block.material.MapColor.mapColorArray[(var2 / 4)].func_151643_b(var2 & 0x3);
        }
      }
      
      field_148243_c.updateDynamicTexture();
    }
    
    private void func_148237_a(boolean p_148237_1_)
    {
      byte var2 = 0;
      byte var3 = 0;
      Tessellator var4 = Tessellator.getInstance();
      WorldRenderer var5 = var4.getWorldRenderer();
      float var6 = 0.0F;
      textureManager.bindTexture(field_148240_d);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(1, 771, 0, 1);
      GlStateManager.disableAlpha();
      var5.startDrawingQuads();
      var5.addVertexWithUV(var2 + 0 + var6, var3 + 128 - var6, -0.009999999776482582D, 0.0D, 1.0D);
      var5.addVertexWithUV(var2 + 128 - var6, var3 + 128 - var6, -0.009999999776482582D, 1.0D, 1.0D);
      var5.addVertexWithUV(var2 + 128 - var6, var3 + 0 + var6, -0.009999999776482582D, 1.0D, 0.0D);
      var5.addVertexWithUV(var2 + 0 + var6, var3 + 0 + var6, -0.009999999776482582D, 0.0D, 0.0D);
      var4.draw();
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
      textureManager.bindTexture(MapItemRenderer.mapIcons);
      int var7 = 0;
      Iterator var8 = field_148242_b.playersVisibleOnMap.values().iterator();
      
      while (var8.hasNext())
      {
        Vec4b var9 = (Vec4b)var8.next();
        
        if ((!p_148237_1_) || (var9.func_176110_a() == 1))
        {
          GlStateManager.pushMatrix();
          GlStateManager.translate(var2 + var9.func_176112_b() / 2.0F + 64.0F, var3 + var9.func_176113_c() / 2.0F + 64.0F, -0.02F);
          GlStateManager.rotate(var9.func_176111_d() * 360 / 16.0F, 0.0F, 0.0F, 1.0F);
          GlStateManager.scale(4.0F, 4.0F, 3.0F);
          GlStateManager.translate(-0.125F, 0.125F, 0.0F);
          byte var10 = var9.func_176110_a();
          float var11 = (var10 % 4 + 0) / 4.0F;
          float var12 = (var10 / 4 + 0) / 4.0F;
          float var13 = (var10 % 4 + 1) / 4.0F;
          float var14 = (var10 / 4 + 1) / 4.0F;
          var5.startDrawingQuads();
          var5.addVertexWithUV(-1.0D, 1.0D, var7 * 0.001F, var11, var12);
          var5.addVertexWithUV(1.0D, 1.0D, var7 * 0.001F, var13, var12);
          var5.addVertexWithUV(1.0D, -1.0D, var7 * 0.001F, var13, var14);
          var5.addVertexWithUV(-1.0D, -1.0D, var7 * 0.001F, var11, var14);
          var4.draw();
          GlStateManager.popMatrix();
          var7++;
        }
      }
      
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 0.0F, -0.04F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
    }
    
    Instance(MapData p_i45008_2_, Object p_i45008_3_)
    {
      this(p_i45008_2_);
    }
  }
}
