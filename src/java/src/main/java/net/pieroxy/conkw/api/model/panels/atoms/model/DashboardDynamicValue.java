package net.pieroxy.conkw.api.model.panels.atoms.model;

import net.pieroxy.conkw.api.metadata.OptionalTypeScriptField;

public class DashboardDynamicValue extends DashboardDynamicElement {
  private ValueExpression value;
  @OptionalTypeScriptField
  private ValueExpression errorValue;

  public ValueExpression getValue() {
    return value;
  }

  public void setValue(ValueExpression value) {
    this.value = value;
  }

  public ValueExpression getErrorValue() {
    return errorValue;
  }

  public void setErrorValue(ValueExpression errorValue) {
    this.errorValue = errorValue;
  }
}
