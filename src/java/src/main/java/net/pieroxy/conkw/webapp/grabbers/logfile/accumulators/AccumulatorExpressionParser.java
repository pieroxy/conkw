package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations.SimpleCounter;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations.SumAccumulator;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AccumulatorExpressionParser {
    private final static Logger LOGGER = Logger.getLogger(AccumulatorExpressionParser.class.getName());
    private static final Map<String, Class<? extends Accumulator<? extends LogRecord>>> accumulatorsByName = new HashMap();

    public Accumulator<LogRecord> parse(String expr) {
        return parseAccumulator(expr, new int[]{0});
    }

    private Accumulator<LogRecord> parseAccumulator(String expr, int[]index) {
        StringBuilder name = new StringBuilder();
        for (int i=index[0] ; i<expr.length() ; i++) {
            char c = expr.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                name.append(c);
            } else {
                break;
            }
        }
        Class<? extends Accumulator<? extends LogRecord>> a = accumulatorsByName.get(name.toString());
        if (a == null) throw new ParseException("Unknown accumulator '" + name + "'.", expr, index);
        TypedConstructor constructor = getConstructorParams(a);
        index[0]+=name.length();
        return buildAccumulator(constructor, expr, index);
    }

    private Accumulator<LogRecord> buildAccumulator(TypedConstructor constructor, String expr, int[]index) {
        if (expr.charAt(index[0])!='(') throw new ParseException("Expected '('", expr, index);
        Object[]params = new Object[constructor.params.size()];
        int paramIndex=0;
        for (ParamType param : constructor.params) {
            params[paramIndex++] = getParameterValue(expr, index, param);
            if (index[0] >= expr.length()) throw new ParseException("Unexpected end of input", expr, index);
            if (paramIndex < constructor.params.size() && expr.charAt(index[0])!=',') throw new ParseException("Expected ','", expr, index);
            if (paramIndex == constructor.params.size() && expr.charAt(index[0])!=')') throw new ParseException("Expected ')'", expr, index);
        }
        if (constructor.params.size()==0) index[0]++;
        index[0]++;
        try {
            return constructor.c.newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getParameterValue(String expr, int[] idx, ParamType param) {
        idx[0]++;
        switch(param) {
            case NUMBER:
                return parseDouble(expr, idx);
            case STRING:
                return parseString(expr, idx);
            case ACCUMULATOR:
                return parseAccumulator(expr, idx);
            case ARRAY_OF_ACCUMULATORS:
                return parseArray(ParamType.ACCUMULATOR, expr, idx);
            case ARRAY_OF_NUMBERS:
                return parseArray(ParamType.NUMBER, expr, idx);
        }
        return null;
    }

    private Object parseArray(ParamType param, String expr, int[] index) {
        if (expr.charAt(index[0])!='[') throw new ParseException("Expected '['", expr, index);
        List<Object> result = new ArrayList<>();
        while (true) {
            char c = expr.charAt(index[0]);
            if (c == ']') break;
            if (c == ',' || c == '[')
                result.add(getParameterValue(expr, index, param));
            else
                throw new ParseException("Expected ',' or ']'", expr, index);
        }
        index[0]++;
        switch(param) {
            case NUMBER: {
                double[] ar = new double[result.size()];
                int i = 0;
                for (Object o : result) {
                    ar[i++] = (double) o;
                }
                return ar;
            }
            case STRING: {
                String[] ar = new String[result.size()];
                int i = 0;
                for (Object o : result) {
                    ar[i++] = (String) o;
                }
                return ar;
            }
            case ACCUMULATOR: {
                Accumulator<LogRecord>[] ar = new Accumulator[result.size()];
                int i = 0;
                for (Object o : result) {
                    ar[i++] = (Accumulator) o;
                }
                return ar;
            }
        }
        throw new ParseException("Internal error.", expr, index);
    }

    private String parseString(String expr, int[] idx) {
        int pos = Math.min(
                indexOf(expr, ',', idx[0]),
                Math.min(
                        indexOf(expr, ')', idx[0]),
                        indexOf(expr, ']', idx[0])
                )
        );
        String s = expr.substring(idx[0], pos);
        idx[0] = pos;
        return s;
    }

    private Double parseDouble(String expr, int[] idx) {
        String extracted = parseString(expr, idx);
        try {
            return Double.parseDouble(extracted);
        } catch (NumberFormatException e) {
            throw new ParseException("Unable to make a number out of the string '"+extracted+"'", expr, idx);
        }
    }

    /**
     * Same as String.indexOf but returns the length of the String instead of -1 when not found.
     * @param data
     * @param toSearch
     * @param startPos
     * @return
     */
    private int indexOf(String data, char toSearch, int startPos) {
        int i = data.indexOf(toSearch, startPos);
        if (i==-1) i=data.length();
        return i;
    }

    private TypedConstructor getConstructorParams(Class<? extends Accumulator<? extends LogRecord>> a) {
        Constructor<Accumulator<LogRecord>>[] cs = (Constructor<Accumulator<LogRecord>>[]) a.getConstructors();
        if (cs.length == 1) return buildTypedConstructor(cs[0]);
        if (cs.length == 2) {
            if (cs[0].getParameterCount() == 0) return buildTypedConstructor(cs[1]);
            if (cs[1].getParameterCount() == 0) return buildTypedConstructor(cs[0]);
        }
        throw new RuntimeException("Class "+a.getName()+" must have only one constructor with parameters.");
    }

    private TypedConstructor buildTypedConstructor(Constructor<Accumulator<LogRecord>> c) {
        Class<?>[] types = c.getParameterTypes();
        TypedConstructor tc = new TypedConstructor();
        tc.c = c;
        tc.params = new ArrayList<>();
        for (Class<?> type : types) {
            try {
                tc.params.add(getParamType(type));
            } catch (Exception e) {
                throw new RuntimeException("Parsing class " + c.getName() + ": " + e.getMessage());
            }
        }
        return tc;
    }

    private ParamType getParamType(Class<?> type) {
        if (type.isArray()) {
            if (type.getComponentType().equals(Double.class)) return ParamType.ARRAY_OF_NUMBERS;
            if (type.getComponentType().equals(Accumulator.class)) return ParamType.ARRAY_OF_ACCUMULATORS;
        } else {
            if (type.equals(Double.class)) return ParamType.NUMBER;
            if (type.equals(Double.TYPE)) return ParamType.NUMBER;
            if (type.equals(String.class)) return ParamType.STRING;
            if (type.equals(Accumulator.class)) return ParamType.ACCUMULATOR;
        }
        throw new RuntimeException("Unsupported constructor type: " + type.getName());
    }

    public static void register(Class<? extends Accumulator> acc) {
        String name = null;
        try {
            name = String.valueOf(acc.getDeclaredField("NAME").get(null));
            accumulatorsByName.put(name, (Class<Accumulator<? extends LogRecord>>)acc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static {
        register(SumAccumulator.class);
        register(SimpleCounter.class);
        register(MultiAccumulator.class);
        register(NamedAccumulator.class);
    }
}

enum ParamType {
    STRING,
    NUMBER,
    ACCUMULATOR,
    ARRAY_OF_ACCUMULATORS,
    ARRAY_OF_NUMBERS
}

class TypedConstructor {
    List<ParamType> params;
    Constructor<Accumulator<LogRecord>> c;
}

class ParseException extends RuntimeException {
    private String message;
    private int position;

    public ParseException(String msg, String expr, int[]index) {
        super(getMessage(msg, expr, index));
        message = msg;
        position = index[0];
    }

    static String getMessage(String msg, String expr, int[]index) {
        return msg + "\r\n" + expr + "\r\n" + getIndexStr(index[0]) + (index[0]<expr.length() ? ("  : " + expr.charAt(index[0])):"");
    }

    private static String getIndexStr(int index) {
        StringBuilder sb = new StringBuilder(index+1);
        while (index-->0) {
            sb.append("-");
        }
        return sb.append("^").toString();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getPosition() {
        return position;
    }
}
