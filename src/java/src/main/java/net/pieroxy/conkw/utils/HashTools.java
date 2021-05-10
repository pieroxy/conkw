package net.pieroxy.conkw.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashTools {

  // From https://stackoverflow.com/questions/4895523/java-string-to-sha1
  public static String toSHA1(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      return byteArrayToHexString(md.digest(input.getBytes(StandardCharsets.UTF_8)));
    }
    catch(NoSuchAlgorithmException e) {
      throw new RuntimeException("No SHA1 digest detected.", e);
    }
  }

  // From https://stackoverflow.com/questions/4895523/java-string-to-sha1
  public static String byteArrayToHexString(byte[] b) {
    String result = "";
    for (int i=0; i < b.length; i++) {
      result +=
          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
    }
    return result;
  }

  public static String getRandomSequence(int sequenceLength) {
    byte[] data = new byte[sequenceLength];
    new SecureRandom().nextBytes(data);
    return byteArrayToHexString(data);
  }
}
