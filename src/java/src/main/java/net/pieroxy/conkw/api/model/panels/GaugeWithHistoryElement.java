package net.pieroxy.conkw.api.model.panels;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.DynamicLabel;
import net.pieroxy.conkw.api.model.panels.atoms.GaugeWithHistory;
import net.pieroxy.conkw.api.model.panels.atoms.SiPrefixedValue;
import net.pieroxy.conkw.api.model.panels.atoms.model.*;

@CompiledJson
@TypeScriptType
public class GaugeWithHistoryElement extends TopLevelPanelElement {
    TopLevelPanelElementEnum type;
    private DynamicLabel label;
    private SiPrefixedValue value;
    private GaugeWithHistory gauge;
    private int lines;

    public GaugeWithHistoryElement() {
        this.type = TopLevelPanelElementEnum.GAUGE_WITH_HISTORY;
    }

    @Override
    public void initialize() {
        label = new DynamicLabel();
        label.setValue(new ValueExpression());
        label.getValue().setValue("The label");
        label.getValue().setType(ExpressionValueType.STRING);
        label.getValue().setClazz(ExpressionClass.LITERAL);
        label.setStaleDelay(5);

        value = new SiPrefixedValue();
        value.setValue(new ValueExpression());
        value.getValue().setValue("35");
        value.getValue().setType(ExpressionValueType.NUMERIC);
        value.getValue().setClazz(ExpressionClass.LITERAL);
        value.setUnit("%");
        value.setThousand(1000);
        value.setStaleDelay(5);

        gauge = new GaugeWithHistory();
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
        gauge.setNbLinesHeight(2);
        gauge.setStaleDelay(5);
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

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }
}
