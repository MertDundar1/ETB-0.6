package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;























public class UnpooledHeapByteBuf
  extends AbstractReferenceCountedByteBuf
{
  private final ByteBufAllocator alloc;
  private byte[] array;
  private ByteBuffer tmpNioBuf;
  
  protected UnpooledHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity)
  {
    this(alloc, new byte[initialCapacity], 0, 0, maxCapacity);
  }
  





  protected UnpooledHeapByteBuf(ByteBufAllocator alloc, byte[] initialArray, int maxCapacity)
  {
    this(alloc, initialArray, 0, initialArray.length, maxCapacity);
  }
  

  private UnpooledHeapByteBuf(ByteBufAllocator alloc, byte[] initialArray, int readerIndex, int writerIndex, int maxCapacity)
  {
    super(maxCapacity);
    
    if (alloc == null) {
      throw new NullPointerException("alloc");
    }
    if (initialArray == null) {
      throw new NullPointerException("initialArray");
    }
    if (initialArray.length > maxCapacity) {
      throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", new Object[] { Integer.valueOf(initialArray.length), Integer.valueOf(maxCapacity) }));
    }
    

    this.alloc = alloc;
    setArray(initialArray);
    setIndex(readerIndex, writerIndex);
  }
  
  private void setArray(byte[] initialArray) {
    array = initialArray;
    tmpNioBuf = null;
  }
  
  public ByteBufAllocator alloc()
  {
    return alloc;
  }
  
  public ByteOrder order()
  {
    return ByteOrder.BIG_ENDIAN;
  }
  
  public boolean isDirect()
  {
    return false;
  }
  
  public int capacity()
  {
    ensureAccessible();
    return array.length;
  }
  
  public ByteBuf capacity(int newCapacity)
  {
    ensureAccessible();
    if ((newCapacity < 0) || (newCapacity > maxCapacity())) {
      throw new IllegalArgumentException("newCapacity: " + newCapacity);
    }
    
    int oldCapacity = array.length;
    if (newCapacity > oldCapacity) {
      byte[] newArray = new byte[newCapacity];
      System.arraycopy(array, 0, newArray, 0, array.length);
      setArray(newArray);
    } else if (newCapacity < oldCapacity) {
      byte[] newArray = new byte[newCapacity];
      int readerIndex = readerIndex();
      if (readerIndex < newCapacity) {
        int writerIndex = writerIndex();
        if (writerIndex > newCapacity) {
          writerIndex(writerIndex = newCapacity);
        }
        System.arraycopy(array, readerIndex, newArray, readerIndex, writerIndex - readerIndex);
      } else {
        setIndex(newCapacity, newCapacity);
      }
      setArray(newArray);
    }
    return this;
  }
  
  public boolean hasArray()
  {
    return true;
  }
  
  public byte[] array()
  {
    ensureAccessible();
    return array;
  }
  
  public int arrayOffset()
  {
    return 0;
  }
  
  public boolean hasMemoryAddress()
  {
    return false;
  }
  
  public long memoryAddress()
  {
    throw new UnsupportedOperationException();
  }
  
  public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length)
  {
    checkDstIndex(index, length, dstIndex, dst.capacity());
    if (dst.hasMemoryAddress()) {
      PlatformDependent.copyMemory(array, index, dst.memoryAddress() + dstIndex, length);
    } else if (dst.hasArray()) {
      getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
    } else {
      dst.setBytes(dstIndex, array, index, length);
    }
    return this;
  }
  
  public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length)
  {
    checkDstIndex(index, length, dstIndex, dst.length);
    System.arraycopy(array, index, dst, dstIndex, length);
    return this;
  }
  
  public ByteBuf getBytes(int index, ByteBuffer dst)
  {
    ensureAccessible();
    dst.put(array, index, Math.min(capacity() - index, dst.remaining()));
    return this;
  }
  
  public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException
  {
    ensureAccessible();
    out.write(array, index, length);
    return this;
  }
  
  public int getBytes(int index, GatheringByteChannel out, int length) throws IOException
  {
    ensureAccessible();
    return getBytes(index, out, length, false);
  }
  
  private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
    ensureAccessible();
    ByteBuffer tmpBuf;
    ByteBuffer tmpBuf; if (internal) {
      tmpBuf = internalNioBuffer();
    } else {
      tmpBuf = ByteBuffer.wrap(array);
    }
    return out.write((ByteBuffer)tmpBuf.clear().position(index).limit(index + length));
  }
  
  public int readBytes(GatheringByteChannel out, int length) throws IOException
  {
    checkReadableBytes(length);
    int readBytes = getBytes(readerIndex, out, length, true);
    readerIndex += readBytes;
    return readBytes;
  }
  
  public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length)
  {
    checkSrcIndex(index, length, srcIndex, src.capacity());
    if (src.hasMemoryAddress()) {
      PlatformDependent.copyMemory(src.memoryAddress() + srcIndex, array, index, length);
    } else if (src.hasArray()) {
      setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
    } else {
      src.getBytes(srcIndex, array, index, length);
    }
    return this;
  }
  
  public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length)
  {
    checkSrcIndex(index, length, srcIndex, src.length);
    System.arraycopy(src, srcIndex, array, index, length);
    return this;
  }
  
  public ByteBuf setBytes(int index, ByteBuffer src)
  {
    ensureAccessible();
    src.get(array, index, src.remaining());
    return this;
  }
  
  public int setBytes(int index, InputStream in, int length) throws IOException
  {
    ensureAccessible();
    return in.read(array, index, length);
  }
  
  public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException
  {
    ensureAccessible();
    try {
      return in.read((ByteBuffer)internalNioBuffer().clear().position(index).limit(index + length));
    } catch (ClosedChannelException ignored) {}
    return -1;
  }
  

  public int nioBufferCount()
  {
    return 1;
  }
  
  public ByteBuffer nioBuffer(int index, int length)
  {
    ensureAccessible();
    return ByteBuffer.wrap(array, index, length).slice();
  }
  
  public ByteBuffer[] nioBuffers(int index, int length)
  {
    return new ByteBuffer[] { nioBuffer(index, length) };
  }
  
  public ByteBuffer internalNioBuffer(int index, int length)
  {
    checkIndex(index, length);
    return (ByteBuffer)internalNioBuffer().clear().position(index).limit(index + length);
  }
  
  public byte getByte(int index)
  {
    ensureAccessible();
    return _getByte(index);
  }
  
  protected byte _getByte(int index)
  {
    return array[index];
  }
  
  public short getShort(int index)
  {
    ensureAccessible();
    return _getShort(index);
  }
  
  protected short _getShort(int index)
  {
    return (short)(array[index] << 8 | array[(index + 1)] & 0xFF);
  }
  
  public int getUnsignedMedium(int index)
  {
    ensureAccessible();
    return _getUnsignedMedium(index);
  }
  
  protected int _getUnsignedMedium(int index)
  {
    return (array[index] & 0xFF) << 16 | (array[(index + 1)] & 0xFF) << 8 | array[(index + 2)] & 0xFF;
  }
  


  public int getInt(int index)
  {
    ensureAccessible();
    return _getInt(index);
  }
  
  protected int _getInt(int index)
  {
    return (array[index] & 0xFF) << 24 | (array[(index + 1)] & 0xFF) << 16 | (array[(index + 2)] & 0xFF) << 8 | array[(index + 3)] & 0xFF;
  }
  



  public long getLong(int index)
  {
    ensureAccessible();
    return _getLong(index);
  }
  
  protected long _getLong(int index)
  {
    return (array[index] & 0xFF) << 56 | (array[(index + 1)] & 0xFF) << 48 | (array[(index + 2)] & 0xFF) << 40 | (array[(index + 3)] & 0xFF) << 32 | (array[(index + 4)] & 0xFF) << 24 | (array[(index + 5)] & 0xFF) << 16 | (array[(index + 6)] & 0xFF) << 8 | array[(index + 7)] & 0xFF;
  }
  







  public ByteBuf setByte(int index, int value)
  {
    ensureAccessible();
    _setByte(index, value);
    return this;
  }
  
  protected void _setByte(int index, int value)
  {
    array[index] = ((byte)value);
  }
  
  public ByteBuf setShort(int index, int value)
  {
    ensureAccessible();
    _setShort(index, value);
    return this;
  }
  
  protected void _setShort(int index, int value)
  {
    array[index] = ((byte)(value >>> 8));
    array[(index + 1)] = ((byte)value);
  }
  
  public ByteBuf setMedium(int index, int value)
  {
    ensureAccessible();
    _setMedium(index, value);
    return this;
  }
  
  protected void _setMedium(int index, int value)
  {
    array[index] = ((byte)(value >>> 16));
    array[(index + 1)] = ((byte)(value >>> 8));
    array[(index + 2)] = ((byte)value);
  }
  
  public ByteBuf setInt(int index, int value)
  {
    ensureAccessible();
    _setInt(index, value);
    return this;
  }
  
  protected void _setInt(int index, int value)
  {
    array[index] = ((byte)(value >>> 24));
    array[(index + 1)] = ((byte)(value >>> 16));
    array[(index + 2)] = ((byte)(value >>> 8));
    array[(index + 3)] = ((byte)value);
  }
  
  public ByteBuf setLong(int index, long value)
  {
    ensureAccessible();
    _setLong(index, value);
    return this;
  }
  
  protected void _setLong(int index, long value)
  {
    array[index] = ((byte)(int)(value >>> 56));
    array[(index + 1)] = ((byte)(int)(value >>> 48));
    array[(index + 2)] = ((byte)(int)(value >>> 40));
    array[(index + 3)] = ((byte)(int)(value >>> 32));
    array[(index + 4)] = ((byte)(int)(value >>> 24));
    array[(index + 5)] = ((byte)(int)(value >>> 16));
    array[(index + 6)] = ((byte)(int)(value >>> 8));
    array[(index + 7)] = ((byte)(int)value);
  }
  
  public ByteBuf copy(int index, int length)
  {
    checkIndex(index, length);
    byte[] copiedArray = new byte[length];
    System.arraycopy(array, index, copiedArray, 0, length);
    return new UnpooledHeapByteBuf(alloc(), copiedArray, maxCapacity());
  }
  
  private ByteBuffer internalNioBuffer() {
    ByteBuffer tmpNioBuf = this.tmpNioBuf;
    if (tmpNioBuf == null) {
      this.tmpNioBuf = (tmpNioBuf = ByteBuffer.wrap(array));
    }
    return tmpNioBuf;
  }
  
  protected void deallocate()
  {
    array = null;
  }
  
  public ByteBuf unwrap()
  {
    return null;
  }
}
