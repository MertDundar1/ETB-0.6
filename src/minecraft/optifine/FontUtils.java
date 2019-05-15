package optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;

public class FontUtils
{
  public FontUtils() {}
  
  public static Properties readFontProperties(ResourceLocation locationFontTexture)
  {
    String fontFileName = locationFontTexture.getResourcePath();
    Properties props = new Properties();
    String suffix = ".png";
    
    if (!fontFileName.endsWith(suffix))
    {
      return props;
    }
    

    String fileName = fontFileName.substring(0, fontFileName.length() - suffix.length()) + ".properties";
    
    try
    {
      ResourceLocation e = new ResourceLocation(locationFontTexture.getResourceDomain(), fileName);
      java.io.InputStream in = Config.getResourceStream(Config.getResourceManager(), e);
      
      if (in == null)
      {
        return props;
      }
      
      Config.log("Loading " + fileName);
      props.load(in);


    }
    catch (FileNotFoundException localFileNotFoundException) {}catch (IOException var8)
    {


      var8.printStackTrace();
    }
    
    return props;
  }
  

  public static void readCustomCharWidths(Properties props, float[] charWidth)
  {
    java.util.Set keySet = props.keySet();
    Iterator iter = keySet.iterator();
    
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      String prefix = "width.";
      
      if (key.startsWith(prefix))
      {
        String numStr = key.substring(prefix.length());
        int num = Config.parseInt(numStr, -1);
        
        if ((num >= 0) && (num < charWidth.length))
        {
          String value = props.getProperty(key);
          float width = Config.parseFloat(value, -1.0F);
          
          if (width >= 0.0F)
          {
            charWidth[num] = width;
          }
        }
      }
    }
  }
  
  public static float readFloat(Properties props, String key, float defOffset)
  {
    String str = props.getProperty(key);
    
    if (str == null)
    {
      return defOffset;
    }
    

    float offset = Config.parseFloat(str, Float.MIN_VALUE);
    
    if (offset == Float.MIN_VALUE)
    {
      Config.warn("Invalid value for " + key + ": " + str);
      return defOffset;
    }
    

    return offset;
  }
  


  public static ResourceLocation getHdFontLocation(ResourceLocation fontLoc)
  {
    if (!Config.isCustomFonts())
    {
      return fontLoc;
    }
    if (fontLoc == null)
    {
      return fontLoc;
    }
    

    String fontName = fontLoc.getResourcePath();
    String texturesStr = "textures/";
    String mcpatcherStr = "mcpatcher/";
    
    if (!fontName.startsWith(texturesStr))
    {
      return fontLoc;
    }
    

    fontName = fontName.substring(texturesStr.length());
    fontName = mcpatcherStr + fontName;
    ResourceLocation fontLocHD = new ResourceLocation(fontLoc.getResourceDomain(), fontName);
    return Config.hasResource(Config.getResourceManager(), fontLocHD) ? fontLocHD : fontLoc;
  }
}
