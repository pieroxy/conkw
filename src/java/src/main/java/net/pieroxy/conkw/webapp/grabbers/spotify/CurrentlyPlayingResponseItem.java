package net.pieroxy.conkw.webapp.grabbers.spotify;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
class CurrentlyPlayingResponseItem {
  @JsonAttribute
  public String[] available_markets;
  @JsonAttribute
  public CurrentlyPlayingResponseAlbum album;
  @JsonAttribute
  public CurrentlyPlayingResponseArtist[] artists;
  @JsonAttribute
  public String href;
  @JsonAttribute
  public String id;
  @JsonAttribute
  public boolean is_local;
  @JsonAttribute
  public String name;
  @JsonAttribute
  public int popularity;
  @JsonAttribute
  public String preview_url;
  @JsonAttribute
  public int track_number;
  @JsonAttribute
  public String type;
  @JsonAttribute
  public String uri;
  @JsonAttribute
  public int disc_number;
  @JsonAttribute
  public long duration_ms;
  @JsonAttribute
  public boolean explicit;
}
