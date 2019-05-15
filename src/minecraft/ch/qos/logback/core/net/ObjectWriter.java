package ch.qos.logback.core.net;

import java.io.IOException;

public abstract interface ObjectWriter
{
  public abstract void write(Object paramObject)
    throws IOException;
}
