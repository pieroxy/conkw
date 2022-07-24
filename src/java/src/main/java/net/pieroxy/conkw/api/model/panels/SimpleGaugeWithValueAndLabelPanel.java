package net.pieroxy.conkw.api.model.panels;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.DashboardPanelType;

import java.util.List;

@TypeScriptType
@CompiledJson
public class SimpleGaugeWithValueAndLabelPanel extends DashboardPanel {

  private List<SimpleGaugeWithValueAndLabelElement> content;

  @Override
  public DashboardPanelType getDashboardPanelType() {
    return DashboardPanelType.SIMPLE_GAUGE_WITH_VALUE_AND_LABEL;
  }
}

