package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;





















public abstract class AbstractNioMessageChannel
  extends AbstractNioChannel
{
  protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp)
  {
    super(parent, ch, readInterestOp);
  }
  


  protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() { return new NioMessageUnsafe(null); }
  
  private final class NioMessageUnsafe extends AbstractNioChannel.AbstractNioUnsafe {
    private NioMessageUnsafe() { super(); }
    
    private final List<Object> readBuf = new ArrayList();
    
    public void read()
    {
      assert (eventLoop().inEventLoop());
      ChannelConfig config = config();
      if ((!config.isAutoRead()) && (!isReadPending()))
      {
        removeReadOp();
        return;
      }
      
      int maxMessagesPerRead = config.getMaxMessagesPerRead();
      ChannelPipeline pipeline = pipeline();
      boolean closed = false;
      Throwable exception = null;
      try {
        try {
          for (;;) {
            int localRead = doReadMessages(readBuf);
            if (localRead == 0) {
              break;
            }
            if (localRead < 0) {
              closed = true;



            }
            else if (config.isAutoRead())
            {


              if (readBuf.size() >= maxMessagesPerRead)
                break;
            }
          }
        } catch (Throwable t) {
          exception = t;
        }
        setReadPending(false);
        int size = readBuf.size();
        for (int i = 0; i < size; i++) {
          pipeline.fireChannelRead(readBuf.get(i));
        }
        
        readBuf.clear();
        pipeline.fireChannelReadComplete();
        
        if (exception != null) {
          if ((exception instanceof IOException))
          {

            closed = !(AbstractNioMessageChannel.this instanceof ServerChannel);
          }
          
          pipeline.fireExceptionCaught(exception);
        }
        
        if ((closed) && 
          (isOpen())) {
          close(voidPromise());

        }
        


      }
      finally
      {

        if ((!config.isAutoRead()) && (!isReadPending())) {
          removeReadOp();
        }
      }
    }
  }
  
  protected void doWrite(ChannelOutboundBuffer in) throws Exception
  {
    SelectionKey key = selectionKey();
    int interestOps = key.interestOps();
    for (;;)
    {
      Object msg = in.current();
      if (msg == null)
      {
        if ((interestOps & 0x4) == 0) break;
        key.interestOps(interestOps & 0xFFFFFFFB); break;
      }
      
      try
      {
        boolean done = false;
        for (int i = config().getWriteSpinCount() - 1; i >= 0; i--) {
          if (doWriteMessage(msg, in)) {
            done = true;
            break;
          }
        }
        
        if (done) {
          in.remove();
        }
        else {
          if ((interestOps & 0x4) == 0) {
            key.interestOps(interestOps | 0x4);
          }
          break;
        }
      } catch (IOException e) {
        if (continueOnWriteError()) {
          in.remove(e);
        } else {
          throw e;
        }
      }
    }
  }
  


  protected boolean continueOnWriteError()
  {
    return false;
  }
  
  protected abstract int doReadMessages(List<Object> paramList)
    throws Exception;
  
  protected abstract boolean doWriteMessage(Object paramObject, ChannelOutboundBuffer paramChannelOutboundBuffer)
    throws Exception;
}
