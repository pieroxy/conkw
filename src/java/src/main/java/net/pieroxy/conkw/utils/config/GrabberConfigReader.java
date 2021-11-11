package net.pieroxy.conkw.utils.config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrabberConfigReader {
    private static Logger LOGGER = Logger.getLogger(GrabberConfigReader.class.getName());

    public static void fillObject(Object config, Object json) {
        if (config == null || json == null) return;
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Instance is " + json);
            LOGGER.fine("Class is " + json.getClass().getName());
        }
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
                    m.invoke(src, buildObject(value, m.getGenericParameterTypes()[0]));
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static boolean isSimpleType(Type t) {
        return t == Double.class || t == Boolean.class || t == String.class;
    }

    public static Object buildObject(Object value, Type genericType) {
        if (value instanceof Number && genericType == Double.class) {
            return ((Number)value).doubleValue();
        }
        if (value instanceof String && genericType == String.class) {
            return value;
        }
        if (value instanceof Boolean && genericType == Boolean.class) {
            return value;
        }
        String reason = "";
        if (value instanceof List) {
            List list = (List)value;
            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                if (pt.getRawType() == List.class) {
                    Type listOf = pt.getActualTypeArguments()[0];
                    if (isSimpleType(listOf)) {
                        List res = new ArrayList(list.size());
                        list.forEach(e -> res.add(buildObject(e, listOf)));
                        return res;
                    } else {
                        reason = "*not* a simple type : " + listOf;
                    }
                } else {
                    reason = "*not* a list : " + pt.getRawType();
                }
            } else {
                reason = "*not* a ParameterizedType : " + genericType;
            }
        } else {
            reason = "value is *not* a list : " + value;
        }

        throw new RuntimeException("Could not coerce value of type " + value.getClass().getName() + " to " + genericType.getTypeName() + " because " + reason);
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
