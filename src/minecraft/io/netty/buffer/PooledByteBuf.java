package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

















abstract class PooledByteBuf<T>
  extends AbstractReferenceCountedByteBuf
{
  private final Recycler.Handle recyclerHandle;
  protected PoolChunk<T> chunk;
  protected long handle;
  protected T memory;
  protected int offset;
  protected int length;
  int maxLength;
  private ByteBuffer tmpNioBuf;
  
  protected PooledByteBuf(Recycler.Handle recyclerHandle, int maxCapacity)
  {
    super(maxCapacity);
    this.recyclerHandle = recyclerHandle;
  }
  
  void init(PoolChunk<T> chunk, long handle, int offset, int length, int maxLength) {
    assert (handle >= 0L);
    assert (chunk != null);
    
    this.chunk = chunk;
    this.handle = handle;
    memory = memory;
    this.offset = offset;
    this.length = length;
    this.maxLength = maxLength;
    setIndex(0, 0);
    tmpNioBuf = null;
  }
  
  void initUnpooled(PoolChunk<T> chunk, int length) {
    assert (chunk != null);
    
    this.chunk = chunk;
    handle = 0L;
    memory = memory;
    offset = 0;
    this.length = (this.maxLength = length);
    setIndex(0, 0);
    tmpNioBuf = null;
  }
  
  public final int capacity()
  {
    return length;
  }
  
  public final ByteBuf capacity(int newCapacity)
  {
    ensureAccessible();
    

    if (chunk.unpooled) {
      if (newCapacity == length) {
        return this;
      }
    }
    else if (newCapacity > length) {
      if (newCapacity <= maxLength) {
        length = newCapacity;
        return this;
      }
    } else if (newCapacity < length) {
      if (newCapacity > maxLength >>> 1) {
        if (maxLength <= 512) {
          if (newCapacity > maxLength - 16) {
            length = newCapacity;
            setIndex(Math.min(readerIndex(), newCapacity), Math.min(writerIndex(), newCapacity));
            return this;
          }
        } else {
          length = newCapacity;
          setIndex(Math.min(readerIndex(), newCapacity), Math.min(writerIndex(), newCapacity));
          return this;
        }
      }
    } else {
      return this;
    }
    


    chunk.arena.reallocate(this, newCapacity, true);
    return this;
  }
  
  public final ByteBufAllocator alloc()
  {
    return chunk.arena.parent;
  }
  
  public final ByteOrder order()
  {
    return ByteOrder.BIG_ENDIAN;
  }
  
  public final ByteBuf unwrap()
  {
    return null;
  }
  
  protected final ByteBuffer internalNioBuffer() {
    ByteBuffer tmpNioBuf = this.tmpNioBuf;
    if (tmpNioBuf == null) {
      this.tmpNioBuf = (tmpNioBuf = newInternalNioBuffer(memory));
    }
    return tmpNioBuf;
  }
  
  protected abstract ByteBuffer newInternalNioBuffer(T paramT);
  
  protected final void deallocate()
  {
    if (this.handle >= 0L) {
      long handle = this.handle;
      this.handle = -1L;
      memory = null;
      chunk.arena.free(chunk, handle, maxLength);
      recycle();
    }
  }
  
  private void recycle() {
    Recycler.Handle recyclerHandle = this.recyclerHandle;
    if (recyclerHandle != null) {
      recycler().recycle(this, recyclerHandle);
    }
  }
  
  protected abstract Recycler<?> recycler();
  
  protected final int idx(int index) {
    return offset + index;
  }
}
