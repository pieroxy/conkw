package net.pieroxy.conkw.webapp.grabbers.bingnews;

import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.webapp.grabbers.TimeThrottledGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class BingNewsGrabber extends TimeThrottledGrabber {
  static final String NAME = "bingnews";

  String countrycode,key;

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public void applyConfig(Map<String, String> config, Map<String, Map<String, String>> configs){
    countrycode = String.valueOf(config.get("countrycode"));
    key = String.valueOf(config.get("key"));
  }

  @Override
  protected CDuration getDefaultTtl() {
    return CDurationParser.parse("1h");
  }

  @Override
  protected void load(ResponseData res) {
    try {
/**
 * curl
 * --request GET
 * --url 'https://bing-news-search1.p.rapidapi.com/news?safeSearch=Off&textFormat=Raw&cc=fr'
 * --header 'x-bingapis-sdk: true'
 * --header 'x-rapidapi-host: bing-news-search1.p.rapidapi.com'
 * --header 'x-rapidapi-key: 297df7b9f3msh5744c499d256951p13de3cjsnba5f5a25bc3b'
 */

      URL url = new URL("https://bing-news-search1.p.rapidapi.com/news?safeSearch=Off&textFormat=Raw&cc="+countrycode);
      URLConnection con = url.openConnection();
      HttpURLConnection http = (HttpURLConnection) con;
      http.setRequestMethod("GET");
      http.setRequestProperty("x-rapidapi-key", key);
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

        res.addMetric("news_name_"+i, v.getName());
        res.addMetric("news_url_"+i, v.getUrl());
        if (v.getImage()!=null && v.getImage().getThumbnail()!=null)
          res.addMetric("news_imageurl_"+i, v.getImage().getThumbnail().getContentUrl());
        if (v.getProvider()!=null && v.getProvider().size()>0 && v.getProvider().get(0).getImage()!=null && v.getProvider().get(0).getImage().getThumbnail()!=null)
          res.addMetric("news_providerimageurl_"+i, v.getProvider().get(0).getImage().getThumbnail().getContentUrl());
        i++;
      }

      res.addMetric("news_size", i);

      return;
    } catch (Exception e) {
      e.printStackTrace();
      res.addError("bing: " + e.getMessage());
    }
  }

  @Override
  protected String getCacheKey() {
    return countrycode + key;
  }
}
