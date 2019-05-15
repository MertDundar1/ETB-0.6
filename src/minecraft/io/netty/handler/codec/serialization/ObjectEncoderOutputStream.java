package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;



























public class ObjectEncoderOutputStream
  extends OutputStream
  implements ObjectOutput
{
  private final DataOutputStream out;
  private final int estimatedLength;
  
  public ObjectEncoderOutputStream(OutputStream out)
  {
    this(out, 512);
  }
  














  public ObjectEncoderOutputStream(OutputStream out, int estimatedLength)
  {
    if (out == null) {
      throw new NullPointerException("out");
    }
    if (estimatedLength < 0) {
      throw new IllegalArgumentException("estimatedLength: " + estimatedLength);
    }
    
    if ((out instanceof DataOutputStream)) {
      this.out = ((DataOutputStream)out);
    } else {
      this.out = new DataOutputStream(out);
    }
    this.estimatedLength = estimatedLength;
  }
  
  public void writeObject(Object obj) throws IOException
  {
    ByteBufOutputStream bout = new ByteBufOutputStream(Unpooled.buffer(estimatedLength));
    ObjectOutputStream oout = new CompactObjectOutputStream(bout);
    oout.writeObject(obj);
    oout.flush();
    oout.close();
    
    ByteBuf buffer = bout.buffer();
    int objectSize = buffer.readableBytes();
    writeInt(objectSize);
    buffer.getBytes(0, this, objectSize);
  }
  
  public void write(int b) throws IOException
  {
    out.write(b);
  }
  
  public void close() throws IOException
  {
    out.close();
  }
  
  public void flush() throws IOException
  {
    out.flush();
  }
  
  public final int size() {
    return out.size();
  }
  
  public void write(byte[] b, int off, int len) throws IOException
  {
    out.write(b, off, len);
  }
  
  public void write(byte[] b) throws IOException
  {
    out.write(b);
  }
  
  public final void writeBoolean(boolean v) throws IOException
  {
    out.writeBoolean(v);
  }
  
  public final void writeByte(int v) throws IOException
  {
    out.writeByte(v);
  }
  
  public final void writeBytes(String s) throws IOException
  {
    out.writeBytes(s);
  }
  
  public final void writeChar(int v) throws IOException
  {
    out.writeChar(v);
  }
  
  public final void writeChars(String s) throws IOException
  {
    out.writeChars(s);
  }
  
  public final void writeDouble(double v) throws IOException
  {
    out.writeDouble(v);
  }
  
  public final void writeFloat(float v) throws IOException
  {
    out.writeFloat(v);
  }
  
  public final void writeInt(int v) throws IOException
  {
    out.writeInt(v);
  }
  
  public final void writeLong(long v) throws IOException
  {
    out.writeLong(v);
  }
  
  public final void writeShort(int v) throws IOException
  {
    out.writeShort(v);
  }
  
  public final void writeUTF(String str) throws IOException
  {
    out.writeUTF(str);
  }
}
