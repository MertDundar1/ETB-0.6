package io.netty.buffer;

import io.netty.util.ThreadDeathWatcher;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;

























final class PoolThreadCache
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
  
  final PoolArena<byte[]> heapArena;
  
  final PoolArena<ByteBuffer> directArena;
  
  private final MemoryRegionCache<byte[]>[] tinySubPageHeapCaches;
  
  private final MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
  
  private final MemoryRegionCache<ByteBuffer>[] tinySubPageDirectCaches;
  
  private final MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
  
  private final MemoryRegionCache<byte[]>[] normalHeapCaches;
  private final MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
  private final int numShiftsNormalDirect;
  private final int numShiftsNormalHeap;
  private final int freeSweepAllocationThreshold;
  private int allocations;
  private final Thread thread = Thread.currentThread();
  private final Runnable freeTask = new Runnable()
  {
    public void run() {
      PoolThreadCache.this.free0();
    }
  };
  




  PoolThreadCache(PoolArena<byte[]> heapArena, PoolArena<ByteBuffer> directArena, int tinyCacheSize, int smallCacheSize, int normalCacheSize, int maxCachedBufferCapacity, int freeSweepAllocationThreshold)
  {
    if (maxCachedBufferCapacity < 0) {
      throw new IllegalArgumentException("maxCachedBufferCapacity: " + maxCachedBufferCapacity + " (expected: >= 0)");
    }
    
    if (freeSweepAllocationThreshold < 1) {
      throw new IllegalArgumentException("freeSweepAllocationThreshold: " + maxCachedBufferCapacity + " (expected: > 0)");
    }
    
    this.freeSweepAllocationThreshold = freeSweepAllocationThreshold;
    this.heapArena = heapArena;
    this.directArena = directArena;
    if (directArena != null) {
      tinySubPageDirectCaches = createSubPageCaches(tinyCacheSize, 32);
      smallSubPageDirectCaches = createSubPageCaches(smallCacheSize, numSmallSubpagePools);
      
      numShiftsNormalDirect = log2(pageSize);
      normalDirectCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, directArena);
    }
    else
    {
      tinySubPageDirectCaches = null;
      smallSubPageDirectCaches = null;
      normalDirectCaches = null;
      numShiftsNormalDirect = -1;
    }
    if (heapArena != null)
    {
      tinySubPageHeapCaches = createSubPageCaches(tinyCacheSize, 32);
      smallSubPageHeapCaches = createSubPageCaches(smallCacheSize, numSmallSubpagePools);
      
      numShiftsNormalHeap = log2(pageSize);
      normalHeapCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, heapArena);
    }
    else
    {
      tinySubPageHeapCaches = null;
      smallSubPageHeapCaches = null;
      normalHeapCaches = null;
      numShiftsNormalHeap = -1;
    }
    


    ThreadDeathWatcher.watch(thread, freeTask);
  }
  
  private static <T> SubPageMemoryRegionCache<T>[] createSubPageCaches(int cacheSize, int numCaches) {
    if (cacheSize > 0)
    {
      SubPageMemoryRegionCache<T>[] cache = new SubPageMemoryRegionCache[numCaches];
      for (int i = 0; i < cache.length; i++)
      {
        cache[i] = new SubPageMemoryRegionCache(cacheSize);
      }
      return cache;
    }
    return null;
  }
  

  private static <T> NormalMemoryRegionCache<T>[] createNormalCaches(int cacheSize, int maxCachedBufferCapacity, PoolArena<T> area)
  {
    if (cacheSize > 0) {
      int max = Math.min(chunkSize, maxCachedBufferCapacity);
      int arraySize = Math.max(1, max / pageSize);
      

      NormalMemoryRegionCache<T>[] cache = new NormalMemoryRegionCache[arraySize];
      for (int i = 0; i < cache.length; i++) {
        cache[i] = new NormalMemoryRegionCache(cacheSize);
      }
      return cache;
    }
    return null;
  }
  
  private static int log2(int val)
  {
    int res = 0;
    while (val > 1) {
      val >>= 1;
      res++;
    }
    return res;
  }
  


  boolean allocateTiny(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity)
  {
    return allocate(cacheForTiny(area, normCapacity), buf, reqCapacity);
  }
  


  boolean allocateSmall(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity)
  {
    return allocate(cacheForSmall(area, normCapacity), buf, reqCapacity);
  }
  


  boolean allocateNormal(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity)
  {
    return allocate(cacheForNormal(area, normCapacity), buf, reqCapacity);
  }
  
  private boolean allocate(MemoryRegionCache<?> cache, PooledByteBuf buf, int reqCapacity)
  {
    if (cache == null)
    {
      return false;
    }
    boolean allocated = cache.allocate(buf, reqCapacity);
    if (++allocations >= freeSweepAllocationThreshold) {
      allocations = 0;
      trim();
    }
    return allocated;
  }
  

  boolean add(PoolArena<?> area, PoolChunk chunk, long handle, int normCapacity)
  {
    MemoryRegionCache<?> cache;
    
    MemoryRegionCache<?> cache;
    
    if (area.isTinyOrSmall(normCapacity)) { MemoryRegionCache<?> cache;
      if (PoolArena.isTiny(normCapacity)) {
        cache = cacheForTiny(area, normCapacity);
      } else {
        cache = cacheForSmall(area, normCapacity);
      }
    } else {
      cache = cacheForNormal(area, normCapacity);
    }
    if (cache == null) {
      return false;
    }
    return cache.add(chunk, handle);
  }
  


  void free()
  {
    ThreadDeathWatcher.unwatch(thread, freeTask);
    free0();
  }
  
  private void free0() {
    int numFreed = free(tinySubPageDirectCaches) + free(smallSubPageDirectCaches) + free(normalDirectCaches) + free(tinySubPageHeapCaches) + free(smallSubPageHeapCaches) + free(normalHeapCaches);
    





    if ((numFreed > 0) && (logger.isDebugEnabled())) {
      logger.debug("Freed {} thread-local buffer(s) from thread: {}", Integer.valueOf(numFreed), thread.getName());
    }
  }
  
  private static int free(MemoryRegionCache<?>[] caches) {
    if (caches == null) {
      return 0;
    }
    
    int numFreed = 0;
    for (MemoryRegionCache<?> c : caches) {
      numFreed += free(c);
    }
    return numFreed;
  }
  
  private static int free(MemoryRegionCache<?> cache) {
    if (cache == null) {
      return 0;
    }
    return cache.free();
  }
  
  void trim() {
    trim(tinySubPageDirectCaches);
    trim(smallSubPageDirectCaches);
    trim(normalDirectCaches);
    trim(tinySubPageHeapCaches);
    trim(smallSubPageHeapCaches);
    trim(normalHeapCaches);
  }
  
  private static void trim(MemoryRegionCache<?>[] caches) {
    if (caches == null) {
      return;
    }
    for (MemoryRegionCache<?> c : caches) {
      trim(c);
    }
  }
  
  private static void trim(MemoryRegionCache<?> cache) {
    if (cache == null) {
      return;
    }
    cache.trim();
  }
  
  private MemoryRegionCache<?> cacheForTiny(PoolArena<?> area, int normCapacity) {
    int idx = PoolArena.tinyIdx(normCapacity);
    if (area.isDirect()) {
      return cache(tinySubPageDirectCaches, idx);
    }
    return cache(tinySubPageHeapCaches, idx);
  }
  
  private MemoryRegionCache<?> cacheForSmall(PoolArena<?> area, int normCapacity) {
    int idx = PoolArena.smallIdx(normCapacity);
    if (area.isDirect()) {
      return cache(smallSubPageDirectCaches, idx);
    }
    return cache(smallSubPageHeapCaches, idx);
  }
  
  private MemoryRegionCache<?> cacheForNormal(PoolArena<?> area, int normCapacity) {
    if (area.isDirect()) {
      int idx = log2(normCapacity >> numShiftsNormalDirect);
      return cache(normalDirectCaches, idx);
    }
    int idx = log2(normCapacity >> numShiftsNormalHeap);
    return cache(normalHeapCaches, idx);
  }
  
  private static <T> MemoryRegionCache<T> cache(MemoryRegionCache<T>[] cache, int idx) {
    if ((cache == null) || (idx > cache.length - 1)) {
      return null;
    }
    return cache[idx];
  }
  
  private static final class SubPageMemoryRegionCache<T>
    extends PoolThreadCache.MemoryRegionCache<T>
  {
    SubPageMemoryRegionCache(int size)
    {
      super();
    }
    

    protected void initBuf(PoolChunk<T> chunk, long handle, PooledByteBuf<T> buf, int reqCapacity)
    {
      chunk.initBufWithSubpage(buf, handle, reqCapacity);
    }
  }
  
  private static final class NormalMemoryRegionCache<T>
    extends PoolThreadCache.MemoryRegionCache<T>
  {
    NormalMemoryRegionCache(int size)
    {
      super();
    }
    

    protected void initBuf(PoolChunk<T> chunk, long handle, PooledByteBuf<T> buf, int reqCapacity)
    {
      chunk.initBuf(buf, handle, reqCapacity);
    }
  }
  

  private static abstract class MemoryRegionCache<T>
  {
    private final Entry<T>[] entries;
    
    private final int maxUnusedCached;
    private int head;
    private int tail;
    private int maxEntriesInUse;
    private int entriesInUse;
    
    MemoryRegionCache(int size)
    {
      entries = new Entry[powerOfTwo(size)];
      for (int i = 0; i < entries.length; i++) {
        entries[i] = new Entry(null);
      }
      maxUnusedCached = (size / 2);
    }
    
    private static int powerOfTwo(int res) {
      if (res <= 2) {
        return 2;
      }
      res--;
      res |= res >> 1;
      res |= res >> 2;
      res |= res >> 4;
      res |= res >> 8;
      res |= res >> 16;
      res++;
      return res;
    }
    



    protected abstract void initBuf(PoolChunk<T> paramPoolChunk, long paramLong, PooledByteBuf<T> paramPooledByteBuf, int paramInt);
    



    public boolean add(PoolChunk<T> chunk, long handle)
    {
      Entry<T> entry = entries[tail];
      if (chunk != null)
      {
        return false;
      }
      entriesInUse -= 1;
      
      chunk = chunk;
      handle = handle;
      tail = nextIdx(tail);
      return true;
    }
    


    public boolean allocate(PooledByteBuf<T> buf, int reqCapacity)
    {
      Entry<T> entry = entries[head];
      if (chunk == null) {
        return false;
      }
      
      entriesInUse += 1;
      if (maxEntriesInUse < entriesInUse) {
        maxEntriesInUse = entriesInUse;
      }
      initBuf(chunk, handle, buf, reqCapacity);
      
      chunk = null;
      head = nextIdx(head);
      return true;
    }
    


    public int free()
    {
      int numFreed = 0;
      entriesInUse = 0;
      maxEntriesInUse = 0;
      for (int i = head;; i = nextIdx(i)) {
        if (freeEntry(entries[i])) {
          numFreed++;
        }
        else {
          return numFreed;
        }
      }
    }
    


    private void trim()
    {
      int free = size() - maxEntriesInUse;
      entriesInUse = 0;
      maxEntriesInUse = 0;
      
      if (free <= maxUnusedCached) {
        return;
      }
      
      int i = head;
      for (; free > 0; free--) {
        if (!freeEntry(entries[i]))
        {
          return;
        }
        i = nextIdx(i);
      }
    }
    
    private static boolean freeEntry(Entry entry)
    {
      PoolChunk chunk = entry.chunk;
      if (chunk == null) {
        return false;
      }
      
      synchronized (arena) {
        parent.free(chunk, handle);
      }
      entry.chunk = null;
      return true;
    }
    


    private int size()
    {
      return tail - head & entries.length - 1;
    }
    
    private int nextIdx(int index)
    {
      return index + 1 & entries.length - 1;
    }
    
    private static final class Entry<T>
    {
      PoolChunk<T> chunk;
      long handle;
      
      private Entry() {}
    }
  }
}
