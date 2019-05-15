package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.Buffer;
import java.nio.ByteBuffer;














final class UnpooledUnsafeNoCleanerDirectByteBuf
  extends UnpooledUnsafeDirectByteBuf
{
  UnpooledUnsafeNoCleanerDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity)
  {
    super(alloc, initialCapacity, maxCapacity);
  }
  
  protected ByteBuffer allocateDirect(int initialCapacity)
  {
    return PlatformDependent.allocateDirectNoCleaner(initialCapacity);
  }
  
  protected void freeDirect(ByteBuffer buffer)
  {
    PlatformDependent.freeDirectNoCleaner(buffer);
  }
  
  public ByteBuf capacity(int newCapacity)
  {
    ensureAccessible();
    if ((newCapacity < 0) || (newCapacity > maxCapacity())) {
      throw new IllegalArgumentException("newCapacity: " + newCapacity);
    }
    
    int readerIndex = readerIndex();
    int writerIndex = writerIndex();
    int oldCapacity = capacity();
    
    if (newCapacity > oldCapacity) {
      ByteBuffer oldBuffer = buffer;
      ByteBuffer newBuffer = PlatformDependent.reallocateDirectNoCleaner(oldBuffer, newCapacity);
      setByteBuffer(newBuffer, false);
    } else if (newCapacity < oldCapacity) {
      ByteBuffer oldBuffer = buffer;
      ByteBuffer newBuffer = allocateDirect(newCapacity);
      if (readerIndex < newCapacity) {
        if (writerIndex > newCapacity) {
          writerIndex = newCapacity;
          writerIndex(writerIndex);
        }
        oldBuffer.position(readerIndex).limit(writerIndex);
        newBuffer.position(readerIndex).limit(writerIndex);
        newBuffer.put(oldBuffer);
        newBuffer.clear();
      } else {
        setIndex(newCapacity, newCapacity);
      }
      setByteBuffer(newBuffer, true);
    }
    return this;
  }
}
