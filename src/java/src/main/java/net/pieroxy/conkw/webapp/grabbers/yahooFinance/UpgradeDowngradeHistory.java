package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class UpgradeDowngradeHistory {
  Grade[]history;
  double maxAge;

  public Grade[] getHistory() {
    return history;
  }

  public void setHistory(Grade[] history) {
    this.history = history;
  }

  public double getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(double maxAge) {
    this.maxAge = maxAge;
  }
}
