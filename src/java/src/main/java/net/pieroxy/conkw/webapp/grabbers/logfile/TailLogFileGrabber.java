package net.pieroxy.conkw.webapp.grabbers.logfile;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorCollector;
import net.pieroxy.conkw.accumulators.implementations.RootAccumulator;
import net.pieroxy.conkw.accumulators.parser.AccumulatorExpressionParser;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.pub.mdlog.LogParser;
import net.pieroxy.conkw.webapp.grabbers.logfile.listeners.LogListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TailLogFileGrabber<T extends DataRecord>
        extends AsyncGrabber<AccumulatorCollector<T>, TailLogFileGrabber.TailLogFileGrabberConfig<T>>
        implements LogListener<T> {
    private final static Logger LOGGER = Logger.getLogger(TailLogFileGrabber.class.getName());

    private RealTimeLogFileReader<T> reader;

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
                reader = new RealTimeLogFileReader<T>(getConfig().getFilename(), this, (LogParser<T>) Class.forName(getConfig().getParserClassName()).newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                log(Level.SEVERE, "Impossible to instanciate class " + getConfig().getParserClassName(), e);
            }
            reader.start();
        }
    }

    @Override
    public AccumulatorCollector<T> parseCollector(String param) {
        // There is a cache, so there is probably no need to optimize this.
        RootAccumulator res = new AccumulatorExpressionParser().parse(param);
        return new AccumulatorCollector<>(this, param, "", res);
    }

    @Override
    public String getDefaultName() {
        return "taillog";
    }

    @Override
    public AccumulatorCollector<T> getDefaultCollector() {
        return new AccumulatorCollector<T>(this, DEFAULT_CONFIG_KEY, "default", getConfig().getAccumulator());
    }

    @Override
    public TailLogFileGrabberConfig getDefaultConfig() {
        return new TailLogFileGrabberConfig();
    }

    @Override
    public synchronized void newLine(long time, T line) {
        Accumulator<T> accumulator = getConfig().getAccumulator();
        synchronized (accumulator) {
            accumulator.add(line);
            line.close();
        }
    }

    public static class TailLogFileGrabberConfig<T extends DataRecord> {
        private String filename;
        private String parserClassName;
        private RootAccumulator accumulator;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getParserClassName() {
            return parserClassName;
        }

        public void setParserClassName(String parserClassName) {
            this.parserClassName = parserClassName;
        }

        public RootAccumulator<T> getAccumulator() {
            return accumulator;
        }

        public void setAccumulator(RootAccumulator accumulator) {
            this.accumulator = accumulator;
        }
    }
}
