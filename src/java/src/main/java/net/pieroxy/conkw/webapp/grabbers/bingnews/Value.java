package net.pieroxy.conkw.webapp.grabbers.bingnews;

import java.util.List;

public class Value {
  private String type;
  private String name;
  private String url;
  private Image image;
  private String description;
  private List<Provider> provider = null;
  private String datePublished;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Provider> getProvider() {
    return provider;
  }

  public void setProvider(List<Provider> provider) {
    this.provider = provider;
  }

  public String getDatePublished() {
    return datePublished;
  }

  public void setDatePublished(String datePublished) {
    this.datePublished = datePublished;
  }
}
