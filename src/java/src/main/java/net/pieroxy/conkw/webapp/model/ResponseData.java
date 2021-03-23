package net.pieroxy.conkw.webapp.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.grabbers.Grabber;

import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class ResponseData {
  private final static Logger LOGGER = Logger.getLogger(ResponseData.class.getName());

  public static final String TIMESTAMP_PREFIX="ts:";

  private long timestamp;
  private long elapsedToGrab;
  private Collection<String> errors=new LinkedList<>();
  private String name,extractor;
  private Map<String, Double> num = new HashMap<>();
  private Map<String, String> str = new HashMap<>();
  private Set<String> extracted   = new HashSet<>();

  public ResponseData() {
  }

  public ResponseData(Grabber grabber, long timestamp) {
    this.timestamp = timestamp;
    if (grabber!=null) {
      this.name = grabber.getName();
      this.extractor = grabber.getClass().getSimpleName();
    }
  }

  public void addExtracted(String e) {
    extracted.add(e);
  }

  public void addError(String e) {
    errors.add(e);
  }

  public void addMetric(String name, Double value) {
    num.put(name, value);
  }

  public void addMetric(String name, String value) {
    str.put(name, value);
  }

  public void addMetric(String name, int value) {
    addMetric(name, (double)value);
  }
  public void addMetric(String name, long value) {
    addMetric(name, (double)value);
  }

  public synchronized void atomicAddMetricWithTimestamp(String name, Double value) {
    String tsName = TIMESTAMP_PREFIX + name;
    if (!num.containsKey(name) || !num.containsKey(tsName)) {
      // Change the map structurally, needs to do it atomically.
      Map<String, Double> nnum = new HashMap<>();
      nnum.putAll(num);
      nnum.put(name, value);
      nnum.put(tsName, (double)System.currentTimeMillis());
      num = nnum;
    } else {
      num.put(name, value);
      num.put(tsName, (double)System.currentTimeMillis());
    }
  }

  public synchronized void atomicAddMetricWithTimestamp(String name, String value) {
    String tsName = TIMESTAMP_PREFIX + name;
    if (!str.containsKey(name) || !num.containsKey(tsName)) {
      // Change the map structurally, needs to do it atomically.
      Map<String, String> nstr = new HashMap<>();
      nstr.putAll(str);
      nstr.put(name, value);
      str = nstr;
      Map<String, Double> nnum = new HashMap<>();
      nnum.putAll(num);
      nnum.put(tsName, (double)System.currentTimeMillis());
      num = nnum;
    } else {
      str.put(name, value);
      num.put(tsName, (double)System.currentTimeMillis());
    }
  }

  /**
   *
   * @param timespan
   * @return true if something was deleted.
   */
  public synchronized boolean purgeDataOlderThan(Duration timespan) {
    long now = System.currentTimeMillis();
    boolean changed = false;
    Map<String, String> nstr = new HashMap<>();
    nstr.putAll(str);
    Map<String, Double> nnum = new HashMap<>();
    nnum.putAll(num);

    HashSet<String> keys = new HashSet<>();
    keys.addAll(nstr.keySet());
    for (String gstrKey : keys) {
      String tsKey = TIMESTAMP_PREFIX + gstrKey;
      Double d = nnum.get(tsKey);
      if (d!=null) {
        if (now - d > timespan.toMillis()) {
          nstr.remove(gstrKey);
          nnum.remove(tsKey);
          changed = true;
        }
      }
    }

    keys = new HashSet<>();
    keys.addAll(nnum.keySet());
    for (String gstrKey : keys) {
      if (gstrKey.startsWith(TIMESTAMP_PREFIX)) continue;
      String tsKey = TIMESTAMP_PREFIX + gstrKey;
      Double d = nnum.get(tsKey);
      if (d!=null) {
        if (now - d > timespan.toMillis()) {
          nnum.remove(gstrKey);
          nnum.remove(tsKey);
          changed = true;
          LOGGER.info("PURGED " + gstrKey);
        }
      }
    }
    if (changed) {
      num = nnum;
      str = nstr;
    }
    return changed;
  }

  public Double getNumMetric(String name) {
    return num.get(name);
  }

  public String getStrMetric(String name) {
    return str.get(name);
  }

  public String getName() {
    return name;
  }

  public Collection<String> getErrors() {
    return errors;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public long getElapsedToGrab() {
    return elapsedToGrab;
  }

  public void setElapsedToGrab(long elapsedToGrab) {
    this.elapsedToGrab = elapsedToGrab;
  }

  public Map<String, Double> getNum() {
    return num;
  }

  public Map<String, String> getStr() {
    return str;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNum(Map<String, Double> num) {
    this.num = num;
  }

  public void setStr(Map<String, String> str) {
    this.str = str;
  }

  public Set<String> getExtracted() {
    return extracted;
  }

  public void setExtracted(Set<String> extracted) {
    this.extracted = extracted;
  }

  public String getExtractor() {
    return extractor;
  }

  public void setExtractor(String extractor) {
    this.extractor = extractor;
  }

  public void setErrors(Collection<String> errors) {
    this.errors = errors;
  }
}
