package ch.qos.logback.core.net.server;

public abstract interface ClientVisitor<T extends Client>
{
  public abstract void visit(T paramT);
}
