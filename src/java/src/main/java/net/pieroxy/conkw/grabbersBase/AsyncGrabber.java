package net.pieroxy.conkw.grabbersBase;

import net.pieroxy.conkw.collectors.Collector;
import net.pieroxy.conkw.webapp.grabbers.oshi.OshiGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public abstract class AsyncGrabber<T extends Collector> extends SimpleGrabber<T> implements Runnable {
  public static final String LOAD_STATUS = "grab_status";
  public static final int GRAB_INACTIVE_THRESHOLD = 5000;

  abstract public boolean changed(T c);
  abstract public void grabSync(T c);
  protected void disposeSync() {}

  public Thread thread;
  private boolean shouldStop;
  private boolean running;
  private long lastGrab;
  private boolean grabbingRightNow;

  private double time=100, count=1;

  protected boolean shutdownRequested() {
    return shouldStop;
  }

  @Override
  public final void dispose() {
    shouldStop=true;
    synchronized (this) {
      this.notifyAll();
    }
    disposeSync();
    while(running) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }
    }
  }

  @Override
  public final void collect(T c) {
    lastGrab = System.currentTimeMillis();

    if (thread == null) {
      running = true;
      thread = new Thread(this, this.getClass().getSimpleName() + "/" + getName());
      this.log(Level.INFO, "Starts " + getName());
      try {
        thread.start();
      } catch (Exception e) {
        log(Level.SEVERE, "", e);
        thread = null;
        running = false;
      }
      c.collect(LOAD_STATUS, "Initializing");
    } else if (grabbingRightNow) {
      c.collect(LOAD_STATUS, getLoadingString());
    } else {
      c.collect(LOAD_STATUS, "Loaded");
    }
  }

  public void run() {
    while (!shutdownRequested() && thread == Thread.currentThread()) {
      runEverySecond();
    }
    this.log(Level.INFO, "Dispose " + getName());
    running = false;
  }

  private String getLoadingString() {
    long s = System.currentTimeMillis()/1000;
    int i = (int)s%4;
    return "Loading" + ((i==0) ? "" : (i==1) ? "." : (i==2)?"..":"...");
  }

  private void runEverySecond() {
    try {
      long now = System.currentTimeMillis();
      long msn = now%1000;
      long delay = 1000-msn-((long)(time/count))-50; // Give 50ms margin to the grabber jitter.
      if (delay<0) delay+=1000;
      if (delay <=0 || delay > 1500) delay = 1000;
      if (delay<100) delay+=1000;
      if (delay<=0) delay = 1000;
      if (canLogFiner()) log(Level.FINER, System.currentTimeMillis() + "::"+getName() + " waiting for " + delay);
      synchronized (this) {
        if (canLogFine()) this.log(Level.FINE, "Waiting " + (delay));
        this.wait(delay);
      }
      if (canLogFiner()) log(Level.FINER, System.currentTimeMillis() + "::"+getName() + " up");
      try {
        if (now - lastGrab < GRAB_INACTIVE_THRESHOLD) {
          if (canLogFiner()) log(Level.FINER, System.currentTimeMillis() + "::" + getName() + " grab");
          now = System.currentTimeMillis();
          grabbingRightNow=true;
          try {
            getActiveCollectors().forEach(sc -> {
              if (changed(sc)) {
                long a = System.nanoTime();
                this.grabSync(sc);
                sc.setTime(System.nanoTime() - a);
                sc.collectionDone();
              }
            });
          } finally {
            grabbingRightNow = false;
          }
          if (!shutdownRequested()) {
            collectionRoundIsDone(getActiveCollectors());
            if (canLogFine()) {
              long eor = System.currentTimeMillis();
              time += eor - now;
              count++;
              time *= 0.9; // 0.9 factor to forget old values over time.
              count *= 0.9;
              this.log(Level.FINE, getName() + " takes on avg " + (long) (time / count) + " (this time " + (eor - now) + ")");
            }
          }
        } else {
          // Pause the grabbers after 5s of inactivity.
          if (canLogFine()) this.log(Level.FINE, "Avoid run because of no activity for " + getName());
        }
      } catch (Exception e) {
        log(Level.SEVERE, "Grabbing " + getName(), e);
      }
    } catch (Exception e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
    }
  }

  public void collectionRoundIsDone(Collection<T> data) {
  }
}
