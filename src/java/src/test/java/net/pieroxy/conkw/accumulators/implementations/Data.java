package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.pub.mdlog.DataRecord;

import java.util.HashMap;
import java.util.Map;

public class Data implements DataRecord {
  HashMap<String, String> dims = new HashMap<>();
  HashMap<String, Double> vals = new HashMap<>();

  public Data addDim(String name, String value) {
    dims.put(name, value);
    return this;
  }

  public Data addVal(String name, Double value) {
    vals.put(name, value);
    return this;
  }

  @Override
  public Map<String, String> getDimensions() {
    return dims;
  }

  @Override
  public Map<String, Double> getValues() {
    return vals;
  }

  @Override
  public void close() {
  }

  public void assertEquals(Data log2, ConkwTestCase tc) {
    tc.assertMapEquals(log2.getValues(), getValues());
    tc.assertMapEquals(log2.getDimensions(), getDimensions());
  }
}
