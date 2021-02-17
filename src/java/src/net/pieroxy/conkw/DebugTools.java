package net.pieroxy.conkw;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class DebugTools {
  public static String isToString(InputStream inputStream) {
    return new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
  }

  public static InputStream debugHttpRequest(HttpURLConnection conn) throws IOException {
    String s = isToString(conn.getInputStream());
    System.out.println("RC:"+conn.getResponseCode());
    System.out.println("CL:"+conn.getContentLength());
    System.out.println("Content:"+s);
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }
}
