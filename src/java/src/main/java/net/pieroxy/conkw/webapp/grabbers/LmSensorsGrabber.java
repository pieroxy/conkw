package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.utils.ExternalBinaryRunner;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LmSensorsGrabber extends AsyncGrabber {
  static final String NAME = "lmsensors";


  @Override
  public boolean changed() {
    return true;
  }

  @Override
  public ResponseData grabSync() {
    ExternalBinaryRunner runner = new ExternalBinaryRunner(new String[] {"sensors", "-u"});
    runner.exec();
    return parse(runner.getBuffer());
  }

  private ResponseData parse(byte[] buffer) {
    ResponseData res = new ResponseData(this, System.currentTimeMillis());
    BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer)));
    String line = null;
    String chipid = null;
    String chipname = null;
    String chipsensor = null;

    Map<String, StringBuilder> categories = new HashMap<>();

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
          addMetric(res, categories, chipid, chipsensor, parts[0].trim(), Double.parseDouble(parts[1].trim()));
        } else {
          chipsensor = line.substring(0, line.length()-1);
        }
      } catch (IOException e) {
        log(Level.SEVERE, "", e);
        res.addError(e.getMessage());
        break;
      }
    }
    for (Map.Entry<String, StringBuilder> e : categories.entrySet()) {
      res.addMetric(e.getKey(), e.getValue().toString());
    }
    return res;
  }

  private void addMetric(ResponseData res, Map<String, StringBuilder> categories, String prefix1, String prefix2, String metricName, double value) {
    String fullName = (prefix1 + "_" + prefix2 + "_" + metricName).replaceAll(",", ".");
    computeAutoMaxMinAbsolute(res, fullName, value);
    res.addMetric("shortname_" + fullName, prefix2);
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

  @Override
  protected void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {

  }
}
