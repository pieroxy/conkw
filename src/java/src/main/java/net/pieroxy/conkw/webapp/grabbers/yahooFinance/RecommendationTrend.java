package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class RecommendationTrend {
  String period;
  double strongBuy;
  double buy;
  double hold;
  double sell;
  double strongSell;

  public String getPeriod() {
    return period;
  }

  public void setPeriod(String period) {
    this.period = period;
  }

  public double getStrongBuy() {
    return strongBuy;
  }

  public void setStrongBuy(double strongBuy) {
    this.strongBuy = strongBuy;
  }

  public double getBuy() {
    return buy;
  }

  public void setBuy(double buy) {
    this.buy = buy;
  }

  public double getHold() {
    return hold;
  }

  public void setHold(double hold) {
    this.hold = hold;
  }

  public double getSell() {
    return sell;
  }

  public void setSell(double sell) {
    this.sell = sell;
  }

  public double getStrongSell() {
    return strongSell;
  }

  public void setStrongSell(double strongSell) {
    this.strongSell = strongSell;
  }
}
