package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.webapp.grabbers.TimeThrottledGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;

public class YahooFinanceGrabber extends TimeThrottledGrabber {
  private CDuration CACHE_TTL = CDurationParser.parse("4h");
  private static final String NAME = "yahoof";

  String symbol,region,key;

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
    symbol = String.valueOf(config.get("symbol"));
    region = String.valueOf(config.get("region"));
    key = String.valueOf(config.get("key"));
    String ttl = String.valueOf(config.get("ttl"));
    if (ttl!=null) {
      CACHE_TTL = CDurationParser.parse(ttl);
    }
    log(Level.FINE, "Initializing with symbol " + symbol);
  }

  @Override
  protected CDuration getTtl() {
    return CACHE_TTL;
  }

  @Override
  protected void load(ResponseData res) {
    try {
      if (!StringUtil.isValidApiKey(key)) {
        res.addError("YahooFinanceGrabber not properly configured.");
        return;
      }

      log(Level.FINE, "Loading data for symbol " + symbol);

      URL url = new URL("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-summary?symbol="+symbol+"&region="+region);
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

      Summary response = JsonHelper.getJson().deserialize(Summary.class, is);
      res.addMetric("price", response.getPrice().getRegularMarketPrice().getRaw());
      res.addMetric("priceAvg50Days", response.getSummaryDetail().getFiftyDayAverage().getRaw());
      res.addMetric("changeprc", response.getPrice().getRegularMarketChangePercent().getRaw());
      res.addMetric("change", response.getPrice().getRegularMarketChange().getRaw());
      res.addMetric("name", response.getPrice().getShortName());
      res.addMetric("currencySymbol", response.getPrice().getCurrencySymbol());
      res.addMetric("currency", response.getPrice().getCurrency());
      res.addMetric("marketCapFmt", response.getPrice().getMarketCap().getFmt());

      return;
    } catch (Exception e) {
      e.printStackTrace();
      res.addError("yahoof: " + e.getMessage());
    }
  }

  @Override
  protected String getCacheKey() {
    return symbol + "/" + region + "/" + key;
  }
}
