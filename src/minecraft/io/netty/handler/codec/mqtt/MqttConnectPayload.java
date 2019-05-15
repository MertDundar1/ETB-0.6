package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
























public final class MqttConnectPayload
{
  private final String clientIdentifier;
  private final String willTopic;
  private final String willMessage;
  private final String userName;
  private final String password;
  
  public MqttConnectPayload(String clientIdentifier, String willTopic, String willMessage, String userName, String password)
  {
    this.clientIdentifier = clientIdentifier;
    this.willTopic = willTopic;
    this.willMessage = willMessage;
    this.userName = userName;
    this.password = password;
  }
  
  public String clientIdentifier() {
    return clientIdentifier;
  }
  
  public String willTopic() {
    return willTopic;
  }
  
  public String willMessage() {
    return willMessage;
  }
  
  public String userName() {
    return userName;
  }
  
  public String password() {
    return password;
  }
  
  public String toString()
  {
    return StringUtil.simpleClassName(this) + '[' + "clientIdentifier=" + clientIdentifier + ", willTopic=" + willTopic + ", willMessage=" + willMessage + ", userName=" + userName + ", password=" + password + ']';
  }
}
