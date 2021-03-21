package net.pieroxy.conkw.webapp.grabbers.openweathermap;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class DailyForecast {
  private double dt;
  private double sunrise;
  private double sunset;
  private double pressure;
  private double humidity;
  private double dew_point;
  private double wind_speed;
  private double wind_deg;
  private double clouds;
  private double pop;
  private double rain;
  private double snow;
  private double uvi;
  private WeatherElement[]weather;
  private TempElement temp;
  private DailyFeelsLike feels_like ;

  public double getDt() {
    return dt;
  }

  public void setDt(double dt) {
    this.dt = dt;
  }

  public double getSunrise() {
    return sunrise;
  }

  public void setSunrise(double sunrise) {
    this.sunrise = sunrise;
  }

  public double getSunset() {
    return sunset;
  }

  public void setSunset(double sunset) {
    this.sunset = sunset;
  }

  public double getPressure() {
    return pressure;
  }

  public void setPressure(double pressure) {
    this.pressure = pressure;
  }

  public double getHumidity() {
    return humidity;
  }

  public void setHumidity(double humidity) {
    this.humidity = humidity;
  }

  public double getDew_point() {
    return dew_point;
  }

  public void setDew_point(double dew_point) {
    this.dew_point = dew_point;
  }

  public double getWind_speed() {
    return wind_speed;
  }

  public void setWind_speed(double wind_speed) {
    this.wind_speed = wind_speed;
  }

  public double getWind_deg() {
    return wind_deg;
  }

  public void setWind_deg(double wind_deg) {
    this.wind_deg = wind_deg;
  }

  public double getClouds() {
    return clouds;
  }

  public void setClouds(double clouds) {
    this.clouds = clouds;
  }

  public double getPop() {
    return pop;
  }

  public void setPop(double pop) {
    this.pop = pop;
  }

  public double getRain() {
    return rain;
  }

  public void setRain(double rain) {
    this.rain = rain;
  }

  public double getUvi() {
    return uvi;
  }

  public void setUvi(double uvi) {
    this.uvi = uvi;
  }

  public WeatherElement[] getWeather() {
    return weather;
  }

  public void setWeather(WeatherElement[] weather) {
    this.weather = weather;
  }

  public TempElement getTemp() {
    return temp;
  }

  public void setTemp(TempElement temp) {
    this.temp = temp;
  }

  public DailyFeelsLike getFeels_like() {
    return feels_like;
  }

  public void setFeels_like(DailyFeelsLike feels_like) {
    this.feels_like = feels_like;
  }

  public double getSnow() {
    return snow;
  }

  public void setSnow(double snow) {
    this.snow = snow;
  }
}