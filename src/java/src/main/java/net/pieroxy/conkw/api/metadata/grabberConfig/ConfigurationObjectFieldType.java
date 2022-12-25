package net.pieroxy.conkw.api.metadata.grabberConfig;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@CompiledJson
@TypeScriptType
public enum ConfigurationObjectFieldType {
  NUMBER,
  STRING,
  BOOLEAN,
  OBJECT
}
