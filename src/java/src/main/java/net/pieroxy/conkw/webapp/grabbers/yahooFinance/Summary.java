package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class Summary {
  PageViews pageViews;
  UpgradeDowngradeHistory upgradeDowngradeHistory;
  // esgScores Dunno what that is
  String symbol;
  SummaryDetail summaryDetail;
  CalendarEvents calendarEvents;
  // Object institutionOwnership; No need for daily reporting
  QuoteType quoteType;
  // financialData No need for daily reporting
  // majorHoldersBreakdown
  // netSharePurchaseActivity
  // insiderHolders Who sold and was granted what
  // insiderTransactions kind of the same
  // fundOwnership
  Price price;
  // earnings
  // majorDirectHolders
  // financialsTemplate
  RecommendationTrends recommendationTrend;
  // summaryProfile
  // details
  // defaultKeyStatistics Despite the name, I don't think there's anything of value here.


  public PageViews getPageViews() {
    return pageViews;
  }

  public void setPageViews(PageViews pageViews) {
    this.pageViews = pageViews;
  }

  public UpgradeDowngradeHistory getUpgradeDowngradeHistory() {
    return upgradeDowngradeHistory;
  }

  public void setUpgradeDowngradeHistory(UpgradeDowngradeHistory upgradeDowngradeHistory) {
    this.upgradeDowngradeHistory = upgradeDowngradeHistory;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public SummaryDetail getSummaryDetail() {
    return summaryDetail;
  }

  public void setSummaryDetail(SummaryDetail summaryDetail) {
    this.summaryDetail = summaryDetail;
  }

  public CalendarEvents getCalendarEvents() {
    return calendarEvents;
  }

  public void setCalendarEvents(CalendarEvents calendarEvents) {
    this.calendarEvents = calendarEvents;
  }

  public QuoteType getQuoteType() {
    return quoteType;
  }

  public void setQuoteType(QuoteType quoteType) {
    this.quoteType = quoteType;
  }

  public Price getPrice() {
    return price;
  }

  public void setPrice(Price price) {
    this.price = price;
  }

  public RecommendationTrends getRecommendationTrend() {
    return recommendationTrend;
  }

  public void setRecommendationTrend(RecommendationTrends recommendationTrend) {
    this.recommendationTrend = recommendationTrend;
  }
}
