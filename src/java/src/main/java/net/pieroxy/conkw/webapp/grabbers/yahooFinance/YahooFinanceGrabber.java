package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.utils.hashing.Md5Sum;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;

public class YahooFinanceGrabber extends TimeThrottledGrabber<YahooFinanceGrabber.YahooFinanceGrabberConfig> {
  private static final String NAME = "yahoof";

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public YahooFinanceGrabberConfig getDefaultConfig() {
    YahooFinanceGrabberConfig res = new YahooFinanceGrabberConfig();
    res.setTtl(CDurationParser.parse("5h"));
    return res;
  }

  @Override
  public void initializeGrabber(File homeDir) {
    super.initializeGrabber(homeDir);
    if (canLogFine()) log(Level.FINE, "Initializing with symbol " + getConfig().getSymbol());
  }

  @Override
  protected void load(SimpleCollector c) {
    try {
      if (!StringUtil.isValidApiKey(getConfig().getKey())) {
        log(Level.INFO, "YahooFinanceGrabber not properly configured.");
        c.addError("YahooFinanceGrabber not properly configured.");
        return;
      }

      log(Level.FINE, "Loading data for symbol " + getConfig().getSymbol());

      URL url = new URL("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-summary?symbol="+ getConfig().getSymbol()+"&region="+ getConfig().getRegion());
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

      Summary response = JsonHelper.getJson().deserialize(Summary.class, is);
      c.collect("price", response.getPrice().getRegularMarketPrice().getRaw());
      c.collect("priceAvg50Days", response.getSummaryDetail().getFiftyDayAverage().getRaw());
      c.collect("changeprc", response.getPrice().getRegularMarketChangePercent().getRaw());
      c.collect("change", response.getPrice().getRegularMarketChange().getRaw());
      c.collect("name", response.getPrice().getShortName());
      c.collect("currencySymbol", response.getPrice().getCurrencySymbol());
      c.collect("currency", response.getPrice().getCurrency());
      c.collect("marketCapFmt", response.getPrice().getMarketCap().getFmt());
    } catch (Exception e) {
      log(Level.SEVERE, "", e);
    }
  }

  public static class YahooFinanceGrabberConfig extends TimeThrottledGrabber.SimpleTimeThrottledGrabberConfig {
    private String symbol,region,key;

    @Override
    public void addToHash(Md5Sum sum) {
      sum.add(symbol).add(region).add(key);
    }

    public String getSymbol() {
      return symbol;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    public String getRegion() {
      return region;
    }

    public void setRegion(String region) {
      this.region = region;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }
  }
}
