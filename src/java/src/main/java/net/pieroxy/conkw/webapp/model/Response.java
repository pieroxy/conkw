package net.pieroxy.conkw.webapp.model;

import com.dslplatform.json.CompiledJson;

import java.util.*;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class Response {
  private Map<String, ResponseData> metrics = new HashMap<>();
  private long timestamp;
  private int responseJitter;
  private boolean needsAuthentication;
  private Collection<String> errors = new LinkedList<>();

  public Response() {
    this.timestamp = System.currentTimeMillis();
  }
  public Response(Response r, int jitter, String[]grabbers) {
    this.timestamp = r.timestamp;
    this.responseJitter = jitter;
    this.errors = r.errors;
    this.metrics = new HashMap<>(grabbers.length*2);
    for (String s : grabbers) {
      ResponseData rd = r.metrics.get(s);
      if (rd!=null) {
        this.metrics.put(s, rd);
      }
    }
  }

  public static Response getError(String message) {
    Response res = new Response();
    res.addError(message);
    return res;
  }

  public void addError(String e) {
    errors.add(e);
  }

  public void add(ResponseData response) {
    if (response == null) return;
    if (response.getName() == null) {
      throw new NullPointerException("ResponseData has no name.");
    }
    metrics.put(response.getName(), response);
    errors.addAll(response.getErrors());
  }


  public Collection<String> getErrors() {
    return errors;
  }

  public void setErrors(Collection<String> errors) {
    this.errors = errors;
  }

  public Map<String, ResponseData> getMetrics() {
    return metrics;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setMetrics(Map<String, ResponseData> metrics) {
    this.metrics = metrics;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public int getResponseJitter() {
    return responseJitter;
  }

  public void setResponseJitter(int responseJitter) {
    this.responseJitter = responseJitter;
  }

  public boolean isNeedsAuthentication() {
    return needsAuthentication;
  }

  public void setNeedsAuthentication(boolean needsAuthentication) {
    this.needsAuthentication = needsAuthentication;
  }
}

