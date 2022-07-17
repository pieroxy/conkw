package net.pieroxy.conkw.utils.exceptions;

import java.io.IOException;

public class SwallowIOException {
  public static void run(SwallowIOExceptionRunnable r) {
    try {
      r.run();
    } catch (IOException e) {
      // Nothing, the whole point
    }
  }

  public interface SwallowIOExceptionRunnable {
    void run() throws IOException;
  }
}
