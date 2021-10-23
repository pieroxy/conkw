package net.pieroxy.conkw.webapp.grabbers.logfile;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorCollector;
import net.pieroxy.conkw.accumulators.parser.AccumulatorExpressionParser;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.pub.mdlog.LogRecord;
import net.pieroxy.conkw.webapp.grabbers.logfile.listeners.LogListener;
import net.pieroxy.conkw.pub.mdlog.LogParser;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TailLogFileGrabber<T extends LogRecord> extends AsyncGrabber<AccumulatorCollector<T>> implements LogListener<T> {
    private final static Logger LOGGER = Logger.getLogger(TailLogFileGrabber.class.getName());

    private String filename;
    private String parserClassName;
    private RealTimeLogFileReader<T> reader;
    private Accumulator<T> accumulator;

    @Override
    public void disposeSync() {
        if (reader!=null) reader.shutdown();
    }

    @Override
    public boolean changed(AccumulatorCollector<T> c) {
        return true;
    }

    @Override
    public synchronized void grabSync(AccumulatorCollector<T> c) {
        if (reader == null) {
            try {
                reader = new RealTimeLogFileReader<T>(filename, this, (LogParser<T>) Class.forName(parserClassName).newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                log(Level.SEVERE, "Impossible to instanciate class " + parserClassName, e);
            }
            reader.start();
        }
    }

    @Override
    public String getDefaultName() {
        return "taillog";
    }

    @Override
    public AccumulatorCollector<T> getDefaultCollector() {
        return new AccumulatorCollector<T>(this, DEFAULT_CONFIG_KEY, "default", accumulator);
    }

    @Override
    protected void setConfig(Map<String, String> config, Map<String, Map<String, String>> namedConfigs) {
        filename = config.get("filename");
        parserClassName = config.get("parserClassName");
        String accConf = config.get("accumulators");
        accumulator = new AccumulatorExpressionParser<T>().parse(accConf);
    }

    @Override
    public synchronized void newLine(long time, T line) {
        synchronized (accumulator) {
            accumulator.add(line);
        }
    }
}
