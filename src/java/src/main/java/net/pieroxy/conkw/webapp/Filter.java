package net.pieroxy.conkw.webapp;

import net.pieroxy.conkw.webapp.servlets.HttpLogger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Filter implements javax.servlet.Filter {
    private final static Logger LOGGER = Logger.getLogger(Filter.class.getName());

    public static final String API_VERB = "API_VERB";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long begin = System.currentTimeMillis();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        LogResponseWrapper response = new LogResponseWrapper(httpResponse);

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "While processing request", e);
        }

        long time = (System.currentTimeMillis() - begin);
        HttpLogger.log(
                request.getHeader("Host"),
                request.getRequestURI(),
                request.getHeader("User-Agent"),
                (String)request.getAttribute(API_VERB),
                time,
                httpResponse.getStatus(),
                (int)response.getLength(),
                httpResponse.getContentType(),
                getIp(request)
        );

    }

    private String getIp(HttpServletRequest request) {
        String headerName = "X-Forwarded-For";

        String ip = request.getHeader(headerName);
        if (ip == null) return request.getRemoteAddr();
        int lastIp = ip.indexOf(',');
        if (lastIp > -1) {
            ip = ip.substring(0, lastIp).trim();
        }
        return ip;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }
}

class LogResponseWrapper extends HttpServletResponseWrapper
{
    private MyServletOutputStream os = null;
    private PrintWriter pw = null;


    public long getLength() {
        return os == null ? 0 : os.getLength();
    }

    LogResponseWrapper(HttpServletResponse response)
    {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (os!=null && pw == null) throw new IllegalStateException("getOutputStream() has already been called on this response");
        if (pw == null) {
            pw = new PrintWriter(getOutputStream());
        }
        return pw;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (pw!=null) throw new IllegalStateException("getOutputStream() has already been called on this response");
        if (os == null) {
            os = new MyServletOutputStream(super.getOutputStream());
        }
        return os;
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();
    }
}


/**
 * Below code to count the number of bytes in the response.
 * Quite ugly but there's really no alternative.
 */
class MyServletOutputStream extends ServletOutputStream {
    private long written = 0;
    private ServletOutputStream inner;
    public MyServletOutputStream(ServletOutputStream inner) {
        this.inner = inner;
    }
    public void close() throws IOException {
        inner.close();
    }
    public void flush() throws IOException {
        inner.flush();
    }
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }
    public void write(byte[] b, int off, int len) throws IOException {
        inner.write(b, off, len);
        written += len;
    }
    public void write(int b) throws IOException {
        written ++;
        inner.write(b);
    }

    @Override
    public boolean isReady() {
        return inner.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        inner.setWriteListener(writeListener);
    }

    public long getLength() {
        return written;
    }
}

