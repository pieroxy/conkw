package net.pieroxy.conkw.webapp.servlets;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.config.ApiAuth;
import net.pieroxy.conkw.config.User;
import net.pieroxy.conkw.utils.HashTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.model.NeedsAuthResponse;
import net.pieroxy.conkw.webapp.model.Response;
import net.pieroxy.conkw.webapp.servlets.auth.ChallengeResponse;
import net.pieroxy.conkw.webapp.servlets.auth.Session;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Api extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(Api.class.getName());
  private final static String USER_FIELD = "__U__";
  private final static String PASS_FIELD = "__P__";
  private final static String SID_FIELD = "__SID__";

  private static ApiManager api;
  private static ApiAuth authConfig;
  private static Map<String, Session> sessions = new HashMap<>();
  private static List<ChallengeResponse> challenges = new ArrayList<>();

  public static void setContext(ApiManager api, ApiAuth auth) {
    Api.api = api;
    Api.authConfig = auth == null ? new ApiAuth() : auth;
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (authConfig.isAuth()) {
      if (!checkRequestAuth(req)) {
        LOGGER.fine("Auth needed");
        performAuthentication(req, resp);
        return;
      }
    }

    long now = System.currentTimeMillis();
    final String action = req.getParameter("grabberAction");
    if (action!=null) {
      String res = api.notifyGrabberAction(action, req.getParameterMap());
      try {
        resp.getOutputStream().write(res.getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        LOGGER.log(Level.FINE, "Writing response", e);
      }
    } else {
      String grabbers = req.getParameter("grabbers");
      if (grabbers == null) {
        writeResponse(resp, Response.getError("Grabbers were not specified"));
      } else {
        Response r = api.buildResponse(now, grabbers);
        writeResponse(resp, r);
      }
    }
  }

  private void performAuthentication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
          addSession(new Session(random, validatedCr.getUser()));
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

    writeResponse(resp, response);
  }

  private void addSession(Session session) {
    Map<String, Session> newsessions = new HashMap<>();
    sessions.values().forEach(s -> {
      if (!s.expired()) {
        newsessions.put(s.getKey(), s);
      }
    });
    newsessions.put(session.getKey(), session);
    sessions = newsessions;
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

    sessions.values().forEach(s -> {
      if (!s.expired()) {
        newsessions.put(s.getKey(), s);
      }
    });

    sessions = newsessions;
    challenges = newchallenges;
  }

  private boolean checkRequestAuth(HttpServletRequest req) {
    String sid = req.getParameter(SID_FIELD);
    if (sid == null) return false;
    if (getUserFromToken(sid) == null) return false;
    return true;
  }

  private User getUserFromToken(String token) {
    Session s = sessions.get(token);
    if (s==null || s.expired()) return null;
    s.use();
    return s.getUser();
  }

  private void writeResponse(HttpServletResponse resp, Object r) throws IOException {
    resp.setContentType("application/json;charset=utf-8");
    DslJson<Object> json = JsonHelper.getJson();
    JsonWriter w = JsonHelper.getWriter();
    synchronized (w) {
      w.reset(resp.getOutputStream());
      json.serialize(w, r);
      w.flush();
    }
  }
}

