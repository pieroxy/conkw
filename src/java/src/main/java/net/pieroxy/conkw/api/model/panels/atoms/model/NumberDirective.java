package net.pieroxy.conkw.api.model.panels.atoms.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.OptionalTypeScriptField;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@CompiledJson
@TypeScriptType
public class NumberDirective {
  @OptionalTypeScriptField
  private NumberOperator operator;
  @OptionalTypeScriptField
  private double value;
  @OptionalTypeScriptField
  private NumberFormat format;
  @OptionalTypeScriptField
  private WarningDirective test;
  @OptionalTypeScriptField
  private String unit;
  @OptionalTypeScriptField
  private int thousand;


  public NumberOperator getOperator() {
    return operator;
  }

  public void setOperator(NumberOperator operator) {
    this.operator = operator;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public NumberFormat getFormat() {
    return format;
  }

  public void setFormat(NumberFormat format) {
    this.format = format;
  }

  public WarningDirective getTest() {
    return test;
  }

  public void setTest(WarningDirective test) {
    this.test = test;
  }

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
