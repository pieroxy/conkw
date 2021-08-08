package net.pieroxy.conkw.utils.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingPrintStream extends PrintStream {

    public LoggingPrintStream(String name, Level level) {
        super(new OutputStream() {
            private final Logger LOGGER = Logger.getLogger(name);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            boolean l10,l13;
            @Override
            public void write(int b) throws IOException {
                if (b==10 && !l13) {
                    l13 = ! (l10 = true);
                    LOGGER.log(level, new String(os.toByteArray()));
                    os = new ByteArrayOutputStream();
                } else if (b==13 && !l13) {
                    l10 = !(l13 = true);
                    LOGGER.log(level, new String(os.toByteArray()));
                    os = new ByteArrayOutputStream();
                } else {
                    l10=l13=false;
                    os.write(b);
                }
            }
        });
    }
}
