package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.AppendableCharSequence;
import java.util.List;
import java.util.Locale;






































public class StompSubframeDecoder
  extends ReplayingDecoder<State>
{
  private static final int DEFAULT_CHUNK_SIZE = 8132;
  private static final int DEFAULT_MAX_LINE_LENGTH = 1024;
  private final int maxLineLength;
  private final int maxChunkSize;
  private int alreadyReadChunkSize;
  private LastStompContentSubframe lastContent;
  
  static enum State
  {
    SKIP_CONTROL_CHARACTERS, 
    READ_HEADERS, 
    READ_CONTENT, 
    FINALIZE_FRAME_READ, 
    BAD_FRAME, 
    INVALID_CHUNK;
    

    private State() {}
  }
  

  private long contentLength = -1L;
  
  public StompSubframeDecoder() {
    this(1024, 8132);
  }
  
  public StompSubframeDecoder(int maxLineLength, int maxChunkSize) {
    super(State.SKIP_CONTROL_CHARACTERS);
    if (maxLineLength <= 0) {
      throw new IllegalArgumentException("maxLineLength must be a positive integer: " + maxLineLength);
    }
    

    if (maxChunkSize <= 0) {
      throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + maxChunkSize);
    }
    

    this.maxChunkSize = maxChunkSize;
    this.maxLineLength = maxLineLength;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
  {
    switch (1.$SwitchMap$io$netty$handler$codec$stomp$StompSubframeDecoder$State[((State)state()).ordinal()]) {
    case 1: 
      skipControlCharacters(in);
      checkpoint(State.READ_HEADERS);
    
    case 2: 
      StompCommand command = StompCommand.UNKNOWN;
      StompHeadersSubframe frame = null;
      try {
        command = readCommand(in);
        frame = new DefaultStompHeadersSubframe(command);
        checkpoint(readHeaders(in, frame.headers()));
        out.add(frame);
      } catch (Exception e) {
        if (frame == null) {
          frame = new DefaultStompHeadersSubframe(command);
        }
        frame.setDecoderResult(DecoderResult.failure(e));
        out.add(frame);
        checkpoint(State.BAD_FRAME);
        return;
      }
    
    case 3: 
      in.skipBytes(actualReadableBytes());
      return;
    }
    try {
      switch (1.$SwitchMap$io$netty$handler$codec$stomp$StompSubframeDecoder$State[((State)state()).ordinal()]) {
      case 4: 
        int toRead = in.readableBytes();
        if (toRead == 0) {
          return;
        }
        if (toRead > maxChunkSize) {
          toRead = maxChunkSize;
        }
        if (contentLength >= 0L) {
          int remainingLength = (int)(contentLength - alreadyReadChunkSize);
          if (toRead > remainingLength) {
            toRead = remainingLength;
          }
          ByteBuf chunkBuffer = ByteBufUtil.readBytes(ctx.alloc(), in, toRead);
          if (this.alreadyReadChunkSize += toRead >= contentLength) {
            lastContent = new DefaultLastStompContentSubframe(chunkBuffer);
            checkpoint(State.FINALIZE_FRAME_READ);
          } else {
            out.add(new DefaultStompContentSubframe(chunkBuffer));
            return;
          }
        } else {
          int nulIndex = ByteBufUtil.indexOf(in, in.readerIndex(), in.writerIndex(), (byte)0);
          if (nulIndex == in.readerIndex()) {
            checkpoint(State.FINALIZE_FRAME_READ);
          } else {
            if (nulIndex > 0) {
              toRead = nulIndex - in.readerIndex();
            } else {
              toRead = in.writerIndex() - in.readerIndex();
            }
            ByteBuf chunkBuffer = ByteBufUtil.readBytes(ctx.alloc(), in, toRead);
            alreadyReadChunkSize += toRead;
            if (nulIndex > 0) {
              lastContent = new DefaultLastStompContentSubframe(chunkBuffer);
              checkpoint(State.FINALIZE_FRAME_READ);
            } else {
              out.add(new DefaultStompContentSubframe(chunkBuffer));
              return;
            }
          }
        }
      
      case 5: 
        skipNullCharacter(in);
        if (lastContent == null) {
          lastContent = LastStompContentSubframe.EMPTY_LAST_CONTENT;
        }
        out.add(lastContent);
        resetDecoder();
      }
    } catch (Exception e) {
      StompContentSubframe errorContent = new DefaultLastStompContentSubframe(Unpooled.EMPTY_BUFFER);
      errorContent.setDecoderResult(DecoderResult.failure(e));
      out.add(errorContent);
      checkpoint(State.BAD_FRAME);
    }
  }
  
  private StompCommand readCommand(ByteBuf in) {
    String commandStr = readLine(in, maxLineLength);
    StompCommand command = null;
    try {
      command = StompCommand.valueOf(commandStr);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    
    if (command == null) {
      commandStr = commandStr.toUpperCase(Locale.US);
      try {
        command = StompCommand.valueOf(commandStr);
      }
      catch (IllegalArgumentException localIllegalArgumentException1) {}
    }
    
    if (command == null) {
      throw new DecoderException("failed to read command from channel");
    }
    return command;
  }
  
  private State readHeaders(ByteBuf buffer, StompHeaders headers) {
    for (;;) {
      String line = readLine(buffer, maxLineLength);
      if (!line.isEmpty()) {
        String[] split = line.split(":");
        if (split.length == 2) {
          headers.add(split[0], split[1]);
        }
      } else {
        if (headers.contains(StompHeaders.CONTENT_LENGTH)) {
          contentLength = getContentLength(headers, 0L);
          if (contentLength == 0L) {
            return State.FINALIZE_FRAME_READ;
          }
        }
        return State.READ_CONTENT;
      }
    }
  }
  
  private static long getContentLength(StompHeaders headers, long defaultValue) {
    long contentLength = headers.getLong(StompHeaders.CONTENT_LENGTH, defaultValue);
    if (contentLength < 0L) {
      throw new DecoderException(StompHeaders.CONTENT_LENGTH + " must be non-negative");
    }
    return contentLength;
  }
  
  private static void skipNullCharacter(ByteBuf buffer) {
    byte b = buffer.readByte();
    if (b != 0) {
      throw new IllegalStateException("unexpected byte in buffer " + b + " while expecting NULL byte");
    }
  }
  
  private static void skipControlCharacters(ByteBuf buffer) {
    byte b;
    do {
      b = buffer.readByte();
    } while ((b == 13) || (b == 10));
    buffer.readerIndex(buffer.readerIndex() - 1);
  }
  


  private static String readLine(ByteBuf buffer, int maxLineLength)
  {
    AppendableCharSequence buf = new AppendableCharSequence(128);
    int lineLength = 0;
    for (;;) {
      byte nextByte = buffer.readByte();
      if (nextByte == 13) {
        nextByte = buffer.readByte();
        if (nextByte == 10)
          return buf.toString();
      } else {
        if (nextByte == 10) {
          return buf.toString();
        }
        if (lineLength >= maxLineLength) {
          throw new TooLongFrameException("An STOMP line is larger than " + maxLineLength + " bytes.");
        }
        lineLength++;
        buf.append((char)nextByte);
      }
    }
  }
  
  private void resetDecoder() {
    checkpoint(State.SKIP_CONTROL_CHARACTERS);
    contentLength = -1L;
    alreadyReadChunkSize = 0;
    lastContent = null;
  }
}
