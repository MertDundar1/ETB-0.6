package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteOrder;



















final class UnsafeDirectSwappedByteBuf
  extends SwappedByteBuf
{
  private static final boolean NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
  private final boolean nativeByteOrder;
  private final AbstractByteBuf wrapped;
  
  UnsafeDirectSwappedByteBuf(AbstractByteBuf buf) {
    super(buf);
    wrapped = buf;
    nativeByteOrder = (NATIVE_ORDER == (order() == ByteOrder.BIG_ENDIAN));
  }
  



  private long addr(int index)
  {
    return wrapped.memoryAddress() + index;
  }
  
  public long getLong(int index)
  {
    wrapped.checkIndex(index, 8);
    long v = PlatformDependent.getLong(addr(index));
    return nativeByteOrder ? v : Long.reverseBytes(v);
  }
  
  public float getFloat(int index)
  {
    return Float.intBitsToFloat(getInt(index));
  }
  
  public double getDouble(int index)
  {
    return Double.longBitsToDouble(getLong(index));
  }
  
  public char getChar(int index)
  {
    return (char)getShort(index);
  }
  
  public long getUnsignedInt(int index)
  {
    return getInt(index) & 0xFFFFFFFF;
  }
  
  public int getInt(int index)
  {
    wrapped.checkIndex(index, 4);
    int v = PlatformDependent.getInt(addr(index));
    return nativeByteOrder ? v : Integer.reverseBytes(v);
  }
  
  public int getUnsignedShort(int index)
  {
    return getShort(index) & 0xFFFF;
  }
  
  public short getShort(int index)
  {
    wrapped.checkIndex(index, 2);
    short v = PlatformDependent.getShort(addr(index));
    return nativeByteOrder ? v : Short.reverseBytes(v);
  }
  
  public ByteBuf setShort(int index, int value)
  {
    wrapped.checkIndex(index, 2);
    _setShort(index, value);
    return this;
  }
  
  public ByteBuf setInt(int index, int value)
  {
    wrapped.checkIndex(index, 4);
    _setInt(index, value);
    return this;
  }
  
  public ByteBuf setLong(int index, long value)
  {
    wrapped.checkIndex(index, 8);
    _setLong(index, value);
    return this;
  }
  
  public ByteBuf setChar(int index, int value)
  {
    setShort(index, value);
    return this;
  }
  
  public ByteBuf setFloat(int index, float value)
  {
    setInt(index, Float.floatToRawIntBits(value));
    return this;
  }
  
  public ByteBuf setDouble(int index, double value)
  {
    setLong(index, Double.doubleToRawLongBits(value));
    return this;
  }
  
  public ByteBuf writeShort(int value)
  {
    wrapped.ensureAccessible();
    wrapped.ensureWritable(2);
    _setShort(wrapped.writerIndex, value);
    wrapped.writerIndex += 2;
    return this;
  }
  
  public ByteBuf writeInt(int value)
  {
    wrapped.ensureAccessible();
    wrapped.ensureWritable(4);
    _setInt(wrapped.writerIndex, value);
    wrapped.writerIndex += 4;
    return this;
  }
  
  public ByteBuf writeLong(long value)
  {
    wrapped.ensureAccessible();
    wrapped.ensureWritable(8);
    _setLong(wrapped.writerIndex, value);
    wrapped.writerIndex += 8;
    return this;
  }
  
  public ByteBuf writeChar(int value)
  {
    writeShort(value);
    return this;
  }
  
  public ByteBuf writeFloat(float value)
  {
    writeInt(Float.floatToRawIntBits(value));
    return this;
  }
  
  public ByteBuf writeDouble(double value)
  {
    writeLong(Double.doubleToRawLongBits(value));
    return this;
  }
  
  private void _setShort(int index, int value) {
    PlatformDependent.putShort(addr(index), nativeByteOrder ? (short)value : Short.reverseBytes((short)value));
  }
  
  private void _setInt(int index, int value) {
    PlatformDependent.putInt(addr(index), nativeByteOrder ? value : Integer.reverseBytes(value));
  }
  
  private void _setLong(int index, long value) {
    PlatformDependent.putLong(addr(index), nativeByteOrder ? value : Long.reverseBytes(value));
  }
}
