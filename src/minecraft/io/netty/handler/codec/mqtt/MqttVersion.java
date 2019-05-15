package io.netty.handler.codec.mqtt;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;



















public enum MqttVersion
{
  MQTT_3_1("MQIsdp", (byte)3), 
  MQTT_3_1_1("MQTT", (byte)4);
  
  private final String name;
  private final byte level;
  
  private MqttVersion(String protocolName, byte protocolLevel) {
    name = ((String)ObjectUtil.checkNotNull(protocolName, "protocolName"));
    level = protocolLevel;
  }
  
  public String protocolName() {
    return name;
  }
  
  public byte[] protocolNameBytes() {
    return name.getBytes(CharsetUtil.UTF_8);
  }
  
  public byte protocolLevel() {
    return level;
  }
  
  public static MqttVersion fromProtocolNameAndLevel(String protocolName, byte protocolLevel) {
    for (MqttVersion mv : ) {
      if (name.equals(protocolName)) {
        if (level == protocolLevel) {
          return mv;
        }
        throw new MqttUnacceptableProtocolVersionException(protocolName + " and " + protocolLevel + " are not match");
      }
    }
    

    throw new MqttUnacceptableProtocolVersionException(protocolName + "is unknown protocol name");
  }
}
