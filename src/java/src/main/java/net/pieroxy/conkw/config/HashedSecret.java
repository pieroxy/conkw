package net.pieroxy.conkw.config;

import java.util.Date;

public class HashedSecret {
  private String salt;
  private String hashedSecret;
  private int iterations;
  private int keySize;
  private String method;
  private String tempPassword;
  private Date tempPasswordExpiration;

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

  public String getTempPassword() {
    return tempPassword;
  }

  public void setTempPassword(String tempPassword) {
    this.tempPassword = tempPassword;
  }

  public Date getTempPasswordExpiration() {
    return tempPasswordExpiration;
  }

  public void setTempPasswordExpiration(Date tempPasswordExpiration) {
    this.tempPasswordExpiration = tempPasswordExpiration;
  }
}
