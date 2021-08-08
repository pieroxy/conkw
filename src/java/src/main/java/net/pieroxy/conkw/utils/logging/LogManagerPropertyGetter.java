package net.pieroxy.conkw.utils.logging;

import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class LogManagerPropertyGetter {
    LogManager m;

    public LogManagerPropertyGetter(LogManager m) {
        this.m = m;
    }

    // Package private method to get a String property.
    // If the property is not defined we return the given
    // default value.
    public String getStringProperty(String name, String defaultValue) {
        String val = m.getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        return val.trim();
    }

    // Package private method to get an integer property.
    // If the property is not defined or cannot be parsed
    // we return the given default value.
    public int getIntProperty(String name, int defaultValue) {
        String val = m.getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    // Package private method to get a boolean property.
    // If the property is not defined or cannot be parsed
    // we return the given default value.
    public boolean getBooleanProperty(String name, boolean defaultValue) {
        String val = m.getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        val = val.toLowerCase();
        if (val.equals("true") || val.equals("1")) {
            return true;
        } else if (val.equals("false") || val.equals("0")) {
            return false;
        }
        return defaultValue;
    }

    // Package private method to get a Level property.
    // If the property is not defined or cannot be parsed
    // we return the given default value.
    public Level getLevelProperty(String name, Level defaultValue) {
        String val = m.getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        Level l = findLevel(val.trim());
        return l != null ? l : defaultValue;
    }

    private Level findLevel(String trim) {
        switch (trim) {
            case "SEVERE": return Level.SEVERE;
            case "WARNING": return Level.WARNING;
            case "INFO": return Level.INFO;
            case "CONFIG": return Level.CONFIG;
            case "FINE": return Level.FINE;
            case "FINER": return Level.FINER;
            case "FINEST": return Level.FINEST;
        }
        return Level.INFO;
    }

    // Package private method to get a filter property.
    // We return an instance of the class named by the "name"
    // property. If the property is not defined or has problems
    // we return the defaultValue.
    public Filter getFilterProperty(String name, Filter defaultValue) {
        String val = m.getProperty(name);
        try {
            if (val != null) {
                Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
                return (Filter) clz.newInstance();
            }
        } catch (Exception ex) {
            // We got one of a variety of exceptions in creating the
            // class or creating an instance.
            // Drop through.
        }
        // We got an exception.  Return the defaultValue.
        return defaultValue;
    }


    // Package private method to get a formatter property.
    // We return an instance of the class named by the "name"
    // property. If the property is not defined or has problems
    // we return the defaultValue.
    public Formatter getFormatterProperty(String name, Formatter defaultValue) {
        String val = m.getProperty(name);
        try {
            if (val != null) {
                Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
                return (Formatter) clz.newInstance();
            }
        } catch (Exception ex) {
            // We got one of a variety of exceptions in creating the
            // class or creating an instance.
            // Drop through.
        }
        // We got an exception.  Return the defaultValue.
        return defaultValue;
    }
}
