package net.pieroxy.conkw.webapp.grabbers.spotify;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
class AccessTokenResponse {
  public String access_token;
  public String token_type;
  public int expires_in;
  public String refresh_token;
  public String scope;
  public String error;
  public String error_description;
}
