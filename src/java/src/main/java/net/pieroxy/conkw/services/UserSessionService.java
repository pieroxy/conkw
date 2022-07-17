package net.pieroxy.conkw.services;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.LocalStorageManager;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.hashing.HashTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class UserSessionService {
  private final static Logger LOGGER = Logger.getLogger(UserSessionService.class.getName());
  public static final int SESSION_TOKEN_DIGITS = 32;
  public static final long SESSION_DEFAULT_DURATION = (long)1000*60*60*24*365;
  private final File sessionsFile;

  public UserSessionService(LocalStorageManager localStorageManager) {
    sessionsFile = new File(localStorageManager.getConfDir(), "usersSessions.json");
  }

  public Session getSession(String token) {
    Sessions ss = readSessions();
    for (Session s : ss.getSessions()) {
      if (token.equals(s.getToken())) return s;
    }
    return null;
  }

  public String createSession(User u) {
    Session session = new Session();
    session.setExpiration(new Date(System.currentTimeMillis() + SESSION_DEFAULT_DURATION)); // 1 year sessions
    session.setUserid(u.getId());
    session.setToken(HashTools.getRandomSequence(SESSION_TOKEN_DIGITS)); // 128 bits should be enough
    synchronized (session) {
      Sessions ss = readSessions();
      ss.getSessions().add(session);
      save(ss);
    }
    return session.getToken();
  }

  private void save(Sessions ss) {
    try {
      JsonHelper.writeToFile(ss, sessionsFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Sessions readSessions() {
    try {
      Sessions sessions = JsonHelper.readFromFile(Sessions.class, sessionsFile);
      if (sessions == null) {
        sessions = new Sessions();
        sessions.setSessions(new ArrayList<>());
        LOGGER.warning("Sessions file ("+sessionsFile+") could not be found/read. Creating a new one.");
      }
      return sessions;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
  public static class Session {
    private String userid;
    private String token;
    private Date expiration;

    public String getUserid() {
      return userid;
    }

    public void setUserid(String userid) {
      this.userid = userid;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public Date getExpiration() {
      return expiration;
    }

    public void setExpiration(Date expiration) {
      this.expiration = expiration;
    }
  }


  public static @CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
  class Sessions {
    List<Session> sessions;

    public List<Session> getSessions() {
      return sessions;
    }

    public void setSessions(List<Session> sessions) {
      this.sessions = sessions;
    }
  }
}

