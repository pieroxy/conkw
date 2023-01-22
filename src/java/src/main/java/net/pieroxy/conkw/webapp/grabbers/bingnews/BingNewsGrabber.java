package net.pieroxy.conkw.webapp.grabbers.bingnews;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.utils.hashing.Md5Sum;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class BingNewsGrabber extends TimeThrottledGrabber<BingNewsGrabber.BingNewsGrabberConfig> {
  static final String NAME = "bingnews";

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public BingNewsGrabberConfig getDefaultConfig() {
    BingNewsGrabberConfig c = new BingNewsGrabberConfig();
    c.setTtl(CDuration.ONE_HOUR);
    return c;
  }

  @Override
  protected void load(SimpleCollector res) {
    try {
/**
 * curl
 * --request GET
 * --url 'https://bing-news-search1.p.rapidapi.com/news?safeSearch=Off&textFormat=Raw&cc=fr'
 * --header 'x-bingapis-sdk: true'
 * --header 'x-rapidapi-host: bing-news-search1.p.rapidapi.com'
 * --header 'x-rapidapi-key: 297df7b9f3msh5744c499d256951p13de3cjsnba5f5a25bc3b'
 */

      URL url = new URL("https://bing-news-search1.p.rapidapi.com/news?safeSearch=Off&textFormat=Raw&cc="+ getConfig().getCountrycode());
      URLConnection con = url.openConnection();
      HttpURLConnection http = (HttpURLConnection) con;
      http.setRequestMethod("GET");
      http.setRequestProperty("x-rapidapi-key", getConfig().getKey());
      http.connect();

      InputStream is;
      if (canLogFine()) {
        is = DebugTools.debugHttpRequest(http);
      } else {
        is = http.getInputStream();
      }

      News response = JsonHelper.getJson().deserialize(News.class, is);
      int i=0;
      for (Value v : response.getValue()) {

        res.collect("news_name_"+i, v.getName());
        res.collect("news_url_"+i, v.getUrl());
        if (v.getImage()!=null && v.getImage().getThumbnail()!=null)
          res.collect("news_imageurl_"+i, v.getImage().getThumbnail().getContentUrl());
        if (v.getProvider()!=null && v.getProvider().size()>0 && v.getProvider().get(0).getImage()!=null && v.getProvider().get(0).getImage().getThumbnail()!=null)
          res.collect("news_providerimageurl_"+i, v.getProvider().get(0).getImage().getThumbnail().getContentUrl());
        i++;
      }

      res.collect("news_size", i);

      return;
    } catch (Exception e) {
      log(Level.SEVERE, "", e);
    }
  }

  public static class BingNewsGrabberConfig extends TimeThrottledGrabber.SimpleTimeThrottledGrabberConfig {
    private String countrycode;
    private String key;

    @Override
    public void addToHash(Md5Sum sum) {
      sum.add(countrycode);
    }

    public String getCountrycode() {
      return countrycode;
    }

    public void setCountrycode(String countrycode) {
      this.countrycode = countrycode;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }
  }
}
