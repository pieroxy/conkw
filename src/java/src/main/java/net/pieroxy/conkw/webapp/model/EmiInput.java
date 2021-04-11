package net.pieroxy.conkw.webapp.model;

import java.util.*;

public class EmiInput {
  private Map<String, Double> num = new HashMap<>();
  private Map<String, String> str = new HashMap<>();

  public Map<String, Double> getNum() {
    return num;
  }

  public void setNum(Map<String, Double> num) {
    this.num = num;
  }

  public Map<String, String> getStr() {
    return str;
  }

  public void setStr(Map<String, String> str) {
    this.str = str;
  }
}
