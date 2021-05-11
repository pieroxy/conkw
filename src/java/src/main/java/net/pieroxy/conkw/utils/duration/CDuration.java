package net.pieroxy.conkw.utils.duration;

public final class CDuration {
  private final long seconds;

  protected CDuration(long numberOfSeconds) {
    this.seconds = numberOfSeconds;
  }

  public boolean isExpired(long sinceInMilliseconds, long nowInMilliseconds) {
    return seconds<0 || (nowInMilliseconds-sinceInMilliseconds)/1000 > seconds;
  }

  long asSeconds() {
    return seconds;
  }
  long asMinutes() {
    return seconds/60;
  }
  long asHours() {
    return seconds/3600;
  }
  long asDays() {
    return seconds/86400;
  }
  long asYears() {
    return seconds/31536000;
  }
}
