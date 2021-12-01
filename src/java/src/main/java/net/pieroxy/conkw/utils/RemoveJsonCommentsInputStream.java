package net.pieroxy.conkw.utils;

import java.io.IOException;
import java.io.InputStream;

public class RemoveJsonCommentsInputStream extends InputStream {

    private final InputStream source;
    private final String name;
    int pos;
    int status;

    private static final int STATUS_NORMAL=0;
    private static final int STATUS_STRING=1;
    private static final int STATUS_STRING_BACKSLASH=2;

    public RemoveJsonCommentsInputStream(InputStream source, String name) {
        this.source = source;
        this.name = name;
    }

    public int readNext() throws IOException {
        pos++;
        return source.read();
    }

    @Override
    public int read() throws IOException {
        int next = readNext();
        switch (status) {
            case STATUS_NORMAL:
                if (next == '/') next = consumeComment();
                else if (next == '"') status = STATUS_STRING;
                break;
            case STATUS_STRING:
                if (next == '"') status = STATUS_NORMAL;
                else if (next == '\\') status = STATUS_STRING_BACKSLASH;
                break;
            case STATUS_STRING_BACKSLASH:
                status = STATUS_STRING;
                break;
        }
        return next;
    }

    private int consumeComment() throws IOException {
        int c = readNext();
        if (c == '*') return consumeMlComment();
        if (c == '/') return consumeOlComment();
        throw new RuntimeException("Expected * or / at position " + pos + " in " + name);
    }

    private int consumeOlComment() throws IOException {
        while (true) {
            int next = readNext();
            if (next == '\r' || next == '\n') return readNext();
            if (next == -1) return -1;
        }
    }

    private int consumeMlComment() throws IOException {
        boolean star=false;
        while (true) {
            int next = readNext();
            if (star) {
                if (next == '/') return readNext();
                star = false;
            } else {
                if (next == '*') star=true;
            }
            if (next == -1) {
                throw new RuntimeException("Unclosed comment in file " + name);
            }
        }
    }
}
