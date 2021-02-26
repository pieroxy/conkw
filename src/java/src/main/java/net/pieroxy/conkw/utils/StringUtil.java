package net.pieroxy.conkw.utils;

public class StringUtil {
  public static final int INDEX_NOT_FOUND = -1;

  public static boolean isEmpty(final String cs) {
    return cs == null || cs.length() == 0;
  }

  public static int countMatches(final String str, final String sub) {
    if (isEmpty(str) || isEmpty(sub)) {
      return 0;
    }
    int count = 0;
    int idx = 0;
    while ((idx = str.indexOf(sub, idx)) != INDEX_NOT_FOUND) {
      count++;
      idx += sub.length();
    }
    return count;
  }
}
