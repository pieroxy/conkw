package net.pieroxy.conkw.utils.config;

import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class GrabberConfigReader {
    private static Logger LOGGER = Logger.getLogger(GrabberConfigReader.class.getName());
    private static final Map<Class, CustomConverter> customStringConverters = new HashMap<>();

    static {
        customStringConverters.put(CDuration.class, new CustomConverter() {
            @Override
            public Object convert(String dat) {
                return CDurationParser.parse(dat);
            }
        });
        customStringConverters.put(Pattern.class, new CustomConverter() {
            @Override
            public Object convert(String dat) {
                return Pattern.compile(dat);
            }
        });
    }

    public static <T> T fillObject(T container, Object json) {
        if (container == null || json == null) return container;
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Instance is " + json);
            LOGGER.fine("Class is " + json.getClass().getName());
        }
        if (json instanceof Map) {
            fillMap(container, (Map<String, ?>)json);
            return container;
        } else {
            throw new RuntimeException(json.getClass().getName() + " is not a Map");
        }
    }

    private static void fillMap(Object src, Map<String, ?> json) {
        json.keySet().stream().forEach(s -> {
            Object value = json.get(s);
            if (value != null) {
                try {
                    Method m = getSetter(src, s);
                    m.invoke(src, buildObject(value, m.getGenericParameterTypes()[0]));
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Could not access field " + s + ". Please ensure both the containing class and the setter are public.", e);
                }
            }
        });
    }

    private static boolean isSimpleType(Type t) {
        return t == Double.class || t == Boolean.class || t == String.class;
    }

    private static Object buildObject(Object value, Type genericType) {
        String reason = "";
        if (value == null) return null;
        else if (value instanceof Number && genericType == Double.class) {
            return ((Number)value).doubleValue();
        } else if (value instanceof String && genericType == String.class) {
            return value;
        } else if (value instanceof Boolean && genericType == Boolean.class) {
            return value;
        } else if (value instanceof List) {
            List list = (List)value;
            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                if (pt.getRawType() == List.class) {
                    Type listOf = pt.getActualTypeArguments()[0];
                        List res = new ArrayList(list.size());
                        list.forEach(e -> res.add(buildObject(e, listOf)));
                        return res;
                } else {
                    reason = "*not* a list : " + pt.getRawType();
                }
            } else {
                reason = "*not* a ParameterizedType : " + genericType;
            }
        } else if (value instanceof Map) {
            if (genericType instanceof Class) { // We have a custom object here, handled as a JavaBean
                try {
                    Object res = ((Class)genericType).newInstance();
                    fillObject(res, value);
                    return res;
                } catch (Exception e) {
                    throw new RuntimeException("Could not instantiate "+genericType+".", e);
                }
            } else if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                if (pt.getRawType() == Map.class) {
                    Type keys = pt.getActualTypeArguments()[0];
                    Type values = pt.getActualTypeArguments()[1];
                    Map<Object, Object> valueMap = (Map<Object, Object>) value;
                    Map res = new HashMap();
                    valueMap.entrySet().forEach((Map.Entry e) -> {
                        res.put(buildObject(e.getKey(), keys), buildObject(e.getValue(), values));
                    });
                    return res;
                } else {
                    reason = "*not* A Map " + genericType;
                }
            } else {
                reason = "*not* A java.lang.Class nor a ParameterizedType: " + genericType;
            }

        } else if (value instanceof String && customStringConverters.containsKey(genericType)) { // We have a custom converter
            return customStringConverters.get(genericType).convert((String)value);
        } else {
            throw new RuntimeException("Could not coerce value of type " + (value == null ? "<null>" : value.getClass().getName()) + " to " + genericType.getTypeName() + " because its type isn't supported.");
        }

        throw new RuntimeException("Could not coerce value of type " + (value == null ? "<null>" : value.getClass().getName()) + " to " + genericType.getTypeName() + " because " + reason);
    }

    private static Method getSetter(Object src, String fieldName) {
        Method[] methods = src.getClass().getDeclaredMethods();
        String name = getSetterName(fieldName);
        for (Method m : methods) {
            if (m.getParameterCount() == 1 && m.getName().equals(name) && Modifier.isPublic(m.getModifiers())) return m;
        }
        throw new RuntimeException("Could not find public setter for field " + fieldName);
    }

    private static String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0,1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
    }
}
