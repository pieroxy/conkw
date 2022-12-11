package net.pieroxy.conkw.api.implementations.grabbers;


import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.grabbersBase.Grabber;

import java.util.logging.Level;

@TypeScriptType
@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class GrabberDetail {
  private String implementation;
  private String name;
  private Level logLevel;

  public GrabberDetail(Grabber grabber) {
    name = grabber.getName();
    implementation = grabber.getClass().getSimpleName();
    logLevel = grabber.getLogLevel();
  }

  public GrabberDetail() {
  }
}
