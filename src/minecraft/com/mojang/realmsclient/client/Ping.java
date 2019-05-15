package com.mojang.realmsclient.client;

import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Ping
{
  public Ping() {}
  
  public static List<RegionPingResult> ping(Region... regions)
  {
    for (Region region : regions) {
      ping(endpoint);
    }
    

    List<RegionPingResult> results = new ArrayList();
    for (Region region : regions) {
      results.add(new RegionPingResult(name, ping(endpoint)));
    }
    

    Collections.sort(results, new Comparator()
    {
      public int compare(RegionPingResult o1, RegionPingResult o2) {
        return o1.ping() - o2.ping();
      }
    });
    return results;
  }
  
  private static int ping(String host) {
    int timeout = 700;
    long sum = 0L;
    Socket socket = null;
    for (int i = 0; i < 5; i++) {
      try {
        SocketAddress sockAddr = new java.net.InetSocketAddress(host, 80);
        socket = new Socket();
        long t1 = now();
        socket.connect(sockAddr, 700);
        sum += now() - t1;
      } catch (Exception e) {
        sum += 700L;
      } finally {
        close(socket);
      }
    }
    return (int)(sum / 5.0D);
  }
  
  private static void close(Socket socket) {
    try {
      if (socket != null) {
        socket.close();
      }
    }
    catch (Throwable t) {}
  }
  
  private static long now()
  {
    return System.currentTimeMillis();
  }
  
  public static List<RegionPingResult> pingAllRegions() {
    return ping(Region.values());
  }
  



  static enum Region
  {
    US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"), 
    US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"), 
    US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"), 
    EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"), 
    AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"), 
    AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"), 
    AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"), 
    SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");
    
    private Region(String name, String endpoint) {
      this.name = name;
      this.endpoint = endpoint;
    }
    
    private final String name;
    private final String endpoint;
  }
}
