package ch.qos.logback.core;

import ch.qos.logback.core.spi.ContextAwareBase;










public abstract class LayoutBase<E>
  extends ContextAwareBase
  implements Layout<E>
{
  protected boolean started;
  String fileHeader;
  String fileFooter;
  String presentationHeader;
  String presentationFooter;
  
  public LayoutBase() {}
  
  public void setContext(Context context)
  {
    this.context = context;
  }
  
  public Context getContext() {
    return context;
  }
  
  public void start() {
    started = true;
  }
  
  public void stop() {
    started = false;
  }
  
  public boolean isStarted() {
    return started;
  }
  
  public String getFileHeader() {
    return fileHeader;
  }
  
  public String getPresentationHeader() {
    return presentationHeader;
  }
  
  public String getPresentationFooter() {
    return presentationFooter;
  }
  
  public String getFileFooter() {
    return fileFooter;
  }
  
  public String getContentType() {
    return "text/plain";
  }
  
  public void setFileHeader(String header) {
    fileHeader = header;
  }
  
  public void setFileFooter(String footer) {
    fileFooter = footer;
  }
  
  public void setPresentationHeader(String header) {
    presentationHeader = header;
  }
  
  public void setPresentationFooter(String footer) {
    presentationFooter = footer;
  }
}
