package ch.qos.logback.core.pattern;

public abstract interface PostCompileProcessor<E>
{
  public abstract void process(Converter<E> paramConverter);
}
