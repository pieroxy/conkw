package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.grabbersBase.Grabber;

public class SimplePermanentCollector extends SimpleTransientCollector {

  public SimplePermanentCollector(Grabber g) {
    super(g);
  }

  @Override
  public void reset() {
  }
}
