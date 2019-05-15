package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.PropertySetter;
import org.xml.sax.Attributes;
















public class ParamAction
  extends Action
{
  static String NO_NAME = "No name attribute in <param> element";
  static String NO_VALUE = "No name attribute in <param> element";
  boolean inError = false;
  
  public ParamAction() {}
  
  public void begin(InterpretationContext ec, String localName, Attributes attributes) { String name = attributes.getValue("name");
    String value = attributes.getValue("value");
    
    if (name == null) {
      inError = true;
      addError(NO_NAME);
      return;
    }
    
    if (value == null) {
      inError = true;
      addError(NO_VALUE);
      return;
    }
    

    value = value.trim();
    
    Object o = ec.peekObject();
    PropertySetter propSetter = new PropertySetter(o);
    propSetter.setContext(context);
    value = ec.subst(value);
    

    name = ec.subst(name);
    


    propSetter.setProperty(name, value);
  }
  
  public void end(InterpretationContext ec, String localName) {}
  
  public void finish(InterpretationContext ec) {}
}
