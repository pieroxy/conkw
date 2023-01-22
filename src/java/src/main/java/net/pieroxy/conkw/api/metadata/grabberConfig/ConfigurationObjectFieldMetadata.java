package net.pieroxy.conkw.api.metadata.grabberConfig;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.IdLabelPair;

import java.util.List;

@CompiledJson
@TypeScriptType
public class ConfigurationObjectFieldMetadata {
  private ConfigurationObjectFieldType type;
  private boolean isList;
  private String listItemsName;
  private IdLabelPair[] possibleValues;
  private String name;
  private String label;
  private List<ConfigurationObjectFieldMetadata> subFields; // Only used when type = OBJECT

  public ConfigurationObjectFieldType getType() {
    return type;
  }

  public void setType(ConfigurationObjectFieldType type) {
    this.type = type;
  }

  public boolean isList() {
    return isList;
  }

  public void setList(boolean list) {
    isList = list;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ConfigurationObjectFieldMetadata> getSubFields() {
    return subFields;
  }

  public void setSubFields(List<ConfigurationObjectFieldMetadata> subFields) {
    this.subFields = subFields;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getListItemsName() {
    return listItemsName;
  }

  public void setListItemsName(String listItemsName) {
    this.listItemsName = listItemsName;
  }

  public IdLabelPair[] getPossibleValues() {
    return possibleValues;
  }

  public void setPossibleValues(IdLabelPair[] possibleValues) {
    this.possibleValues = possibleValues;
  }
}
