package net.pieroxy.conkw.webapp.grabbers.spotify;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
class CurrentlyPlayingResponseArtist {
  @JsonAttribute(ignore = true)
  public Object external_urls;
  @JsonAttribute
  public String href;
  @JsonAttribute
  public String id;
  @JsonAttribute
  public String name;
  @JsonAttribute
  public String type;
  @JsonAttribute
  public String uri;
}
