package net.pieroxy.conkw.webapp.grabbers.bingnews;

public class Image {
  private String type;
  private Thumbnail thumbnail;
  private Boolean isLicensed;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Thumbnail getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(Thumbnail thumbnail) {
    this.thumbnail = thumbnail;
  }

  public Boolean getLicensed() {
    return isLicensed;
  }

  public void setLicensed(Boolean licensed) {
    isLicensed = licensed;
  }
}
