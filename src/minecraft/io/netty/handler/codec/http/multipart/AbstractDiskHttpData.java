package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;



















public abstract class AbstractDiskHttpData
  extends AbstractHttpData
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractDiskHttpData.class);
  protected File file;
  private boolean isRenamed;
  private FileChannel fileChannel;
  
  protected AbstractDiskHttpData(String name, Charset charset, long size)
  {
    super(name, charset, size);
  }
  



  protected abstract String getDiskFilename();
  



  protected abstract String getPrefix();
  



  protected abstract String getBaseDirectory();
  



  protected abstract String getPostfix();
  



  protected abstract boolean deleteOnExit();
  



  private File tempFile()
    throws IOException
  {
    String diskFilename = getDiskFilename();
    String newpostfix; String newpostfix; if (diskFilename != null) {
      newpostfix = '_' + diskFilename;
    } else
      newpostfix = getPostfix();
    File tmpFile;
    File tmpFile;
    if (getBaseDirectory() == null)
    {
      tmpFile = File.createTempFile(getPrefix(), newpostfix);
    } else {
      tmpFile = File.createTempFile(getPrefix(), newpostfix, new File(getBaseDirectory()));
    }
    
    if (deleteOnExit()) {
      tmpFile.deleteOnExit();
    }
    return tmpFile;
  }
  
  public void setContent(ByteBuf buffer) throws IOException
  {
    if (buffer == null) {
      throw new NullPointerException("buffer");
    }
    try {
      size = buffer.readableBytes();
      if ((definedSize > 0L) && (definedSize < size)) {
        throw new IOException("Out of size: " + size + " > " + definedSize);
      }
      if (file == null) {
        file = tempFile();
      }
      if (buffer.readableBytes() == 0)
      {
        file.createNewFile();
      }
      else {
        FileOutputStream outputStream = new FileOutputStream(file);
        FileChannel localfileChannel = outputStream.getChannel();
        ByteBuffer byteBuffer = buffer.nioBuffer();
        int written = 0;
        while (written < size) {
          written += localfileChannel.write(byteBuffer);
        }
        buffer.readerIndex(buffer.readerIndex() + written);
        localfileChannel.force(false);
        localfileChannel.close();
        outputStream.close();
        completed = true;
      }
    }
    finally {
      buffer.release();
    }
  }
  
  public void addContent(ByteBuf buffer, boolean last)
    throws IOException
  {
    if (buffer != null) {
      try {
        int localsize = buffer.readableBytes();
        if ((definedSize > 0L) && (definedSize < size + localsize)) {
          throw new IOException("Out of size: " + (size + localsize) + " > " + definedSize);
        }
        
        ByteBuffer byteBuffer = buffer.nioBufferCount() == 1 ? buffer.nioBuffer() : buffer.copy().nioBuffer();
        int written = 0;
        if (file == null) {
          file = tempFile();
        }
        if (fileChannel == null) {
          FileOutputStream outputStream = new FileOutputStream(file);
          fileChannel = outputStream.getChannel();
        }
        while (written < localsize) {
          written += fileChannel.write(byteBuffer);
        }
        size += localsize;
        buffer.readerIndex(buffer.readerIndex() + written);
      }
      finally
      {
        buffer.release();
      }
    }
    if (last) {
      if (file == null) {
        file = tempFile();
      }
      if (fileChannel == null) {
        FileOutputStream outputStream = new FileOutputStream(file);
        fileChannel = outputStream.getChannel();
      }
      fileChannel.force(false);
      fileChannel.close();
      fileChannel = null;
      completed = true;
    }
    else if (buffer == null) {
      throw new NullPointerException("buffer");
    }
  }
  
  public void setContent(File file)
    throws IOException
  {
    if (this.file != null) {
      delete();
    }
    this.file = file;
    size = file.length();
    isRenamed = true;
    completed = true;
  }
  
  public void setContent(InputStream inputStream) throws IOException
  {
    if (inputStream == null) {
      throw new NullPointerException("inputStream");
    }
    if (file != null) {
      delete();
    }
    file = tempFile();
    FileOutputStream outputStream = new FileOutputStream(file);
    FileChannel localfileChannel = outputStream.getChannel();
    byte[] bytes = new byte['䀀'];
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    int read = inputStream.read(bytes);
    int written = 0;
    while (read > 0) {
      byteBuffer.position(read).flip();
      written += localfileChannel.write(byteBuffer);
      read = inputStream.read(bytes);
    }
    localfileChannel.force(false);
    localfileChannel.close();
    size = written;
    if ((definedSize > 0L) && (definedSize < size)) {
      file.delete();
      file = null;
      throw new IOException("Out of size: " + size + " > " + definedSize);
    }
    isRenamed = true;
    completed = true;
  }
  
  public void delete()
  {
    if (fileChannel != null) {
      try {
        fileChannel.force(false);
        fileChannel.close();
      } catch (IOException e) {
        logger.warn("Failed to close a file.", e);
      }
      fileChannel = null;
    }
    if (!isRenamed) {
      if ((file != null) && (file.exists())) {
        file.delete();
      }
      file = null;
    }
  }
  
  public byte[] get() throws IOException
  {
    if (file == null) {
      return EmptyArrays.EMPTY_BYTES;
    }
    return readFrom(file);
  }
  
  public ByteBuf getByteBuf() throws IOException
  {
    if (file == null) {
      return Unpooled.EMPTY_BUFFER;
    }
    byte[] array = readFrom(file);
    return Unpooled.wrappedBuffer(array);
  }
  
  public ByteBuf getChunk(int length) throws IOException
  {
    if ((file == null) || (length == 0)) {
      return Unpooled.EMPTY_BUFFER;
    }
    if (fileChannel == null) {
      FileInputStream inputStream = new FileInputStream(file);
      fileChannel = inputStream.getChannel();
    }
    int read = 0;
    ByteBuffer byteBuffer = ByteBuffer.allocate(length);
    while (read < length) {
      int readnow = fileChannel.read(byteBuffer);
      if (readnow == -1) {
        fileChannel.close();
        fileChannel = null;
        break;
      }
      read += readnow;
    }
    
    if (read == 0) {
      return Unpooled.EMPTY_BUFFER;
    }
    byteBuffer.flip();
    ByteBuf buffer = Unpooled.wrappedBuffer(byteBuffer);
    buffer.readerIndex(0);
    buffer.writerIndex(read);
    return buffer;
  }
  
  public String getString() throws IOException
  {
    return getString(HttpConstants.DEFAULT_CHARSET);
  }
  
  public String getString(Charset encoding) throws IOException
  {
    if (file == null) {
      return "";
    }
    if (encoding == null) {
      byte[] array = readFrom(file);
      return new String(array, HttpConstants.DEFAULT_CHARSET.name());
    }
    byte[] array = readFrom(file);
    return new String(array, encoding.name());
  }
  
  public boolean isInMemory()
  {
    return false;
  }
  
  public boolean renameTo(File dest) throws IOException
  {
    if (dest == null) {
      throw new NullPointerException("dest");
    }
    if (file == null) {
      throw new IOException("No file defined so cannot be renamed");
    }
    if (!file.renameTo(dest))
    {
      FileInputStream inputStream = new FileInputStream(file);
      FileOutputStream outputStream = new FileOutputStream(dest);
      FileChannel in = inputStream.getChannel();
      FileChannel out = outputStream.getChannel();
      int chunkSize = 8196;
      long position = 0L;
      while (position < size) {
        if (chunkSize < size - position) {
          chunkSize = (int)(size - position);
        }
        position += in.transferTo(position, chunkSize, out);
      }
      in.close();
      out.close();
      if (position == size) {
        file.delete();
        file = dest;
        isRenamed = true;
        return true;
      }
      dest.delete();
      return false;
    }
    
    file = dest;
    isRenamed = true;
    return true;
  }
  


  private static byte[] readFrom(File src)
    throws IOException
  {
    long srcsize = src.length();
    if (srcsize > 2147483647L) {
      throw new IllegalArgumentException("File too big to be loaded in memory");
    }
    
    FileInputStream inputStream = new FileInputStream(src);
    FileChannel fileChannel = inputStream.getChannel();
    byte[] array = new byte[(int)srcsize];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    int read = 0;
    while (read < srcsize) {
      read += fileChannel.read(byteBuffer);
    }
    fileChannel.close();
    return array;
  }
  
  public File getFile() throws IOException
  {
    return file;
  }
}
