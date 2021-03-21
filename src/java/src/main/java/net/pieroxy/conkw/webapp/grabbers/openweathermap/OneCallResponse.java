package net.pieroxy.conkw.webapp.grabbers.openweathermap;


import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class OneCallResponse {

  @JsonAttribute(mandatory = true)
  private double lat;
  @JsonAttribute(mandatory = true)
  private double lon;
  @JsonAttribute
  private String timezone;
  @JsonAttribute
  private double timezone_offset;
  @JsonAttribute
  private CurrentWeather current;
  @JsonAttribute
  private MinutelyForecast [] minutely;
  @JsonAttribute
  private HourlyForecast [] hourly;
  @JsonAttribute
  private DailyForecast[] daily;
  @JsonAttribute
  private WeatherAlert[]alerts;

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLon() {
    return lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public double getTimezone_offset() {
    return timezone_offset;
  }

  public void setTimezone_offset(double timezone_offset) {
    this.timezone_offset = timezone_offset;
  }

  public CurrentWeather getCurrent() {
    return current;
  }

  public void setCurrent(CurrentWeather current) {
    this.current = current;
  }

  public MinutelyForecast[] getMinutely() {
    return minutely;
  }

  public void setMinutely(MinutelyForecast[] minutely) {
    this.minutely = minutely;
  }

  public HourlyForecast[] getHourly() {
    return hourly;
  }

  public void setHourly(HourlyForecast[] hourly) {
    this.hourly = hourly;
  }

  public DailyForecast[] getDaily() {
    return daily;
  }

  public void setDaily(DailyForecast[] daily) {
    this.daily = daily;
  }

  public WeatherAlert[] getAlerts() {
    return alerts;
  }

  public void setAlerts(WeatherAlert[] alerts) {
    this.alerts = alerts;
  }
}
