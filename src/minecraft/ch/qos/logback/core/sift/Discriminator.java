package ch.qos.logback.core.sift;

import ch.qos.logback.core.spi.LifeCycle;

public abstract interface Discriminator<E>
  extends LifeCycle
{
  public abstract String getDiscriminatingValue(E paramE);
  
  public abstract String getKey();
}
