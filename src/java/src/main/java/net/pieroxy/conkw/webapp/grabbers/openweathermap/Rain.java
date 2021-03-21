package net.pieroxy.conkw.webapp.grabbers.openweathermap;


import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class Rain {
  @JsonAttribute(name="1h")
  private double oneh;

  public double getOneh() {
    return oneh;
  }

  public void setOneh(double oneh) {
    this.oneh = oneh;
  }
}