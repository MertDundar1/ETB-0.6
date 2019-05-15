package com.ibm.icu.impl;

import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import java.io.PrintStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;










































































public class ICUService
  extends ICUNotifier
{
  protected final String name;
  
  public ICUService()
  {
    name = "";
  }
  
  private static final boolean DEBUG = ICUDebug.enabled("service");
  

  public ICUService(String name)
  {
    this.name = name;
  }
  





  private final ICURWLock factoryLock = new ICURWLock();
  



  private final List<Factory> factories = new ArrayList();
  




  private int defaultSize = 0;
  



  private SoftReference<Map<String, CacheEntry>> cacheref;
  



  private SoftReference<Map<String, Factory>> idref;
  


  private LocaleRef dnref;
  



  public static class Key
  {
    private final String id;
    



    public Key(String id)
    {
      this.id = id;
    }
    


    public final String id()
    {
      return id;
    }
    



    public String canonicalID()
    {
      return id;
    }
    



    public String currentID()
    {
      return canonicalID();
    }
    








    public String currentDescriptor()
    {
      return "/" + currentID();
    }
    






    public boolean fallback()
    {
      return false;
    }
    



    public boolean isFallbackOf(String idToCheck)
    {
      return canonicalID().equals(idToCheck);
    }
  }
  







  public static abstract interface Factory
  {
    public abstract Object create(ICUService.Key paramKey, ICUService paramICUService);
    






    public abstract void updateVisibleIDs(Map<String, Factory> paramMap);
    






    public abstract String getDisplayName(String paramString, ULocale paramULocale);
  }
  






  public static class SimpleFactory
    implements ICUService.Factory
  {
    protected Object instance;
    





    protected String id;
    





    protected boolean visible;
    






    public SimpleFactory(Object instance, String id)
    {
      this(instance, id, true);
    }
    




    public SimpleFactory(Object instance, String id, boolean visible)
    {
      if ((instance == null) || (id == null)) {
        throw new IllegalArgumentException("Instance or id is null");
      }
      this.instance = instance;
      this.id = id;
      this.visible = visible;
    }
    



    public Object create(ICUService.Key key, ICUService service)
    {
      if (id.equals(key.currentID())) {
        return instance;
      }
      return null;
    }
    



    public void updateVisibleIDs(Map<String, ICUService.Factory> result)
    {
      if (visible) {
        result.put(id, this);
      } else {
        result.remove(id);
      }
    }
    




    public String getDisplayName(String identifier, ULocale locale)
    {
      return (visible) && (id.equals(identifier)) ? identifier : null;
    }
    


    public String toString()
    {
      StringBuilder buf = new StringBuilder(super.toString());
      buf.append(", id: ");
      buf.append(id);
      buf.append(", visible: ");
      buf.append(visible);
      return buf.toString();
    }
  }
  



  public Object get(String descriptor)
  {
    return getKey(createKey(descriptor), null);
  }
  



  public Object get(String descriptor, String[] actualReturn)
  {
    if (descriptor == null) {
      throw new NullPointerException("descriptor must not be null");
    }
    return getKey(createKey(descriptor), actualReturn);
  }
  


  public Object getKey(Key key)
  {
    return getKey(key, null);
  }
  














  public Object getKey(Key key, String[] actualReturn)
  {
    return getKey(key, actualReturn, null);
  }
  


  public Object getKey(Key key, String[] actualReturn, Factory factory)
  {
    if (factories.size() == 0) {
      return handleDefault(key, actualReturn);
    }
    
    if (DEBUG) { System.out.println("Service: " + name + " key: " + key.canonicalID());
    }
    CacheEntry result = null;
    if (key != null)
    {
      try
      {

        factoryLock.acquireRead();
        
        Map<String, CacheEntry> cache = null;
        SoftReference<Map<String, CacheEntry>> cref = cacheref;
        if (cref != null) {
          if (DEBUG) System.out.println("Service " + name + " ref exists");
          cache = (Map)cref.get();
        }
        if (cache == null) {
          if (DEBUG) { System.out.println("Service " + name + " cache was empty");
          }
          
          cache = Collections.synchronizedMap(new HashMap());
          
          cref = new SoftReference(cache);
        }
        
        String currentDescriptor = null;
        ArrayList<String> cacheDescriptorList = null;
        boolean putInCache = false;
        
        int NDebug = 0;
        
        int startIndex = 0;
        int limit = factories.size();
        boolean cacheResult = true;
        if (factory != null) {
          for (int i = 0; i < limit; i++) {
            if (factory == factories.get(i)) {
              startIndex = i + 1;
              break;
            }
          }
          if (startIndex == 0) {
            throw new IllegalStateException("Factory " + factory + "not registered with service: " + this);
          }
          cacheResult = false;
        }
        
        do
        {
          currentDescriptor = key.currentDescriptor();
          if (DEBUG) System.out.println(name + "[" + NDebug++ + "] looking for: " + currentDescriptor);
          result = (CacheEntry)cache.get(currentDescriptor);
          if (result != null) {
            if (!DEBUG) break; System.out.println(name + " found with descriptor: " + currentDescriptor); break;
          }
          
          if (DEBUG) { System.out.println("did not find: " + currentDescriptor + " in cache");
          }
          



          putInCache = cacheResult;
          

          int index = startIndex;
          while (index < limit) {
            Factory f = (Factory)factories.get(index++);
            if (DEBUG) System.out.println("trying factory[" + (index - 1) + "] " + f.toString());
            Object service = f.create(key, this);
            if (service != null) {
              result = new CacheEntry(currentDescriptor, service);
              if (!DEBUG) break label704; System.out.println(name + " factory supported: " + currentDescriptor + ", caching");
              break label704;
            }
            if (DEBUG) { System.out.println("factory did not support: " + currentDescriptor);
            }
          }
          





          if (cacheDescriptorList == null) {
            cacheDescriptorList = new ArrayList(5);
          }
          cacheDescriptorList.add(currentDescriptor);
        }
        while (key.fallback());
        label704:
        if (result != null) {
          if (putInCache) {
            if (DEBUG) System.out.println("caching '" + actualDescriptor + "'");
            cache.put(actualDescriptor, result);
            if (cacheDescriptorList != null) {
              for (String desc : cacheDescriptorList) {
                if (DEBUG) { System.out.println(name + " adding descriptor: '" + desc + "' for actual: '" + actualDescriptor + "'");
                }
                cache.put(desc, result);
              }
            }
            



            cacheref = cref;
          }
          
          if (actualReturn != null)
          {
            if (actualDescriptor.indexOf("/") == 0) {
              actualReturn[0] = actualDescriptor.substring(1);
            } else {
              actualReturn[0] = actualDescriptor;
            }
          }
          
          if (DEBUG) { System.out.println("found in service: " + name);
          }
          return service;
        }
      }
      finally {
        factoryLock.releaseRead();
      }
    }
    
    if (DEBUG) { System.out.println("not found in service: " + name);
    }
    return handleDefault(key, actualReturn);
  }
  
  private static final class CacheEntry
  {
    final String actualDescriptor;
    final Object service;
    
    CacheEntry(String actualDescriptor, Object service)
    {
      this.actualDescriptor = actualDescriptor;
      this.service = service;
    }
  }
  




  protected Object handleDefault(Key key, String[] actualIDReturn)
  {
    return null;
  }
  



  public Set<String> getVisibleIDs()
  {
    return getVisibleIDs(null);
  }
  










  public Set<String> getVisibleIDs(String matchID)
  {
    Set<String> result = getVisibleIDMap().keySet();
    
    Key fallbackKey = createKey(matchID);
    
    if (fallbackKey != null) {
      Set<String> temp = new HashSet(result.size());
      for (String id : result) {
        if (fallbackKey.isFallbackOf(id)) {
          temp.add(id);
        }
      }
      result = temp;
    }
    return result;
  }
  


  private Map<String, Factory> getVisibleIDMap()
  {
    Map<String, Factory> idcache = null;
    SoftReference<Map<String, Factory>> ref = idref;
    if (ref != null) {
      idcache = (Map)ref.get();
    }
    while (idcache == null) {
      synchronized (this) {
        if ((ref == idref) || (idref == null))
        {
          try
          {
            factoryLock.acquireRead();
            idcache = new HashMap();
            ListIterator<Factory> lIter = factories.listIterator(factories.size());
            while (lIter.hasPrevious()) {
              Factory f = (Factory)lIter.previous();
              f.updateVisibleIDs(idcache);
            }
            idcache = Collections.unmodifiableMap(idcache);
            idref = new SoftReference(idcache);
          }
          finally {
            factoryLock.releaseRead();
          }
          
        }
        else
        {
          ref = idref;
          idcache = (Map)ref.get();
        }
      }
    }
    
    return idcache;
  }
  




  public String getDisplayName(String id)
  {
    return getDisplayName(id, ULocale.getDefault(ULocale.Category.DISPLAY));
  }
  




  public String getDisplayName(String id, ULocale locale)
  {
    Map<String, Factory> m = getVisibleIDMap();
    Factory f = (Factory)m.get(id);
    if (f != null) {
      return f.getDisplayName(id, locale);
    }
    
    Key key = createKey(id);
    while (key.fallback()) {
      f = (Factory)m.get(key.currentID());
      if (f != null) {
        return f.getDisplayName(id, locale);
      }
    }
    
    return null;
  }
  




  public SortedMap<String, String> getDisplayNames()
  {
    ULocale locale = ULocale.getDefault(ULocale.Category.DISPLAY);
    return getDisplayNames(locale, null, null);
  }
  



  public SortedMap<String, String> getDisplayNames(ULocale locale)
  {
    return getDisplayNames(locale, null, null);
  }
  



  public SortedMap<String, String> getDisplayNames(ULocale locale, Comparator<Object> com)
  {
    return getDisplayNames(locale, com, null);
  }
  



  public SortedMap<String, String> getDisplayNames(ULocale locale, String matchID)
  {
    return getDisplayNames(locale, null, matchID);
  }
  









  public SortedMap<String, String> getDisplayNames(ULocale locale, Comparator<Object> com, String matchID)
  {
    SortedMap<String, String> dncache = null;
    LocaleRef ref = dnref;
    
    if (ref != null) {
      dncache = ref.get(locale, com);
    }
    
    while (dncache == null) {
      synchronized (this) {
        if ((ref == dnref) || (dnref == null)) {
          dncache = new TreeMap(com);
          
          Map<String, Factory> m = getVisibleIDMap();
          Iterator<Map.Entry<String, Factory>> ei = m.entrySet().iterator();
          while (ei.hasNext()) {
            Map.Entry<String, Factory> e = (Map.Entry)ei.next();
            String id = (String)e.getKey();
            Factory f = (Factory)e.getValue();
            dncache.put(f.getDisplayName(id, locale), id);
          }
          
          dncache = Collections.unmodifiableSortedMap(dncache);
          dnref = new LocaleRef(dncache, locale, com);
        } else {
          ref = dnref;
          dncache = ref.get(locale, com);
        }
      }
    }
    
    Key matchKey = createKey(matchID);
    if (matchKey == null) {
      return dncache;
    }
    
    SortedMap<String, String> result = new TreeMap(dncache);
    Iterator<Map.Entry<String, String>> iter = result.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, String> e = (Map.Entry)iter.next();
      if (!matchKey.isFallbackOf((String)e.getValue())) {
        iter.remove();
      }
    }
    return result;
  }
  
  private static class LocaleRef
  {
    private final ULocale locale;
    private SoftReference<SortedMap<String, String>> ref;
    private Comparator<Object> com;
    
    LocaleRef(SortedMap<String, String> dnCache, ULocale locale, Comparator<Object> com)
    {
      this.locale = locale;
      this.com = com;
      ref = new SoftReference(dnCache);
    }
    
    SortedMap<String, String> get(ULocale loc, Comparator<Object> comp)
    {
      SortedMap<String, String> m = (SortedMap)ref.get();
      if ((m != null) && (locale.equals(loc)) && ((com == comp) || ((com != null) && (com.equals(comp)))))
      {


        return m;
      }
      return null;
    }
  }
  




  public final List<Factory> factories()
  {
    try
    {
      factoryLock.acquireRead();
      return new ArrayList(factories);
    }
    finally {
      factoryLock.releaseRead();
    }
  }
  



  public Factory registerObject(Object obj, String id)
  {
    return registerObject(obj, id, true);
  }
  




  public Factory registerObject(Object obj, String id, boolean visible)
  {
    String canonicalID = createKey(id).canonicalID();
    return registerFactory(new SimpleFactory(obj, canonicalID, visible));
  }
  




  public final Factory registerFactory(Factory factory)
  {
    if (factory == null) {
      throw new NullPointerException();
    }
    try {
      factoryLock.acquireWrite();
      factories.add(0, factory);
      clearCaches();
    }
    finally {
      factoryLock.releaseWrite();
    }
    notifyChanged();
    return factory;
  }
  




  public final boolean unregisterFactory(Factory factory)
  {
    if (factory == null) {
      throw new NullPointerException();
    }
    
    boolean result = false;
    try {
      factoryLock.acquireWrite();
      if (factories.remove(factory)) {
        result = true;
        clearCaches();
      }
    }
    finally {
      factoryLock.releaseWrite();
    }
    
    if (result) {
      notifyChanged();
    }
    return result;
  }
  


  public final void reset()
  {
    try
    {
      factoryLock.acquireWrite();
      reInitializeFactories();
      clearCaches();
    }
    finally {
      factoryLock.releaseWrite();
    }
    notifyChanged();
  }
  






  protected void reInitializeFactories()
  {
    factories.clear();
  }
  



  public boolean isDefault()
  {
    return factories.size() == defaultSize;
  }
  



  protected void markDefault()
  {
    defaultSize = factories.size();
  }
  




  public Key createKey(String id)
  {
    return id == null ? null : new Key(id);
  }
  









  protected void clearCaches()
  {
    cacheref = null;
    idref = null;
    dnref = null;
  }
  





  protected void clearServiceCache()
  {
    cacheref = null;
  }
  















  protected boolean acceptsListener(EventListener l)
  {
    return l instanceof ServiceListener;
  }
  



  protected void notifyListener(EventListener l)
  {
    ((ServiceListener)l).serviceChanged(this);
  }
  



  public String stats()
  {
    ICURWLock.Stats stats = factoryLock.resetStats();
    if (stats != null) {
      return stats.toString();
    }
    return "no stats";
  }
  


  public String getName()
  {
    return name;
  }
  


  public String toString()
  {
    return super.toString() + "{" + name + "}";
  }
  
  public static abstract interface ServiceListener
    extends EventListener
  {
    public abstract void serviceChanged(ICUService paramICUService);
  }
}
