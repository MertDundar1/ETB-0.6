package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;






















abstract class PoolArena<T>
{
  static final int numTinySubpagePools = 32;
  final PooledByteBufAllocator parent;
  private final int maxOrder;
  final int pageSize;
  final int pageShifts;
  final int chunkSize;
  final int subpageOverflowMask;
  final int numSmallSubpagePools;
  private final PoolSubpage<T>[] tinySubpagePools;
  private final PoolSubpage<T>[] smallSubpagePools;
  private final PoolChunkList<T> q050;
  private final PoolChunkList<T> q025;
  private final PoolChunkList<T> q000;
  private final PoolChunkList<T> qInit;
  private final PoolChunkList<T> q075;
  private final PoolChunkList<T> q100;
  
  protected PoolArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize)
  {
    this.parent = parent;
    this.pageSize = pageSize;
    this.maxOrder = maxOrder;
    this.pageShifts = pageShifts;
    this.chunkSize = chunkSize;
    subpageOverflowMask = (pageSize - 1 ^ 0xFFFFFFFF);
    tinySubpagePools = newSubpagePoolArray(32);
    for (int i = 0; i < tinySubpagePools.length; i++) {
      tinySubpagePools[i] = newSubpagePoolHead(pageSize);
    }
    
    numSmallSubpagePools = (pageShifts - 9);
    smallSubpagePools = newSubpagePoolArray(numSmallSubpagePools);
    for (int i = 0; i < smallSubpagePools.length; i++) {
      smallSubpagePools[i] = newSubpagePoolHead(pageSize);
    }
    
    q100 = new PoolChunkList(this, null, 100, Integer.MAX_VALUE);
    q075 = new PoolChunkList(this, q100, 75, 100);
    q050 = new PoolChunkList(this, q075, 50, 100);
    q025 = new PoolChunkList(this, q050, 25, 75);
    q000 = new PoolChunkList(this, q025, 1, 50);
    qInit = new PoolChunkList(this, q000, Integer.MIN_VALUE, 25);
    
    q100.prevList = q075;
    q075.prevList = q050;
    q050.prevList = q025;
    q025.prevList = q000;
    q000.prevList = null;
    qInit.prevList = qInit;
  }
  
  private PoolSubpage<T> newSubpagePoolHead(int pageSize) {
    PoolSubpage<T> head = new PoolSubpage(pageSize);
    prev = head;
    next = head;
    return head;
  }
  
  private PoolSubpage<T>[] newSubpagePoolArray(int size)
  {
    return new PoolSubpage[size];
  }
  
  abstract boolean isDirect();
  
  PooledByteBuf<T> allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
    PooledByteBuf<T> buf = newByteBuf(maxCapacity);
    allocate(cache, buf, reqCapacity);
    return buf;
  }
  
  static int tinyIdx(int normCapacity) {
    return normCapacity >>> 4;
  }
  
  static int smallIdx(int normCapacity) {
    int tableIdx = 0;
    int i = normCapacity >>> 10;
    while (i != 0) {
      i >>>= 1;
      tableIdx++;
    }
    return tableIdx;
  }
  
  boolean isTinyOrSmall(int normCapacity)
  {
    return (normCapacity & subpageOverflowMask) == 0;
  }
  
  static boolean isTiny(int normCapacity)
  {
    return (normCapacity & 0xFE00) == 0;
  }
  
  private void allocate(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity) {
    int normCapacity = normalizeCapacity(reqCapacity);
    if (isTinyOrSmall(normCapacity)) { PoolSubpage<T>[] table;
      int tableIdx;
      PoolSubpage<T>[] table;
      if (isTiny(normCapacity)) {
        if (cache.allocateTiny(this, buf, reqCapacity, normCapacity))
        {
          return;
        }
        int tableIdx = tinyIdx(normCapacity);
        table = tinySubpagePools;
      } else {
        if (cache.allocateSmall(this, buf, reqCapacity, normCapacity))
        {
          return;
        }
        tableIdx = smallIdx(normCapacity);
        table = smallSubpagePools;
      }
      
      synchronized (this) {
        PoolSubpage<T> head = table[tableIdx];
        PoolSubpage<T> s = next;
        if (s != head) {
          assert ((doNotDestroy) && (elemSize == normCapacity));
          long handle = s.allocate();
          assert (handle >= 0L);
          chunk.initBufWithSubpage(buf, handle, reqCapacity);
          return;
        }
      }
    } else if (normCapacity <= chunkSize) {
      if (!cache.allocateNormal(this, buf, reqCapacity, normCapacity)) {}

    }
    else
    {

      allocateHuge(buf, reqCapacity);
      return;
    }
    allocateNormal(buf, reqCapacity, normCapacity);
  }
  
  private synchronized void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
    if ((q050.allocate(buf, reqCapacity, normCapacity)) || (q025.allocate(buf, reqCapacity, normCapacity)) || (q000.allocate(buf, reqCapacity, normCapacity)) || (qInit.allocate(buf, reqCapacity, normCapacity)) || (q075.allocate(buf, reqCapacity, normCapacity)) || (q100.allocate(buf, reqCapacity, normCapacity)))
    {

      return;
    }
    

    PoolChunk<T> c = newChunk(pageSize, maxOrder, pageShifts, chunkSize);
    long handle = c.allocate(normCapacity);
    assert (handle > 0L);
    c.initBuf(buf, handle, reqCapacity);
    qInit.add(c);
  }
  
  private void allocateHuge(PooledByteBuf<T> buf, int reqCapacity) {
    buf.initUnpooled(newUnpooledChunk(reqCapacity), reqCapacity);
  }
  
  void free(PoolChunk<T> chunk, long handle, int normCapacity) {
    if (unpooled) {
      destroyChunk(chunk);
    } else {
      PoolThreadCache cache = (PoolThreadCache)parent.threadCache.get();
      if (cache.add(this, chunk, handle, normCapacity))
      {
        return;
      }
      synchronized (this) {
        parent.free(chunk, handle);
      }
    }
  }
  
  PoolSubpage<T> findSubpagePoolHead(int elemSize) { PoolSubpage<T>[] table;
    int tableIdx;
    PoolSubpage<T>[] table;
    if (isTiny(elemSize)) {
      int tableIdx = elemSize >>> 4;
      table = tinySubpagePools;
    } else {
      tableIdx = 0;
      elemSize >>>= 10;
      while (elemSize != 0) {
        elemSize >>>= 1;
        tableIdx++;
      }
      table = smallSubpagePools;
    }
    
    return table[tableIdx];
  }
  
  int normalizeCapacity(int reqCapacity) {
    if (reqCapacity < 0) {
      throw new IllegalArgumentException("capacity: " + reqCapacity + " (expected: 0+)");
    }
    if (reqCapacity >= chunkSize) {
      return reqCapacity;
    }
    
    if (!isTiny(reqCapacity))
    {

      int normalizedCapacity = reqCapacity;
      normalizedCapacity--;
      normalizedCapacity |= normalizedCapacity >>> 1;
      normalizedCapacity |= normalizedCapacity >>> 2;
      normalizedCapacity |= normalizedCapacity >>> 4;
      normalizedCapacity |= normalizedCapacity >>> 8;
      normalizedCapacity |= normalizedCapacity >>> 16;
      normalizedCapacity++;
      
      if (normalizedCapacity < 0) {
        normalizedCapacity >>>= 1;
      }
      
      return normalizedCapacity;
    }
    

    if ((reqCapacity & 0xF) == 0) {
      return reqCapacity;
    }
    
    return (reqCapacity & 0xFFFFFFF0) + 16;
  }
  
  void reallocate(PooledByteBuf<T> buf, int newCapacity, boolean freeOldMemory) {
    if ((newCapacity < 0) || (newCapacity > buf.maxCapacity())) {
      throw new IllegalArgumentException("newCapacity: " + newCapacity);
    }
    
    int oldCapacity = length;
    if (oldCapacity == newCapacity) {
      return;
    }
    
    PoolChunk<T> oldChunk = chunk;
    long oldHandle = handle;
    T oldMemory = memory;
    int oldOffset = offset;
    int oldMaxLength = maxLength;
    int readerIndex = buf.readerIndex();
    int writerIndex = buf.writerIndex();
    
    allocate((PoolThreadCache)parent.threadCache.get(), buf, newCapacity);
    if (newCapacity > oldCapacity) {
      memoryCopy(oldMemory, oldOffset, memory, offset, oldCapacity);

    }
    else if (newCapacity < oldCapacity) {
      if (readerIndex < newCapacity) {
        if (writerIndex > newCapacity) {
          writerIndex = newCapacity;
        }
        memoryCopy(oldMemory, oldOffset + readerIndex, memory, offset + readerIndex, writerIndex - readerIndex);
      }
      else
      {
        readerIndex = writerIndex = newCapacity;
      }
    }
    
    buf.setIndex(readerIndex, writerIndex);
    
    if (freeOldMemory)
      free(oldChunk, oldHandle, oldMaxLength); }
  
  protected abstract PoolChunk<T> newChunk(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  protected abstract PoolChunk<T> newUnpooledChunk(int paramInt);
  
  protected abstract PooledByteBuf<T> newByteBuf(int paramInt);
  
  protected abstract void memoryCopy(T paramT1, int paramInt1, T paramT2, int paramInt2, int paramInt3);
  
  protected abstract void destroyChunk(PoolChunk<T> paramPoolChunk);
  public synchronized String toString() { StringBuilder buf = new StringBuilder();
    buf.append("Chunk(s) at 0~25%:");
    buf.append(StringUtil.NEWLINE);
    buf.append(qInit);
    buf.append(StringUtil.NEWLINE);
    buf.append("Chunk(s) at 0~50%:");
    buf.append(StringUtil.NEWLINE);
    buf.append(q000);
    buf.append(StringUtil.NEWLINE);
    buf.append("Chunk(s) at 25~75%:");
    buf.append(StringUtil.NEWLINE);
    buf.append(q025);
    buf.append(StringUtil.NEWLINE);
    buf.append("Chunk(s) at 50~100%:");
    buf.append(StringUtil.NEWLINE);
    buf.append(q050);
    buf.append(StringUtil.NEWLINE);
    buf.append("Chunk(s) at 75~100%:");
    buf.append(StringUtil.NEWLINE);
    buf.append(q075);
    buf.append(StringUtil.NEWLINE);
    buf.append("Chunk(s) at 100%:");
    buf.append(StringUtil.NEWLINE);
    buf.append(q100);
    buf.append(StringUtil.NEWLINE);
    buf.append("tiny subpages:");
    for (int i = 1; i < tinySubpagePools.length; i++) {
      PoolSubpage<T> head = tinySubpagePools[i];
      if (next != head)
      {


        buf.append(StringUtil.NEWLINE);
        buf.append(i);
        buf.append(": ");
        PoolSubpage<T> s = next;
        for (;;) {
          buf.append(s);
          s = next;
          if (s == head)
            break;
        }
      }
    }
    buf.append(StringUtil.NEWLINE);
    buf.append("small subpages:");
    for (int i = 1; i < smallSubpagePools.length; i++) {
      PoolSubpage<T> head = smallSubpagePools[i];
      if (next != head)
      {


        buf.append(StringUtil.NEWLINE);
        buf.append(i);
        buf.append(": ");
        PoolSubpage<T> s = next;
        for (;;) {
          buf.append(s);
          s = next;
          if (s == head)
            break;
        }
      }
    }
    buf.append(StringUtil.NEWLINE);
    
    return buf.toString();
  }
  
  static final class HeapArena extends PoolArena<byte[]>
  {
    HeapArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
      super(pageSize, maxOrder, pageShifts, chunkSize);
    }
    
    boolean isDirect()
    {
      return false;
    }
    
    protected PoolChunk<byte[]> newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize)
    {
      return new PoolChunk(this, new byte[chunkSize], pageSize, maxOrder, pageShifts, chunkSize);
    }
    
    protected PoolChunk<byte[]> newUnpooledChunk(int capacity)
    {
      return new PoolChunk(this, new byte[capacity], capacity);
    }
    


    protected void destroyChunk(PoolChunk<byte[]> chunk) {}
    

    protected PooledByteBuf<byte[]> newByteBuf(int maxCapacity)
    {
      return PooledHeapByteBuf.newInstance(maxCapacity);
    }
    
    protected void memoryCopy(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length)
    {
      if (length == 0) {
        return;
      }
      
      System.arraycopy(src, srcOffset, dst, dstOffset, length);
    }
  }
  
  static final class DirectArena extends PoolArena<ByteBuffer>
  {
    private static final boolean HAS_UNSAFE = ;
    
    DirectArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
      super(pageSize, maxOrder, pageShifts, chunkSize);
    }
    
    boolean isDirect()
    {
      return true;
    }
    
    protected PoolChunk<ByteBuffer> newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize)
    {
      return new PoolChunk(this, ByteBuffer.allocateDirect(chunkSize), pageSize, maxOrder, pageShifts, chunkSize);
    }
    

    protected PoolChunk<ByteBuffer> newUnpooledChunk(int capacity)
    {
      return new PoolChunk(this, ByteBuffer.allocateDirect(capacity), capacity);
    }
    
    protected void destroyChunk(PoolChunk<ByteBuffer> chunk)
    {
      PlatformDependent.freeDirectBuffer((ByteBuffer)memory);
    }
    
    protected PooledByteBuf<ByteBuffer> newByteBuf(int maxCapacity)
    {
      if (HAS_UNSAFE) {
        return PooledUnsafeDirectByteBuf.newInstance(maxCapacity);
      }
      return PooledDirectByteBuf.newInstance(maxCapacity);
    }
    

    protected void memoryCopy(ByteBuffer src, int srcOffset, ByteBuffer dst, int dstOffset, int length)
    {
      if (length == 0) {
        return;
      }
      
      if (HAS_UNSAFE) {
        PlatformDependent.copyMemory(PlatformDependent.directBufferAddress(src) + srcOffset, PlatformDependent.directBufferAddress(dst) + dstOffset, length);

      }
      else
      {
        src = src.duplicate();
        dst = dst.duplicate();
        src.position(srcOffset).limit(srcOffset + length);
        dst.position(dstOffset);
        dst.put(src);
      }
    }
  }
}
