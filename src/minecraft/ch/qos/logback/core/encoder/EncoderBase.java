package ch.qos.logback.core.encoder;

import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.IOException;
import java.io.OutputStream;










public abstract class EncoderBase<E>
  extends ContextAwareBase
  implements Encoder<E>
{
  protected boolean started;
  protected OutputStream outputStream;
  
  public EncoderBase() {}
  
  public void init(OutputStream os)
    throws IOException
  {
    outputStream = os;
  }
  
  public boolean isStarted() {
    return started;
  }
  
  public void start() {
    started = true;
  }
  
  public void stop() {
    started = false;
  }
}
