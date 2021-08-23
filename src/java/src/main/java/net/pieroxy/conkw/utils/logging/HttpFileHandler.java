package net.pieroxy.conkw.utils.logging;

import java.io.IOException;

public class HttpFileHandler extends FileHandler {
    public HttpFileHandler() throws IOException, SecurityException {
    }

    public HttpFileHandler(String pattern) throws IOException, SecurityException {
        super(pattern);
    }

    public HttpFileHandler(String pattern, boolean append) throws IOException, SecurityException {
        super(pattern, append);
    }

    public HttpFileHandler(String pattern, int limit, int count) throws IOException, SecurityException {
        super(pattern, limit, count);
    }

    public HttpFileHandler(String pattern, int limit, int count, boolean append) throws IOException, SecurityException {
        super(pattern, limit, count, append);
    }
}
