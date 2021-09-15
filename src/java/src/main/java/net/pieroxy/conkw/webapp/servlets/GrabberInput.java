package net.pieroxy.conkw.webapp.servlets;

import java.util.Collection;
import java.util.Objects;

public class GrabberInput {
    private final String name;
    private final String paramName;
    private String paramValue;

    public GrabberInput(String nameInList) {
        int pp = nameInList.indexOf('(');
        if (pp>0) {
            name = nameInList.substring(0, pp);
            paramName = nameInList.substring(pp+1, nameInList.indexOf(')'));
        } else {
            name = nameInList;
            paramName = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrabberInput that = (GrabberInput) o;
        return name.equals(that.name) && Objects.equals(paramName, that.paramName) && Objects.equals(paramValue, that.paramValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, paramName, paramValue);
    }

    public String getName() {
        return name;
    }

    public String getParamName() {
        return paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public static class InputHolder {
        Collection<GrabberInput> in;
        Collection<String> errors;
    }
}
