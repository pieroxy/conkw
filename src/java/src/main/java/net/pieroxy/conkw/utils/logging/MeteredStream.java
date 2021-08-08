package net.pieroxy.conkw.utils.logging;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A metered stream is a subclass of OutputStream that
 * (a) forwards all its output to a target stream
 * (b) keeps track of how many bytes have been written
 */
public class MeteredStream extends OutputStream {
    final OutputStream out;
    int written;

    MeteredStream(OutputStream out, int written) {
        this.out = out;
        this.written = written;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        written++;
    }

    @Override
    public void write(byte buff[]) throws IOException {
        out.write(buff);
        written += buff.length;
    }

    @Override
    public void write(byte buff[], int off, int len) throws IOException {
        out.write(buff,off,len);
        written += len;
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
