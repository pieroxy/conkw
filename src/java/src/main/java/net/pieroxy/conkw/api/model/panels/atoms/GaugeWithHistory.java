package net.pieroxy.conkw.api.model.panels.atoms;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@TypeScriptType
@CompiledJson
public class GaugeWithHistory extends SimpleGauge {
    private int nbLinesHeight;

    public int getNbLinesHeight() {
        return nbLinesHeight;
    }

    public void setNbLinesHeight(int nbLinesHeight) {
        this.nbLinesHeight = nbLinesHeight;
    }
}
