package shadersmod.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public abstract class SMCLog
{
  public static final String smcLogName = "SMC";
  public static final Logger logger = new SMCLogger("SMC");
  public static final Level SMCINFO = new SMCLevel("INF", 850, null);
  public static final Level SMCCONFIG = new SMCLevel("CFG", 840, null);
  public static final Level SMCFINE = new SMCLevel("FNE", 830, null);
  public static final Level SMCFINER = new SMCLevel("FNR", 820, null);
  public static final Level SMCFINEST = new SMCLevel("FNT", 810, null);
  
  public SMCLog() {}
  
  public static void log(Level level, String message) { if (logger.isLoggable(level))
    {
      logger.log(level, message);
    }
  }
  
  public static void severe(String message)
  {
    if (logger.isLoggable(Level.SEVERE))
    {
      logger.log(Level.SEVERE, message);
    }
  }
  
  public static void warning(String message)
  {
    if (logger.isLoggable(Level.WARNING))
    {
      logger.log(Level.WARNING, message);
    }
  }
  
  public static void info(String message)
  {
    if (logger.isLoggable(SMCINFO))
    {
      logger.log(SMCINFO, message);
    }
  }
  
  public static void config(String message)
  {
    if (logger.isLoggable(SMCCONFIG))
    {
      logger.log(SMCCONFIG, message);
    }
  }
  
  public static void fine(String message)
  {
    if (logger.isLoggable(SMCFINE))
    {
      logger.log(SMCFINE, message);
    }
  }
  
  public static void finer(String message)
  {
    if (logger.isLoggable(SMCFINER))
    {
      logger.log(SMCFINER, message);
    }
  }
  
  public static void finest(String message)
  {
    if (logger.isLoggable(SMCFINEST))
    {
      logger.log(SMCFINEST, message);
    }
  }
  
  public static void log(Level level, String format, Object... args)
  {
    if (logger.isLoggable(level))
    {
      logger.log(level, String.format(format, args));
    }
  }
  
  public static void severe(String format, Object... args)
  {
    if (logger.isLoggable(Level.SEVERE))
    {
      logger.log(Level.SEVERE, String.format(format, args));
    }
  }
  
  public static void warning(String format, Object... args)
  {
    if (logger.isLoggable(Level.WARNING))
    {
      logger.log(Level.WARNING, String.format(format, args));
    }
  }
  
  public static void info(String format, Object... args)
  {
    if (logger.isLoggable(SMCINFO))
    {
      logger.log(SMCINFO, String.format(format, args));
    }
  }
  
  public static void config(String format, Object... args)
  {
    if (logger.isLoggable(SMCCONFIG))
    {
      logger.log(SMCCONFIG, String.format(format, args));
    }
  }
  
  public static void fine(String format, Object... args)
  {
    if (logger.isLoggable(SMCFINE))
    {
      logger.log(SMCFINE, String.format(format, args));
    }
  }
  
  public static void finer(String format, Object... args)
  {
    if (logger.isLoggable(SMCFINER))
    {
      logger.log(SMCFINER, String.format(format, args));
    }
  }
  
  public static void finest(String format, Object... args)
  {
    if (logger.isLoggable(SMCFINEST))
    {
      logger.log(SMCFINEST, String.format(format, args));
    }
  }
  
  static class NamelessClass763038833
  {
    NamelessClass763038833() {}
  }
  
  private static class SMCFormatter extends Formatter {
    int tzOffset = java.util.Calendar.getInstance().getTimeZone().getRawOffset();
    
    private SMCFormatter() {}
    
    public String format(LogRecord record) { StringBuilder sb = new StringBuilder();
      sb.append("[");
      sb.append("Shaders").append("]");
      
      if (record.getLevel() != SMCLog.SMCINFO)
      {
        sb.append("[").append(record.getLevel()).append("]");
      }
      
      sb.append(" ");
      sb.append(record.getMessage()).append("\n");
      return sb.toString();
    }
  }
  
  private static class SMCLevel extends Level
  {
    private SMCLevel(String name, int value)
    {
      super(value);
    }
    
    SMCLevel(String x0, int x1, SMCLog.NamelessClass763038833 x2)
    {
      this(x0, x1);
    }
  }
  
  private static class SMCLogger extends Logger
  {
    SMCLogger(String name)
    {
      super(null);
      setUseParentHandlers(false);
      SMCLog.SMCFormatter formatter = new SMCLog.SMCFormatter(null);
      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(formatter);
      handler.setLevel(Level.ALL);
      addHandler(handler);
      
      try
      {
        FileOutputStream e = new FileOutputStream("logs/shadersmod.log", false);
        StreamHandler handler1 = new StreamHandler(e, formatter)
        {

          public synchronized void publish(LogRecord record)
          {
            super.publish(record);
            flush();
          }
        };
        handler1.setFormatter(formatter);
        handler1.setLevel(Level.ALL);
        addHandler(handler1);
      }
      catch (IOException var5)
      {
        var5.printStackTrace();
      }
      
      setLevel(Level.ALL);
    }
  }
}
