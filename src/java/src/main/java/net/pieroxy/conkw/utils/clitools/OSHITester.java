package net.pieroxy.conkw.utils.clitools;

import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.webapp.grabbers.oshi.OshiGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.*;

public class OSHITester {

  public static String formatName(String key) {
    int idx = key.indexOf('_');
    return StringUtil.toFixedLengthRightPadded(key.substring(0, idx), 15) + " " + StringUtil.toFixedLengthRightPadded(key.substring(idx+1), 25);
  }

  public static void main(String[]args) throws InterruptedException {
    OshiGrabber grabber = new OshiGrabber();
    grabber.setConfig(new HashMap<>(), new HashMap<>());
    ResponseData rd = grabber.grabSync();
    Thread.sleep(1000);
    rd = grabber.grabSync();

    Set<String> keys = new HashSet<>();
    keys.addAll(rd.getNum().keySet());
    keys.addAll(rd.getStr().keySet());
    List<String> skeys = new ArrayList<>(keys);
    Collections.sort(skeys);

    for (String key : skeys) {
      if (rd.getNum().containsKey(key)) {
        System.out.println(formatName(key) + ": " + rd.getNum().get(key));
      }
      if (rd.getStr().containsKey(key)) {
        System.out.println(formatName(key) + ": " + rd.getStr().get(key));
      }
    }
  }
}
