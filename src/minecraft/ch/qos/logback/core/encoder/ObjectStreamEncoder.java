package ch.qos.logback.core.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;




















public class ObjectStreamEncoder<E>
  extends EncoderBase<E>
{
  public static final int START_PEBBLE = 1853421169;
  public static final int STOP_PEBBLE = 640373619;
  
  public ObjectStreamEncoder() {}
  
  private int MAX_BUFFER_SIZE = 100;
  
  List<E> bufferList = new ArrayList(MAX_BUFFER_SIZE);
  
  public void doEncode(E event) throws IOException {
    bufferList.add(event);
    if (bufferList.size() == MAX_BUFFER_SIZE) {
      writeBuffer();
    }
  }
  
  void writeHeader(ByteArrayOutputStream baos, int bufferSize) {
    ByteArrayUtil.writeInt(baos, 1853421169);
    ByteArrayUtil.writeInt(baos, bufferSize);
    ByteArrayUtil.writeInt(baos, 0);
    ByteArrayUtil.writeInt(baos, 0x6E78F671 ^ bufferSize);
  }
  
  void writeFooter(ByteArrayOutputStream baos, int bufferSize) {
    ByteArrayUtil.writeInt(baos, 640373619);
    ByteArrayUtil.writeInt(baos, 0x262B5373 ^ bufferSize);
  }
  
  void writeBuffer() throws IOException { ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
    
    int size = bufferList.size();
    writeHeader(baos, size);
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    for (E e : bufferList) {
      oos.writeObject(e);
    }
    bufferList.clear();
    oos.flush();
    
    writeFooter(baos, size);
    
    byte[] byteArray = baos.toByteArray();
    oos.close();
    writeEndPosition(byteArray);
    outputStream.write(byteArray);
  }
  
  void writeEndPosition(byte[] byteArray)
  {
    int offset = 8;
    ByteArrayUtil.writeInt(byteArray, offset, byteArray.length - offset);
  }
  
  public void init(OutputStream os) throws IOException
  {
    super.init(os);
    bufferList.clear();
  }
  
  public void close() throws IOException {
    writeBuffer();
  }
}
