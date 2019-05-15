package io.netty.buffer;

import io.netty.util.ResourceLeak;
import java.nio.ByteOrder;
















final class SimpleLeakAwareCompositeByteBuf
  extends WrappedCompositeByteBuf
{
  private final ResourceLeak leak;
  
  SimpleLeakAwareCompositeByteBuf(CompositeByteBuf wrapped, ResourceLeak leak)
  {
    super(wrapped);
    this.leak = leak;
  }
  
  public boolean release()
  {
    boolean deallocated = super.release();
    if (deallocated) {
      leak.close();
    }
    return deallocated;
  }
  
  public boolean release(int decrement)
  {
    boolean deallocated = super.release(decrement);
    if (deallocated) {
      leak.close();
    }
    return deallocated;
  }
  
  public ByteBuf order(ByteOrder endianness)
  {
    leak.record();
    if (order() == endianness) {
      return this;
    }
    return new SimpleLeakAwareByteBuf(super.order(endianness), leak);
  }
  

  public ByteBuf slice()
  {
    return new SimpleLeakAwareByteBuf(super.slice(), leak);
  }
  
  public ByteBuf retainedSlice()
  {
    return new SimpleLeakAwareByteBuf(super.retainedSlice(), leak);
  }
  
  public ByteBuf slice(int index, int length)
  {
    return new SimpleLeakAwareByteBuf(super.slice(index, length), leak);
  }
  
  public ByteBuf retainedSlice(int index, int length)
  {
    return new SimpleLeakAwareByteBuf(super.retainedSlice(index, length), leak);
  }
  
  public ByteBuf duplicate()
  {
    return new SimpleLeakAwareByteBuf(super.duplicate(), leak);
  }
  
  public ByteBuf retainedDuplicate()
  {
    return new SimpleLeakAwareByteBuf(super.retainedDuplicate(), leak);
  }
  
  public ByteBuf readSlice(int length)
  {
    return new SimpleLeakAwareByteBuf(super.readSlice(length), leak);
  }
  
  public ByteBuf readRetainedSlice(int length)
  {
    return new SimpleLeakAwareByteBuf(super.readRetainedSlice(length), leak);
  }
  
  public ByteBuf asReadOnly()
  {
    return new SimpleLeakAwareByteBuf(super.asReadOnly(), leak);
  }
}
