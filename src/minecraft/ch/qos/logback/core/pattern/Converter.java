package ch.qos.logback.core.pattern;









public abstract class Converter<E>
{
  Converter<E> next;
  







  public Converter() {}
  







  public abstract String convert(E paramE);
  







  public void write(StringBuilder buf, E event)
  {
    buf.append(convert(event));
  }
  
  public final void setNext(Converter<E> next) {
    if (this.next != null) {
      throw new IllegalStateException("Next converter has been already set");
    }
    this.next = next;
  }
  
  public final Converter<E> getNext() {
    return next;
  }
}
