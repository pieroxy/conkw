package net.pieroxy.conkw.utils;

import java.util.Random;

public class RandomHelper {
  public static String getSequence() {
    // Copy/pasted from https://stackoverflow.com/questions/50904587/java-how-to-generate-a-6-digit-random-hexadecimal-value/50904683
    String zeros = "000000";
    Random rnd = new Random();
    String s = Integer.toString(rnd.nextInt(0X1000000), 16);
    s = zeros.substring(s.length()) + s;
    return s;
  }
}
