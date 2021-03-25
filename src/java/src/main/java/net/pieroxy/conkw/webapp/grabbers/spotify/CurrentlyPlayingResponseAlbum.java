package net.pieroxy.conkw.webapp.grabbers.spotify;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
class CurrentlyPlayingResponseAlbum {
  @JsonAttribute
  public String album_type;
  @JsonAttribute
  public CurrentlyPlayingResponseArtist[] artists;
  @JsonAttribute
  public String[] available_markets;
  @JsonAttribute(ignore = true)
  public Object external_urls;
  @JsonAttribute
  public String href;
  @JsonAttribute
  public String id;
  @JsonAttribute
  public CurrentlyPlayingResponseImage[] images;
  @JsonAttribute
  public String name;
  @JsonAttribute
  public String release_date;
  @JsonAttribute
  public String release_date_precision;
  @JsonAttribute
  public int total_tracks;
  @JsonAttribute
  public String type;
  @JsonAttribute
  public String uri;
}
