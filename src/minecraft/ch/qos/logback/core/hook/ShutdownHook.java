package ch.qos.logback.core.hook;

import ch.qos.logback.core.spi.ContextAware;

public abstract interface ShutdownHook
  extends Runnable, ContextAware
{}
