package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.slf4j.Marker;



















































































































public class DynamicThresholdFilter
  extends TurboFilter
{
  private Map<String, Level> valueLevelMap = new HashMap();
  private Level defaultThreshold = Level.ERROR;
  
  private String key;
  private FilterReply onHigherOrEqual = FilterReply.NEUTRAL;
  private FilterReply onLower = FilterReply.DENY;
  

  public DynamicThresholdFilter() {}
  

  public String getKey()
  {
    return key;
  }
  


  public void setKey(String key)
  {
    this.key = key;
  }
  




  public Level getDefaultThreshold()
  {
    return defaultThreshold;
  }
  
  public void setDefaultThreshold(Level defaultThreshold) {
    this.defaultThreshold = defaultThreshold;
  }
  





  public FilterReply getOnHigherOrEqual()
  {
    return onHigherOrEqual;
  }
  
  public void setOnHigherOrEqual(FilterReply onHigherOrEqual) {
    this.onHigherOrEqual = onHigherOrEqual;
  }
  





  public FilterReply getOnLower()
  {
    return onLower;
  }
  
  public void setOnLower(FilterReply onLower) {
    this.onLower = onLower;
  }
  


  public void addMDCValueLevelPair(MDCValueLevelPair mdcValueLevelPair)
  {
    if (valueLevelMap.containsKey(mdcValueLevelPair.getValue())) {
      addError(mdcValueLevelPair.getValue() + " has been already set");
    } else {
      valueLevelMap.put(mdcValueLevelPair.getValue(), mdcValueLevelPair.getLevel());
    }
  }
  




  public void start()
  {
    if (key == null) {
      addError("No key name was specified");
    }
    super.start();
  }
  






















  public FilterReply decide(Marker marker, Logger logger, Level level, String s, Object[] objects, Throwable throwable)
  {
    String mdcValue = MDC.get(key);
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }
    
    Level levelAssociatedWithMDCValue = null;
    if (mdcValue != null) {
      levelAssociatedWithMDCValue = (Level)valueLevelMap.get(mdcValue);
    }
    if (levelAssociatedWithMDCValue == null) {
      levelAssociatedWithMDCValue = defaultThreshold;
    }
    if (level.isGreaterOrEqual(levelAssociatedWithMDCValue)) {
      return onHigherOrEqual;
    }
    return onLower;
  }
}
