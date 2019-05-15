package net.minecraft.client.gui;

import com.enjoytheban.Client;
import com.enjoytheban.management.ModuleManager;
import com.enjoytheban.module.modules.player.NoStrike;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.CustomColors;
import optifine.FontUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener
{
  private static final ResourceLocation[] unicodePageLocations = new ResourceLocation['Ā'];
  

  private float[] charWidth = new float['Ā'];
  

  public int FONT_HEIGHT = 9;
  public Random fontRandom = new Random();
  




  private byte[] glyphWidth = new byte[65536];
  




  private int[] colorCode = new int[32];
  

  private ResourceLocation locationFontTexture;
  

  private final TextureManager renderEngine;
  

  private float posX;
  

  private float posY;
  

  private boolean unicodeFlag;
  

  private boolean bidiFlag;
  

  private float red;
  

  private float blue;
  

  private float green;
  

  private float alpha;
  

  private int textColor;
  

  private boolean randomStyle;
  

  private boolean boldStyle;
  

  private boolean italicStyle;
  

  private boolean underlineStyle;
  

  private boolean strikethroughStyle;
  

  private static final String __OBFID = "CL_00000660";
  

  public GameSettings gameSettings;
  

  public ResourceLocation locationFontTextureBase;
  

  public boolean enabled = true;
  public float offsetBold = 1.0F;
  
  public FontRenderer(GameSettings p_i1035_1_, ResourceLocation p_i1035_2_, TextureManager p_i1035_3_, boolean p_i1035_4_)
  {
    gameSettings = p_i1035_1_;
    locationFontTextureBase = p_i1035_2_;
    locationFontTexture = p_i1035_2_;
    renderEngine = p_i1035_3_;
    unicodeFlag = p_i1035_4_;
    locationFontTexture = FontUtils.getHdFontLocation(locationFontTextureBase);
    bindTexture(locationFontTexture);
    
    for (int var5 = 0; var5 < 32; var5++) {
      int var6 = (var5 >> 3 & 0x1) * 85;
      int var7 = (var5 >> 2 & 0x1) * 170 + var6;
      int var8 = (var5 >> 1 & 0x1) * 170 + var6;
      int var9 = (var5 >> 0 & 0x1) * 170 + var6;
      
      if (var5 == 6) {
        var7 += 85;
      }
      
      if (anaglyph) {
        int var10 = (var7 * 30 + var8 * 59 + var9 * 11) / 100;
        int var11 = (var7 * 30 + var8 * 70) / 100;
        int var12 = (var7 * 30 + var9 * 70) / 100;
        var7 = var10;
        var8 = var11;
        var9 = var12;
      }
      
      if (var5 >= 16) {
        var7 /= 4;
        var8 /= 4;
        var9 /= 4;
      }
      
      colorCode[var5] = ((var7 & 0xFF) << 16 | (var8 & 0xFF) << 8 | var9 & 0xFF);
    }
    
    readGlyphSizes();
  }
  
  public void onResourceManagerReload(IResourceManager resourceManager) {
    locationFontTexture = FontUtils.getHdFontLocation(locationFontTextureBase);
    
    for (int i = 0; i < unicodePageLocations.length; i++) {
      unicodePageLocations[i] = null;
    }
    
    readFontTexture();
    readGlyphSizes();
  }
  
  private void readFontTexture()
  {
    try
    {
      bufferedimage = net.minecraft.client.renderer.texture.TextureUtil.func_177053_a(getResourceInputStream(locationFontTexture));
    } catch (IOException var21) { BufferedImage bufferedimage;
      throw new RuntimeException(var21);
    }
    BufferedImage bufferedimage;
    Properties props = FontUtils.readFontProperties(locationFontTexture);
    int imgWidth = bufferedimage.getWidth();
    int imgHeight = bufferedimage.getHeight();
    int charW = imgWidth / 16;
    int charH = imgHeight / 16;
    float kx = imgWidth / 128.0F;
    float boldScaleFactor = Config.limit(kx, 1.0F, 2.0F);
    offsetBold = (1.0F / boldScaleFactor);
    float offsetBoldConfig = FontUtils.readFloat(props, "offsetBold", -1.0F);
    
    if (offsetBoldConfig >= 0.0F) {
      offsetBold = offsetBoldConfig;
    }
    
    int[] ai = new int[imgWidth * imgHeight];
    bufferedimage.getRGB(0, 0, imgWidth, imgHeight, ai, 0, imgWidth);
    int k = 0;
    
    while (k < 256) {
      int cx = k % 16;
      int cy = k / 16;
      boolean px = false;
      int var22 = charW - 1;
      

      while (var22 >= 0) {
        int x = cx * charW + var22;
        boolean flag = true;
        
        for (int py = 0; (py < charH) && (flag); py++) {
          int ypos = (cy * charH + py) * imgWidth;
          int col = ai[(x + ypos)];
          int al = col >> 24 & 0xFF;
          
          if (al > 16) {
            flag = false;
          }
        }
        
        if (!flag) break;
        var22--;
      }
      


      if (k == 65) {
        k = k;
      }
      
      if (k == 32) {
        if (charW <= 8) {
          var22 = (int)(2.0F * kx);
        } else {
          var22 = (int)(1.5F * kx);
        }
      }
      
      charWidth[k] = ((var22 + 1) / kx + 1.0F);
      k++;
    }
    


    FontUtils.readCustomCharWidths(props, charWidth);
  }
  
  private void readGlyphSizes() {
    InputStream var1 = null;
    try
    {
      var1 = getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
      var1.read(glyphWidth);
    } catch (IOException var6) {
      throw new RuntimeException(var6);
    } finally {
      IOUtils.closeQuietly(var1);
    }
  }
  


  private float renderCharAtPos(int p_78278_1_, char p_78278_2_, boolean p_78278_3_)
  {
    return 
    
      ("ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000"
      .indexOf(p_78278_2_) != -1) && (!unicodeFlag) ? 
      renderDefaultChar(p_78278_1_, p_78278_3_) : p_78278_2_ == ' ' ? 4.0F : p_78278_2_ == ' ' ? 4.0F : !unicodeFlag ? charWidth[p_78278_2_] : 
      renderUnicodeChar(p_78278_2_, p_78278_3_);
  }
  



  private float renderDefaultChar(int p_78266_1_, boolean p_78266_2_)
  {
    float var3 = p_78266_1_ % 16 * 8;
    float var4 = p_78266_1_ / 16 * 8;
    float var5 = p_78266_2_ ? 1.0F : 0.0F;
    bindTexture(locationFontTexture);
    float var6 = 7.99F;
    GL11.glBegin(5);
    GL11.glTexCoord2f(var3 / 128.0F, var4 / 128.0F);
    GL11.glVertex3f(posX + var5, posY, 0.0F);
    GL11.glTexCoord2f(var3 / 128.0F, (var4 + 7.99F) / 128.0F);
    GL11.glVertex3f(posX - var5, posY + 7.99F, 0.0F);
    GL11.glTexCoord2f((var3 + var6 - 1.0F) / 128.0F, var4 / 128.0F);
    GL11.glVertex3f(posX + var6 - 1.0F + var5, posY, 0.0F);
    GL11.glTexCoord2f((var3 + var6 - 1.0F) / 128.0F, (var4 + 7.99F) / 128.0F);
    GL11.glVertex3f(posX + var6 - 1.0F - var5, posY + 7.99F, 0.0F);
    GL11.glEnd();
    return charWidth[p_78266_1_];
  }
  
  private ResourceLocation getUnicodePageLocation(int p_111271_1_) {
    if (unicodePageLocations[p_111271_1_] == null) {
      unicodePageLocations[p_111271_1_] = new ResourceLocation(
        String.format("textures/font/unicode_page_%02x.png", new Object[] { Integer.valueOf(p_111271_1_) }));
      unicodePageLocations[p_111271_1_] = FontUtils.getHdFontLocation(unicodePageLocations[p_111271_1_]);
    }
    
    return unicodePageLocations[p_111271_1_];
  }
  



  private void loadGlyphTexture(int p_78257_1_)
  {
    bindTexture(getUnicodePageLocation(p_78257_1_));
  }
  



  private float renderUnicodeChar(char p_78277_1_, boolean p_78277_2_)
  {
    if (glyphWidth[p_78277_1_] == 0) {
      return 0.0F;
    }
    int var3 = p_78277_1_ / 'Ā';
    loadGlyphTexture(var3);
    int var4 = glyphWidth[p_78277_1_] >>> 4;
    int var5 = glyphWidth[p_78277_1_] & 0xF;
    var4 &= 0xF;
    float var6 = var4;
    float var7 = var5 + 1;
    float var8 = p_78277_1_ % '\020' * 16 + var6;
    float var9 = (p_78277_1_ & 0xFF) / '\020' * 16;
    float var10 = var7 - var6 - 0.02F;
    float var11 = p_78277_2_ ? 1.0F : 0.0F;
    GL11.glBegin(5);
    GL11.glTexCoord2f(var8 / 256.0F, var9 / 256.0F);
    GL11.glVertex3f(posX + var11, posY, 0.0F);
    GL11.glTexCoord2f(var8 / 256.0F, (var9 + 15.98F) / 256.0F);
    GL11.glVertex3f(posX - var11, posY + 7.99F, 0.0F);
    GL11.glTexCoord2f((var8 + var10) / 256.0F, var9 / 256.0F);
    GL11.glVertex3f(posX + var10 / 2.0F + var11, posY, 0.0F);
    GL11.glTexCoord2f((var8 + var10) / 256.0F, (var9 + 15.98F) / 256.0F);
    GL11.glVertex3f(posX + var10 / 2.0F - var11, posY + 7.99F, 0.0F);
    GL11.glEnd();
    return (var7 - var6) / 2.0F + 1.0F;
  }
  









  public int drawStringWithShadow(String text, float x, float y, int color)
  {
    return func_175065_a(text, x, y, color, true);
  }
  


  public int drawString(String text, int x, int y, int color)
  {
    return !enabled ? 0 : func_175065_a(text, x, y, color, false);
  }
  
  public int func_175065_a(String p_175065_1_, float p_175065_2_, float p_175065_3_, int p_175065_4_, boolean p_175065_5_)
  {
    enableAlpha();
    if (StringUtils.containsIgnoreCase(p_175065_1_, "Internal Exception: java.lang.NullPointerException")) {
      p_175065_1_ = p_175065_1_.replaceAll("(?i)Internal Exception: java.lang.NullPointerException", String.valueOf("") + EnumChatFormatting.BLUE + "Please use our alt manager to log into an account!");
    }
    if (Client.instance.getModuleManager().getModuleByClass(NoStrike.class).isEnabled()) {
      if (StringUtils.containsIgnoreCase(p_175065_1_, "VeltLLC")) {
        p_175065_1_ = p_175065_1_.replaceAll("(?i)VeltLLC", String.valueOf("") + EnumChatFormatting.BLUE + "ETB");
      }
      if (StringUtils.containsIgnoreCase(p_175065_1_, "VeltPVP")) {
        p_175065_1_ = p_175065_1_.replaceAll("(?i)VeltPVP", String.valueOf("") + EnumChatFormatting.BLUE + "ETB");
      }
      if (StringUtils.containsIgnoreCase(p_175065_1_, "Velt")) {
        p_175065_1_ = p_175065_1_.replaceAll("(?i)Velt", String.valueOf("") + EnumChatFormatting.BLUE + "ETB");
      }
      if (StringUtils.containsIgnoreCase(p_175065_1_, "Arcane")) {
        p_175065_1_ = p_175065_1_.replaceAll("(?i)Arcane", String.valueOf("") + EnumChatFormatting.BLUE + "ETB");
      }
      if (StringUtils.containsIgnoreCase(p_175065_1_, "FaithfulMC")) {
        p_175065_1_ = p_175065_1_.replaceAll("(?i)FaithfulMC", String.valueOf("") + EnumChatFormatting.BLUE + "ETB");
      }
      if (StringUtils.containsIgnoreCase(p_175065_1_, "Faithful")) {
        p_175065_1_ = p_175065_1_.replaceAll("(?i)Faithful", String.valueOf("") + EnumChatFormatting.BLUE + "ETB");
      }
      if (StringUtils.containsIgnoreCase(p_175065_1_, "ViperMC")) {
        p_175065_1_ = p_175065_1_.replaceAll("(?i)ViperMC", String.valueOf("") + EnumChatFormatting.BLUE + "ETB");
      }
      if (StringUtils.containsIgnoreCase(p_175065_1_, "Viper")) {
        p_175065_1_ = p_175065_1_.replaceAll("(?i)Viper", String.valueOf("") + EnumChatFormatting.BLUE + "ETB");
      }
    }
    resetStyles();
    
    int var6;
    if (p_175065_5_) {
      int var6 = func_180455_b(p_175065_1_, p_175065_2_ + 1.0F, p_175065_3_ + 1.0F, p_175065_4_, true);
      var6 = Math.max(var6, func_180455_b(p_175065_1_, p_175065_2_, p_175065_3_, p_175065_4_, false));
    } else {
      var6 = func_180455_b(p_175065_1_, p_175065_2_, p_175065_3_, p_175065_4_, false);
    }
    
    return var6;
  }
  


  private String bidiReorder(String p_147647_1_)
  {
    try
    {
      Bidi var3 = new Bidi(new ArabicShaping(8).shape(p_147647_1_), 127);
      var3.setReorderingMode(0);
      return var3.writeReordered(2);
    } catch (ArabicShapingException var31) {}
    return p_147647_1_;
  }
  




  private void resetStyles()
  {
    randomStyle = false;
    boldStyle = false;
    italicStyle = false;
    underlineStyle = false;
    strikethroughStyle = false;
  }
  


  private void renderStringAtPos(String p_78255_1_, boolean p_78255_2_)
  {
    for (int var3 = 0; var3 < p_78255_1_.length(); var3++) {
      char var4 = p_78255_1_.charAt(var3);
      


      if ((var4 == '§') && (var3 + 1 < p_78255_1_.length())) {
        int var5 = "0123456789abcdefklmnor".indexOf(p_78255_1_.toLowerCase().charAt(var3 + 1));
        
        if (var5 < 16) {
          randomStyle = false;
          boldStyle = false;
          strikethroughStyle = false;
          underlineStyle = false;
          italicStyle = false;
          
          if ((var5 < 0) || (var5 > 15)) {
            var5 = 15;
          }
          
          if (p_78255_2_) {
            var5 += 16;
          }
          
          int var6 = colorCode[var5];
          
          if (Config.isCustomColors()) {
            var6 = CustomColors.getTextColor(var5, var6);
          }
          
          textColor = var6;
          setColor((var6 >> 16) / 255.0F, (var6 >> 8 & 0xFF) / 255.0F, 
            (var6 & 0xFF) / 255.0F, alpha);
        } else if (var5 == 16) {
          randomStyle = true;
        } else if (var5 == 17) {
          boldStyle = true;
        } else if (var5 == 18) {
          strikethroughStyle = true;
        } else if (var5 == 19) {
          underlineStyle = true;
        } else if (var5 == 20) {
          italicStyle = true;
        } else if (var5 == 21) {
          randomStyle = false;
          boldStyle = false;
          strikethroughStyle = false;
          underlineStyle = false;
          italicStyle = false;
          setColor(red, blue, green, alpha);
        }
        
        var3++;
      } else {
        int var5 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000"
          .indexOf(var4);
        
        if ((randomStyle) && (var5 != -1)) {
          int var6;
          do { var6 = fontRandom.nextInt(charWidth.length);
          } while ((int)charWidth[var5] != (int)charWidth[var6]);
          
          var5 = var6;
        }
        
        float var12 = (var5 != -1) && (!unicodeFlag) ? offsetBold : 0.5F;
        boolean var7 = ((var4 == 0) || (var5 == -1) || (unicodeFlag)) && (p_78255_2_);
        
        if (var7) {
          posX -= var12;
          posY -= var12;
        }
        
        float var8 = renderCharAtPos(var5, var4, italicStyle);
        
        if (var7) {
          posX += var12;
          posY += var12;
        }
        
        if (boldStyle) {
          posX += var12;
          
          if (var7) {
            posX -= var12;
            posY -= var12;
          }
          
          renderCharAtPos(var5, var4, italicStyle);
          posX -= var12;
          
          if (var7) {
            posX += var12;
            posY += var12;
          }
          
          var8 += var12;
        }
        



        if (strikethroughStyle) {
          Tessellator var9 = Tessellator.getInstance();
          WorldRenderer var10 = var9.getWorldRenderer();
          GlStateManager.func_179090_x();
          var10.startDrawingQuads();
          var10.addVertex(posX, posY + FONT_HEIGHT / 2, 0.0D);
          var10.addVertex(posX + var8, posY + FONT_HEIGHT / 2, 
            0.0D);
          var10.addVertex(posX + var8, 
            posY + FONT_HEIGHT / 2 - 1.0F, 0.0D);
          var10.addVertex(posX, posY + FONT_HEIGHT / 2 - 1.0F, 
            0.0D);
          var9.draw();
          GlStateManager.func_179098_w();
        }
        
        if (underlineStyle) {
          Tessellator var9 = Tessellator.getInstance();
          WorldRenderer var10 = var9.getWorldRenderer();
          GlStateManager.func_179090_x();
          var10.startDrawingQuads();
          int var11 = underlineStyle ? -1 : 0;
          var10.addVertex(posX + var11, 
            posY + FONT_HEIGHT, 0.0D);
          var10.addVertex(posX + var8, posY + FONT_HEIGHT, 0.0D);
          var10.addVertex(posX + var8, posY + FONT_HEIGHT - 1.0F, 
            0.0D);
          var10.addVertex(posX + var11, 
            posY + FONT_HEIGHT - 1.0F, 0.0D);
          var9.draw();
          GlStateManager.func_179098_w();
        }
        
        posX += var8;
      }
    }
  }
  



  private int renderStringAligned(String p_78274_1_, int p_78274_2_, int p_78274_3_, int p_78274_4_, int p_78274_5_, boolean p_78274_6_)
  {
    if (bidiFlag) {
      int var7 = getStringWidth(bidiReorder(p_78274_1_));
      p_78274_2_ = p_78274_2_ + p_78274_4_ - var7;
    }
    
    return func_180455_b(p_78274_1_, p_78274_2_, p_78274_3_, p_78274_5_, p_78274_6_);
  }
  
  private int func_180455_b(String p_180455_1_, float p_180455_2_, float p_180455_3_, int p_180455_4_, boolean p_180455_5_)
  {
    if (p_180455_1_ == null) {
      return 0;
    }
    if (bidiFlag) {
      p_180455_1_ = bidiReorder(p_180455_1_);
    }
    
    if ((p_180455_4_ & 0xFC000000) == 0) {
      p_180455_4_ |= 0xFF000000;
    }
    
    if (p_180455_5_) {
      p_180455_4_ = (p_180455_4_ & 0xFCFCFC) >> 2 | p_180455_4_ & 0xFF000000;
    }
    
    red = ((p_180455_4_ >> 16 & 0xFF) / 255.0F);
    blue = ((p_180455_4_ >> 8 & 0xFF) / 255.0F);
    green = ((p_180455_4_ & 0xFF) / 255.0F);
    alpha = ((p_180455_4_ >> 24 & 0xFF) / 255.0F);
    setColor(red, blue, green, alpha);
    posX = p_180455_2_;
    posY = p_180455_3_;
    renderStringAtPos(p_180455_1_, p_180455_5_);
    return (int)posX;
  }
  




  public int getStringWidth(String p_78256_1_)
  {
    if (p_78256_1_ == null) {
      return 0;
    }
    float var2 = 0.0F;
    boolean var3 = false;
    
    for (int var4 = 0; var4 < p_78256_1_.length(); var4++) {
      char var5 = p_78256_1_.charAt(var4);
      float var6 = getCharWidthFloat(var5);
      
      if ((var6 < 0.0F) && (var4 < p_78256_1_.length() - 1)) {
        var4++;
        var5 = p_78256_1_.charAt(var4);
        
        if ((var5 != 'l') && (var5 != 'L')) {
          if ((var5 == 'r') || (var5 == 'R')) {
            var3 = false;
          }
        } else {
          var3 = true;
        }
        
        var6 = 0.0F;
      }
      
      var2 += var6;
      
      if ((var3) && (var6 > 0.0F)) {
        var2 += (unicodeFlag ? 1.0F : offsetBold);
      }
    }
    
    return (int)var2;
  }
  



  public int getCharWidth(char par1)
  {
    return Math.round(getCharWidthFloat(par1));
  }
  
  private float getCharWidthFloat(char p_78263_1_) {
    if (p_78263_1_ == '§')
      return -1.0F;
    if (p_78263_1_ == ' ') {
      return charWidth[32];
    }
    int var2 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000"
      .indexOf(p_78263_1_);
    
    if ((p_78263_1_ > 0) && (var2 != -1) && (!unicodeFlag))
      return charWidth[var2];
    if (glyphWidth[p_78263_1_] != 0) {
      int var3 = glyphWidth[p_78263_1_] >>> 4;
      int var4 = glyphWidth[p_78263_1_] & 0xF;
      var3 &= 0xF;
      var4++;
      return (var4 - var3) / 2 + 1;
    }
    return 0.0F;
  }
  




  public String trimStringToWidth(String p_78269_1_, int p_78269_2_)
  {
    return trimStringToWidth(p_78269_1_, p_78269_2_, false);
  }
  


  public String trimStringToWidth(String p_78262_1_, int p_78262_2_, boolean p_78262_3_)
  {
    StringBuilder var4 = new StringBuilder();
    float var5 = 0.0F;
    int var6 = p_78262_3_ ? p_78262_1_.length() - 1 : 0;
    int var7 = p_78262_3_ ? -1 : 1;
    boolean var8 = false;
    boolean var9 = false;
    
    for (int var10 = var6; (var10 >= 0) && (var10 < p_78262_1_.length()) && (var5 < p_78262_2_); var10 += var7) {
      char var11 = p_78262_1_.charAt(var10);
      float var12 = getCharWidthFloat(var11);
      
      if (var8) {
        var8 = false;
        
        if ((var11 != 'l') && (var11 != 'L')) {
          if ((var11 == 'r') || (var11 == 'R')) {
            var9 = false;
          }
        } else {
          var9 = true;
        }
      } else if (var12 < 0.0F) {
        var8 = true;
      } else {
        var5 += var12;
        
        if (var9) {
          var5 += 1.0F;
        }
      }
      
      if (var5 > p_78262_2_) {
        break;
      }
      
      if (p_78262_3_) {
        var4.insert(0, var11);
      } else {
        var4.append(var11);
      }
    }
    
    return var4.toString();
  }
  


  private String trimStringNewline(String p_78273_1_)
  {
    while ((p_78273_1_ != null) && (p_78273_1_.endsWith("\n"))) {
      p_78273_1_ = p_78273_1_.substring(0, p_78273_1_.length() - 1);
    }
    
    return p_78273_1_;
  }
  


  public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor)
  {
    resetStyles();
    this.textColor = textColor;
    str = trimStringNewline(str);
    renderSplitString(str, x, y, wrapWidth, false);
  }
  



  private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow)
  {
    List var6 = listFormattedStringToWidth(str, wrapWidth);
    
    for (Iterator var7 = var6.iterator(); var7.hasNext(); y += FONT_HEIGHT) {
      String var8 = (String)var7.next();
      renderStringAligned(var8, x, y, wrapWidth, textColor, addShadow);
    }
  }
  


  public int splitStringWidth(String p_78267_1_, int p_78267_2_)
  {
    return FONT_HEIGHT * listFormattedStringToWidth(p_78267_1_, p_78267_2_).size();
  }
  



  public void setUnicodeFlag(boolean p_78264_1_)
  {
    unicodeFlag = p_78264_1_;
  }
  



  public boolean getUnicodeFlag()
  {
    return unicodeFlag;
  }
  



  public void setBidiFlag(boolean p_78275_1_)
  {
    bidiFlag = p_78275_1_;
  }
  


  public List listFormattedStringToWidth(String str, int wrapWidth)
  {
    return Arrays.asList(wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
  }
  



  String wrapFormattedStringToWidth(String str, int wrapWidth)
  {
    int var3 = sizeStringToWidth(str, wrapWidth);
    
    if (str.length() <= var3) {
      return str;
    }
    String var4 = str.substring(0, var3);
    char var5 = str.charAt(var3);
    boolean var6 = (var5 == ' ') || (var5 == '\n');
    String var7 = getFormatFromString(var4) + str.substring(var3 + (var6 ? 1 : 0));
    return var4 + "\n" + wrapFormattedStringToWidth(var7, wrapWidth);
  }
  




  private int sizeStringToWidth(String str, int wrapWidth)
  {
    int var3 = str.length();
    float var4 = 0.0F;
    int var5 = 0;
    int var6 = -1;
    
    for (boolean var7 = false; var5 < var3; var5++) {
      char var8 = str.charAt(var5);
      
      switch (var8) {
      case '\n': 
        var5--;
        break;
      
      case '§': 
        if (var5 < var3 - 1) {
          var5++;
          char var9 = str.charAt(var5);
          
          if ((var9 != 'l') && (var9 != 'L')) {
            if ((var9 == 'r') || (var9 == 'R') || (isFormatColor(var9))) {
              var7 = false;
            }
          } else {
            var7 = true;
          }
        }
        
        break;
      
      case ' ': 
        var6 = var5;
      
      default: 
        var4 += getCharWidthFloat(var8);
        
        if (var7) {
          var4 += 1.0F;
        }
        break;
      }
      if (var8 == '\n') {
        var5++;
        var6 = var5;
      }
      else
      {
        if (var4 > wrapWidth) {
          break;
        }
      }
    }
    return (var5 != var3) && (var6 != -1) && (var6 < var5) ? var6 : var5;
  }
  


  private static boolean isFormatColor(char colorChar)
  {
    return ((colorChar >= '0') && (colorChar <= '9')) || ((colorChar >= 'a') && (colorChar <= 'f')) || (
      (colorChar >= 'A') && (colorChar <= 'F'));
  }
  


  private static boolean isFormatSpecial(char formatChar)
  {
    return ((formatChar >= 'k') && (formatChar <= 'o')) || ((formatChar >= 'K') && (formatChar <= 'O')) || (formatChar == 'r') || 
      (formatChar == 'R');
  }
  



  public static String getFormatFromString(String p_78282_0_)
  {
    String var1 = "";
    int var2 = -1;
    int var3 = p_78282_0_.length();
    
    while ((var2 = p_78282_0_.indexOf('§', var2 + 1)) != -1) {
      if (var2 < var3 - 1) {
        char var4 = p_78282_0_.charAt(var2 + 1);
        
        if (isFormatColor(var4)) {
          var1 = "§" + var4;
        } else if (isFormatSpecial(var4)) {
          var1 = var1 + "§" + var4;
        }
      }
    }
    
    return var1;
  }
  



  public boolean getBidiFlag()
  {
    return bidiFlag;
  }
  
  public int func_175064_b(char p_175064_1_) {
    int index = "0123456789abcdef".indexOf(p_175064_1_);
    
    if ((index >= 0) && (index < colorCode.length)) {
      int color = colorCode[index];
      
      if (Config.isCustomColors()) {
        color = CustomColors.getTextColor(index, color);
      }
      
      return color;
    }
    return 16777215;
  }
  
  protected void setColor(float r, float g, float b, float a)
  {
    GlStateManager.color(r, g, b, a);
  }
  

  protected void enableAlpha() {}
  
  protected void bindTexture(ResourceLocation location)
  {
    renderEngine.bindTexture(location);
  }
  
  protected InputStream getResourceInputStream(ResourceLocation location) throws IOException {
    return Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
  }
  
  public void drawCenteredString(String text, int x, int y, int color) {
    drawStringWithShadow(text, x - getStringWidth(text) / 2, y, color);
  }
}
