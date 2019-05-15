package ch.qos.logback.core.joran;

import ch.qos.logback.core.joran.action.AppenderAction;
import ch.qos.logback.core.joran.action.AppenderRefAction;
import ch.qos.logback.core.joran.action.ContextPropertyAction;
import ch.qos.logback.core.joran.action.ConversionRuleAction;
import ch.qos.logback.core.joran.action.DefinePropertyAction;
import ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
import ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
import ch.qos.logback.core.joran.action.NewRuleAction;
import ch.qos.logback.core.joran.action.ParamAction;
import ch.qos.logback.core.joran.action.PropertyAction;
import ch.qos.logback.core.joran.action.ShutdownHookAction;
import ch.qos.logback.core.joran.action.StatusListenerAction;
import ch.qos.logback.core.joran.action.TimestampAction;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.RuleStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

























public abstract class JoranConfiguratorBase
  extends GenericConfigurator
{
  public JoranConfiguratorBase() {}
  
  public List getErrorList()
  {
    return null;
  }
  


  protected void addInstanceRules(RuleStore rs)
  {
    rs.addRule(new ElementSelector("configuration/variable"), new PropertyAction());
    rs.addRule(new ElementSelector("configuration/property"), new PropertyAction());
    
    rs.addRule(new ElementSelector("configuration/substitutionProperty"), new PropertyAction());
    

    rs.addRule(new ElementSelector("configuration/timestamp"), new TimestampAction());
    rs.addRule(new ElementSelector("configuration/shutdownHook"), new ShutdownHookAction());
    rs.addRule(new ElementSelector("configuration/define"), new DefinePropertyAction());
    


    rs.addRule(new ElementSelector("configuration/contextProperty"), new ContextPropertyAction());
    

    rs.addRule(new ElementSelector("configuration/conversionRule"), new ConversionRuleAction());
    

    rs.addRule(new ElementSelector("configuration/statusListener"), new StatusListenerAction());
    

    rs.addRule(new ElementSelector("configuration/appender"), new AppenderAction());
    rs.addRule(new ElementSelector("configuration/appender/appender-ref"), new AppenderRefAction());
    
    rs.addRule(new ElementSelector("configuration/newRule"), new NewRuleAction());
    rs.addRule(new ElementSelector("*/param"), new ParamAction());
  }
  

  protected void addImplicitRules(Interpreter interpreter)
  {
    NestedComplexPropertyIA nestedComplexPropertyIA = new NestedComplexPropertyIA();
    nestedComplexPropertyIA.setContext(context);
    interpreter.addImplicitAction(nestedComplexPropertyIA);
    
    NestedBasicPropertyIA nestedBasicIA = new NestedBasicPropertyIA();
    nestedBasicIA.setContext(context);
    interpreter.addImplicitAction(nestedBasicIA);
  }
  
  protected void buildInterpreter()
  {
    super.buildInterpreter();
    Map<String, Object> omap = interpreter.getInterpretationContext().getObjectMap();
    
    omap.put("APPENDER_BAG", new HashMap());
    omap.put("FILTER_CHAIN_BAG", new HashMap());
  }
  
  public InterpretationContext getInterpretationContext() {
    return interpreter.getInterpretationContext();
  }
}
