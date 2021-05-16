package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

public class Earnings {
  NumericValue[]earningsDate;
  NumericValue earningsAverage;
  NumericValue earningsLow;
  NumericValue earningsHigh;
  NumericValue revenueAverage;
  NumericValue revenueLow;
  NumericValue revenueHigh;


  public NumericValue[] getEarningsDate() {
    return earningsDate;
  }

  public void setEarningsDate(NumericValue[] earningsDate) {
    this.earningsDate = earningsDate;
  }

  public NumericValue getEarningsAverage() {
    return earningsAverage;
  }

  public void setEarningsAverage(NumericValue earningsAverage) {
    this.earningsAverage = earningsAverage;
  }

  public NumericValue getEarningsLow() {
    return earningsLow;
  }

  public void setEarningsLow(NumericValue earningsLow) {
    this.earningsLow = earningsLow;
  }

  public NumericValue getEarningsHigh() {
    return earningsHigh;
  }

  public void setEarningsHigh(NumericValue earningsHigh) {
    this.earningsHigh = earningsHigh;
  }

  public NumericValue getRevenueAverage() {
    return revenueAverage;
  }

  public void setRevenueAverage(NumericValue revenueAverage) {
    this.revenueAverage = revenueAverage;
  }

  public NumericValue getRevenueLow() {
    return revenueLow;
  }

  public void setRevenueLow(NumericValue revenueLow) {
    this.revenueLow = revenueLow;
  }

  public NumericValue getRevenueHigh() {
    return revenueHigh;
  }

  public void setRevenueHigh(NumericValue revenueHigh) {
    this.revenueHigh = revenueHigh;
  }
}
