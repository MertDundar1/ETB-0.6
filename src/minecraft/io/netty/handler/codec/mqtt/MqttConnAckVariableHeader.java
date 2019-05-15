package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;




















public final class MqttConnAckVariableHeader
{
  private final MqttConnectReturnCode connectReturnCode;
  private final boolean sessionPresent;
  
  public MqttConnAckVariableHeader(MqttConnectReturnCode connectReturnCode, boolean sessionPresent)
  {
    this.connectReturnCode = connectReturnCode;
    this.sessionPresent = sessionPresent;
  }
  
  public MqttConnectReturnCode connectReturnCode() {
    return connectReturnCode;
  }
  
  public boolean isSessionPresent() {
    return sessionPresent;
  }
  
  public String toString()
  {
    return StringUtil.simpleClassName(this) + '[' + "connectReturnCode=" + connectReturnCode + ", sessionPresent=" + sessionPresent + ']';
  }
}
