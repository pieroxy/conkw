package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class SummaryDetail {
  NumericValue previousClose;
  NumericValue regularMarketOpen;
  NumericValue twoHundredDayAverage;
  //    "trailingAnnualDividendYield": {},
  NumericValue payoutRatio;
  //    "volume24Hr": {},
  NumericValue regularMarketDayHigh;
  //    "navPrice": {},
  NumericValue averageDailyVolume10Day;
  //    "totalAssets": {},
  NumericValue regularMarketPreviousClose;
  NumericValue fiftyDayAverage;
  //    "trailingAnnualDividendRate": {},
  NumericValue open;
  //    "toCurrency": null,
  NumericValue averageVolume10days;
  //    "expireDate": {},
  //    "yield": {},
  //    "algorithm": null,
  //    "dividendRate": {},
  //    "exDividendDate": {},
  NumericValue beta;
  //    "circulatingSupply": {},
  //    "startDate": {},
  NumericValue regularMarketDayLow;
  NumericValue priceHint;
  String currency;
  NumericValue trailingPE;
  NumericValue regularMarketVolume;
  //    "lastMarket": null,
  //    "maxSupply": {},
  //    "openInterest": {},
  NumericValue marketCap;
  //    "volumeAllCurrencies": {},
  //    "strikePrice": {},
  NumericValue averageVolume;
  NumericValue priceToSalesTrailing12Months;
  NumericValue dayLow;
  NumericValue ask;
  //    "ytdReturn": {},
  NumericValue askSize;
  NumericValue volume;
  NumericValue fiftyTwoWeekHigh;
  NumericValue forwardPE;
  double maxAge;
  //    "fromCurrency": null,
  //    "fiveYearAvgDividendYield": {},
  NumericValue fiftyTwoWeekLow;
  NumericValue bid;
  boolean tradeable;
  //    "dividendYield": {},
  NumericValue bidSize;
  NumericValue dayHigh;

  public NumericValue getPreviousClose() {
    return previousClose;
  }

  public void setPreviousClose(NumericValue previousClose) {
    this.previousClose = previousClose;
  }

  public NumericValue getRegularMarketOpen() {
    return regularMarketOpen;
  }

  public void setRegularMarketOpen(NumericValue regularMarketOpen) {
    this.regularMarketOpen = regularMarketOpen;
  }

  public NumericValue getTwoHundredDayAverage() {
    return twoHundredDayAverage;
  }

  public void setTwoHundredDayAverage(NumericValue twoHundredDayAverage) {
    this.twoHundredDayAverage = twoHundredDayAverage;
  }

  public NumericValue getPayoutRatio() {
    return payoutRatio;
  }

  public void setPayoutRatio(NumericValue payoutRatio) {
    this.payoutRatio = payoutRatio;
  }

  public NumericValue getRegularMarketDayHigh() {
    return regularMarketDayHigh;
  }

  public void setRegularMarketDayHigh(NumericValue regularMarketDayHigh) {
    this.regularMarketDayHigh = regularMarketDayHigh;
  }

  public NumericValue getAverageDailyVolume10Day() {
    return averageDailyVolume10Day;
  }

  public void setAverageDailyVolume10Day(NumericValue averageDailyVolume10Day) {
    this.averageDailyVolume10Day = averageDailyVolume10Day;
  }

  public NumericValue getRegularMarketPreviousClose() {
    return regularMarketPreviousClose;
  }

  public void setRegularMarketPreviousClose(NumericValue regularMarketPreviousClose) {
    this.regularMarketPreviousClose = regularMarketPreviousClose;
  }

  public NumericValue getFiftyDayAverage() {
    return fiftyDayAverage;
  }

  public void setFiftyDayAverage(NumericValue fiftyDayAverage) {
    this.fiftyDayAverage = fiftyDayAverage;
  }

  public NumericValue getOpen() {
    return open;
  }

  public void setOpen(NumericValue open) {
    this.open = open;
  }

  public NumericValue getAverageVolume10days() {
    return averageVolume10days;
  }

  public void setAverageVolume10days(NumericValue averageVolume10days) {
    this.averageVolume10days = averageVolume10days;
  }

  public NumericValue getBeta() {
    return beta;
  }

  public void setBeta(NumericValue beta) {
    this.beta = beta;
  }

  public NumericValue getRegularMarketDayLow() {
    return regularMarketDayLow;
  }

  public void setRegularMarketDayLow(NumericValue regularMarketDayLow) {
    this.regularMarketDayLow = regularMarketDayLow;
  }

  public NumericValue getPriceHint() {
    return priceHint;
  }

  public void setPriceHint(NumericValue priceHint) {
    this.priceHint = priceHint;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public NumericValue getTrailingPE() {
    return trailingPE;
  }

  public void setTrailingPE(NumericValue trailingPE) {
    this.trailingPE = trailingPE;
  }

  public NumericValue getRegularMarketVolume() {
    return regularMarketVolume;
  }

  public void setRegularMarketVolume(NumericValue regularMarketVolume) {
    this.regularMarketVolume = regularMarketVolume;
  }

  public NumericValue getMarketCap() {
    return marketCap;
  }

  public void setMarketCap(NumericValue marketCap) {
    this.marketCap = marketCap;
  }

  public NumericValue getAverageVolume() {
    return averageVolume;
  }

  public void setAverageVolume(NumericValue averageVolume) {
    this.averageVolume = averageVolume;
  }

  public NumericValue getPriceToSalesTrailing12Months() {
    return priceToSalesTrailing12Months;
  }

  public void setPriceToSalesTrailing12Months(NumericValue priceToSalesTrailing12Months) {
    this.priceToSalesTrailing12Months = priceToSalesTrailing12Months;
  }

  public NumericValue getDayLow() {
    return dayLow;
  }

  public void setDayLow(NumericValue dayLow) {
    this.dayLow = dayLow;
  }

  public NumericValue getAsk() {
    return ask;
  }

  public void setAsk(NumericValue ask) {
    this.ask = ask;
  }

  public NumericValue getAskSize() {
    return askSize;
  }

  public void setAskSize(NumericValue askSize) {
    this.askSize = askSize;
  }

  public NumericValue getVolume() {
    return volume;
  }

  public void setVolume(NumericValue volume) {
    this.volume = volume;
  }

  public NumericValue getFiftyTwoWeekHigh() {
    return fiftyTwoWeekHigh;
  }

  public void setFiftyTwoWeekHigh(NumericValue fiftyTwoWeekHigh) {
    this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
  }

  public NumericValue getForwardPE() {
    return forwardPE;
  }

  public void setForwardPE(NumericValue forwardPE) {
    this.forwardPE = forwardPE;
  }

  public double getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(double maxAge) {
    this.maxAge = maxAge;
  }

  public NumericValue getFiftyTwoWeekLow() {
    return fiftyTwoWeekLow;
  }

  public void setFiftyTwoWeekLow(NumericValue fiftyTwoWeekLow) {
    this.fiftyTwoWeekLow = fiftyTwoWeekLow;
  }

  public NumericValue getBid() {
    return bid;
  }

  public void setBid(NumericValue bid) {
    this.bid = bid;
  }

  public boolean isTradeable() {
    return tradeable;
  }

  public void setTradeable(boolean tradeable) {
    this.tradeable = tradeable;
  }

  public NumericValue getBidSize() {
    return bidSize;
  }

  public void setBidSize(NumericValue bidSize) {
    this.bidSize = bidSize;
  }

  public NumericValue getDayHigh() {
    return dayHigh;
  }

  public void setDayHigh(NumericValue dayHigh) {
    this.dayHigh = dayHigh;
  }
}
