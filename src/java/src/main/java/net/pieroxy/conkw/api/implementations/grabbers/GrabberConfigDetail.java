package net.pieroxy.conkw.api.implementations.grabbers;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.config.GrabberConfig;
import net.pieroxy.conkw.grabbersBase.Grabber;

@CompiledJson
@TypeScriptType
public class GrabberConfigDetail {

    private String defaultAccumulator;
    private String implementation;
    private String name;
    private String logLevel;
    private Object config;

    public GrabberConfigDetail() {
    }

    public GrabberConfigDetail(GrabberConfig config) {
        this.implementation = config.getImplementation();
        this.logLevel = config.getLogLevel();
        if (logLevel == null) logLevel = Grabber.DEFAULT_LOG_LEVEL.getName();
        this.name = config.getName();
        this.config = config.getConfig();
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getDefaultAccumulator() {
        return defaultAccumulator;
    }

    public void setDefaultAccumulator(String defaultAccumulator) {
        this.defaultAccumulator = defaultAccumulator;
    }

    public Object getConfig() {
        return config;
    }

    public void setConfig(Object config) {
        this.config = config;
    }
}
