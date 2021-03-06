package net.pieroxy.conkw.webapp.servlets;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.config.ApiAuth;
import net.pieroxy.conkw.config.User;
import net.pieroxy.conkw.utils.HashTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.model.NeedsAuthResponse;
import net.pieroxy.conkw.webapp.servlets.auth.ChallengeResponse;
import net.pieroxy.conkw.webapp.servlets.auth.Session;
import net.pieroxy.conkw.webapp.servlets.auth.Sessions;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiAuthManager {
  private final static Logger LOGGER = Logger.getLogger(ApiAuthManager.class.getName());
  public final static String USER_FIELD = "__U__";
  public final static String PASS_FIELD = "__P__";
  public final static String SID_FIELD = "__SID__";

  private ApiAuth authConfig;
  private Sessions sessions = new Sessions();
  private List<ChallengeResponse> challenges = new ArrayList<>();
  private File sessionsStore;
  private boolean changed;
  private Thread saveThread;

  public void applyConfig(ApiAuth auth, File dataDir) {
    ApiAuth newAuth = auth == null ? new ApiAuth() : auth;
    updateAuthConfig(newAuth);
    sessionsStore = new File(dataDir, "sessions.json");
    if (saveThread==null) initFromStore(auth);
  }

  private void updateAuthConfig(ApiAuth newAuth) {
    Map<String, Session> newsessions = new HashMap<>();
    List<ChallengeResponse> newchallenges = new ArrayList<>();
    Map<String, User> allUsersToKeep = new HashMap<>();
    if (authConfig!=null) {
      Arrays.stream(authConfig.getUsers()).forEach(u -> allUsersToKeep.put(u.getLogin(), u));
    }
    Arrays.stream(newAuth.getUsers()).forEach(u -> {
      User current = allUsersToKeep.get(u.getLogin());
      if (current != null && !current.equals(u)) {
        allUsersToKeep.remove(u.getLogin());
      }
    });
    sessions.getSessions().values().forEach(s -> {
      if (!s.expired() && allUsersToKeep.containsKey(s.getUser().getLogin())) {
        newsessions.put(s.getKey(), s);
        s.applyConfig(newAuth);
      }});
    challenges.forEach(c -> {if (!c.expired() && allUsersToKeep.containsKey(c.getUser().getLogin())) newchallenges.add(c);});

    authConfig = newAuth;
    sessions.setSessions(newsessions);
    challenges = newchallenges;
    changed = true;
  }

  private void initFromStore(ApiAuth cfg) {
    try (FileInputStream fis = new FileInputStream(sessionsStore)) {
      LOGGER.info("Loading state from " + sessionsStore.getAbsolutePath());
      sessions = JsonHelper.getJson().deserialize(Sessions.class, fis);
      sessions.getSessions().values().forEach(s -> s.applyConfig(cfg));
    } catch (FileNotFoundException e) {
      LOGGER.warning("No sessions saved.");
      sessions = new Sessions();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Could not load cached sessions file.", e);
      sessions = new Sessions();
    }
    saveThread = new Thread(() -> {save();}, "Sessions save");
    saveThread.start();
  }

  private void save() {
    while (saveThread == Thread.currentThread()) {
      try {
        synchronized (this) {
          this.wait(10000); // No need to save more than once every 10s
        }
        if (changed) {
          JsonWriter w = JsonHelper.getWriter();
          DslJson<Object> json = JsonHelper.getJson();
          synchronized (w) {
            try (OutputStream os = new FileOutputStream(sessionsStore)) {
              w.reset(os);
              json.serialize(w, sessions);
              w.flush();
              changed = false;
            } catch (Exception e) {
              LOGGER.log(Level.SEVERE, "Saving sessions.", e);
            }
          }
        }
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Saving sessions.", e);
      }
    }
    LOGGER.log(Level.INFO, "Stopping save thread.");
  }

  public NeedsAuthResponse performAuthentication(HttpServletRequest req) throws IOException {
    cleanAuthData();
    String user = req.getParameter(USER_FIELD);
    String pass = req.getParameter(PASS_FIELD);
    NeedsAuthResponse response = new NeedsAuthResponse();

    LOGGER.fine("Performing authentication");
    if (user!=null) {
      LOGGER.fine("User information present: " + user);
      if (pass != null) {
        LOGGER.fine("Password information present: " + pass);
        ChallengeResponse validatedCr = null;
        for (ChallengeResponse cr : challenges) {
          if (cr.getUser().getLogin().equals(user)) {
            String sha1 = HashTools.toSHA1(cr.getSaltValue() + cr.getUser().getPassword());
            if (sha1.equals(pass)) {
              validatedCr = cr;
              break;
            }
          }
        }
        if (validatedCr!=null) {
          LOGGER.fine("Valid CR found");
          String random = HashTools.getRandomSequence(8);
          addSession(new Session(random, validatedCr.getUser().clearPassword(), authConfig));
          addSession(new Session(random, validatedCr.getUser().clearPassword(), authConfig));
          response.setSessionToken(random);
        } else {
          LOGGER.fine("No valid CR found");
          response.setErrorMessage("Invalid user/password combination.");
        }
      } else {
        String random = HashTools.getRandomSequence(8);
        response.setSaltForPassword(random);
        User u = getUser(user);
        if (u!=null) addChallenge(new ChallengeResponse(random, u));
      }
    }

    return response;
  }

  private void addSession(Session session) {
    changed = true;
    Map<String, Session> newsessions = new HashMap<>();
    sessions.getSessions().values().forEach(s -> {
      if (!s.expired()) {
        newsessions.put(s.getKey(), s);
      }
    });
    newsessions.put(session.getKey(), session);
    sessions.setSessions(newsessions);
  }

  private User getUser(String user) {
    User[]users = authConfig.getUsers();
    if (users == null) return null;
    for (User u : users) {
      if (u.getLogin().equals(user)) return u;
    }
    return null;
  }

  private void addChallenge(ChallengeResponse cr) {
    List<ChallengeResponse> newchallenges = new ArrayList<>();
    challenges.stream().forEach(c -> {
      if (!c.expired()) newchallenges.add(c);
    });

    newchallenges.add(cr);
    challenges = newchallenges;
  }

  private void cleanAuthData() {
    Map<String, Session> newsessions = new HashMap<>();
    List<ChallengeResponse> newchallenges = new ArrayList<>();
    challenges.stream().forEach(c -> {
      if (!c.expired()) newchallenges.add(c);
    });

    sessions.getSessions().values().forEach(s -> {
      if (!s.expired()) {
        newsessions.put(s.getKey(), s);
      } else {
        changed = true;
      }
    });

    sessions.setSessions(newsessions);
    challenges = newchallenges;
  }

  private boolean checkRequestAuth(HttpServletRequest req) {
    String sid = req.getParameter(SID_FIELD);
    if (sid == null) return false;
    if (getUserFromToken(sid) == null) return false;
    return true;
  }

  private User getUserFromToken(String token) {
    Session s = sessions.getSession(token);
    if (s==null || s.expired()) return null;
    s.use();
    changed = true;
    return s.getUser();
  }


  public boolean isAuthOk(HttpServletRequest req) {
    if (authConfig!=null && authConfig.isAuth()) {
      if (!checkRequestAuth(req)) {
        return false;
      }
    }
    return true;
  }

  public void close() {
    saveThread = null;
    synchronized (this) {
      this.notify();
    }
  }
}
