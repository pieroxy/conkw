package net.pieroxy.conkw.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StreamTools {
  public static void copyStreamAndClose(InputStream is, OutputStream os) throws IOException {
    try {
      int read = 0;
      byte[]buffer = new byte[10240];
      while ((read = is.read(buffer))!=-1) {
        os.write(buffer, 0, read);
      }
    } finally {
      os.close();
      is.close();
    }
  }

  public static void copyTextFilePreserveOriginalAndWarnOnStdout(InputStream source, File out) throws IOException {
    String nf = copyTextFilePreserveOriginal(source, out);
    if (nf != null) {
      System.out.println("WARNING: File " + out.getName() + " existed and was modified. New file was created as " + nf + ".");
    }
  }

  /**
   *
   * @param source
   * @param out
   * @return The final name of the new File if it was renamed.
   * @throws IOException
   */
  public static String copyTextFilePreserveOriginal(InputStream source, File out) throws IOException {
    List<String> sourceData = loadTextStream(source);
    if (out.exists()) {
      List<String> alreadyThere = loadTextFile(out.toPath());
      if (!areEqual(sourceData, alreadyThere)) {
        File newfile = findNewFilename(out);
        writeTextFile(sourceData, newfile);
        return newfile.getName();
      }
    } else {
      writeTextFile(sourceData, out);
    }
    return null;
  }

  private static File findNewFilename(File out) {
    String path = out.getAbsolutePath();
    int counter = 1;
    while (true) {
      File newFile = new File(path+"."+counter);
      if (newFile.exists()) {
        counter++;
        continue;
      } else {
        return newFile;
      }
    }
  }

  public static void writeTextFile(List<String> data, File out) throws IOException {
    Writer w = new FileWriter(out);
    for (String s : data) {
      w.write(s);
      w.write(System.lineSeparator());
    }
    w.close();
  }

  public static boolean areEqual(List<String> a, List<String> b) {
    if (a == null && b==null) return true;
    if (a == null || b==null) return false;
    if (a.size() != b.size()) return false;
    for (int i=0 ; i<a.size() ; i++) {
      if (!a.get(i).equals(b.get(i))) return false;
    }
    return true;
  }

  public static List<String> loadTextFile(Path input) throws IOException {
    return Files.readAllLines(input);
  }
  public static List<String> loadTextStream(InputStream input) throws IOException {
    List<String> res = new ArrayList<>();
    BufferedReader br = new BufferedReader(new InputStreamReader(input));
    while (true) {
      String line = br.readLine();
      if (line == null) {
        return res;
      }
      res.add(line);
    }
  }
}
