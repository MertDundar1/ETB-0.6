package ch.qos.logback.classic.gaffer;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.StatusManager;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

















public class GafferUtil
{
  public GafferUtil() {}
  
  private static String ERROR_MSG = "Failed to instantiate ch.qos.logback.classic.gaffer.GafferConfigurator";
  
  public static void runGafferConfiguratorOn(LoggerContext loggerContext, Object origin, File configFile) {
    GafferConfigurator gafferConfigurator = newGafferConfiguratorInstance(loggerContext, origin);
    if (gafferConfigurator != null) {
      gafferConfigurator.run(configFile);
    }
  }
  
  public static void runGafferConfiguratorOn(LoggerContext loggerContext, Object origin, URL configFile) {
    GafferConfigurator gafferConfigurator = newGafferConfiguratorInstance(loggerContext, origin);
    if (gafferConfigurator != null) {
      gafferConfigurator.run(configFile);
    }
  }
  
  private static GafferConfigurator newGafferConfiguratorInstance(LoggerContext loggerContext, Object origin)
  {
    try {
      Class gcClass = Class.forName("ch.qos.logback.classic.gaffer.GafferConfigurator");
      Constructor c = gcClass.getConstructor(new Class[] { LoggerContext.class });
      return (GafferConfigurator)c.newInstance(new Object[] { loggerContext });
    } catch (ClassNotFoundException e) {
      addError(loggerContext, origin, ERROR_MSG, e);
    } catch (NoSuchMethodException e) {
      addError(loggerContext, origin, ERROR_MSG, e);
    } catch (InvocationTargetException e) {
      addError(loggerContext, origin, ERROR_MSG, e);
    } catch (InstantiationException e) {
      addError(loggerContext, origin, ERROR_MSG, e);
    } catch (IllegalAccessException e) {
      addError(loggerContext, origin, ERROR_MSG, e);
    }
    return null;
  }
  
  private static void addError(LoggerContext context, Object origin, String msg) {
    addError(context, origin, msg, null);
  }
  
  private static void addError(LoggerContext context, Object origin, String msg, Throwable t) {
    StatusManager sm = context.getStatusManager();
    if (sm == null) {
      return;
    }
    sm.add(new ErrorStatus(msg, origin, t));
  }
}
