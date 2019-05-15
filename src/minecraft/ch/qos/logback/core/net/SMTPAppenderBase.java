package ch.qos.logback.core.net;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.sift.DefaultDiscriminator;
import ch.qos.logback.core.sift.Discriminator;
import ch.qos.logback.core.spi.CyclicBufferTracker;
import ch.qos.logback.core.util.ContentTypeUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;































public abstract class SMTPAppenderBase<E>
  extends AppenderBase<E>
{
  static InternetAddress[] EMPTY_IA_ARRAY = new InternetAddress[0];
  static final int MAX_DELAY_BETWEEN_STATUS_MESSAGES = 1228800000;
  
  public SMTPAppenderBase() {
    lastTrackerStatusPrint = 0L;
    delayBetweenStatusMessages = 300000;
    



    toPatternLayoutList = new ArrayList();
    
    subjectStr = null;
    
    smtpPort = 25;
    starttls = false;
    ssl = false;
    sessionViaJNDI = false;
    jndiLocation = "java:comp/env/mail/Session";
    





    asynchronousSending = true;
    
    charsetEncoding = "UTF-8";
    




    discriminator = new DefaultDiscriminator();
    

    errorCount = 0;
  }
  
  long lastTrackerStatusPrint;
  int delayBetweenStatusMessages;
  protected Layout<E> subjectLayout;
  protected Layout<E> layout;
  private List<PatternLayoutBase<E>> toPatternLayoutList;
  private String from;
  private String subjectStr;
  private String smtpHost;
  private int smtpPort;
  private boolean starttls;
  private boolean ssl;
  protected abstract Layout<E> makeSubjectLayout(String paramString);
  
  public void start() {
    if (cbTracker == null) {
      cbTracker = new CyclicBufferTracker();
    }
    
    if (sessionViaJNDI) {
      session = lookupSessionInJNDI();
    } else {
      session = buildSessionFromProperties();
    }
    if (session == null) {
      addError("Failed to obtain javax.mail.Session. Cannot start.");
      return;
    }
    
    subjectLayout = makeSubjectLayout(subjectStr);
    
    started = true;
  }
  
  private Session lookupSessionInJNDI() {
    addInfo("Looking up javax.mail.Session at JNDI location [" + jndiLocation + "]");
    try {
      javax.naming.Context initialContext = new InitialContext();
      Object obj = initialContext.lookup(jndiLocation);
      return (Session)obj;
    } catch (Exception e) {
      addError("Failed to obtain javax.mail.Session from JNDI location [" + jndiLocation + "]"); }
    return null;
  }
  
  private Session buildSessionFromProperties()
  {
    Properties props = new Properties(OptionHelper.getSystemProperties());
    if (smtpHost != null) {
      props.put("mail.smtp.host", smtpHost);
    }
    props.put("mail.smtp.port", Integer.toString(smtpPort));
    
    if (localhost != null) {
      props.put("mail.smtp.localhost", localhost);
    }
    
    LoginAuthenticator loginAuthenticator = null;
    
    if (username != null) {
      loginAuthenticator = new LoginAuthenticator(username, password);
      props.put("mail.smtp.auth", "true");
    }
    
    if ((isSTARTTLS()) && (isSSL())) {
      addError("Both SSL and StartTLS cannot be enabled simultaneously");
    } else {
      if (isSTARTTLS())
      {
        props.put("mail.smtp.starttls.enable", "true");
      }
      if (isSSL()) {
        String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.put("mail.smtp.socketFactory.port", Integer.toString(smtpPort));
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "true");
      }
    }
    


    return Session.getInstance(props, loginAuthenticator);
  }
  




  protected void append(E eventObject)
  {
    if (!checkEntryConditions()) {
      return;
    }
    
    String key = discriminator.getDiscriminatingValue(eventObject);
    long now = System.currentTimeMillis();
    CyclicBuffer<E> cb = (CyclicBuffer)cbTracker.getOrCreate(key, now);
    subAppend(cb, eventObject);
    try
    {
      if (eventEvaluator.evaluate(eventObject))
      {
        CyclicBuffer<E> cbClone = new CyclicBuffer(cb);
        
        cb.clear();
        
        if (asynchronousSending)
        {
          SMTPAppenderBase<E>.SenderRunnable senderRunnable = new SenderRunnable(cbClone, eventObject);
          context.getExecutorService().execute(senderRunnable);
        }
        else {
          sendBuffer(cbClone, eventObject);
        }
      }
    } catch (EvaluationException ex) {
      errorCount += 1;
      if (errorCount < 4) {
        addError("SMTPAppender's EventEvaluator threw an Exception-", ex);
      }
    }
    

    if (eventMarksEndOfLife(eventObject)) {
      cbTracker.endOfLife(key);
    }
    
    cbTracker.removeStaleComponents(now);
    
    if (lastTrackerStatusPrint + delayBetweenStatusMessages < now) {
      addInfo("SMTPAppender [" + name + "] is tracking [" + cbTracker.getComponentCount() + "] buffers");
      lastTrackerStatusPrint = now;
      
      if (delayBetweenStatusMessages < 1228800000) {
        delayBetweenStatusMessages *= 4;
      }
    }
  }
  



  protected abstract boolean eventMarksEndOfLife(E paramE);
  


  protected abstract void subAppend(CyclicBuffer<E> paramCyclicBuffer, E paramE);
  


  public boolean checkEntryConditions()
  {
    if (!started) {
      addError("Attempting to append to a non-started appender: " + getName());
      
      return false;
    }
    
    if (eventEvaluator == null) {
      addError("No EventEvaluator is set for appender [" + name + "].");
      return false;
    }
    
    if (layout == null) {
      addError("No layout set for appender named [" + name + "]. For more information, please visit http://logback.qos.ch/codes.html#smtp_no_layout");
      

      return false;
    }
    return true;
  }
  
  public synchronized void stop() {
    started = false;
  }
  
  InternetAddress getAddress(String addressStr) {
    try {
      return new InternetAddress(addressStr);
    } catch (AddressException e) {
      addError("Could not parse address [" + addressStr + "].", e); }
    return null;
  }
  
  private List<InternetAddress> parseAddress(E event)
  {
    int len = toPatternLayoutList.size();
    
    List<InternetAddress> iaList = new ArrayList();
    
    for (int i = 0; i < len; i++) {
      try {
        PatternLayoutBase<E> emailPL = (PatternLayoutBase)toPatternLayoutList.get(i);
        String emailAdrr = emailPL.doLayout(event);
        if ((emailAdrr == null) || (emailAdrr.length() != 0))
        {

          InternetAddress[] tmp = InternetAddress.parse(emailAdrr, true);
          iaList.addAll(Arrays.asList(tmp));
        }
      } catch (AddressException e) { addError("Could not parse email address for [" + toPatternLayoutList.get(i) + "] for event [" + event + "]", e);
        return iaList;
      }
    }
    
    return iaList;
  }
  


  public List<PatternLayoutBase<E>> getToList()
  {
    return toPatternLayoutList;
  }
  




  protected void sendBuffer(CyclicBuffer<E> cb, E lastEventObject)
  {
    try
    {
      MimeBodyPart part = new MimeBodyPart();
      
      StringBuffer sbuf = new StringBuffer();
      
      String header = layout.getFileHeader();
      if (header != null) {
        sbuf.append(header);
      }
      String presentationHeader = layout.getPresentationHeader();
      if (presentationHeader != null) {
        sbuf.append(presentationHeader);
      }
      fillBuffer(cb, sbuf);
      String presentationFooter = layout.getPresentationFooter();
      if (presentationFooter != null) {
        sbuf.append(presentationFooter);
      }
      String footer = layout.getFileFooter();
      if (footer != null) {
        sbuf.append(footer);
      }
      
      String subjectStr = "Undefined subject";
      if (subjectLayout != null) {
        subjectStr = subjectLayout.doLayout(lastEventObject);
        



        int newLinePos = subjectStr != null ? subjectStr.indexOf('\n') : -1;
        if (newLinePos > -1) {
          subjectStr = subjectStr.substring(0, newLinePos);
        }
      }
      
      MimeMessage mimeMsg = new MimeMessage(session);
      
      if (from != null) {
        mimeMsg.setFrom(getAddress(from));
      } else {
        mimeMsg.setFrom();
      }
      
      mimeMsg.setSubject(subjectStr, charsetEncoding);
      
      List<InternetAddress> destinationAddresses = parseAddress(lastEventObject);
      if (destinationAddresses.isEmpty()) {
        addInfo("Empty destination address. Aborting email transmission");
        return;
      }
      
      InternetAddress[] toAddressArray = (InternetAddress[])destinationAddresses.toArray(EMPTY_IA_ARRAY);
      mimeMsg.setRecipients(Message.RecipientType.TO, toAddressArray);
      
      String contentType = layout.getContentType();
      
      if (ContentTypeUtil.isTextual(contentType)) {
        part.setText(sbuf.toString(), charsetEncoding, ContentTypeUtil.getSubType(contentType));
      }
      else {
        part.setContent(sbuf.toString(), layout.getContentType());
      }
      
      Multipart mp = new MimeMultipart();
      mp.addBodyPart(part);
      mimeMsg.setContent(mp);
      
      mimeMsg.setSentDate(new Date());
      addInfo("About to send out SMTP message \"" + subjectStr + "\" to " + Arrays.toString(toAddressArray));
      Transport.send(mimeMsg);
    } catch (Exception e) {
      addError("Error occurred while sending e-mail notification.", e);
    }
  }
  

  protected abstract void fillBuffer(CyclicBuffer<E> paramCyclicBuffer, StringBuffer paramStringBuffer);
  

  public String getFrom()
  {
    return from;
  }
  


  public String getSubject()
  {
    return subjectStr;
  }
  



  public void setFrom(String from)
  {
    this.from = from;
  }
  



  public void setSubject(String subject)
  {
    subjectStr = subject;
  }
  




  public void setSMTPHost(String smtpHost)
  {
    setSmtpHost(smtpHost);
  }
  



  public void setSmtpHost(String smtpHost)
  {
    this.smtpHost = smtpHost;
  }
  


  public String getSMTPHost()
  {
    return getSmtpHost();
  }
  


  public String getSmtpHost()
  {
    return smtpHost;
  }
  




  public void setSMTPPort(int port)
  {
    setSmtpPort(port);
  }
  




  public void setSmtpPort(int port)
  {
    smtpPort = port;
  }
  




  public int getSMTPPort()
  {
    return getSmtpPort();
  }
  




  public int getSmtpPort()
  {
    return smtpPort;
  }
  

  public String getLocalhost() { return localhost; }
  
  private boolean sessionViaJNDI;
  private String jndiLocation;
  String username;
  String password;
  String localhost;
  boolean asynchronousSending;
  private String charsetEncoding;
  protected Session session;
  protected EventEvaluator<E> eventEvaluator;
  protected Discriminator<E> discriminator;
  protected CyclicBufferTracker<E> cbTracker;
  private int errorCount;
  public void setLocalhost(String localhost) { this.localhost = localhost; }
  

  public CyclicBufferTracker<E> getCyclicBufferTracker() {
    return cbTracker;
  }
  
  public void setCyclicBufferTracker(CyclicBufferTracker<E> cbTracker) {
    this.cbTracker = cbTracker;
  }
  
  public Discriminator<E> getDiscriminator() {
    return discriminator;
  }
  
  public void setDiscriminator(Discriminator<E> discriminator) {
    this.discriminator = discriminator;
  }
  
  public boolean isAsynchronousSending() {
    return asynchronousSending;
  }
  






  public void setAsynchronousSending(boolean asynchronousSending)
  {
    this.asynchronousSending = asynchronousSending;
  }
  
  public void addTo(String to) {
    if ((to == null) || (to.length() == 0)) {
      throw new IllegalArgumentException("Null or empty <to> property");
    }
    PatternLayoutBase plb = makeNewToPatternLayout(to.trim());
    plb.setContext(context);
    plb.start();
    toPatternLayoutList.add(plb);
  }
  
  protected abstract PatternLayoutBase<E> makeNewToPatternLayout(String paramString);
  
  public List<String> getToAsListOfString() {
    List<String> toList = new ArrayList();
    for (PatternLayoutBase plb : toPatternLayoutList) {
      toList.add(plb.getPattern());
    }
    return toList;
  }
  
  public boolean isSTARTTLS() {
    return starttls;
  }
  
  public void setSTARTTLS(boolean startTLS) {
    starttls = startTLS;
  }
  
  public boolean isSSL() {
    return ssl;
  }
  
  public void setSSL(boolean ssl) {
    this.ssl = ssl;
  }
  





  public void setEvaluator(EventEvaluator<E> eventEvaluator)
  {
    this.eventEvaluator = eventEvaluator;
  }
  
  public String getUsername() {
    return username;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  



  public String getCharsetEncoding()
  {
    return charsetEncoding;
  }
  
  public String getJndiLocation()
  {
    return jndiLocation;
  }
  






  public void setJndiLocation(String jndiLocation)
  {
    this.jndiLocation = jndiLocation;
  }
  
  public boolean isSessionViaJNDI() {
    return sessionViaJNDI;
  }
  





  public void setSessionViaJNDI(boolean sessionViaJNDI)
  {
    this.sessionViaJNDI = sessionViaJNDI;
  }
  





  public void setCharsetEncoding(String charsetEncoding)
  {
    this.charsetEncoding = charsetEncoding;
  }
  
  public Layout<E> getLayout() {
    return layout;
  }
  
  public void setLayout(Layout<E> layout) {
    this.layout = layout;
  }
  
  class SenderRunnable implements Runnable
  {
    final CyclicBuffer<E> cyclicBuffer;
    final E e;
    
    SenderRunnable(E cyclicBuffer) {
      this.cyclicBuffer = cyclicBuffer;
      this.e = e;
    }
    
    public void run() {
      sendBuffer(cyclicBuffer, e);
    }
  }
}
