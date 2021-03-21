package net.pieroxy.conkw.webapp.grabbers.openweathermap;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class MinutelyForecast {
  private double dt;
  private double precipitation;

  public double getDt() {
    return dt;
  }

  public void setDt(double dt) {
    this.dt = dt;
  }

  public double getPrecipitation() {
    return precipitation;
  }

  public void setPrecipitation(double precipitation) {
    this.precipitation = precipitation;
  }
}
