package ch.qos.logback.core.net;

import java.io.IOException;
import java.io.ObjectOutputStream;



















public class AutoFlushingObjectWriter
  implements ObjectWriter
{
  private final ObjectOutputStream objectOutputStream;
  private final int resetFrequency;
  private int writeCounter = 0;
  






  public AutoFlushingObjectWriter(ObjectOutputStream objectOutputStream, int resetFrequency)
  {
    this.objectOutputStream = objectOutputStream;
    this.resetFrequency = resetFrequency;
  }
  
  public void write(Object object) throws IOException
  {
    objectOutputStream.writeObject(object);
    objectOutputStream.flush();
    preventMemoryLeak();
  }
  


  private void preventMemoryLeak()
    throws IOException
  {
    if (++writeCounter >= resetFrequency) {
      objectOutputStream.reset();
      writeCounter = 0;
    }
  }
}
