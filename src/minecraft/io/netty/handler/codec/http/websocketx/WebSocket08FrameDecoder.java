package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.List;























































public class WebSocket08FrameDecoder
  extends ReplayingDecoder<State>
  implements WebSocketFrameDecoder
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
  
  private static final byte OPCODE_CONT = 0;
  private static final byte OPCODE_TEXT = 1;
  private static final byte OPCODE_BINARY = 2;
  private static final byte OPCODE_CLOSE = 8;
  private static final byte OPCODE_PING = 9;
  private static final byte OPCODE_PONG = 10;
  private int fragmentedFramesCount;
  private final long maxFramePayloadLength;
  private boolean frameFinalFlag;
  private int frameRsv;
  private int frameOpcode;
  private long framePayloadLength;
  private ByteBuf framePayload;
  private int framePayloadBytesRead;
  private byte[] maskingKey;
  private ByteBuf payloadBuffer;
  private final boolean allowExtensions;
  private final boolean maskedPayload;
  private boolean receivedClosingHandshake;
  private Utf8Validator utf8Validator;
  
  static enum State
  {
    FRAME_START,  MASKING_KEY,  PAYLOAD,  CORRUPT;
    





    private State() {}
  }
  




  public WebSocket08FrameDecoder(boolean maskedPayload, boolean allowExtensions, int maxFramePayloadLength)
  {
    super(State.FRAME_START);
    this.maskedPayload = maskedPayload;
    this.allowExtensions = allowExtensions;
    this.maxFramePayloadLength = maxFramePayloadLength;
  }
  

  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
    throws Exception
  {
    if (receivedClosingHandshake) {
      in.skipBytes(actualReadableBytes());
      return;
    }
    try
    {
      switch (1.$SwitchMap$io$netty$handler$codec$http$websocketx$WebSocket08FrameDecoder$State[((State)state()).ordinal()]) {
      case 1: 
        framePayloadBytesRead = 0;
        framePayloadLength = -1L;
        framePayload = null;
        payloadBuffer = null;
        

        byte b = in.readByte();
        frameFinalFlag = ((b & 0x80) != 0);
        frameRsv = ((b & 0x70) >> 4);
        frameOpcode = (b & 0xF);
        
        if (logger.isDebugEnabled()) {
          logger.debug("Decoding WebSocket Frame opCode={}", Integer.valueOf(frameOpcode));
        }
        

        b = in.readByte();
        boolean frameMasked = (b & 0x80) != 0;
        int framePayloadLen1 = b & 0x7F;
        
        if ((frameRsv != 0) && (!allowExtensions)) {
          protocolViolation(ctx, "RSV != 0 and no extension negotiated, RSV:" + frameRsv);
          return;
        }
        
        if ((maskedPayload) && (!frameMasked)) {
          protocolViolation(ctx, "unmasked client to server frame");
          return;
        }
        if (frameOpcode > 7)
        {

          if (!frameFinalFlag) {
            protocolViolation(ctx, "fragmented control frame");
            return;
          }
          

          if (framePayloadLen1 > 125) {
            protocolViolation(ctx, "control frame with payload length > 125 octets");
            return;
          }
          

          if ((frameOpcode != 8) && (frameOpcode != 9) && (frameOpcode != 10))
          {
            protocolViolation(ctx, "control frame using reserved opcode " + frameOpcode);
            return;
          }
          



          if ((frameOpcode == 8) && (framePayloadLen1 == 1)) {
            protocolViolation(ctx, "received close control frame with payload len 1");
          }
        }
        else
        {
          if ((frameOpcode != 0) && (frameOpcode != 1) && (frameOpcode != 2))
          {
            protocolViolation(ctx, "data frame using reserved opcode " + frameOpcode);
            return;
          }
          

          if ((fragmentedFramesCount == 0) && (frameOpcode == 0)) {
            protocolViolation(ctx, "received continuation data frame outside fragmented message");
            return;
          }
          

          if ((fragmentedFramesCount != 0) && (frameOpcode != 0) && (frameOpcode != 9)) {
            protocolViolation(ctx, "received non-continuation data frame while inside fragmented message");
            
            return;
          }
        }
        

        if (framePayloadLen1 == 126) {
          framePayloadLength = in.readUnsignedShort();
          if (framePayloadLength < 126L) {
            protocolViolation(ctx, "invalid data frame length (not using minimal length encoding)");
          }
        }
        else if (framePayloadLen1 == 127) {
          framePayloadLength = in.readLong();
          


          if (framePayloadLength < 65536L) {
            protocolViolation(ctx, "invalid data frame length (not using minimal length encoding)");
          }
        }
        else {
          framePayloadLength = framePayloadLen1;
        }
        
        if (framePayloadLength > maxFramePayloadLength) {
          protocolViolation(ctx, "Max frame length of " + maxFramePayloadLength + " has been exceeded.");
          return;
        }
        
        if (logger.isDebugEnabled()) {
          logger.debug("Decoding WebSocket Frame length={}", Long.valueOf(framePayloadLength));
        }
        
        checkpoint(State.MASKING_KEY);
      case 2: 
        if (maskedPayload) {
          if (maskingKey == null) {
            maskingKey = new byte[4];
          }
          in.readBytes(maskingKey);
        }
        checkpoint(State.PAYLOAD);
      

      case 3: 
        int rbytes = actualReadableBytes();
        
        long willHaveReadByteCount = framePayloadBytesRead + rbytes;
        


        if (willHaveReadByteCount == framePayloadLength)
        {
          payloadBuffer = ctx.alloc().buffer(rbytes);
          payloadBuffer.writeBytes(in, rbytes);
        } else { if (willHaveReadByteCount < framePayloadLength)
          {


            if (framePayload == null) {
              framePayload = ctx.alloc().buffer(toFrameLength(framePayloadLength));
            }
            framePayload.writeBytes(in, rbytes);
            framePayloadBytesRead += rbytes;
            

            return; }
          if (willHaveReadByteCount > framePayloadLength)
          {

            if (framePayload == null) {
              framePayload = ctx.alloc().buffer(toFrameLength(framePayloadLength));
            }
            framePayload.writeBytes(in, toFrameLength(framePayloadLength - framePayloadBytesRead));
          }
        }
        

        checkpoint(State.FRAME_START);
        

        if (framePayload == null) {
          framePayload = payloadBuffer;
          payloadBuffer = null;
        } else if (payloadBuffer != null) {
          framePayload.writeBytes(payloadBuffer);
          payloadBuffer.release();
          payloadBuffer = null;
        }
        

        if (maskedPayload) {
          unmask(framePayload);
        }
        


        if (frameOpcode == 9) {
          out.add(new PingWebSocketFrame(frameFinalFlag, frameRsv, framePayload));
          framePayload = null;
          return;
        }
        if (frameOpcode == 10) {
          out.add(new PongWebSocketFrame(frameFinalFlag, frameRsv, framePayload));
          framePayload = null;
          return;
        }
        if (frameOpcode == 8) {
          checkCloseFrameBody(ctx, framePayload);
          receivedClosingHandshake = true;
          out.add(new CloseWebSocketFrame(frameFinalFlag, frameRsv, framePayload));
          framePayload = null;
          return;
        }
        


        if (frameFinalFlag)
        {

          if (frameOpcode != 9) {
            fragmentedFramesCount = 0;
            

            if ((frameOpcode == 1) || ((utf8Validator != null) && (utf8Validator.isChecking())))
            {

              checkUTF8String(ctx, framePayload);
              


              utf8Validator.finish();
            }
          }
        }
        else
        {
          if (fragmentedFramesCount == 0)
          {
            if (frameOpcode == 1) {
              checkUTF8String(ctx, framePayload);
            }
            
          }
          else if ((utf8Validator != null) && (utf8Validator.isChecking())) {
            checkUTF8String(ctx, framePayload);
          }
          


          fragmentedFramesCount += 1;
        }
        

        if (frameOpcode == 1) {
          out.add(new TextWebSocketFrame(frameFinalFlag, frameRsv, framePayload));
          framePayload = null;
          return; }
        if (frameOpcode == 2) {
          out.add(new BinaryWebSocketFrame(frameFinalFlag, frameRsv, framePayload));
          framePayload = null;
          return; }
        if (frameOpcode == 0) {
          out.add(new ContinuationWebSocketFrame(frameFinalFlag, frameRsv, framePayload));
          framePayload = null;
          return;
        }
        throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + frameOpcode);
      



      case 4: 
        in.readByte();
        return;
      }
      throw new Error("Shouldn't reach here.");
    }
    catch (Exception e) {
      if (payloadBuffer != null) {
        if (payloadBuffer.refCnt() > 0) {
          payloadBuffer.release();
        }
        payloadBuffer = null;
      }
      if (framePayload != null) {
        if (framePayload.refCnt() > 0) {
          framePayload.release();
        }
        framePayload = null;
      }
      throw e;
    }
  }
  
  private void unmask(ByteBuf frame) {
    for (int i = frame.readerIndex(); i < frame.writerIndex(); i++) {
      frame.setByte(i, frame.getByte(i) ^ maskingKey[(i % 4)]);
    }
  }
  
  private void protocolViolation(ChannelHandlerContext ctx, String reason) {
    protocolViolation(ctx, new CorruptedFrameException(reason));
  }
  
  private void protocolViolation(ChannelHandlerContext ctx, CorruptedFrameException ex) {
    checkpoint(State.CORRUPT);
    if (ctx.channel().isActive()) {
      ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    throw ex;
  }
  
  private static int toFrameLength(long l) {
    if (l > 2147483647L) {
      throw new TooLongFrameException("Length:" + l);
    }
    return (int)l;
  }
  
  private void checkUTF8String(ChannelHandlerContext ctx, ByteBuf buffer)
  {
    try {
      if (utf8Validator == null) {
        utf8Validator = new Utf8Validator();
      }
      utf8Validator.check(buffer);
    } catch (CorruptedFrameException ex) {
      protocolViolation(ctx, ex);
    }
  }
  

  protected void checkCloseFrameBody(ChannelHandlerContext ctx, ByteBuf buffer)
  {
    if ((buffer == null) || (!buffer.isReadable())) {
      return;
    }
    if (buffer.readableBytes() == 1) {
      protocolViolation(ctx, "Invalid close frame body");
    }
    

    int idx = buffer.readerIndex();
    buffer.readerIndex(0);
    

    int statusCode = buffer.readShort();
    if (((statusCode >= 0) && (statusCode <= 999)) || ((statusCode >= 1004) && (statusCode <= 1006)) || ((statusCode >= 1012) && (statusCode <= 2999)))
    {
      protocolViolation(ctx, "Invalid close frame getStatus code: " + statusCode);
    }
    

    if (buffer.isReadable()) {
      try {
        new Utf8Validator().check(buffer);
      } catch (CorruptedFrameException ex) {
        protocolViolation(ctx, ex);
      }
    }
    

    buffer.readerIndex(idx);
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception
  {
    super.channelInactive(ctx);
    


    if (framePayload != null) {
      framePayload.release();
    }
    if (payloadBuffer != null) {
      payloadBuffer.release();
    }
  }
}
