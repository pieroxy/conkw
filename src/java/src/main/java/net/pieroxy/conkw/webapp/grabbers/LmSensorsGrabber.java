package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.collectors.SimpleTransientCollector;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.utils.ExternalBinaryRunner;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class LmSensorsGrabber extends AsyncGrabber<SimpleCollector, LmSensorsGrabber.LmSensorsGrabberConfig> {
  static final String NAME = "lmsensors";

  ExternalBinaryRunner runner;
  private Set<Pattern> include;
  private Map<String, Boolean> extractCache = new HashMap<>();

  @Override
  public boolean changed(SimpleCollector c) {
    return true;
  }

  public SimpleCollector getDefaultCollector() {
    return new SimpleTransientCollector(this, DEFAULT_CONFIG_KEY);
  }

  @Override
  public void grabSync(SimpleCollector c) {
    if (runner == null) runner = new ExternalBinaryRunner(new String[] {"sensors", "-u"});
    runner.exec();
    parse(c, runner.getBuffer());
  }

  private void parse(SimpleCollector c, byte[] buffer) {
    BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer)));
    String line = null;
    String chipid = null;
    String chipname = null;
    String chipsensor = null;

    Map<String, StringBuilder> categories = HashMapPool.getInstance().borrow(HashMapPool.getUniqueCode(this.getClass().getName(), getName(), "categories"));

    while (true) {
      try {
        line = br.readLine();
        if (line == null) break;

        if (chipid == null) {
          chipid = line;
        } else if (chipname == null) {
          chipname = line;
        } else if (line.equals("")) {
          chipid = chipname = chipsensor = null;
        } else if (line.startsWith("  ")) {
          String[]parts = line.split(":");
          String mname = parts[0].trim();
          if (shouldExtractMetric(mname))
            addMetric(c, categories, chipid, chipsensor, mname, Double.parseDouble(parts[1].trim()));
        } else {
          chipsensor = line.substring(0, line.length()-1);
        }
      } catch (IOException e) {
        log(Level.SEVERE, "", e);
        c.addError(e.getMessage());
        break;
      }
    }
    for (Map.Entry<String, StringBuilder> e : categories.entrySet()) {
      c.collect(e.getKey(), e.getValue().toString());
    }
    HashMapPool.getInstance().giveBack(categories);
  }

  private void addMetric(SimpleCollector c, Map<String, StringBuilder> categories, String prefix1, String prefix2, String metricName, double value) {
    String fullName = (prefix1 + "_" + prefix2 + "_" + metricName).replaceAll(",", ".");
    computeAutoMaxMinAbsolute(c, fullName, value);
    c.collect("shortname_" + fullName, prefix2);
    String cat = getCategory(metricName);
    StringBuilder sbcat = categories.get(cat);
    if (sbcat == null) {
      sbcat = new StringBuilder();
      categories.put(cat, sbcat);
    }
    if (sbcat.length()>0) sbcat.append(",");
    sbcat.append(fullName);
  }

  private String getCategory(String metricName) {
    int up = metricName.indexOf("_");
    if (up<1) return "misc";
    StringBuilder sb = new StringBuilder(metricName.length());
    for (int i=0 ; i<up ; i++) {
      char c = metricName.charAt(i);
      if (!Character.isDigit(c)) sb.append(c);
    }
    sb.append(metricName.substring(up));
    return sb.toString();
  }

  @Override
  public String getDefaultName() {
    return NAME;
  }

  private boolean shouldExtractMetric(String f) {
    if (include == null) return true;
    Boolean cacheValue = extractCache.get(f);
    if (cacheValue!=null) return cacheValue;

    for (Pattern p : include) {
      if (p.matcher(f).matches()) {
        extractCache.put(f, true);
        if (canLogFiner()) log(Level.FINER, p + " matches " + f);
        return true;
      } else {
        if (canLogFiner()) log(Level.FINER, p + " !matches " + f);
      }
    }
    extractCache.put(f, false);
    return false;
  }

  @Override
  protected void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
    String includeString = config.get("include");

    if (includeString != null) {
      include = new HashSet<>();
      for (String s : includeString.split(",")) {
        include.add(Pattern.compile(s));
      }
    }
  }

  public static class LmSensorsGrabberConfig {

  }
}
