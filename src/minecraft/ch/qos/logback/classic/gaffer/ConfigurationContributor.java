package ch.qos.logback.classic.gaffer;

import java.util.Map;

public abstract interface ConfigurationContributor
{
  public abstract Map<String, String> getMappings();
}
