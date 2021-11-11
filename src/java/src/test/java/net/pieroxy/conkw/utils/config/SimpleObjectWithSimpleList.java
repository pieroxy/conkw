package net.pieroxy.conkw.utils.config;

import java.util.List;

public class SimpleObjectWithSimpleList {
    private List<String> stringValues;
    private List<Double> doubleValues;
    private List<Boolean> boolValues;


    public List<String> getStringValues() {
        return stringValues;
    }

    public void setStringValues(List<String> stringValues) {
        this.stringValues = stringValues;
    }

    public List<Double> getDoubleValues() {
        return doubleValues;
    }

    public void setDoubleValues(List<Double> doubleValues) {
        this.doubleValues = doubleValues;
    }

    public List<Boolean> getBoolValues() {
        return boolValues;
    }

    public void setBoolValues(List<Boolean> boolValues) {
        this.boolValues = boolValues;
    }
}
