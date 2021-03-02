package net.pieroxy.conkw.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends Formatter {
  // Alternative: -Djava.util.logging.SimpleFormatter.format=%1$tF %1$tT %4$7s %2$.30s %5$s%6$s%n
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"  );

  /**
   * Format the given LogRecord.
   * @param record the log record to be formatted.
   * @return a formatted log record
   */
  public synchronized String format(LogRecord record) {
    StringBuilder sbf = new StringBuilder();

    sbf.append(sdf.format(new Date()));
    StringUtil.addPaddedString(sbf, record.getLevel().getName(), 7);
    sbf.append(" ");
    StringUtil.addPaddedString(sbf,record.getLoggerName(),30);
    sbf.append(" ");

    sbf.append(record.getMessage());
    sbf.append("\n");

    if (record.getThrown() != null) {
      try {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        record.getThrown().printStackTrace(pw);
        pw.close();
        sbf.append(sw.toString());
      } catch (Exception ex) {
      }
    }

    return sbf.toString();
  }
}