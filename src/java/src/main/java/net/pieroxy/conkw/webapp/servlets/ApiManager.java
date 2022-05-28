package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.model.Response;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ApiManager implements MetaGrabber {
  private final static Logger LOGGER = Logger.getLogger(Api.class.getName());

  private Map<String, Grabber> allGrabbers;

  public ApiManager(List<Grabber> grabbers) {
    allGrabbers = HashMapPool.getInstance().borrow(grabbers.size());
    grabbers.forEach(g -> {allGrabbers.put(g.getName(), g);});
  }

  public void close() {
  }

  public Response buildResponse(long now, Collection<GrabberInput>grabbersRequested) {
    Response r = new Response(grabbersRequested.size());
    r.setInstanceName(Listener.getInstanceName());
    grabbersRequested.stream().parallel().forEach(
            s -> {
              Grabber g = allGrabbers.get(s.getName());
              if (g == null) {
                synchronized (r) {
                  r.addError("Grabber '" + s.getName() + "' not found.");
                }
              } else {
                g.addActiveCollector(s.getParamValue());
                g.collect();
                synchronized (r) {
                  r.add(g.getCollectorToUse(s.getParamValue()).getDataCopy());
                }
              }
            }
    );
    return r;
  }

  public String notifyGrabberAction(String action, Map<String, String[]> parameterMap) {
    final StringBuilder res = new StringBuilder();
    allGrabbers.values().forEach((g) -> {
      if (g.getName().equals(action)) {
        res.append(g.processAction(parameterMap));
      }
    });
    return res.toString();
  }
}

