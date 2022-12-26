package net.pieroxy.conkw.api.metadata.grabberConfig;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationObjectMetadataBuilder {
  private static final Logger LOGGER = Logger.getLogger(ConfigurationObjectMetadataBuilder.class.getName());

  public static ConfigurationObjectMetadata buildMetadata(Class c) {
    ConfigurationObjectMetadata result = new ConfigurationObjectMetadata(c.getName());


    for (Field f : c.getDeclaredFields()) {
      ConfigField annotation = f.getAnnotation(ConfigField.class);
      if (LOGGER.isLoggable(Level.FINE)) LOGGER.fine("Field " + f.getName() + " gives annotation " + annotation);
      if (annotation!=null) {
        result.getFields().add(getField(annotation, f));
      }
    }
    if (LOGGER.isLoggable(Level.FINE)) LOGGER.fine("Fields " + result.getFields().size());

    return result;
  }

  private static ConfigurationObjectFieldMetadata getField(ConfigField annotation, Field f) {
    ConfigurationObjectFieldMetadata res = new ConfigurationObjectFieldMetadata();
    res.setName(f.getName());
    res.setLabel(annotation.label());
    res.setDefaultValue(annotation.defaultValue());
    res.setList(f.getType().isAssignableFrom(List.class));
    if (res.isList()) {
      res.setType(inferType((Class<?>)((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0]));
    } else {
      res.setType(inferType(f.getType()));
    }
    return res;
  }

  private static ConfigurationObjectFieldType inferType(Class c) {
    if (c == String.class) return ConfigurationObjectFieldType.STRING;
    if (c == Integer.class || c == Integer.TYPE ||
        c == Long.class || c == Long.TYPE ||
        c == Float.class || c == Float.TYPE ||
        c == Double.class || c == Double.TYPE ||
        c == Byte.class || c == Byte.TYPE ||
        c == Short.class || c == Short.TYPE)
      return ConfigurationObjectFieldType.NUMBER;
    if (c == Boolean.class || c == Boolean.TYPE) return ConfigurationObjectFieldType.BOOLEAN;

    return ConfigurationObjectFieldType.OBJECT;
  }
}
