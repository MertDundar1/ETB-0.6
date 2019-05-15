package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAware;
import java.io.IOException;

public abstract interface ServerRunner<T extends Client>
  extends ContextAware, Runnable
{
  public abstract boolean isRunning();
  
  public abstract void stop()
    throws IOException;
  
  public abstract void accept(ClientVisitor<T> paramClientVisitor);
}
