package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class CalendarEvents {
  double maxAge;
  Earnings earnings;
  //    "exDividendDate": {},
  //    "dividendDate": {}


  public double getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(double maxAge) {
    this.maxAge = maxAge;
  }

  public Earnings getEarnings() {
    return earnings;
  }

  public void setEarnings(Earnings earnings) {
    this.earnings = earnings;
  }
}
