package net.pieroxy.conkw.api.model.panels;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@TypeScriptType
@CompiledJson
public class SimpleGauge {
  private String metric;
  private String max;
  private String min;
  private boolean thresholdAbove;
  private String threshold;
  private String tooltip;

  public String getMetric() {
    return metric;
  }

  public void setMetric(String metric) {
    this.metric = metric;
  }

  public String getMax() {
    return max;
  }

  public void setMax(String max) {
    this.max = max;
  }

  public String getMin() {
    return min;
  }

  public void setMin(String min) {
    this.min = min;
  }

  public boolean isThresholdAbove() {
    return thresholdAbove;
  }

  public void setThresholdAbove(boolean thresholdAbove) {
    this.thresholdAbove = thresholdAbove;
  }

  public String getThreshold() {
    return threshold;
  }

  public void setThreshold(String threshold) {
    this.threshold = threshold;
  }

  public String getTooltip() {
    return tooltip;
  }

  public void setTooltip(String tooltip) {
    this.tooltip = tooltip;
  }
}
