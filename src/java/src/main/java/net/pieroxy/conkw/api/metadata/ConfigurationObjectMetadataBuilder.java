package net.pieroxy.conkw.api.metadata;

public class ConfigurationObjectMetadataBuilder {
  public static ConfigurationObjectMetadata buildMetadata(Class c) {
    return new ConfigurationObjectMetadata(c.getName());
  }
}
