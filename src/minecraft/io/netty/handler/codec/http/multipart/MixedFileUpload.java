package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;






















public class MixedFileUpload
  implements FileUpload
{
  private FileUpload fileUpload;
  private final long limitSize;
  private final long definedSize;
  
  public MixedFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size, long limitSize)
  {
    this.limitSize = limitSize;
    if (size > this.limitSize) {
      fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
    }
    else {
      fileUpload = new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
    }
    
    definedSize = size;
  }
  
  public void addContent(ByteBuf buffer, boolean last)
    throws IOException
  {
    if (((fileUpload instanceof MemoryFileUpload)) && 
      (fileUpload.length() + buffer.readableBytes() > limitSize)) {
      DiskFileUpload diskFileUpload = new DiskFileUpload(fileUpload.getName(), fileUpload.getFilename(), fileUpload.getContentType(), fileUpload.getContentTransferEncoding(), fileUpload.getCharset(), definedSize);
      




      ByteBuf data = fileUpload.getByteBuf();
      if ((data != null) && (data.isReadable())) {
        diskFileUpload.addContent(data.retain(), false);
      }
      
      fileUpload.release();
      
      fileUpload = diskFileUpload;
    }
    
    fileUpload.addContent(buffer, last);
  }
  
  public void delete()
  {
    fileUpload.delete();
  }
  
  public byte[] get() throws IOException
  {
    return fileUpload.get();
  }
  
  public ByteBuf getByteBuf() throws IOException
  {
    return fileUpload.getByteBuf();
  }
  
  public Charset getCharset()
  {
    return fileUpload.getCharset();
  }
  
  public String getContentType()
  {
    return fileUpload.getContentType();
  }
  
  public String getContentTransferEncoding()
  {
    return fileUpload.getContentTransferEncoding();
  }
  
  public String getFilename()
  {
    return fileUpload.getFilename();
  }
  
  public String getString() throws IOException
  {
    return fileUpload.getString();
  }
  
  public String getString(Charset encoding) throws IOException
  {
    return fileUpload.getString(encoding);
  }
  
  public boolean isCompleted()
  {
    return fileUpload.isCompleted();
  }
  
  public boolean isInMemory()
  {
    return fileUpload.isInMemory();
  }
  
  public long length()
  {
    return fileUpload.length();
  }
  
  public boolean renameTo(File dest) throws IOException
  {
    return fileUpload.renameTo(dest);
  }
  
  public void setCharset(Charset charset)
  {
    fileUpload.setCharset(charset);
  }
  
  public void setContent(ByteBuf buffer) throws IOException
  {
    if ((buffer.readableBytes() > limitSize) && 
      ((fileUpload instanceof MemoryFileUpload))) {
      FileUpload memoryUpload = fileUpload;
      
      fileUpload = new DiskFileUpload(memoryUpload.getName(), memoryUpload.getFilename(), memoryUpload.getContentType(), memoryUpload.getContentTransferEncoding(), memoryUpload.getCharset(), definedSize);
      





      memoryUpload.release();
    }
    
    fileUpload.setContent(buffer);
  }
  
  public void setContent(File file) throws IOException
  {
    if ((file.length() > limitSize) && 
      ((fileUpload instanceof MemoryFileUpload))) {
      FileUpload memoryUpload = fileUpload;
      

      fileUpload = new DiskFileUpload(memoryUpload.getName(), memoryUpload.getFilename(), memoryUpload.getContentType(), memoryUpload.getContentTransferEncoding(), memoryUpload.getCharset(), definedSize);
      





      memoryUpload.release();
    }
    
    fileUpload.setContent(file);
  }
  
  public void setContent(InputStream inputStream) throws IOException
  {
    if ((fileUpload instanceof MemoryFileUpload)) {
      FileUpload memoryUpload = fileUpload;
      

      fileUpload = new DiskFileUpload(fileUpload.getName(), fileUpload.getFilename(), fileUpload.getContentType(), fileUpload.getContentTransferEncoding(), fileUpload.getCharset(), definedSize);
      





      memoryUpload.release();
    }
    fileUpload.setContent(inputStream);
  }
  
  public void setContentType(String contentType)
  {
    fileUpload.setContentType(contentType);
  }
  
  public void setContentTransferEncoding(String contentTransferEncoding)
  {
    fileUpload.setContentTransferEncoding(contentTransferEncoding);
  }
  
  public void setFilename(String filename)
  {
    fileUpload.setFilename(filename);
  }
  
  public InterfaceHttpData.HttpDataType getHttpDataType()
  {
    return fileUpload.getHttpDataType();
  }
  
  public String getName()
  {
    return fileUpload.getName();
  }
  
  public int compareTo(InterfaceHttpData o)
  {
    return fileUpload.compareTo(o);
  }
  
  public String toString()
  {
    return "Mixed: " + fileUpload.toString();
  }
  
  public ByteBuf getChunk(int length) throws IOException
  {
    return fileUpload.getChunk(length);
  }
  
  public File getFile() throws IOException
  {
    return fileUpload.getFile();
  }
  
  public FileUpload copy()
  {
    return fileUpload.copy();
  }
  
  public FileUpload duplicate()
  {
    return fileUpload.duplicate();
  }
  
  public ByteBuf content()
  {
    return fileUpload.content();
  }
  
  public int refCnt()
  {
    return fileUpload.refCnt();
  }
  
  public FileUpload retain()
  {
    fileUpload.retain();
    return this;
  }
  
  public FileUpload retain(int increment)
  {
    fileUpload.retain(increment);
    return this;
  }
  
  public boolean release()
  {
    return fileUpload.release();
  }
  
  public boolean release(int decrement)
  {
    return fileUpload.release(decrement);
  }
}
