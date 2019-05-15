package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundBuffer.MessageProcessor;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.util.concurrent.FastThreadLocal;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;


















final class NativeDatagramPacketArray
  implements ChannelOutboundBuffer.MessageProcessor
{
  private static final FastThreadLocal<NativeDatagramPacketArray> ARRAY = new FastThreadLocal()
  {
    protected NativeDatagramPacketArray initialValue() throws Exception
    {
      return new NativeDatagramPacketArray(null);
    }
    
    protected void onRemoval(NativeDatagramPacketArray value) throws Exception
    {
      NativeDatagramPacketArray.NativeDatagramPacket[] array = packets;
      
      for (int i = 0; i < array.length; i++) {
        array[i].release();
      }
    }
  };
  

  private final NativeDatagramPacket[] packets = new NativeDatagramPacket[Native.UIO_MAX_IOV];
  private int count;
  
  private NativeDatagramPacketArray() {
    for (int i = 0; i < packets.length; i++) {
      packets[i] = new NativeDatagramPacket();
    }
  }
  



  boolean add(DatagramPacket packet)
  {
    if (count == packets.length) {
      return false;
    }
    ByteBuf content = (ByteBuf)packet.content();
    int len = content.readableBytes();
    if (len == 0) {
      return true;
    }
    NativeDatagramPacket p = packets[count];
    InetSocketAddress recipient = (InetSocketAddress)packet.recipient();
    if (!p.init(content, recipient)) {
      return false;
    }
    
    count += 1;
    return true;
  }
  
  public boolean processMessage(Object msg) throws Exception
  {
    return ((msg instanceof DatagramPacket)) && (add((DatagramPacket)msg));
  }
  


  int count()
  {
    return count;
  }
  


  NativeDatagramPacket[] packets()
  {
    return packets;
  }
  


  static NativeDatagramPacketArray getInstance(ChannelOutboundBuffer buffer)
    throws Exception
  {
    NativeDatagramPacketArray array = (NativeDatagramPacketArray)ARRAY.get();
    count = 0;
    buffer.forEachFlushedMessage(array);
    return array;
  }
  






  static final class NativeDatagramPacket
  {
    private final IovArray array = new IovArray();
    private long memoryAddress;
    private int count;
    private byte[] addr;
    private int scopeId;
    private int port;
    
    NativeDatagramPacket() {}
    
    private void release()
    {
      array.release();
    }
    


    private boolean init(ByteBuf buf, InetSocketAddress recipient)
    {
      array.clear();
      if (!array.add(buf)) {
        return false;
      }
      
      memoryAddress = array.memoryAddress(0);
      count = array.count();
      
      InetAddress address = recipient.getAddress();
      if ((address instanceof Inet6Address)) {
        addr = address.getAddress();
        scopeId = ((Inet6Address)address).getScopeId();
      } else {
        addr = NativeInetAddress.ipv4MappedIpv6Address(address.getAddress());
        scopeId = 0;
      }
      port = recipient.getPort();
      return true;
    }
  }
}
