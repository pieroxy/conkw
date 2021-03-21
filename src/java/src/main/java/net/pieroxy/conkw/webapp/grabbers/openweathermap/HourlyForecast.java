package net.pieroxy.conkw.webapp.grabbers.openweathermap;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class HourlyForecast {

  private double dt;
  private double temp;
  private double feels_like;
  private double pressure;
  private double humidity;
  private double dew_point;
  private double uvi;
  private double clouds;
  private double visibility;
  private double wind_speed;
  private double wind_deg;
  private double pop;
  private Rain rain;
  private Rain snow;


  WeatherElement[]weather;

  public double getDt() {
    return dt;
  }

  public void setDt(double dt) {
    this.dt = dt;
  }

  public double getTemp() {
    return temp;
  }

  public void setTemp(double temp) {
    this.temp = temp;
  }

  public double getFeels_like() {
    return feels_like;
  }

  public void setFeels_like(double feels_like) {
    this.feels_like = feels_like;
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

  public double getUvi() {
    return uvi;
  }

  public void setUvi(double uvi) {
    this.uvi = uvi;
  }

  public double getClouds() {
    return clouds;
  }

  public void setClouds(double clouds) {
    this.clouds = clouds;
  }

  public double getVisibility() {
    return visibility;
  }

  public void setVisibility(double visibility) {
    this.visibility = visibility;
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

  public double getPop() {
    return pop;
  }

  public void setPop(double pop) {
    this.pop = pop;
  }

  public WeatherElement[] getWeather() {
    return weather;
  }

  public void setWeather(WeatherElement[] weather) {
    this.weather = weather;
  }

  public Rain getRain() {
    return rain;
  }

  public void setRain(Rain rain) {
    this.rain = rain;
  }

  public Rain getSnow() {
    return snow;
  }

  public void setSnow(Rain snow) {
    this.snow = snow;
  }
}