package net.pieroxy.conkw.config;

import java.util.HashSet;
import java.util.Set;

public enum UserRole {
  ANONYMOUS,
  VIEWER(ANONYMOUS),
  DESIGNER(VIEWER, ANONYMOUS),
  ADMIN(DESIGNER, VIEWER, ANONYMOUS);

  private Set<UserRole> contains;
  UserRole(UserRole...roles) {
    contains = new HashSet<>();
    for (UserRole ur : roles) contains.add(ur);
    contains.add(this);
  }

  public boolean doesRoleContains(UserRole r) {
    return contains.contains(r);
  }
}
