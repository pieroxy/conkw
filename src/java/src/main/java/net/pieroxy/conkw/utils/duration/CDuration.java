package net.pieroxy.conkw.utils.duration;

public final class CDuration {
  private final long milliseconds;
  public static final CDuration ZERO = CDurationParser.parse("0m");
  public static final CDuration FIVE_SECONDS = CDurationParser.parse("5s");
  public static final CDuration ONE_MINUTE = CDurationParser.parse("1m");
  public static final CDuration FIVE_MINUTES = CDurationParser.parse("5m");
  public static final CDuration ONE_HOUR = CDurationParser.parse("1h");
  public static final CDuration ONE_DAY = CDurationParser.parse("1d");

  public CDuration(long numberOfSeconds) {
    this.milliseconds = numberOfSeconds * 1000;
  }

  public CDuration(long number, boolean isMilliseconds) {
    this.milliseconds = isMilliseconds ? (number) : (number * 1000);
  }

  public boolean isExpired(long sinceInMilliseconds, long nowInMilliseconds) {
    return milliseconds<=0 || (nowInMilliseconds-sinceInMilliseconds) > milliseconds;
  }

  public long asSeconds() {
    return milliseconds/1000l;
  }
  public long asMilliseconds() {
    return milliseconds;
  }
  public long asMinutes() {
    return milliseconds/60000l;
  }
  public long asHours() {
    return milliseconds/3600000l;
  }
  public long asDays() {
    return milliseconds/86400000l;
  }
  public long asYears() {
    return milliseconds/31536000000l;
  }

  @Override
  public String toString() {
    return "CDuration{" +
        "milliseconds=" + milliseconds +
        '}';
  }

  /**
   * Not really optimized, to state it gently. Use with care and only for debugging.
   * @param d
   * @return
   */
  public static String formatDurationInMs(long d) {
    StringBuilder res = new StringBuilder();
    res.append(d%1000).append("ms");
    d/=1000;
    res.insert(0, (d%60) + "s ");
    d/=60;
    res.insert(0, (d%60) + "m ");
    d/=60;
    res.insert(0, (d%24) + "h ");
    d/=24;
    res.insert(0, (d) + "d ");
    return res.toString();
  }
}
