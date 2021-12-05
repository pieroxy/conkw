package net.pieroxy.conkw.webapp.grabbers.openweathermap;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.grabbersBase.PartiallyExtractableConfig;
import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.utils.hashing.Md5Sum;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class OpenWeatherMapGrabber extends TimeThrottledGrabber<OpenWeatherMapGrabber.OpenWeatherMapGrabberConfig> {
  static final String NAME = "owm";

  static Map<String, String> locationNames = new ConcurrentHashMap<>();

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public void applyConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
  }

  @Override
  protected CDuration getDefaultTtl() {
    return CDurationParser.parse("5m");
  }

  @Override
  public OpenWeatherMapGrabberConfig getDefaultConfig() {
    OpenWeatherMapGrabberConfig conf = new OpenWeatherMapGrabberConfig();
    conf.setTtl(CDurationParser.parse("5m"));
    return conf;
  }

  @Override
  protected void load(SimpleCollector responseData) {
    if (!StringUtil.isValidApiKey(getConfig().getToken())) {
      responseData.addError("Open Weather Map is not properly configured.");
      return;
    }

    String city = grabCity();

    try {
      URL url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+getConfig().getLat()+"&lon="+getConfig().getLon()+"&units=metric&appid="+getConfig().getToken());
      URLConnection con = url.openConnection();
      HttpURLConnection http = (HttpURLConnection) con;
      http.connect();

      InputStream is;
      if (canLogFine()) {
        is = DebugTools.debugHttpRequest(http);
      } else {
        is = http.getInputStream();
      }

      OneCallResponse response = JsonHelper.getJson().deserialize(OneCallResponse.class, is);

      responseData.collect("location_name", city);

      double sunrise = response.getCurrent().getSunrise();
      double sunset = response.getCurrent().getSunset();


      extract(responseData, "current", (res) -> {
        res.collect("current_desc", response.getCurrent().getWeather()[0].getDescription());
        res.collect("current_icon", getFullIcon((int)response.getCurrent().getWeather()[0].getId(), sunset, sunrise, response.getCurrent().getDt()));
        res.collect("current_temp", response.getCurrent().getTemp());
        res.collect("current_feels", response.getCurrent().getFeels_like());
        res.collect("current_pressure", response.getCurrent().getPressure());
        res.collect("current_humidity", response.getCurrent().getHumidity());
        res.collect("current_visibility", response.getCurrent().getVisibility());
        res.collect("current_wind_speed", response.getCurrent().getWind_speed()*3.6); // m/s by default -> km/h
        res.collect("current_wind_speed_icon", getWindIcon(response.getCurrent().getWind_speed()));
        res.collect("current_wind_deg", response.getCurrent().getWind_deg());
        res.collect("current_clouds", response.getCurrent().getClouds());
        res.collect("current_sunrise", response.getCurrent().getSunrise());
        res.collect("current_sunset", response.getCurrent().getSunset());
      }, CDuration.ZERO);

      extract(responseData, "minute", (res) -> {
        int i=0;
        if (response.getMinutely()!=null) {
          if (canLogFine()) log(Level.FINE, "Minute extraction, number of elements: " + response.getMinutely().length);
          for (MinutelyForecast f : response.getMinutely()) {
            res.collect("minutely_pim_" + i++, f.getPrecipitation());
          }
        }
      }, CDuration.ZERO);

      extract(responseData, "hour", (res) -> {
        int i=0;
        for (HourlyForecast f : response.getHourly()) {
          res.collect("hourly_dt_"+i, f.getDt());
          res.collect("hourly_temp_"+i, f.getTemp());
          res.collect("hourly_temp_feels_"+i, f.getFeels_like());
          res.collect("hourly_wind_speed_"+i, f.getWind_speed()*3.6); // m/s by default -> km/h
          res.collect("hourly_wind_speed_icon_"+i, getWindIcon(f.getWind_speed()));
          res.collect("hourly_wind_speed_beaufort_"+i, (double)getBeaufortScale(f.getWind_speed()));
          res.collect("hourly_pressure_"+i, f.getPressure());
          res.collect("hourly_humidity_"+i, f.getHumidity());
          res.collect("hourly_visibility_"+i, f.getVisibility());
          res.collect("hourly_desc_"+i, f.getWeather()[0].getDescription());
          res.collect("hourly_icon_"+i, getFullIcon((int)f.getWeather()[0].getId(), sunset, sunrise, f.getDt()));
          res.collect("hourly_pop_"+i, f.getPop());
          if (f.getRain()!=null)
            res.collect("hourly_rain_"+i, f.getRain().getOneh());
          else
            res.collect("hourly_rain_"+i, 0.);
          if (f.getSnow()!=null)
            res.collect("hourly_snow_"+i, f.getSnow().getOneh());
          else
            res.collect("hourly_snow_"+i, 0.);

          i++;
        }
      }, CDuration.ZERO);

      extract(responseData, "day", (res) -> {
        int i=0;
        for (DailyForecast f : response.getDaily()) {
          res.collect("daily_dt_"+i, f.getDt());
          res.collect("daily_temp_max_"+i, f.getTemp().getMax());
          res.collect("daily_temp_min_"+i, f.getTemp().getMin());
          res.collect("daily_wind_speed_"+i, f.getWind_speed()*3.6);// m/s by default -> km/h
          res.collect("daily_wind_speed_icon_"+i, getWindIcon(f.getWind_speed()));
          res.collect("daily_wind_speed_beaufort_"+i, (double)getBeaufortScale(f.getWind_speed()));
          res.collect("daily_pressure_"+i, f.getPressure());
          res.collect("daily_humidity_"+i, f.getHumidity());
          res.collect("daily_desc_"+i, f.getWeather()[0].getDescription());
          res.collect("daily_icon_"+i, getFullIcon((int)f.getWeather()[0].getId(), 0, 0, 0));
          res.collect("daily_rain_"+i, f.getRain());
          res.collect("daily_snow_"+i, f.getSnow());
          res.collect("daily_pop_"+i, f.getPop());

          i++;
        }
      }, CDuration.ZERO);
    } catch (Exception e) {
      e.printStackTrace();
      responseData.addError("owm: " + e.getMessage());
    }
  }

  private String grabCity() {
    String key = getConfig().getLat()+"x"+getConfig().getLon();
    if (!locationNames.containsKey(key)) {
      try {
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + getConfig().getLat() + "&lon=" + getConfig().getLon() + "&units=metric&appid=" + getConfig().getToken());
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.connect();

        InputStream is;
        if (canLogFine()) {
          is = DebugTools.debugHttpRequest(http);
        } else {
          is = http.getInputStream();
        }

        WeatherResponse response = JsonHelper.getJson().deserialize(WeatherResponse.class, is);
        if (response != null) {
          locationNames.put(key, response.getName());
          log(Level.INFO, "City is " + response.getName());
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return locationNames.get(key);
  }

  private String getWindIcon(double windspeedinmps) {
    return "/owm-icons/wi-wind-beaufort-"+getBeaufortScale(windspeedinmps)+".svg";
  }

  private int getBeaufortScale(double windspeedinmps) {
    if (windspeedinmps < 0.5) return 0;
    if (windspeedinmps <= 1.5) return 1;
    if (windspeedinmps <= 3.3) return 2;
    if (windspeedinmps <= 5.5) return 3;
    if (windspeedinmps <= 7.9) return 4;
    if (windspeedinmps <= 10.7) return 5;
    if (windspeedinmps <= 13.8) return 6;
    if (windspeedinmps <= 17.1) return 7;
    if (windspeedinmps <= 20.7) return 8;
    if (windspeedinmps <= 24.4) return 9;
    if (windspeedinmps <= 28.4) return 10;
    if (windspeedinmps <= 32.6) return 11;
    return 12;
  }

  private String getFullIcon(int num, double sunset, double sunrise, double ts) {
    return "/owm-icons/wi-" + getIcon(num, getTod(sunset, sunrise, ts)) + ".svg";
  }

  private int getTod(double sunset, double sunrise, double ts) {
    if (sunset == 0 || sunrise == 0) return 0;
    if (ts%86400 < sunrise%86400) return 2;
    if (ts%86400 > sunset%86400) return 2;
    return 1;
  }

  private String getIcon(int num, int tod /* 0=neutral, 1=day, 2=night */) {
    // Uses https://erikflowers.github.io/weather-icons/
    // Also available at https://github.com/erikflowers/weather-icons
    switch (tod) {
      case 0:
        switch (num) {
          case 200:return "thunderstorm";
          case 201:return "thunderstorm";
          case 202:return "thunderstorm";
          case 210:return "lightning";
          case 211:return "lightning";
          case 212:return "lightning";
          case 221:return "lightning";
          case 230:return "thunderstorm";
          case 231:return "thunderstorm";
          case 232:return "thunderstorm";
          case 300:return "sprinkle";
          case 301:return "sprinkle";
          case 302:return "rain";
          case 310:return "rain-mix";
          case 311:return "rain";
          case 312:return "rain";
          case 313:return "showers";
          case 314:return "rain";
          case 321:return "sprinkle";
          case 500:return "sprinkle";
          case 501:return "rain";
          case 502:return "rain";
          case 503:return "rain";
          case 504:return "rain";
          case 511:return "rain-mix";
          case 520:return "showers";
          case 521:return "showers";
          case 522:return "showers";
          case 531:return "storm-showers";
          case 600:return "snow";
          case 601:return "snow";
          case 602:return "sleet";
          case 611:return "rain-mix";
          case 612:return "rain-mix";
          case 615:return "rain-mix";
          case 616:return "rain-mix";
          case 620:return "rain-mix";
          case 621:return "snow";
          case 622:return "snow";
          case 701:return "fog";
          case 711:return "smoke";
          case 721:return "day-haze";
          case 731:return "dust";
          case 741:return "fog";
          case 761:return "dust";
          case 762:return "dust";
          case 771:return "cloudy-gusts";
          case 781:return "tornado";
          case 800:return "day-sunny";
          case 801:return "cloud";
          case 802:return "cloud";
          case 803:return "cloudy";
          case 804:return "cloudy";
          case 900:return "tornado";
          case 901:return "storm-showers";
          case 902:return "hurricane";
          case 903:return "snowflake-cold";
          case 904:return "hot";
          case 905:return "windy";
          case 906:return "hail";
          case 957:return "strong-wind";
        }
      case 1:
        switch (num) {
          case 200: return "day-thunderstorm";
          case 201: return "day-thunderstorm";
          case 202: return "day-thunderstorm";
          case 210: return "day-lightning";
          case 211: return "day-lightning";
          case 212: return "day-lightning";
          case 221: return "day-lightning";
          case 230: return "day-thunderstorm";
          case 231: return "day-thunderstorm";
          case 232: return "day-thunderstorm";
          case 300: return "day-sprinkle";
          case 301: return "day-sprinkle";
          case 302: return "day-rain";
          case 310: return "day-rain";
          case 311: return "day-rain";
          case 312: return "day-rain";
          case 313: return "day-rain";
          case 314: return "day-rain";
          case 321: return "day-sprinkle";
          case 500: return "day-sprinkle";
          case 501: return "day-rain";
          case 502: return "day-rain";
          case 503: return "day-rain";
          case 504: return "day-rain";
          case 511: return "day-rain-mix";
          case 520: return "day-showers";
          case 521: return "day-showers";
          case 522: return "day-showers";
          case 531: return "day-storm-showers";
          case 600: return "day-snow";
          case 601: return "day-sleet";
          case 602: return "day-snow";
          case 611: return "day-rain-mix";
          case 612: return "day-rain-mix";
          case 615: return "day-rain-mix";
          case 616: return "day-rain-mix";
          case 620: return "day-rain-mix";
          case 621: return "day-snow";
          case 622: return "day-snow";
          case 701: return "day-fog";
          case 711: return "smoke";
          case 721: return "day-haze";
          case 731: return "dust";
          case 741: return "day-fog";
          case 761: return "dust";
          case 762: return "dust";
          case 781: return "tornado";
          case 800: return "day-sunny";
          case 801: return "day-cloudy";
          case 802: return "day-cloudy";
          case 803: return "cloudy";
          case 804: return "cloudy";
          case 900: return "tornado";
          case 902: return "hurricane";
          case 903: return "snowflake-cold";
          case 904: return "hot";
          case 906: return "day-hail";
          case 957: return "strong-wind";
        }
      case 2:
        switch (num) {
          case 200: return "night-alt-thunderstorm";
          case 201: return "night-alt-thunderstorm";
          case 202: return "night-alt-thunderstorm";
          case 210: return "night-alt-lightning";
          case 211: return "night-alt-lightning";
          case 212: return "night-alt-lightning";
          case 221: return "night-alt-lightning";
          case 230: return "night-alt-thunderstorm";
          case 231: return "night-alt-thunderstorm";
          case 232: return "night-alt-thunderstorm";
          case 300: return "night-alt-sprinkle";
          case 301: return "night-alt-sprinkle";
          case 302: return "night-alt-rain";
          case 310: return "night-alt-rain";
          case 311: return "night-alt-rain";
          case 312: return "night-alt-rain";
          case 313: return "night-alt-rain";
          case 314: return "night-alt-rain";
          case 321: return "night-alt-sprinkle";
          case 500: return "night-alt-sprinkle";
          case 501: return "night-alt-rain";
          case 502: return "night-alt-rain";
          case 503: return "night-alt-rain";
          case 504: return "night-alt-rain";
          case 511: return "night-alt-rain-mix";
          case 520: return "night-alt-showers";
          case 521: return "night-alt-showers";
          case 522: return "night-alt-showers";
          case 531: return "night-alt-storm-showers";
          case 600: return "night-alt-snow";
          case 601: return "night-alt-sleet";
          case 602: return "night-alt-snow";
          case 611: return "night-alt-rain-mix";
          case 612: return "night-alt-rain-mix";
          case 615: return "night-alt-rain-mix";
          case 616: return "night-alt-rain-mix";
          case 620: return "night-alt-rain-mix";
          case 621: return "night-alt-snow";
          case 622: return "night-alt-snow";
          case 701: return "night-fog";
          case 711: return "smoke";
          case 721: return "day-haze";
          case 731: return "dust";
          case 741: return "night-fog";
          case 761: return "dust";
          case 762: return "dust";
          case 781: return "tornado";
          case 800: return "night-clear";
          case 801: return "night-alt-partly-cloudy";
          case 802: return "night-alt-cloudy";
          case 803: return "cloudy";
          case 804: return "cloudy";
          case 900: return "tornado";
          case 902: return "hurricane";
          case 903: return "snowflake-cold";
          case 904: return "hot";
          case 906: return "night-alt-hail";
          case 957: return "strong-wind";
        }
    }
    return "align.svg";
  }

  public static class OpenWeatherMapGrabberConfig extends TimeThrottledGrabber.TimeThrottledGrabberConfig implements PartiallyExtractableConfig {
    private Double lon;
    private Double lat;
    private String token;
    private List<String> toExtract;

    @Override
    public void addToHash(Md5Sum sum) {
      sum.add(String.valueOf(lon)).add("//").add(String.valueOf(lat));
      if (toExtract!=null) toExtract.forEach(sum::add);
    }

    public Double getLon() {
      return lon;
    }

    public void setLon(Double lon) {
      this.lon = lon;
    }

    public Double getLat() {
      return lat;
    }

    public void setLat(Double lat) {
      this.lat = lat;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    @Override
    public List<String> getToExtract() {
      return toExtract;
    }

    @Override
    public void setToExtract(List<String> toExtract) {
      this.toExtract = toExtract;
    }
  }
}
