package com.enjoytheban.ui.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CFont
{
  private final float imgSize = 512.0F;
  protected CharData[] charData = new CharData['Ā'];
  protected Font font;
  protected boolean antiAlias;
  protected boolean fractionalMetrics;
  protected int fontHeight = -1;
  protected int charOffset = 0;
  protected DynamicTexture tex;
  
  public CFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
    this.font = font;
    this.antiAlias = antiAlias;
    this.fractionalMetrics = fractionalMetrics;
    tex = setupTexture(font, antiAlias, fractionalMetrics, charData);
  }
  
  protected DynamicTexture setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars) {
    BufferedImage img = generateFontImage(font, antiAlias, fractionalMetrics, chars);
    try {
      return new DynamicTexture(img);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars) {
    int imgSize = 512;
    BufferedImage bufferedImage = new BufferedImage(imgSize, imgSize, 2);
    Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
    g.setFont(font);
    g.setColor(new Color(255, 255, 255, 0));
    g.fillRect(0, 0, imgSize, imgSize);
    g.setColor(Color.WHITE);
    g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    FontMetrics fontMetrics = g.getFontMetrics();
    int charHeight = 0;
    int positionX = 0;
    int positionY = 1;
    for (int i = 0; i < chars.length; i++) {
      char ch = (char)i;
      CharData charData = new CharData();
      Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(ch), g);
      width = (getBoundswidth + 8);
      height = getBoundsheight;
      if (positionX + width >= imgSize) {
        positionX = 0;
        positionY += charHeight;
        charHeight = 0;
      }
      if (height > charHeight) {
        charHeight = height;
      }
      storedX = positionX;
      storedY = positionY;
      if (height > fontHeight) {
        fontHeight = height;
      }
      chars[i] = charData;
      g.drawString(String.valueOf(ch), positionX + 2, positionY + fontMetrics.getAscent());
      positionX += width;
    }
    return bufferedImage;
  }
  
  public void drawChar(CharData[] chars, char c, float x, float y) throws ArrayIndexOutOfBoundsException {
    try {
      drawQuad(x, y, width, height, storedX, storedY, width, height);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  protected void drawQuad(float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
    float renderSRCX = srcX / 512.0F;
    float renderSRCY = srcY / 512.0F;
    float renderSRCWidth = srcWidth / 512.0F;
    float renderSRCHeight = srcHeight / 512.0F;
    GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
    GL11.glVertex2d(x + width, y);
    GL11.glTexCoord2f(renderSRCX, renderSRCY);
    GL11.glVertex2d(x, y);
    GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
    GL11.glVertex2d(x, y + height);
    GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
    GL11.glVertex2d(x, y + height);
    GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
    GL11.glVertex2d(x + width, y + height);
    GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
    GL11.glVertex2d(x + width, y);
  }
  
  public int getStringHeight(String text) {
    return getHeight();
  }
  
  public int getHeight() {
    return (fontHeight - 8) / 2;
  }
  
  public int getStringWidth(String text) {
    int width = 0;
    for (char c : text.toCharArray()) {
      if ((c < charData.length) && (c >= 0)) width += charData[c].width - 8 + charOffset;
    }
    return width / 2;
  }
  
  public boolean isAntiAlias() {
    return antiAlias;
  }
  
  public void setAntiAlias(boolean antiAlias) {
    if (this.antiAlias != antiAlias) {
      this.antiAlias = antiAlias;
      tex = setupTexture(font, antiAlias, fractionalMetrics, charData);
    }
  }
  
  public boolean isFractionalMetrics() {
    return fractionalMetrics;
  }
  
  public void setFractionalMetrics(boolean fractionalMetrics) {
    if (this.fractionalMetrics != fractionalMetrics) {
      this.fractionalMetrics = fractionalMetrics;
      tex = setupTexture(font, antiAlias, fractionalMetrics, charData);
    }
  }
  
  public Font getFont() {
    return font;
  }
  
  public void setFont(Font font) {
    this.font = font;
    tex = setupTexture(font, antiAlias, fractionalMetrics, charData);
  }
  
  protected class CharData
  {
    public int width;
    public int height;
    public int storedX;
    public int storedY;
    
    protected CharData() {}
  }
}
