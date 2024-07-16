package net.pieroxy.conkw.utils.config;

import net.pieroxy.conkw.accumulators.implementations.RootAccumulator;
import net.pieroxy.conkw.accumulators.parser.AccumulatorExpressionParser;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;

import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class GrabberConfigReader {
    private static Logger LOGGER = Logger.getLogger(GrabberConfigReader.class.getName());
    private static final Map<Class, CustomConverter> customStringConverters = new HashMap<>();

    static {
        customStringConverters.put(CDuration.class, dat -> CDurationParser.parse(dat));
        customStringConverters.put(Pattern.class, dat -> Pattern.compile(dat, Pattern.DOTALL));
        customStringConverters.put(RootAccumulator.class, dat -> new AccumulatorExpressionParser<>().parse(dat));
        customStringConverters.put(URL.class, dat -> {
            try {
                return new URL(dat);
            } catch (MalformedURLException e) {
                throw new RuntimeException("URL could not be parsed: " + e.getMessage());
            }
        });
    }

    /**
     * Handle basic datatypes along with Pattern, CDuration, URL, RootAccumulator objects, parsed from their String
     * representation.
     * @param container The object supposed to be filled
     * @param json The data to fill the object with
     * @param errorReport If provided, converters errors will be logged in this list and will not throw.
     * @param <T> The type of the object being passed as a first parameter.
     * @return The container passed as a parameter, for convenience
     */
    public static <T> T fillObject(T container, Object json, List<ParsingError> errorReport) {
        return fillObjectInternal("", container, json, errorReport);
    }
    private static <T> T fillObjectInternal(String name, T container, Object json, List<ParsingError> errorReport) {
        if (container == null || json == null) return container;
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Instance is " + json);
            LOGGER.fine("Class is " + json.getClass().getName());
        }
        if (json instanceof Map) {
            fillMap(name, container, (Map<String, ?>)json, errorReport);
            return container;
        } else {
            throw new RuntimeException(json.getClass().getName() + " is not a Map");
        }
    }

    private static void fillMap(String name, Object src, Map<String, ?> json, List<ParsingError> errorReport) {
        json.keySet().stream().forEach(s -> {
            Object value = json.get(s);
            if (value != null) {
                try {
                    Method m = getSetter(src, s);
                    String newName;
                    if (name.length()==0) {
                        newName = s;
                    } else {
                        newName = name + "." + s;
                    }
                    Object builtValue = buildObject(newName, value, m.getGenericParameterTypes()[0], errorReport);
                    if (builtValue!=null) m.invoke(src, builtValue);
                } catch (RuntimeException e) {
                    throw e;
                } catch (IllegalAccessException|InvocationTargetException e) {
                    throw new RuntimeException("Could not access field " + s + ". Please ensure both the containing class and the setter are public.", e);
                }
            }
        });
    }

    private static boolean isSimpleType(Type t) {
        return t == Double.class || t == Boolean.class || t == String.class;
    }

    private static Object buildObject(String name, Object value, Type genericType, List<ParsingError> errorReport) {
        String reason = "";
        if (value == null) return null;
        else if (value instanceof Number) {
            if (genericType == Double.class || genericType == double.class) {
                return ((Number) value).doubleValue();
            } else if (genericType == Integer.class || genericType == int.class) {
                return ((Number) value).intValue();
            } else if (genericType == Long.class || genericType == long.class) {
                return ((Number) value).longValue();
            } else if (genericType == Short.class || genericType == short.class) {
                return ((Number) value).shortValue();
            } else if (genericType == Byte.class || genericType == byte.class) {
                return ((Number) value).byteValue();
            } else if (genericType == Float.class || genericType == float.class) {
                return ((Number) value).floatValue();
            } else {
                throw new RuntimeException("Could not coerce a number to " + genericType.getTypeName() + ".");
            }
        } else if (value instanceof String && genericType == String.class) {
            return value;
        } else if (value instanceof Boolean && genericType == Boolean.class) {
            return value;
        } else if (value instanceof Boolean && genericType == boolean.class) {
            return value;
        } else if (value instanceof List) {
            List list = (List)value;
            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                if (pt.getRawType() == List.class) {
                    Type listOf = pt.getActualTypeArguments()[0];
                        List res = new ArrayList(list.size());
                        list.forEach(e -> res.add(buildObject(name, e, listOf, errorReport)));
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
                    fillObjectInternal(name, res, value, errorReport);
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
                        Object key = buildObject(name, e.getKey(), keys, errorReport);
                        res.put(key, buildObject(name+"."+key, e.getValue(), values, errorReport));
                    });
                    return res;
                } else {
                    reason = "*not* A Map " + genericType;
                }
            } else {
                reason = "*not* A java.lang.Class nor a ParameterizedType: " + genericType;
            }

        } else if (value instanceof String && customStringConverters.containsKey(genericType)) { // We have a custom converter
            try {
                return customStringConverters.get(genericType).convert((String) value);
            } catch (Exception e) {
                if (errorReport!=null) {
                    errorReport.add(new ParsingError(name, e.getMessage()));
                    return null;
                } else {
                    throw e;
                }
            }
        } else {
            throw new RuntimeException("Could not coerce value of type " + (value == null ? "<null>" : value.getClass().getName()) + " to " + genericType.getTypeName() + " because no converter could be found.");
        }

        throw new RuntimeException("Could not coerce value of type " + (value == null ? "<null>" : value.getClass().getName()) + " to " + genericType.getTypeName() + " because " + reason);
    }

    private static Method getSetter(Object src, String fieldName) {
        return getSetter(src.getClass(), src.getClass(), fieldName);
    }
    private static Method getSetter(Class originalClazz, Class clazz, String fieldName) {
        Method[] methods = clazz.getDeclaredMethods();
        String name = getSetterName(fieldName);
        for (Method m : methods) {
            if (m.getParameterCount() == 1 && m.getName().equals(name) && Modifier.isPublic(m.getModifiers())) return m;
        }
        Class superClazz = clazz.getSuperclass();
        if (superClazz!=null) return getSetter(originalClazz, superClazz, fieldName);

        if (clazz.isInterface()) return null;
        throw new RuntimeException("Could not find public setter for field " + fieldName + " on " + originalClazz);
    }

    private static String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0,1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
    }
}
