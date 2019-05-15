package ch.qos.logback.core.joran.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;





















public enum ConsoleTarget
{
  SystemOut("System.out", new OutputStream()), 
  

















  SystemErr("System.err", new OutputStream());
  





  private final String name;
  



  private final OutputStream stream;
  




  public static ConsoleTarget findByName(String name)
  {
    for (ConsoleTarget target : ) {
      if (name.equalsIgnoreCase(name)) {
        return target;
      }
    }
    return null;
  }
  


  private ConsoleTarget(String name, OutputStream stream)
  {
    this.name = name;
    this.stream = stream;
  }
  
  public String getName() {
    return name;
  }
  
  public OutputStream getStream() {
    return stream;
  }
}
