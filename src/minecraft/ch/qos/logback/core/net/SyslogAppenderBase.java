package ch.qos.logback.core.net;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
























public abstract class SyslogAppenderBase<E>
  extends AppenderBase<E>
{
  static final String SYSLOG_LAYOUT_URL = "http://logback.qos.ch/codes.html#syslog_layout";
  static final int MAX_MESSAGE_SIZE_LIMIT = 65000;
  Layout<E> layout;
  String facilityStr;
  String syslogHost;
  protected String suffixPattern;
  SyslogOutputStream sos;
  int port = 514;
  int maxMessageSize;
  
  public SyslogAppenderBase() {}
  
  public void start() { int errorCount = 0;
    if (facilityStr == null) {
      addError("The Facility option is mandatory");
      errorCount++;
    }
    
    if (charset == null)
    {

      charset = Charset.defaultCharset();
    }
    try
    {
      sos = createOutputStream();
      
      int systemDatagramSize = sos.getSendBufferSize();
      if (maxMessageSize == 0) {
        maxMessageSize = Math.min(systemDatagramSize, 65000);
        addInfo("Defaulting maxMessageSize to [" + maxMessageSize + "]");
      } else if (maxMessageSize > systemDatagramSize) {
        addWarn("maxMessageSize of [" + maxMessageSize + "] is larger than the system defined datagram size of [" + systemDatagramSize + "].");
        addWarn("This may result in dropped logs.");
      }
    } catch (UnknownHostException e) {
      addError("Could not create SyslogWriter", e);
      errorCount++;
    } catch (SocketException e) {
      addWarn("Failed to bind to a random datagram socket. Will try to reconnect later.", e);
    }
    


    if (layout == null) {
      layout = buildLayout();
    }
    
    if (errorCount == 0) {
      super.start();
    }
  }
  
  public abstract SyslogOutputStream createOutputStream() throws UnknownHostException, SocketException;
  
  public abstract Layout<E> buildLayout();
  
  public abstract int getSeverityForEvent(Object paramObject);
  
  protected void append(E eventObject)
  {
    if (!isStarted()) {
      return;
    }
    try
    {
      String msg = layout.doLayout(eventObject);
      if (msg == null) {
        return;
      }
      if (msg.length() > maxMessageSize) {
        msg = msg.substring(0, maxMessageSize);
      }
      sos.write(msg.getBytes(charset));
      sos.flush();
      postProcess(eventObject, sos);
    } catch (IOException ioe) {
      addError("Failed to send diagram to " + syslogHost, ioe);
    }
  }
  




  protected void postProcess(Object event, OutputStream sw) {}
  



  public static int facilityStringToint(String facilityStr)
  {
    if ("KERN".equalsIgnoreCase(facilityStr))
      return 0;
    if ("USER".equalsIgnoreCase(facilityStr))
      return 8;
    if ("MAIL".equalsIgnoreCase(facilityStr))
      return 16;
    if ("DAEMON".equalsIgnoreCase(facilityStr))
      return 24;
    if ("AUTH".equalsIgnoreCase(facilityStr))
      return 32;
    if ("SYSLOG".equalsIgnoreCase(facilityStr))
      return 40;
    if ("LPR".equalsIgnoreCase(facilityStr))
      return 48;
    if ("NEWS".equalsIgnoreCase(facilityStr))
      return 56;
    if ("UUCP".equalsIgnoreCase(facilityStr))
      return 64;
    if ("CRON".equalsIgnoreCase(facilityStr))
      return 72;
    if ("AUTHPRIV".equalsIgnoreCase(facilityStr))
      return 80;
    if ("FTP".equalsIgnoreCase(facilityStr))
      return 88;
    if ("NTP".equalsIgnoreCase(facilityStr))
      return 96;
    if ("AUDIT".equalsIgnoreCase(facilityStr))
      return 104;
    if ("ALERT".equalsIgnoreCase(facilityStr))
      return 112;
    if ("CLOCK".equalsIgnoreCase(facilityStr))
      return 120;
    if ("LOCAL0".equalsIgnoreCase(facilityStr))
      return 128;
    if ("LOCAL1".equalsIgnoreCase(facilityStr))
      return 136;
    if ("LOCAL2".equalsIgnoreCase(facilityStr))
      return 144;
    if ("LOCAL3".equalsIgnoreCase(facilityStr))
      return 152;
    if ("LOCAL4".equalsIgnoreCase(facilityStr))
      return 160;
    if ("LOCAL5".equalsIgnoreCase(facilityStr))
      return 168;
    if ("LOCAL6".equalsIgnoreCase(facilityStr))
      return 176;
    if ("LOCAL7".equalsIgnoreCase(facilityStr)) {
      return 184;
    }
    throw new IllegalArgumentException(facilityStr + " is not a valid syslog facility string");
  }
  




  public String getSyslogHost()
  {
    return syslogHost;
  }
  





  public void setSyslogHost(String syslogHost)
  {
    this.syslogHost = syslogHost;
  }
  




  public String getFacility()
  {
    return facilityStr;
  }
  




  Charset charset;
  



  public void setFacility(String facilityStr)
  {
    if (facilityStr != null) {
      facilityStr = facilityStr.trim();
    }
    this.facilityStr = facilityStr;
  }
  



  public int getPort()
  {
    return port;
  }
  



  public void setPort(int port)
  {
    this.port = port;
  }
  



  public int getMaxMessageSize()
  {
    return maxMessageSize;
  }
  






  public void setMaxMessageSize(int maxMessageSize)
  {
    this.maxMessageSize = maxMessageSize;
  }
  
  public Layout<E> getLayout() {
    return layout;
  }
  
  public void setLayout(Layout<E> layout) {
    addWarn("The layout of a SyslogAppender cannot be set directly. See also http://logback.qos.ch/codes.html#syslog_layout");
  }
  

  public void stop()
  {
    if (sos != null) {
      sos.close();
    }
    super.stop();
  }
  




  public String getSuffixPattern()
  {
    return suffixPattern;
  }
  





  public void setSuffixPattern(String suffixPattern)
  {
    this.suffixPattern = suffixPattern;
  }
  



  public Charset getCharset()
  {
    return charset;
  }
  




  public void setCharset(Charset charset)
  {
    this.charset = charset;
  }
}
