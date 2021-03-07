package net.pieroxy.conkw.utils;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExternalBinaryRunner {
  private Logger LOGGER = Logger.getLogger(ExternalBinaryRunner.class.getName());
  private final static String[]locations = new String[]{"/bin/","/usr/bin/",};

  private final String[]binaryPath;
  private File binaryPathFile;
  private byte[]buffer = new byte[1024]; // Buffer will auto enlarge if needed, and is then cached.
  private int length;

  public ExternalBinaryRunner(String[]binaryPathAndArgs) {
    this.binaryPath = binaryPathAndArgs;
    for (String prefix : locations) {
      this.binaryPathFile = new File(prefix+binaryPathAndArgs[0]);
      this.binaryPath[0] = this.binaryPathFile.getAbsolutePath();
      if (exists()) break;
    }
  }

  public boolean exists() {
    return binaryPathFile.exists() && binaryPathFile.isFile();
  }

  /**
   *
   * @return true if the process was successfully executed.
   */
  public boolean exec() {
    Process process = null;
    try {
      process = new ProcessBuilder(binaryPath).start();
      InputStream is = process.getInputStream();
      int read=0, idx=0;
      while (true) {
        read = is.read(buffer, idx, buffer.length-idx);
        if (read == -1) {
          length = idx;
          return true;
        }
        idx = read;
        if (idx == buffer.length) {
          if (buffer.length > 100000) {
            LOGGER.log(Level.SEVERE, "Executing " + binaryPath[0] + " returned more than 100000 bytes");
            return false;
          }
          buffer = new byte[buffer.length*2];
          LOGGER.log(Level.WARNING, "Enlarging buffer to " + buffer.length + " for " + binaryPathFile.getName());
          return exec();
        }
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Executing " + binaryPath[0], e);
      return false;
    }
  }

  public byte[] getBuffer() {
    return buffer;
  }

  public int getLength() {
    return length;
  }
}
