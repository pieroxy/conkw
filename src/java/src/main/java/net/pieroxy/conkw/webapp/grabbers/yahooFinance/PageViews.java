package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class PageViews {
  String shortTermTrend;
  String midTermTrend;
  String longTermTrend;
  double maxAge;

  public String getShortTermTrend() {
    return shortTermTrend;
  }

  public void setShortTermTrend(String shortTermTrend) {
    this.shortTermTrend = shortTermTrend;
  }

  public String getMidTermTrend() {
    return midTermTrend;
  }

  public void setMidTermTrend(String midTermTrend) {
    this.midTermTrend = midTermTrend;
  }

  public String getLongTermTrend() {
    return longTermTrend;
  }

  public void setLongTermTrend(String longTermTrend) {
    this.longTermTrend = longTermTrend;
  }

  public double getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(double maxAge) {
    this.maxAge = maxAge;
  }
}
