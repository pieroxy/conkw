package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.grabbersBase.Grabber;

public class SimplePermanentCollector extends SimpleTransientCollector {

  public SimplePermanentCollector(Grabber g, String configKey) {
    super(g,configKey);
  }

  @Override
  public void prepareForCollection() {
  }
}
