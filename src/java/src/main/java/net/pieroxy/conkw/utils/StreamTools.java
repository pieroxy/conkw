package net.pieroxy.conkw.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamTools {
  public static void copyStreamAndClose(InputStream is, OutputStream os) throws IOException {
    try {
      int read = 0;
      byte[]buffer = new byte[10240];
      while ((read = is.read(buffer))!=-1) {
        os.write(buffer, 0, read);
      }
    } finally {
      os.close();
      is.close();
    }
  }
}
