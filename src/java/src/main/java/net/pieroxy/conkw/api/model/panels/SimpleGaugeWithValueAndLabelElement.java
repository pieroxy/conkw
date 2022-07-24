package net.pieroxy.conkw.api.model.panels;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@CompiledJson
@TypeScriptType
public class SimpleGaugeWithValueAndLabelElement {
  private String label;
  private String unit;
  private SimpleGauge gauge;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public SimpleGauge getGauge() {
    return gauge;
  }

  public void setGauge(SimpleGauge gauge) {
    this.gauge = gauge;
  }
}