package net.pieroxy.conkw.pub.mdlog;

import java.util.HashMap;
import java.util.Map;

public class GenericLogRecord implements LogRecord {
    private final Map<String, String> dims = new HashMap<>();
    private final Map<String, Double> vals = new HashMap<>();
    private final String name;

    public GenericLogRecord(String name) {
        this.name = name;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Map<String, String> getDimensions() {
        return dims;
    }

    @Override
    public Map<String, Double> getValues() {
        return vals;
    }

    @Override
    public String getName() {
        return name;
    }

    public GenericLogRecord addValue(String valName, double value) {
        vals.put(valName,value);
        return this;
    }

    public GenericLogRecord addDimension(String valName, String value) {
        dims.put(valName,value);
        return this;
    }

}
