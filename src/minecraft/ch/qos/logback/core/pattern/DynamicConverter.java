package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.Status;
import java.util.List;














public abstract class DynamicConverter<E>
  extends FormattingConverter<E>
  implements LifeCycle, ContextAware
{
  ContextAwareBase cab = new ContextAwareBase(this);
  


  private List<String> optionList;
  


  protected boolean started = false;
  


  public DynamicConverter() {}
  

  public void start()
  {
    started = true;
  }
  
  public void stop() {
    started = false;
  }
  
  public boolean isStarted() {
    return started;
  }
  
  public void setOptionList(List<String> optionList) {
    this.optionList = optionList;
  }
  





  public String getFirstOption()
  {
    if ((optionList == null) || (optionList.size() == 0)) {
      return null;
    }
    return (String)optionList.get(0);
  }
  
  protected List<String> getOptionList()
  {
    return optionList;
  }
  
  public void setContext(Context context) {
    cab.setContext(context);
  }
  
  public Context getContext() {
    return cab.getContext();
  }
  
  public void addStatus(Status status) {
    cab.addStatus(status);
  }
  
  public void addInfo(String msg) {
    cab.addInfo(msg);
  }
  
  public void addInfo(String msg, Throwable ex) {
    cab.addInfo(msg, ex);
  }
  
  public void addWarn(String msg) {
    cab.addWarn(msg);
  }
  
  public void addWarn(String msg, Throwable ex) {
    cab.addWarn(msg, ex);
  }
  
  public void addError(String msg) {
    cab.addError(msg);
  }
  
  public void addError(String msg, Throwable ex) {
    cab.addError(msg, ex);
  }
}
