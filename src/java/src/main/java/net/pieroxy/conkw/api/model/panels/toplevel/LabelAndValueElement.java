package net.pieroxy.conkw.api.model.panels.toplevel;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.DynamicLabel;
import net.pieroxy.conkw.api.model.panels.atoms.SiPrefixedValue;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElement;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElementEnum;

@CompiledJson
@TypeScriptType
public class LabelAndValueElement extends TopLevelPanelElement {
  TopLevelPanelElementEnum type;
  private DynamicLabel label;
  private SiPrefixedValue value;

  public LabelAndValueElement() {
    this.type = TopLevelPanelElementEnum.LABEL_VALUE;
  }

  @Override
  public TopLevelPanelElementEnum getType() {
    return type;
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

  public void setType(TopLevelPanelElementEnum type) {
    // Type is a constant
  }
}
