package net.pieroxy.conkw.api.model.panels.atoms;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.model.DashboardDynamicValue;

@CompiledJson
@TypeScriptType
public class SiPrefixedValue extends DashboardDynamicValue {
  private String unit;
  private int thousand;

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public int getThousand() {
    return thousand;
  }

  public void setThousand(int thousand) {
    this.thousand = thousand;
  }
}
