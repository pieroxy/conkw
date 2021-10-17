package net.pieroxy.conkw.webapp.model;

import net.pieroxy.conkw.utils.ConkwCloseable;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.util.*;

public class EmiInput implements ConkwCloseable {
  private Map<String, Double> num;
  private Map<String, String> str;

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

  @Override
  public void close() {
    HashMapPool.getInstance().giveBack(num);
    HashMapPool.getInstance().giveBack(str);
  }
}
