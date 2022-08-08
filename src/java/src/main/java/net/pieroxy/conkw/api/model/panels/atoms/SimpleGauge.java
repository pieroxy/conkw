package net.pieroxy.conkw.api.model.panels.atoms;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.model.DashboardDynamicValue;
import net.pieroxy.conkw.api.model.panels.atoms.model.ValueExpression;

@TypeScriptType
@CompiledJson
public class SimpleGauge extends DashboardDynamicValue {
  private ValueExpression max;
  private ValueExpression min;
  private boolean logarithmic;

  public ValueExpression getMax() {
    return max;
  }

  public void setMax(ValueExpression max) {
    this.max = max;
  }

  public ValueExpression getMin() {
    return min;
  }

  public void setMin(ValueExpression min) {
    this.min = min;
  }

  public boolean isLogarithmic() {
    return logarithmic;
  }

  public void setLogarithmic(boolean logarithmic) {
    this.logarithmic = logarithmic;
  }
}
