package net.pieroxy.conkw.webapp.grabbers.spotify;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
class CurrentlyPlayingResponseImage {
  @JsonAttribute
  public int height;
  @JsonAttribute
  public int width;
  @JsonAttribute
  public String url;
}
