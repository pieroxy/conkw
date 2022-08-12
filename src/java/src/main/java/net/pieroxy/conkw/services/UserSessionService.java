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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UserSessionService implements Runnable {
  private final static Logger LOGGER = Logger.getLogger(UserSessionService.class.getName());
  public static final int SESSION_TOKEN_DIGITS = 32; // 128 bits should be enough
  public static final long SESSION_DEFAULT_DURATION = (long)1000*60*60*24*365; // 1 year sessions
  private final File sessionsFile;
  private Thread saveThread;
  private boolean stopped = false;
  private boolean modified = false;

  private Sessions store;

  public UserSessionService(LocalStorageManager localStorageManager) {
    sessionsFile = new File(localStorageManager.getConfDir(), "usersSessions.json");
    saveThread = new Thread(this);
    saveThread.start();
  }

  public void dispose() {
    saveThread = null;
    synchronized (this) {
      this.notifyAll();
    }
  }

  public void run() {
    store = __readSessions();
    while (saveThread == Thread.currentThread()) {
      try {
        synchronized (this) {
          this.wait(60*60*1000);
        }
      } catch (InterruptedException e) {
      }
      if (modified) {
        modified = false;
        try {
          __save(store);
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Could not save user sessions", e);
        }
      }
    }
    LOGGER.info(getClass().getSimpleName() + " stopping");
    stopped = true;
  }

  public Session getSession(String token) {
    Sessions ss = store;
    for (Session s : ss.getSessions()) {
      if (token.equals(s.getToken())) {
        s.setLastUsed(new Date());
        modified = true;
        return new Session(s);
      }
    }
    return null;
  }

  public String createSession(User u, String userAgent, String ip) {
    Session session = new Session();
    session.setExpiration(new Date(System.currentTimeMillis() + SESSION_DEFAULT_DURATION));
    session.setUserid(u.getId());
    session.setToken(HashTools.getRandomSequence(SESSION_TOKEN_DIGITS));
    session.setCreation(new Date());
    session.setUserAgent(userAgent);
    session.setIp(ip);
    synchronized (this) {
      store.getSessions().add(session);
      modified = true;
      this.notifyAll();
    }
    return session.getToken();
  }

  private synchronized void __save(Sessions ss) {
    try {
      JsonHelper.writeToFile(ss, sessionsFile);
      LOGGER.info("User sessions saved");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Sessions __readSessions() {
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

  public void removeSession(String token) {
    synchronized (this) {
      store.setSessions(store.getSessions().stream().filter((session) -> !session.getToken().equals(token)).collect(Collectors.toList()));
      modified = true;
      this.notifyAll();
    }
  }

  public List<Session> getSessions(User user) {
    return store.getSessions().stream().filter(s -> s.getUserid().equals(user.getId())).map(Session::new).collect(Collectors.toList());
  }


  @CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
  public static class Session {
    private String userid;
    private Date lastUsed;
    private String token;
    private Date expiration;
    private Date creation;
    private String userAgent, ip;

    public Session() {
    }

    public Session(Session session) {
      this.userid = session.userid;
      this.lastUsed = session.lastUsed;
      this.token = session.token;
      this.expiration = session.expiration;
      this.creation = session.creation;
      this.userAgent = session.userAgent;
      this.ip = session.ip;
    }

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

    public Date getCreation() {
      return creation;
    }

    public void setCreation(Date creation) {
      this.creation = creation;
    }

    public String getUserAgent() {
      return userAgent;
    }

    public void setUserAgent(String userAgent) {
      this.userAgent = userAgent;
    }

    public String getIp() {
      return ip;
    }

    public void setIp(String ip) {
      this.ip = ip;
    }

    public Date getLastUsed() {
      return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
      this.lastUsed = lastUsed;
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

