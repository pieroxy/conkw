package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class Price {
  String quoteSourceName;
  NumericValue regularMarketOpen;
  NumericValue averageDailyVolume3Month;
  String exchange;
  double regularMarketTime;
  //    "volume24Hr": {},
  NumericValue regularMarketDayHigh;
  String shortName;
  NumericValue averageDailyVolume10Day;
  String longName;
  NumericValue regularMarketChange;
  String currencySymbol;
  NumericValue regularMarketPreviousClose;
  double postMarketTime;
  //   "preMarketPrice": {},
  double exchangeDataDelayedBy;
  //   "toCurrency": null,
  NumericValue postMarketChange;
  NumericValue postMarketPrice;
  String exchangeName;
  //   "preMarketChange": {},
  //   "circulatingSupply": {},
  NumericValue regularMarketDayLow;
  NumericValue priceHint;
  String currency;
  NumericValue regularMarketPrice;
  NumericValue regularMarketVolume;
  //  "lastMarket": null,
  String regularMarketSource;
  //  "openInterest": {},
  String marketState;
  //  "underlyingSymbol": null,
  NumericValue marketCap;
  String quoteType;
  //  "volumeAllCurrencies": {},
  String postMarketSource;
  //  "strikePrice": {},
  String symbol;
  NumericValue postMarketChangePercent;
  String preMarketSource;
  double maxAge;
  // "fromCurrency": null,
  NumericValue regularMarketChangePercent;

  public String getQuoteSourceName() {
    return quoteSourceName;
  }

  public void setQuoteSourceName(String quoteSourceName) {
    this.quoteSourceName = quoteSourceName;
  }

  public NumericValue getRegularMarketOpen() {
    return regularMarketOpen;
  }

  public void setRegularMarketOpen(NumericValue regularMarketOpen) {
    this.regularMarketOpen = regularMarketOpen;
  }

  public NumericValue getAverageDailyVolume3Month() {
    return averageDailyVolume3Month;
  }

  public void setAverageDailyVolume3Month(NumericValue averageDailyVolume3Month) {
    this.averageDailyVolume3Month = averageDailyVolume3Month;
  }

  public String getExchange() {
    return exchange;
  }

  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  public double getRegularMarketTime() {
    return regularMarketTime;
  }

  public void setRegularMarketTime(double regularMarketTime) {
    this.regularMarketTime = regularMarketTime;
  }

  public NumericValue getRegularMarketDayHigh() {
    return regularMarketDayHigh;
  }

  public void setRegularMarketDayHigh(NumericValue regularMarketDayHigh) {
    this.regularMarketDayHigh = regularMarketDayHigh;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public NumericValue getAverageDailyVolume10Day() {
    return averageDailyVolume10Day;
  }

  public void setAverageDailyVolume10Day(NumericValue averageDailyVolume10Day) {
    this.averageDailyVolume10Day = averageDailyVolume10Day;
  }

  public String getLongName() {
    return longName;
  }

  public void setLongName(String longName) {
    this.longName = longName;
  }

  public NumericValue getRegularMarketChange() {
    return regularMarketChange;
  }

  public void setRegularMarketChange(NumericValue regularMarketChange) {
    this.regularMarketChange = regularMarketChange;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }

  public NumericValue getRegularMarketPreviousClose() {
    return regularMarketPreviousClose;
  }

  public void setRegularMarketPreviousClose(NumericValue regularMarketPreviousClose) {
    this.regularMarketPreviousClose = regularMarketPreviousClose;
  }

  public double getPostMarketTime() {
    return postMarketTime;
  }

  public void setPostMarketTime(double postMarketTime) {
    this.postMarketTime = postMarketTime;
  }

  public double getExchangeDataDelayedBy() {
    return exchangeDataDelayedBy;
  }

  public void setExchangeDataDelayedBy(double exchangeDataDelayedBy) {
    this.exchangeDataDelayedBy = exchangeDataDelayedBy;
  }

  public NumericValue getPostMarketChange() {
    return postMarketChange;
  }

  public void setPostMarketChange(NumericValue postMarketChange) {
    this.postMarketChange = postMarketChange;
  }

  public NumericValue getPostMarketPrice() {
    return postMarketPrice;
  }

  public void setPostMarketPrice(NumericValue postMarketPrice) {
    this.postMarketPrice = postMarketPrice;
  }

  public String getExchangeName() {
    return exchangeName;
  }

  public void setExchangeName(String exchangeName) {
    this.exchangeName = exchangeName;
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

  public NumericValue getRegularMarketPrice() {
    return regularMarketPrice;
  }

  public void setRegularMarketPrice(NumericValue regularMarketPrice) {
    this.regularMarketPrice = regularMarketPrice;
  }

  public NumericValue getRegularMarketVolume() {
    return regularMarketVolume;
  }

  public void setRegularMarketVolume(NumericValue regularMarketVolume) {
    this.regularMarketVolume = regularMarketVolume;
  }

  public String getRegularMarketSource() {
    return regularMarketSource;
  }

  public void setRegularMarketSource(String regularMarketSource) {
    this.regularMarketSource = regularMarketSource;
  }

  public String getMarketState() {
    return marketState;
  }

  public void setMarketState(String marketState) {
    this.marketState = marketState;
  }

  public NumericValue getMarketCap() {
    return marketCap;
  }

  public void setMarketCap(NumericValue marketCap) {
    this.marketCap = marketCap;
  }

  public String getQuoteType() {
    return quoteType;
  }

  public void setQuoteType(String quoteType) {
    this.quoteType = quoteType;
  }

  public String getPostMarketSource() {
    return postMarketSource;
  }

  public void setPostMarketSource(String postMarketSource) {
    this.postMarketSource = postMarketSource;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public NumericValue getPostMarketChangePercent() {
    return postMarketChangePercent;
  }

  public void setPostMarketChangePercent(NumericValue postMarketChangePercent) {
    this.postMarketChangePercent = postMarketChangePercent;
  }

  public String getPreMarketSource() {
    return preMarketSource;
  }

  public void setPreMarketSource(String preMarketSource) {
    this.preMarketSource = preMarketSource;
  }

  public double getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(double maxAge) {
    this.maxAge = maxAge;
  }

  public NumericValue getRegularMarketChangePercent() {
    return regularMarketChangePercent;
  }

  public void setRegularMarketChangePercent(NumericValue regularMarketChangePercent) {
    this.regularMarketChangePercent = regularMarketChangePercent;
  }
}
