package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileResourcePack extends AbstractResourcePack implements Closeable
{
  public static final Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(3);
  private ZipFile resourcePackZipFile;
  private static final String __OBFID = "CL_00001075";
  
  public FileResourcePack(File p_i1290_1_)
  {
    super(p_i1290_1_);
  }
  
  private ZipFile getResourcePackZipFile() throws IOException
  {
    if (resourcePackZipFile == null)
    {
      resourcePackZipFile = new ZipFile(resourcePackFile);
    }
    
    return resourcePackZipFile;
  }
  
  protected InputStream getInputStreamByName(String p_110591_1_) throws IOException
  {
    ZipFile var2 = getResourcePackZipFile();
    ZipEntry var3 = var2.getEntry(p_110591_1_);
    
    if (var3 == null)
    {
      throw new ResourcePackFileNotFoundException(resourcePackFile, p_110591_1_);
    }
    

    return var2.getInputStream(var3);
  }
  

  public boolean hasResourceName(String p_110593_1_)
  {
    try
    {
      return getResourcePackZipFile().getEntry(p_110593_1_) != null;
    }
    catch (IOException var3) {}
    
    return false;
  }
  



  public Set getResourceDomains()
  {
    try
    {
      var1 = getResourcePackZipFile();
    }
    catch (IOException var8) {
      ZipFile var1;
      return Collections.emptySet();
    }
    ZipFile var1;
    Enumeration var2 = var1.entries();
    HashSet var3 = Sets.newHashSet();
    
    while (var2.hasMoreElements())
    {
      ZipEntry var4 = (ZipEntry)var2.nextElement();
      String var5 = var4.getName();
      
      if (var5.startsWith("assets/"))
      {
        ArrayList var6 = Lists.newArrayList(entryNameSplitter.split(var5));
        
        if (var6.size() > 1)
        {
          String var7 = (String)var6.get(1);
          
          if (!var7.equals(var7.toLowerCase()))
          {
            logNameNotLowercase(var7);
          }
          else
          {
            var3.add(var7);
          }
        }
      }
    }
    
    return var3;
  }
  
  protected void finalize() throws Throwable
  {
    close();
    super.finalize();
  }
  
  public void close() throws IOException
  {
    if (resourcePackZipFile != null)
    {
      resourcePackZipFile.close();
      resourcePackZipFile = null;
    }
  }
}
