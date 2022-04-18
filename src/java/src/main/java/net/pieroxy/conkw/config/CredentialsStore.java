package net.pieroxy.conkw.config;

import java.util.HashMap;
import java.util.Map;

public class CredentialsStore {
  private Map<String, Credentials> store;

  public Map<String, Credentials> getStore() {
    return store;
  }

  public void setStore(Map<String, Credentials> store) {
    this.store = store;
  }

  public CredentialsStore getFor(Class<?> aClass) {
    CredentialsStore res = new CredentialsStore();
    res.setStore(new HashMap<>());
    for (Map.Entry<String, Credentials> entry : store.entrySet()) {
      if (entry.getValue().getAccessibleTo().contains(aClass.getCanonicalName())) {
        res.getStore().put(entry.getKey(), entry.getValue());
      }
    }
    return res;
  }
}
