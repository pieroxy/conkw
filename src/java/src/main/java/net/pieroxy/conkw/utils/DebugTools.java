package net.pieroxy.conkw.utils;

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
    LOGGER.info("RC:"+conn.getResponseCode());
    LOGGER.info("CL:"+conn.getContentLength());
    LOGGER.info("Content:"+s);
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }

}
