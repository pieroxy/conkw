package net.pieroxy.conkw.config;

import net.pieroxy.conkw.utils.hashing.Hashable;
import net.pieroxy.conkw.utils.hashing.Md5Sum;

import java.util.Objects;
import java.util.Set;

public class Credentials implements Hashable {
  private String reference;
  private String id;
  private String secret;
  private Set<String> accessibleTo;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Credentials user = (Credentials) o;
    return id.equals(user.id) && secret.equals(user.secret);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, secret);
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  @Override
  public void addToHash(Md5Sum sum) {
    sum.add(getId());
    sum.add(getReference());
    sum.add(getSecret());
  }

  public Set<String> getAccessibleTo() {
    return accessibleTo;
  }

  public void setAccessibleTo(Set<String> accessibleTo) {
    this.accessibleTo = accessibleTo;
  }

  @Override
  public String toString() {
    return "Credentials{" +
        "reference='" + reference + '\'' +
        ", id='" + id + '\'' +
        ", accessibleTo=" + accessibleTo +
        '}';
  }
}
