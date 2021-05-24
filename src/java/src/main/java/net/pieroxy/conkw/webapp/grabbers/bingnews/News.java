package net.pieroxy.conkw.webapp.grabbers.bingnews;

import java.util.List;

public class News {
  private String type;
  private String webSearchUrl;
  private List<Value> value = null;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getWebSearchUrl() {
    return webSearchUrl;
  }

  public void setWebSearchUrl(String webSearchUrl) {
    this.webSearchUrl = webSearchUrl;
  }

  public List<Value> getValue() {
    return value;
  }

  public void setValue(List<Value> value) {
    this.value = value;
  }
}
