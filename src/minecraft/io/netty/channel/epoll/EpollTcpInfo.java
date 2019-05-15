package io.netty.channel.epoll;






























public final class EpollTcpInfo
{
  public EpollTcpInfo() {}
  




























  final int[] info = new int[32];
  
  public int state() {
    return info[0] & 0xFF;
  }
  
  public int caState() {
    return info[1] & 0xFF;
  }
  
  public int retransmits() {
    return info[2] & 0xFF;
  }
  
  public int probes() {
    return info[3] & 0xFF;
  }
  
  public int backoff() {
    return info[4] & 0xFF;
  }
  
  public int options() {
    return info[5] & 0xFF;
  }
  
  public int sndWscale() {
    return info[6] & 0xFF;
  }
  
  public int rcvWscale() {
    return info[7] & 0xFF;
  }
  
  public long rto() {
    return info[8] & 0xFFFFFFFF;
  }
  
  public long ato() {
    return info[9] & 0xFFFFFFFF;
  }
  
  public long sndMss() {
    return info[10] & 0xFFFFFFFF;
  }
  
  public long rcvMss() {
    return info[11] & 0xFFFFFFFF;
  }
  
  public long unacked() {
    return info[12] & 0xFFFFFFFF;
  }
  
  public long sacked() {
    return info[13] & 0xFFFFFFFF;
  }
  
  public long lost() {
    return info[14] & 0xFFFFFFFF;
  }
  
  public long retrans() {
    return info[15] & 0xFFFFFFFF;
  }
  
  public long fackets() {
    return info[16] & 0xFFFFFFFF;
  }
  
  public long lastDataSent() {
    return info[17] & 0xFFFFFFFF;
  }
  
  public long lastAckSent() {
    return info[18] & 0xFFFFFFFF;
  }
  
  public long lastDataRecv() {
    return info[19] & 0xFFFFFFFF;
  }
  
  public long lastAckRecv() {
    return info[20] & 0xFFFFFFFF;
  }
  
  public long pmtu() {
    return info[21] & 0xFFFFFFFF;
  }
  
  public long rcvSsthresh() {
    return info[22] & 0xFFFFFFFF;
  }
  
  public long rtt() {
    return info[23] & 0xFFFFFFFF;
  }
  
  public long rttvar() {
    return info[24] & 0xFFFFFFFF;
  }
  
  public long sndSsthresh() {
    return info[25] & 0xFFFFFFFF;
  }
  
  public long sndCwnd() {
    return info[26] & 0xFFFFFFFF;
  }
  
  public long advmss() {
    return info[27] & 0xFFFFFFFF;
  }
  
  public long reordering() {
    return info[28] & 0xFFFFFFFF;
  }
  
  public long rcvRtt() {
    return info[29] & 0xFFFFFFFF;
  }
  
  public long rcvSpace() {
    return info[30] & 0xFFFFFFFF;
  }
  
  public long totalRetrans() {
    return info[31] & 0xFFFFFFFF;
  }
}
