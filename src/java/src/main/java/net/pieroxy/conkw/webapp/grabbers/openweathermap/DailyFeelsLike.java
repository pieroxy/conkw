package net.pieroxy.conkw.webapp.grabbers.openweathermap;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class DailyFeelsLike {
  private double day;
  private double night;
  private double eve;
  private double morn;

  public double getDay() {
    return day;
  }

  public void setDay(double day) {
    this.day = day;
  }

  public double getNight() {
    return night;
  }

  public void setNight(double night) {
    this.night = night;
  }

  public double getEve() {
    return eve;
  }

  public void setEve(double eve) {
    this.eve = eve;
  }

  public double getMorn() {
    return morn;
  }

  public void setMorn(double morn) {
    this.morn = morn;
  }
}