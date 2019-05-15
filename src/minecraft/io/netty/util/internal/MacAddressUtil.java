package io.netty.util.internal;

import io.netty.util.NetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;






















public final class MacAddressUtil
{
  public static final int MAC_ADDRESS_LENGTH = 8;
  private static final byte[] NOT_FOUND = { -1 };
  
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(MacAddressUtil.class);
  







  public static byte[] bestAvailableMac()
  {
    byte[] bestMacAddr = NOT_FOUND;
    InetAddress bestInetAddr = NetUtil.LOCALHOST4;
    

    Map<NetworkInterface, InetAddress> ifaces = new LinkedHashMap();
    try {
      for (i = NetworkInterface.getNetworkInterfaces(); i.hasMoreElements();) {
        NetworkInterface iface = (NetworkInterface)i.nextElement();
        
        Enumeration<InetAddress> addrs = iface.getInetAddresses();
        if (addrs.hasMoreElements()) {
          InetAddress a = (InetAddress)addrs.nextElement();
          if (!a.isLoopbackAddress())
            ifaces.put(iface, a);
        }
      }
    } catch (SocketException e) {
      Enumeration<NetworkInterface> i;
      logger.warn("Failed to retrieve the list of available network interfaces", e);
    }
    
    for (Map.Entry<NetworkInterface, InetAddress> entry : ifaces.entrySet()) {
      NetworkInterface iface = (NetworkInterface)entry.getKey();
      InetAddress inetAddr = (InetAddress)entry.getValue();
      if (!iface.isVirtual())
      {

        try
        {

          macAddr = iface.getHardwareAddress();
        } catch (SocketException e) { byte[] macAddr;
          logger.debug("Failed to get the hardware address of a network interface: {}", iface, e); }
        continue;
        
        byte[] macAddr;
        boolean replace = false;
        int res = compareAddresses(bestMacAddr, macAddr);
        if (res < 0)
        {
          replace = true;
        } else if (res == 0)
        {
          res = compareAddresses(bestInetAddr, inetAddr);
          if (res < 0)
          {
            replace = true;
          } else if (res == 0)
          {
            if (bestMacAddr.length < macAddr.length) {
              replace = true;
            }
          }
        }
        
        if (replace) {
          bestMacAddr = macAddr;
          bestInetAddr = inetAddr;
        }
      }
    }
    if (bestMacAddr == NOT_FOUND) {
      return null;
    }
    
    switch (bestMacAddr.length) {
    case 6: 
      byte[] newAddr = new byte[8];
      System.arraycopy(bestMacAddr, 0, newAddr, 0, 3);
      newAddr[3] = -1;
      newAddr[4] = -2;
      System.arraycopy(bestMacAddr, 3, newAddr, 5, 3);
      bestMacAddr = newAddr;
      break;
    default: 
      bestMacAddr = Arrays.copyOf(bestMacAddr, 8);
    }
    
    return bestMacAddr;
  }
  



  public static String formatAddress(byte[] addr)
  {
    StringBuilder buf = new StringBuilder(24);
    for (byte b : addr) {
      buf.append(String.format("%02x:", new Object[] { Integer.valueOf(b & 0xFF) }));
    }
    return buf.substring(0, buf.length() - 1);
  }
  


  private static int compareAddresses(byte[] current, byte[] candidate)
  {
    if (candidate == null) {
      return 1;
    }
    

    if (candidate.length < 6) {
      return 1;
    }
    

    boolean onlyZeroAndOne = true;
    for (byte b : candidate) {
      if ((b != 0) && (b != 1)) {
        onlyZeroAndOne = false;
        break;
      }
    }
    
    if (onlyZeroAndOne) {
      return 1;
    }
    

    if ((candidate[0] & 0x1) != 0) {
      return 1;
    }
    

    if ((current[0] & 0x2) == 0) {
      if ((candidate[0] & 0x2) == 0)
      {
        return 0;
      }
      
      return 1;
    }
    
    if ((candidate[0] & 0x2) == 0)
    {
      return -1;
    }
    
    return 0;
  }
  




  private static int compareAddresses(InetAddress current, InetAddress candidate)
  {
    return scoreAddress(current) - scoreAddress(candidate);
  }
  
  private static int scoreAddress(InetAddress addr) {
    if ((addr.isAnyLocalAddress()) || (addr.isLoopbackAddress())) {
      return 0;
    }
    if (addr.isMulticastAddress()) {
      return 1;
    }
    if (addr.isLinkLocalAddress()) {
      return 2;
    }
    if (addr.isSiteLocalAddress()) {
      return 3;
    }
    
    return 4;
  }
  
  private MacAddressUtil() {}
}
