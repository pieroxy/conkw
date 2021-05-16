package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class NumericValue {
  double raw;
  String fmt;
  String longFmt;

  public double getRaw() {
    return raw;
  }

  public void setRaw(double raw) {
    this.raw = raw;
  }

  public String getFmt() {
    return fmt;
  }

  public void setFmt(String fmt) {
    this.fmt = fmt;
  }

  public String getLongFmt() {
    return longFmt;
  }

  public void setLongFmt(String longFmt) {
    this.longFmt = longFmt;
  }
}
