package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;




















public final class MqttTopicSubscription
{
  private final String topicFilter;
  private final MqttQoS qualityOfService;
  
  public MqttTopicSubscription(String topicFilter, MqttQoS qualityOfService)
  {
    this.topicFilter = topicFilter;
    this.qualityOfService = qualityOfService;
  }
  
  public String topicName() {
    return topicFilter;
  }
  
  public MqttQoS qualityOfService() {
    return qualityOfService;
  }
  
  public String toString()
  {
    return StringUtil.simpleClassName(this) + '[' + "topicFilter=" + topicFilter + ", qualityOfService=" + qualityOfService + ']';
  }
}
