package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;

public class YahooFinanceGrabber extends TimeThrottledGrabber {
  private static final String NAME = "yahoof";

  String symbol,region,key;

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public void applyConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
    symbol = String.valueOf(config.get("symbol"));
    region = String.valueOf(config.get("region"));
    key = String.valueOf(config.get("key"));
    if (canLogFine()) log(Level.FINE, "Initializing with symbol " + symbol);
  }

  @Override
  protected CDuration getDefaultTtl() {
    return CDurationParser.parse("4h");
  }

  @Override
  protected void load(SimpleCollector c) {
    try {
      if (!StringUtil.isValidApiKey(key)) {
        c.addError("YahooFinanceGrabber not properly configured.");
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
      c.collect("price", response.getPrice().getRegularMarketPrice().getRaw());
      c.collect("priceAvg50Days", response.getSummaryDetail().getFiftyDayAverage().getRaw());
      c.collect("changeprc", response.getPrice().getRegularMarketChangePercent().getRaw());
      c.collect("change", response.getPrice().getRegularMarketChange().getRaw());
      c.collect("name", response.getPrice().getShortName());
      c.collect("currencySymbol", response.getPrice().getCurrencySymbol());
      c.collect("currency", response.getPrice().getCurrency());
      c.collect("marketCapFmt", response.getPrice().getMarketCap().getFmt());

      return;
    } catch (Exception e) {
      log(Level.SEVERE, "", e);
    }
  }

  @Override
  protected String getCacheKey() {
    return symbol + "/" + region + "/" + key;
  }
}
