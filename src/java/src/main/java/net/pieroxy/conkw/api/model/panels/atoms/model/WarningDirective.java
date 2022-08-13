package net.pieroxy.conkw.api.model.panels.atoms.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@TypeScriptType
@CompiledJson
public enum WarningDirective {
    ISNOT,
    IS,
    VALUECONTAINS,
    VALUEABOVE,
    VALUEBELOW
}
