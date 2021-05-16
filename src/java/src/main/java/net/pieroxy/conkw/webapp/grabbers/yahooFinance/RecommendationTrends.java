package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class RecommendationTrends {
  RecommendationTrend []trend;
  double maxAge;

  public double getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(double maxAge) {
    this.maxAge = maxAge;
  }

  public RecommendationTrend[] getTrend() {
    return trend;
  }

  public void setTrend(RecommendationTrend[] trend) {
    this.trend = trend;
  }
}
