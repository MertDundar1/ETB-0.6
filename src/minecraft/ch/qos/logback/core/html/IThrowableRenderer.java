package ch.qos.logback.core.html;

public abstract interface IThrowableRenderer<E>
{
  public abstract void render(StringBuilder paramStringBuilder, E paramE);
}
