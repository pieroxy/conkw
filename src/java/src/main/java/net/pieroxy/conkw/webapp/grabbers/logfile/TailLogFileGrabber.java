package net.pieroxy.conkw.webapp.grabbers.logfile;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorCollector;
import net.pieroxy.conkw.accumulators.parser.AccumulatorExpressionParser;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.webapp.grabbers.logfile.listeners.LogListener;
import net.pieroxy.conkw.webapp.grabbers.logfile.listeners.LogParser;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TailLogFileGrabber extends AsyncGrabber<AccumulatorCollector> implements LogListener<LogRecord> {
    private final static Logger LOGGER = Logger.getLogger(TailLogFileGrabber.class.getName());

    private String filename;
    private String parserClassName;
    private RealTimeLogFileReader reader;
    private Accumulator<LogRecord> accumulator;

    @Override
    public void disposeSync() {
        if (reader!=null) reader.shutdown();
    }

    @Override
    public boolean changed() {
        return true;
    }

    @Override
    public synchronized void grabSync(AccumulatorCollector c) {
        if (reader == null) {
            try {
                reader = new RealTimeLogFileReader(filename, this, (LogParser<LogRecord>) Class.forName(parserClassName).newInstance());
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
    public AccumulatorCollector getDefaultCollector() {
        return new AccumulatorCollector(this, DEFAULT_CONFIG_KEY, "default", accumulator);
    }

    @Override
    protected void setConfig(Map<String, String> config, Map<String, Map<String, String>> namedConfigs) {
        filename = config.get("filename");
        parserClassName = config.get("parserClassName");
        String accConf = config.get("accumulators");
        accumulator = new AccumulatorExpressionParser().parse(accConf);
    }

    @Override
    public synchronized void newLine(long time, LogRecord line) {
        synchronized (accumulator) {
            accumulator.add(line);
        }
    }
}
