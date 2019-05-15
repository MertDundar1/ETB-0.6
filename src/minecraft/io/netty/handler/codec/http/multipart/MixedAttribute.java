package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


















public class MixedAttribute
  implements Attribute
{
  private Attribute attribute;
  private final long limitSize;
  
  public MixedAttribute(String name, long limitSize)
  {
    this.limitSize = limitSize;
    attribute = new MemoryAttribute(name);
  }
  
  public MixedAttribute(String name, String value, long limitSize) {
    this.limitSize = limitSize;
    if (value.length() > this.limitSize) {
      try {
        attribute = new DiskAttribute(name, value);
      }
      catch (IOException e) {
        try {
          attribute = new MemoryAttribute(name, value);
        } catch (IOException e1) {
          throw new IllegalArgumentException(e);
        }
      }
    } else {
      try {
        attribute = new MemoryAttribute(name, value);
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }
  
  public void addContent(ByteBuf buffer, boolean last) throws IOException
  {
    if (((attribute instanceof MemoryAttribute)) && 
      (attribute.length() + buffer.readableBytes() > limitSize)) {
      DiskAttribute diskAttribute = new DiskAttribute(attribute.getName());
      
      if (((MemoryAttribute)attribute).getByteBuf() != null) {
        diskAttribute.addContent(((MemoryAttribute)attribute).getByteBuf(), false);
      }
      
      attribute = diskAttribute;
    }
    
    attribute.addContent(buffer, last);
  }
  
  public void delete()
  {
    attribute.delete();
  }
  
  public byte[] get() throws IOException
  {
    return attribute.get();
  }
  
  public ByteBuf getByteBuf() throws IOException
  {
    return attribute.getByteBuf();
  }
  
  public Charset getCharset()
  {
    return attribute.getCharset();
  }
  
  public String getString() throws IOException
  {
    return attribute.getString();
  }
  
  public String getString(Charset encoding) throws IOException
  {
    return attribute.getString(encoding);
  }
  
  public boolean isCompleted()
  {
    return attribute.isCompleted();
  }
  
  public boolean isInMemory()
  {
    return attribute.isInMemory();
  }
  
  public long length()
  {
    return attribute.length();
  }
  
  public boolean renameTo(File dest) throws IOException
  {
    return attribute.renameTo(dest);
  }
  
  public void setCharset(Charset charset)
  {
    attribute.setCharset(charset);
  }
  
  public void setContent(ByteBuf buffer) throws IOException
  {
    if ((buffer.readableBytes() > limitSize) && 
      ((attribute instanceof MemoryAttribute)))
    {
      attribute = new DiskAttribute(attribute.getName());
    }
    
    attribute.setContent(buffer);
  }
  
  public void setContent(File file) throws IOException
  {
    if ((file.length() > limitSize) && 
      ((attribute instanceof MemoryAttribute)))
    {
      attribute = new DiskAttribute(attribute.getName());
    }
    
    attribute.setContent(file);
  }
  
  public void setContent(InputStream inputStream) throws IOException
  {
    if ((attribute instanceof MemoryAttribute))
    {
      attribute = new DiskAttribute(attribute.getName());
    }
    attribute.setContent(inputStream);
  }
  
  public InterfaceHttpData.HttpDataType getHttpDataType()
  {
    return attribute.getHttpDataType();
  }
  
  public String getName()
  {
    return attribute.getName();
  }
  
  public int compareTo(InterfaceHttpData o)
  {
    return attribute.compareTo(o);
  }
  
  public String toString()
  {
    return "Mixed: " + attribute.toString();
  }
  
  public String getValue() throws IOException
  {
    return attribute.getValue();
  }
  
  public void setValue(String value) throws IOException
  {
    attribute.setValue(value);
  }
  
  public ByteBuf getChunk(int length) throws IOException
  {
    return attribute.getChunk(length);
  }
  
  public File getFile() throws IOException
  {
    return attribute.getFile();
  }
  
  public Attribute copy()
  {
    return attribute.copy();
  }
  
  public Attribute duplicate()
  {
    return attribute.duplicate();
  }
  
  public ByteBuf content()
  {
    return attribute.content();
  }
  
  public int refCnt()
  {
    return attribute.refCnt();
  }
  
  public Attribute retain()
  {
    attribute.retain();
    return this;
  }
  
  public Attribute retain(int increment)
  {
    attribute.retain(increment);
    return this;
  }
  
  public boolean release()
  {
    return attribute.release();
  }
  
  public boolean release(int decrement)
  {
    return attribute.release(decrement);
  }
}
