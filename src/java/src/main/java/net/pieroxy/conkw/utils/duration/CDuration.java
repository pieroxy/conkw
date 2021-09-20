package net.pieroxy.conkw.utils.duration;

public final class CDuration {
  private final long seconds;
  public static final CDuration ZERO = CDurationParser.parse("0m");
  public static final CDuration ONE_MINUTE = CDurationParser.parse("1m");
  public static final CDuration ONE_HOUR = CDurationParser.parse("1h");
  public static final CDuration FIVE_SECONDS = CDurationParser.parse("5s");

  public CDuration(long numberOfSeconds) {
    this.seconds = numberOfSeconds;
  }

  public boolean isExpired(long sinceInMilliseconds, long nowInMilliseconds) {
    return seconds<=0 || (nowInMilliseconds-sinceInMilliseconds)/1000 > seconds;
  }

  public long asSeconds() {
    return seconds;
  }
  public long asMilliseconds() {
    return seconds*1000;
  }
  public long asMinutes() {
    return seconds/60;
  }
  public long asHours() {
    return seconds/3600;
  }
  public long asDays() {
    return seconds/86400;
  }
  public long asYears() {
    return seconds/31536000;
  }
}
