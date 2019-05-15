package io.netty.buffer;



final class PoolSubpage<T>
{
  final PoolChunk<T> chunk;
  

  private final int memoryMapIdx;
  

  private final int runOffset;
  

  private final int pageSize;
  

  private final long[] bitmap;
  

  PoolSubpage<T> prev;
  

  PoolSubpage<T> next;
  
  boolean doNotDestroy;
  
  int elemSize;
  
  private int maxNumElems;
  
  private int bitmapLength;
  
  private int nextAvail;
  
  private int numAvail;
  

  PoolSubpage(int pageSize)
  {
    chunk = null;
    memoryMapIdx = -1;
    runOffset = -1;
    elemSize = -1;
    this.pageSize = pageSize;
    bitmap = null;
  }
  
  PoolSubpage(PoolChunk<T> chunk, int memoryMapIdx, int runOffset, int pageSize, int elemSize) {
    this.chunk = chunk;
    this.memoryMapIdx = memoryMapIdx;
    this.runOffset = runOffset;
    this.pageSize = pageSize;
    bitmap = new long[pageSize >>> 10];
    init(elemSize);
  }
  
  void init(int elemSize) {
    doNotDestroy = true;
    this.elemSize = elemSize;
    if (elemSize != 0) {
      maxNumElems = (this.numAvail = pageSize / elemSize);
      nextAvail = 0;
      bitmapLength = (maxNumElems >>> 6);
      if ((maxNumElems & 0x3F) != 0) {
        bitmapLength += 1;
      }
      
      for (int i = 0; i < bitmapLength; i++) {
        bitmap[i] = 0L;
      }
    }
    
    addToPool();
  }
  


  long allocate()
  {
    if (elemSize == 0) {
      return toHandle(0);
    }
    
    if ((numAvail == 0) || (!doNotDestroy)) {
      return -1L;
    }
    
    int bitmapIdx = getNextAvail();
    int q = bitmapIdx >>> 6;
    int r = bitmapIdx & 0x3F;
    assert ((bitmap[q] >>> r & 1L) == 0L);
    bitmap[q] |= 1L << r;
    
    if (--numAvail == 0) {
      removeFromPool();
    }
    
    return toHandle(bitmapIdx);
  }
  




  boolean free(int bitmapIdx)
  {
    if (elemSize == 0) {
      return true;
    }
    
    int q = bitmapIdx >>> 6;
    int r = bitmapIdx & 0x3F;
    assert ((bitmap[q] >>> r & 1L) != 0L);
    bitmap[q] ^= 1L << r;
    
    setNextAvail(bitmapIdx);
    
    if (numAvail++ == 0) {
      addToPool();
      return true;
    }
    
    if (numAvail != maxNumElems) {
      return true;
    }
    
    if (prev == next)
    {
      return true;
    }
    

    doNotDestroy = false;
    removeFromPool();
    return false;
  }
  
  private void addToPool()
  {
    PoolSubpage<T> head = chunk.arena.findSubpagePoolHead(elemSize);
    assert ((prev == null) && (next == null));
    prev = head;
    next = next;
    next.prev = this;
    next = this;
  }
  
  private void removeFromPool() {
    assert ((prev != null) && (next != null));
    prev.next = next;
    next.prev = prev;
    next = null;
    prev = null;
  }
  
  private void setNextAvail(int bitmapIdx) {
    nextAvail = bitmapIdx;
  }
  
  private int getNextAvail() {
    int nextAvail = this.nextAvail;
    if (nextAvail >= 0) {
      this.nextAvail = -1;
      return nextAvail;
    }
    return findNextAvail();
  }
  
  private int findNextAvail() {
    long[] bitmap = this.bitmap;
    int bitmapLength = this.bitmapLength;
    for (int i = 0; i < bitmapLength; i++) {
      long bits = bitmap[i];
      if ((bits ^ 0xFFFFFFFFFFFFFFFF) != 0L) {
        return findNextAvail0(i, bits);
      }
    }
    return -1;
  }
  
  private int findNextAvail0(int i, long bits) {
    int maxNumElems = this.maxNumElems;
    int baseVal = i << 6;
    
    for (int j = 0; j < 64; j++) {
      if ((bits & 1L) == 0L) {
        int val = baseVal | j;
        if (val >= maxNumElems) break;
        return val;
      }
      


      bits >>>= 1;
    }
    return -1;
  }
  
  private long toHandle(int bitmapIdx) {
    return 0x4000000000000000 | bitmapIdx << 32 | memoryMapIdx;
  }
  
  public String toString() {
    if (!doNotDestroy) {
      return "(" + memoryMapIdx + ": not in use)";
    }
    
    return String.valueOf('(') + memoryMapIdx + ": " + (maxNumElems - numAvail) + '/' + maxNumElems + ", offset: " + runOffset + ", length: " + pageSize + ", elemSize: " + elemSize + ')';
  }
}
