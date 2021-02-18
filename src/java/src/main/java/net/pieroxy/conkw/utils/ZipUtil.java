package net.pieroxy.conkw.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

    public static void unzip(InputStream is, File destination) throws IOException {
        ZipInputStream zipis = new ZipInputStream(is);
        ZipEntry entry = zipis.getNextEntry();
        while (entry != null) {
            File destFile = new File(destination, entry.getName());
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipis, destFile);
            } else {
                destFile.mkdirs();
            }
            zipis.closeEntry();
            entry = zipis.getNextEntry();
        }
        zipis.close();
    }

    private static void extractFile(ZipInputStream zipis, File destination) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destination));
        byte[] bytesIn = new byte[1024];
        int read = 0;
        while ((read = zipis.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
