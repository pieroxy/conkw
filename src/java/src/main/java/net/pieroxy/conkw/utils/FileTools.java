package net.pieroxy.conkw.utils;

import java.io.File;

public class FileTools {
    public static void makeFileReadonlyForUser(File out) {
        out.setExecutable(false, false);
        out.setWritable(false, false);
        out.setReadable(false, false);
        out.setReadable(true, true);
    }

    public static void makeFileWritableForUser(File out) {
        out.setWritable(true, true);
    }
}
