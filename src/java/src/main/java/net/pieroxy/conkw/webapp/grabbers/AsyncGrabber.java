package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.logging.Level;

public abstract class AsyncGrabber extends Grabber implements Runnable {
  private ResponseData cached;

  abstract public boolean changed();
  abstract public ResponseData grabSync();

  void disposeSync() {}

  public Thread thread;
  private boolean shouldStop;
  private boolean running;
  private long lastGrab;

  private double time=100, count=1;

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

  public final ResponseData grab() {
    lastGrab = System.currentTimeMillis();

    if (thread == null) {
      running = true;
      thread = new Thread(this, this.getClass().getSimpleName() + "/" + getName());
      this.log(Level.INFO, "Starts " + getName());
      thread.start();
    }

    return cached;
  }

  public void run() {
    while (!shouldStop && thread == Thread.currentThread()) {
      runEverySecond();
    }
    this.log(Level.INFO, "Dispose " + getName());
    running = false;
  }

  private void runEverySecond() {
    try {
      long now = System.currentTimeMillis();
      long msn = now%1000;
      long delay = 1000-msn-((long)(time/count))-50; // Give 50ms margin to the grabber jitter.
      if (delay<0) delay+=1000;
      if (delay <=0 || delay > 1500) delay = 1000;
      if (delay<500) delay+=1000;
      if (delay<=0) delay = 1000;
      if (canLogFiner()) log(Level.FINER, System.currentTimeMillis() + "::"+getName() + " waiting for " + delay);
      synchronized (this) {
        if (canLogFine()) this.log(Level.FINE, "Waiting " + (delay));
        this.wait(delay);
      }
      if (canLogFiner()) log(Level.FINER, System.currentTimeMillis() + "::"+getName() + " up");
      try {
        if (now - lastGrab < 2100) {
          if (changed()) {
            if (canLogFiner()) log(Level.FINER, System.currentTimeMillis() + "::" + getName() + " grab");
            now = System.currentTimeMillis();
            cached = grabSync();
            long eor = System.currentTimeMillis();
            time += eor - now;
            count++;
            time *= 0.9; // 0.9 factor to forget old values over time.
            count *= 0.9;
            if (canLogFine())
              this.log(Level.FINE, getName() + " takes on avg " + (long) (time / count) + " (this time " + (eor - now)+ ")");
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
}
