package ch.qos.logback.core.spi;

import java.io.Serializable;

public abstract interface PreSerializationTransformer<E>
{
  public abstract Serializable transform(E paramE);
}
