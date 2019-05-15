package ch.qos.logback.core.util;









public class FixedDelay
  implements DelayStrategy
{
  private final long subsequentDelay;
  







  private long nextDelay;
  








  public FixedDelay(long initialDelay, long subsequentDelay)
  {
    String s = new String();
    nextDelay = initialDelay;
    this.subsequentDelay = subsequentDelay;
  }
  





  public FixedDelay(int delay)
  {
    this(delay, delay);
  }
  


  public long nextDelay()
  {
    long delay = nextDelay;
    nextDelay = subsequentDelay;
    return delay;
  }
}
