package net.pieroxy.conkw.services;

import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GrabbersService {
  private final static Logger LOGGER = Logger.getLogger(GrabbersService.class.getName());

  private List<Grabber> grabbers = new ArrayList<>();
  private Map<String, Grabber> grabbersByName;

  public synchronized void replaceGrabbers(List<Grabber> grabbers) {
    List<Grabber> oldGrabbers = new ArrayList<>(this.grabbers);
    this.grabbers.clear();
    this.grabbers.addAll(grabbers);
    this.grabbersByName = HashMapPool.getInstance().borrow(grabbers.size());
    grabbers.forEach(g -> this.grabbersByName.put(g.getName(), g));

    oldGrabbers.forEach((gr) -> gr.dispose());
  }


  public synchronized Grabber getGrabberByName(String name) {
    return grabbersByName.get(name);
  }

  public synchronized Stream<Grabber> getAllGrabbers() {
    return grabbers.stream();
  }

  public synchronized int count() {
    return grabbers.size();
  }

  public void destroy() {
    if (grabbers!=null) {
      for (Grabber g : grabbers) {
        try {
          if (g!=null) {
            g.dispose();
          }
        } catch (Exception e) {
          LOGGER.log(Level.INFO, "", e);
        }
      }
    }
  }
}
