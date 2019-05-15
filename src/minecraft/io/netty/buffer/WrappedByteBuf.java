package io.netty.buffer;

import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
















class WrappedByteBuf
  extends ByteBuf
{
  protected final ByteBuf buf;
  
  protected WrappedByteBuf(ByteBuf buf)
  {
    if (buf == null) {
      throw new NullPointerException("buf");
    }
    this.buf = buf;
  }
  
  public boolean hasMemoryAddress()
  {
    return buf.hasMemoryAddress();
  }
  
  public long memoryAddress()
  {
    return buf.memoryAddress();
  }
  
  public int capacity()
  {
    return buf.capacity();
  }
  
  public ByteBuf capacity(int newCapacity)
  {
    buf.capacity(newCapacity);
    return this;
  }
  
  public int maxCapacity()
  {
    return buf.maxCapacity();
  }
  
  public ByteBufAllocator alloc()
  {
    return buf.alloc();
  }
  
  public ByteOrder order()
  {
    return buf.order();
  }
  
  public ByteBuf order(ByteOrder endianness)
  {
    return buf.order(endianness);
  }
  
  public ByteBuf unwrap()
  {
    return buf;
  }
  
  public boolean isDirect()
  {
    return buf.isDirect();
  }
  
  public int readerIndex()
  {
    return buf.readerIndex();
  }
  
  public ByteBuf readerIndex(int readerIndex)
  {
    buf.readerIndex(readerIndex);
    return this;
  }
  
  public int writerIndex()
  {
    return buf.writerIndex();
  }
  
  public ByteBuf writerIndex(int writerIndex)
  {
    buf.writerIndex(writerIndex);
    return this;
  }
  
  public ByteBuf setIndex(int readerIndex, int writerIndex)
  {
    buf.setIndex(readerIndex, writerIndex);
    return this;
  }
  
  public int readableBytes()
  {
    return buf.readableBytes();
  }
  
  public int writableBytes()
  {
    return buf.writableBytes();
  }
  
  public int maxWritableBytes()
  {
    return buf.maxWritableBytes();
  }
  
  public boolean isReadable()
  {
    return buf.isReadable();
  }
  
  public boolean isWritable()
  {
    return buf.isWritable();
  }
  
  public ByteBuf clear()
  {
    buf.clear();
    return this;
  }
  
  public ByteBuf markReaderIndex()
  {
    buf.markReaderIndex();
    return this;
  }
  
  public ByteBuf resetReaderIndex()
  {
    buf.resetReaderIndex();
    return this;
  }
  
  public ByteBuf markWriterIndex()
  {
    buf.markWriterIndex();
    return this;
  }
  
  public ByteBuf resetWriterIndex()
  {
    buf.resetWriterIndex();
    return this;
  }
  
  public ByteBuf discardReadBytes()
  {
    buf.discardReadBytes();
    return this;
  }
  
  public ByteBuf discardSomeReadBytes()
  {
    buf.discardSomeReadBytes();
    return this;
  }
  
  public ByteBuf ensureWritable(int minWritableBytes)
  {
    buf.ensureWritable(minWritableBytes);
    return this;
  }
  
  public int ensureWritable(int minWritableBytes, boolean force)
  {
    return buf.ensureWritable(minWritableBytes, force);
  }
  
  public boolean getBoolean(int index)
  {
    return buf.getBoolean(index);
  }
  
  public byte getByte(int index)
  {
    return buf.getByte(index);
  }
  
  public short getUnsignedByte(int index)
  {
    return buf.getUnsignedByte(index);
  }
  
  public short getShort(int index)
  {
    return buf.getShort(index);
  }
  
  public int getUnsignedShort(int index)
  {
    return buf.getUnsignedShort(index);
  }
  
  public int getMedium(int index)
  {
    return buf.getMedium(index);
  }
  
  public int getUnsignedMedium(int index)
  {
    return buf.getUnsignedMedium(index);
  }
  
  public int getInt(int index)
  {
    return buf.getInt(index);
  }
  
  public long getUnsignedInt(int index)
  {
    return buf.getUnsignedInt(index);
  }
  
  public long getLong(int index)
  {
    return buf.getLong(index);
  }
  
  public char getChar(int index)
  {
    return buf.getChar(index);
  }
  
  public float getFloat(int index)
  {
    return buf.getFloat(index);
  }
  
  public double getDouble(int index)
  {
    return buf.getDouble(index);
  }
  
  public ByteBuf getBytes(int index, ByteBuf dst)
  {
    buf.getBytes(index, dst);
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuf dst, int length)
  {
    buf.getBytes(index, dst, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length)
  {
    buf.getBytes(index, dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, byte[] dst)
  {
    buf.getBytes(index, dst);
    return this;
  }
  
  public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length)
  {
    buf.getBytes(index, dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuffer dst)
  {
    buf.getBytes(index, dst);
    return this;
  }
  
  public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException
  {
    buf.getBytes(index, out, length);
    return this;
  }
  
  public int getBytes(int index, GatheringByteChannel out, int length) throws IOException
  {
    return buf.getBytes(index, out, length);
  }
  
  public ByteBuf setBoolean(int index, boolean value)
  {
    buf.setBoolean(index, value);
    return this;
  }
  
  public ByteBuf setByte(int index, int value)
  {
    buf.setByte(index, value);
    return this;
  }
  
  public ByteBuf setShort(int index, int value)
  {
    buf.setShort(index, value);
    return this;
  }
  
  public ByteBuf setMedium(int index, int value)
  {
    buf.setMedium(index, value);
    return this;
  }
  
  public ByteBuf setInt(int index, int value)
  {
    buf.setInt(index, value);
    return this;
  }
  
  public ByteBuf setLong(int index, long value)
  {
    buf.setLong(index, value);
    return this;
  }
  
  public ByteBuf setChar(int index, int value)
  {
    buf.setChar(index, value);
    return this;
  }
  
  public ByteBuf setFloat(int index, float value)
  {
    buf.setFloat(index, value);
    return this;
  }
  
  public ByteBuf setDouble(int index, double value)
  {
    buf.setDouble(index, value);
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuf src)
  {
    buf.setBytes(index, src);
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuf src, int length)
  {
    buf.setBytes(index, src, length);
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length)
  {
    buf.setBytes(index, src, srcIndex, length);
    return this;
  }
  
  public ByteBuf setBytes(int index, byte[] src)
  {
    buf.setBytes(index, src);
    return this;
  }
  
  public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length)
  {
    buf.setBytes(index, src, srcIndex, length);
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuffer src)
  {
    buf.setBytes(index, src);
    return this;
  }
  
  public int setBytes(int index, InputStream in, int length) throws IOException
  {
    return buf.setBytes(index, in, length);
  }
  
  public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException
  {
    return buf.setBytes(index, in, length);
  }
  
  public ByteBuf setZero(int index, int length)
  {
    buf.setZero(index, length);
    return this;
  }
  
  public boolean readBoolean()
  {
    return buf.readBoolean();
  }
  
  public byte readByte()
  {
    return buf.readByte();
  }
  
  public short readUnsignedByte()
  {
    return buf.readUnsignedByte();
  }
  
  public short readShort()
  {
    return buf.readShort();
  }
  
  public int readUnsignedShort()
  {
    return buf.readUnsignedShort();
  }
  
  public int readMedium()
  {
    return buf.readMedium();
  }
  
  public int readUnsignedMedium()
  {
    return buf.readUnsignedMedium();
  }
  
  public int readInt()
  {
    return buf.readInt();
  }
  
  public long readUnsignedInt()
  {
    return buf.readUnsignedInt();
  }
  
  public long readLong()
  {
    return buf.readLong();
  }
  
  public char readChar()
  {
    return buf.readChar();
  }
  
  public float readFloat()
  {
    return buf.readFloat();
  }
  
  public double readDouble()
  {
    return buf.readDouble();
  }
  
  public ByteBuf readBytes(int length)
  {
    return buf.readBytes(length);
  }
  
  public ByteBuf readSlice(int length)
  {
    return buf.readSlice(length);
  }
  
  public ByteBuf readBytes(ByteBuf dst)
  {
    buf.readBytes(dst);
    return this;
  }
  
  public ByteBuf readBytes(ByteBuf dst, int length)
  {
    buf.readBytes(dst, length);
    return this;
  }
  
  public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length)
  {
    buf.readBytes(dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf readBytes(byte[] dst)
  {
    buf.readBytes(dst);
    return this;
  }
  
  public ByteBuf readBytes(byte[] dst, int dstIndex, int length)
  {
    buf.readBytes(dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf readBytes(ByteBuffer dst)
  {
    buf.readBytes(dst);
    return this;
  }
  
  public ByteBuf readBytes(OutputStream out, int length) throws IOException
  {
    buf.readBytes(out, length);
    return this;
  }
  
  public int readBytes(GatheringByteChannel out, int length) throws IOException
  {
    return buf.readBytes(out, length);
  }
  
  public ByteBuf skipBytes(int length)
  {
    buf.skipBytes(length);
    return this;
  }
  
  public ByteBuf writeBoolean(boolean value)
  {
    buf.writeBoolean(value);
    return this;
  }
  
  public ByteBuf writeByte(int value)
  {
    buf.writeByte(value);
    return this;
  }
  
  public ByteBuf writeShort(int value)
  {
    buf.writeShort(value);
    return this;
  }
  
  public ByteBuf writeMedium(int value)
  {
    buf.writeMedium(value);
    return this;
  }
  
  public ByteBuf writeInt(int value)
  {
    buf.writeInt(value);
    return this;
  }
  
  public ByteBuf writeLong(long value)
  {
    buf.writeLong(value);
    return this;
  }
  
  public ByteBuf writeChar(int value)
  {
    buf.writeChar(value);
    return this;
  }
  
  public ByteBuf writeFloat(float value)
  {
    buf.writeFloat(value);
    return this;
  }
  
  public ByteBuf writeDouble(double value)
  {
    buf.writeDouble(value);
    return this;
  }
  
  public ByteBuf writeBytes(ByteBuf src)
  {
    buf.writeBytes(src);
    return this;
  }
  
  public ByteBuf writeBytes(ByteBuf src, int length)
  {
    buf.writeBytes(src, length);
    return this;
  }
  
  public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length)
  {
    buf.writeBytes(src, srcIndex, length);
    return this;
  }
  
  public ByteBuf writeBytes(byte[] src)
  {
    buf.writeBytes(src);
    return this;
  }
  
  public ByteBuf writeBytes(byte[] src, int srcIndex, int length)
  {
    buf.writeBytes(src, srcIndex, length);
    return this;
  }
  
  public ByteBuf writeBytes(ByteBuffer src)
  {
    buf.writeBytes(src);
    return this;
  }
  
  public int writeBytes(InputStream in, int length) throws IOException
  {
    return buf.writeBytes(in, length);
  }
  
  public int writeBytes(ScatteringByteChannel in, int length) throws IOException
  {
    return buf.writeBytes(in, length);
  }
  
  public ByteBuf writeZero(int length)
  {
    buf.writeZero(length);
    return this;
  }
  
  public int indexOf(int fromIndex, int toIndex, byte value)
  {
    return buf.indexOf(fromIndex, toIndex, value);
  }
  
  public int bytesBefore(byte value)
  {
    return buf.bytesBefore(value);
  }
  
  public int bytesBefore(int length, byte value)
  {
    return buf.bytesBefore(length, value);
  }
  
  public int bytesBefore(int index, int length, byte value)
  {
    return buf.bytesBefore(index, length, value);
  }
  
  public int forEachByte(ByteBufProcessor processor)
  {
    return buf.forEachByte(processor);
  }
  
  public int forEachByte(int index, int length, ByteBufProcessor processor)
  {
    return buf.forEachByte(index, length, processor);
  }
  
  public int forEachByteDesc(ByteBufProcessor processor)
  {
    return buf.forEachByteDesc(processor);
  }
  
  public int forEachByteDesc(int index, int length, ByteBufProcessor processor)
  {
    return buf.forEachByteDesc(index, length, processor);
  }
  
  public ByteBuf copy()
  {
    return buf.copy();
  }
  
  public ByteBuf copy(int index, int length)
  {
    return buf.copy(index, length);
  }
  
  public ByteBuf slice()
  {
    return buf.slice();
  }
  
  public ByteBuf slice(int index, int length)
  {
    return buf.slice(index, length);
  }
  
  public ByteBuf duplicate()
  {
    return buf.duplicate();
  }
  
  public int nioBufferCount()
  {
    return buf.nioBufferCount();
  }
  
  public ByteBuffer nioBuffer()
  {
    return buf.nioBuffer();
  }
  
  public ByteBuffer nioBuffer(int index, int length)
  {
    return buf.nioBuffer(index, length);
  }
  
  public ByteBuffer[] nioBuffers()
  {
    return buf.nioBuffers();
  }
  
  public ByteBuffer[] nioBuffers(int index, int length)
  {
    return buf.nioBuffers(index, length);
  }
  
  public ByteBuffer internalNioBuffer(int index, int length)
  {
    return buf.internalNioBuffer(index, length);
  }
  
  public boolean hasArray()
  {
    return buf.hasArray();
  }
  
  public byte[] array()
  {
    return buf.array();
  }
  
  public int arrayOffset()
  {
    return buf.arrayOffset();
  }
  
  public String toString(Charset charset)
  {
    return buf.toString(charset);
  }
  
  public String toString(int index, int length, Charset charset)
  {
    return buf.toString(index, length, charset);
  }
  
  public int hashCode()
  {
    return buf.hashCode();
  }
  

  public boolean equals(Object obj)
  {
    return buf.equals(obj);
  }
  
  public int compareTo(ByteBuf buffer)
  {
    return buf.compareTo(buffer);
  }
  
  public String toString()
  {
    return StringUtil.simpleClassName(this) + '(' + buf.toString() + ')';
  }
  
  public ByteBuf retain(int increment)
  {
    buf.retain(increment);
    return this;
  }
  
  public ByteBuf retain()
  {
    buf.retain();
    return this;
  }
  
  public boolean isReadable(int size)
  {
    return buf.isReadable(size);
  }
  
  public boolean isWritable(int size)
  {
    return buf.isWritable(size);
  }
  
  public int refCnt()
  {
    return buf.refCnt();
  }
  
  public boolean release()
  {
    return buf.release();
  }
  
  public boolean release(int decrement)
  {
    return buf.release(decrement);
  }
}
