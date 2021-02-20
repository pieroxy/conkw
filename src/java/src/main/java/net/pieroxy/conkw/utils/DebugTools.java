package net.pieroxy.conkw.utils;

import net.pieroxy.conkw.standalone.Runner;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DebugTools {
  private final static Logger LOGGER = Logger.getLogger(DebugTools.class.getName());

  public static String isToString(InputStream inputStream) {
    return new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
  }

  public static InputStream debugHttpRequest(HttpURLConnection conn) throws IOException {
    String s = isToString(conn.getInputStream());
    LOGGER.fine("RC:"+conn.getResponseCode());
    LOGGER.fine("CL:"+conn.getContentLength());
    LOGGER.fine("Content:"+s);
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }

  public static void addPaddedString(StringBuilder sbf, String toAdd, int len) {
    if (toAdd.length() == len) sbf.append(toAdd);
    else if (toAdd.length()<len) {
      for (int i=0 ; i<len-toAdd.length() ; i++)
        sbf.append(" ");
      sbf.append(toAdd);
    } else {
      sbf.append(toAdd.substring(toAdd.length() - len));
    }
  }
}
