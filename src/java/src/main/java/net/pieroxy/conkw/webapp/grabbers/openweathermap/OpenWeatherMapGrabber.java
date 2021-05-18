package net.pieroxy.conkw.webapp.grabbers.openweathermap;

import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.webapp.grabbers.TimeThrottledGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class OpenWeatherMapGrabber extends TimeThrottledGrabber {
  static final CDuration CACHE_TTL = CDurationParser.parse("5m"); // 5 min
  static final String NAME = "owm";

  String token;
  double lat, lon;
  static Map<String, String> locationNames = new ConcurrentHashMap<>();

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public void setConfig(Map<String, String> config) {
    lon = Double.parseDouble(config.get("lon"));
    lat = Double.parseDouble(config.get("lat"));
    token = String.valueOf(config.get("token"));
  }

  @Override
  protected CDuration getTtl() {
    return CACHE_TTL;
  }

  @Override
  protected void load(ResponseData responseData) {
    if (token.equals("your api token here") || token.isEmpty()) {
      responseData.addError("Open Weather Map is not properly configured.");
      return;
    }

    String city = grabCity();

    try {
      URL url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&units=metric&appid="+token);
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

      responseData.addMetric("location_name", city);

      extract(responseData, "current", (res) -> {
        res.addMetric("current_desc", response.getCurrent().getWeather()[0].getDescription());
        res.addMetric("current_icon", getFullIcon((int)response.getCurrent().getWeather()[0].getId()));
        res.addMetric("current_temp", response.getCurrent().getTemp());
        res.addMetric("current_feels", response.getCurrent().getFeels_like());
        res.addMetric("current_pressure", response.getCurrent().getPressure());
        res.addMetric("current_humidity", response.getCurrent().getHumidity());
        res.addMetric("current_visibility", response.getCurrent().getVisibility());
        res.addMetric("current_wind_speed", response.getCurrent().getWind_speed()*3.6); // m/s by default -> km/h
        res.addMetric("current_wind_speed_icon", getWindIcon(response.getCurrent().getWind_speed()));
        res.addMetric("current_wind_deg", response.getCurrent().getWind_deg());
        res.addMetric("current_clouds", response.getCurrent().getClouds());
        res.addMetric("current_sunrise", response.getCurrent().getSunrise());
        res.addMetric("current_sunset", response.getCurrent().getSunset());
      }, Duration.ZERO);

      extract(responseData, "minute", (res) -> {
        int i=0;
        if (response.getMinutely()!=null) {
          if (canLogFine()) log(Level.FINE, "Minute extraction, number of elements: " + response.getMinutely().length);
          for (MinutelyForecast f : response.getMinutely()) {
            res.addMetric("minutely_pim_" + i++, f.getPrecipitation());
          }
        }
      }, Duration.ZERO);

      extract(responseData, "hour", (res) -> {
        int i=0;
        for (HourlyForecast f : response.getHourly()) {
          res.addMetric("hourly_dt_"+i, f.getDt());
          res.addMetric("hourly_temp_"+i, f.getTemp());
          res.addMetric("hourly_temp_feels_"+i, f.getFeels_like());
          res.addMetric("hourly_wind_speed_"+i, f.getWind_speed()*3.6); // m/s by default -> km/h
          res.addMetric("hourly_wind_speed_icon_"+i, getWindIcon(f.getWind_speed()));
          res.addMetric("hourly_wind_speed_beaufort_"+i, (double)getBeaufortScale(f.getWind_speed()));
          res.addMetric("hourly_pressure_"+i, f.getPressure());
          res.addMetric("hourly_humidity_"+i, f.getHumidity());
          res.addMetric("hourly_visibility_"+i, f.getVisibility());
          res.addMetric("hourly_desc_"+i, f.getWeather()[0].getDescription());
          res.addMetric("hourly_icon_"+i, getFullIcon((int)f.getWeather()[0].getId()));
          res.addMetric("hourly_pop_"+i, f.getPop());
          if (f.getRain()!=null)
            res.addMetric("hourly_rain_"+i, f.getRain().getOneh());
          else
            res.addMetric("hourly_rain_"+i, 0.);
          if (f.getSnow()!=null)
            res.addMetric("hourly_snow_"+i, f.getSnow().getOneh());
          else
            res.addMetric("hourly_snow_"+i, 0.);

          i++;
        }
      }, Duration.ZERO);

      extract(responseData, "day", (res) -> {
        int i=0;
        for (DailyForecast f : response.getDaily()) {
          res.addMetric("daily_dt_"+i, f.getDt());
          res.addMetric("daily_temp_max_"+i, f.getTemp().getMax());
          res.addMetric("daily_temp_min_"+i, f.getTemp().getMin());
          res.addMetric("daily_wind_speed_"+i, f.getWind_speed()*3.6);// m/s by default -> km/h
          res.addMetric("daily_wind_speed_icon_"+i, getWindIcon(f.getWind_speed()));
          res.addMetric("daily_wind_speed_beaufort_"+i, (double)getBeaufortScale(f.getWind_speed()));
          res.addMetric("daily_pressure_"+i, f.getPressure());
          res.addMetric("daily_humidity_"+i, f.getHumidity());
          res.addMetric("daily_desc_"+i, f.getWeather()[0].getDescription());
          res.addMetric("daily_icon_"+i, getFullIcon((int)f.getWeather()[0].getId()));
          res.addMetric("daily_rain_"+i, f.getRain());
          res.addMetric("daily_snow_"+i, f.getSnow());
          res.addMetric("daily_pop_"+i, f.getPop());

          i++;
        }
      }, Duration.ZERO);
    } catch (Exception e) {
      e.printStackTrace();
      responseData.addError("owm: " + e.getMessage());
    }
  }

  @Override
  protected String getCacheKey() {
    return lat + "x" + lon;
  }

  private String grabCity() {
    String key = lat+"x"+lon;
    if (!locationNames.containsKey(key)) {
      try {
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=" + token);
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

  private String getFullIcon(int num) {
    return "/owm-icons/wi-" + getIcon(num) + ".svg";
  }
  private String getIcon(int num) {
    // Uses https://erikflowers.github.io/weather-icons/
    // Also available at https://github.com/erikflowers/weather-icons
    int tod = 0; // 0=neutral, 1=day, 2=night
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
}
