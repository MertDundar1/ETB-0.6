package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.action.Action;
import java.util.List;

public abstract interface RuleStore
{
  public abstract void addRule(ElementSelector paramElementSelector, String paramString)
    throws ClassNotFoundException;
  
  public abstract void addRule(ElementSelector paramElementSelector, Action paramAction);
  
  public abstract List<Action> matchActions(ElementPath paramElementPath);
}
