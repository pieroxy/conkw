package net.pieroxy.conkw.api.model.panels;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.DynamicLabel;
import net.pieroxy.conkw.api.model.panels.atoms.GaugeWithHistory;
import net.pieroxy.conkw.api.model.panels.atoms.SiPrefixedValue;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElement;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElementEnum;

@CompiledJson
@TypeScriptType
public class GaugeWithHistoryElement extends TopLevelPanelElement {
    TopLevelPanelElementEnum type;
    private DynamicLabel label;
    private SiPrefixedValue value;
    private GaugeWithHistory gauge;
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

    public SiPrefixedValue getValue() {
        return value;
    }

    public void setValue(SiPrefixedValue value) {
        this.value = value;
    }

    public GaugeWithHistory getGauge() {
        return gauge;
    }

    public void setGauge(GaugeWithHistory gauge) {
        this.gauge = gauge;
    }
}
