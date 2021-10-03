package net.pieroxy.conkw.webapp.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.io.Closeable;
import java.util.*;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class Response implements Closeable {
  private Map<String, ResponseData> metrics;
  private long timestamp;
  private int responseJitter;
  private boolean needsAuthentication;
  private Collection<String> errors = new LinkedList<>();

  public Response() {
    this.timestamp = System.currentTimeMillis();
    metrics = HashMapPool.getInstance().borrow();
  }
  public Response(Response r, int jitter, String[]grabbers) {
    this.timestamp = r.timestamp;
    this.responseJitter = jitter;
    this.errors = r.errors;
    this.metrics = HashMapPool.getInstance().borrow(); // No handling of capacity yet: grabbers.length elements planned.
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

  public synchronized void addError(String e) {
    errors.add(e);
  }

  public synchronized void add(ResponseData response) {
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
    Map m = this.metrics;
    this.metrics = metrics;
    HashMapPool.getInstance().giveBack(m);
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

  @Override
  public void close() {
    HashMapPool.getInstance().giveBack(metrics);
    metrics = null;
  }
}

