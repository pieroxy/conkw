package net.pieroxy.conkw.config;

import net.pieroxy.conkw.utils.Utils;

import java.util.Objects;

public class User implements CredentialsProvider {
  private Credentials credentials;
  private String credentialsRef;

  public User() {
  }

  public User(Credentials credentials) {
    this.credentials = credentials;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Utils.objectEquals(credentials, user.credentials) &&
        Utils.objectEquals(credentialsRef, user.credentialsRef);
  }

  @Override
  public int hashCode() {
    return Objects.hash(credentials, credentialsRef);
  }

  public Credentials getCredentials() {
    return credentials;
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }

  public String getCredentialsRef() {
    return credentialsRef;
  }

  public void setCredentialsRef(String credentialsRef) {
    this.credentialsRef = credentialsRef;
  }

  @Override
  public String toString() {
    return "User{" +
        "credentials=" + credentials +
        ", credentialsRef='" + credentialsRef + '\'' +
        '}';
  }
}
