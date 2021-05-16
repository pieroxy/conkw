package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class QuoteType {
  String exchange;
  String shortName;
  String longName;
  String exchangeTimezoneName;
  String exchangeTimezoneShortName;
  boolean isEsgPopulated;
  String gmtOffSetMilliseconds;
  String quoteType;
  String symbol;
  String messageBoardId;
  String market;

  public String getExchange() {
    return exchange;
  }

  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getLongName() {
    return longName;
  }

  public void setLongName(String longName) {
    this.longName = longName;
  }

  public String getExchangeTimezoneName() {
    return exchangeTimezoneName;
  }

  public void setExchangeTimezoneName(String exchangeTimezoneName) {
    this.exchangeTimezoneName = exchangeTimezoneName;
  }

  public String getExchangeTimezoneShortName() {
    return exchangeTimezoneShortName;
  }

  public void setExchangeTimezoneShortName(String exchangeTimezoneShortName) {
    this.exchangeTimezoneShortName = exchangeTimezoneShortName;
  }

  public boolean isEsgPopulated() {
    return isEsgPopulated;
  }

  public void setEsgPopulated(boolean esgPopulated) {
    isEsgPopulated = esgPopulated;
  }

  public String getGmtOffSetMilliseconds() {
    return gmtOffSetMilliseconds;
  }

  public void setGmtOffSetMilliseconds(String gmtOffSetMilliseconds) {
    this.gmtOffSetMilliseconds = gmtOffSetMilliseconds;
  }

  public String getQuoteType() {
    return quoteType;
  }

  public void setQuoteType(String quoteType) {
    this.quoteType = quoteType;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getMessageBoardId() {
    return messageBoardId;
  }

  public void setMessageBoardId(String messageBoardId) {
    this.messageBoardId = messageBoardId;
  }

  public String getMarket() {
    return market;
  }

  public void setMarket(String market) {
    this.market = market;
  }
}
