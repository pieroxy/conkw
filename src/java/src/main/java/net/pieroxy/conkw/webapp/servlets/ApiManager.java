package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.api.model.IdLabelPair;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.model.MetricsApiResponse;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ApiManager implements MetaGrabber {
  private final static Logger LOGGER = Logger.getLogger(Api.class.getName());
  private final Services services;


  public ApiManager(Services services) {
    this.services = services;
  }


  public List<IdLabelPair> getGrabbersList() {
    return services
        .getGrabbersService()
        .getAllGrabbers()
        .map(grabber -> new IdLabelPair(grabber.getName(), grabber.getName() + ": " + grabber.getClass().getSimpleName()))
        .collect(Collectors.toList());
  }

  public MetricsApiResponse buildResponse(long now, Collection<GrabberInput>grabbersRequested) {
    MetricsApiResponse r = new MetricsApiResponse(grabbersRequested.size());
    r.setInstanceName(Listener.getInstanceName());
    grabbersRequested.stream().parallel().forEach(
      s -> {
        Grabber g = services.getGrabbersService().getGrabberByName(s.getName());
        if (g == null) {
          synchronized (r) {
            r.addError("Grabber '" + s.getName() + "' not found.");
          }
        } else {
          g.addActiveCollector(s.getParamValue());
          g.collect();
          ResponseData data = g.getCollectorToUse(s.getParamValue()).getDataCopy();
          synchronized (r) {
            r.add(data);
          }
        }
      }
    );
    return r;
  }

  public String notifyGrabberAction(String action, Map<String, String[]> parameterMap) {
    final StringBuilder res = new StringBuilder();
    services.getGrabbersService().getAllGrabbers().forEach((g) -> {
      if (g.getName().equals(action)) {
        res.append(g.processAction(parameterMap));
      }
    });
    return res.toString();
  }
}

