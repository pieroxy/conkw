package net.pieroxy.conkw.config;

public class HashedSecret {
  private String salt;
  private String hashedSecret;
  private int iterations;
  private int keySize;
  private String method;

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public String getHashedSecret() {
    return hashedSecret;
  }

  public void setHashedSecret(String hashedSecret) {
    this.hashedSecret = hashedSecret;
  }

  public int getIterations() {
    return iterations;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  public int getKeySize() {
    return keySize;
  }

  public void setKeySize(int keySize) {
    this.keySize = keySize;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }
}
