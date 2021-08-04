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

  public static void copyTextFilePreserveOriginalAndWarnOnStdout(InputStream source, File out, boolean overrideTarget, TextReplacer tr) throws IOException {
    String nf = copyTextFilePreserveOriginal(source, out, overrideTarget, tr);
    if (nf != null) {
      if (overrideTarget)
        System.out.println("WARNING: File " + out.getAbsolutePath() + " existed and was modified. It has been renamed as " + nf + ".");
      else
        System.out.println("WARNING: File " + out.getAbsolutePath() + " existed and was modified. New file was created as " + nf + ".");
    }
  }

  /**
   *
   * @param source
   * @param out
   * @param overrideTarget
   * @return The final name of the new File if it was renamed.
   * @throws IOException
   */
  public static String copyTextFilePreserveOriginal(InputStream source, File out, boolean overrideTarget, TextReplacer tr) throws IOException {
    List<String> sourceData = loadTextStream(source, tr);
    if (isConflict(out)) {
      File newfile = findNewFilename(out);
      if (overrideTarget) {
        writeTextFile(loadTextStream(new FileInputStream(out),null), newfile);
        writeTextFile(loadTextStream(new FileInputStream(getHiddenFile(out)),null), getHiddenFile(newfile));
        writeTextFiles(sourceData, out);
      } else {
        writeTextFiles(sourceData, newfile);
      }
      FileTools.makeFileReadonlyForUser(newfile);
      FileTools.makeFileReadonlyForUser(getHiddenFile(newfile));
      FileTools.makeFileReadonlyForUser(out);
      FileTools.makeFileReadonlyForUser(getHiddenFile(out));
      return newfile.getName();
    } else {
      writeTextFiles(sourceData, out);
      FileTools.makeFileReadonlyForUser(out);
      FileTools.makeFileReadonlyForUser(getHiddenFile(out));
    }
    return null;
  }

  private static boolean isConflict(File out) throws IOException {
    if (out.exists() && getHiddenFile(out).exists()) {
      List<String> original = loadTextFile(getHiddenFile(out).toPath());
      List<String> alreadyThere = loadTextFile(out.toPath());
      if (!areEqual(original, alreadyThere)) {
        return true;
      }
    }
    return false;
  }

  private static File getHiddenFile(File out) {
    return new File(out.getParentFile(), "."+out.getName());
  }

  private static File findNewFilename(File out) throws IOException {
    String path = out.getAbsolutePath();
    int counter = 1;
    while (true) {
      File newFile = new File(path+"."+counter);
      if (isConflict(newFile)) {
        counter++;
        continue;
      } else {
        return newFile;
      }
    }
  }

  public static void writeTextFiles(List<String> data, File out) throws IOException {
    writeTextFile(data, out);
    writeTextFile(data, getHiddenFile(out));
  }

  public static void writeTextFile(List<String> data, File out) throws IOException {
    FileTools.makeFileWritableForUser(out);
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
  public static List<String> loadTextStream(InputStream input, TextReplacer tr) throws IOException {
    List<String> res = new ArrayList<>();
    BufferedReader br = new BufferedReader(new InputStreamReader(input));
    while (true) {
      String line = tr == null ? br.readLine() : tr.replace(br.readLine());
      if (line == null) {
        return res;
      }
      res.add(line);
    }
  }
}
