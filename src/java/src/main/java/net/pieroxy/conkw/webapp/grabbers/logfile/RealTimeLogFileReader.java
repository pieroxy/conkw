package net.pieroxy.conkw.webapp.grabbers.logfile;

import net.pieroxy.conkw.webapp.grabbers.logfile.listeners.LogListener;
import net.pieroxy.conkw.webapp.grabbers.logfile.listeners.LogParser;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will read new lines from the file provided, parse them with the parser provided and feed them to the
 * listener provided.
 * Note that if the file is seen smaller than its know previous size, the file is reopened as it is assumed to have been
 * truncated or deleted.
 * Note that is the file is not found, it will wait until the file exists and start following it.
 * Note that if the file exists, its content up to the execution of the program will be skipped.
 */
public class RealTimeLogFileReader<T extends LogRecord> extends Thread {
    private final static Logger LOGGER = Logger.getLogger(RealTimeLogFileReader.class.getName());

    private String fullFilename;
    private final LogListener<T> listener;
    private final LogParser<T> parser;
    private long currentLength;
    private boolean shouldShutdown = false;
    private boolean shutdownComplete = false;

    public RealTimeLogFileReader(String fullFilename, LogListener<T> listener, LogParser<T> parser) {
        super("RealTimeLogFileReader(" + fullFilename + ")");
        this.fullFilename = fullFilename;
        this.listener = listener;
        this.parser = parser;
    }

    @Override
    public void run() {
        boolean discardBeginning = true;
        File f = new File(fullFilename);
        LOGGER.info("Tailing file " + f.getAbsolutePath());

        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                currentLength = f.length();
                try {
                    if (discardBeginning) br.skip(f.length());
                } catch (IOException e) {
                    // What to do, really ? If we can't skip, let's not skip.
                    LOGGER.log(Level.WARNING, "Could not skip beginning of file " + fullFilename +". Processing the entire file.");
                }
                discardBeginning = false;

                String line;
                try {
                    while (true) {
                        if (shouldShutdown) break;
                        line = br.readLine();
                        if (line == null) {
                            // Wait until there is more of the file for us to read
                            waitFor(200);
                        } else {
                            if (line.length()>0) {
                                try {
                                    T parsedLine = parser.parse(line);
                                    if (parsedLine != null) listener.newLine(System.currentTimeMillis(), parsedLine);
                                } catch (Exception e) {
                                    LOGGER.log(Level.SEVERE, "Parsing and processing: " + line, e);
                                }
                            }
                        }
                        long curLen = f.length();
                        if (currentLength > curLen) {
                            LOGGER.fine("File changed down, reopening it.");
                            break;
                        } else {
                            currentLength = curLen;
                        }
                    }
                } catch (IOException e) {
                    // Trouble reading the file, exiting main loop.
                    LOGGER.log(Level.SEVERE, "Error reading file.", e);
                }
                if (shouldShutdown) break;

                try {
                    br.close();
                } catch (IOException e) {
                    // Nothing to do here, nothing to cleanup.
                }
            } catch (FileNotFoundException e) {
                // File disappeared or is not there anymore.
                waitFor(100);
                LOGGER.log(Level.WARNING, "File not found: " + fullFilename +". Waiting.");
            }
        }
        shutdownComplete = true;
    }

    private void waitFor(long milliseconds) {
        synchronized (this) {
            try {
                this.wait(milliseconds);
            } catch (InterruptedException e) {}
        }
    }

    public void shutdown() {
        LOGGER.info("RealTimeLogFileReader("+fullFilename+") is shutting down");
        shouldShutdown = true;
        synchronized (this) {
            this.notifyAll();
        }
        while (true) {
            if (shutdownComplete) return;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
    }
}
