package net.pieroxy.conkw.model;

import com.dslplatform.json.CompiledJson;

import java.util.*;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class Response {
  private Map<String, ResponseData> metrics = new HashMap<>();
  private long timestamp;
  private int responseJitter;
  private Collection<String> errors = new LinkedList<>();

  public Response() {
    this.timestamp = System.currentTimeMillis();
  }

  public void addError(String e) {
    errors.add(e);
  }

  public void add(ResponseData response) {
    if (response == null) return;
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
}

