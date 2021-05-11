package net.pieroxy.conkw.webapp.servlets.auth;

import com.dslplatform.json.CompiledJson;

import java.util.HashMap;
import java.util.Map;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class Sessions {
  private Map<String, Session> sessions;

  public Sessions() {
    sessions = new HashMap<>();
  }

  public Session getSession(String sid) {
    return sessions.get(sid);
  }

  public Map<String, Session> getSessions() {
    return sessions;
  }

  public void setSessions(Map<String, Session> sessions) {
    this.sessions = sessions;
  }
}
