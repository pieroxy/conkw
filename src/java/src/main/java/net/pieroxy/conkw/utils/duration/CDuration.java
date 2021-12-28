package net.pieroxy.conkw.utils.duration;

public final class CDuration {
  private final long seconds;
  public static final CDuration ZERO = CDurationParser.parse("0m");
  public static final CDuration FIVE_SECONDS = CDurationParser.parse("5s");
  public static final CDuration ONE_MINUTE = CDurationParser.parse("1m");
  public static final CDuration FIVE_MINUTES = CDurationParser.parse("5m");
  public static final CDuration ONE_HOUR = CDurationParser.parse("1h");
  public static final CDuration ONE_DAY = CDurationParser.parse("1d");

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

  @Override
  public String toString() {
    return "CDuration{" +
        "seconds=" + seconds +
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
