package net.pieroxy.conkw.webapp.grabbers.spotify;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public
class CurrentlyPlayingResponse {
  @JsonAttribute
  public long timestamp;
  @JsonAttribute(ignore = true)
  public Object context;
  @JsonAttribute
  public long progress_ms; // The current playing time in the song
  @JsonAttribute
  public CurrentlyPlayingResponseItem item;
  @JsonAttribute
  public String currently_playing_type;
  @JsonAttribute(ignore = true)
  public Object actions;
  @JsonAttribute
  public boolean is_playing;
  @JsonAttribute
  public SpotifyResponseError error;
}
