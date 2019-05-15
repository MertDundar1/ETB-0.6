package ch.qos.logback.core.db;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.util.OptionHelper;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.xml.sax.Attributes;























public class BindDataSourceToJNDIAction
  extends Action
{
  static final String DATA_SOURCE_CLASS = "dataSourceClass";
  static final String URL = "url";
  static final String USER = "user";
  static final String PASSWORD = "password";
  
  public BindDataSourceToJNDIAction() {}
  
  public void begin(InterpretationContext ec, String localName, Attributes attributes)
  {
    String dsClassName = ec.getProperty("dataSourceClass");
    
    if (OptionHelper.isEmpty(dsClassName)) {
      addWarn("dsClassName is a required parameter");
      ec.addError("dsClassName is a required parameter");
      
      return;
    }
    
    String urlStr = ec.getProperty("url");
    String userStr = ec.getProperty("user");
    String passwordStr = ec.getProperty("password");
    try
    {
      DataSource ds = (DataSource)OptionHelper.instantiateByClassName(dsClassName, DataSource.class, context);
      

      PropertySetter setter = new PropertySetter(ds);
      setter.setContext(context);
      
      if (!OptionHelper.isEmpty(urlStr)) {
        setter.setProperty("url", urlStr);
      }
      
      if (!OptionHelper.isEmpty(userStr)) {
        setter.setProperty("user", userStr);
      }
      
      if (!OptionHelper.isEmpty(passwordStr)) {
        setter.setProperty("password", passwordStr);
      }
      
      Context ctx = new InitialContext();
      ctx.rebind("dataSource", ds);
    } catch (Exception oops) {
      addError("Could not bind  datasource. Reported error follows.", oops);
      
      ec.addError("Could not not bind  datasource of type [" + dsClassName + "].");
    }
  }
  
  public void end(InterpretationContext ec, String name) {}
}
