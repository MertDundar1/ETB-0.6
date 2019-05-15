package ch.qos.logback.classic;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import org.slf4j.LoggerFactory;























public class BasicConfigurator
{
  static final BasicConfigurator hiddenSingleton = new BasicConfigurator();
  
  private BasicConfigurator() {}
  
  public static void configure(LoggerContext lc)
  {
    StatusManager sm = lc.getStatusManager();
    if (sm != null) {
      sm.add(new InfoStatus("Setting up default configuration.", lc));
    }
    ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender();
    ca.setContext(lc);
    ca.setName("console");
    PatternLayoutEncoder pl = new PatternLayoutEncoder();
    pl.setContext(lc);
    pl.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
    pl.start();
    
    ca.setEncoder(pl);
    ca.start();
    Logger rootLogger = lc.getLogger("ROOT");
    rootLogger.addAppender(ca);
  }
  
  public static void configureDefaultContext() {
    LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
    configure(lc);
  }
}
