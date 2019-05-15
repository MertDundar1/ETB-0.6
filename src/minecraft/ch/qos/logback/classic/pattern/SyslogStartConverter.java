package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.LevelToSyslogSeverity;
import ch.qos.logback.core.net.SyslogAppenderBase;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;














public class SyslogStartConverter
  extends ClassicConverter
{
  long lastTimestamp = -1L;
  String timesmapStr = null;
  SimpleDateFormat simpleMonthFormat;
  SimpleDateFormat simpleTimeFormat;
  private final Calendar calendar = Calendar.getInstance(Locale.US);
  String localHostName;
  int facility;
  
  public SyslogStartConverter() {}
  
  public void start() { int errorCount = 0;
    
    String facilityStr = getFirstOption();
    if (facilityStr == null) {
      addError("was expecting a facility string as an option");
      return;
    }
    
    facility = SyslogAppenderBase.facilityStringToint(facilityStr);
    
    localHostName = getLocalHostname();
    try
    {
      simpleMonthFormat = new SimpleDateFormat("MMM", Locale.US);
      simpleTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
    } catch (IllegalArgumentException e) {
      addError("Could not instantiate SimpleDateFormat", e);
      errorCount++;
    }
    
    if (errorCount == 0) {
      super.start();
    }
  }
  
  public String convert(ILoggingEvent event) {
    StringBuilder sb = new StringBuilder();
    
    int pri = facility + LevelToSyslogSeverity.convert(event);
    
    sb.append("<");
    sb.append(pri);
    sb.append(">");
    sb.append(computeTimeStampString(event.getTimeStamp()));
    sb.append(' ');
    sb.append(localHostName);
    sb.append(' ');
    
    return sb.toString();
  }
  




  public String getLocalHostname()
  {
    try
    {
      InetAddress addr = InetAddress.getLocalHost();
      return addr.getHostName();
    } catch (UnknownHostException uhe) {
      addError("Could not determine local host name", uhe); }
    return "UNKNOWN_LOCALHOST";
  }
  
  String computeTimeStampString(long now)
  {
    synchronized (this)
    {

      if (now / 1000L != lastTimestamp) {
        lastTimestamp = (now / 1000L);
        Date nowDate = new Date(now);
        calendar.setTime(nowDate);
        timesmapStr = String.format("%s %2d %s", new Object[] { simpleMonthFormat.format(nowDate), Integer.valueOf(calendar.get(5)), simpleTimeFormat.format(nowDate) });
      }
      
      return timesmapStr;
    }
  }
}
