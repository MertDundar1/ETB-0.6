package io.netty.handler.ipfilter;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

















public final class IpSubnetFilterRule
  implements IpFilterRule
{
  private final IpFilterRule filterRule;
  
  public IpSubnetFilterRule(String ipAddress, int cidrPrefix, IpFilterRuleType ruleType)
  {
    try
    {
      filterRule = selectFilterRule(InetAddress.getByName(ipAddress), cidrPrefix, ruleType);
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("ipAddress", e);
    }
  }
  
  public IpSubnetFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
    filterRule = selectFilterRule(ipAddress, cidrPrefix, ruleType);
  }
  
  private static IpFilterRule selectFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
    if (ipAddress == null) {
      throw new NullPointerException("ipAddress");
    }
    
    if (ruleType == null) {
      throw new NullPointerException("ruleType");
    }
    
    if ((ipAddress instanceof Inet4Address))
      return new Ip4SubnetFilterRule((Inet4Address)ipAddress, cidrPrefix, ruleType, null);
    if ((ipAddress instanceof Inet6Address)) {
      return new Ip6SubnetFilterRule((Inet6Address)ipAddress, cidrPrefix, ruleType, null);
    }
    throw new IllegalArgumentException("Only IPv4 and IPv6 addresses are supported");
  }
  

  public boolean matches(InetSocketAddress remoteAddress)
  {
    return filterRule.matches(remoteAddress);
  }
  
  public IpFilterRuleType ruleType()
  {
    return filterRule.ruleType();
  }
  
  private static final class Ip4SubnetFilterRule implements IpFilterRule
  {
    private final int networkAddress;
    private final int subnetMask;
    private final IpFilterRuleType ruleType;
    
    private Ip4SubnetFilterRule(Inet4Address ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
      if ((cidrPrefix < 0) || (cidrPrefix > 32)) {
        throw new IllegalArgumentException(String.format("IPv4 requires the subnet prefix to be in range of [0,32]. The prefix was: %d", new Object[] { Integer.valueOf(cidrPrefix) }));
      }
      

      subnetMask = prefixToSubnetMask(cidrPrefix);
      networkAddress = (ipToInt(ipAddress) & subnetMask);
      this.ruleType = ruleType;
    }
    
    public boolean matches(InetSocketAddress remoteAddress)
    {
      int ipAddress = ipToInt((Inet4Address)remoteAddress.getAddress());
      
      return (ipAddress & subnetMask) == networkAddress;
    }
    
    public IpFilterRuleType ruleType()
    {
      return ruleType;
    }
    
    private static int ipToInt(Inet4Address ipAddress) {
      byte[] octets = ipAddress.getAddress();
      assert (octets.length == 4);
      
      return (octets[0] & 0xFF) << 24 | (octets[1] & 0xFF) << 16 | (octets[2] & 0xFF) << 8 | octets[3] & 0xFF;
    }
    












    private static int prefixToSubnetMask(int cidrPrefix)
    {
      return (int)(-1L << 32 - cidrPrefix & 0xFFFFFFFFFFFFFFFF);
    }
  }
  
  private static final class Ip6SubnetFilterRule implements IpFilterRule
  {
    private static final BigInteger MINUS_ONE = BigInteger.valueOf(-1L);
    private final BigInteger networkAddress;
    private final BigInteger subnetMask;
    private final IpFilterRuleType ruleType;
    
    private Ip6SubnetFilterRule(Inet6Address ipAddress, int cidrPrefix, IpFilterRuleType ruleType)
    {
      if ((cidrPrefix < 0) || (cidrPrefix > 128)) {
        throw new IllegalArgumentException(String.format("IPv6 requires the subnet prefix to be in range of [0,128]. The prefix was: %d", new Object[] { Integer.valueOf(cidrPrefix) }));
      }
      

      subnetMask = prefixToSubnetMask(cidrPrefix);
      networkAddress = ipToInt(ipAddress).and(subnetMask);
      this.ruleType = ruleType;
    }
    
    public boolean matches(InetSocketAddress remoteAddress)
    {
      BigInteger ipAddress = ipToInt((Inet6Address)remoteAddress.getAddress());
      
      return ipAddress.and(subnetMask).equals(networkAddress);
    }
    
    public IpFilterRuleType ruleType()
    {
      return ruleType;
    }
    
    private static BigInteger ipToInt(Inet6Address ipAddress) {
      byte[] octets = ipAddress.getAddress();
      assert (octets.length == 16);
      
      return new BigInteger(octets);
    }
    
    private static BigInteger prefixToSubnetMask(int cidrPrefix) {
      return MINUS_ONE.shiftLeft(128 - cidrPrefix);
    }
  }
}
