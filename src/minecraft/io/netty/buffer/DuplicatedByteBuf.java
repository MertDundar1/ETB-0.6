package io.netty.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;




















public class DuplicatedByteBuf
  extends AbstractDerivedByteBuf
{
  private final ByteBuf buffer;
  
  public DuplicatedByteBuf(ByteBuf buffer)
  {
    super(buffer.maxCapacity());
    
    if ((buffer instanceof DuplicatedByteBuf)) {
      this.buffer = buffer;
    } else {
      this.buffer = buffer;
    }
    
    setIndex(buffer.readerIndex(), buffer.writerIndex());
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
    return buffer.capacity();
  }
  
  public ByteBuf capacity(int newCapacity)
  {
    buffer.capacity(newCapacity);
    return this;
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
    return buffer.arrayOffset();
  }
  
  public boolean hasMemoryAddress()
  {
    return buffer.hasMemoryAddress();
  }
  
  public long memoryAddress()
  {
    return buffer.memoryAddress();
  }
  
  public byte getByte(int index)
  {
    return _getByte(index);
  }
  
  protected byte _getByte(int index)
  {
    return buffer.getByte(index);
  }
  
  public short getShort(int index)
  {
    return _getShort(index);
  }
  
  protected short _getShort(int index)
  {
    return buffer.getShort(index);
  }
  
  public int getUnsignedMedium(int index)
  {
    return _getUnsignedMedium(index);
  }
  
  protected int _getUnsignedMedium(int index)
  {
    return buffer.getUnsignedMedium(index);
  }
  
  public int getInt(int index)
  {
    return _getInt(index);
  }
  
  protected int _getInt(int index)
  {
    return buffer.getInt(index);
  }
  
  public long getLong(int index)
  {
    return _getLong(index);
  }
  
  protected long _getLong(int index)
  {
    return buffer.getLong(index);
  }
  
  public ByteBuf copy(int index, int length)
  {
    return buffer.copy(index, length);
  }
  
  public ByteBuf slice(int index, int length)
  {
    return buffer.slice(index, length);
  }
  
  public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length)
  {
    buffer.getBytes(index, dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length)
  {
    buffer.getBytes(index, dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuffer dst)
  {
    buffer.getBytes(index, dst);
    return this;
  }
  
  public ByteBuf setByte(int index, int value)
  {
    _setByte(index, value);
    return this;
  }
  
  protected void _setByte(int index, int value)
  {
    buffer.setByte(index, value);
  }
  
  public ByteBuf setShort(int index, int value)
  {
    _setShort(index, value);
    return this;
  }
  
  protected void _setShort(int index, int value)
  {
    buffer.setShort(index, value);
  }
  
  public ByteBuf setMedium(int index, int value)
  {
    _setMedium(index, value);
    return this;
  }
  
  protected void _setMedium(int index, int value)
  {
    buffer.setMedium(index, value);
  }
  
  public ByteBuf setInt(int index, int value)
  {
    _setInt(index, value);
    return this;
  }
  
  protected void _setInt(int index, int value)
  {
    buffer.setInt(index, value);
  }
  
  public ByteBuf setLong(int index, long value)
  {
    _setLong(index, value);
    return this;
  }
  
  protected void _setLong(int index, long value)
  {
    buffer.setLong(index, value);
  }
  
  public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length)
  {
    buffer.setBytes(index, src, srcIndex, length);
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length)
  {
    buffer.setBytes(index, src, srcIndex, length);
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuffer src)
  {
    buffer.setBytes(index, src);
    return this;
  }
  
  public ByteBuf getBytes(int index, OutputStream out, int length)
    throws IOException
  {
    buffer.getBytes(index, out, length);
    return this;
  }
  
  public int getBytes(int index, GatheringByteChannel out, int length)
    throws IOException
  {
    return buffer.getBytes(index, out, length);
  }
  
  public int setBytes(int index, InputStream in, int length)
    throws IOException
  {
    return buffer.setBytes(index, in, length);
  }
  
  public int setBytes(int index, ScatteringByteChannel in, int length)
    throws IOException
  {
    return buffer.setBytes(index, in, length);
  }
  
  public int nioBufferCount()
  {
    return buffer.nioBufferCount();
  }
  
  public ByteBuffer[] nioBuffers(int index, int length)
  {
    return buffer.nioBuffers(index, length);
  }
  
  public ByteBuffer internalNioBuffer(int index, int length)
  {
    return nioBuffer(index, length);
  }
  
  public int forEachByte(int index, int length, ByteBufProcessor processor)
  {
    return buffer.forEachByte(index, length, processor);
  }
  
  public int forEachByteDesc(int index, int length, ByteBufProcessor processor)
  {
    return buffer.forEachByteDesc(index, length, processor);
  }
}
