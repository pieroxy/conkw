package net.pieroxy.conkw.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtils {
  public static <T> T get(String urlString, Class<T> type) throws IOException {
    URL url = new URL(urlString);
    URLConnection con = url.openConnection();
    HttpURLConnection http = (HttpURLConnection) con;
    http.connect();

    InputStream is = http.getInputStream();
    return JsonHelper.getJson().deserialize(type, is);
  }
}
