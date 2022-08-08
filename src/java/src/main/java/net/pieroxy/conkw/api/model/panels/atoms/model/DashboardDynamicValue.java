package net.pieroxy.conkw.api.model.panels.atoms.model;

import net.pieroxy.conkw.api.metadata.OptionalTypeScriptField;

public class DashboardDynamicValue extends DashboardDynamicElement {
  private ValueExpression value;
  @OptionalTypeScriptField
  private ValueExpression errorValue;
  private int staleDelay;

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

  public int getStaleDelay() {
    return staleDelay;
  }

  public void setStaleDelay(int staleDelay) {
    this.staleDelay = staleDelay;
  }
}
