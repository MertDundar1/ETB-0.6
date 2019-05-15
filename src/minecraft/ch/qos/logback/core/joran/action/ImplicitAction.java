package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public abstract class ImplicitAction
  extends Action
{
  public ImplicitAction() {}
  
  public abstract boolean isApplicable(ElementPath paramElementPath, Attributes paramAttributes, InterpretationContext paramInterpretationContext);
}
