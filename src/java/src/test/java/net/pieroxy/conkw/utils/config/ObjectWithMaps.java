package net.pieroxy.conkw.utils.config;

import java.util.Map;

public class ObjectWithMaps {
    private Map<String, String> ss;
    private Map<String, Double> sd;
    private Map<String, Boolean> sb;
    private Map<String, CustomObject> sc;

    public Map<String, String> getSs() {
        return ss;
    }

    public void setSs(Map<String, String> ss) {
        this.ss = ss;
    }

    public Map<String, Double> getSd() {
        return sd;
    }

    public void setSd(Map<String, Double> sd) {
        this.sd = sd;
    }

    public Map<String, Boolean> getSb() {
        return sb;
    }

    public void setSb(Map<String, Boolean> sb) {
        this.sb = sb;
    }

    public Map<String, CustomObject> getSc() {
        return sc;
    }

    public void setSc(Map<String, CustomObject> sc) {
        this.sc = sc;
    }
}
