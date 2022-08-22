package net.pieroxy.conkw.api.model.panels.toplevel;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.DynamicLabel;
import net.pieroxy.conkw.api.model.panels.atoms.GaugeWithHistory;
import net.pieroxy.conkw.api.model.panels.atoms.model.DashboardDynamicValue;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElement;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElementEnum;

@CompiledJson
@TypeScriptType
public class GaugeWithHistoryElement extends TopLevelPanelElement {
    TopLevelPanelElementEnum type;
    private DynamicLabel label;
    private DashboardDynamicValue value;
    private GaugeWithHistory gauge;
    private boolean valueIsGauge;

    public GaugeWithHistoryElement() {
        this.type = TopLevelPanelElementEnum.GAUGE_WITH_HISTORY;
    }

    @Override
    public TopLevelPanelElementEnum getType() {
        return type;
    }

    public void setType(TopLevelPanelElementEnum type) {
        // Type is a constant
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

    public GaugeWithHistory getGauge() {
        return gauge;
    }

    public void setGauge(GaugeWithHistory gauge) {
        this.gauge = gauge;
    }

    public boolean isValueIsGauge() {
        return valueIsGauge;
    }

    public void setValueIsGauge(boolean valueIsGauge) {
        this.valueIsGauge = valueIsGauge;
    }
}
