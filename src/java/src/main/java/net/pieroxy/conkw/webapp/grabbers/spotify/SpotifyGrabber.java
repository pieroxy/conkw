package net.pieroxy.conkw.webapp.grabbers.spotify;

import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.RandomHelper;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.webapp.grabbers.TimeThrottledGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SpotifyGrabber extends TimeThrottledGrabber {

  private CDuration RUNNING_TTL = CDurationParser.parse("0m");
  private CDuration IDLE_TTL = CDurationParser.parse("3s");

  static final String NAME = "spotify";
  private File DATAFILE = null;
  private String ACCESS_TOKEN = null;
  private String AUTH_CODE = null;
  private String clientId;
  private String clientSecret;
  private String redirectUri;
  private boolean isPlaying = true;
  private static String stateToken = RandomHelper.getSequence()+";grabberAction=spotify;";

  public String processAction(Map<String, String[]> parameterMap) {
    try {
      String[]sa = parameterMap.get("state");
      if (sa!=null && sa.length==1 && sa[0].equals(stateToken)) {
        log(Level.INFO, "In action " + sa[0]);
        String[]spcode = parameterMap.get("code");
        if (spcode !=null && spcode.length==1) {
          AUTH_CODE = spcode[0];
        } else {
          return "{\"error\":\"Unknown action 2\"}";
        }
      } else
      if (parameterMap.get("play") != null) {
        sendAction("PUT", "play");
      } else
      if (parameterMap.get("pause") != null) {
        sendAction("PUT", "pause");
      } else
      if (parameterMap.get("next") != null) {
        sendAction("POST", "next");
      } else
      if (parameterMap.get("prev") != null) {
        sendAction("POST", "previous");
      } else {
        return "{\"error\":\"Unknown action\"}";
      }
      return "{\"status\":\"ok\"}";
    } catch (Exception e) {
      e.printStackTrace();
      return "{\"error\":\""+(e.getMessage().replaceAll("\"", "'").replaceAll("\\\\", "/"))+"\"}";
    }
  }

  public void sendAction(String method, String name) {
    try {
      URL url = new URL("https://api.spotify.com/v1/me/player/" + name);
      URLConnection con = url.openConnection();
      HttpURLConnection http = (HttpURLConnection) con;
      http.setRequestMethod(method);
      http.setRequestProperty("Content-Length", "0");
      http.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
      http.setRequestProperty("accept", "*/*");
      http.setDoOutput(true);
      http.connect();
      http.getOutputStream().close();

      if (http.getResponseCode() == 204 || http.getResponseCode() == 200) {
        return;
      }
      InputStream is = http.getErrorStream();
      StringBuilder sb = new StringBuilder();
      byte[]buffer = new byte[1000];
      int read;
      while ((read = is.read(buffer)) != -1) {
        sb.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
      }
      log(Level.SEVERE, "Response code " + http.getResponseCode());
      log(Level.SEVERE, "Response message " + http.getResponseMessage());
      log(Level.SEVERE, "URL " + url);
      log(Level.SEVERE, "Body " + sb);
      return;

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setConfig(Map<String, String> config){
    clientId = config.get("clientId");
    if (clientId!=null && clientId.equals("Your client id"))
      clientId=null;
    clientSecret = config.get("clientSecret");
    if (clientSecret!=null && clientSecret.equals("Your client secret"))
      clientSecret=null;
    redirectUri = config.get("redirectUri");
    if (redirectUri!=null && redirectUri.equals("Your redirect URI"))
      redirectUri=null;

    try {
      DATAFILE = getTmp("spotifyTokens.txt");
      if (DATAFILE.exists()) {
        log(Level.INFO, "Loading tokens from " + DATAFILE.getAbsolutePath());
        List<String> content = Files.readAllLines(DATAFILE.toPath(), StandardCharsets.UTF_8);
        ACCESS_TOKEN = content.get(0);
        AUTH_CODE = content.get(1);
      } else {
        log(Level.INFO, "Token file " + DATAFILE.getAbsolutePath() + " not found. Starting unconnected.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public SpotifyGrabber() {
  }

  @Override
  public String getDefaultName() {
    return NAME;
  }


  @Override
  protected CDuration getTtl() {
    return isPlaying ? RUNNING_TTL : IDLE_TTL;
  }

  @Override
  protected void load(ResponseData sr) {
    try {
      loadSpotify(sr);
    } catch (Exception e) {
      this.log(Level.SEVERE, "Loading spotify failed miserably");
      e.printStackTrace();
      sr.addError("spotify-error: " + e.getMessage());
    }
  }

  @Override
  protected String getCacheKey() {
    return clientId;
  }

  private void loadSpotify(ResponseData r) throws IOException {
    if (AUTH_CODE == null) {
      r.addError("spotify-error: <a href=\""+getInitUrl()+"\">please login</a>");
      return;
    }
    if (ACCESS_TOKEN == null) {
      loadAccessToken(r);
    }
    if (ACCESS_TOKEN != null) {
      loadSpotifyData(r, true);
    }
  }

  private void loadSpotifyData(ResponseData r, boolean firstCall) throws IOException {
    URL url = new URL("https://api.spotify.com/v1/me/player/currently-playing");
    URLConnection con = url.openConnection();
    HttpURLConnection http = (HttpURLConnection)con;
    http.setRequestMethod("GET"); // PUT is another valid option
    http.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
    http.connect();
    if (canLogFine()) this.log(Level.FINE, "Response code " + http.getResponseCode());
    if (http.getResponseCode() == 401) {
      if (canLogFine()) this.log(Level.FINE, "First call: " + firstCall);
      if (firstCall) {
        if (canLogFine()) this.log(Level.FINE, "Reloading token");
        refreshAccessToken(r);
        if (canLogFine()) this.log(Level.FINE, "Reloading data");
        loadSpotifyData(r, false);
        if (canLogFine()) this.log(Level.FINE, "Reloading of token and data complete");
      } else {
        if (canLogFine()) this.log(Level.FINE, "Retry didn't work, unauthenticating");
        AUTH_CODE=ACCESS_TOKEN=null;
      }
      return;
    }
    if (http.getResponseCode() == 204) return;

    InputStream is;
    if (canLogFine()) {
      is = DebugTools.debugHttpRequest(http);
    } else {
      is = http.getInputStream();
    }

    try {
      CurrentlyPlayingResponse response = JsonHelper.getJson().deserialize(CurrentlyPlayingResponse.class, is);
      if (response == null) { // Not playing
        r.addMetric("status", "Stopped");
        return;
      }
      if (response.error!=null) {
        r.addError("spotify-error: " + response.error.getMessage());
        return;
      }
      isPlaying = response.is_playing;
      r.addMetric("status", response.is_playing ? "Playing": "Paused");
      CurrentlyPlayingResponseItem item = response.item;
      if (item != null) {
        r.addMetric("trackname", item.name);
        r.addMetric("tracknum", item.track_number + "");
        r.addMetric("islocal", item.is_local + "");
        boolean haveArtist = false;
        if (item.artists!=null && item.artists.length>0) {
          r.addMetric("album_artist", item.artists[0].name);
          haveArtist = true;
        }

        if (item.album!=null) {
          if (item.album.images!=null && item.album.images.length>0) {
            r.addMetric("album_art", item.album.images[0].url);
          }
          r.addMetric("album_name", item.album.name);
          r.addMetric("album_date", item.album.release_date);
          r.addMetric("album_tracks", item.album.total_tracks+"");
          if (!haveArtist && item.album.artists!=null && item.album.artists.length>0) {
            r.addMetric("album_artist", item.album.artists[0].name);
          }
        }
        r.addMetric("len", item.duration_ms/1000.);
      }
      r.addMetric("pos", response.progress_ms/1000.);
      if (canLogFine()) this.log(Level.FINE, "CurrentlyPlayingResponse: " + JsonHelper.toString(response));

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static CurrentlyPlayingResponse parseResponse(InputStream is) throws IOException {
    return JsonHelper.getJson().deserialize(CurrentlyPlayingResponse.class, is);
  }



  private void loadAccessToken(ResponseData r) throws IOException {
    URL url = new URL("https://accounts.spotify.com/api/token");
    URLConnection con = url.openConnection();
    HttpURLConnection http = (HttpURLConnection)con;
    http.setRequestMethod("POST"); // PUT is another valid option
    http.setDoOutput(true);
    String body = getLoadAccessTokenRequest();
    http.setFixedLengthStreamingMode(body.length());
    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    http.connect();
    try(OutputStream os = http.getOutputStream()) {
      os.write(body.getBytes(StandardCharsets.UTF_8));
    }
    InputStream is =null;
    if (http.getResponseCode() != 200)
      is = http.getErrorStream();
    else
      is = http.getInputStream();
    AccessTokenResponse response = JsonHelper.getJson().deserialize(AccessTokenResponse.class, is);
    if (canLogFine()) this.log(Level.FINE, "AccessTokenResponse: " + JsonHelper.toString(response));
    if (response.error!=null) {
      r.addError("spotify-error: " + response.error_description);
      return;
    }
    ACCESS_TOKEN = response.access_token;
    AUTH_CODE = response.refresh_token; // For when the AT expires

    try (PrintStream out = new PrintStream(new FileOutputStream(DATAFILE))) {
      out.println(ACCESS_TOKEN);
      out.println(AUTH_CODE);
    }


  }

  private void refreshAccessToken(ResponseData r) throws IOException {
    URL url = new URL("https://accounts.spotify.com/api/token");
    URLConnection con = url.openConnection();
    HttpURLConnection http = (HttpURLConnection)con;
    http.setRequestMethod("POST"); // PUT is another valid option
    http.setDoOutput(true);
    String body = getRefreshAccessTokenRequest();
    http.setFixedLengthStreamingMode(body.length());
    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    http.connect();
    try(OutputStream os = http.getOutputStream()) {
      os.write(body.getBytes(StandardCharsets.UTF_8));
    }
    InputStream is =null;
    if (http.getResponseCode() != 200)
      is = http.getErrorStream();
    else
      is = http.getInputStream();
    AccessTokenResponse response = JsonHelper.getJson().deserialize(AccessTokenResponse.class, is);

    if (canLogFine()) this.log(Level.FINE, "RefreshTokenResponse: " + JsonHelper.toString(response));
    if (response.error!=null) {
      r.addError("spotify-error: " + response.error_description);
      return;
    }
    ACCESS_TOKEN = response.access_token;
  }

  private String getRefreshAccessTokenRequest() throws UnsupportedEncodingException {
    return "client_id="+clientId+"&client_secret="+clientSecret+"&grant_type=refresh_token&refresh_token="+URLEncoder.encode(AUTH_CODE, StandardCharsets.UTF_8.toString());
  }

  private String getInitUrl() throws UnsupportedEncodingException {
    return  "https://accounts.spotify.com/authorize?client_id="+clientId+"&response_type=code&redirect_uri="+URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString())+"&scope=user-modify-playback-state%20user-read-private%20user-read-email%20user-read-currently-playing&state=" + stateToken;
  }
  private String getLoadAccessTokenRequest() throws UnsupportedEncodingException {
    return "client_id="+clientId+"&client_secret="+clientSecret+"&grant_type=authorization_code&code="+ URLEncoder.encode(AUTH_CODE, StandardCharsets.UTF_8.toString())+"&redirect_uri="+URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString());
  }

}