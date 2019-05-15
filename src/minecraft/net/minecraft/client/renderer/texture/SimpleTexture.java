package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadersmod.client.ShadersTex;

public class SimpleTexture
  extends AbstractTexture
{
  private static final Logger logger = ;
  protected final ResourceLocation textureLocation;
  private static final String __OBFID = "CL_00001052";
  
  public SimpleTexture(ResourceLocation p_i1275_1_)
  {
    textureLocation = p_i1275_1_;
  }
  
  public void loadTexture(IResourceManager p_110551_1_) throws IOException
  {
    deleteGlTexture();
    InputStream var2 = null;
    
    try
    {
      IResource var3 = p_110551_1_.getResource(textureLocation);
      var2 = var3.getInputStream();
      BufferedImage var4 = TextureUtil.func_177053_a(var2);
      boolean var5 = false;
      boolean var6 = false;
      
      if (var3.hasMetadata())
      {
        try
        {
          TextureMetadataSection var11 = (TextureMetadataSection)var3.getMetadata("texture");
          
          if (var11 != null)
          {
            var5 = var11.getTextureBlur();
            var6 = var11.getTextureClamp();
          }
        }
        catch (RuntimeException var111)
        {
          logger.warn("Failed reading metadata of: " + textureLocation, var111);
        }
      }
      
      if (Config.isShaders())
      {
        ShadersTex.loadSimpleTexture(getGlTextureId(), var4, var5, var6, p_110551_1_, textureLocation, getMultiTexID());
      }
      else
      {
        TextureUtil.uploadTextureImageAllocate(getGlTextureId(), var4, var5, var6);
      }
    }
    finally
    {
      if (var2 != null)
      {
        var2.close();
      }
    }
  }
}
