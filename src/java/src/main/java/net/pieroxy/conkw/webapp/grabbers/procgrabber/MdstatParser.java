package net.pieroxy.conkw.webapp.grabbers.procgrabber;

import net.pieroxy.conkw.utils.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MdstatParser {
  private static final Logger LOGGER = Logger.getLogger(MdstatParser.class.getName());

  public static MdstatResult parseMdstat(File f) {
    MdstatResult res = new MdstatResult();
    StringBuilder summary = new StringBuilder();
    if (f.exists()) {
      try (BufferedReader br = new BufferedReader(new FileReader(f))) {
        br.readLine();
        StringBuilder resline=new StringBuilder();
        while (true) {
          resline.setLength(0);
          String first = br.readLine();
          if (first == null || first.startsWith("unused")) {
            res.setOneline(summary.toString());
            return res;
          }
          String second = br.readLine();
          resline.append(first.split(":")[0].trim());
          if (summary.length()>0) summary.append(" ");
          summary.append(resline).append(":");
          res.setFailedDisks(res.getFailedDisks() + StringUtil.countMatches(first, "(F)"));
          String status = second.substring(second.indexOf('[')+1, second.indexOf(']'));
          summary.append('[').append(status).append(']');
          resline.append(":[").append(status).append("]");
          String l;
          l=br.readLine();
          while (true) {
            if (l==null) {
              res.getIndividual().add(resline.toString());
              res.setOneline(summary.toString());
              return res;
            }
            if (l.length()==0) {
              res.getIndividual().add(resline.toString());
              res.setOneline(summary.toString());
              break;
            }

            if (l.contains("recovery")) {
              try {
                double prc = Double.parseDouble(l.substring(l.indexOf("recovery = ")+11, l.indexOf("%")));
                summary.append("<").append((int)Math.round(prc)).append("%>");
                resline.append("<").append((int)Math.round(prc)).append("%>");
              } catch (Exception e) {
                LOGGER.log(Level.WARNING, "", e);
                summary.append("<*>");
              }
            }

            l=br.readLine();
          }
        }
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "", e);
      }
    }
    return res;
  }

  public static class MdstatResult {
    private List<String> individual = new ArrayList<>();
    private String oneline;
    private int failedDisks = 0;

    public List<String> getIndividual() {
      return individual;
    }

    public void setIndividual(List<String> individual) {
      this.individual = individual;
    }

    public String getOneline() {
      return oneline;
    }

    public void setOneline(String oneline) {
      this.oneline = oneline;
    }

    public int getFailedDisks() {
      return failedDisks;
    }

    public void setFailedDisks(int failedDisks) {
      this.failedDisks = failedDisks;
    }
  }
}
