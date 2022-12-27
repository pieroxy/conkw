package net.pieroxy.conkw.api.metadata.grabberConfig;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationObjectMetadataBuilder {
  private static final Logger LOGGER = Logger.getLogger(ConfigurationObjectMetadataBuilder.class.getName());

  public static ConfigurationObjectMetadata buildMetadata(Class orig) {
    ConfigurationObjectMetadata result = new ConfigurationObjectMetadata(orig.getName());

    Class c = orig;
    while (c != Object.class) {
      for (Field f : c.getDeclaredFields()) {
        ConfigField annotation = f.getAnnotation(ConfigField.class);
        if (LOGGER.isLoggable(Level.FINE)) LOGGER.fine("Field " + f.getName() + " gives annotation " + annotation);
        if (annotation!=null) {
          result.getFields().add(getField(annotation, f, c));
        }
      }
      c = c.getSuperclass();
    }
    if (LOGGER.isLoggable(Level.FINE)) LOGGER.fine("Fields for " + orig.getName() + " " + result.getFields().size());

    return result;
  }

  private static ConfigurationObjectFieldMetadata getField(ConfigField annotation, Field f, Class context) {
    ConfigurationObjectFieldMetadata res = new ConfigurationObjectFieldMetadata();
    res.setName(f.getName());
    res.setLabel(annotation.label());
    res.setDefaultValue(annotation.defaultValue());
    res.setList(f.getType().isAssignableFrom(List.class));
    if (res.isList()) {
      res.setType(inferType(annotation, (Class<?>)((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0], context));
    } else {
      res.setType(inferType(annotation, f.getType(), context));
    }
    return res;
  }

  private static ConfigurationObjectFieldType inferType(ConfigField annotation, Class c, Class context) {
    if (c == String.class) {
      if (annotation.isDelay()) {
        return ConfigurationObjectFieldType.DELAY;
      }
      return ConfigurationObjectFieldType.STRING;
    }
    if (annotation.isDelay()) throw new RuntimeException("Delay cannot be true for fields other than String. Analyzing class " + context.getName());
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
