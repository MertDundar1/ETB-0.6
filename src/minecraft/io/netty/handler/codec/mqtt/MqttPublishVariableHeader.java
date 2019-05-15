package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;



















public final class MqttPublishVariableHeader
{
  private final String topicName;
  private final int messageId;
  
  public MqttPublishVariableHeader(String topicName, int messageId)
  {
    this.topicName = topicName;
    this.messageId = messageId;
  }
  
  public String topicName() {
    return topicName;
  }
  
  public int messageId() {
    return messageId;
  }
  
  public String toString()
  {
    return StringUtil.simpleClassName(this) + '[' + "topicName=" + topicName + ", messageId=" + messageId + ']';
  }
}
