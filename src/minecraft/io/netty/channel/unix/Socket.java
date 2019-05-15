package io.netty.channel.unix;

import io.netty.channel.ChannelException;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThrowableUtil;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;




























public final class Socket
  extends FileDescriptor
{
  private static final ClosedChannelException SHUTDOWN_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Socket.class, "shutdown(...)");
  
  private static final ClosedChannelException SEND_TO_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Socket.class, "sendTo(...)");
  
  private static final ClosedChannelException SEND_TO_ADDRESS_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Socket.class, "sendToAddress(...)");
  
  private static final ClosedChannelException SEND_TO_ADDRESSES_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Socket.class, "sendToAddresses(...)");
  
  private static final Errors.NativeIoException SEND_TO_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:sendto(...)", Errors.ERRNO_EPIPE_NEGATIVE), Socket.class, "sendTo(...)");
  

  private static final Errors.NativeIoException SEND_TO_ADDRESS_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:sendto(...)", Errors.ERRNO_EPIPE_NEGATIVE), Socket.class, "sendToAddress(...)");
  

  private static final Errors.NativeIoException CONNECTION_RESET_EXCEPTION_SENDMSG = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:sendmsg(...)", Errors.ERRNO_EPIPE_NEGATIVE), Socket.class, "sendToAddresses(...)");
  

  private static final Errors.NativeIoException CONNECTION_RESET_SHUTDOWN_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:shutdown(...)", Errors.ERRNO_ECONNRESET_NEGATIVE), Socket.class, "shutdown(...)");
  

  private static final Errors.NativeConnectException FINISH_CONNECT_REFUSED_EXCEPTION = (Errors.NativeConnectException)ThrowableUtil.unknownStackTrace(new Errors.NativeConnectException("syscall:getsockopt(...)", Errors.ERROR_ECONNREFUSED_NEGATIVE), Socket.class, "finishConnect(...)");
  

  private static final Errors.NativeConnectException CONNECT_REFUSED_EXCEPTION = (Errors.NativeConnectException)ThrowableUtil.unknownStackTrace(new Errors.NativeConnectException("syscall:connect(...)", Errors.ERROR_ECONNREFUSED_NEGATIVE), Socket.class, "connect(...)");
  
  public Socket(int fd)
  {
    super(fd);
  }
  
  public void shutdown() throws IOException {
    shutdown(true, true);
  }
  

  public void shutdown(boolean read, boolean write)
    throws IOException
  {
    for (;;)
    {
      int oldState = state;
      if (isClosed(oldState)) {
        throw new ClosedChannelException();
      }
      int newState = oldState;
      if ((read) && (!isInputShutdown(newState))) {
        newState = inputShutdown(newState);
      }
      if ((write) && (!isOutputShutdown(newState))) {
        newState = outputShutdown(newState);
      }
      

      if (newState == oldState) {
        return;
      }
      if (casState(oldState, newState)) {
        break;
      }
    }
    int res = shutdown(fd, read, write);
    if (res < 0) {
      Errors.ioResult("shutdown", res, CONNECTION_RESET_SHUTDOWN_EXCEPTION, SHUTDOWN_CLOSED_CHANNEL_EXCEPTION);
    }
  }
  
  public boolean isShutdown() {
    int state = this.state;
    return (isInputShutdown(state)) && (isOutputShutdown(state));
  }
  
  public boolean isInputShutdown() {
    return isInputShutdown(state);
  }
  
  public boolean isOutputShutdown() {
    return isOutputShutdown(state);
  }
  
  public int sendTo(ByteBuffer buf, int pos, int limit, InetAddress addr, int port) throws IOException
  {
    int scopeId;
    int scopeId;
    byte[] address;
    if ((addr instanceof Inet6Address)) {
      byte[] address = addr.getAddress();
      scopeId = ((Inet6Address)addr).getScopeId();
    }
    else {
      scopeId = 0;
      address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
    }
    int res = sendTo(fd, buf, pos, limit, address, scopeId, port);
    if (res >= 0) {
      return res;
    }
    return Errors.ioResult("sendTo", res, SEND_TO_CONNECTION_RESET_EXCEPTION, SEND_TO_CLOSED_CHANNEL_EXCEPTION);
  }
  
  public int sendToAddress(long memoryAddress, int pos, int limit, InetAddress addr, int port)
    throws IOException
  {
    int scopeId;
    int scopeId;
    byte[] address;
    if ((addr instanceof Inet6Address)) {
      byte[] address = addr.getAddress();
      scopeId = ((Inet6Address)addr).getScopeId();
    }
    else {
      scopeId = 0;
      address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
    }
    int res = sendToAddress(fd, memoryAddress, pos, limit, address, scopeId, port);
    if (res >= 0) {
      return res;
    }
    return Errors.ioResult("sendToAddress", res, SEND_TO_ADDRESS_CONNECTION_RESET_EXCEPTION, SEND_TO_ADDRESS_CLOSED_CHANNEL_EXCEPTION);
  }
  
  public int sendToAddresses(long memoryAddress, int length, InetAddress addr, int port)
    throws IOException
  {
    int scopeId;
    int scopeId;
    byte[] address;
    if ((addr instanceof Inet6Address)) {
      byte[] address = addr.getAddress();
      scopeId = ((Inet6Address)addr).getScopeId();
    }
    else {
      scopeId = 0;
      address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
    }
    int res = sendToAddresses(fd, memoryAddress, length, address, scopeId, port);
    if (res >= 0) {
      return res;
    }
    return Errors.ioResult("sendToAddresses", res, CONNECTION_RESET_EXCEPTION_SENDMSG, SEND_TO_ADDRESSES_CLOSED_CHANNEL_EXCEPTION);
  }
  
  public DatagramSocketAddress recvFrom(ByteBuffer buf, int pos, int limit) throws IOException
  {
    return recvFrom(fd, buf, pos, limit);
  }
  
  public DatagramSocketAddress recvFromAddress(long memoryAddress, int pos, int limit) throws IOException {
    return recvFromAddress(fd, memoryAddress, pos, limit);
  }
  
  public boolean connect(SocketAddress socketAddress) throws IOException {
    int res;
    if ((socketAddress instanceof InetSocketAddress)) {
      InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
      NativeInetAddress address = NativeInetAddress.newInstance(inetSocketAddress.getAddress());
      res = connect(fd, address, scopeId, inetSocketAddress.getPort()); } else { int res;
      if ((socketAddress instanceof DomainSocketAddress)) {
        DomainSocketAddress unixDomainSocketAddress = (DomainSocketAddress)socketAddress;
        res = connectDomainSocket(fd, unixDomainSocketAddress.path().getBytes(CharsetUtil.UTF_8));
      } else {
        throw new Error("Unexpected SocketAddress implementation " + socketAddress); } }
    int res;
    if (res < 0) {
      if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE)
      {
        return false;
      }
      Errors.throwConnectException("connect", CONNECT_REFUSED_EXCEPTION, res);
    }
    return true;
  }
  
  public boolean finishConnect() throws IOException {
    int res = finishConnect(fd);
    if (res < 0) {
      if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE)
      {
        return false;
      }
      Errors.throwConnectException("finishConnect", FINISH_CONNECT_REFUSED_EXCEPTION, res);
    }
    return true;
  }
  
  public void bind(SocketAddress socketAddress) throws IOException {
    if ((socketAddress instanceof InetSocketAddress)) {
      InetSocketAddress addr = (InetSocketAddress)socketAddress;
      NativeInetAddress address = NativeInetAddress.newInstance(addr.getAddress());
      int res = bind(fd, address, scopeId, addr.getPort());
      if (res < 0) {
        throw Errors.newIOException("bind", res);
      }
    } else if ((socketAddress instanceof DomainSocketAddress)) {
      DomainSocketAddress addr = (DomainSocketAddress)socketAddress;
      int res = bindDomainSocket(fd, addr.path().getBytes(CharsetUtil.UTF_8));
      if (res < 0) {
        throw Errors.newIOException("bind", res);
      }
    } else {
      throw new Error("Unexpected SocketAddress implementation " + socketAddress);
    }
  }
  
  public void listen(int backlog) throws IOException {
    int res = listen(fd, backlog);
    if (res < 0) {
      throw Errors.newIOException("listen", res);
    }
  }
  
  public int accept(byte[] addr) throws IOException {
    int res = accept(fd, addr);
    if (res >= 0) {
      return res;
    }
    if ((res == Errors.ERRNO_EAGAIN_NEGATIVE) || (res == Errors.ERRNO_EWOULDBLOCK_NEGATIVE))
    {
      return -1;
    }
    throw Errors.newIOException("accept", res);
  }
  
  public InetSocketAddress remoteAddress() {
    byte[] addr = remoteAddress(fd);
    

    if (addr == null) {
      return null;
    }
    return NativeInetAddress.address(addr, 0, addr.length);
  }
  
  public InetSocketAddress localAddress() {
    byte[] addr = localAddress(fd);
    

    if (addr == null) {
      return null;
    }
    return NativeInetAddress.address(addr, 0, addr.length);
  }
  
  public int getReceiveBufferSize() throws IOException {
    return getReceiveBufferSize(fd);
  }
  
  public int getSendBufferSize() throws IOException {
    return getSendBufferSize(fd);
  }
  
  public boolean isKeepAlive() throws IOException {
    return isKeepAlive(fd) != 0;
  }
  
  public boolean isTcpNoDelay() throws IOException {
    return isTcpNoDelay(fd) != 0;
  }
  
  public boolean isTcpCork() throws IOException {
    return isTcpCork(fd) != 0;
  }
  
  public int getSoLinger() throws IOException {
    return getSoLinger(fd);
  }
  
  public int getTcpDeferAccept() throws IOException {
    return getTcpDeferAccept(fd);
  }
  
  public boolean isTcpQuickAck() throws IOException {
    return isTcpQuickAck(fd) != 0;
  }
  
  public int getSoError() throws IOException {
    return getSoError(fd);
  }
  
  public void setKeepAlive(boolean keepAlive) throws IOException {
    setKeepAlive(fd, keepAlive ? 1 : 0);
  }
  
  public void setReceiveBufferSize(int receiveBufferSize) throws IOException {
    setReceiveBufferSize(fd, receiveBufferSize);
  }
  
  public void setSendBufferSize(int sendBufferSize) throws IOException {
    setSendBufferSize(fd, sendBufferSize);
  }
  
  public void setTcpNoDelay(boolean tcpNoDelay) throws IOException {
    setTcpNoDelay(fd, tcpNoDelay ? 1 : 0);
  }
  
  public void setTcpCork(boolean tcpCork) throws IOException {
    setTcpCork(fd, tcpCork ? 1 : 0);
  }
  
  public void setSoLinger(int soLinger) throws IOException {
    setSoLinger(fd, soLinger);
  }
  
  public void setTcpDeferAccept(int deferAccept) throws IOException {
    setTcpDeferAccept(fd, deferAccept);
  }
  
  public void setTcpQuickAck(boolean quickAck) throws IOException {
    setTcpQuickAck(fd, quickAck ? 1 : 0);
  }
  
  public String toString()
  {
    return "Socket{fd=" + fd + '}';
  }
  

  public static Socket newSocketStream()
  {
    int res = newSocketStreamFd();
    if (res < 0) {
      throw new ChannelException(Errors.newIOException("newSocketStream", res));
    }
    return new Socket(res);
  }
  
  public static Socket newSocketDgram() {
    int res = newSocketDgramFd();
    if (res < 0) {
      throw new ChannelException(Errors.newIOException("newSocketDgram", res));
    }
    return new Socket(res);
  }
  
  public static Socket newSocketDomain() {
    int res = newSocketDomainFd();
    if (res < 0) {
      throw new ChannelException(Errors.newIOException("newSocketDomain", res));
    }
    return new Socket(res);
  }
  
  private static native int shutdown(int paramInt, boolean paramBoolean1, boolean paramBoolean2);
  
  private static native int connect(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  private static native int connectDomainSocket(int paramInt, byte[] paramArrayOfByte);
  
  private static native int finishConnect(int paramInt);
  
  private static native int bind(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  private static native int bindDomainSocket(int paramInt, byte[] paramArrayOfByte);
  
  private static native int listen(int paramInt1, int paramInt2);
  
  private static native int accept(int paramInt, byte[] paramArrayOfByte);
  
  private static native byte[] remoteAddress(int paramInt);
  
  private static native byte[] localAddress(int paramInt);
  
  private static native int sendTo(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3, byte[] paramArrayOfByte, int paramInt4, int paramInt5);
  
  private static native int sendToAddress(int paramInt1, long paramLong, int paramInt2, int paramInt3, byte[] paramArrayOfByte, int paramInt4, int paramInt5);
  
  private static native int sendToAddresses(int paramInt1, long paramLong, int paramInt2, byte[] paramArrayOfByte, int paramInt3, int paramInt4);
  
  private static native DatagramSocketAddress recvFrom(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3)
    throws IOException;
  
  private static native DatagramSocketAddress recvFromAddress(int paramInt1, long paramLong, int paramInt2, int paramInt3)
    throws IOException;
  
  private static native int newSocketStreamFd();
  
  private static native int newSocketDgramFd();
  
  private static native int newSocketDomainFd();
  
  private static native int getReceiveBufferSize(int paramInt)
    throws IOException;
  
  private static native int getSendBufferSize(int paramInt)
    throws IOException;
  
  private static native int isKeepAlive(int paramInt)
    throws IOException;
  
  private static native int isTcpNoDelay(int paramInt)
    throws IOException;
  
  private static native int isTcpCork(int paramInt)
    throws IOException;
  
  private static native int getSoLinger(int paramInt)
    throws IOException;
  
  private static native int getSoError(int paramInt)
    throws IOException;
  
  private static native int getTcpDeferAccept(int paramInt)
    throws IOException;
  
  private static native int isTcpQuickAck(int paramInt)
    throws IOException;
  
  private static native void setKeepAlive(int paramInt1, int paramInt2)
    throws IOException;
  
  private static native void setReceiveBufferSize(int paramInt1, int paramInt2)
    throws IOException;
  
  private static native void setSendBufferSize(int paramInt1, int paramInt2)
    throws IOException;
  
  private static native void setTcpNoDelay(int paramInt1, int paramInt2)
    throws IOException;
  
  private static native void setTcpCork(int paramInt1, int paramInt2)
    throws IOException;
  
  private static native void setSoLinger(int paramInt1, int paramInt2)
    throws IOException;
  
  private static native void setTcpDeferAccept(int paramInt1, int paramInt2)
    throws IOException;
  
  private static native void setTcpQuickAck(int paramInt1, int paramInt2)
    throws IOException;
}
