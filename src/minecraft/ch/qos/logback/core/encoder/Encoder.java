package ch.qos.logback.core.encoder;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import java.io.IOException;
import java.io.OutputStream;

public abstract interface Encoder<E>
  extends ContextAware, LifeCycle
{
  public abstract void init(OutputStream paramOutputStream)
    throws IOException;
  
  public abstract void doEncode(E paramE)
    throws IOException;
  
  public abstract void close()
    throws IOException;
}
