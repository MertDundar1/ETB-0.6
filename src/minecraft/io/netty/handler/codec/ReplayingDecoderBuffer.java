package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.Signal;
import io.netty.util.internal.StringUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;



















final class ReplayingDecoderBuffer
  extends ByteBuf
{
  private static final Signal REPLAY = ReplayingDecoder.REPLAY;
  
  private ByteBuf buffer;
  
  private boolean terminated;
  private SwappedByteBuf swapped;
  static final ReplayingDecoderBuffer EMPTY_BUFFER = new ReplayingDecoderBuffer(Unpooled.EMPTY_BUFFER);
  
  static {
    EMPTY_BUFFER.terminate();
  }
  

  ReplayingDecoderBuffer(ByteBuf buffer)
  {
    setCumulation(buffer);
  }
  
  void setCumulation(ByteBuf buffer) {
    this.buffer = buffer;
  }
  
  void terminate() {
    terminated = true;
  }
  
  public int capacity()
  {
    if (terminated) {
      return buffer.capacity();
    }
    return Integer.MAX_VALUE;
  }
  

  public ByteBuf capacity(int newCapacity)
  {
    reject();
    return this;
  }
  
  public int maxCapacity()
  {
    return capacity();
  }
  
  public ByteBufAllocator alloc()
  {
    return buffer.alloc();
  }
  
  public boolean isDirect()
  {
    return buffer.isDirect();
  }
  
  public boolean hasArray()
  {
    return false;
  }
  
  public byte[] array()
  {
    throw new UnsupportedOperationException();
  }
  
  public int arrayOffset()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean hasMemoryAddress()
  {
    return false;
  }
  
  public long memoryAddress()
  {
    throw new UnsupportedOperationException();
  }
  
  public ByteBuf clear()
  {
    reject();
    return this;
  }
  
  public boolean equals(Object obj)
  {
    return this == obj;
  }
  
  public int compareTo(ByteBuf buffer)
  {
    reject();
    return 0;
  }
  
  public ByteBuf copy()
  {
    reject();
    return this;
  }
  
  public ByteBuf copy(int index, int length)
  {
    checkIndex(index, length);
    return buffer.copy(index, length);
  }
  
  public ByteBuf discardReadBytes()
  {
    reject();
    return this;
  }
  
  public ByteBuf ensureWritable(int writableBytes)
  {
    reject();
    return this;
  }
  
  public int ensureWritable(int minWritableBytes, boolean force)
  {
    reject();
    return 0;
  }
  
  public ByteBuf duplicate()
  {
    reject();
    return this;
  }
  
  public boolean getBoolean(int index)
  {
    checkIndex(index, 1);
    return buffer.getBoolean(index);
  }
  
  public byte getByte(int index)
  {
    checkIndex(index, 1);
    return buffer.getByte(index);
  }
  
  public short getUnsignedByte(int index)
  {
    checkIndex(index, 1);
    return buffer.getUnsignedByte(index);
  }
  
  public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length)
  {
    checkIndex(index, length);
    buffer.getBytes(index, dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, byte[] dst)
  {
    checkIndex(index, dst.length);
    buffer.getBytes(index, dst);
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuffer dst)
  {
    reject();
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length)
  {
    checkIndex(index, length);
    buffer.getBytes(index, dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuf dst, int length)
  {
    reject();
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuf dst)
  {
    reject();
    return this;
  }
  
  public int getBytes(int index, GatheringByteChannel out, int length)
  {
    reject();
    return 0;
  }
  
  public ByteBuf getBytes(int index, OutputStream out, int length)
  {
    reject();
    return this;
  }
  
  public int getInt(int index)
  {
    checkIndex(index, 4);
    return buffer.getInt(index);
  }
  
  public long getUnsignedInt(int index)
  {
    checkIndex(index, 4);
    return buffer.getUnsignedInt(index);
  }
  
  public long getLong(int index)
  {
    checkIndex(index, 8);
    return buffer.getLong(index);
  }
  
  public int getMedium(int index)
  {
    checkIndex(index, 3);
    return buffer.getMedium(index);
  }
  
  public int getUnsignedMedium(int index)
  {
    checkIndex(index, 3);
    return buffer.getUnsignedMedium(index);
  }
  
  public short getShort(int index)
  {
    checkIndex(index, 2);
    return buffer.getShort(index);
  }
  
  public int getUnsignedShort(int index)
  {
    checkIndex(index, 2);
    return buffer.getUnsignedShort(index);
  }
  
  public char getChar(int index)
  {
    checkIndex(index, 2);
    return buffer.getChar(index);
  }
  
  public float getFloat(int index)
  {
    checkIndex(index, 4);
    return buffer.getFloat(index);
  }
  
  public double getDouble(int index)
  {
    checkIndex(index, 8);
    return buffer.getDouble(index);
  }
  
  public int hashCode()
  {
    reject();
    return 0;
  }
  
  public int indexOf(int fromIndex, int toIndex, byte value)
  {
    if (fromIndex == toIndex) {
      return -1;
    }
    
    if (Math.max(fromIndex, toIndex) > buffer.writerIndex()) {
      throw REPLAY;
    }
    
    return buffer.indexOf(fromIndex, toIndex, value);
  }
  
  public int bytesBefore(byte value)
  {
    int bytes = buffer.bytesBefore(value);
    if (bytes < 0) {
      throw REPLAY;
    }
    return bytes;
  }
  
  public int bytesBefore(int length, byte value)
  {
    int readerIndex = buffer.readerIndex();
    return bytesBefore(readerIndex, buffer.writerIndex() - readerIndex, value);
  }
  
  public int bytesBefore(int index, int length, byte value)
  {
    int writerIndex = buffer.writerIndex();
    if (index >= writerIndex) {
      throw REPLAY;
    }
    
    if (index <= writerIndex - length) {
      return buffer.bytesBefore(index, length, value);
    }
    
    int res = buffer.bytesBefore(index, writerIndex - index, value);
    if (res < 0) {
      throw REPLAY;
    }
    return res;
  }
  

  public int forEachByte(ByteBufProcessor processor)
  {
    int ret = buffer.forEachByte(processor);
    if (ret < 0) {
      throw REPLAY;
    }
    return ret;
  }
  

  public int forEachByte(int index, int length, ByteBufProcessor processor)
  {
    int writerIndex = buffer.writerIndex();
    if (index >= writerIndex) {
      throw REPLAY;
    }
    
    if (index <= writerIndex - length) {
      return buffer.forEachByte(index, length, processor);
    }
    
    int ret = buffer.forEachByte(index, writerIndex - index, processor);
    if (ret < 0) {
      throw REPLAY;
    }
    return ret;
  }
  

  public int forEachByteDesc(ByteBufProcessor processor)
  {
    if (terminated) {
      return buffer.forEachByteDesc(processor);
    }
    reject();
    return 0;
  }
  

  public int forEachByteDesc(int index, int length, ByteBufProcessor processor)
  {
    if (index + length > buffer.writerIndex()) {
      throw REPLAY;
    }
    
    return buffer.forEachByteDesc(index, length, processor);
  }
  
  public ByteBuf markReaderIndex()
  {
    buffer.markReaderIndex();
    return this;
  }
  
  public ByteBuf markWriterIndex()
  {
    reject();
    return this;
  }
  
  public ByteOrder order()
  {
    return buffer.order();
  }
  
  public ByteBuf order(ByteOrder endianness)
  {
    if (endianness == null) {
      throw new NullPointerException("endianness");
    }
    if (endianness == order()) {
      return this;
    }
    
    SwappedByteBuf swapped = this.swapped;
    if (swapped == null) {
      this.swapped = (swapped = new SwappedByteBuf(this));
    }
    return swapped;
  }
  
  public boolean isReadable()
  {
    return terminated ? buffer.isReadable() : true;
  }
  
  public boolean isReadable(int size)
  {
    return terminated ? buffer.isReadable(size) : true;
  }
  
  public int readableBytes()
  {
    if (terminated) {
      return buffer.readableBytes();
    }
    return Integer.MAX_VALUE - buffer.readerIndex();
  }
  

  public boolean readBoolean()
  {
    checkReadableBytes(1);
    return buffer.readBoolean();
  }
  
  public byte readByte()
  {
    checkReadableBytes(1);
    return buffer.readByte();
  }
  
  public short readUnsignedByte()
  {
    checkReadableBytes(1);
    return buffer.readUnsignedByte();
  }
  
  public ByteBuf readBytes(byte[] dst, int dstIndex, int length)
  {
    checkReadableBytes(length);
    buffer.readBytes(dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf readBytes(byte[] dst)
  {
    checkReadableBytes(dst.length);
    buffer.readBytes(dst);
    return this;
  }
  
  public ByteBuf readBytes(ByteBuffer dst)
  {
    reject();
    return this;
  }
  
  public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length)
  {
    checkReadableBytes(length);
    buffer.readBytes(dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf readBytes(ByteBuf dst, int length)
  {
    reject();
    return this;
  }
  
  public ByteBuf readBytes(ByteBuf dst)
  {
    checkReadableBytes(dst.writableBytes());
    buffer.readBytes(dst);
    return this;
  }
  
  public int readBytes(GatheringByteChannel out, int length)
  {
    reject();
    return 0;
  }
  
  public ByteBuf readBytes(int length)
  {
    checkReadableBytes(length);
    return buffer.readBytes(length);
  }
  
  public ByteBuf readSlice(int length)
  {
    checkReadableBytes(length);
    return buffer.readSlice(length);
  }
  
  public ByteBuf readBytes(OutputStream out, int length)
  {
    reject();
    return this;
  }
  
  public int readerIndex()
  {
    return buffer.readerIndex();
  }
  
  public ByteBuf readerIndex(int readerIndex)
  {
    buffer.readerIndex(readerIndex);
    return this;
  }
  
  public int readInt()
  {
    checkReadableBytes(4);
    return buffer.readInt();
  }
  
  public long readUnsignedInt()
  {
    checkReadableBytes(4);
    return buffer.readUnsignedInt();
  }
  
  public long readLong()
  {
    checkReadableBytes(8);
    return buffer.readLong();
  }
  
  public int readMedium()
  {
    checkReadableBytes(3);
    return buffer.readMedium();
  }
  
  public int readUnsignedMedium()
  {
    checkReadableBytes(3);
    return buffer.readUnsignedMedium();
  }
  
  public short readShort()
  {
    checkReadableBytes(2);
    return buffer.readShort();
  }
  
  public int readUnsignedShort()
  {
    checkReadableBytes(2);
    return buffer.readUnsignedShort();
  }
  
  public char readChar()
  {
    checkReadableBytes(2);
    return buffer.readChar();
  }
  
  public float readFloat()
  {
    checkReadableBytes(4);
    return buffer.readFloat();
  }
  
  public double readDouble()
  {
    checkReadableBytes(8);
    return buffer.readDouble();
  }
  
  public ByteBuf resetReaderIndex()
  {
    buffer.resetReaderIndex();
    return this;
  }
  
  public ByteBuf resetWriterIndex()
  {
    reject();
    return this;
  }
  
  public ByteBuf setBoolean(int index, boolean value)
  {
    reject();
    return this;
  }
  
  public ByteBuf setByte(int index, int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length)
  {
    reject();
    return this;
  }
  
  public ByteBuf setBytes(int index, byte[] src)
  {
    reject();
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuffer src)
  {
    reject();
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length)
  {
    reject();
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuf src, int length)
  {
    reject();
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuf src)
  {
    reject();
    return this;
  }
  
  public int setBytes(int index, InputStream in, int length)
  {
    reject();
    return 0;
  }
  
  public ByteBuf setZero(int index, int length)
  {
    reject();
    return this;
  }
  
  public int setBytes(int index, ScatteringByteChannel in, int length)
  {
    reject();
    return 0;
  }
  
  public ByteBuf setIndex(int readerIndex, int writerIndex)
  {
    reject();
    return this;
  }
  
  public ByteBuf setInt(int index, int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf setLong(int index, long value)
  {
    reject();
    return this;
  }
  
  public ByteBuf setMedium(int index, int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf setShort(int index, int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf setChar(int index, int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf setFloat(int index, float value)
  {
    reject();
    return this;
  }
  
  public ByteBuf setDouble(int index, double value)
  {
    reject();
    return this;
  }
  
  public ByteBuf skipBytes(int length)
  {
    checkReadableBytes(length);
    buffer.skipBytes(length);
    return this;
  }
  
  public ByteBuf slice()
  {
    reject();
    return this;
  }
  
  public ByteBuf slice(int index, int length)
  {
    checkIndex(index, length);
    return buffer.slice(index, length);
  }
  
  public int nioBufferCount()
  {
    return buffer.nioBufferCount();
  }
  
  public ByteBuffer nioBuffer()
  {
    reject();
    return null;
  }
  
  public ByteBuffer nioBuffer(int index, int length)
  {
    checkIndex(index, length);
    return buffer.nioBuffer(index, length);
  }
  
  public ByteBuffer[] nioBuffers()
  {
    reject();
    return null;
  }
  
  public ByteBuffer[] nioBuffers(int index, int length)
  {
    checkIndex(index, length);
    return buffer.nioBuffers(index, length);
  }
  
  public ByteBuffer internalNioBuffer(int index, int length)
  {
    checkIndex(index, length);
    return buffer.internalNioBuffer(index, length);
  }
  
  public String toString(int index, int length, Charset charset)
  {
    checkIndex(index, length);
    return buffer.toString(index, length, charset);
  }
  
  public String toString(Charset charsetName)
  {
    reject();
    return null;
  }
  
  public String toString()
  {
    return StringUtil.simpleClassName(this) + '(' + "ridx=" + readerIndex() + ", " + "widx=" + writerIndex() + ')';
  }
  






  public boolean isWritable()
  {
    return false;
  }
  
  public boolean isWritable(int size)
  {
    return false;
  }
  
  public int writableBytes()
  {
    return 0;
  }
  
  public int maxWritableBytes()
  {
    return 0;
  }
  
  public ByteBuf writeBoolean(boolean value)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeByte(int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeBytes(byte[] src, int srcIndex, int length)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeBytes(byte[] src)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeBytes(ByteBuffer src)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeBytes(ByteBuf src, int length)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeBytes(ByteBuf src)
  {
    reject();
    return this;
  }
  
  public int writeBytes(InputStream in, int length)
  {
    reject();
    return 0;
  }
  
  public int writeBytes(ScatteringByteChannel in, int length)
  {
    reject();
    return 0;
  }
  
  public ByteBuf writeInt(int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeLong(long value)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeMedium(int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeZero(int length)
  {
    reject();
    return this;
  }
  
  public int writerIndex()
  {
    return buffer.writerIndex();
  }
  
  public ByteBuf writerIndex(int writerIndex)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeShort(int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeChar(int value)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeFloat(float value)
  {
    reject();
    return this;
  }
  
  public ByteBuf writeDouble(double value)
  {
    reject();
    return this;
  }
  
  private void checkIndex(int index, int length) {
    if (index + length > buffer.writerIndex()) {
      throw REPLAY;
    }
  }
  
  private void checkReadableBytes(int readableBytes) {
    if (buffer.readableBytes() < readableBytes) {
      throw REPLAY;
    }
  }
  
  public ByteBuf discardSomeReadBytes()
  {
    reject();
    return this;
  }
  
  public int refCnt()
  {
    return buffer.refCnt();
  }
  
  public ByteBuf retain()
  {
    reject();
    return this;
  }
  
  public ByteBuf retain(int increment)
  {
    reject();
    return this;
  }
  
  public boolean release()
  {
    reject();
    return false;
  }
  
  public boolean release(int decrement)
  {
    reject();
    return false;
  }
  
  public ByteBuf unwrap()
  {
    reject();
    return this;
  }
  
  private static void reject() {
    throw new UnsupportedOperationException("not a replayable operation");
  }
  
  ReplayingDecoderBuffer() {}
}
