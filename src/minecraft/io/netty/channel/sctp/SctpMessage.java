package io.netty.channel.sctp;

import com.sun.nio.sctp.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.DefaultByteBufHolder;























public final class SctpMessage
  extends DefaultByteBufHolder
{
  private final int streamIdentifier;
  private final int protocolIdentifier;
  private final MessageInfo msgInfo;
  
  public SctpMessage(int protocolIdentifier, int streamIdentifier, ByteBuf payloadBuffer)
  {
    super(payloadBuffer);
    this.protocolIdentifier = protocolIdentifier;
    this.streamIdentifier = streamIdentifier;
    msgInfo = null;
  }
  




  public SctpMessage(MessageInfo msgInfo, ByteBuf payloadBuffer)
  {
    super(payloadBuffer);
    if (msgInfo == null) {
      throw new NullPointerException("msgInfo");
    }
    this.msgInfo = msgInfo;
    streamIdentifier = msgInfo.streamNumber();
    protocolIdentifier = msgInfo.payloadProtocolID();
  }
  


  public int streamIdentifier()
  {
    return streamIdentifier;
  }
  


  public int protocolIdentifier()
  {
    return protocolIdentifier;
  }
  



  public MessageInfo messageInfo()
  {
    return msgInfo;
  }
  


  public boolean isComplete()
  {
    if (msgInfo != null) {
      return msgInfo.isComplete();
    }
    
    return true;
  }
  

  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    
    SctpMessage sctpFrame = (SctpMessage)o;
    
    if (protocolIdentifier != protocolIdentifier) {
      return false;
    }
    
    if (streamIdentifier != streamIdentifier) {
      return false;
    }
    
    if (!content().equals(sctpFrame.content())) {
      return false;
    }
    
    return true;
  }
  
  public int hashCode()
  {
    int result = streamIdentifier;
    result = 31 * result + protocolIdentifier;
    result = 31 * result + content().hashCode();
    return result;
  }
  
  public SctpMessage copy()
  {
    if (msgInfo == null) {
      return new SctpMessage(protocolIdentifier, streamIdentifier, content().copy());
    }
    return new SctpMessage(msgInfo, content().copy());
  }
  

  public SctpMessage duplicate()
  {
    if (msgInfo == null) {
      return new SctpMessage(protocolIdentifier, streamIdentifier, content().duplicate());
    }
    return new SctpMessage(msgInfo, content().copy());
  }
  

  public SctpMessage retain()
  {
    super.retain();
    return this;
  }
  
  public SctpMessage retain(int increment)
  {
    super.retain(increment);
    return this;
  }
  
  public String toString()
  {
    if (refCnt() == 0) {
      return "SctpFrame{streamIdentifier=" + streamIdentifier + ", protocolIdentifier=" + protocolIdentifier + ", data=(FREED)}";
    }
    

    return "SctpFrame{streamIdentifier=" + streamIdentifier + ", protocolIdentifier=" + protocolIdentifier + ", data=" + ByteBufUtil.hexDump(content()) + '}';
  }
}
