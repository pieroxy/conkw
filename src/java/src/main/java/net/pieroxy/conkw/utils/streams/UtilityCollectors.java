package net.pieroxy.conkw.utils.streams;

import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UtilityCollectors {
  // From https://stackoverflow.com/questions/22694884/filter-java-stream-to-1-and-only-1-element
  public static <T> Collector<T, ?, T> getOneItemOrNull() {
    return Collectors.collectingAndThen(
        Collectors.toList(),
        list -> {
          if (list.size() != 1) {
            throw new IllegalStateException();
          }
          return list.get(0);
        }
    );
  }
}
