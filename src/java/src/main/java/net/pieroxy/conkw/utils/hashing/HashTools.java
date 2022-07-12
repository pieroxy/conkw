package net.pieroxy.conkw.utils.hashing;

import net.pieroxy.conkw.config.HashedSecret;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

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

  public static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) {

    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
      PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
      SecretKey key = skf.generateSecret( spec );
      byte[] res = key.getEncoded( );
      return res;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e ) {
      throw new RuntimeException( e );
    }
  }
  public static String hashPassword( final String password, HashedSecret context) {
    return byteArrayToHexString(hashPassword(password.toCharArray(), context.getSalt().getBytes(StandardCharsets.UTF_8), context.getIterations(), context.getKeySize()));
  }

  public static void main(String[]args) {
    if (args.length == 3 && args[0].equals("hashPassword")) {

    }
  }
}
