package net.pieroxy.conkw.utils;

public class StringUtil {
  public static final int INDEX_NOT_FOUND = -1;

  public static boolean isEmpty(final String cs) {
    return cs == null || cs.length() == 0;
  }

  public static boolean isValidApiKey(String value) {
    // Based on the premise that API keys are hexadecimal or base64 representations, while default values in config files have space in them.
    return value!=null && value.indexOf(' ')==-1;
  }

  public static boolean isValidUrl(String value) {
    return value!=null && value.indexOf("http")==0 && value.indexOf("://")>-1;
  }

  public static boolean isNullOrEmptyTrimmed(String s) {
    if (s == null) return true;
    if (s.trim().length() == 0) return true;
    return false;
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

  public static String toFixedLengthRightPadded(String value, int len) {
    StringBuilder sbf = new StringBuilder(len);
    addPaddedString(sbf, value, len);
    return sbf.toString();
  }

  public static void addPaddedString(StringBuilder sbf, String toAdd, int len) {
    if (toAdd.length() == len) sbf.append(toAdd);
    else if (toAdd.length()<len) {
      for (int i=0 ; i<len-toAdd.length() ; i++)
        sbf.append(" ");
      sbf.append(toAdd);
    } else {
      sbf.append(toAdd.substring(toAdd.length() - len));
    }
  }
}
