package net.minecraft.client.resources;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager
{
  private static final Logger logger = ;
  private static final Joiner joinerResourcePacks = Joiner.on(", ");
  private final Map domainResourceManagers = Maps.newHashMap();
  private final List reloadListeners = Lists.newArrayList();
  private final Set setResourceDomains = Sets.newLinkedHashSet();
  private final IMetadataSerializer rmMetadataSerializer;
  private static final String __OBFID = "CL_00001091";
  
  public SimpleReloadableResourceManager(IMetadataSerializer p_i1299_1_)
  {
    rmMetadataSerializer = p_i1299_1_;
  }
  

  public void reloadResourcePack(IResourcePack p_110545_1_)
  {
    FallbackResourceManager var4;
    for (Iterator var2 = p_110545_1_.getResourceDomains().iterator(); var2.hasNext(); var4.addResourcePack(p_110545_1_))
    {
      String var3 = (String)var2.next();
      setResourceDomains.add(var3);
      var4 = (FallbackResourceManager)domainResourceManagers.get(var3);
      
      if (var4 == null)
      {
        var4 = new FallbackResourceManager(rmMetadataSerializer);
        domainResourceManagers.put(var3, var4);
      }
    }
  }
  
  public Set getResourceDomains()
  {
    return setResourceDomains;
  }
  
  public IResource getResource(ResourceLocation p_110536_1_) throws IOException
  {
    IResourceManager var2 = (IResourceManager)domainResourceManagers.get(p_110536_1_.getResourceDomain());
    
    if (var2 != null)
    {
      return var2.getResource(p_110536_1_);
    }
    

    throw new FileNotFoundException(p_110536_1_.toString());
  }
  
  public List getAllResources(ResourceLocation p_135056_1_)
    throws IOException
  {
    IResourceManager var2 = (IResourceManager)domainResourceManagers.get(p_135056_1_.getResourceDomain());
    
    if (var2 != null)
    {
      return var2.getAllResources(p_135056_1_);
    }
    

    throw new FileNotFoundException(p_135056_1_.toString());
  }
  

  private void clearResources()
  {
    domainResourceManagers.clear();
    setResourceDomains.clear();
  }
  
  public void reloadResources(List p_110541_1_)
  {
    clearResources();
    logger.info("Reloading ResourceManager: " + joinerResourcePacks.join(Iterables.transform(p_110541_1_, new Function()
    {
      private static final String __OBFID = "CL_00001092";
      
      public String apply(IResourcePack p_apply_1_) {
        return p_apply_1_.getPackName();
      }
      
      public Object apply(Object p_apply_1_) {
        return apply((IResourcePack)p_apply_1_);
      }
    })));
    Iterator var2 = p_110541_1_.iterator();
    
    while (var2.hasNext())
    {
      IResourcePack var3 = (IResourcePack)var2.next();
      reloadResourcePack(var3);
    }
    
    notifyReloadListeners();
  }
  
  public void registerReloadListener(IResourceManagerReloadListener p_110542_1_)
  {
    reloadListeners.add(p_110542_1_);
    p_110542_1_.onResourceManagerReload(this);
  }
  
  private void notifyReloadListeners()
  {
    Iterator var1 = reloadListeners.iterator();
    
    while (var1.hasNext())
    {
      IResourceManagerReloadListener var2 = (IResourceManagerReloadListener)var1.next();
      var2.onResourceManagerReload(this);
    }
  }
}
