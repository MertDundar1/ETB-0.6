package org.apache.log4j;

import org.apache.log4j.spi.OptionHandler;

public class AppenderSkeleton
  implements OptionHandler
{
  public AppenderSkeleton() {}
  
  public void setLayout(Layout layout) {}
  
  public void setName(String name) {}
  
  public void activateOptions() {}
  
  public void setThreshold(Priority threshold) {}
}
