package net.pieroxy.conkw.utils.duration;

public final class CDuration {
  private final long seconds;

  protected CDuration(long numberOfSeconds) {
    this.seconds = numberOfSeconds;
  }

  public boolean isExpired(long sinceInMilliseconds, long nowInMilliseconds) {
    return seconds<0 || (nowInMilliseconds-sinceInMilliseconds)/1000 > seconds;
  }

  public long asSeconds() {
    return seconds;
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
