package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;













public class ContextUtil
  extends ContextAwareBase
{
  public ContextUtil(Context context)
  {
    setContext(context);
  }
  
  public static String getLocalHostName() throws UnknownHostException, SocketException
  {
    try {
      InetAddress localhost = InetAddress.getLocalHost();
      return localhost.getHostName();
    } catch (UnknownHostException e) {}
    return getLocalAddressAsString();
  }
  
  private static String getLocalAddressAsString()
    throws UnknownHostException, SocketException
  {
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    
    while ((interfaces != null) && (interfaces.hasMoreElements())) {
      Enumeration<InetAddress> addresses = ((NetworkInterface)interfaces.nextElement()).getInetAddresses();
      
      while ((addresses != null) && (addresses.hasMoreElements())) {
        InetAddress address = (InetAddress)addresses.nextElement();
        if (acceptableAddress(address)) {
          return address.getHostAddress();
        }
      }
    }
    throw new UnknownHostException();
  }
  
  private static boolean acceptableAddress(InetAddress address) {
    return (address != null) && (!address.isLoopbackAddress()) && (!address.isAnyLocalAddress()) && (!address.isLinkLocalAddress());
  }
  




  public void addHostNameAsProperty()
  {
    try
    {
      String localhostName = getLocalHostName();
      context.putProperty("HOSTNAME", localhostName);
    } catch (UnknownHostException e) {
      addError("Failed to get local hostname", e);
    } catch (SocketException e) {
      addError("Failed to get local hostname", e);
    } catch (SecurityException e) {
      addError("Failed to get local hostname", e);
    }
  }
  
  public void addProperties(Properties props) {
    if (props == null) {
      return;
    }
    Iterator i = props.keySet().iterator();
    while (i.hasNext()) {
      String key = (String)i.next();
      context.putProperty(key, props.getProperty(key));
    }
  }
  

  public void addGroovyPackages(List<String> frameworkPackages)
  {
    addFrameworkPackage(frameworkPackages, "org.codehaus.groovy.runtime");
  }
  
  public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
    if (!frameworkPackages.contains(packageName)) {
      frameworkPackages.add(packageName);
    }
  }
}
