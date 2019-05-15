package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ImageBufferDownload implements IImageBuffer
{
  private int[] imageData;
  private int imageWidth;
  private int imageHeight;
  
  public ImageBufferDownload() {}
  
  public BufferedImage parseUserSkin(BufferedImage p_78432_1_)
  {
    if (p_78432_1_ == null)
    {
      return null;
    }
    

    imageWidth = 64;
    imageHeight = 64;
    int srcWidth = p_78432_1_.getWidth();
    int srcHeight = p_78432_1_.getHeight();
    

    for (int k = 1; (imageWidth < srcWidth) || (imageHeight < srcHeight); k *= 2)
    {
      imageWidth *= 2;
      imageHeight *= 2;
    }
    
    BufferedImage var2 = new BufferedImage(imageWidth, imageHeight, 2);
    Graphics var3 = var2.getGraphics();
    var3.drawImage(p_78432_1_, 0, 0, null);
    
    if (p_78432_1_.getHeight() == 32 * k)
    {
      var3.drawImage(var2, 24 * k, 48 * k, 20 * k, 52 * k, 4 * k, 16 * k, 8 * k, 20 * k, null);
      var3.drawImage(var2, 28 * k, 48 * k, 24 * k, 52 * k, 8 * k, 16 * k, 12 * k, 20 * k, null);
      var3.drawImage(var2, 20 * k, 52 * k, 16 * k, 64 * k, 8 * k, 20 * k, 12 * k, 32 * k, null);
      var3.drawImage(var2, 24 * k, 52 * k, 20 * k, 64 * k, 4 * k, 20 * k, 8 * k, 32 * k, null);
      var3.drawImage(var2, 28 * k, 52 * k, 24 * k, 64 * k, 0 * k, 20 * k, 4 * k, 32 * k, null);
      var3.drawImage(var2, 32 * k, 52 * k, 28 * k, 64 * k, 12 * k, 20 * k, 16 * k, 32 * k, null);
      var3.drawImage(var2, 40 * k, 48 * k, 36 * k, 52 * k, 44 * k, 16 * k, 48 * k, 20 * k, null);
      var3.drawImage(var2, 44 * k, 48 * k, 40 * k, 52 * k, 48 * k, 16 * k, 52 * k, 20 * k, null);
      var3.drawImage(var2, 36 * k, 52 * k, 32 * k, 64 * k, 48 * k, 20 * k, 52 * k, 32 * k, null);
      var3.drawImage(var2, 40 * k, 52 * k, 36 * k, 64 * k, 44 * k, 20 * k, 48 * k, 32 * k, null);
      var3.drawImage(var2, 44 * k, 52 * k, 40 * k, 64 * k, 40 * k, 20 * k, 44 * k, 32 * k, null);
      var3.drawImage(var2, 48 * k, 52 * k, 44 * k, 64 * k, 52 * k, 20 * k, 56 * k, 32 * k, null);
    }
    
    var3.dispose();
    imageData = ((java.awt.image.DataBufferInt)var2.getRaster().getDataBuffer()).getData();
    setAreaOpaque(0, 0, 32 * k, 16 * k);
    setAreaTransparent(32 * k, 0, 64 * k, 32 * k);
    setAreaOpaque(0, 16 * k, 64 * k, 32 * k);
    setAreaTransparent(0, 32 * k, 16 * k, 48 * k);
    setAreaTransparent(16 * k, 32 * k, 40 * k, 48 * k);
    setAreaTransparent(40 * k, 32 * k, 56 * k, 48 * k);
    setAreaTransparent(0, 48 * k, 16 * k, 64 * k);
    setAreaOpaque(16 * k, 48 * k, 48 * k, 64 * k);
    setAreaTransparent(48 * k, 48 * k, 64 * k, 64 * k);
    return var2;
  }
  



  public void func_152634_a() {}
  



  private void setAreaTransparent(int p_78434_1_, int p_78434_2_, int p_78434_3_, int p_78434_4_)
  {
    if (!hasTransparency(p_78434_1_, p_78434_2_, p_78434_3_, p_78434_4_))
    {
      for (int var5 = p_78434_1_; var5 < p_78434_3_; var5++)
      {
        for (int var6 = p_78434_2_; var6 < p_78434_4_; var6++)
        {
          imageData[(var5 + var6 * imageWidth)] &= 0xFFFFFF;
        }
      }
    }
  }
  



  private void setAreaOpaque(int p_78433_1_, int p_78433_2_, int p_78433_3_, int p_78433_4_)
  {
    for (int var5 = p_78433_1_; var5 < p_78433_3_; var5++)
    {
      for (int var6 = p_78433_2_; var6 < p_78433_4_; var6++)
      {
        imageData[(var5 + var6 * imageWidth)] |= 0xFF000000;
      }
    }
  }
  



  private boolean hasTransparency(int p_78435_1_, int p_78435_2_, int p_78435_3_, int p_78435_4_)
  {
    for (int var5 = p_78435_1_; var5 < p_78435_3_; var5++)
    {
      for (int var6 = p_78435_2_; var6 < p_78435_4_; var6++)
      {
        int var7 = imageData[(var5 + var6 * imageWidth)];
        
        if ((var7 >> 24 & 0xFF) < 128)
        {
          return true;
        }
      }
    }
    
    return false;
  }
}
