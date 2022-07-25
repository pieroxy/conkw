package net.pieroxy.conkw.api.model.panels;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.DynamicLabel;
import net.pieroxy.conkw.api.model.panels.atoms.SiPrefixedValue;
import net.pieroxy.conkw.api.model.panels.atoms.SimpleGauge;
import net.pieroxy.conkw.api.model.panels.atoms.model.DashboardDynamicElement;

@CompiledJson
@TypeScriptType
public class SimpleGaugeWithValueAndLabelElement extends DashboardDynamicElement {
  private DynamicLabel label;
  private SiPrefixedValue value;
  private SimpleGauge gauge;

  public DynamicLabel getLabel() {
    return label;
  }

  public void setLabel(DynamicLabel label) {
    this.label = label;
  }

  public SiPrefixedValue getValue() {
    return value;
  }

  public void setValue(SiPrefixedValue value) {
    this.value = value;
  }

  public SimpleGauge getGauge() {
    return gauge;
  }

  public void setGauge(SimpleGauge gauge) {
    this.gauge = gauge;
  }
}