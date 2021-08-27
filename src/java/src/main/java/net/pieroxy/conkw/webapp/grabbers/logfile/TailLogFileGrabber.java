package net.pieroxy.conkw.webapp.grabbers.logfile;

import net.pieroxy.conkw.webapp.grabbers.AsyncGrabber;
import net.pieroxy.conkw.webapp.grabbers.logfile.listeners.LogListener;
import net.pieroxy.conkw.webapp.grabbers.logfile.listeners.LogParser;
import net.pieroxy.conkw.webapp.grabbers.logfile.parsers.HttpLogFileParser;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TailLogFileGrabber extends AsyncGrabber implements LogListener<LogRecord> {
    private final static Logger LOGGER = Logger.getLogger(TailLogFileGrabber.class.getName());


    private String filename;
    private String parserClassName;
    private RealTimeLogFileReader reader;
    private ResponseData data;
    private Map<String, Set<String>> dimValues = new HashMap<>();

    @Override
    public boolean changed() {
        return true;
    }

    @Override
    public synchronized ResponseData grabSync() {
        if (reader == null) {
            try {
                reader = new RealTimeLogFileReader(filename, this, (LogParser<LogRecord>) Class.forName(parserClassName).newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                log(Level.SEVERE, "Impossible to instanciate class " + parserClassName, e);
            }
            reader.start();
            data = new ResponseData(this, System.currentTimeMillis());
        }
        ResponseData res = data;
        dimValues = new HashMap<>();
        data = new ResponseData(this, 0);
        res.setTimestamp(System.currentTimeMillis());
        return res;
    }

    @Override
    public String getDefaultName() {
        return "taillog";
    }

    @Override
    protected void setConfig(Map<String, String> config, Map<String, Map<String, String>> namedConfigs) {
        filename = config.get("filename");
        parserClassName = config.get("parserClassName");
    }

    @Override
    public synchronized void newLine(long time, LogRecord line) {
        line.getDimensions().entrySet().forEach(entry -> {
            String baseName = line.getName() + "." + entry.getKey() + "." + entry.getValue();
            addDimValue(line.getName() + "." + entry.getKey(), entry.getValue());
            line.getValues().entrySet().forEach(ventry -> {
                String name = baseName+"."+ventry.getKey();
                Double d = data.getNumMetric(name);
                if (d==null) d=0d;
                double curValue = d+ventry.getValue();
                data.addMetric(name, curValue);
                String maxName = "max."+line.getName() + "." + entry.getKey() + "." + ventry.getKey();
                d = data.getNumMetric(maxName);
                if (d==null) d=0d;
                data.addMetric(maxName, Math.max(d, curValue));
                String totalName = "total."+line.getName() + "." + entry.getKey() + "." + ventry.getKey();
                d = data.getNumMetric(totalName);
                if (d==null) d=0d;
                data.addMetric(totalName, d+ ventry.getValue());
            });
        });
    }

    private void addDimValue(String dim, String value) {
        if (value == null) {
            value = "";
        }
        Set<String> values = dimValues.get(dim);
        if (values == null) {
            values = new HashSet<>();
            dimValues.put(dim, values);
        }
        if (!values.contains(value)) {
            values.add(value);
            data.addMetric("values." + dim, values.stream().sorted().collect(Collectors.joining(",")));
        }
    }
}
