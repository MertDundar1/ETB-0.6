package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import java.util.ArrayList;
import java.util.List;




























public final class MqttDecoder
  extends ReplayingDecoder<DecoderState>
{
  private static final int DEFAULT_MAX_BYTES_IN_MESSAGE = 8092;
  private MqttFixedHeader mqttFixedHeader;
  private Object variableHeader;
  private Object payload;
  private int bytesRemainingInVariablePart;
  private final int maxBytesInMessage;
  
  static enum DecoderState
  {
    READ_FIXED_HEADER, 
    READ_VARIABLE_HEADER, 
    READ_PAYLOAD, 
    BAD_MESSAGE;
    


    private DecoderState() {}
  }
  


  public MqttDecoder()
  {
    this(8092);
  }
  
  public MqttDecoder(int maxBytesInMessage) {
    super(DecoderState.READ_FIXED_HEADER);
    this.maxBytesInMessage = maxBytesInMessage;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception
  {
    switch (1.$SwitchMap$io$netty$handler$codec$mqtt$MqttDecoder$DecoderState[((DecoderState)state()).ordinal()]) {
    case 1: 
      mqttFixedHeader = decodeFixedHeader(buffer);
      bytesRemainingInVariablePart = mqttFixedHeader.remainingLength();
      checkpoint(DecoderState.READ_VARIABLE_HEADER);
    case 2: 
      try
      {
        if (bytesRemainingInVariablePart > maxBytesInMessage) {
          throw new DecoderException("too large message: " + bytesRemainingInVariablePart + " bytes");
        }
        Result<?> decodedVariableHeader = decodeVariableHeader(buffer, mqttFixedHeader);
        variableHeader = value;
        bytesRemainingInVariablePart -= numberOfBytesConsumed;
        checkpoint(DecoderState.READ_PAYLOAD);
      }
      catch (Exception cause) {
        out.add(invalidMessage(cause));
        return;
      }
    case 3: 
      try {
        Result<?> decodedPayload = decodePayload(buffer, mqttFixedHeader.messageType(), bytesRemainingInVariablePart, variableHeader);
        




        payload = value;
        bytesRemainingInVariablePart -= numberOfBytesConsumed;
        if (bytesRemainingInVariablePart != 0) {
          throw new DecoderException("non-zero remaining payload bytes: " + bytesRemainingInVariablePart + " (" + mqttFixedHeader.messageType() + ')');
        }
        

        checkpoint(DecoderState.READ_FIXED_HEADER);
        MqttMessage message = MqttMessageFactory.newMessage(mqttFixedHeader, variableHeader, payload);
        mqttFixedHeader = null;
        variableHeader = null;
        payload = null;
        out.add(message);
      }
      catch (Exception cause) {
        out.add(invalidMessage(cause));
        return;
      }
    

    case 4: 
      buffer.skipBytes(actualReadableBytes());
      break;
    }
    
    
    throw new Error();
  }
  
  private MqttMessage invalidMessage(Throwable cause)
  {
    checkpoint(DecoderState.BAD_MESSAGE);
    return MqttMessageFactory.newInvalidMessage(cause);
  }
  





  private static MqttFixedHeader decodeFixedHeader(ByteBuf buffer)
  {
    short b1 = buffer.readUnsignedByte();
    
    MqttMessageType messageType = MqttMessageType.valueOf(b1 >> 4);
    boolean dupFlag = (b1 & 0x8) == 8;
    int qosLevel = (b1 & 0x6) >> 1;
    boolean retain = (b1 & 0x1) != 0;
    
    int remainingLength = 0;
    int multiplier = 1;
    
    int loops = 0;
    short digit;
    do { digit = buffer.readUnsignedByte();
      remainingLength += (digit & 0x7F) * multiplier;
      multiplier *= 128;
      loops++;
    } while (((digit & 0x80) != 0) && (loops < 4));
    

    if ((loops == 4) && ((digit & 0x80) != 0)) {
      throw new DecoderException("remaining length exceeds 4 digits (" + messageType + ')');
    }
    MqttFixedHeader decodedFixedHeader = new MqttFixedHeader(messageType, dupFlag, MqttQoS.valueOf(qosLevel), retain, remainingLength);
    
    return MqttCodecUtil.validateFixedHeader(MqttCodecUtil.resetUnusedFields(decodedFixedHeader));
  }
  





  private static Result<?> decodeVariableHeader(ByteBuf buffer, MqttFixedHeader mqttFixedHeader)
  {
    switch (1.$SwitchMap$io$netty$handler$codec$mqtt$MqttMessageType[mqttFixedHeader.messageType().ordinal()]) {
    case 1: 
      return decodeConnectionVariableHeader(buffer);
    
    case 2: 
      return decodeConnAckVariableHeader(buffer);
    
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
      return decodeMessageIdVariableHeader(buffer);
    
    case 11: 
      return decodePublishVariableHeader(buffer, mqttFixedHeader);
    

    case 12: 
    case 13: 
    case 14: 
      return new Result(null, 0);
    }
    return new Result(null, 0);
  }
  
  private static Result<MqttConnectVariableHeader> decodeConnectionVariableHeader(ByteBuf buffer) {
    Result<String> protoString = decodeString(buffer);
    int numberOfBytesConsumed = numberOfBytesConsumed;
    
    byte protocolLevel = buffer.readByte();
    numberOfBytesConsumed++;
    
    MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel((String)value, protocolLevel);
    
    int b1 = buffer.readUnsignedByte();
    numberOfBytesConsumed++;
    
    Result<Integer> keepAlive = decodeMsbLsb(buffer);
    numberOfBytesConsumed += numberOfBytesConsumed;
    
    boolean hasUserName = (b1 & 0x80) == 128;
    boolean hasPassword = (b1 & 0x40) == 64;
    boolean willRetain = (b1 & 0x20) == 32;
    int willQos = (b1 & 0x18) >> 3;
    boolean willFlag = (b1 & 0x4) == 4;
    boolean cleanSession = (b1 & 0x2) == 2;
    if (mqttVersion == MqttVersion.MQTT_3_1_1) {
      boolean zeroReservedFlag = (b1 & 0x1) == 0;
      if (!zeroReservedFlag)
      {


        throw new DecoderException("non-zero reserved flag");
      }
    }
    
    MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(mqttVersion.protocolName(), mqttVersion.protocolLevel(), hasUserName, hasPassword, willRetain, willQos, willFlag, cleanSession, ((Integer)value).intValue());
    








    return new Result(mqttConnectVariableHeader, numberOfBytesConsumed);
  }
  
  private static Result<MqttConnAckVariableHeader> decodeConnAckVariableHeader(ByteBuf buffer) {
    boolean sessionPresent = (buffer.readUnsignedByte() & 0x1) == 1;
    byte returnCode = buffer.readByte();
    int numberOfBytesConsumed = 2;
    MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.valueOf(returnCode), sessionPresent);
    
    return new Result(mqttConnAckVariableHeader, 2);
  }
  
  private static Result<MqttMessageIdVariableHeader> decodeMessageIdVariableHeader(ByteBuf buffer) {
    Result<Integer> messageId = decodeMessageId(buffer);
    return new Result(MqttMessageIdVariableHeader.from(((Integer)value).intValue()), numberOfBytesConsumed);
  }
  



  private static Result<MqttPublishVariableHeader> decodePublishVariableHeader(ByteBuf buffer, MqttFixedHeader mqttFixedHeader)
  {
    Result<String> decodedTopic = decodeString(buffer);
    if (!MqttCodecUtil.isValidPublishTopicName((String)value)) {
      throw new DecoderException("invalid publish topic name: " + (String)value + " (contains wildcards)");
    }
    int numberOfBytesConsumed = numberOfBytesConsumed;
    
    int messageId = -1;
    if (mqttFixedHeader.qosLevel().value() > 0) {
      Result<Integer> decodedMessageId = decodeMessageId(buffer);
      messageId = ((Integer)value).intValue();
      numberOfBytesConsumed += numberOfBytesConsumed;
    }
    MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader((String)value, messageId);
    
    return new Result(mqttPublishVariableHeader, numberOfBytesConsumed);
  }
  
  private static Result<Integer> decodeMessageId(ByteBuf buffer) {
    Result<Integer> messageId = decodeMsbLsb(buffer);
    if (!MqttCodecUtil.isValidMessageId(((Integer)value).intValue())) {
      throw new DecoderException("invalid messageId: " + value);
    }
    return messageId;
  }
  












  private static Result<?> decodePayload(ByteBuf buffer, MqttMessageType messageType, int bytesRemainingInVariablePart, Object variableHeader)
  {
    switch (1.$SwitchMap$io$netty$handler$codec$mqtt$MqttMessageType[messageType.ordinal()]) {
    case 1: 
      return decodeConnectionPayload(buffer, (MqttConnectVariableHeader)variableHeader);
    
    case 3: 
      return decodeSubscribePayload(buffer, bytesRemainingInVariablePart);
    
    case 5: 
      return decodeSubackPayload(buffer, bytesRemainingInVariablePart);
    
    case 4: 
      return decodeUnsubscribePayload(buffer, bytesRemainingInVariablePart);
    
    case 11: 
      return decodePublishPayload(buffer, bytesRemainingInVariablePart);
    }
    
    
    return new Result(null, 0);
  }
  


  private static Result<MqttConnectPayload> decodeConnectionPayload(ByteBuf buffer, MqttConnectVariableHeader mqttConnectVariableHeader)
  {
    Result<String> decodedClientId = decodeString(buffer);
    String decodedClientIdValue = (String)value;
    MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(mqttConnectVariableHeader.name(), (byte)mqttConnectVariableHeader.version());
    
    if (!MqttCodecUtil.isValidClientId(mqttVersion, decodedClientIdValue)) {
      throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + decodedClientIdValue);
    }
    int numberOfBytesConsumed = numberOfBytesConsumed;
    
    Result<String> decodedWillTopic = null;
    Result<String> decodedWillMessage = null;
    if (mqttConnectVariableHeader.isWillFlag()) {
      decodedWillTopic = decodeString(buffer, 0, 32767);
      numberOfBytesConsumed += numberOfBytesConsumed;
      decodedWillMessage = decodeAsciiString(buffer);
      numberOfBytesConsumed += numberOfBytesConsumed;
    }
    Result<String> decodedUserName = null;
    Result<String> decodedPassword = null;
    if (mqttConnectVariableHeader.hasUserName()) {
      decodedUserName = decodeString(buffer);
      numberOfBytesConsumed += numberOfBytesConsumed;
    }
    if (mqttConnectVariableHeader.hasPassword()) {
      decodedPassword = decodeString(buffer);
      numberOfBytesConsumed += numberOfBytesConsumed;
    }
    
    MqttConnectPayload mqttConnectPayload = new MqttConnectPayload((String)value, decodedWillTopic != null ? (String)value : null, decodedWillMessage != null ? (String)value : null, decodedUserName != null ? (String)value : null, decodedPassword != null ? (String)value : null);
    





    return new Result(mqttConnectPayload, numberOfBytesConsumed);
  }
  

  private static Result<MqttSubscribePayload> decodeSubscribePayload(ByteBuf buffer, int bytesRemainingInVariablePart)
  {
    List<MqttTopicSubscription> subscribeTopics = new ArrayList();
    int numberOfBytesConsumed = 0;
    while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
      Result<String> decodedTopicName = decodeString(buffer);
      numberOfBytesConsumed += numberOfBytesConsumed;
      int qos = buffer.readUnsignedByte() & 0x3;
      numberOfBytesConsumed++;
      subscribeTopics.add(new MqttTopicSubscription((String)value, MqttQoS.valueOf(qos)));
    }
    return new Result(new MqttSubscribePayload(subscribeTopics), numberOfBytesConsumed);
  }
  

  private static Result<MqttSubAckPayload> decodeSubackPayload(ByteBuf buffer, int bytesRemainingInVariablePart)
  {
    List<Integer> grantedQos = new ArrayList();
    int numberOfBytesConsumed = 0;
    while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
      int qos = buffer.readUnsignedByte() & 0x3;
      numberOfBytesConsumed++;
      grantedQos.add(Integer.valueOf(qos));
    }
    return new Result(new MqttSubAckPayload(grantedQos), numberOfBytesConsumed);
  }
  

  private static Result<MqttUnsubscribePayload> decodeUnsubscribePayload(ByteBuf buffer, int bytesRemainingInVariablePart)
  {
    List<String> unsubscribeTopics = new ArrayList();
    int numberOfBytesConsumed = 0;
    while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
      Result<String> decodedTopicName = decodeString(buffer);
      numberOfBytesConsumed += numberOfBytesConsumed;
      unsubscribeTopics.add(value);
    }
    return new Result(new MqttUnsubscribePayload(unsubscribeTopics), numberOfBytesConsumed);
  }
  

  private static Result<ByteBuf> decodePublishPayload(ByteBuf buffer, int bytesRemainingInVariablePart)
  {
    ByteBuf b = buffer.readRetainedSlice(bytesRemainingInVariablePart);
    return new Result(b, bytesRemainingInVariablePart);
  }
  
  private static Result<String> decodeString(ByteBuf buffer) {
    return decodeString(buffer, 0, Integer.MAX_VALUE);
  }
  
  private static Result<String> decodeAsciiString(ByteBuf buffer) {
    Result<String> result = decodeString(buffer, 0, Integer.MAX_VALUE);
    String s = (String)value;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) > '') {
        return new Result(null, numberOfBytesConsumed);
      }
    }
    return new Result(s, numberOfBytesConsumed);
  }
  
  private static Result<String> decodeString(ByteBuf buffer, int minBytes, int maxBytes) {
    Result<Integer> decodedSize = decodeMsbLsb(buffer);
    int size = ((Integer)value).intValue();
    int numberOfBytesConsumed = numberOfBytesConsumed;
    if ((size < minBytes) || (size > maxBytes)) {
      buffer.skipBytes(size);
      numberOfBytesConsumed += size;
      return new Result(null, numberOfBytesConsumed);
    }
    String s = buffer.toString(buffer.readerIndex(), size, CharsetUtil.UTF_8);
    buffer.skipBytes(size);
    numberOfBytesConsumed += size;
    return new Result(s, numberOfBytesConsumed);
  }
  
  private static Result<Integer> decodeMsbLsb(ByteBuf buffer) {
    return decodeMsbLsb(buffer, 0, 65535);
  }
  
  private static Result<Integer> decodeMsbLsb(ByteBuf buffer, int min, int max) {
    short msbSize = buffer.readUnsignedByte();
    short lsbSize = buffer.readUnsignedByte();
    int numberOfBytesConsumed = 2;
    int result = msbSize << 8 | lsbSize;
    if ((result < min) || (result > max)) {
      result = -1;
    }
    return new Result(Integer.valueOf(result), 2);
  }
  
  private static final class Result<T>
  {
    private final T value;
    private final int numberOfBytesConsumed;
    
    Result(T value, int numberOfBytesConsumed) {
      this.value = value;
      this.numberOfBytesConsumed = numberOfBytesConsumed;
    }
  }
}
