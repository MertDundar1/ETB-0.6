package ch.qos.logback.core;

import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import ch.qos.logback.core.status.ErrorStatus;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;


































public class OutputStreamAppender<E>
  extends UnsynchronizedAppenderBase<E>
{
  protected Encoder<E> encoder;
  protected final ReentrantLock lock = new ReentrantLock(true);
  

  private OutputStream outputStream;
  


  public OutputStreamAppender() {}
  


  public OutputStream getOutputStream()
  {
    return outputStream;
  }
  



  public void start()
  {
    int errors = 0;
    if (encoder == null) {
      addStatus(new ErrorStatus("No encoder set for the appender named \"" + name + "\".", this));
      
      errors++;
    }
    
    if (outputStream == null) {
      addStatus(new ErrorStatus("No output stream set for the appender named \"" + name + "\".", this));
      
      errors++;
    }
    
    if (errors == 0) {
      super.start();
    }
  }
  
  public void setLayout(Layout<E> layout) {
    addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
    addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
    addWarn("See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details");
    LayoutWrappingEncoder<E> lwe = new LayoutWrappingEncoder();
    lwe.setLayout(layout);
    lwe.setContext(context);
    encoder = lwe;
  }
  
  protected void append(E eventObject)
  {
    if (!isStarted()) {
      return;
    }
    
    subAppend(eventObject);
  }
  






  public void stop()
  {
    lock.lock();
    try {
      closeOutputStream();
      super.stop();
    } finally {
      lock.unlock();
    }
  }
  


  protected void closeOutputStream()
  {
    if (outputStream != null) {
      try
      {
        encoderClose();
        outputStream.close();
        outputStream = null;
      } catch (IOException e) {
        addStatus(new ErrorStatus("Could not close output stream for OutputStreamAppender.", this, e));
      }
    }
  }
  
  void encoderInit()
  {
    if ((encoder != null) && (outputStream != null)) {
      try {
        encoder.init(outputStream);
      } catch (IOException ioe) {
        started = false;
        addStatus(new ErrorStatus("Failed to initialize encoder for appender named [" + name + "].", this, ioe));
      }
    }
  }
  

  void encoderClose()
  {
    if ((encoder != null) && (outputStream != null)) {
      try {
        encoder.close();
      } catch (IOException ioe) {
        started = false;
        addStatus(new ErrorStatus("Failed to write footer for appender named [" + name + "].", this, ioe));
      }
    }
  }
  










  public void setOutputStream(OutputStream outputStream)
  {
    lock.lock();
    try
    {
      closeOutputStream();
      
      this.outputStream = outputStream;
      if (encoder == null) {
        addWarn("Encoder has not been set. Cannot invoke its init method.");
      }
      else
      {
        encoderInit(); }
    } finally {
      lock.unlock();
    }
  }
  
  protected void writeOut(E event) throws IOException {
    encoder.doEncode(event);
  }
  







  protected void subAppend(E event)
  {
    if (!isStarted()) {
      return;
    }
    try
    {
      if ((event instanceof DeferredProcessingAware)) {
        ((DeferredProcessingAware)event).prepareForDeferredProcessing();
      }
      


      lock.lock();
      try {
        writeOut(event);
      } finally {
        lock.unlock();
      }
    }
    catch (IOException ioe)
    {
      started = false;
      addStatus(new ErrorStatus("IO failure in appender", this, ioe));
    }
  }
  
  public Encoder<E> getEncoder() {
    return encoder;
  }
  
  public void setEncoder(Encoder<E> encoder) {
    this.encoder = encoder;
  }
}
