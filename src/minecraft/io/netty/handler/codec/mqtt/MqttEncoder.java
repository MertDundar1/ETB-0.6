package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.EmptyArrays;
import java.util.Iterator;
import java.util.List;






















@ChannelHandler.Sharable
public final class MqttEncoder
  extends MessageToMessageEncoder<MqttMessage>
{
  public static final MqttEncoder INSTANCE = new MqttEncoder();
  
  private MqttEncoder() {}
  
  protected void encode(ChannelHandlerContext ctx, MqttMessage msg, List<Object> out) throws Exception
  {
    out.add(doEncode(ctx.alloc(), msg));
  }
  








  static ByteBuf doEncode(ByteBufAllocator byteBufAllocator, MqttMessage message)
  {
    switch (1.$SwitchMap$io$netty$handler$codec$mqtt$MqttMessageType[message.fixedHeader().messageType().ordinal()]) {
    case 1: 
      return encodeConnectMessage(byteBufAllocator, (MqttConnectMessage)message);
    
    case 2: 
      return encodeConnAckMessage(byteBufAllocator, (MqttConnAckMessage)message);
    
    case 3: 
      return encodePublishMessage(byteBufAllocator, (MqttPublishMessage)message);
    
    case 4: 
      return encodeSubscribeMessage(byteBufAllocator, (MqttSubscribeMessage)message);
    
    case 5: 
      return encodeUnsubscribeMessage(byteBufAllocator, (MqttUnsubscribeMessage)message);
    
    case 6: 
      return encodeSubAckMessage(byteBufAllocator, (MqttSubAckMessage)message);
    
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
      return encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(byteBufAllocator, message);
    
    case 12: 
    case 13: 
    case 14: 
      return encodeMessageWithOnlySingleByteFixedHeader(byteBufAllocator, message);
    }
    
    throw new IllegalArgumentException("Unknown message type: " + message.fixedHeader().messageType().value());
  }
  



  private static ByteBuf encodeConnectMessage(ByteBufAllocator byteBufAllocator, MqttConnectMessage message)
  {
    int payloadBufferSize = 0;
    
    MqttFixedHeader mqttFixedHeader = message.fixedHeader();
    MqttConnectVariableHeader variableHeader = message.variableHeader();
    MqttConnectPayload payload = message.payload();
    MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(variableHeader.name(), (byte)variableHeader.version());
    


    String clientIdentifier = payload.clientIdentifier();
    if (!MqttCodecUtil.isValidClientId(mqttVersion, clientIdentifier)) {
      throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + clientIdentifier);
    }
    byte[] clientIdentifierBytes = encodeStringUtf8(clientIdentifier);
    payloadBufferSize += 2 + clientIdentifierBytes.length;
    

    String willTopic = payload.willTopic();
    byte[] willTopicBytes = willTopic != null ? encodeStringUtf8(willTopic) : EmptyArrays.EMPTY_BYTES;
    String willMessage = payload.willMessage();
    byte[] willMessageBytes = willMessage != null ? encodeStringUtf8(willMessage) : EmptyArrays.EMPTY_BYTES;
    if (variableHeader.isWillFlag()) {
      payloadBufferSize += 2 + willTopicBytes.length;
      payloadBufferSize += 2 + willMessageBytes.length;
    }
    
    String userName = payload.userName();
    byte[] userNameBytes = userName != null ? encodeStringUtf8(userName) : EmptyArrays.EMPTY_BYTES;
    if (variableHeader.hasUserName()) {
      payloadBufferSize += 2 + userNameBytes.length;
    }
    
    String password = payload.password();
    byte[] passwordBytes = password != null ? encodeStringUtf8(password) : EmptyArrays.EMPTY_BYTES;
    if (variableHeader.hasPassword()) {
      payloadBufferSize += 2 + passwordBytes.length;
    }
    

    byte[] protocolNameBytes = mqttVersion.protocolNameBytes();
    int variableHeaderBufferSize = 2 + protocolNameBytes.length + 4;
    int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
    int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
    ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
    buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
    writeVariableLengthInt(buf, variablePartSize);
    
    buf.writeShort(protocolNameBytes.length);
    buf.writeBytes(protocolNameBytes);
    
    buf.writeByte(variableHeader.version());
    buf.writeByte(getConnVariableHeaderFlag(variableHeader));
    buf.writeShort(variableHeader.keepAliveTimeSeconds());
    

    buf.writeShort(clientIdentifierBytes.length);
    buf.writeBytes(clientIdentifierBytes, 0, clientIdentifierBytes.length);
    if (variableHeader.isWillFlag()) {
      buf.writeShort(willTopicBytes.length);
      buf.writeBytes(willTopicBytes, 0, willTopicBytes.length);
      buf.writeShort(willMessageBytes.length);
      buf.writeBytes(willMessageBytes, 0, willMessageBytes.length);
    }
    if (variableHeader.hasUserName()) {
      buf.writeShort(userNameBytes.length);
      buf.writeBytes(userNameBytes, 0, userNameBytes.length);
    }
    if (variableHeader.hasPassword()) {
      buf.writeShort(passwordBytes.length);
      buf.writeBytes(passwordBytes, 0, passwordBytes.length);
    }
    return buf;
  }
  
  private static int getConnVariableHeaderFlag(MqttConnectVariableHeader variableHeader) {
    int flagByte = 0;
    if (variableHeader.hasUserName()) {
      flagByte |= 0x80;
    }
    if (variableHeader.hasPassword()) {
      flagByte |= 0x40;
    }
    if (variableHeader.isWillRetain()) {
      flagByte |= 0x20;
    }
    flagByte |= (variableHeader.willQos() & 0x3) << 3;
    if (variableHeader.isWillFlag()) {
      flagByte |= 0x4;
    }
    if (variableHeader.isCleanSession()) {
      flagByte |= 0x2;
    }
    return flagByte;
  }
  

  private static ByteBuf encodeConnAckMessage(ByteBufAllocator byteBufAllocator, MqttConnAckMessage message)
  {
    ByteBuf buf = byteBufAllocator.buffer(4);
    buf.writeByte(getFixedHeaderByte1(message.fixedHeader()));
    buf.writeByte(2);
    buf.writeByte(message.variableHeader().isSessionPresent() ? 1 : 0);
    buf.writeByte(message.variableHeader().connectReturnCode().byteValue());
    
    return buf;
  }
  

  private static ByteBuf encodeSubscribeMessage(ByteBufAllocator byteBufAllocator, MqttSubscribeMessage message)
  {
    int variableHeaderBufferSize = 2;
    int payloadBufferSize = 0;
    
    MqttFixedHeader mqttFixedHeader = message.fixedHeader();
    MqttMessageIdVariableHeader variableHeader = message.variableHeader();
    MqttSubscribePayload payload = message.payload();
    
    for (MqttTopicSubscription topic : payload.topicSubscriptions()) {
      String topicName = topic.topicName();
      byte[] topicNameBytes = encodeStringUtf8(topicName);
      payloadBufferSize += 2 + topicNameBytes.length;
      payloadBufferSize++;
    }
    
    int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
    int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
    
    ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
    buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
    writeVariableLengthInt(buf, variablePartSize);
    

    int messageId = variableHeader.messageId();
    buf.writeShort(messageId);
    

    for (MqttTopicSubscription topic : payload.topicSubscriptions()) {
      String topicName = topic.topicName();
      byte[] topicNameBytes = encodeStringUtf8(topicName);
      buf.writeShort(topicNameBytes.length);
      buf.writeBytes(topicNameBytes, 0, topicNameBytes.length);
      buf.writeByte(topic.qualityOfService().value());
    }
    
    return buf;
  }
  

  private static ByteBuf encodeUnsubscribeMessage(ByteBufAllocator byteBufAllocator, MqttUnsubscribeMessage message)
  {
    int variableHeaderBufferSize = 2;
    int payloadBufferSize = 0;
    
    MqttFixedHeader mqttFixedHeader = message.fixedHeader();
    MqttMessageIdVariableHeader variableHeader = message.variableHeader();
    MqttUnsubscribePayload payload = message.payload();
    
    for (String topicName : payload.topics()) {
      byte[] topicNameBytes = encodeStringUtf8(topicName);
      payloadBufferSize += 2 + topicNameBytes.length;
    }
    
    int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
    int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
    
    ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
    buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
    writeVariableLengthInt(buf, variablePartSize);
    

    int messageId = variableHeader.messageId();
    buf.writeShort(messageId);
    

    for (String topicName : payload.topics()) {
      byte[] topicNameBytes = encodeStringUtf8(topicName);
      buf.writeShort(topicNameBytes.length);
      buf.writeBytes(topicNameBytes, 0, topicNameBytes.length);
    }
    
    return buf;
  }
  

  private static ByteBuf encodeSubAckMessage(ByteBufAllocator byteBufAllocator, MqttSubAckMessage message)
  {
    int variableHeaderBufferSize = 2;
    int payloadBufferSize = message.payload().grantedQoSLevels().size();
    int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
    int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
    ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
    buf.writeByte(getFixedHeaderByte1(message.fixedHeader()));
    writeVariableLengthInt(buf, variablePartSize);
    buf.writeShort(message.variableHeader().messageId());
    for (Iterator i$ = message.payload().grantedQoSLevels().iterator(); i$.hasNext();) { int qos = ((Integer)i$.next()).intValue();
      buf.writeByte(qos);
    }
    
    return buf;
  }
  

  private static ByteBuf encodePublishMessage(ByteBufAllocator byteBufAllocator, MqttPublishMessage message)
  {
    MqttFixedHeader mqttFixedHeader = message.fixedHeader();
    MqttPublishVariableHeader variableHeader = message.variableHeader();
    ByteBuf payload = message.payload().duplicate();
    
    String topicName = variableHeader.topicName();
    byte[] topicNameBytes = encodeStringUtf8(topicName);
    
    int variableHeaderBufferSize = 2 + topicNameBytes.length + (mqttFixedHeader.qosLevel().value() > 0 ? 2 : 0);
    
    int payloadBufferSize = payload.readableBytes();
    int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
    int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
    
    ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
    buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
    writeVariableLengthInt(buf, variablePartSize);
    buf.writeShort(topicNameBytes.length);
    buf.writeBytes(topicNameBytes);
    if (mqttFixedHeader.qosLevel().value() > 0) {
      buf.writeShort(variableHeader.messageId());
    }
    buf.writeBytes(payload);
    
    return buf;
  }
  

  private static ByteBuf encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(ByteBufAllocator byteBufAllocator, MqttMessage message)
  {
    MqttFixedHeader mqttFixedHeader = message.fixedHeader();
    MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader)message.variableHeader();
    int msgId = variableHeader.messageId();
    
    int variableHeaderBufferSize = 2;
    int fixedHeaderBufferSize = 1 + getVariableLengthInt(variableHeaderBufferSize);
    ByteBuf buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variableHeaderBufferSize);
    buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
    writeVariableLengthInt(buf, variableHeaderBufferSize);
    buf.writeShort(msgId);
    
    return buf;
  }
  

  private static ByteBuf encodeMessageWithOnlySingleByteFixedHeader(ByteBufAllocator byteBufAllocator, MqttMessage message)
  {
    MqttFixedHeader mqttFixedHeader = message.fixedHeader();
    ByteBuf buf = byteBufAllocator.buffer(2);
    buf.writeByte(getFixedHeaderByte1(mqttFixedHeader));
    buf.writeByte(0);
    
    return buf;
  }
  
  private static int getFixedHeaderByte1(MqttFixedHeader header) {
    int ret = 0;
    ret |= header.messageType().value() << 4;
    if (header.isDup()) {
      ret |= 0x8;
    }
    ret |= header.qosLevel().value() << 1;
    if (header.isRetain()) {
      ret |= 0x1;
    }
    return ret;
  }
  
  private static void writeVariableLengthInt(ByteBuf buf, int num) {
    do {
      int digit = num % 128;
      num /= 128;
      if (num > 0) {
        digit |= 0x80;
      }
      buf.writeByte(digit);
    } while (num > 0);
  }
  
  private static int getVariableLengthInt(int num) {
    int count = 0;
    do {
      num /= 128;
      count++;
    } while (num > 0);
    return count;
  }
  
  private static byte[] encodeStringUtf8(String s) {
    return s.getBytes(CharsetUtil.UTF_8);
  }
}
