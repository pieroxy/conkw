package net.pieroxy.conkw.utils.config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class GrabberConfigReader {
    private static Logger LOGGER = Logger.getLogger(GrabberConfigReader.class.getName());

    public static void fillObject(Object config, Object json) {
        if (config == null || json == null) return;
        LOGGER.info("Instance is " + json);
        LOGGER.info("Class is " + json.getClass().getName());
        if (json instanceof Map) {
            fillMap(config, (Map<String, ?>)json);
        }
    }

    public static void fillMap(Object src, Map<String, ?> json) {
        json.keySet().stream().forEach(s -> {
            Object value = json.get(s);
            if (value != null) {
                try {
                    Method m = getSetter(src, s);
                    m.invoke(src, buildObject(value, m.getParameterTypes()[0], m.getGenericParameterTypes()[0]));
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static Object buildObject(Object value, Class type, Type genericType) {
        if (value instanceof Number && type == Double.class) {
            return ((Number)value).doubleValue();
        }
        if (value instanceof String && type == String.class) {
            return value;
        }

        throw new RuntimeException("Could not coerce value of type " + value.getClass().getName() + " to " + type.getName());
    }

    static Method getSetter(Object src, String fieldName) {
        Method[] methods = src.getClass().getDeclaredMethods();
        String name = getSetterName(fieldName);
        for (Method m : methods) {
            if (m.getParameterCount() == 1 && m.getName().equals(name) && Modifier.isPublic(m.getModifiers())) return m;
        }
        throw new RuntimeException("Could not find public setter for field " + fieldName);
    }

    static String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0,1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
    }
}
