package net.pieroxy.conkw.webapp.model;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import net.pieroxy.conkw.pub.misc.ConkwCloseable;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapStringDoubleConverter;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapStringStringConverter;

import java.util.*;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class EmiInput implements ConkwCloseable {
  @JsonAttribute(converter = HashMapStringDoubleConverter.class)
  private Map<String, Double> num;
  @JsonAttribute(converter = HashMapStringStringConverter.class)
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
