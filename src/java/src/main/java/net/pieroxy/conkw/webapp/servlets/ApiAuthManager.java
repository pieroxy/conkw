package net.pieroxy.conkw.webapp.servlets;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.config.ApiAuth;
import net.pieroxy.conkw.config.Credentials;
import net.pieroxy.conkw.config.CredentialsStore;
import net.pieroxy.conkw.config.User;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.hashing.HashTools;
import net.pieroxy.conkw.webapp.model.NeedsAuthResponse;
import net.pieroxy.conkw.webapp.servlets.auth.ChallengeResponse;
import net.pieroxy.conkw.webapp.servlets.auth.Session;
import net.pieroxy.conkw.webapp.servlets.auth.Sessions;

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
  private CredentialsStore credentials;
  private Sessions sessions = new Sessions();
  private List<ChallengeResponse> challenges = new ArrayList<>();
  private File sessionsStore;
  private boolean changed;
  private Thread saveThread;

  public void applyConfig(ApiAuth auth, CredentialsStore creds, File dataDir) {
    ApiAuth newAuth = auth == null ? new ApiAuth() : auth;
    updateAuthConfig(newAuth, creds);
    sessionsStore = new File(dataDir, "sessions.json");
    if (saveThread==null) initFromStore(auth);
  }

  private void updateAuthConfig(ApiAuth newAuth, CredentialsStore credentials) {
    this.credentials = credentials;
    Map<String, Session> newsessions = new HashMap<>();
    List<ChallengeResponse> newchallenges = new ArrayList<>();
    Map<String, User> allUsersToKeep = new HashMap<>();
    if (authConfig!=null && authConfig.getUsers()!=null) {
      Arrays.stream(authConfig.getUsers()).forEach(u -> allUsersToKeep.put(getCredentials(u).getId(), u));
    }
    if (newAuth!=null && newAuth.getUsers()!=null) {
      Arrays.stream(newAuth.getUsers()).forEach(u -> {
        Credentials cred = getCredentials(u);
        if (cred!=null) {
          User current = allUsersToKeep.get(cred.getId());
          if (current != null && !current.equals(u)) {
            allUsersToKeep.remove(getCredentials(u).getId());
          }
        }
      });
    }
    sessions.getSessions().values().forEach(s -> {
      if (!s.expired() && allUsersToKeep.containsKey(getCredentials(s.getUser()).getId())) {
        newsessions.put(s.getKey(), s);
        s.applyConfig(newAuth);
      }});
    challenges.forEach(c -> {if (!c.expired() && allUsersToKeep.containsKey(getCredentials(c.getUser()).getId())) newchallenges.add(c);});

    authConfig = newAuth;
    sessions.setSessions(newsessions);
    challenges = newchallenges;
    changed = true;
  }

  private Credentials getCredentials(User u) {
    if (u.getCredentials()!=null) return u.getCredentials();
    return credentials.getStore().get(u.getCredentialsRef());

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

  public NeedsAuthResponse performAuthentication(String user, String pass) throws IOException {
    cleanAuthData();
    NeedsAuthResponse response = new NeedsAuthResponse();

    LOGGER.fine("Performing authentication");
    if (user!=null) {
      LOGGER.fine("User information present: " + user);
      if (pass != null) {
        LOGGER.fine("Password information present: " + pass);
        ChallengeResponse validatedCr = null;
        for (ChallengeResponse cr : challenges) {
          if (getCredentials(cr.getUser()).getId().equals(user)) {
            String sha1 = HashTools.toSHA1(cr.getSaltValue() + getCredentials(cr.getUser()).getSecret());
            if (sha1.equals(pass)) {
              validatedCr = cr;
              break;
            }
          }
        }
        if (validatedCr!=null) {
          LOGGER.fine("Valid CR found");
          String random = HashTools.getRandomSequence(8);
          addSession(new Session(random, validatedCr.getUser(), authConfig));
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
      Credentials creds = getCredentials(u);
      if (creds == null) {
        LOGGER.warning("User has no credentials: " + u);
      } else {
        if (creds.getId().equals(user)) return u;
      }
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

  private boolean checkRequestAuth(String sid) {
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


  public boolean isAuthOk(String sid) {
    if (authConfig!=null && authConfig.isAuth()) {
      if (!checkRequestAuth(sid)) {
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
