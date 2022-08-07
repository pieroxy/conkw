package net.pieroxy.conkw.api.model.panels;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.DynamicLabel;
import net.pieroxy.conkw.api.model.panels.atoms.SiPrefixedValue;
import net.pieroxy.conkw.api.model.panels.atoms.SimpleGauge;
import net.pieroxy.conkw.api.model.panels.atoms.model.*;

@CompiledJson
@TypeScriptType
public class SimpleGaugeWithValueAndLabelElement extends TopLevelPanelElement {
  TopLevelPanelElementEnum type;
  private DynamicLabel label;
  private SiPrefixedValue value;
  private SimpleGauge gauge;

  public SimpleGaugeWithValueAndLabelElement() {
    this.type = TopLevelPanelElementEnum.SIMPLE_GAUGE;
  }

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
  @Override
  public TopLevelPanelElementEnum getType() {
    return type;
  }

  public void setType(TopLevelPanelElementEnum type) {
    this.type = type;
  }

  @Override
  public void initialize() {
    label = new DynamicLabel();
    label.setValue(new ValueExpression());
    label.getValue().setValue("The label");
    label.getValue().setType(ExpressionValueType.STRING);
    label.getValue().setClazz(ExpressionClass.LITERAL);

    value = new SiPrefixedValue();
    value.setValue(new ValueExpression());
    value.getValue().setValue("35");
    value.getValue().setType(ExpressionValueType.NUMERIC);
    value.getValue().setClazz(ExpressionClass.LITERAL);
    value.setUnit("%");
    value.setThousand(1000);

    gauge = new SimpleGauge();
    gauge.setMin(new ValueExpression());
    gauge.getMin().setValue("0");
    gauge.getMin().setType(ExpressionValueType.NUMERIC);
    gauge.getMin().setClazz(ExpressionClass.LITERAL);
    gauge.setMax(new ValueExpression());
    gauge.getMax().setValue("100");
    gauge.getMax().setType(ExpressionValueType.NUMERIC);
    gauge.getMax().setClazz(ExpressionClass.LITERAL);
    gauge.setValue(new ValueExpression());
    gauge.getValue().setValue("35");
    gauge.getValue().setType(ExpressionValueType.NUMERIC);
    gauge.getValue().setClazz(ExpressionClass.LITERAL);
  }
}