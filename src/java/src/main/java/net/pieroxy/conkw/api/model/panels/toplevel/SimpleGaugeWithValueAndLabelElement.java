package net.pieroxy.conkw.api.model.panels.toplevel;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.DynamicLabel;
import net.pieroxy.conkw.api.model.panels.atoms.SimpleGauge;
import net.pieroxy.conkw.api.model.panels.atoms.model.DashboardDynamicValue;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElement;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElementEnum;

@CompiledJson
@TypeScriptType
public class SimpleGaugeWithValueAndLabelElement extends TopLevelPanelElement {
  TopLevelPanelElementEnum type;
  private DynamicLabel label;
  private DashboardDynamicValue value;
  private SimpleGauge gauge;
  private boolean valueIsGauge;

  public SimpleGaugeWithValueAndLabelElement() {
    this.type = TopLevelPanelElementEnum.SIMPLE_GAUGE;
  }

  public DynamicLabel getLabel() {
    return label;
  }

  public void setLabel(DynamicLabel label) {
    this.label = label;
  }

  public DashboardDynamicValue getValue() {
    return value;
  }

  public void setValue(DashboardDynamicValue value) {
    this.value = value;
  }

  public SimpleGauge getGauge() {
    return gauge;
  }

  public void setGauge(SimpleGauge gauge) {
    this.gauge = gauge;
  }
  @Override
  public TopLevelPanelElementEnum getType() {
    return type;
  }

  public void setType(TopLevelPanelElementEnum type) {
    this.type = type;
  }

  public boolean isValueIsGauge() {
    return valueIsGauge;
  }

  public void setValueIsGauge(boolean valueIsGauge) {
    this.valueIsGauge = valueIsGauge;
  }
}