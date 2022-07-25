package net.pieroxy.conkw.api.model.panels.atoms.model;

import net.pieroxy.conkw.api.metadata.OptionalTypeScriptField;

public abstract class DashboardDynamicElement {
  @OptionalTypeScriptField
  ValueExpression tooltip;
  @OptionalTypeScriptField
  ValueExpression stale;
  @OptionalTypeScriptField
  ValueExpression error;
  @OptionalTypeScriptField
  String namespace;

  public ValueExpression getTooltip() {
    return tooltip;
  }

  public void setTooltip(ValueExpression tooltip) {
    this.tooltip = tooltip;
  }

  public ValueExpression getStale() {
    return stale;
  }

  public void setStale(ValueExpression stale) {
    this.stale = stale;
  }

  public ValueExpression getError() {
    return error;
  }

  public void setError(ValueExpression error) {
    this.error = error;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }
}
