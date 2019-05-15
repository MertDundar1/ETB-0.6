package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;



















public abstract class AbstractMemoryHttpData
  extends AbstractHttpData
{
  private ByteBuf byteBuf;
  private int chunkPosition;
  protected boolean isRenamed;
  
  protected AbstractMemoryHttpData(String name, Charset charset, long size)
  {
    super(name, charset, size);
  }
  
  public void setContent(ByteBuf buffer) throws IOException
  {
    if (buffer == null) {
      throw new NullPointerException("buffer");
    }
    long localsize = buffer.readableBytes();
    if ((definedSize > 0L) && (definedSize < localsize)) {
      throw new IOException("Out of size: " + localsize + " > " + definedSize);
    }
    
    if (byteBuf != null) {
      byteBuf.release();
    }
    byteBuf = buffer;
    size = localsize;
    completed = true;
  }
  
  public void setContent(InputStream inputStream) throws IOException
  {
    if (inputStream == null) {
      throw new NullPointerException("inputStream");
    }
    ByteBuf buffer = Unpooled.buffer();
    byte[] bytes = new byte['䀀'];
    int read = inputStream.read(bytes);
    int written = 0;
    while (read > 0) {
      buffer.writeBytes(bytes, 0, read);
      written += read;
      read = inputStream.read(bytes);
    }
    size = written;
    if ((definedSize > 0L) && (definedSize < size)) {
      throw new IOException("Out of size: " + size + " > " + definedSize);
    }
    if (byteBuf != null) {
      byteBuf.release();
    }
    byteBuf = buffer;
    completed = true;
  }
  
  public void addContent(ByteBuf buffer, boolean last)
    throws IOException
  {
    if (buffer != null) {
      long localsize = buffer.readableBytes();
      if ((definedSize > 0L) && (definedSize < size + localsize)) {
        throw new IOException("Out of size: " + (size + localsize) + " > " + definedSize);
      }
      
      size += localsize;
      if (byteBuf == null) {
        byteBuf = buffer;
      } else if ((byteBuf instanceof CompositeByteBuf)) {
        CompositeByteBuf cbb = (CompositeByteBuf)byteBuf;
        cbb.addComponent(buffer);
        cbb.writerIndex(cbb.writerIndex() + buffer.readableBytes());
      } else {
        CompositeByteBuf cbb = Unpooled.compositeBuffer(Integer.MAX_VALUE);
        cbb.addComponents(new ByteBuf[] { byteBuf, buffer });
        cbb.writerIndex(byteBuf.readableBytes() + buffer.readableBytes());
        byteBuf = cbb;
      }
    }
    if (last) {
      completed = true;
    }
    else if (buffer == null) {
      throw new NullPointerException("buffer");
    }
  }
  
  public void setContent(File file)
    throws IOException
  {
    if (file == null) {
      throw new NullPointerException("file");
    }
    long newsize = file.length();
    if (newsize > 2147483647L) {
      throw new IllegalArgumentException("File too big to be loaded in memory");
    }
    
    FileInputStream inputStream = new FileInputStream(file);
    FileChannel fileChannel = inputStream.getChannel();
    byte[] array = new byte[(int)newsize];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    int read = 0;
    while (read < newsize) {
      read += fileChannel.read(byteBuffer);
    }
    fileChannel.close();
    inputStream.close();
    byteBuffer.flip();
    if (byteBuf != null) {
      byteBuf.release();
    }
    byteBuf = Unpooled.wrappedBuffer(Integer.MAX_VALUE, new ByteBuffer[] { byteBuffer });
    size = newsize;
    completed = true;
  }
  
  public void delete()
  {
    if (byteBuf != null) {
      byteBuf.release();
      byteBuf = null;
    }
  }
  
  public byte[] get()
  {
    if (byteBuf == null) {
      return Unpooled.EMPTY_BUFFER.array();
    }
    byte[] array = new byte[byteBuf.readableBytes()];
    byteBuf.getBytes(byteBuf.readerIndex(), array);
    return array;
  }
  
  public String getString()
  {
    return getString(HttpConstants.DEFAULT_CHARSET);
  }
  
  public String getString(Charset encoding)
  {
    if (byteBuf == null) {
      return "";
    }
    if (encoding == null) {
      encoding = HttpConstants.DEFAULT_CHARSET;
    }
    return byteBuf.toString(encoding);
  }
  





  public ByteBuf getByteBuf()
  {
    return byteBuf;
  }
  
  public ByteBuf getChunk(int length) throws IOException
  {
    if ((byteBuf == null) || (length == 0) || (byteBuf.readableBytes() == 0)) {
      chunkPosition = 0;
      return Unpooled.EMPTY_BUFFER;
    }
    int sizeLeft = byteBuf.readableBytes() - chunkPosition;
    if (sizeLeft == 0) {
      chunkPosition = 0;
      return Unpooled.EMPTY_BUFFER;
    }
    int sliceLength = length;
    if (sizeLeft < length) {
      sliceLength = sizeLeft;
    }
    ByteBuf chunk = byteBuf.slice(chunkPosition, sliceLength).retain();
    chunkPosition += sliceLength;
    return chunk;
  }
  
  public boolean isInMemory()
  {
    return true;
  }
  
  public boolean renameTo(File dest) throws IOException
  {
    if (dest == null) {
      throw new NullPointerException("dest");
    }
    if (byteBuf == null)
    {
      dest.createNewFile();
      isRenamed = true;
      return true;
    }
    int length = byteBuf.readableBytes();
    FileOutputStream outputStream = new FileOutputStream(dest);
    FileChannel fileChannel = outputStream.getChannel();
    int written = 0;
    if (byteBuf.nioBufferCount() == 1) {
      ByteBuffer byteBuffer = byteBuf.nioBuffer();
      while (written < length) {
        written += fileChannel.write(byteBuffer);
      }
    } else {
      ByteBuffer[] byteBuffers = byteBuf.nioBuffers();
      while (written < length) {
        written = (int)(written + fileChannel.write(byteBuffers));
      }
    }
    
    fileChannel.force(false);
    fileChannel.close();
    outputStream.close();
    isRenamed = true;
    return written == length;
  }
  
  public File getFile() throws IOException
  {
    throw new IOException("Not represented by a file");
  }
}
