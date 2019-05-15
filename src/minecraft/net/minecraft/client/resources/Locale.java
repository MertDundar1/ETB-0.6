package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale
{
  private static final Splitter splitter = Splitter.on('=').limit(2);
  private static final Pattern field_135031_c = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
  Map field_135032_a = Maps.newHashMap();
  
  private boolean field_135029_d;
  private static final String __OBFID = "CL_00001097";
  
  public Locale() {}
  
  public synchronized void loadLocaleDataFiles(IResourceManager p_135022_1_, List p_135022_2_)
  {
    field_135032_a.clear();
    Iterator var3 = p_135022_2_.iterator();
    Iterator var6;
    for (; var3.hasNext(); 
        




        var6.hasNext())
    {
      String var4 = (String)var3.next();
      String var5 = String.format("lang/%s.lang", new Object[] { var4 });
      var6 = p_135022_1_.getResourceDomains().iterator();
      
      continue;
      
      String var7 = (String)var6.next();
      
      try
      {
        loadLocaleData(p_135022_1_.getAllResources(new net.minecraft.util.ResourceLocation(var7, var5)));
      }
      catch (IOException localIOException) {}
    }
    




    checkUnicode();
  }
  
  public boolean isUnicode()
  {
    return field_135029_d;
  }
  
  private void checkUnicode()
  {
    field_135029_d = false;
    int var1 = 0;
    int var2 = 0;
    Iterator var3 = field_135032_a.values().iterator();
    int var5;
    int var6; for (; var3.hasNext(); 
        




        var6 < var5)
    {
      String var4 = (String)var3.next();
      var5 = var4.length();
      var2 += var5;
      
      var6 = 0; continue;
      
      if (var4.charAt(var6) >= 'Ā')
      {
        var1++;
      }
      var6++;
    }
    






    float var7 = var1 / var2;
    field_135029_d = (var7 > 0.1D);
  }
  


  private void loadLocaleData(List p_135028_1_)
    throws IOException
  {
    Iterator var2 = p_135028_1_.iterator();
    
    while (var2.hasNext())
    {
      IResource var3 = (IResource)var2.next();
      InputStream var4 = var3.getInputStream();
      
      try
      {
        loadLocaleData(var4);
      }
      finally
      {
        IOUtils.closeQuietly(var4);
      }
    }
  }
  
  private void loadLocaleData(InputStream p_135021_1_) throws IOException
  {
    Iterator var2 = IOUtils.readLines(p_135021_1_, Charsets.UTF_8).iterator();
    
    while (var2.hasNext())
    {
      String var3 = (String)var2.next();
      
      if ((!var3.isEmpty()) && (var3.charAt(0) != '#'))
      {
        String[] var4 = (String[])com.google.common.collect.Iterables.toArray(splitter.split(var3), String.class);
        
        if ((var4 != null) && (var4.length == 2))
        {
          String var5 = var4[0];
          String var6 = field_135031_c.matcher(var4[1]).replaceAll("%$1s");
          field_135032_a.put(var5, var6);
        }
      }
    }
  }
  



  private String translateKeyPrivate(String p_135026_1_)
  {
    String var2 = (String)field_135032_a.get(p_135026_1_);
    return var2 == null ? p_135026_1_ : var2;
  }
  



  public String formatMessage(String p_135023_1_, Object[] p_135023_2_)
  {
    String var3 = translateKeyPrivate(p_135023_1_);
    
    try
    {
      return String.format(var3, p_135023_2_);
    }
    catch (IllegalFormatException var5) {}
    
    return "Format error: " + var3;
  }
}
