package io.netty.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;





















public class SlicedByteBuf
  extends AbstractDerivedByteBuf
{
  private final ByteBuf buffer;
  private final int adjustment;
  private final int length;
  
  public SlicedByteBuf(ByteBuf buffer, int index, int length)
  {
    super(length);
    if ((index < 0) || (index > buffer.capacity() - length)) {
      throw new IndexOutOfBoundsException(buffer + ".slice(" + index + ", " + length + ')');
    }
    
    if ((buffer instanceof SlicedByteBuf)) {
      this.buffer = buffer;
      adjustment = (adjustment + index);
    } else if ((buffer instanceof DuplicatedByteBuf)) {
      this.buffer = buffer.unwrap();
      adjustment = index;
    } else {
      this.buffer = buffer;
      adjustment = index;
    }
    this.length = length;
    
    writerIndex(length);
  }
  
  public ByteBuf unwrap()
  {
    return buffer;
  }
  
  public ByteBufAllocator alloc()
  {
    return buffer.alloc();
  }
  
  public ByteOrder order()
  {
    return buffer.order();
  }
  
  public boolean isDirect()
  {
    return buffer.isDirect();
  }
  
  public int capacity()
  {
    return length;
  }
  
  public ByteBuf capacity(int newCapacity)
  {
    throw new UnsupportedOperationException("sliced buffer");
  }
  
  public boolean hasArray()
  {
    return buffer.hasArray();
  }
  
  public byte[] array()
  {
    return buffer.array();
  }
  
  public int arrayOffset()
  {
    return buffer.arrayOffset() + adjustment;
  }
  
  public boolean hasMemoryAddress()
  {
    return buffer.hasMemoryAddress();
  }
  
  public long memoryAddress()
  {
    return buffer.memoryAddress() + adjustment;
  }
  
  protected byte _getByte(int index)
  {
    return buffer.getByte(index + adjustment);
  }
  
  protected short _getShort(int index)
  {
    return buffer.getShort(index + adjustment);
  }
  
  protected int _getUnsignedMedium(int index)
  {
    return buffer.getUnsignedMedium(index + adjustment);
  }
  
  protected int _getInt(int index)
  {
    return buffer.getInt(index + adjustment);
  }
  
  protected long _getLong(int index)
  {
    return buffer.getLong(index + adjustment);
  }
  
  public ByteBuf duplicate()
  {
    ByteBuf duplicate = buffer.slice(adjustment, length);
    duplicate.setIndex(readerIndex(), writerIndex());
    return duplicate;
  }
  
  public ByteBuf copy(int index, int length)
  {
    checkIndex(index, length);
    return buffer.copy(index + adjustment, length);
  }
  
  public ByteBuf slice(int index, int length)
  {
    checkIndex(index, length);
    if (length == 0) {
      return Unpooled.EMPTY_BUFFER;
    }
    return buffer.slice(index + adjustment, length);
  }
  
  public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length)
  {
    checkIndex(index, length);
    buffer.getBytes(index + adjustment, dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length)
  {
    checkIndex(index, length);
    buffer.getBytes(index + adjustment, dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuffer dst)
  {
    checkIndex(index, dst.remaining());
    buffer.getBytes(index + adjustment, dst);
    return this;
  }
  
  protected void _setByte(int index, int value)
  {
    buffer.setByte(index + adjustment, value);
  }
  
  protected void _setShort(int index, int value)
  {
    buffer.setShort(index + adjustment, value);
  }
  
  protected void _setMedium(int index, int value)
  {
    buffer.setMedium(index + adjustment, value);
  }
  
  protected void _setInt(int index, int value)
  {
    buffer.setInt(index + adjustment, value);
  }
  
  protected void _setLong(int index, long value)
  {
    buffer.setLong(index + adjustment, value);
  }
  
  public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length)
  {
    checkIndex(index, length);
    buffer.setBytes(index + adjustment, src, srcIndex, length);
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length)
  {
    checkIndex(index, length);
    buffer.setBytes(index + adjustment, src, srcIndex, length);
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuffer src)
  {
    checkIndex(index, src.remaining());
    buffer.setBytes(index + adjustment, src);
    return this;
  }
  
  public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException
  {
    checkIndex(index, length);
    buffer.getBytes(index + adjustment, out, length);
    return this;
  }
  
  public int getBytes(int index, GatheringByteChannel out, int length) throws IOException
  {
    checkIndex(index, length);
    return buffer.getBytes(index + adjustment, out, length);
  }
  
  public int setBytes(int index, InputStream in, int length) throws IOException
  {
    checkIndex(index, length);
    return buffer.setBytes(index + adjustment, in, length);
  }
  
  public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException
  {
    checkIndex(index, length);
    return buffer.setBytes(index + adjustment, in, length);
  }
  
  public int nioBufferCount()
  {
    return buffer.nioBufferCount();
  }
  
  public ByteBuffer nioBuffer(int index, int length)
  {
    checkIndex(index, length);
    return buffer.nioBuffer(index + adjustment, length);
  }
  
  public ByteBuffer[] nioBuffers(int index, int length)
  {
    checkIndex(index, length);
    return buffer.nioBuffers(index + adjustment, length);
  }
  
  public ByteBuffer internalNioBuffer(int index, int length)
  {
    checkIndex(index, length);
    return nioBuffer(index, length);
  }
  
  public int forEachByte(int index, int length, ByteBufProcessor processor)
  {
    int ret = buffer.forEachByte(index + adjustment, length, processor);
    if (ret >= adjustment) {
      return ret - adjustment;
    }
    return -1;
  }
  

  public int forEachByteDesc(int index, int length, ByteBufProcessor processor)
  {
    int ret = buffer.forEachByteDesc(index + adjustment, length, processor);
    if (ret >= adjustment) {
      return ret - adjustment;
    }
    return -1;
  }
}
