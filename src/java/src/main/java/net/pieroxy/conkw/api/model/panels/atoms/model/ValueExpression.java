package net.pieroxy.conkw.api.model.panels.atoms.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.OptionalTypeScriptField;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@CompiledJson
@TypeScriptType
public class ValueExpression {
  private String namespace;
  private ExpressionClass clazz;
  private ExpressionValueType type;
  @OptionalTypeScriptField
  private NumberDirective directive;
  private String value;

  public ExpressionClass getClazz() {
    return clazz;
  }

  public void setClazz(ExpressionClass clazz) {
    this.clazz = clazz;
  }

  public ExpressionValueType getType() {
    return type;
  }

  public void setType(ExpressionValueType type) {
    this.type = type;
  }

  public NumberDirective getDirective() {
    return directive;
  }

  public void setDirective(NumberDirective directive) {
    this.directive = directive;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }
}
